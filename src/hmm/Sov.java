package hmm;

public class Sov {
    private String mem;
    private double score;

    public double GetScore() {
        return score;
    }

    public Sov(String obs, String pred, String _mem) {
        mem = _mem;
        int i, n_aa, sov_method;
        char c;
        SovPars pdata = new SovPars();
        double out0, out1, out2, out3;
        n_aa = obs.length();
        String osec = "";
        String psec = "";

        for (int x = 0; x < n_aa; x++) {
            osec += (mem.lastIndexOf(obs.charAt(x)) > -1) ? 'H' : 'C';
            psec += (mem.lastIndexOf(pred.charAt(x)) > -1) ? 'H' : 'C';

        }

        default_parameters(pdata);

        if (n_aa <= 0) {
            System.out.println("no data");
        }

        if (pdata.sov_out >= 1) {
//		System.out.println("SOV parameters:   DELTA ,  DELTA-S = "+
//				  pdata.sov_delta +" "+
//				  pdata.sov_delta_s);
        }

//
//      System.out.println("\n                                   ALL    HELIX   STRAND     COIL\n");

        pdata.q3_what = 0;
        out0 = q3(n_aa, osec, psec, pdata);
        pdata.q3_what = 1;
        out1 = q3(n_aa, osec, psec, pdata);
        pdata.q3_what = 2;
        out2 = q3(n_aa, osec, psec, pdata);
        pdata.q3_what = 3;
        out3 = q3(n_aa, osec, psec, pdata);
//	    System.out.println("\n Q3                         :"+" "+
//	 		out0*100.0+" "+out1*100.0+" "+out2*100.0+" " +out3*100.0);

        sov_method = pdata.sov_method;

        if (sov_method != 0) pdata.sov_method = 1;

        if (pdata.sov_method == 1) {
            pdata.sov_what = 0;
            out0 = sov(n_aa, osec, psec, pdata);
            pdata.sov_what = 1;
            out1 = sov(n_aa, osec, psec, pdata);
            pdata.sov_what = 2;
            out2 = sov(n_aa, osec, psec, pdata);
            pdata.sov_what = 3;
            out3 = sov(n_aa, osec, psec, pdata);

//		System.out.println("\n SOV                   "+
//				 out0*100.0+" "+out1*100.0+" "+out2*100.0+" "+out3*100.0);
            score = out0;

        }

        if (sov_method != 1) pdata.sov_method = 0;

        if (pdata.sov_method == 0) {
            pdata.sov_delta = 1.0;
            pdata.sov_what = 0;
            out0 = sov(n_aa, osec, psec, pdata);
            pdata.sov_what = 1;
            out1 = sov(n_aa, osec, psec, pdata);
            pdata.sov_what = 2;
            out2 = sov(n_aa, osec, psec, pdata);
            pdata.sov_what = 3;
            out3 = sov(n_aa, osec, psec, pdata);
            pdata.sov_delta = 0.0;

            pdata.sov_what = 0;
            out0 = sov(n_aa, osec, psec, pdata);
            pdata.sov_what = 1;
            out1 = sov(n_aa, osec, psec, pdata);
            pdata.sov_what = 2;
            out2 = sov(n_aa, osec, psec, pdata);
            pdata.sov_what = 3;
            out3 = sov(n_aa, osec, psec, pdata);
        }


        return;
    }

    void default_parameters(SovPars pdata) {

        pdata.input = 0;
        pdata.order = 0;
        pdata.sov_method = 1;   // 0: JMB 1: new
        pdata.sov_delta = 1.0;
        pdata.sov_delta_s = 0.5;
        pdata.sov_out = 0;

        return;
    }

    double sov(int n_aa, String sss1, String sss2, SovPars pdata) {
        int i, k, length1, length2, beg_s1, end_s1, beg_s2, end_s2;
        int j1, j2, k1, k2, minov, maxov, d, d1, d2, n, multiple;
        d = 0;
        char s1, s2;
        char[] sse = new char[3];
        double out;
        double s, x;

        sse[0] = '#';
        sse[1] = '#';
        sse[2] = '#';

        if (pdata.sov_what == 0) {
            sse[0] = 'H';
            sse[1] = 'E';
            sse[2] = 'C';

        }

        if (pdata.sov_what == 1) {
            sse[0] = 'H';
            sse[1] = 'H';
            sse[2] = 'H';

        }

        if (pdata.sov_what == 2) {
            sse[0] = 'E';
            sse[1] = 'E';
            sse[2] = 'E';
        }

        if (pdata.sov_what == 3) {
            sse[0] = 'C';
            sse[1] = 'C';
            sse[2] = 'C';
        }

        n = 0;

        for (i = 0; i < n_aa; i++) {
            s1 = sss1.charAt(i);

            if (s1 == sse[0] || s1 == sse[1] || s1 == sse[2]) {
                n++;
            }
        }

        out = 0.0;
        s = 0.0;
        length1 = 0;
        length2 = 0;
        i = 0;

        while (i < n_aa) {
            beg_s1 = i;
            s1 = sss1.charAt(i);

            while (i < n_aa && sss1.charAt(i) == s1) {
                i++;
            }

            end_s1 = i - 1;
            length1 = end_s1 - beg_s1 + 1;
            multiple = 0;
            k = 0;

            while (k < n_aa) {
                beg_s2 = k;
                s2 = sss2.charAt(k);

                while (k < n_aa && sss2.charAt(k) == s2) {
                    k++;
                }

                end_s2 = k - 1;
                length2 = end_s2 - beg_s2 + 1;

                if (s1 == sse[0] || s1 == sse[1] || s1 == sse[2]) {
                    if (s1 == s2 && end_s2 >= beg_s1 && beg_s2 <= end_s1) {
                        if (multiple > 0 && pdata.sov_method == 1) {
                            n = n + length1;
                        }

                        multiple++;
                        if (beg_s1 > beg_s2) {
                            j1 = beg_s1;
                            j2 = beg_s2;
                        } else {
                            j1 = beg_s2;
                            j2 = beg_s1;
                        }

                        if (end_s1 < end_s2) {
                            k1 = end_s1;
                            k2 = end_s2;
                        } else {
                            k1 = end_s2;
                            k2 = end_s1;
                        }

                        minov = k1 - j1 + 1;
                        maxov = k2 - j2 + 1;
                        d1 = (int) Math.floor(length1 * pdata.sov_delta_s);
                        d2 = (int) Math.floor(length2 * pdata.sov_delta_s);

                        if (d1 > d2) d = d2;
                        if (d1 <= d2 || pdata.sov_method == 0) d = d1;
                        if (d > minov) {
                            d = minov;
                        }
                        if (d > maxov - minov) {
                            d = maxov - minov;
                        }

                        x = pdata.sov_delta * d;
                        x = (minov + x) * length1;

                        if (maxov > 0) {
                            s = s + x / maxov;
                        } else {
                            System.out.println("error");
                            ////	printf("\n ERROR! minov = %-4d maxov = %-4d length = %-4d d = %-4d   %4d %4d  %4d %4d",
                            ////			 minov,maxov,length1,d,beg_s1+1,end_s1+1,beg_s2+1,end_s2+1);
                        }

                        if (pdata.sov_out == 2) {
                            System.out.println("test");
                            ////	printf("\n TEST: minov = %-4d maxov = %-4d length = %-4d d = %-4d   %4d %4d  %4d %4d",
                            ////		   minov,maxov,length1,d,beg_s1+1,end_s1+1,beg_s2+1,end_s2+1);
                        }
                    }
                }
            }
        }

        if (n > 0) {
            out = s / n;
        } else {
            out = 1.0;
        }

        return out;
    }

    double q3(int n_aa, String sss1, String sss2, SovPars pdata) {
        int i, n;
        double out;
        char s;
        char[] sse = new char[3];

        sse[0] = '#';
        sse[1] = '#';
        sse[2] = '#';

        if (pdata.q3_what == 0) {
            sse[0] = 'H';
            sse[1] = 'E';
            sse[2] = 'C';
        }

        if (pdata.q3_what == 1) {
            sse[0] = 'H';
            sse[1] = 'H';
            sse[2] = 'H';
        }

        if (pdata.q3_what == 2) {
            sse[0] = 'E';
            sse[1] = 'E';
            sse[2] = 'E';
        }

        if (pdata.q3_what == 3) {
            sse[0] = 'C';
            sse[1] = 'C';
            sse[2] = 'C';
        }

        n = 0;
        out = 0.0;

        for (i = 0; i < n_aa; i++) {
            s = sss1.charAt(i);
            if (s == sse[0] || s == sse[1] || s == sse[2]) {
                n++;
                if (sss1.charAt(i) == sss2.charAt(i)) {
                    out = out + 1.0;
                }
            }
        }

        if (n > 0) {
            out = out / n;
        } else {
            out = 1.0;
        }

        return out;
    }

}
