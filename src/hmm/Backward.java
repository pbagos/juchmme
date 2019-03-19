package hmm;

class Backward extends HMMAlgo {
    private double[][] b;
    private boolean free;
    private double[][] lge;

    private void Calc() {
        int L = x.getLen();
        b = new double[L + 1][hmm.nstte];

        for (int k = 1; k < hmm.nstte; k++) {
            b[L][k] = Params.ALLOW_END ? hmm.getLoga(k, hmm.nstte - 1) : 0;   // @@
        }

        for (int i = L - 1; i >= 1; i--) {
            int lab = 0;
            if (!free)
                lab = x.getNPObs(i);

            for (int k = 0; k < hmm.nstte; k++) {
                double sum = Double.NEGATIVE_INFINITY;
                for (int ell = 1; ell < hmm.nstte; ell++) {
                    if (lab == -1 || lab == Model.plab[ell - 0] || free) // @@
                    {
                        sum = logplus(sum, hmm.getLoga(k, ell)
                                + lge[ell][(i + 1) - 1]

                                + b[i + 1][ell]);
                    }
                }
                b[i][k] = sum;
            }
        }
    }

    public Backward(HMM hmm, Seq x, double[][] lge, boolean _free) {
        super(hmm, x);
        this.lge = lge;
        this.free = _free;
        Calc();
    }

    public Backward(HMM hmm, Seq x, boolean _free) {
        super(hmm, x);
        this.free = _free;

        lge = new double[hmm.nstte][x.getLen()];
        for (int k = 0; k < hmm.nstte; k++)
            for (int i = 0; i < x.getLen(); i++)
                lge[k][i] = hmm.getLoge(k, x, i);

        Calc();
    }

    double logprob() // @@
    {
        double sum = Double.NEGATIVE_INFINITY;
        int lab = x.getNPObs(0);

        for (int ell = 0; ell < hmm.nstte; ell++) {
            if (lab == -1 || lab == Model.plab[ell - 0] || free) // @@
                sum = logplus(sum, hmm.getLoga(0, ell)
                        + hmm.getLoge(ell, x, 0)
                        + b[1][ell]);
        }

        if (sum == Double.NEGATIVE_INFINITY)
            System.out.println("Zero likelihood at at sequence " + x.header);

        return sum;
    }


    public double GetVal(int x, int y) {
        return b[x][y];
    }


    public void print(Output out) {
        for (int j = 0; j < hmm.nstte; j++) {
            for (int i = 0; i < b.length; i++)
                out.print(HMM.fmtlog(b[i][j]));

            out.println();
        }
    }
}
