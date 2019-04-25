package hmm;

class PosteriorProb {
    Forward fwd;                  // result of the forward algorithm
    Backward bwd;                 // result of the backward algorithm
    private double logprob;
    private double[][] pprob;
    private int L;

    PosteriorProb(Forward fwd, Backward bwd) {
        this.fwd = fwd;
        this.bwd = bwd;
        double logprob1 = fwd.logprob();    // should equal fwd.logprob()
        double logprob2 = bwd.logprob();    // should equal bwd.logprob()

        //System.out.println( "Logprob (fwd) : "+logprob1 );
        //System.out.println( "Logprob (bwd) : "+logprob2 );

        logprob = logprob1;
        L = fwd.seqLen();
        pprob = new double[L][Model.lpsym];

        for (int i = 0; i < L; i++) {
            for (int q = 0; q < Model.lpsym; q++)
                pprob[i][q] = 0;

            for (int g = 1; g < Model.nstate - 1; g++)  //@@
            {
                //System.out.println( g+" "+Model.pstate[g]+" "+Model.psym.indexOf( Model.pstate[g] ) );
                pprob[i][Model.psym.indexOf(Model.pstate[g])] += posterior(i, g);
            }
            /*int best=0;
            for( int q=1; q<Model.npsym; q++ )
                  if( total[i][q]>total[i][best] )
                      best=q;*/
        }

    }

    double[][] getPProb() {
        return pprob;
    }

    private double posterior(int i, int k) // i=index into the seq; k=the HMM state
    {
        //	System.out.println( Math.exp(fwd.f[i+1][k] + bwd.GetVal( i+1, k )- logprob) );
        return Math.exp(fwd.f[i + 1][k] + bwd.GetVal(i + 1, k) - logprob);
    }

    public double getPostForLabel(int i, int k) {
        double total = 0;

        for (int ell = 0; ell < Model.nstate; ell++)
            if (Model.plab[ell] == k)
                total += posterior(i, ell);
        return total;
    }

    public double getPost(int i, int k) {
        return posterior(i, k);
    }

    public void normalize() {
        double tot;

        for (int i = 0; i < L; i++) {
            tot = 0;

            for (int q = 0; q < Model.lpsym; q++)
                tot += pprob[i][q];

            for (int g = 0; g < Model.lpsym; g++)
                pprob[i][g] /= (tot > 0) ? tot : 1;

        }
    }
}

