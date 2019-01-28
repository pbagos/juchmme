package hmm;

class Test {
    private double[][] pp2;

    public Test(HMM estimate, Seq sequence, boolean free) throws Exception {
        Run(estimate, sequence, free, true);
    }

    public Test(HMM estimate, Seq sequence, boolean free, boolean showResults) throws Exception {
        Run(estimate, sequence, free, showResults);
    }

    public void Run(HMM estimate, Seq sequence, boolean free, boolean showResults) throws Exception {
        Forward fwd2 = new Forward(estimate, sequence, free);
        Backward bwd2 = new Backward(estimate, sequence, free);

        if (fwd2.logprob() == Double.NEGATIVE_INFINITY) {
            //return null;
            System.out.println("Error");
            System.exit(0);
        }

        PosteriorProb postp2 = new PosteriorProb(fwd2, bwd2);

        double[][] pp2 = postp2.getPProb();

        if (Params.VITERBI > -1) {
            Viterbi vit2 = new Viterbi(estimate, sequence, free);
            sequence.path[Params.VITERBI] = vit2.getPath2();
            sequence.score[Params.VITERBI] = vit2.getProb() - fwd2.logprob();
            sequence.relscore[Params.VITERBI] = CalcRelScore(sequence.getLen(), sequence.path[Params.VITERBI], pp2);
        }

        if (Params.POSVIT > -1) {
            PosViterbi posvit2 = new PosViterbi(estimate, sequence, free, postp2);
            sequence.path[Params.POSVIT] = posvit2.getPath2();
            sequence.score[Params.POSVIT] = posvit2.getProb() - fwd2.logprob();
            sequence.relscore[Params.POSVIT] = CalcRelScore(sequence.getLen(), sequence.path[Params.POSVIT], pp2);
        }

        if (Params.PLP > -1) {
            PLP plp2 = new PLP(estimate, sequence, free, postp2);
            sequence.path[Params.PLP] = plp2.getPath2();
            sequence.score[Params.PLP] = plp2.getProb() - fwd2.logprob();
            sequence.relscore[Params.PLP] = CalcRelScore(sequence.getLen(), sequence.path[Params.PLP], pp2);
        }


        if (Params.NBEST > -1) {
            NBest nbest = new NBest(estimate, sequence, 1, free);
            sequence.path[Params.NBEST] = nbest.getPath();
            sequence.score[Params.NBEST] = nbest.getProb() - fwd2.logprob();
            sequence.relscore[Params.NBEST] = CalcRelScore(sequence.getLen(), sequence.path[Params.NBEST], pp2);
        }

        if (Params.DYNAMIC > -1 || Args.SHOW_PLOT || Args.graphPlotDir != null) {

            if (Params.DYNAMIC > -1) {
                Dynamic dyn = new Dynamic(postp2.getPProb(), sequence.getLen(), 'M', 'O', 'I');
                sequence.path[Params.DYNAMIC] = dyn.getPath();

                sequence.dynScoreDiff = dyn.GetScoreDiff();

                sequence.relscore[Params.DYNAMIC] = CalcRelScore(sequence.getLen(), sequence.path[Params.DYNAMIC], pp2);

            }

            double[][] pp = postp2.getPProb();

            if (Args.SHOW_PLOT || Args.graphPlotDir != null) {

                if (Args.SHOW_PLOT)
                    sequence.ShowProb(pp);

                if (Args.graphPlotDir != null)
                    saveGraphPlot(sequence, pp);
            }

        }

        int lng = sequence.getLen();
        double maxprob1 = 0;

        for (int i = 0; i < lng; i++) {
            if (maxprob1 < pp2[i][1])
                maxprob1 = pp2[i][1];
        }

        double pnull = 0;

        for (int i = 0; i < lng; i++) {
            int x = Model.esym.indexOf(sequence.getSym(i));
            if (x > -1)
                pnull += Math.log(Model.cprior[x]);
        }

        sequence.logOdds = (fwd2.logprob() - pnull);
        sequence.maxProb = maxprob1;
        sequence.logProb = fwd2.logprob();
        sequence.lng = lng;
    }

    public static double CalcRelScore(final int len, final String path, final double[][] pp) {
        double relScore = 0;
        for (int i = 0; i < len; i++) {
            int lab = Model.psym.indexOf(path.charAt(i));
            if (lab == -1)
                System.out.println("Error. Unknown label on " + i);

            else
                relScore += pp[i][lab];
        }

        relScore /= len;

        return relScore;
    }

    private static void saveGraphPlot(Seq seq, double[][] total) {

        int lng = seq.getLen();
        int nmeth = 0;

        if (Params.VITERBI > -1)
            nmeth++;
        if (Params.POSVIT > -1)
            nmeth++;
        if (Params.PLP > -1)
            nmeth++;
        if (Params.NBEST > -1)
            nmeth++;
        if (Params.DYNAMIC > -1)
            nmeth++;

        String[] mypath = new String[nmeth];

        int i = 0;

        if (Params.VITERBI > -1) {
            mypath[i] = seq.path[Params.VITERBI];
            i++;
        }
        if (Params.POSVIT > -1) {
            mypath[i] = seq.path[Params.POSVIT];
            i++;
        }
        if (Params.PLP > -1) {
            mypath[i] = seq.path[Params.PLP];
            i++;
        }
        if (Params.NBEST > -1) {
            mypath[i] = seq.path[Params.NBEST];
            i++;
        }
        if (Params.DYNAMIC > -1) {
            mypath[i] = seq.path[Params.DYNAMIC];
            i++;
        }

        java.lang.StringBuffer buf1 = new java.lang.StringBuffer();

        for (i = 0; i < seq.getLen(); i++)
            for (int q = 0; q < Model.npsym - 2; q++) {
                if (total == null)
                    buf1.append(0.0D);
                else
                    buf1.append(total[i][q]);


                if (q < Model.npsym - 2 - 1)
                    buf1.append("|");
                else buf1.append("&");
            }

        String plotStr = buf1.toString();


        int scaleX = 50;

        if (lng <= 10)
            scaleX = 2;
        else if (lng <= 50)
            scaleX = 10;
        else if (lng <= 100)
            scaleX = 20;


        int width = 250;

        if (lng < 250)
            width = 250;
        else
            width = lng;

        int height = 120 + mypath.length * 20;


        String filename = seq.header.replaceAll("\\W+?", "_") + ".jpg";

        //ConPlot.display ( plotStr, mypath, width, height, 0.9f,
        //			0.0f, 0.0f, scaleX,
        //			graphPlotDir + File.separator + filename, null, ""  );

    }

}