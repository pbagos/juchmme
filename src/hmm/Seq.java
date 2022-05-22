package hmm;

import java.util.*;

public class Seq {

    private String xs;
    private String xsorig;
    private String xstates;
    private String xorig;
    private int indexID;
    String header;

    String[] path;
    double[] score;
    double[] relscore;
    double dynScoreDiff;
    double logOdds;
    double logProb;
    double maxProb;
    double lng;
    private boolean isUnlabeled;

    public Seq(String seq, String obs, int indx) {
        this.xsorig = seq;
        this.xorig = obs;

        //If set the extension of alphabet transform the sequence with new Alphabet
        if (Params.PAST_OBS_EXT) {
            if (Params.MSA) {
                seq = Utils.removeChars(seq, Params.MSAgapSymbol);
                obs = Utils.removeChars(obs, Params.MSAgapSymbol);
            }

            this.xs = Model.enc.transformSeq(seq);
        } else {
            this.xs = seq;
        }

        this.xstates = obs;
        this.indexID = indx;
        this.isUnlabeled = checkUnlabeledSeq(obs);

        init();
    }

    public Seq(String seq, int indx) {
        this.xsorig = seq;

        if (Params.PAST_OBS_EXT) {
            if (Params.MSA) {
                seq = Utils.removeChars(seq, Params.MSAgapSymbol);
            }

            this.xs = Model.enc.transformSeq(seq);
        } else {
            this.xs = seq;
        }

        this.indexID = indx;

        EmptyXStates();
        init();
    }

    public Seq(int indx) {
        this.xs = "";
        this.xsorig = "";
        this.xorig = "";
        this.xstates = "";
        this.indexID = indx;
        init();
    }

    public void Print() {
        System.out.println(this.header);
        System.out.println(this.xs);
        System.out.println(this.xstates);
    }

    public void SetObs(String newxstates) {
        xstates = newxstates;
    }

    public void SetObsOrig(String newObs) {
        xorig = newObs;
    }

    public void SetSeq(String newSeq) {
        xs = newSeq;
    }

    public void SetIndexID(int idx) {
        this.indexID = idx;
    }

    private void EmptyXStates() {
        this.xstates = "";
        this.xorig = "";

        for (int i = 0; i < xs.length(); i++) {
            this.xstates += "-";
            this.xorig += "-";
        }
    }

    public boolean checkUnlabeledSeq(String obs) {
        if (obs.indexOf('-') > -1)
            return true;

        return false;
    }

    public boolean IsUnlabeled() {
        if (this.isUnlabeled) return true;
        else return false;
    }

    private void init() {
        //Number of Decoding Method Modules
        int nufOfMod = 10;

        if (xs.length() != xstates.length())
            System.out.println("ERROR sequence and observed path differ.");

        header = "";
        path = new String[nufOfMod];
        for (int i = 0; i < nufOfMod; i++)
            for (int j = 0; j < xs.length(); j++)
                path[i] += '-';

        //Score Initialization
        score = new double[nufOfMod];
        for (int i = 0; i < nufOfMod; i++)
            score[i] = Double.NEGATIVE_INFINITY;

        //relScore Initialization
        relscore = new double[nufOfMod];
        for (int i = 0; i < nufOfMod; i++)
            relscore[i] = 0;

        dynScoreDiff = 0;
    }

    public void PutDashes(String M) {
        String newxstates = "";
        List<Integer> start = new ArrayList<>();
        List<Integer> end = new ArrayList<>();
        GetStrands(xstates, start, end, M);

        for (int i = 0; i < xstates.length(); i++) {
            boolean ok = false;

            for (int k = 0; k < start.size(); k++)
                if ((i >= start.get(k) - Params.FLANK && i < start.get(k) + Params.FLANK) ||
                        (i > end.get(k) - Params.FLANK && i <= end.get(k) + Params.FLANK)) {
                    ok = true;
                    break;
                }

            newxstates += ok ? '-' : xstates.charAt(i);
        }

        xstates = newxstates;
    }


    //SHOW RESULTS
    void ShowRes() {

        System.out.println("ID: " + header);
        System.out.println("SQ: " + xs);

        if (Params.PAST_OBS_EXT)
            System.out.println("OR: " + xsorig);

        if (Params.THREELINE || Args.RUN_TRAINING)
            System.out.println("OB: " + xorig);

        System.out.println("CC:\t" + "len = " + lng + "\tlogodds = " + logOdds + "\tmaxProb = " + maxProb + "\t(-logprob/lng) = "+ (-logProb / lng));

        if (Params.VITERBI > -1 && Params.MSA != true) {
            System.out.println("VS: " + score[Params.VITERBI]);
            if (Model.isCHMM)
                System.out.println("VR: " + relscore[Params.VITERBI]);
            System.out.println("VP: " + path[Params.VITERBI]);
        }
        if (Params.POSVIT > -1 && Params.MSA != true) {
            System.out.println("PS: " + score[Params.POSVIT]);
            if (Model.isCHMM)
                System.out.println("PR: " + relscore[Params.POSVIT]);
            System.out.println("PP: " + path[Params.POSVIT]);
        }
        if (Params.PLP > -1 && Model.isCHMM) {
            System.out.println("LS: " + score[Params.PLP]);
            System.out.println("LR: " + relscore[Params.PLP]);
            System.out.println("LP: " + path[Params.PLP]);
        }

        if (Params.NBEST > -1 && Model.isCHMM && Params.MSA != true) {
            System.out.println("NS: " + score[Params.NBEST]);
            System.out.println("NR: " + relscore[Params.NBEST]);
            System.out.println("NP: " + path[Params.NBEST]);
        }

        if (Params.DYNAMIC > -1 && Model.isCHMM && Params.MSA != true) {
            System.out.println("DR: " + relscore[Params.DYNAMIC]);
            System.out.println("DR: " + dynScoreDiff);
            System.out.println("DP: " + path[Params.DYNAMIC]);
        }

        if (Params.LPB > -1 && Model.isCHMM && Params.MSA != true) {
            System.out.println("LPBS: " + score[Params.LPB]);
            System.out.println("LPBR: " + relscore[Params.LPB]);
            System.out.println("LPBP: " + path[Params.LPB]);
        }
    }


    public int getLen() {
        return xs.length();
    }

    public int getLenOrig() {
        return xsorig.length();
    }

    public String getHeader() {
        return header;
    }

    public int getNESym(int i) {
        return Model.esym.lastIndexOf(getSym(i));
    }

    public int getNSym(int i) {
        return Model.osym.lastIndexOf(getSym(i));
    }

    public int getNPObs(int i) {
        if (getObs(i) == '-') return -1;
        return Model.psym.lastIndexOf(getObs(i));
    }

    /*
     * Get the symbol in position i of the Sequence
     */
    public char getSym(int i) {
        return xs.charAt(i);
    }

    /*
     * Get the symbol in position i of the Osbesrvation
     */
    public char getObs(int i) {
        return xstates.charAt(i);
    }

    /*
     * Get the Sequence
     */
    public String getSeq() {
        return xs;
    }

    public String getSeqOrig() {
        return xsorig;
    }

    /*
     * Get the Observasion
     */
    public String getObs() {
        return xstates;
    }

    /*
     * Get the Observasion
     */
    public String getOrigObs() {
        return xorig;
    }

    public int getIndexID() {
        return this.indexID;
    }

    void ShowProb(double[][] total) {
        int wdth = 20;
        System.out.println("#\t(res)\tObs:\tVI\tNB\tDY\tPost.");

        for (int i = 0; i < xs.length(); i++) {
            int g = 0;
            System.out.print((i + 1) + "\t" + "(" + xs.charAt(i) + ")\t");
            System.out.print(xorig.charAt(i));
            System.out.print(":\t");

            if (Params.VITERBI > -1) System.out.print("VI: " + path[Params.VITERBI].charAt(i));
            System.out.print("\t");
            if (Params.NBEST > -1) System.out.print("NB: " + path[Params.NBEST].charAt(i));
            System.out.print("\t");
            if (Params.DYNAMIC > -1) System.out.print("DY: " + path[Params.DYNAMIC].charAt(i));
            System.out.print("\t");
            if (Params.POSVIT > -1) System.out.print("PVI: " + path[Params.POSVIT].charAt(i));
            System.out.print("\t");
            if (Params.PLP > -1) System.out.print("PVI: " + path[Params.PLP].charAt(i));
            System.out.print("\t");

            for (int q = 0; q < Model.lpsym; q++) {
                System.out.print("|");
                for (g = 1; g <= total[i][q] * wdth; g++) {
                    System.out.print(Model.psym.charAt(q));
                }

                for (; g <= wdth; g++) {
                    System.out.print(" ");
                }
            }

            System.out.print("|\t");

            for (int q = 0; q < Model.lpsym; q++) {
                System.out.print(total[i][q] + "\t");
            }

            System.out.print("\n");
        }

    }

    public char[] getWindow(int i) {
        char[] win = new char[Params.window];
        int c = 0;
        for (int j = (i - Params.windowLeft); j <= (i + Params.windowRight); j++) {
            if (j < 0 || j >= xs.length()) {
                win[c] = '0';
            } else {
                win[c] = xs.charAt(j);
            }
            c++;
        }

        return win;

    }

    public int CorrectTop(String pred, String M) {
        return CorrectTop(xorig, pred, M);
    }

    public static int CorrectTop(String obs, String pred, String M) {
        List<Integer> ost = new ArrayList<>();
        List<Integer> oen = new ArrayList<>();
        List<Integer> pst = new ArrayList<>();
        List<Integer> pen = new ArrayList<>();
        GetStrands(obs, ost, oen, M);
        GetStrands(pred, pst, pen, M);

        if (ost.size() != pst.size())
            return -1;

        int err = ost.size();
        int point = 0;
        for (int i = 0; i < ost.size(); i++) {
            boolean ok = false;

            for (int j = point; j < ost.size(); j++) {
                if (oen.get(i) >= pst.get(j) && pen.get(j) >= ost.get(i)) {
                    ok = true;
                    point = j + 1;
                    break;
                }
            }
            if (ok) err--;
        }
        return err;
    }

    public static void GetStrands(String x, List<Integer> start, List<Integer> end, String M) {
        boolean inTM = false;

        for (int i = 0; i < x.length(); i++) {

            char xx = x.charAt(i);
            //System.out.println(xx);

            if (M.lastIndexOf(xx) > -1 && !inTM) {
                //System.out.println(i);
                start.add(i);
                inTM = true;
            } else if (M.lastIndexOf(xx) < 0 && inTM) {
                //System.out.println(i);
                end.add(i - 1);
                inTM = false;
            }
        }

        if (inTM) {
            end.add(x.length() - 1);
        }

    }

    public static boolean NTermIn(String x, String instr, String outstr) {
        if (instr.lastIndexOf(x.charAt(0)) > -1)
            return true;
        else if (outstr.lastIndexOf(x.charAt(0)) > -1)
            return false;
        else {
            for (int i = 1; i < x.length(); i++)
                if (instr.lastIndexOf(x.charAt(0)) > -1)
                    return false;
                else if (outstr.lastIndexOf(x.charAt(0)) > -1)
                    return true;

            return false;
        }
    }

    String ShowProb(double[][] total, int VITERBI, int POSVIT, int NBEST, int DYNAMIC, int PLP) {
        int wdth = 20;
        java.lang.StringBuffer buf = new java.lang.StringBuffer();

        //buf.append("#\t(res)\tObs:\tVI\tNB\tDY\tPost."+"\n");
        //System.out.println("#\t(res)\tObs:\tVI\tNB\tDY\tPost.");

        for (int i = 0; i < xs.length(); i++) {
            int g = 0;
            //buf.append( (i+1)+"\t"+"("+xs.charAt( i ) + ")\t" );
            //System.out.print( (i+1)+"\t"+"("+xs.charAt( i ) + ")\t" );
            if (xstates != "") {
                //System.out.print( xstates.charAt( i ) );
                //buf.append( xstates.charAt( i ) );
            }
            //buf.append( ":\t");
            //System.out.print( ":\t");

            if (VITERBI > -1) {
                //System.out.print( "VI: "+ path[VITERBI].charAt( i ) );
                //buf.append( "VI: "+ path[VITERBI].charAt( i ) );
            }
            //System.out.print( "\t");
            //buf.append( "\t");
            if (NBEST > -1) {
                //System.out.print( "NB: "+ path[NBEST].charAt( i ) );
                //buf.append( "NB: "+ path[NBEST].charAt( i ) );
            }
            //System.out.print( "\t");
            //buf.append( "\t");
            if (DYNAMIC > -1) {
                //System.out.print( "DY: "+ path[DYNAMIC].charAt( i ) );
                //buf.append( "DY: "+ path[DYNAMIC].charAt( i ) );
            }
            //System.out.print( "\t");
            //buf.append( "\t");

            for (int q = 0; q < Model.npsym; q++) {
                //buf.append("|");
                //System.out.print("|");
                for (g = 1; g <= total[i][q] * wdth; g++) {
                    //System.out.print( Model.psym.charAt( q ) );
                    //buf.append( Model.psym.charAt( q ) );
                }
                for (; g <= wdth; g++) {
                    //System.out.print( " " );
                    //buf.append( " " );
                }
            }
            //buf.append( "|\t" );
            //System.out.print( "|\t" );
            for (int q = 0; q < Model.npsym; q++) {
                buf.append(total[i][q]);

                if (q < Model.npsym - 1)
                    buf.append("|");
                else buf.append("&");
                //System.out.print( total[i][q]+"\t" );
            }
            //buf.append("\n");
            //System.out.print("\n");
        }

        return buf.toString();
    }

    String ShowRes(int VITERBI, int NBEST, int DYNAMIC, int POSVIT, int PLP) {
        java.lang.StringBuffer buf = new java.lang.StringBuffer();

        //System.out.println( "ID: "+header );
        //buf.append("ID:  "+header +"\n");

        System.out.println("SQ: " + xs);
        //buf.append("SQ:  "+ xs + "\n" );

        //System.out.println( "OB: "+ xstates );
        //buf.append("OB:  "+xstates +"\n");
        if (VITERBI > -1) {
            buf.append("VI:" + path[VITERBI] + "\n");
            System.out.println("VI: " + path[VITERBI]);
        }
        if (POSVIT > -1) {
            buf.append("PVI:" + path[POSVIT] + "\n");
            System.out.println("PV: " + path[POSVIT]);
        }
        if (PLP > -1) {
            buf.append("PLP:" + path[PLP] + "\n");
            System.out.println("LP: " + path[PLP]);
        }

        if (NBEST > -1) {
            buf.append("NB:" + path[NBEST] + "\n");
            System.out.println("NB: " + path[NBEST]);
        }
        if (DYNAMIC > -1) {

            buf.append("DY:" + path[DYNAMIC] + "\n");
            System.out.println("DY: " + path[DYNAMIC]);
        }

        return buf.toString();
    }

}

