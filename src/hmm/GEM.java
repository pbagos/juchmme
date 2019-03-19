package hmm;

import java.io.*;
import java.util.*;

class GEM extends TrainAlgo {
    public double valLog;
    private boolean valid; // If true enable EARLY functionality
    private double[][] E;
    private double[][] A;
    private Forward[] fwds;
    private Backward[] bwds;

    private double[] logP;
    private String[] vPaths;

    private Probs tab;

    private double oldloglikelihood = 0, loglikelihood = 0;

    public GEM(SeqSet trainSetLa, SeqSet trainSetUn, final Probs tab0) throws Exception {
        SeqSet newSet;
        SeqSet trainSetUnNew;
        WeightsL weightsL;

        A = new double[Model.nstate][Model.nstate];
        E = new double[Model.nstate][Model.nesym];
        tab = new Probs(tab0.aprob, tab0.eprob);
        weightsL = new WeightsL(trainSetLa.nseqs);

        System.out.println("SSL - Semi-supervised Learning");
        System.out.println("SSL - Method GEM - Add Method " + Params.addMethodSSL);
        System.out.println("SSL - Initial model training with " + trainSetLa.nseqs + " Labeled Sequences");

        // noise
        for (int k = 0; k < Model.nstate; k++) {
            if (Params.NOISE_TR)
                tab.aprob[k] = noiseTrans(Model.nstate, tab.aprob[k], tab0.aprob[k], iter);

            if (Params.NOISE_EM)
                tab.eprob[k] = Model.putPriorEM(Model.nesym, tab.eprob[k], tab0.eprob[k], k);
        }

        hmm = new HMM(tab);

        //Use of the completely labeled data to train an initial model

        EMstep(trainSetLa, tab0, weightsL);

        double logdiff = 0;
        int q = 0;
        do {
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
                    //Update weight for each sequence according to a Constant
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
            oldloglikelihood = loglikelihood;

            EMstep(newSet, tab0, weightsL);

            loglikelihood = loglikelihood / newSet.getTotL();
            logdiff = oldloglikelihood - loglikelihood;

            if (iter > 1)
                System.out.println("SSL - Iteration " + iter + "\tlog likelihood = " + loglikelihood + "\t\t diff = " + logdiff);
            else
                System.out.println("SSL - Iteration " + iter + "\tlog likelihood = " + loglikelihood);


            hmm.SaveModel();
            iter++;
        } while (Math.abs(logdiff) > Params.threshold && iter < Params.maxIter);

        hmm.lh = loglikelihood;
        hmm.SaveModel();


    }

    private void EMstep(SeqSet trainSet, final Probs tab0, WeightsL weightsL) throws Exception {
        fwds = new Forward[trainSet.nseqs];
        bwds = new Backward[trainSet.nseqs];
        logP = new double[trainSet.nseqs];

        // Set up the inverse of b -> esym.charAt(b); assume all esyms <= 'Z'
        int[] esyminv = new int[Model.esyminv];

        for (int i = 0; i < esyminv.length; i++)
            esyminv[i] = -1;

        for (int b = 0; b < Model.nesym; b++)
            esyminv[Model.esym.charAt(b)] = b;

        loglikelihood = fwdbwd(fwds, bwds, logP, false, trainSet, weightsL);

        if (loglikelihood == Double.NEGATIVE_INFINITY)
            System.out.println("Probable illegal transition found");

        System.out.println("\tlog likelihood = " + loglikelihood);

        System.out.println("\tComputing expected counts");
        System.out.print("\t");

        for (int s = 0; s < trainSet.nseqs; s++)  // Foreach sequence
        {
            System.out.print(".");

            //Compute estimates for A and E

            Forward fwd = fwds[s];
            Backward bwd = bwds[s];
            int seqLen = trainSet.seq[s].getLen();
            double P = logP[s];

            for (int i = 1; i <= seqLen; i++)
                for (int k = 0; k < Model.nstate; k++) {
                    E[k][esyminv[trainSet.seq[s].getSym(i - 1)]] +=
                            exp(fwd.f[i][k] + bwd.GetVal(i, k) - P) * weightsL.getWeightL(trainSet.seq[s].getIndexID());
                }

            AddExpC_A(A, trainSet.seq[s], P, fwd, bwd, weightsL);
        }    //end foreach sequence

        System.out.println();
        Tying(E);

        BaumWelch(A, E, tab);

        for (int k = 0; k < Model.nstate; k++) {
            if (Params.NOISE_TR)
                tab.aprob[k] = noiseTrans(Model.nstate, tab.aprob[k], tab0.aprob[k], iter);

            if (Params.NOISE_EM)
                tab.eprob[k] = Model.putPriorEM(Model.nesym, tab.eprob[k], tab0.eprob[k], k);
        }

        // Create new model
        hmm = new HMM(tab);

    }

    public Probs GetProbs() {
        return tab;
    }
}