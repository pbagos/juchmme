package hmm;

import java.lang.Math;
import java.util.*;

abstract class TrainAlgo {

    HMM hmm;
    int iter;
    double[][] Ka, Ke;
    Activ[][][] acts;

    public TrainAlgo() {
        iter = 1;

        Ka = new double[Model.nstate][Model.nstate];
        Ke = new double[Model.nstate][Model.nesym];

        //Initialization
        for (int i = 0; i < Model.nstate; i++)
            for (int j = 0; j < Model.nstate; j++)
                Ka[i][j] = Params.kappaA;

        for (int i = 0; i < Model.nstate; i++)
            for (int j = 0; j < Model.nesym; j++)
                Ke[i][j] = Params.kappaE;

    }

    public HMM GetModel() {
        return hmm;
    }

    //ML, CML
    protected void LineSearch() {
        System.out.println("Performing Linesearch\n");

        for (int i = 0; i < Model.nstate; i++)
            for (int j = 0; j < Model.nstate; j++)
                Ka[i][j] = Math.min(Params.kappaAmin, Ka[i][j] / ((iter - 1) / Params.ni + 1));

        for (int i = 0; i < Model.nstate; i++)
            for (int j = 0; j < Model.nesym; j++)
                Ke[i][j] = Math.min(Params.kappaEmin, Ke[i][j] / ((iter - 1) / Params.ni + 1));

    }

    //ML, CML
    protected void Silva(double[][] K, final double Kmax, final double Kmin,
                         double[][] prod, final double[][] prod_old) {
        for (int i = 0; i < prod.length; i++) {
            for (int j = 0; j < prod[0].length; j++) {
                double gin = prod[i][j] * prod_old[i][j];

                if (gin > 0)
                    K[i][j] = Math.min(Kmax, K[i][j] * Params.NPLUS);

                else if (gin < 0) {
                    K[i][j] = Math.max(Kmin, K[i][j] * Params.NMINUS);
                    prod[i][j] = 0;
                }
                /*else if( gin == 0 )
                	???*/
            }
        }
    }


    protected static double[] uniformdiscrete(int n) {
        double[] ps = new double[n];
        for (int i = 0; i < n; i++)
            ps[i] = 1.0 / n;

        return ps;
    }

    protected static double[] randomdiscrete(int n) {
        double[] ps = new double[n];
        double sum = 0;

        // Generate random numbers
        for (int i = 0; i < n; i++) {
            ps[i] = Math.random();
            sum += ps[i];
        }

        // Scale to obtain a discrete probability distribution
        for (int i = 0; i < n; i++)
            ps[i] /= sum;

        return ps;
    }

    protected static double exp(double x) {
        if (x == Double.NEGATIVE_INFINITY)
            return 0;
        else
            return Math.exp(x);
    }

    protected double[][] ComputeGrad(final double[][] A, final double[][] prob) {
        double[][] dZA = new double[A.length][A[0].length];

        for (int k = 0; k < A.length; k++) {
            double Aksum = 0;

            for (int ell = 0; ell < A[0].length; ell++)
                Aksum += A[k][ell];

            for (int ell = 0; ell < A[0].length; ell++) {
                dZA[k][ell] = (A[k][ell] - prob[k][ell] * Aksum);
            }
        }

        return dZA;

    }

    protected void Gradient(double[][] dZA, final double[][] dZA_old, final double[][] K) {
        for (int k = 0; k < dZA.length; k++)
            for (int ell = 0; ell < dZA[0].length; ell++)
                dZA[k][ell] = K[k][ell] * (dZA[k][ell] - Params.momentum * dZA_old[k][ell]);
    }

    protected void Rprop(double[][] dZA, final double[][] K) {

        for (int k = 0; k < dZA.length; k++)
            for (int ell = 0; ell < dZA[0].length; ell++)
                dZA[k][ell] = K[k][ell] * (Sign(dZA[k][ell]));
    }


    protected void Exponentiate(final double[][] dZA, double[][] prob, int q) {
        Exponentiate(dZA, prob, true, q);
    }

    protected void Exponentiate(final double[][] dZA, double[][] prob, final boolean UPDATE_FIRST, int q) {
        int start = UPDATE_FIRST ? 0 : 1;
        double q_param = 1 / Math.pow(2.0D, q);

        for (int k = start; k < prob.length; k++) {
            double[] ArA = new double[prob[0].length];
            double ArASum = 0;

            for (int ell = 0; ell < prob[0].length; ell++) {
                ArA[ell] = prob[k][ell] * exp(dZA[k][ell] * q_param);
                ArASum += ArA[ell];
            }
            for (int ell = 0; ell < prob[0].length; ell++) {
                prob[k][ell] = ((ArASum == 0) ? 0 : ArA[ell] / ArASum);
            }
        }
    }

    //ML
    protected void BaumWelch(double[][] A, double[][] E, Probs tab) {
        // Estimate new model parameters
        System.out.println("\tUpdating transitions and emissions using Baum-Welch");

        for (int k = 0; k < Model.nstate; k++) {
            double Aksum = 0;
            for (int ell = 0; ell < Model.nstate; ell++) {
                Aksum += A[k][ell];
            }

            if (Aksum == 0)
                System.out.println("A zero at k=" + k);

            for (int ell = 0; ell < Model.nstate; ell++) {
                tab.aprob[k][ell] = ((Aksum == 0) ? 0 : A[k][ell] / Aksum);
            }

            double Eksum = 0;
            for (int b = 0; b < Model.nesym; b++)
                Eksum += E[k][b];

            if (Eksum == 0)
                System.out.println("E zero at k=" + k);///

            for (int b = 0; b < Model.nesym; b++) {
                tab.eprob[k][b] = ((Eksum == 0) ? 0 : E[k][b] / Eksum);
            }
        }

    }

    //ML,CML
    public static double[] noiseTrans(int n, double[] mat, double[] mat0, int iter) {
        double amp0 = 0;//NOISE AMPLITUDE

        //double amp=Math.pow( amp0, iter );
        double sum = 0;

        for (int i = 0; i < n; i++) {
            if (mat0[i] != 0) {
                //mat[i] = mat[i]+Math.random()*amp;
                //mat[i] = mat0[i];//!!!!!!!!!!
                mat[i] = mat[i] + Params.PRIOR_TRANS * mat0[i];
                //mat[i] = mat[i]+Params.PRIOR_TRANS;
            }
            sum += mat[i];
        }

        for (int i = 0; i < n; i++) {
            if (sum == 0)
                mat[i] = 0;
            else
                mat[i] /= sum;
        }

        return mat;
    }

    //ML, CML
    public void AddExpC_A(double[][] A, Seq seq, double PC, Forward fwdC, Backward bwdC, WeightsL weightsL) {
        int seqLen = seq.getLen();

        for (int i = 0; i <= seqLen - 1; i++) {
            int lab = seq.getNPObs((i + 1) - 1); // was getNObs
            for (int k = 0; k < Model.nstate; k++) {
                for (int ell = 0; ell < Model.nstate; ell++) {
                    if (lab == Model.plab[ell] || lab == -1) ///pbagos@@
                    {
                        double num = exp(fwdC.f[i][k]
                                + hmm.getLoga(k, ell)
                                + hmm.getLoge(ell, seq, (i + 1) - 1)
                                + bwdC.GetVal(i + 1, ell)
                                - PC);

                        A[k][ell] += num * weightsL.getWeightL(seq.getIndexID());
                    }
                }
            }
        }

        if (Params.ALLOW_END) {
            for (int k = 0; k < Model.nstate; k++) {
                A[k][Model.nstate - 1] = exp(fwdC.f[seqLen][k]
                        + hmm.getLoga(k, Model.nstate - 1)
                        - PC) * weightsL.getWeightL(seq.getIndexID());
            }
        }
    }

    //CML
    public void SubExpF_A(double[][] A, Seq seq, double PF, Forward fwdF, Backward bwdF) {
        for (int i = 0; i < seq.getLen() - 1; i++) {
            //int lab=seq.getNPObs( i+1 ); // was getNObs
            for (int k = 0; k < Model.nstate; k++) {
                for (int ell = 0; ell < Model.nstate; ell++) {
                    double num = exp(fwdF.f[i + 1][k]
                            + hmm.getLoga(k, ell)
                            + hmm.getLoge(ell, seq, i + 1)
                            + bwdF.GetVal(i + 2, ell)
                            - PF);
                    A[k][ell] -= num;
                }
            }
        }

    }

    //BackProp
    public void ComputeDeriv(final SeqSet seqs, final double[][][] E, double[][][] deriv12, double[][][] deriv23) {

        System.out.print("\tComputing derivatives");

        for (int o = 0; o < Model.nosym - 2; o++) {
            System.out.print(".");
            int count_win = 0;
            for (int k = 0; k < Model.nstate; k++) {
                if (Model.slab[k] == o) {
                    for (int s = 0; s < seqs.nseqs; s++) {
                        for (int i = 0; i < seqs.seq[s].getLen(); i++) {
                            count_win++;
                            double e = acts[o][s][i].layer3[0];
                            if (e == 0)
                                System.out.println("e=0 o+1=" + (o + 1) + " s=" + s + " i=" + i);

                            double error = (E[k][i][s]) / e;

                            double D = ((1 - e) * e + Params.ADD_GRAD) * error;

                            for (int n = 0; n < Params.nhidden + 1; n++) {
                                double der = acts[o][s][i].layer2[n] * D;
                                deriv23[o][0][n] += der;
                            }

                            for (int m = 0; m < Params.window * NNEncode.encode[0].length + 1; m++) {
                                for (int n = 1; n < Params.nhidden + 1; n++) {
                                    double der = acts[o][s][i].layer1[m]
                                            * acts[o][s][i].layer2[n] * (1 - acts[o][s][i].layer2[n])
                                            * hmm.nn[o].getWts23(0, n) * D;

                                    deriv12[o][n - 1][m] += der;
                                }
                            }
                        }
                    }
                }
            }

            for (int n = 0; n < Params.nhidden + 1; n++) {
                if (count_win > 0)
                    deriv23[o][0][n] = deriv23[o][0][n] / count_win;
            }

            for (int m = 0; m < Params.window * NNEncode.encode[0].length + 1; m++) {
                for (int n = 1; n < Params.nhidden + 1; n++) {
                    if (count_win > 0)
                        deriv12[o][n - 1][m] = deriv12[o][n - 1][m] / count_win;
                }
            }
        }

        System.out.println();
    }

    //BackProp
    public void UpdateWeights(Weights weights, final boolean rprop, final boolean silva,
                              double[][][] deriv12, double[][][] deriv23) {
        System.out.println("\tUpdating weights");


        double[][][] dw12 = new double[Model.nosym - 2][Params.nhidden][Params.window * NNEncode.encode[0].length + 1];
        double[][][] dw23 = new double[Model.nosym - 2][1][Params.nhidden + 1];
        double[][][] delta12 = new double[Model.nosym - 2][Params.nhidden][Params.window * NNEncode.encode[0].length + 1];
        double[][][] delta23 = new double[Model.nosym - 2][1][Params.nhidden + 1];

        for (int o = 0; o < Model.nosym - 2; o++) {
            for (int n = 0; n < Params.nhidden + 1; n++) {
                double dw;

                if (!rprop && !silva) {
                    dw = Params.kappaE * deriv23[o][0][n] - Params.DECAY * weights.GetWeights23(o)[0][n];
                } else {
                    double gin = deriv23[o][0][n] * weights.deriv23[o][0][n];
                    double delta = weights.delta23[o][0][n];

                    if (gin > 0) {
                        //delta = Math.min( Params.kappaEmax * weights.initd23[o][0][n], delta * Params.NPLUS );
                        delta = Math.min(Params.kappaEmax, delta * Params.NPLUS);

                    } else if (gin < 0 && iter > 1) {
                        //delta = Math.max( Params.kappaEmin * weights.initd23[o][0][n], delta * Params.NMINUS );
                        delta = Math.max(Params.kappaEmin, delta * Params.NMINUS);
                        deriv23[o][0][n] = 0;
                    }

                    if (rprop) {
                        if (iter == 1) {
                            delta = delta * Math.abs(deriv23[o][0][n]);
                            //weights.initd23[o][0][n] = delta;
                        }

                        dw = delta * Sign(deriv23[o][0][n]) - Params.DECAY * weights.GetWeights23(o)[0][n];
                    } else
                        dw = delta * deriv23[o][0][n] - Params.DECAY * weights.GetWeights23(o)[0][n];


                    delta23[o][0][n] = delta;

                }


                dw23[o][0][n] = dw;

            }


            for (int m = 0; m < Params.window * NNEncode.encode[0].length + 1; m++) {
                for (int n = 1; n < Params.nhidden + 1; n++) {
                    double dw;

                    if (!rprop && !silva) {
                        dw = Params.kappaE * deriv12[o][n - 1][m] - Params.DECAY * weights.GetWeights12(o)[n - 1][m];
                    } else {
                        double gin = deriv12[o][n - 1][m] * weights.deriv12[o][n - 1][m];
                        double delta = weights.delta12[o][n - 1][m];

                        if (gin > 0) {
                            delta = Math.min(Params.kappaEmax, delta * Params.NPLUS);
                        } else if (gin < 0 && iter > 1) {
                            delta = Math.max(Params.kappaEmin, delta * Params.NMINUS);
                            deriv12[o][n - 1][m] = 0;
                        }

                        if (rprop) {
                            if (iter == 1) {
                                delta = delta * Math.abs(deriv12[o][n - 1][m]);
                            }

                            dw = delta * Sign(deriv12[o][n - 1][m]) - Params.DECAY * weights.GetWeights12(o)[n - 1][m];
                        } else
                            dw = delta * deriv12[o][n - 1][m] - Params.DECAY * weights.GetWeights12(o)[n - 1][m];

                        delta12[o][n - 1][m] = delta;
                    }

                    dw12[o][n - 1][m] = dw;
                }
            }


            for (int n = 0; n < Params.nhidden + 1; n++) {
                weights.AddW23(o, 0, n, dw23[o][0][n]);

                if (rprop || silva) {
                    weights.delta23[o][0][n] = delta23[o][0][n];
                    weights.deriv23[o][0][n] = deriv23[o][0][n];
                }
            }

            for (int m = 0; m < Params.window * NNEncode.encode[0].length + 1; m++)
                for (int n = 1; n < Params.nhidden + 1; n++) {
                    weights.AddW12(o, n - 1, m, dw12[o][n - 1][m]);
                    if (rprop || silva) {
                        weights.delta12[o][n - 1][m] = delta12[o][n - 1][m];
                        weights.deriv12[o][n - 1][m] = deriv12[o][n - 1][m];
                    }
                }

        }

    }

    //BackProp
    protected void CalcActs(SeqSet calcSeqs) {
        CalcActs(calcSeqs, acts);
    }

    protected void CalcActs(SeqSet calcSeqs, Activ[][][] calcacts) {
        for (int o = 0; o < Model.nosym - 2; o++)
            for (int s = 0; s < calcSeqs.nseqs; s++)
                for (int i = 0; i < calcSeqs.seq[s].getLen(); i++)
                    calcacts[o][s][i] = hmm.nn[o].Calc(calcSeqs.seq[s].getWindow(i));
    }

    double fwdbwd(Forward[] fwds, Backward[] bwds, double[] logP, boolean free, SeqSet fbseqs, WeightsL weightsL) throws Exception {
        return fwdbwd(fwds, bwds, logP, free, fbseqs, acts, 0, weightsL);

    }

    double fwdbwd(Forward[] fwds, Backward[] bwds, double[] logP, boolean free, SeqSet fbseqs) throws Exception {
        WeightsL weightsL = new WeightsL(fbseqs.nseqs);

        return fwdbwd(fwds, bwds, logP, free, fbseqs, acts, 0, weightsL);

    }

    double fwdbwd(Forward[] fwds, Backward[] bwds, double[] logP, boolean free, SeqSet fbseqs, int exclude) throws Exception {
        WeightsL weightsL = new WeightsL(fbseqs.nseqs);

        return fwdbwd(fwds, bwds, logP, free, fbseqs, acts, exclude, weightsL);

    }

    double fwdbwd(boolean free, SeqSet fbseqs) throws Exception {
        return fwdbwd(free, fbseqs, acts);
    }


    double fwdbwd(boolean free, SeqSet fbseqs, Activ[][][] fbacts) throws Exception {
        Forward[] fwds = new Forward[fbseqs.nseqs];
        Backward[] bwds = new Backward[fbseqs.nseqs];
        double[] logP = new double[fbseqs.nseqs];
        WeightsL weightsL = new WeightsL(fbseqs.nseqs);

        return fwdbwd(fwds, bwds, logP, free, fbseqs, fbacts, 0, weightsL);

    }

    double fwdbwd(Forward[] fwds, Backward[] bwds, double[] logP, boolean free,
                  SeqSet fbSeqs, Activ[][][] fbActs, int exclude, WeightsL weightsL) throws Exception {
        System.out.println("\tComputing Forward+Backward (" + ((free) ? "Free" : "Clumped") + ")");
        double loglikelihood = 0;
        System.out.print("\t");

        for (int i = 0; i < fbSeqs.nseqs; i++)
            System.out.print("-");

        System.out.println("");
        System.out.print("\t");

        boolean errors = false;
        for (int s = 0; s < fbSeqs.nseqs; s++) {
            if (Params.HNN) {
                double[][] lge = new double[hmm.nstte][fbSeqs.seq[s].getLen()];
                for (int i = 0; i < fbSeqs.seq[s].getLen(); i++) {
                    lge[0][i] = hmm.getLoge(0, fbSeqs.seq[s], i);
                    lge[hmm.nstte - 1][i] = hmm.getLoge(0, fbSeqs.seq[s], i);

                    for (int k = 1; k < hmm.nstte - 1; k++) {
                        lge[k][i] = Math.log(fbActs[Model.slab[k]][s][i].layer3[0]);
                    }
                }

                fwds[s] = new Forward(hmm, fbSeqs.seq[s], lge, free);
                bwds[s] = new Backward(hmm, fbSeqs.seq[s], lge, free);

            } else {
                try {
                    fwds[s] = new Forward(hmm, fbSeqs.seq[s], free);
                    bwds[s] = new Backward(hmm, fbSeqs.seq[s], free);
                } catch (Exception e) {
                    System.out.println(e.getMessage() + "The error ocurred at sequence " + (s + 1) + " " + fbSeqs.seq[s].header);
                    errors = true;
                    continue;
                }
            }

            System.out.print("*");

            logP[s] = fwds[s].logprob();

            if (s < fbSeqs.nseqs - exclude) {
                loglikelihood += logP[s] * weightsL.getWeightL(fbSeqs.seq[s].getIndexID());
            }

        }
        System.out.println();

        if (errors)
            throw new Exception("Errors ocurred...");

        return loglikelihood;
    }


    public void Tying(double[][] E) {
        //TYING!
        for (int k = 0; k < Model.nstate; k++) {
            for (int b = 0; b < Model.nesym; b++) {
                double oEksum = 0;
                int ns = 0;

                for (int m = 0; m < Model.nstate; m++)
                    if (Model.slab[k] == Model.slab[m]) {
                        oEksum += E[m][b];
                        ns++;
                    }

                for (int m = 0; m < Model.nstate; m++)
                    if (Model.slab[k] == Model.slab[m])
                        E[m][b] = oEksum / ns;
            }
        }
    }

    double Sign(double d) {
        if (d > 0.0D)
            return 1.0D;
        else if (d < 0.0D)
            return -1.0D;
        else return 0.0D;
    }

    double ViterbiTraining(SeqSet seqs, String[] vPath, double[] logP, boolean free, WeightsL weightsL) {
        System.out.println("\tComputing Viterbi Training (" + ((free) ? "Free" : "Clumped") + ")");
        double loglikelihood = 0.D;
        System.out.print("\t");

        for (int i = 0; i < seqs.nseqs; i++)
            System.out.print("-");

        System.out.println("");

        Viterbi v;
        for (int s = 0; s < seqs.nseqs; s++) {
            v = new Viterbi(hmm, seqs.seq[s], free);
            vPath[s] = v.getPath(); // Viterbi Path
            logP[s] = v.getProb(); // Viterbi likelihood
            loglikelihood += v.getProb() * weightsL.getWeightL(s);
        }

        return loglikelihood;

    }

    public void ViterbiTrainingExp(String vPath, Seq x, double[][] A, double[][] E, WeightsL weightsL) {
        String[] PathC;
        int length = x.getLen();// Sequence Length

        //States length. All states must have the same length
        int stateLen = Model.state[0].length();
        PathC = new String[length];

        //Split ViterbiPath to a String Array with path states
        PathC = vPath.split("(?<=\\G.{" + stateLen + "})");

        ViterbiTrainingScore(PathC, A, x, E, weightsL);
    }

    private void ViterbiTrainingScore(String seqPath[], double A[][], Seq x, double E[][], WeightsL weightsL) {
        //if exists the begin state, find the next and scoring
        if (Params.ALLOW_BEGIN) {
            int k = Arrays.asList(Model.state).indexOf(seqPath[0]);
            A[0][k] = A[0][k] + (1 * weightsL.getWeightL(x.getIndexID()));
        }

        int seqLen = x.getLen();
        int sym, row, col, i;
        for (i = 1; i <= seqLen - 1; i++) {
            sym = x.getNESym(i - 1);
            row = Arrays.asList(Model.state).indexOf(seqPath[i - 1]);
            col = Arrays.asList(Model.state).indexOf(seqPath[i]);

            A[row][col] = A[row][col] + (1 * weightsL.getWeightL(x.getIndexID()));
            E[row][sym] = E[row][sym] + (1 * weightsL.getWeightL(x.getIndexID()));

        }

        //Score for the last state
        sym = x.getNESym(i - 1);
        row = Arrays.asList(Model.state).indexOf(seqPath[i - 1]);
        E[row][sym] = E[row][sym] + (1 * weightsL.getWeightL(x.getIndexID()));

        //if exists the end state, find the previous state and scoring
        if (Params.ALLOW_END) {
            row = Arrays.asList(Model.state).indexOf(seqPath[i - 1]);
            col = (Model.nstate) - 1;
            A[row][col] = A[row][col] + (1 * weightsL.getWeightL(x.getIndexID()));
        }

    }

}
