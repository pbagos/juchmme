package hmm;

class TrainProc
{
    public TrainProc(SeqSet trainSet, SeqSet trainSetUn, Probs tab) throws Exception
    {
        if (Args.RUN_JACKNIFE) {
            System.out.println("TRAINING - Jacknife");
            jackNife(trainSet, trainSetUn, tab);
        } else if (Args.RUN_CROSSVAL) {
            System.out.println("TRAINING - Cross Validation k-fold = "+Args.CROSSVAL_CLUSTSIZE);
            CrossVal(trainSet, trainSetUn, tab, Args.CROSSVAL_CLUSTSIZE);
        } else if (Args.RUN_SELFCONS) {
            selfCons(trainSet, trainSetUn, tab);
        }
    }

    // Self Consistency
    static void selfCons( SeqSet trainSet, SeqSet trainSetUn, Probs tab ) throws Exception
    {
        HMM model;

        System.out.println("TRAINING - Self Consistency");
        Estimator est = new Estimator(trainSet, trainSetUn, tab);
        model = est.GetModel();

        System.out.println("TESTING");
        Decoding dec = new Decoding(model, trainSet, true, true);
    }

    //Jacknife
    static void jackNife( SeqSet jackSet, SeqSet trainSetUn, Probs tab ) throws Exception
    {
        CrossVal( jackSet, trainSetUn, tab, 1 );
    }

    //Cross Validation
    static void CrossVal( SeqSet jackSet, SeqSet trainSetUn, Probs tab, int clustSize ) throws Exception
    {
        //Cluster Lenght
        int nClust = ( int )Math.ceil( (double)jackSet.nseqs/(double)clustSize );
        SeqSet trainSet, testSet;
        int cou;
        for( int c=0; c < nClust; c++ )
        {
            System.out.println("Training cluster "+ (c+1) );

            if ((c+1)==nClust) {
                trainSet = new SeqSet(c * clustSize);
                testSet  = new SeqSet(jackSet.nseqs - c * clustSize);
            } else {
                trainSet = new SeqSet(jackSet.nseqs - clustSize);
                testSet  = new SeqSet(clustSize);
            }

            System.out.println("trainSet Len = "+trainSet.nseqs);

            cou = 0;
            for( int i=0; i < jackSet.nseqs; i++ ) {
                if (!(i >= c * clustSize && i < (c + 1) * clustSize)) {
                    trainSet.seq[cou] = jackSet.seq[i];
                    trainSet.seq[cou].SetIndexID(cou);
                    System.out.println("\tAdded " + trainSet.seq[cou].header + " to training set");
                    cou++;
                }
            }

            Estimator est = new Estimator(trainSet, trainSetUn, tab);
            HMM model = est.GetModel();

            System.out.println("Testing cluster "+ (c+1) );
            int min = (c * clustSize);
            int max = (jackSet.nseqs < ((c + 1) * clustSize))?jackSet.nseqs:((c + 1) * clustSize);

            cou = 0;
            for( int i=min; i< max; i++ ) {
                testSet.seq[cou] = jackSet.seq[i];
                testSet.seq[cou].SetIndexID(cou);
                cou++;
            }

            System.out.println("testset Len = "+testSet.nseqs);
            Decoding dec = new Decoding(model, testSet, true, true);

            cou = 0;
            for( int i=min; i< max; i++ ) {
                jackSet.seq[i] = testSet.seq[cou];
                cou++;
            }

        }
    }
}