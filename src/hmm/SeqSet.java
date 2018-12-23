package hmm;

import java.io.*;

public class SeqSet
{
    public Seq[] seq;

    public int nseqs;
    int numOfUnlabeledSeqs;
    int numOfLabeledSeqs;

    //Constructor set by file
    public SeqSet( String file )
    {
        System.out.println("Preparing Sequences");
        nseqs=CountSeqs( file );
        seq = new Seq[nseqs];

        System.out.println( nseqs+ " sequences in file "+file );

        if( Params.FASTA )
            ReadFasta(file);
        else
            ReadSeqs( file );

        numOfLabeledSeqs   = CountSeqsLabeled();
        numOfUnlabeledSeqs = CountSeqsUnLabeled();

        if (numOfLabeledSeqs>0)
            System.out.println( numOfLabeledSeqs+ " sequences are Labeled ");

        if (numOfUnlabeledSeqs>0)
            System.out.println( numOfUnlabeledSeqs+ " sequences are UnLabeled ");

        System.out.println("--------------------------------------------------------------------------");
    }

    //Constructor set by Integer (number of sequences)
    public SeqSet( int i )
    {
        nseqs=i;
        seq =new Seq[nseqs];

        for( int r=0; r<nseqs; r++ )
            seq[r]=new Seq(r);

    }

    public Seq OneSeq()
    {
        StringBuffer s1=new StringBuffer();
        for( int s=0; s<nseqs; s++ )
        {
            s1.append( seq[s].getSeq() );
            for( int j=0; j<Params.window/2-1; j++ )
                s1.append( '.' );
        }
        return new Seq( s1.toString(),0 );
    }

    public String[] getXs()
    {
        String[] arr=new String[nseqs];
        for( int i=0; i<nseqs; i++ )
            arr[i]=seq[i].getSeq();
        return arr;
    }

    //Get Original Observation Path
    public String[] getOrigPath()
    {
        String[] arr=new String[nseqs];
        for( int i=0; i<nseqs; i++ )
            arr[i]=seq[i].getOrigObs();
        return arr;
    }

    //Get the number of all sequences of the Set
    private int CountSeqs( String file )
    {
        String line="";
        int s=0;
        RandomAccessFile in;

        try{
            in = new RandomAccessFile( file,"r");
            try{
                while( (line=in.readLine())!=null ){
                    if( line.startsWith( ">" ) )
                        s++;
                }
            }catch (IOException e) {
                System.err.println("File access ERROR 1 in file "+file);
            }
        } catch (IOException e){
            System.err.println("ERROR 2 in file"+file);
        }

        return s;
    }

    //Get the number of Labeled sequences of the Set
    public int CountSeqsLabeled()
    {
        int s=0;

        for( int i=0; i<nseqs; i++ )
            if (!seq[i].IsUnlabeled())
                s++;

        return s;
    }
    //Get the number of UnLabeled sequences of the Set
    public int CountSeqsUnLabeled()
    {
        int s=0;

        for( int i=0; i<nseqs; i++ )
            if (seq[i].IsUnlabeled())
                s++;

        return s;
    }
    //Method to read all the sequences of the parameter file in threeLine format
   	private int ReadSeqs(  String file )
   	{
		String linea = "";
		String lineb = "";
		String head  = "";

		int s=0;
        Seq sqTemp;
	   	BufferedReader in;

		try{
			in = new BufferedReader( new InputStreamReader(new FileInputStream(file), "UTF-8"));

			try{
				s=0;

				while( (linea=in.readLine())!=null )
				{
					if( linea.startsWith( ">" ) )
					{
						head=linea;
						linea=in.readLine();
						lineb=in.readLine();

						seq[s]=new Seq( linea, lineb, s );
						seq[s].header=head;
						s++;
					}
					else return s;
				}
			}catch (IOException e) {System.err.println("File access ERROR 1 in file "+file);}
		} catch (IOException e){System.err.println("ERROR 2 in file "+file);}
		return s;
   }

    //Method to read all the sequences of the parameter file in Fasta format
	int ReadFasta( String file )
	{
		String ss="";
		int s=-1;
		String head="";
		String line;

		BufferedReader in;

		try{
			in = new BufferedReader( new InputStreamReader(new FileInputStream(file), "UTF-8"));

			try{

			 	while( (line=in.readLine())!=null )
			 	{
					if( line.startsWith( ">" ) )
					{
						if( ss.length()>0 )
						{
							seq[s]=new Seq( ss, s );
							seq[s].header=head;
						}
						ss="";
						head=line;
						s++;
					}
					else
					{
						ss+=line;
					}
			 	}


				if( ss.length()>0 )
				{
					seq[s]=new Seq( ss, s );
					seq[s].header=head;
				}

		 	}catch (IOException e) {System.out.println("File access ERROR 1");}
	 	} catch (IOException e){System.out.println("ERROR 2");}

	return s;
	}

	//Get the Total Length of observation symbols of all sequences
    public int getTotL()
    {
        int totL=0;
        for( int i=0; i<nseqs; i++ )
            totL+=seq[i].getLen();

        return totL;
    }
    //Get the Maximun Length of observation symbols of all sequences
    public int getMaxL()
    {
        int maxL=0;
        for( int i=0; i<nseqs; i++ )
            if( seq[i].getLen()> maxL )
                maxL=seq[i].getLen();

        return maxL;
    }

    //Method to check if exist UnLabeled Sequences in the set
    public boolean ExistUnlabeled()
    {
        for( int i=0; i<nseqs; i++ )
            if (seq[i].IsUnlabeled())
                return true;

        return false;
    }

}
