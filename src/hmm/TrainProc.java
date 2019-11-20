package hmm;

class TrainProc {
    public TrainProc(SeqSet trainSet, SeqSet trainSetUn, Probs tab) throws Exception {
        if (Args.RUN_JACKNIFE) {
            if (Args.CROSSVAL_CLUSTSIZE > 0)
                Args.CROSSVAL_CLUSTERS = (int) Math.ceil((double) trainSet.nseqs / (double) Args.CROSSVAL_CLUSTSIZE);
            else
                Args.CROSSVAL_CLUSTSIZE = (int) Math.ceil((double) trainSet.nseqs / (double) Args.CROSSVAL_CLUSTERS);

            System.out.println("TRAINING - Jacknife");
            CrossVal(trainSet, trainSetUn, tab, Args.CROSSVAL_CLUSTSIZE, Args.CROSSVAL_CLUSTERS);
        } else if (Args.RUN_CROSSVAL) {
            if (Args.CROSSVAL_CLUSTSIZE > 0)
                Args.CROSSVAL_CLUSTERS = (int) Math.ceil((double) trainSet.nseqs / (double) Args.CROSSVAL_CLUSTSIZE);
            else
                Args.CROSSVAL_CLUSTSIZE = (int) Math.ceil((double) trainSet.nseqs / (double) Args.CROSSVAL_CLUSTERS);

            System.out.println("TRAINING - Cross Validation k-fold = " + Args.CROSSVAL_CLUSTERS);
            CrossVal(trainSet, trainSetUn, tab, Args.CROSSVAL_CLUSTSIZE, Args.CROSSVAL_CLUSTERS);
        } else if (Args.RUN_SELFCONS) {
            selfCons(trainSet, trainSetUn, tab);
        }
    }

    // Self Consistency
    static void selfCons(SeqSet trainSet, SeqSet trainSetUn, Probs tab) throws Exception {
        HMM model;

        System.out.println("TRAINING - Self Consistency");
        Estimator est = new Estimator(trainSet, trainSetUn, tab);
        model = est.GetModel();
        model.SaveModel();

        System.out.println("TESTING");
        Decoding dec = new Decoding(model, trainSet, true, true, Params.parallel);
    }

    //Cross Validation
    static void CrossVal(SeqSet jackSet, SeqSet trainSetUn, Probs tab, int clustSize, int nClust) throws Exception {
        SeqSet trainSet, testSet;
        int cou;
        for (int c = 0; c < nClust; c++) {
            System.out.println("Training cluster " + (c + 1));

            if ((c + 1) == nClust) {
                trainSet = new SeqSet(c * clustSize);
                testSet = new SeqSet(jackSet.nseqs - c * clustSize);
            } else {
                trainSet = new SeqSet(jackSet.nseqs - clustSize);
                testSet = new SeqSet(clustSize);
            }

            System.out.println("trainSet Len = " + trainSet.nseqs);

            cou = 0;
            for (int i = 0; i < jackSet.nseqs; i++) {
                if (!(i >= c * clustSize && i < (c + 1) * clustSize)) {
                    trainSet.seq[cou] = jackSet.seq[i];
                    trainSet.seq[cou].SetIndexID(cou);
                    cou++;
                }
            }

            Estimator est = new Estimator(trainSet, trainSetUn, tab);
            HMM model = est.GetModel();
            model.SaveModel(Model.MODEL +"_cross"+(c + 1));

            System.out.println("Testing cluster " + (c + 1));
            int min = (c * clustSize);
            int max = (jackSet.nseqs < ((c + 1) * clustSize)) ? jackSet.nseqs : ((c + 1) * clustSize);

            cou = 0;
            for (int i = min; i < max; i++) {
                testSet.seq[cou] = jackSet.seq[i];
                testSet.seq[cou].SetIndexID(cou);
                cou++;
            }
            //testSet.SaveSet("testSet_cross"+(c + 1));

            System.out.println("testset Len = " + testSet.nseqs);
            Decoding dec = new Decoding(model, testSet, true, true, Params.parallel);

            cou = 0;
            for (int i = min; i < max; i++) {
                jackSet.seq[i] = testSet.seq[cou];
                cou++;
            }

        }
    }
}