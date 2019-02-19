package hmm;

import java.lang.Math;

class Backprop extends TrainAlgo {
    public double valLog;
    private boolean valid;
    private double[][][] E;
    private double[][] A;
    private Probs tab;

    public Backprop(final SeqSet trainSet, final Probs tab0, final SeqSet valSeqs, WeightsL weightsL) throws Exception {
        valid = true;
        Run(trainSet, tab0, valSeqs, 0.0D);
    }


    public Backprop(final SeqSet trainSet, final Probs tab0, final double stopLog, WeightsL weightsL) throws Exception {
        valid = false;
        Run(trainSet, tab0, new SeqSet(0), stopLog);
    }


    public void Run(final SeqSet trainSet, final Probs tab0, final SeqSet valSeqs, final double stopLog) throws Exception {
        int ex = 0;

        SeqSet seqs = new SeqSet(trainSet.nseqs + ex);

        for (int i = 0; i < seqs.nseqs - ex; i++)
            seqs.seq[i] = trainSet.seq[i];

        double loglikelihood, loglikelihoodC, loglikelihoodF;
        double valLoglikelihood, valLoglikelihoodC, valLoglikelihoodF;

        loglikelihood = Double.NEGATIVE_INFINITY;
        loglikelihoodC = Double.NEGATIVE_INFINITY;
        loglikelihoodF = Double.NEGATIVE_INFINITY;
        valLoglikelihood = Double.NEGATIVE_INFINITY;
        valLoglikelihoodC = Double.NEGATIVE_INFINITY;
        valLoglikelihoodF = Double.NEGATIVE_INFINITY;

        Forward[] fwdsC = new Forward[seqs.nseqs];
        Backward[] bwdsC = new Backward[seqs.nseqs];
        double[] logPC = new double[seqs.nseqs];

        Forward[] fwdsF = new Forward[seqs.nseqs];
        Backward[] bwdsF = new Backward[seqs.nseqs];
        double[] logPF = new double[seqs.nseqs];

        acts = new Activ[Model.nosym - 2][seqs.nseqs][seqs.getMaxL()];
        Activ[][][] valActs;

        valActs = new Activ[Model.nosym - 2][valSeqs.nseqs][valSeqs.getMaxL()];

        double bestl = Double.NEGATIVE_INFINITY;
        Weights bestw = new Weights();
        tab = new Probs(tab0.aprob, tab0.weights);

        //System.out.println( "*** Bootstrapping ***" );
        for (int i = 1; i <= Params.BOOT; i++) {
            if (Params.WEIGHTS.equals("RANDOM_NORMAL"))
                tab.weights.RandomizeNormal(Params.STDEV, 0);
            else if (Params.WEIGHTS.equals("RANDOM_UNIFORM"))
                tab.weights.RandomizeUniform(Params.RANGE, 0);

            hmm = new HMM(tab);
            //hmm.print();//////
            CalcActs(seqs);

            loglikelihoodC = fwdbwd(fwdsC, bwdsC, logPC, false, seqs);
            loglikelihoodF = fwdbwd(fwdsF, bwdsF, logPF, true, seqs);
            loglikelihood = loglikelihoodC - loglikelihoodF;
            System.out.println("\tC=" + loglikelihoodC + ", F=" + loglikelihoodF);//////////


            System.out.println(i + "/" + Params.BOOT + "\tlog likelihood = " + loglikelihood);

            if (loglikelihood > bestl) {
                bestl = loglikelihood;
                bestw = tab0.weights.GetClone();
            }

        }

        if (bestl > Double.NEGATIVE_INFINITY) {
            tab.weights = bestw;
            System.out.println("*** Chosen " + loglikelihood + " ***");
        }

        if (Params.NOISE_TR)
            for (int k = 0; k < Model.nstate; k++) {
                tab.aprob[k] = noiseTrans(Model.nstate, tab.aprob[k], tab0.aprob[k], iter);
            }

        hmm = new HMM(tab);

        CalcActs(seqs);

        if (valid)
            CalcActs(valSeqs, valActs);

        loglikelihoodC = fwdbwd(fwdsC, bwdsC, logPC, false, seqs, ex);

        if (valid)
            valLoglikelihoodC = fwdbwd(false, valSeqs, valActs);


        double sum_weights = WeightsSquare(tab.weights);

        loglikelihoodF = fwdbwd(fwdsF, bwdsF, logPF, true, seqs, ex);

        loglikelihood = loglikelihoodC - loglikelihoodF - Params.DECAY * sum_weights;
        System.out.println("\tC=" + loglikelihoodC + ", F=" + loglikelihoodF + ", SqWts=" + sum_weights);

        if (valid) {
            valLoglikelihoodF = fwdbwd(true, valSeqs, valActs);
            valLoglikelihood = valLoglikelihoodC - valLoglikelihoodF - Params.DECAY * sum_weights;
            System.out.println("\tVC=" + valLoglikelihoodC + ", VF=" + valLoglikelihoodF + ", SqWts=" + sum_weights);
        }

        if (loglikelihood == Double.NEGATIVE_INFINITY)
            System.out.println("Probable illegal transition found");

        System.out.println(iter + "\tlog likelihood = " + loglikelihood);

        if (valid)
            System.out.println("\tval log likelihood = " + valLoglikelihood);

        Probs tab_p = new Probs(tab.aprob, tab.weights);

        // hmm.print();
        //   double adiff, ediff;
        double[][] gradA = new double[Model.nstate][Model.nstate];
        double oldloglikelihood, oldvalLoglikelihood;
        double logdiff;
        double[][][] E_old = new double[Model.nstate][seqs.getMaxL()][seqs.nseqs];
        double[][] A_old = new double[Model.nstate][Model.nstate];

        do {
            oldloglikelihood = loglikelihood;
            oldvalLoglikelihood = valLoglikelihood;
            // Compute estimates for A and E

           /*double[][][] EC = new double[Model.nstate][seqs.getMaxL()][seqs.nseqs];
		    double[][][] EF = new double[Model.nstate][seqs.getMaxL()][seqs.nseqs]; */
            E = new double[Model.nstate][seqs.getMaxL()][seqs.nseqs];
            A = new double[Model.nstate][Model.nstate];

            System.out.println("\tComputing expected counts");
            System.out.print("\t");
            for (int s = 0; s < seqs.nseqs; s++)
                System.out.print("-");

            System.out.println();
            System.out.print("\t");

            for (int s = 0; s < seqs.nseqs; s++)  // Foreach sequence
            {
                Forward fwdC = fwdsC[s];
                Backward bwdC = bwdsC[s];
                double PC = logPC[s];            // NOT exp.

                Forward fwdF = fwdsF[s];
                Backward bwdF = bwdsF[s];
                double PF = logPF[s];            // NOT exp.

                int L = seqs.seq[s].getLen();

                //Calc new Emissions
                for (int i = 1; i <= L; i++) {
                    for (int k = 0; k < Model.nstate; k++) {
                        double EC = 0;

                        if (k > 0 && k < Model.nstate - 1)
                            EC = (exp(fwdC.f[i][k] + bwdC.GetVal(i, k) - PC))
                                    / acts[(Model.slab[k])][s][i - 1].layer3[0];

                        double EF = 0;

                        if (k > 0 && k < Model.nstate - 1)
                            EF = (exp(fwdF.f[i][k] + bwdF.GetVal(i, k) - PF))
                                    / acts[(Model.slab[k])][s][i - 1].layer3[0];

                        E[k][i - 1][s] = EC - EF;
                    }
                }

                //Calc new Transitions
                for (int i = 0; i <= L - 1; i++) {
                    int lab = seqs.seq[s].getNPObs(i + 1 - 1); // was getNObs
                    for (int k = 0; k < Model.nstate; k++)
                        for (int ell = 0; ell < Model.nstate; ell++) {
                            if (ell > 0 && ell < hmm.nstte - 1) {
                                if (lab == Model.plab[ell]) {
                                    A[k][ell] += (exp(fwdC.f[i][k]
                                            + hmm.getLoga(k, ell)
                                            + bwdC.GetVal(i + 1, ell)
                                            - PC) * acts[Model.slab[ell]][s][(i + 1) - 1].layer3[0]);
                                }


                                A[k][ell] -= (exp(fwdF.f[i][k]
                                        + hmm.getLoga(k, ell)
                                        + bwdF.GetVal(i + 1, ell)
                                        - PF) * acts[Model.slab[ell]][s][(i + 1) - 1].layer3[0]);


                            }
                        }
                }

                if (Params.ALLOW_END)
                    for (int k = 0; k < Model.nstate; k++) {
                        A[k][Model.nstate - 1] = exp(fwdC.f[L][k]
                                + hmm.getLoga(k, Model.nstate - 1)
                                - PC)
                                - exp(fwdF.f[L][k]
                                + hmm.getLoga(k, Model.nstate - 1)
                                - PF);
                    }

                System.out.print("*");
            } //end foreach sequence
            //hmm=null;
            System.out.println();
            // Estimate new model parameters
            // transitions

            double[][] new_grad;
            new_grad = ComputeGrad(A, tab.aprob);

            if (Params.SILVA)
                Silva(Ka, Params.kappaAmax, Params.kappaAmin, new_grad, gradA);

            if (Params.RPROP)
                Rprop(new_grad, Ka);
            else
                Gradient(new_grad, gradA, Ka);

            Exponentiate(new_grad, tab.aprob, 0);
            gradA = new_grad;

            //emissions
            double[][][] deriv12;
            double[][][] deriv23;
            deriv12 = new double[Model.nosym - 2][Params.nhidden][Params.window * NNEncode.encode[0].length + 1];
            deriv23 = new double[Model.nosym - 2][1][Params.nhidden + 1];

            ComputeDeriv(trainSet, E, deriv12, deriv23);
            UpdateWeights(tab.weights, Params.RPROP, Params.SILVA, deriv12, deriv23);

            //LineSearch();

            iter++;
            if (Params.NOISE_TR)
                for (int k = 0; k < Model.nstate; k++) {
                    tab.aprob[k] = noiseTrans(Model.nstate, tab.aprob[k], tab0.aprob[k], iter);
                }

            // Create new model
            hmm = new HMM(tab);
            //hmm.print();////////////
            CalcActs(seqs);

            if (valid)
                CalcActs(valSeqs, valActs);

            loglikelihoodC = fwdbwd(fwdsC, bwdsC, logPC, false, seqs, ex);

            if (valid)
                valLoglikelihoodC = fwdbwd(false, valSeqs, valActs);

            ////////////////////////////////////////////
            sum_weights = WeightsSquare(tab.weights);
            //sum_weights *=Params.DECAY;
            /////////////////////////////////////////////

            loglikelihoodF = fwdbwd(fwdsF, bwdsF, logPF, true, seqs, ex);
            loglikelihood = loglikelihoodC - loglikelihoodF - Params.DECAY * sum_weights;
            System.out.println("\tC=" + loglikelihoodC + ", F=" + loglikelihoodF + ", SqWts=" + sum_weights);//////////

            if (valid) {
                valLoglikelihoodF = fwdbwd(true, valSeqs, valActs);
                valLoglikelihood = valLoglikelihoodC - valLoglikelihoodF - Params.DECAY * sum_weights; //////////????????
                System.out.println("\tvalC=" + valLoglikelihoodC + ", valF=" + valLoglikelihoodF + ", SqWts=" + sum_weights);//////////
            }

            logdiff = Math.abs(oldloglikelihood - loglikelihood);
            System.out.println(iter + "\tlog likelihood = " + loglikelihood + "\t\t diff = " + logdiff);

            if (valid) {
                System.out.print("\tval log likelihood = " + valLoglikelihood + "\t\t diff = ");


                if (valLoglikelihood > oldvalLoglikelihood || iter < Params.ITER) {
                    System.out.println("DOWN");
                } else {
                    System.out.println("UP");
                    valLog = loglikelihood;
                    return;
                }
            }

            //hmm.SaveModel();
            tab_p = new Probs(tab.aprob, tab.weights);

            //if (Params.PRINT_MODEL)
            if (iter % 10 == 0)
                hmm.print();

        } while (logdiff > Params.threshold && iter < Params.maxIter && loglikelihood < stopLog);

        hmm.lh = loglikelihood;
        hmm.SaveModel();
    }

    private double WeightsSquare(Weights weights) {
        double sum_weights = 0;
        for (int o = 0; o < Model.nosym - 2; o++) {
            for (int j = 0; j < Params.window * NNEncode.encode[0].length + 1; j++) {
                for (int n = 0; n < Params.nhidden; n++) {
                    sum_weights = sum_weights + (weights.GetWeights12(o)[n][j]) * (weights.GetWeights12(o)[n][j]);
                }

                for (int m = 0; m < Params.nhidden + 1; m++) {
                    sum_weights = sum_weights + (weights.GetWeights23(o)[0][m]) * (weights.GetWeights23(o)[0][m]);
                }
            }
        }

        return sum_weights;
    }

    public Probs GetProbs() {

        return tab;
    }
}