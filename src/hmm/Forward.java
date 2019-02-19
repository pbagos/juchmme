package hmm;

class Forward extends HMMAlgo {
    /*private*/ double[][] f;
    private int L;
    private double[][] lge;

    private void Calc(boolean free) throws Exception {
        L = x.getLen();
        f = new double[L + 1][hmm.nstte];
        f[0][0] = 0;

        for (int k = 1; k < hmm.nstte; k++)
            f[0][k] = Double.NEGATIVE_INFINITY;

        for (int i = 1; i <= L; i++)
            f[i][0] = Double.NEGATIVE_INFINITY;

        for (int i = 1; i <= L; i++) {
            int lab = 0;
            boolean nonzero = false;

            if (!free)
                lab = x.getNPObs(i - 1);

            for (int ell = 1; ell < hmm.nstte; ell++) {
                if (lab == -1 || lab == Model.plab[ell - 0] || free) {
                    double sum = Double.NEGATIVE_INFINITY;

                    for (int k = 0; k < hmm.nstte; k++)
                        sum = logplus(sum, f[i - 1][k] + hmm.getLoga(k, ell));

                    f[i][ell] = lge[ell][i - 1] + sum;
                    nonzero = (nonzero || f[i][ell] != Double.NEGATIVE_INFINITY);
                } else
                    f[i][ell] = Double.NEGATIVE_INFINITY;
            }
            if (!nonzero)
                throw new Exception("ERROR: Zero probability at position " + i + ". Symbol: "
                        + x.getSym(i - 1) + " Obs: " + x.getObs(i - 1) + ". ");

        }
    }

    public Forward(HMM hmm, Seq x, double[][] lge, boolean free) throws Exception {
        super(hmm, x);
        this.lge = lge;
        Calc(free);
    }

    public Forward(HMM hmm, Seq x, boolean free) throws Exception {
        super(hmm, x);
        lge = new double[hmm.nstte][x.getLen()];
        for (int k = 0; k < hmm.nstte; k++)
            for (int i = 0; i < x.getLen(); i++)
                lge[k][i] = hmm.getLoge(k, x, i);

        Calc(free);
    }

    double logprob() {
        double sum = Double.NEGATIVE_INFINITY;

        for (int k = 0; k < hmm.nstte; k++)
            sum = logplus(sum, f[L][k] + (Params.ALLOW_END ? hmm.getLoga(k, hmm.nstte - 1) : 0));

        if (sum == Double.NEGATIVE_INFINITY)
            System.out.println("Zero likelihood");

        return sum;
    }

    public double GetVal(int x, int y) {
        return f[x][y];
    }

    public void print(Output out) {
        for (int j = 0; j < hmm.nstte; j++) {
            for (int i = 0; i < f.length; i++)
                out.print(HMM.fmtlog(f[i][j]));

            out.println();
        }
    }

    public int seqLen() {
        return L;
    }
}