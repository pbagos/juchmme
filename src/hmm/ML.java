package hmm;
import java.io.*;
import java.util.*;

class ML extends TrainAlgo
{
    public  double  valLog;
    private boolean valid; // If true enable EARLY functionality
    private double[][]E;
    private double[][]A;

    private Probs tab;

    public ML( final SeqSet trainSet, final Probs tab0,  final SeqSet valSeqs, WeightsL weightsL  ) throws Exception
    {
        valid = true;
        Run( trainSet, tab0, valSeqs, 0.0D, weightsL );
    }

    public ML( final SeqSet trainSet, final Probs tab0, final double stopLog, WeightsL weightsL  ) throws Exception
    {
        valid = false;
        Run( trainSet, tab0, new SeqSet( 0 ), stopLog, weightsL );
    }

    public void Run( final SeqSet trainSet, final Probs tab0, final SeqSet valSeqs, final double stopLog, WeightsL weightsL ) throws Exception
    {
        //If TRUE  reestimation computed using the ViterbiTraining algorithm
        //if FALSE reestimation computed using the Forward-Backward alogirth
        boolean TrainingWithViterbi=Params.RUN_ViterbiTraining;

        tab=new Probs( tab0.aprob, tab0.eprob );

        double[][] gradA = new double[Model.nstate][Model.nstate];
        double[][] gradE = new double[Model.nstate][Model.nesym];

        double[][] down_gradA = new double[Model.nstate][Model.nstate];
        double[][] down_gradE = new double[Model.nstate][Model.nesym];

        // Set up the inverse of b -> esym.charAt(b); assume all esyms <= 'Z'
        int[] esyminv = new int[Model.esyminv];

        for (int i=0; i<esyminv.length; i++)
            esyminv[i] = -1;

        for (int b=0; b<Model.nesym; b++)
            esyminv[Model.esym.charAt(b)] = b;

        /*
   		// Initially use random transition and emission matrices
   		for (int k=0; k<Model.nstate; k++)
        {
             amat[k] = randomdiscrete(Model.nstate);
             emat[k] = randomdiscrete(Model.nesym);
        }
        */

        // noise
        for (int k=0; k<Model.nstate; k++)
        {
            if (Params.NOISE_TR)
                tab.aprob[k] = noiseTrans( Model.nstate, tab.aprob[k], tab0.aprob[k], iter );

            if (Params.NOISE_EM)
                tab.eprob[k] = Model.putPriorEM( Model.nesym, tab.eprob[k], tab0.eprob[k], k  );
        }

        hmm = new HMM( tab );

        double oldloglikelihood =0, oldvalLoglikelihood =0, loglikelihood=0;

        Forward[]  fwds   = new Forward[trainSet.nseqs];
        Backward[] bwds   = new Backward[trainSet.nseqs];

        double[]   logP   = new double[trainSet.nseqs];
        String[]   vPaths = new String[trainSet.nseqs];

        //Initialization Step
        if (TrainingWithViterbi)
            loglikelihood = ViterbiTraining( trainSet, vPaths, logP, false, weightsL  );
        else
            loglikelihood = fwdbwd( fwds, bwds, logP, false, trainSet, weightsL  );


        double valLoglikelihood=Double.NEGATIVE_INFINITY;

        if( valid )
            valLoglikelihood = fwdbwd( false, valSeqs );

        if( loglikelihood==Double.NEGATIVE_INFINITY )
            System.out.println("Probable illegal transition found");

        System.out.println(iter + "\tlog likelihood = " + loglikelihood);

        if( valid  )
            System.out.println("\tval log likelihood = " + valLoglikelihood);

        double logdiff = 0;

        double[][] A_old = new double[Model.nstate][Model.nstate];
        double[][] E_old = new double[Model.nstate][Model.nesym];

        int q = 0;
        do
        {
            A = new double[Model.nstate][Model.nstate];
            E = new double[Model.nstate][Model.nesym];

            oldloglikelihood    = loglikelihood;
            oldvalLoglikelihood = valLoglikelihood;

            if( logdiff<= 0 ||  ( !Params.JACOBI ))
            {
                q = 1;

                for( int k=0; k<Model.nstate; k++ )
                    for( int ell=0; ell<Model.nstate; ell++ )
                        down_gradA[k][ell] = gradA[k][ell];

                for( int ell=0; ell<Model.nstate; ell++ )
                    for( int b=0; b<Model.nesym; b++ )
                        down_gradE[ell][b] = gradE[ell][b];

                System.out.println("\tComputing expected counts");
                System.out.print("\t");

                for (int s=0; s<trainSet.nseqs; s++)  // Foreach sequence
                {
                    System.out.print( "." );

                    //Compute estimates for A and E
                    //If TRUE  reestimation computed using the ViterbiTraining algorithm
                    //if FALSE reestimation computed using the Forward-Backward alogirth
                    if(!TrainingWithViterbi)
                    {
                        Forward fwd  = fwds[s];
                        Backward bwd = bwds[s];
                        int seqLen = trainSet.seq[s].getLen();
                        double P = logP[s];

                        for (int i=1; i<=seqLen; i++)
                            for (int k=0; k<Model.nstate; k++)
                            {
                                E[k][esyminv[trainSet.seq[s].getSym(i-1)]] +=
                                        exp(fwd.f[i][k] + bwd.GetVal( i, k ) - P) * weightsL.getWeightL(trainSet.seq[s].getIndexID()) ;

                            }

                        AddExpC_A( A, trainSet.seq[s], P, fwd, bwd, weightsL );
                    }
                    else
                    {
                        String vPath = vPaths[s];
                        ViterbiTrainingExp(vPath, trainSet.seq[s], A, E, weightsL );
                    }
                }	//end foreach sequence

                System.out.println();
                Tying( E );

                //M-step Update Parameters
                if(Params.RUN_GRADIENT )
                {
                    double[][] new_grad;
                    //new_grad is created
                    new_grad = ComputeGrad( A, tab.aprob );

                    if( Params.SILVA )
                        //ka is modified
                        Silva( Ka, Params.kappaAmax, Params.kappaAmin, new_grad, gradA );

                    //new_grad is modified
                    if( Params.RPROP )
                        Rprop( new_grad, Ka );
                    else
                        Gradient( new_grad, gradA, Ka );

                    //tab.aprob is modidied
                    Exponentiate( new_grad, tab.aprob, 0 );
                    gradA = new_grad;

                    new_grad = ComputeGrad( E, tab.eprob );

                    if( Params.SILVA )
                        Silva( Ke, Params.kappaEmax, Params.kappaEmin, new_grad, gradE );

                    if( Params.RPROP )
                        Rprop( new_grad, Ke );
                    else
                        Gradient( new_grad, gradE, Ke );

                    Exponentiate( new_grad, tab.eprob, 0  );
                    gradE = new_grad;

                    //LineSearch();

                } else {
                    BaumWelch( A, E, tab );
                }

            } else {
                System.out.println("\tPerforming Jacobi bisection q = " + q );
                /*	tab = new Probs( down_tab.aprob, down_tab.eprob );*/
                Exponentiate( down_gradA, tab.aprob, q );
                Exponentiate( down_gradE, tab.eprob, q );
                /*	oldloglikelihood = down_loglikelihood; */
                q++;
            }

            iter++;

            for (int k=0; k<Model.nstate; k++)
            {
                if (Params.NOISE_TR)
                    //tab.aprob is modified
                    tab.aprob[k] = noiseTrans( Model.nstate, tab.aprob[k], tab0.aprob[k], iter );

                if (Params.NOISE_EM)
                    tab.eprob[k] = Model.putPriorEM(Model.nesym, tab.eprob[k], tab0.eprob[k], k );
            }

            // Create new model
            hmm = new HMM( tab );

            //E-step
            //
            if (TrainingWithViterbi)
                loglikelihood = ViterbiTraining(trainSet, vPaths, logP, false, weightsL  );
            else
                loglikelihood = fwdbwd( fwds, bwds, logP, false, trainSet, weightsL  );

            logdiff = oldloglikelihood - loglikelihood;
            System.out.println(iter+"\tlog likelihood = " + loglikelihood + "\t\t diff = "+logdiff) ;


            if( valid )
            {
                valLoglikelihood = fwdbwd( false, valSeqs);

                System.out.print("\tval log likelihood = " + valLoglikelihood + "\t\t diff = " );

                if( valLoglikelihood > oldvalLoglikelihood || iter<Params.ITER )
                {
                    System.out.println( "DOWN" );
                } else {
                    System.out.println( "UP" );
                    valLog = loglikelihood;
                    return;
                }
            }

            hmm.SaveModel();
        } while ( Math.abs ( logdiff ) > Params.threshold && iter<Params.maxIter && loglikelihood<stopLog);

        hmm.lh=loglikelihood;
        hmm.SaveModel();
    }

    public Probs GetProbs()
    {
        return tab;
    }
}
