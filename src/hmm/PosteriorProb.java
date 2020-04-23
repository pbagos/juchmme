package hmm;

class PosteriorProb {
    Forward fwd;                  // result of the forward algorithm
    Backward bwd;                 // result of the backward algorithm
    private double logprob;
    private double[][] pprob;
    private int L;
    private boolean MSA;

    PosteriorProb(Forward fwd, Backward bwd) {
        this.fwd = fwd;
        this.bwd = bwd;
        this.MSA = false;
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
        //printProbs();
    }

    PosteriorProb(String p_paths[], int L){
        this.MSA = true;
        int[] cntLabels = new int[Model.lpsym];
        int pointer=0, cnt;
        pprob = new double[L][Model.lpsym];

        for (int j=0; j < p_paths[0].length(); j++){
            //ignore positions in the sequence that have dashes "-"
            if (p_paths[0].charAt(j) != '-') {
                cnt=0;
                for (int i=0; i < p_paths.length; i++ ) {
                    if (p_paths[i].charAt(j) != '-'){
                        int lab = Model.psym.indexOf(p_paths[i].charAt(j));
                        if (lab == -1)
                            System.out.println("Error. Unknown label on " + i);
                        else
                            pprob[pointer][lab]++;

                        cnt++;
                    }
                }

                for (int t=0;t<Model.lpsym;t++) {
                    pprob[pointer][t] = pprob[pointer][t] / cnt;
                    //pprob[pointer][t] = pprob[pointer][t] + 0.1;
                }

                //next Letter without dash
                pointer++;
            }
        }
        //printProbs();
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

        //System.out.println("i = "+i+" k = "+k);
        //System.exit(0);
        if (this.MSA)
        {
            if (k < Model.lpsym) {
                total = pprob[i][k];
            }
        } else {
            for (int ell = 0; ell < Model.nstate; ell++)
                if (Model.plab[ell] == k)
                    total += posterior(i, ell);
        }
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

    public void printProbs() {
        for (int i = 0; i < pprob.length; i++) {
            for (int j = 0; j < pprob[i].length; j++) {
                System.out.print(pprob[i][j] + "\t");
            }
            System.out.println("");
        }

    }
}

