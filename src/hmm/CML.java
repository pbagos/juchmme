package hmm;

import java.util.*;

class CML extends TrainAlgo {

    public double valLog;
    private boolean valid;
    private double[][] E;
    private double[][] A;
    private Probs tab;

    public CML(final SeqSet trainSet, final Probs tab0, final SeqSet valSeqs, WeightsL weightsL) throws Exception {
        valid = true;
        Run(trainSet, tab0, valSeqs, 0.0D, weightsL);
    }

    public CML(final SeqSet trainSet, final Probs tab0, final double stopLog, WeightsL weightsL) throws Exception {
        valid = false;
        Run(trainSet, tab0, new SeqSet(0), stopLog, weightsL);
    }


    public void Run(final SeqSet trainSet, final Probs tab0, final SeqSet valSeqs, final double stopLog, WeightsL weightsL) throws Exception {
        //If TRUE  reestimation computed using the ViterbiTraining algorithm
        //if FALSE reestimation computed using the Forward-Backward alogirth
        boolean TrainingWithViterbi = Params.RUN_ViterbiTraining;
        Probs tab = new Probs(tab0.aprob, tab0.eprob);
        Probs tab_p = new Probs(tab0.aprob, tab0.eprob);


        double[][] deltaA = new double[Model.nstate][Model.nstate];
        double[][] deltaE = new double[Model.nstate][Model.nesym];

        double[][] gradA = new double[Model.nstate][Model.nstate];
        double[][] gradE = new double[Model.nstate][Model.nesym];

        double[][] down_gradA = new double[Model.nstate][Model.nstate];
        double[][] down_gradE = new double[Model.nstate][Model.nesym];

        for (int i = 0; i < Model.nstate; i++) {
            for (int j = 0; j < Model.nstate; j++) {
                deltaA[i][j] = Params.kappaA;
            }
        }

        for (int i = 0; i < Model.nstate; i++) {
            for (int j = 0; j < Model.nesym; j++) {
                deltaE[i][j] = Params.kappaE;
            }
        }

        // Set up the inverse of b -> esym.charAt(b); assume all esyms <= 'Z'
        int[] esyminv = new int[Model.esyminv];
        for (int i = 0; i < esyminv.length; i++)
            esyminv[i] = -1;

        for (int b = 0; b < Model.nesym; b++)
            esyminv[Model.esym.charAt(b)] = b;

        // noise @@
        if (Params.NOISE_TR || Params.NOISE_EM) {
            for (int k = 0; k < Model.nstate; k++) {
                if (Params.NOISE_TR)
                    tab.aprob[k] = noiseTrans(Model.nstate, tab.aprob[k], tab0.aprob[k], iter);

                if (Params.NOISE_EM)
                    tab.eprob[k] = Model.putPriorEM(Model.nesym, tab.eprob[k], tab0.eprob[k], k);
            }
        }

        hmm = new HMM(tab);

        double oldloglikelihood = 0, oldvalLoglikelihood = 0;
        double valLoglikelihoodC, valLoglikelihoodF, valLoglikelihood;
        valLoglikelihood = Double.NEGATIVE_INFINITY;
        double loglikelihoodC = 0, loglikelihoodF = 0;

        Forward[] fwdsC = new Forward[trainSet.nseqs];
        Backward[] bwdsC = new Backward[trainSet.nseqs];
        double[] logPC = new double[trainSet.nseqs];
        String[] vPathsC = new String[trainSet.nseqs];

        Forward[] fwdsF = new Forward[trainSet.nseqs];
        Backward[] bwdsF = new Backward[trainSet.nseqs];
        double[] logPF = new double[trainSet.nseqs];
        String[] vPathsF = new String[trainSet.nseqs];

        //Initialization Step
        if (TrainingWithViterbi) {
            loglikelihoodC = ViterbiTraining(trainSet, vPathsC, logPC, false, weightsL);
            loglikelihoodF = ViterbiTraining(trainSet, vPathsF, logPF, true, weightsL);
        } else {
            // Compute Forward and Backward tables for the sequences
            loglikelihoodC = fwdbwd(fwdsC, bwdsC, logPC, false, trainSet, weightsL);
            loglikelihoodF = fwdbwd(fwdsF, bwdsF, logPF, true, trainSet, weightsL);
        }

        double loglikelihood = loglikelihoodC - loglikelihoodF;

        System.out.println("\tC=" + Params.fmt.format(loglikelihoodC) + ", F=" + Params.fmt.format(loglikelihoodF));

        System.out.println(iter + "\tlog likelihood = " + Params.fmt.format(loglikelihood));

        if (valid) {
            valLoglikelihoodC = fwdbwd(false, valSeqs);
            valLoglikelihoodF = fwdbwd(true, valSeqs);

            valLoglikelihood = valLoglikelihoodC - valLoglikelihoodF;
            System.out.println("\tvalC=" + valLoglikelihoodC + ", valF=" + valLoglikelihoodF);
            System.out.println(iter + "\tval log likelihood = " + valLoglikelihood);
        }

        double logdiff = 0;
        double[][] A_old = new double[Model.nstate][Model.nstate];
        double[][] E_old = new double[Model.nstate][Model.nesym];

        int q = 1;

        do {
            oldloglikelihood = loglikelihood;
            oldvalLoglikelihood = valLoglikelihood;

            if (logdiff <= 0 || (!Params.JACOBI)) {
                q = 1;

                for (int k = 0; k < Model.nstate; k++)
                    for (int ell = 0; ell < Model.nstate; ell++)
                        down_gradA[k][ell] = gradA[k][ell];

                for (int ell = 0; ell < Model.nstate; ell++)
                    for (int b = 0; b < Model.nesym; b++)
                        down_gradE[ell][b] = gradE[ell][b];

                System.out.println("\tComputing expected counts");
                System.out.print("\t");

                for (int s = 0; s < trainSet.nseqs; s++)
                    System.out.print("-");

                System.out.println();
                System.out.print("\t");


                double[][] E, EC, EF;
                E = new double[Model.nstate][Model.nesym];
                EC = new double[Model.nstate][Model.nesym];
                EF = new double[Model.nstate][Model.nesym];

                double[][] A, AC, AF;
                A = new double[Model.nstate][Model.nstate];
                AC = new double[Model.nstate][Model.nstate];
                AF = new double[Model.nstate][Model.nstate];

                for (int s = 0; s < trainSet.nseqs; s++)  // Foreach sequence
                {
                    // Compute estimates for A and E
                    //If TRUE  reestimation computed using the ViterbiTraining algorithm
                    //if FALSE reestimation computed using the Forward-Backward alogirth
                    if (!TrainingWithViterbi) {
                        Forward fwdC = fwdsC[s];
                        Backward bwdC = bwdsC[s];
                        double PC = logPC[s];            // NOT exp.

                        Forward fwdF = fwdsF[s];
                        Backward bwdF = bwdsF[s];
                        double PF = logPF[s];            // NOT exp.

                        int seqLen = trainSet.seq[s].getLen();

                        for (int i = 1; i <= seqLen; i++)
                            for (int k = 0; k < Model.nstate; k++) // @@ ektos begin kai end?
                            {
                                if (esyminv[trainSet.seq[s].getSym(i - 1)] < 0)
                                    throw new Exception("ERROR: Symbol " + trainSet.seq[s].getSym(i - 1) +
                                            " at position " + i + ", sequence " + (s + 1) + ".");

                                EC[k][esyminv[trainSet.seq[s].getSym(i - 1)]] += exp(fwdC.f[i][k] + bwdC.GetVal(i, k) - PC) * weightsL.getWeightL(trainSet.seq[s].getIndexID()); // @@
                                EF[k][esyminv[trainSet.seq[s].getSym(i - 1)]] += exp(fwdF.f[i][k] + bwdF.GetVal(i, k) - PF) * weightsL.getWeightL(trainSet.seq[s].getIndexID()); // @@

                            }

                        //-Calc new transitions
                        for (int i = 0; i <= seqLen - 1; i++) // @@ 1 prin + 1 meta?
                        {
                            int lab = (trainSet.seq[s].getNPObs((i + 1) - 1));

                            for (int k = 0; k < Model.nstate; k++)
                                for (int ell = 0; ell < Model.nstate; ell++) {
                                    if (lab == Model.plab[ell])
                                    {
                                        double num = exp(fwdC.f[i][k]  // Forward value
                                                + hmm.getLoga(k, ell)//transition
                                                + (hmm.getLoge(ell, trainSet.seq[s], (i + 1) - 1))// @@ emmissions
                                                + (bwdC.GetVal(i + 1, ell))//@@ Backward value
                                                - PC);// Likelihood with Labels

                                        A[k][ell] += num * weightsL.getWeightL(trainSet.seq[s].getIndexID());
                                    }

                                    double num = exp(fwdF.f[i][k] // @@
                                            + hmm.getLoga(k, ell)
                                            + (hmm.getLoge(ell, trainSet.seq[s], (i + 1) - 1))// @@
                                            + (bwdF.GetVal(i + 1, ell))// @@
                                            - PF);//-Likelihood value without labels

                                    A[k][ell] -= num * weightsL.getWeightL(trainSet.seq[s].getIndexID());
                                }
                        }

                        if (Params.ALLOW_END)
                            for (int k = 0; k < Model.nstate; k++) {
                                A[k][Model.nstate - 1] = exp(fwdC.f[seqLen][k]
                                        + hmm.getLoga(k, Model.nstate - 1)
                                        - PC)

                                        - exp(fwdF.f[seqLen][k]
                                        + hmm.getLoga(k, Model.nstate - 1)
                                        - PF);
                            }

                    } else
                    {
                        String vPathC = vPathsC[s];
                        String vPathF = vPathsF[s];

                        ViterbiTrainingExp(vPathC, trainSet.seq[s], AC, EC, weightsL);
                        ViterbiTrainingExp(vPathF, trainSet.seq[s], AF, EF, weightsL);

                        for (int i = 0; i < Model.nstate; i++)
                            for (int j = 0; j < Model.nstate; j++)
                                A[i][j] = AC[i][j] - AF[i][j];

                    }

                    //AddExpC_A( A, trainSet.seq[s], PC, fwdC, bwdC );
                    //SubExpF_A( A, trainSet.seq[s], PF, fwdF, bwdF );

                    System.out.print("*");
                }    //end foreach sequence

                //hmm=null;
                System.out.println("");
                Tying(EC);
                Tying(EF);
                for (int b = 0; b < Model.nesym; b++)
                    for (int m = 0; m < Model.nstate; m++) {
                        E[m][b] = EC[m][b] - EF[m][b];
                    }

                //Estimate new model parameters
                //M-step Update Parameters
                double[][] new_grad;
                new_grad = ComputeGrad(A, tab.aprob);

                if (Params.SILVA)
                    Silva(Ka, Params.kappaAmax, Params.kappaAmin, new_grad, gradA);

                if (Params.RPROP)
                    Rprop(new_grad, Ka);
                else
                    Gradient(new_grad, gradA, Ka);

                Exponentiate(new_grad, tab.aprob, Params.ALLOW_BEGIN, 0);
                gradA = new_grad;

                new_grad = ComputeGrad(E, tab.eprob);
                if (Params.SILVA)
                    Silva(Ke, Params.kappaEmax, Params.kappaEmin, new_grad, gradE);

                if (Params.RPROP)
                    Rprop(new_grad, Ke);
                else
                    Gradient(new_grad, gradE, Ke);

                Exponentiate(new_grad, tab.eprob, 0);
                gradE = new_grad;

                //LineSearch();
            } else {
                System.out.println("\tPerforming Jacobi bisection q = " + q);
                Exponentiate(down_gradA, tab.aprob, q);
                Exponentiate(down_gradE, tab.eprob, q);
                /*		oldloglikelihood = down_loglikelihood;*/
                q++;
            }

            iter++;

            // noise @@
            if (Params.NOISE_TR || Params.NOISE_EM) {
                for (int k = 0; k < Model.nstate; k++) {
                    if (Params.NOISE_TR)
                        tab.aprob[k] = noiseTrans(Model.nstate, tab.aprob[k], tab0.aprob[k], iter);

                    if (Params.NOISE_EM)
                        tab.eprob[k] = Model.putPriorEM(Model.nesym, tab.eprob[k], tab0.eprob[k], k);
                }
            }

            // Create new model
            hmm = new HMM(tab);

            if (TrainingWithViterbi) {
                loglikelihoodC = ViterbiTraining(trainSet, vPathsC, logPC, false, weightsL);
                loglikelihoodF = ViterbiTraining(trainSet, vPathsF, logPF, true, weightsL);
            } else {
                // Compute Forward and Backward tables for the sequences
                loglikelihoodC = fwdbwd(fwdsC, bwdsC, logPC, false, trainSet, weightsL);
                loglikelihoodF = fwdbwd(fwdsF, bwdsF, logPF, true, trainSet, weightsL);
            }

            System.out.println("\tC=" + loglikelihoodC + ", F=" + loglikelihoodF);
            loglikelihood = loglikelihoodC - loglikelihoodF;

            logdiff = oldloglikelihood - loglikelihood;
            System.out.println(iter + "\tlog likelihood = " + loglikelihood + "\t\t diff = " + logdiff);

            if (valid) {
                valLoglikelihoodC = fwdbwd(false, valSeqs);
                valLoglikelihoodF = fwdbwd(true, valSeqs);

                valLoglikelihood = valLoglikelihoodC - valLoglikelihoodF;
                System.out.println("\tvalC=" + valLoglikelihoodC + ", valF=" + valLoglikelihoodF);
                System.out.println(iter + "\tval log likelihood = " + valLoglikelihood);
                System.out.print("\tval log likelihood = " + valLoglikelihood + "\t\t diff = ");

                if (valLoglikelihood > oldvalLoglikelihood || iter < Params.ITER)
                {
                    System.out.println("DOWN");
                } else {
                    System.out.println("UP");
                    valLog = loglikelihood;
                    return;
                }
            }

            //hmm.SaveModel();
            //adiff = tab.aDiff( tab_p );
            //ediff = tab.eDiff( tab_p );
            //System.out.println( "adiff = "+adiff+"\tediff = "+ediff );
            tab_p = new Probs(tab.aprob, tab.eprob);

        } while (Math.abs(logdiff) > Params.threshold && iter < Params.maxIter && loglikelihood < stopLog);

        System.out.println(stopLog);
        hmm.lh = loglikelihood;
        hmm.SaveModel();
    }

    public Probs GetProbs() {
        return tab;
    }
}