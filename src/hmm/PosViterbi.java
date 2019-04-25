package hmm;

class PosViterbi extends HMMAlgo {
    double[][] v;         // the matrix used to find the decoding
    // log(max(P(pi in state k has sym i | path pi)))

    Traceback2[][] B;     // the traceback matrix
    Traceback2 B0;        // the start of the traceback

    private double prob;

    public PosViterbi(HMM hmm, Seq x, boolean free, PosteriorProb postp2) {
        super(hmm, x);
        prob = Double.NEGATIVE_INFINITY;
        final int L = x.getLen();
        v = new double[L + 1][hmm.nstte];
        B = new Traceback2[L + 1][hmm.nstte];

        v[0][0] = 0;                // = log(1)

        for (int k = 1; k < hmm.nstte; k++)
            v[0][k] = Double.NEGATIVE_INFINITY; // = log(0)

        for (int i = 1; i <= L; i++)
            v[i][0] = Double.NEGATIVE_INFINITY; // = log(0)

        for (int i = 1; i <= L; i++)
            for (int ell = 0; ell < hmm.nstte; ell++) {
                double delta;
                delta = Double.NEGATIVE_INFINITY;
                int kmax = 0;

                double maxprod = v[i - 1][kmax] + ((hmm.getLoga(kmax, ell) > Double.NEGATIVE_INFINITY) ? 0 : delta);
                for (int k = 1; k < hmm.nstte; k++) {
                    int lab = x.getNPObs(i - 1);
                    double prod;

                    if (lab == -1 || lab == Model.plab[ell - 0] || free) {
                        prod = v[i - 1][k] + ((hmm.getLoga(k, ell) > Double.NEGATIVE_INFINITY) ? 0 : delta);
                        //System.out.println (prod+"\t"+v[i-1][k]);

                    } else {
                        prod = Double.NEGATIVE_INFINITY;
                    }

                    // System.out.println (prod+"\t"+maxprod);

                    if (prod > maxprod) {
                        kmax = k;
                        maxprod = prod;
                    }
                }
                //if (i==17){System.out.println (kmax);}
                //System.out.println (postp2.getPost(i-1, ell ));
                //System.out.println ("KMAX="+kmax);
                double p = postp2.getPost(i - 1, ell);
                v[i][ell] = Math.log(p) + maxprod;
                //System.out.println (Math.log(postp2.getPost(i-1, ell )));

                B[i][ell] = new Traceback2(i - 1, kmax);

            }


        int kmax = 0;
        double delta = Double.NEGATIVE_INFINITY;
        delta = ((hmm.getLoga(kmax, hmm.nstte - 1) > Double.NEGATIVE_INFINITY) ? 0 : Double.NEGATIVE_INFINITY);

        ///////////((hmm.getLoga( k, ell ) > Double.NEGATIVE_INFINITY)? 0 : delta);
        double max = v[L][kmax] + (Params.ALLOW_END ? delta : 0);
        for (int k = 1; k < hmm.nstte; k++) {
            delta = ((hmm.getLoga(k, hmm.nstte - 1) > Double.NEGATIVE_INFINITY) ? 0 : Double.NEGATIVE_INFINITY);
            //System.out.println (v[L][k]);
            prob = v[L][k] + (Params.ALLOW_END ? delta : 0);
            if (prob > max) {
                kmax = k;
                max = prob;
            }
        }
        //System.out.println (kmax+"\t"+ max);

        prob = max;

        B0 = new Traceback2(L, kmax);
    }

    public String getPath() {
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
        return prob;
    }

    public String getPath2() {
        StringBuffer res = new StringBuffer();
        Traceback2 tb = B0;
        int i = tb.i, j = tb.j;
        while ((tb = B[tb.i][tb.j]) != null) {
            //int l = hmm.stte[j].length();
            res.append(hmm.pstte[j]);
            i = tb.i;
            j = tb.j;
        }

        return res.reverse().toString();
    }

}
