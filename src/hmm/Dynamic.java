package hmm;

import java.lang.Math;

class Dynamic {
    char M;
    char O;
    char I;

    // 	final double VBIG =  (1e32F);
    final double BIG = 100000000;
    final int MAXSEQLEN = 2000;

    static final int IN = 0;
    static final int MEM = 1;
    static final int OUT = 2;

    /* Maximum number of predicted helices */
    private int MAXNSTRAND, STRDIV, MINLLEN, MINHLEN, MAXHLEN;

    /* Maximum length of "topogenic" loop */
    final int LIMITLOOP = 1000000;


    double best_top_score;
    double best2_top_score;
    double max_strand_score;
    double min_strand_score;
    int best_top_nstrand;
    double sum_strand_score;
    double MINSSC;
    double[][] post;

    int[] seq;
    String fpath;
    int[] structure_start, structure_length;
    int[] bestst_start, bestst_length;

    double[][] mat;
    int[][] length, path;

    int seqlen;

    /* Return score for given predicted strand */
    double helscore(int start, int length, boolean inflg, int nstrand) {
        double tot = 0;

        for (int i = 0; i <= length - 1; i++)
            tot += post[i + start][MEM];

        return tot;
    }

    /* Print string representation of predicted structure */
    String prt_model(boolean inflg, int nstrand, int[] structure_start, int[] structure_length) {
        char[] sstruc = new char[seqlen];

        for (int i = 0; i < structure_start[0]; i++)
            sstruc[i] = inflg ? I : O;

        if (nstrand > 0) {
            for (int i = 0; i < nstrand; i++) {
                if (i > 0)
                    for (int j = structure_start[i - 1] + structure_length[i - 1]; j < structure_start[i]; j++)
                        sstruc[j] = inflg ? I : O;

                for (int j = 0; j < structure_length[i]; j++)
                    sstruc[j + structure_start[i]] = M;

                inflg = !inflg;
            }

            for (int i = structure_start[nstrand - 1] + structure_length[nstrand - 1]; i < seqlen; i++)
                sstruc[i] = inflg ? I : O;
        }

        StringBuffer ss = new StringBuffer();
        for (int i = 0; i < seqlen; i++)
            ss.append(sstruc[i]);

        return ss.toString();
    }

    // Calculate score/path matrix by dynamic programming
    void calcmat(boolean inflg, int nstrand) {
        int maxj, maxl;
        double maxsc, hsc, lsc;
        boolean intog;

        intog = inflg ^ !((nstrand & 1) > 0 ? true : false);

        for (int h = nstrand - 1; h >= 0; h--, intog = !intog) {
            for (int i = 0; i < seqlen; i++)
                mat[h][i] = -BIG;

            for (int i = seqlen - MINHLEN - MINLLEN; i >= MINLLEN; i--) {
                maxsc = -BIG;
                maxj = maxl = 0;
                for (int l = MINHLEN; l <= MAXHLEN; l++)
                    if (i + l + MINLLEN <= seqlen) {
                        hsc = helscore(i, l, intog, nstrand);

                        if (h == nstrand - 1) {
                            lsc = 0;
                            int k;

                            for (k = i + l; k < seqlen; k++)
                                if (!intog)
                                    lsc += post[k][IN];
                                else
                                    lsc += post[k][OUT];

                            if (k - i - l + 1 > LIMITLOOP)
                                lsc = 0;

                            if (hsc + lsc > maxsc) {
                                maxsc = hsc + lsc;
                                maxl = l;
                            }
                        } else {/* Calculate initial loop score */
                            lsc = 0;
                            for (int k = i + l; k < i + l + MINLLEN - 1; k++)
                                lsc += intog ? post[k][OUT] : post[k][IN];

                            for (int j = i + l + MINLLEN; j < seqlen - MINHLEN - MINLLEN; j++) {
                                /* Add extra loop residue score */
                                lsc += (intog ? post[j - 1][OUT] : post[j - 1][IN]);

                                if (j - i - l > LIMITLOOP)
                                    lsc = 0;

                                if (hsc + lsc + mat[h + 1][j] > maxsc) {
                                    maxsc = hsc + lsc + mat[h + 1][j];
                                    maxl = l;
                                    maxj = j;
                                }
                            }
                        }
                    }

                mat[h][i] = maxsc;
                length[h][i] = maxl;
                path[h][i] = maxj;

            }
        }
    }

    /* Handle first loop region */
    void firstlp(int starthel, boolean inflg, int nstrand) {
        double lsc = 0;
        for (int i = 0; i < seqlen - (nstrand - starthel) * (MINHLEN + MINLLEN); i++) {
            lsc += (inflg ? post[i][IN] : post[i][OUT]);
            if (i + 1 > LIMITLOOP)
                lsc = 0;

            mat[starthel][i + 1] += lsc;

        }
    }

    /* Trace back highest scoring path through matrix */
    private double trace_back(int starthel, boolean inflg, int nstrand) {
        int res = 0;
        boolean intog = inflg;
        double maxsc = -BIG;
        double hsc;
        boolean weak_h = false;

        for (int i = 1; i < seqlen - (nstrand - starthel) * (MINHLEN + MINLLEN); i++)
            if (mat[starthel][i] > maxsc) {
                maxsc = mat[starthel][i];
                res = i;
            }

        for (int h = 0; h < nstrand - starthel; h++, intog = !intog) {
            hsc = helscore(res, length[h + starthel][res], intog, nstrand);

	/*	if (intog)
		    System.out.println("Strand "+(h + 1)+" from "+(res+1)+" (in) to "
				+(res + length[h+starthel][res])+" (out) : "+ hsc);
		else
		    System.out.println("Strand "+(h + 1)+" from "+(res+1)+" (out) to "
				+(res + length[h+starthel][res])+" (in) : "+ hsc);
		
		//System.out.println( "HSC/LEN: "+hsc/length[h+starthel][res] );///////	
		
		//if (hsc/length[h+starthel][res] < MINSSC)//        */

            if (hsc < MINSSC)
                weak_h = true;

            structure_start[h] = res;
            structure_length[h] = length[h + starthel][res];
            res = path[h + starthel][res];
        }

        //System.out.println( prt_model(inflg, nstrand-starthel, structure_start, structure_length) ) ;

        return ((nstrand == 1) ? maxsc : (weak_h ? -BIG : maxsc));
    }

    public Dynamic(double[][] pp, int slen, char _m, char _o, char _i) {

        M = _m;
        O = _o;
        I = _i;

        post = pp;
        seqlen = slen;
        boolean t;
        int nstrand;
        double[][] finalsc = new double[50][2];
        double score;
        boolean inflg;
        double maxsc = -BIG;
        double max2sc = -BIG;
        boolean maxt = false;
        int maxnh = 0;

        /* Maximum number of predicted strands */
        MAXNSTRAND = Params.MAXNSTRAND;

        /* The minimum sequence length required for one predicted strand */
        STRDIV = Params.STRDIV;

        /* Minimum length of 'loop' */
        MINLLEN = Params.MINLLEN;

        /* Minimum length of strand */
        MINHLEN = Params.MINHLEN;

        /* Maximum length of strand */
        MAXHLEN = Params.MAXHLEN;

        /* Minimum score for strand */
        MINSSC = Params.MINSSC;

        structure_start = new int[50];
        structure_length = new int[50];
        bestst_start = new int[50];
        bestst_length = new int[50];
        mat = new double[50][seqlen];
        length = new int[50][seqlen];
        path = new int[50][seqlen];
        nstrand = Math.max(1, Math.min(MAXNSTRAND, seqlen / STRDIV));
        inflg = true;

        calcmat(inflg, nstrand);

        for (int h = 0; h < nstrand; h++, inflg = !inflg) // NOT nstrand-1 !
        {
            firstlp(h, inflg, nstrand);
            score = trace_back(h, inflg, nstrand);
            if (score > maxsc && Params.isOk(nstrand - h)) {
                for (int i = 0; i < 50; i++) {
                    bestst_start[i] = structure_start[i];
                    bestst_length[i] = structure_length[i];
                }

                max2sc = maxsc;
                maxsc = score; // @@@
                maxt = inflg;
                maxnh = nstrand - h;
                //System.out.println( " ***** maxnh="+nstrand+" - "+ h+" = "+maxnh );//////////****
            }

            finalsc[nstrand - h - 1][inflg ? 1 : 0] = score;
            //System.out.println("Score = " + score/1000.0);
        }

        inflg = false;
        calcmat(inflg, nstrand);

        for (int h = 0; h < nstrand; h++, inflg = !inflg) // NOT nstrand -1!
        {
            firstlp(h, inflg, nstrand);
            score = trace_back(h, inflg, nstrand);
            if (score > maxsc && Params.isOk(nstrand - h)) {
                for (int i = 0; i < 50; i++) {
                    bestst_start[i] = structure_start[i];
                    bestst_length[i] = structure_length[i];
                }

                max2sc = maxsc;
                maxsc = score; // @@@
                maxt = inflg;
                maxnh = nstrand - h;
                //System.out.println( " ***** maxnh="+nstrand+" - "+ h+" = "+maxnh );//////////****
            }

            finalsc[nstrand - h - 1][inflg ? 1 : 0] = score;
            //System.out.println("Score = " + score/1000.0);
        }

        for (nstrand = 1; nstrand <= Math.max(1, Math.min(MAXNSTRAND, seqlen / STRDIV)); nstrand++) {
            if (finalsc[nstrand - 1][1] > max_strand_score) max_strand_score = finalsc[nstrand - 1][1];
            if (finalsc[nstrand - 1][0] > max_strand_score) max_strand_score = finalsc[nstrand - 1][0];
            if (finalsc[nstrand - 1][1] < min_strand_score) min_strand_score = finalsc[nstrand - 1][1];
            if (finalsc[nstrand - 1][0] < min_strand_score) min_strand_score = finalsc[nstrand - 1][0];

            //System.out.println( nstrand + " strands (+) : Score = " + finalsc[nstrand-1][1] );
            //System.out.println( nstrand + " strands (-) : Score = " + finalsc[nstrand-1][0] );

            sum_strand_score += finalsc[nstrand - 1][1];
            sum_strand_score += finalsc[nstrand - 1][0];

        }

        t = maxt;
	    /*System.out.print("1: " +( t ? "(in)" : "(out)" )+ " ");
	    System.out.println((bestst_start[0]+1)+"-"
	    			+(bestst_start[0]+1 + bestst_length[0] - 1)+"\t("
				+(helscore(bestst_start[0], bestst_length[0], t, maxnh)/1000.0)+")" );*/
        t = !t;

        max_strand_score = -BIG;
        min_strand_score = BIG;
        sum_strand_score = 0;

        for (int i = 1; i < maxnh; i++, t = !t) {
            double sc = helscore(bestst_start[i], bestst_length[i], t, maxnh);
	        /*System.out.println( (i+1)+": "
					+(bestst_start[i]+1)+"-"
					+(bestst_start[i]+1 + bestst_length[i] - 1)+"\t("
					+ sc +")");*/

            if (sc > max_strand_score) max_strand_score = sc;
            if (sc < min_strand_score) min_strand_score = sc;
            sum_strand_score += sc;
        }

        fpath = prt_model(maxt, maxnh, bestst_start, bestst_length);

        best_top_score = maxsc;
        best2_top_score = max2sc;
        best_top_nstrand = maxnh;

        return;
    }

    public String getPath() {
        return fpath;
    }

    public double GetScoreDiff() {
        return best_top_score - best2_top_score;
    }
}