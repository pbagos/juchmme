package hmm;

class PLP extends HMMAlgo {
    double[][] v;         // the matrix used to find the decoding
    // log(max(P(pi in state k has sym i | path pi)))

    Traceback2[][] B;     // the traceback matrix
    Traceback2 B0;        // the start of the traceback

    private double prob;

    public PLP(HMM hmm, Seq x, boolean free, PosteriorProb postp2) {
        super(hmm, x);
        prob = 0;
        final int L = x.getLen();
        v = new double[L + 1][hmm.nstte];
        B = new Traceback2[L + 1][hmm.nstte];

        v[0][0] = 0;

        for (int k = 1; k < hmm.nstte; k++)
            v[0][k] = Double.NEGATIVE_INFINITY;

        for (int i = 1; i <= L; i++)
            v[i][0] = Double.NEGATIVE_INFINITY;

        for (int i = 1; i <= L; i++) {
            for (int ell = 0; ell < hmm.nstte; ell++) {
                double delta;
                delta = 0;
                int kmax = 0;

                double maxprod = v[i - 1][kmax] * ((hmm.getLoga(kmax, ell) > Double.NEGATIVE_INFINITY) ? 1 : delta);
                for (int k = 1; k < hmm.nstte; k++) {
                    int lab = x.getNPObs(i - 1);
                    double prod;

                    if (lab == -1 || lab == Model.plab[ell - 0] || free) {
                        prod = v[i - 1][k] * ((hmm.getLoga(k, ell) > Double.NEGATIVE_INFINITY) ? 1 : delta);
                    } else {
                        prod = 0;

                    }

                    if (prod > maxprod) {
                        kmax = k;
                        maxprod = prod;
                    }
                }

                double p = postp2.getPostForLabel(i - 1, Model.plab[ell]);

                v[i][ell] = p + maxprod;

                B[i][ell] = new Traceback2(i - 1, kmax);

            }
        }

        int kmax = 0;

        double delta = 0;
        delta = ((hmm.getLoga(kmax, hmm.nstte - 1) > Double.NEGATIVE_INFINITY) ? 1 : 0);
        double max = v[L][kmax] * (Params.ALLOW_END ? delta : 1);

        for (int k = 1; k < hmm.nstte; k++) {
            delta = ((hmm.getLoga(k, hmm.nstte - 1) > Double.NEGATIVE_INFINITY) ? 1 : 0);
            prob = v[L][k] * (Params.ALLOW_END ? delta : 1);
            if (prob > max) {
                kmax = k;
                max = prob;
            }
        }

        prob = max;

        B0 = new Traceback2(L, kmax);
    }

    private String getPath() {
        StringBuffer res = new StringBuffer();
        Traceback2 tb = B0;
        int i = tb.i, j = tb.j;
        while ((tb = B[tb.i][tb.j]) != null) {
            res.append(hmm.stte[j]);
            i = tb.i;
            j = tb.j;
        }

        return res.reverse().toString();
    }

    public void print(Output out) {
        for (int j = 0; j < hmm.nstte; j++) {
            for (int i = 0; i < v.length; i++)
                out.print(HMM.fmtlog(v[i][j]));

            out.println();
        }
    }

    public double getProb() {
        return Math.log(prob);
    }

    public String getPath2() {
        StringBuffer res = new StringBuffer();
        Traceback2 tb = B0;
        int i = tb.i, j = tb.j;
        while ((tb = B[tb.i][tb.j]) != null) {
            //System.out.println(hmm.pstte[j] + " : "+hmm.stte[j]);
            res.append(hmm.pstte[j]);
            i = tb.i;
            j = tb.j;
        }

        return res.reverse().toString();
    }

}
