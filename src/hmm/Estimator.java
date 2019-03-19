package hmm;

class Estimator {
    private HMM hmm;
    public double valLog;
    private Probs tabUpdate;

    public Estimator(SeqSet trainSetLa, SeqSet trainSetUn, Probs tab) throws Exception {
        valLog = trainSetLa.getTotL() * Params.CUSTOM_STOP;
        SeqSet newSet;

        //Check if exists any Unlabaled sequence to activate Semi-supervised Learning
        if (trainSetUn.nseqs > 0 && Params.enabledSSL) {
            System.out.println(Params.methodSSL);

            if (Params.methodSSL.equals("GEM")) {

                GEM training = new GEM(trainSetLa, trainSetUn, tab);
                hmm = training.GetModel();
            } else {
                SeqSet trainSetUnNew;
                WeightsL weightsL;

                double logdiff = 0, oldloglikelihood = 0, loglikelihood = 0;
                int iter = 0;

                System.out.println("SSL - Semi-supervised Learning");
                System.out.println("SSL - Method SSL - Add Method " + Params.addMethodSSL);
                System.out.println("SSL - Initial model training with " + trainSetLa.nseqs + " Labeled Sequences");

                //Use of the completely labeled data to train an initial model
                weightsL = new WeightsL(trainSetLa.nseqs);
                hmm = Estimate(trainSetLa, tab, weightsL);

                do {
                    iter++;

                    oldloglikelihood = loglikelihood;
                    trainSetUnNew = trainSetUn;

                    //E-step
                    //Predict the labels of the unlabeled or partially labeled data
                    System.out.println("SSL - Predict the labels of  " + trainSetUnNew.nseqs + " unLabeled Sequences");
                    Decoding dec = new Decoding(hmm, trainSetUnNew, false, false, true);

                    int numOfSeqToAdd = 0;
                    for (int s = 0; s < trainSetUnNew.nseqs; s++) {
                        if (Params.addMethodSSL == 4) {
                            if (trainSetUnNew.seq[s].relscore[Params.usingMethodSSL] > Params.relscoreSSL)
                                numOfSeqToAdd++;
                        } else {
                            numOfSeqToAdd++;
                        }
                    }

                    System.out.println("SSL - Preparing training set no ");
                    //Add newly labeled data to labeled data
                    //add all
                    //add a few most confident pairs
                    newSet = new SeqSet(trainSetLa.nseqs + numOfSeqToAdd);
                    weightsL = new WeightsL(newSet.nseqs);

                    int j;
                    for (j = 0; j < trainSetLa.nseqs; j++) {
                        newSet.seq[j] = trainSetLa.seq[j];
                        newSet.seq[j].SetIndexID(j);
                    }

                    int cou = 0;
                    for (int i = 0; i < trainSetUnNew.nseqs; i++) {
                        if (Params.addMethodSSL == 4) {
                            // add a few most confident pairs according to relscore to be maximum of an initial parameter value
                            if (trainSetUnNew.seq[i].relscore[Params.usingMethodSSL] > Params.relscoreSSL) {
                                newSet.seq[j + cou] = trainSetUnNew.seq[i];
                                newSet.seq[j + cou].SetIndexID(j + cou);
                                newSet.seq[j + cou].SetObs(trainSetUnNew.seq[i].path[Params.usingMethodSSL]);
                                System.out.println("\tAdded " + trainSetUnNew.seq[i].header + " to training set");
                                cou++;
                            }
                        } else {
                            //add all Sequences
                            newSet.seq[j + cou] = trainSetUnNew.seq[i];
                            newSet.seq[j + cou].SetIndexID(j + cou);
                            newSet.seq[j + cou].SetObs(trainSetUnNew.seq[i].path[Params.usingMethodSSL]);

                            //Update weight for each sequence according to relscore
                            if (Params.addMethodSSL == 3) {
                                weightsL.setWeightL(j + cou, trainSetUnNew.seq[i].relscore[Params.usingMethodSSL]);
                            }

                            //Update weight for each sequence according to constant
                            if (Params.addMethodSSL == 2) {
                                weightsL.setWeightL(j + cou, Params.weightSSL);
                            }
                            cou++;
                        }
                    }

                    System.out.println("SSL - Added " + cou + " sequences");
                    //M-step
                    //Use the newly labeled data along with completely labeled ones in order to train a new model
                    System.out.println("SSL - Train a new model adding the newly labeled data");
                    hmm = Estimate(newSet, tab, weightsL); // try tabUpdate

                    loglikelihood = hmm.lh / newSet.getTotL();

                    logdiff = (oldloglikelihood - loglikelihood);

                    if (iter > 1)
                        System.out.println("SSL - Iteration " + iter + "\tlog likelihood = " + loglikelihood + "\t\t diff = " + logdiff);
                    else
                        System.out.println("SSL - Iteration " + iter + "\tlog likelihood = " + loglikelihood);

                } while (Math.abs(logdiff) > Params.thresholdSSL && iter < Params.maxIterSSL);
            }

        } else {
            if (Params.EARLY)
                valLog = early(trainSetLa, tab);

            hmm = Estimate(trainSetLa, tab);

        }

    }

    public HMM GetModel() {
        return hmm;
    }

    private HMM Estimate(SeqSet trainSet, Probs tab) throws Exception {
        WeightsL weightsL;
        weightsL = new WeightsL(trainSet.nseqs);

        return Estimate(trainSet, tab, weightsL);

    }

    private HMM Estimate(SeqSet trainSet, Probs tab, WeightsL weightsL) throws Exception {
        if (Params.HNN)
        // HNN
        {
            Backprop training = new Backprop(trainSet, tab, valLog, weightsL);
            tabUpdate = training.GetProbs();
            return training.GetModel();
        } else if (!Params.RUN_CML) {
            // ML

            if (Params.REFINE) {
                ML training = new ML(trainSet, tab, 0, weightsL);

                System.out.println("ML TRAINING for path refinement: ");
                //training.GetModel().print( new SystemOut() );

                for (int j = 0; j < trainSet.nseqs; j++) {
                    System.out.println("Performed refinement " + trainSet.seq[j].header);
                    trainSet.seq[j].PutDashes(Model.transmLabels);
                    Viterbi vit2 = new Viterbi(training.GetModel(), trainSet.seq[j], false);
                    trainSet.seq[j].SetObs(vit2.getPath2());
                }
            }

            ML training = new ML(trainSet, tab, valLog, weightsL);
            tabUpdate = training.GetProbs();
            return training.GetModel();
        } else {
            // CML
            if (Params.REFINE) {
                ML training = new ML(trainSet, tab, 0, weightsL);

                System.out.println("ML TRAINING for path refinement: ");
                //training.GetModel().print( new SystemOut() );

                for (int j = 0; j < trainSet.nseqs; j++) {
                    trainSet.seq[j].PutDashes(Model.transmLabels);
                    Viterbi vit2 = new Viterbi(training.GetModel(), trainSet.seq[j], false);
                    trainSet.seq[j].SetObs(vit2.getPath2());
                    System.out.println("Performed refinement.");
                }
            }

            if (Params.ML_INIT) {
                ML training = new ML(trainSet, tab, 0, weightsL);
                System.out.println("ML TRAINING for ML INIT: ");
                //training.GetModel().print( new SystemOut() );
                System.out.println("Starting CML with ML output model.");
                tab = training.GetModel().GetProbs();
            }

            CML training = new CML(trainSet, tab, valLog, weightsL);
            tabUpdate = training.GetProbs();
            return training.GetModel();
        }
    }

    private static double early(SeqSet jackSet, Probs tab) throws Exception {
        double vLog = 0;
        int nTrain = Params.NTRAIN;
        int nRound = Params.NROUND;
        WeightsL weightsL = new WeightsL(jackSet.nseqs);

        SeqSet trainSet = new SeqSet(nTrain);
        SeqSet valSet = new SeqSet(jackSet.nseqs - nTrain);

        for (int i = 0; i < nRound; i++) {
            System.out.println("Preparing training set no " + (i + 1));
            int cou = 0;
            boolean[] sqtr = new boolean[jackSet.nseqs];
            while (cou < nTrain) {
                int pick = (int) Math.floor(Math.random() * jackSet.nseqs);
                System.out.println("pick=" + pick);
                if (!sqtr[pick]) {
                    trainSet.seq[cou] = jackSet.seq[pick];
                    trainSet.seq[cou].SetIndexID(cou);
                    sqtr[pick] = true;
                    System.out.println("\tAdded " + trainSet.seq[cou].header + " to training set");
                    cou++;
                }

            }

            cou = 0;
            for (int j = 0; j < jackSet.nseqs; j++)
                if (!sqtr[j]) {
                    valSet.seq[cou] = jackSet.seq[j];
                    valSet.seq[cou].SetIndexID(cou);
                    System.out.println("\tAdded " + valSet.seq[cou].header + " to validation set");
                    cou++;
                }

            double vlog;

            if (Params.HNN) {
                Backprop training = new Backprop(trainSet, tab, valSet, weightsL);
                vlog = training.valLog;
            } else if (!Params.RUN_CML) {
                ML training = new ML(trainSet, tab, valSet, weightsL);
                vlog = training.valLog;
            } else {
                CML training = new CML(trainSet, tab, valSet, weightsL);
                vlog = training.valLog;
            }

            double vl = vlog / trainSet.getTotL();

            System.out.println("\tStopped at likelihood " + vlog + " / " + trainSet.getTotL() +
                    " residues = " + vl);

            vLog += vl;

        }

        vLog /= nRound;

        vLog *= jackSet.getTotL();

        System.out.println("I will stop when likelihood is " + vLog);

        return vLog;

    }

}