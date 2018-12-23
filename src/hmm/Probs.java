package hmm;

import java.text.*;
import java.util.*;
import java.io.*;

import nn.FileEntries;

class Probs
{
    double[][] aprob;
    double[][] eprob;

    Weights weights;


    public Probs( double[][] a, double[][] e )
    {
        this.aprob=new double[Model.nstate][Model.nstate];
        this.eprob=new double[Model.nstate][Model.nesym];

        for( int i=0; i<Model.nstate; i++ )
        {
            for( int j=0; j<Model.nstate; j++ )
            {
                this.aprob[i][j]=a[i][j];
            }
        }

        for( int i=0; i<Model.nstate; i++ )
        {
            for( int j=0; j<Model.nesym; j++ )
            {
                this.eprob[i][j]=e[i][j];
            }
        }
    }


    public Probs( double[][] a, Weights w )
    {
        this.aprob=new double[Model.nstate][Model.nstate];

        for( int i=0; i<Model.nstate; i++ )
        {
            for( int j=0; j<Model.nstate; j++ )
            {
                this.aprob[i][j]=a[i][j];
            }
        }

        this.weights = w.GetClone();
    }


    public Probs( String file_a )
    {
        this.aprob=new double[Model.nstate][Model.nstate];
        initProbsByFile( file_a, this.aprob, Model.nstate, Model.nstate  );

        weights = new Weights();

    }

    public Probs( String file_a, String file_e )
    {
        this.aprob=new double[Model.nstate][Model.nstate];
        this.eprob=new double[Model.nstate][Model.nesym];

        initProbsByFile( file_a, this.aprob, Model.nstate, Model.nstate );
        initProbsByFile( file_e, this.eprob, Model.nstate, Model.nesym );
    }

    public Probs(SeqSet seqs )
    {

        String[] vPaths      = new String[seqs.nseqs];
        String[] vPathSOstte = new String[seqs.nseqs];

        this.aprob = new double[Model.nstate][Model.nstate];
        this.eprob = new double[Model.nstate][Model.nesym];

        if (Params.TRANSITIONS.equals("VITERBI") ||
            Params.EMISSIONS.equals("VITERBI")   ||
            Params.WEIGHTS.equals("RPROP")   ) {

            ViterbiRun(seqs, vPaths, vPathSOstte);
        }

        //Transitions
        if (Params.TRANSITIONS.equals("FILE")) {
            System.out.println("Initialize Transition Probabilities By File");
            initProbsByFile( Args.fileA, this.aprob, Model.nstate, Model.nstate );
        } else if (Params.TRANSITIONS.equals("UNIFORM")) {
            UniformizeTransitions();
        } else if (Params.TRANSITIONS.equals("RANDOM")) {
            RandomizeTransitions();
        } else if (Params.TRANSITIONS.equals("VITERBI")) {
            ViterbiSetTransitions(seqs, vPaths, this.aprob);
        } else{
            System.err.println( "Error with Configuration Setting TRANSITIONS. " +
                    "Acceptable values are FILE,RANDOM,UNIFORM,VITERBI" );
            System.exit(1);
        }

        //Emissions
        if ( !Params.HNN ) {
            if (Params.EMISSIONS.equals("FILE")) {
                System.out.println("Initialize Emission Probabilities By File");
                initProbsByFile(Args.fileE, this.eprob, Model.nstate, Model.nesym);
            } else if (Params.EMISSIONS.equals("UNIFORM")) {
                UniformizeEmissions();
            } else if (Params.EMISSIONS.equals("RANDOM")) {
                RandomizeEmissions();
            } else if (Params.EMISSIONS.equals("VITERBI")) {
                ViterbiSetEmissions(seqs, vPaths, this.eprob);
            } else {
                System.err.println("Error with Configuration Setting EMISSIONS. " +
                        "Acceptable values are FILE,RANDOM,UNIFORM,VITERBI");
                System.exit(1);
            }
        }

        //Weights (only HNN)
        if ( Params.HNN ) {
            weights = new Weights();
            FileEntries fileData;

            if (Params.WEIGHTS.equals("FILE")) {
                System.out.println("Initialize Weights By File");
                weights.initWeightsByFile(Args.fileW);
                //fileData = new FileEntries(Args.fileD);

                //Run RpropNN to initalize weights
                //weights.InitializeByRpropNN(fileData);

            } else if ( Params.WEIGHTS.equals("RANDOM_NORMAL" )) {
                weights.RandomizeNormal(Params.STDEV, Params.SEED);
            } else if ( Params.WEIGHTS.equals("RANDOM_UNIFORM" )) {
                weights.RandomizeUniform( Params.RANGE, Params.SEED );
            }else if (Params.WEIGHTS.equals("RPROP")) {
                System.out.println("Initialize Weights using RpropNN Method");

                fileData = new FileEntries(seqs, vPathSOstte);

                //Run RpropNN to initalize weights
                weights.InitializeByRpropNN(fileData);
                weights.Initialize();

            } else {
                System.err.println("Error with Configuration Setting WEIGHTS. " +
                        "Acceptable values are FILE, RANDOM_NORMAL, RANDOM_UNIFORM, RPROP, BOOT");
                System.exit(1);

            }

        }

        //printTransitions();
        //printEmissions();

        System.out.println("--------------------------------------------------------------------------");

    }

    private void ViterbiRun(SeqSet seqs, String[] vPathStte, String[] vPathOstte){
        Probs tab0;
        HMM   hmm0;

        if ( Params.HNN )
            tab0 = new Probs(Args.fileA);
        else
            tab0 = new Probs( Args.fileA, Args.fileE );

        hmm0  = new HMM( tab0 );

        Viterbi v;
        for (int s=0; s<seqs.nseqs; s++)
        {
            v = new Viterbi(hmm0, seqs.seq[s], false );
            vPathStte[s]  = v.getPath();      // Viterbi Path
            vPathOstte[s] = v.getPathOstte(); // Viterbi Path Ostte
        }
    }

    private void ViterbiSetTransitions(SeqSet seqs, String[] vPaths, double[][] a){
        System.out.println("Initialize Transition Probabilities using Viterbi Method");
        Seq x;
        double[][] A  = new double[Model.nstate][Model.nstate];
        double[][] a0 = new double[Model.nstate][Model.nstate];
        a0=a;
        for (int s=0; s<seqs.nseqs; s++)
        {
            String [] Path;
            x = seqs.seq[s];
            int length=x.getLen();// Sequence Length

            //States length. All states must have the same length
            int stateLen = Model.state[0].length();
            Path = new String[length];

            //Split ViterbiPath to a String Array with path states
            Path = vPaths[s].split("(?<=\\G.{"+stateLen+"})");

            //if exists the begin state, find the next and scoring
            if(Params.ALLOW_BEGIN)
            {
                int k=Arrays.asList(Model.state).indexOf(Path[0]);
                A[0][k]=A[0][k] + 1;
            }

            int seqLen=x.getLen();
            int sym, row, col, i;
            for (i=1; i<=seqLen-1; i++)
            {
                sym = x.getNESym(i-1);
                row = Arrays.asList(Model.state).indexOf(Path[i-1]);
                col = Arrays.asList(Model.state).indexOf(Path[i]);

                A[row][col] = A[row][col] + 1;
            }

            //Score for the last state
            sym = x.getNESym(i-1);
            row = Arrays.asList(Model.state).indexOf(Path[i-1]);
            //e[row][sym]=e[row][sym] + 1;

            //if exists the end state, find the previous state and scoring
            if(Params.ALLOW_END)
            {
                row = Arrays.asList(Model.state).indexOf(Path[i-1]);
                col = (Model.nstate)-1;
                A[row][col] = A[row][col] + 1;
            }
        }

        //Updating transitions
        for (int k=0; k<Model.nstate; k++)
        {
            double Aksum = 0;
            for (int ell=0; ell<Model.nstate; ell++){
                Aksum += A[k][ell];
            }

            for (int ell=0; ell<Model.nstate; ell++)
            {
                a0[k][ell] = ( ( Aksum==0 )? 0: A[k][ell] / Aksum );
            }

        }

        for (int k=0; k<Model.nstate; k++)
        {
            if (Params.NOISE_TR)
                a0[k] = ML.noiseTrans( Model.nstate, a0[k], a[k], 0 );
        }

        a = a0;

    }


    private void ViterbiSetEmissions(SeqSet seqs, String[] vPaths, double[][] e){
        System.out.println("Initialize Emission Probabilities using Viterbi Method");

        Seq x;
        double[][] E  = new double[Model.nstate][Model.nesym];
        double[][] e0 = new double[Model.nstate][Model.nesym];
        e0=e;

        for (int s=0; s<seqs.nseqs; s++)
        {
            String [] Path;
            x = seqs.seq[s];
            int length=x.getLen();// Sequence Length

            //States length. All states must have the same length
            int stateLen = Model.state[0].length();
            Path = new String[length];

            //Split ViterbiPath to a String Array with path states
            Path = vPaths[s].split("(?<=\\G.{"+stateLen+"})");

            int seqLen=x.getLen();
            int sym, row, col, i;
            for (i=1; i<=seqLen-1; i++)
            {
                sym = x.getNESym(i-1);
                row = Arrays.asList(Model.state).indexOf(Path[i-1]);
                col = Arrays.asList(Model.state).indexOf(Path[i]);

                E[row][sym] = E[row][sym] + 1;
            }

            //Score for the last state
            sym = x.getNESym(i-1);
            row = Arrays.asList(Model.state).indexOf(Path[i-1]);
            E[row][sym]=E[row][sym] + 1;

        }

        //Updating emissions
        for (int k=0; k<Model.nstate; k++)
        {
            double Eksum = 0;
            for (int b=0; b<Model.nesym; b++)
                Eksum += E[k][b];

            for (int b=0; b<Model.nesym; b++)
            {
                e0[k][b] = (( Eksum==0 )? 0: E[k][b] / Eksum );
            }
        }

        for (int k=0; k<Model.nstate; k++)
        {
             if (Params.NOISE_EM)
                e0[k] = Model.putPriorEM(Model.nesym, e0[k], e[k], k );
        }

        e = e0;

    }

    private void initProbsByFile(  String file, double[][] ap, int li, int lj )
    {
        String linea="";
        int s=0,y=0;
        RandomAccessFile in=null;
        int extraParams = ( Params.PAST_OBS_EXT? Model.enc.getDimension():1 );

        try{
            in = new RandomAccessFile( file,"r");
            try{
                s=0;

                while( (linea=in.readLine())!=null )
                {
                    if( s>li )
                        System.err.println( file+": more rows than "+li );

                    StringTokenizer st = new StringTokenizer(linea, " \t");
                    int count = st.countTokens();

                    if( count !=lj )
                        if( count*extraParams !=lj )
                            System.err.println( file+": "+count+"columns instead of "+lj+" at line "+(s+1) );

                    for(int a=0; a<count; a++)
                    {
                        Double A=Double.valueOf(st.nextToken());

                        //Check for Esym Alphabet extented Parameters
                        if( count*extraParams ==lj ) {
                            for (int j = 0; j < extraParams; j++) {
                                ap[s][y] = (double) A.doubleValue() / extraParams;
                                //System.out.println(A.doubleValue()+" "+ap[s][y]);
                                y++;
                            }
                        }else{
                            ap[s][a] = A.doubleValue();
                            //System.out.println(A.doubleValue()+" "+ap[s][a]);
                        }

                    }

                    s++;
                    y=0;
                }
            }catch (IOException e) {System.out.println("File access "+file+"ERROR 1");}
        } catch (IOException e){System.out.println("File access "+file+" ERROR 2");}
        finally
        {
            try{
                in.close();
            }
            catch (Exception e)
            {}
        }
        //System.out.println(s);
        return;
    }

    public void UniformizeTransitions()
    {
        System.out.println( "Uniformizing Transitions" );
        for( int i=0; i<this.aprob.length; i++ )
        {
            int c=0;
            for( int j=0; j<this.aprob[i].length; j++ )
                if( aprob[i][j]>0 )
                    c++;

            for( int j=0; j<this.aprob[i].length; j++ )
                if( aprob[i][j]>0 )
                    aprob[i][j]=1.0D/c;
        }
    }

    public void UniformizeEmissions()
    {
        System.out.println( "Uniformizing Emissions" );
        for( int i=0; i < this.eprob.length; i++ )
            for( int j=0; j < this.eprob[i].length; j++ )
                this.eprob[i][j]=1.0D/this.eprob[i].length;


    }

    public void RandomizeTransitions()
    {
        System.out.println( "Randomizing Transitions" );
        Random rnd=new Random( Params.SEED );

        for( int i=0; i<this.aprob.length; i++ )
        {
            double sum=0.0D;

            for( int j=0; j<this.aprob[i].length; j++ )
                if( aprob[i][j]>0 )
                {
                    aprob[i][j] = rnd.nextDouble();
                    sum+=aprob[i][j];
                }

            for( int j=0; j<this.aprob[i].length; j++ )
                if( aprob[i][j]>0 )
                    aprob[i][j]/=sum;
        }

    }

    public void RandomizeEmissions()
    {
        System.out.println( "Randomizing Emissions" );
        Random rnd=new Random( Params.SEED );

        for( int i=0; i<this.eprob.length; i++ )
        {
            double sum=0.0D;
            for( int j=0; j<this.eprob[i].length; j++ )
            {
                eprob[i][j]=rnd.nextDouble();
                sum+=eprob[i][j];
            }

            for( int j=0; j<this.eprob[i].length; j++ )
                eprob[i][j]/=sum;

        }

    }

    public double aDiff ( Probs pr )
    {
        double dmax=0;
        for( int i=0; i<Model.nstate; i++ )
        {
            for( int j=0; j<Model.nstate; j++ )
            {
                double d = Math.abs( this.aprob[i][j] - pr.aprob[i][j] );
                if( d> dmax )	dmax=d;
            }

        }

        return dmax;
    }

    public double eDiff ( Probs pr )
    {
        double dmax=0;
        for( int i=0; i<Model.nstate; i++ )
        {
            for( int j=0; j<Model.nesym; j++ )
            {
                double d = Math.abs( this.eprob[i][j] - pr.eprob[i][j] );
                if( d>dmax )	dmax=d;
            }
        }

        return dmax;
    }

    public void printTransitions(){
        for( int i=0; i<this.aprob.length; i++ ) {
            for (int j = 0; j < this.aprob[i].length; j++) {
                System.out.print(this.aprob[i][j] + "\t");
            }
            System.out.println("");
        }

    }

    public void printEmissions(){
        for( int i=0; i<this.eprob.length; i++ ) {
            for (int j = 0; j < this.eprob[i].length; j++) {
                System.out.print(this.eprob[i][j] + "\t");
            }
            System.out.println("");
        }

    }

}
