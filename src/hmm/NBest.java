package hmm;

import java.util.*;

class NBest extends HMMAlgo
{
    private String path;
    int i;
    private double best_sum;

    public NBest(HMM hmm, Seq x, final int nbh, boolean free )
    {
        super(hmm, x);
        final int L = x.getLen();
        Vector hypo = new Vector();
        path=new String();

        //System.out.println( "Start 1 Best" );
        i=1;	//start at 1
        {
            //	System.out.println( " Position "+i );
            for( int o=0; o<Model.npsym; o++ )
            {
                Hypothese hy=new Hypothese( L+1, hmm.nstte );
                hy.lab[i]=o;
                hy.gamma[0]=Double.NEGATIVE_INFINITY;

		        int lab = x.getNPObs( i-1 );

                for (int ell=1; ell<hmm.nstte; ell++)
                {
		    	if( ( free || lab==-1 || lab==Model.plab[ell-0]  ) && Model.plab[ell-0] == o )
                       	 	hy.gamma[ell]=hmm.getLoga( 0, ell ) + hmm.getLoge( ell, x, 0 );
                    	else
                        	hy.gamma[ell]=Double.NEGATIVE_INFINITY;
                }
                hypo.add( hy );
            }
        }

        for ( i=2; i<=L; i++ )
        {
            Vector hypo_temp = new Vector();
            for( int h=0; h<hypo.size(); h++ )
            {
                for( int o=0; o<Model.npsym; o++ )
                {
                    Hypothese hy=( (Hypothese)hypo.elementAt( h ) ).copy();
                    hy.lab[i]=o;
                    hypo_temp.add( hy );
                }
            }

            hypo=hypo_temp;
            boolean chosen[]=new boolean[hypo.size()];

            for (int ell=0; ell<hmm.nstte; ell++)
            {
                int[] best_hypo=new int[nbh];
                double[] maxgam=new double[nbh];

                for( int bh=0; bh<nbh; bh++ )
                {
                    best_hypo[bh]=-1;
                    maxgam[bh]=Double.NEGATIVE_INFINITY;
                }


                for( int h=0; h<hypo.size(); h++ )
                {
                    Hypothese hy=( (Hypothese)hypo.elementAt( h ) );
                    double gam = Double.NEGATIVE_INFINITY; // = log(0)
                    if( ell>0 )
                    {
                        int lab = x.getNPObs( i-1 );

                        if( ( free || lab==-1 || lab==Model.plab[ell-0]  )&& Model.plab[ell-0] == hy.lab[i] )
                        {
                            for (int k=1; k<hmm.nstte; k++)
                            {
                                double prod = hy.gamma0[k] + hmm.getLoga( k, ell );
                                gam= logplus( gam, prod );
                            }
                            gam+=hmm.getLoge( ell, x, i-1 );
                        }
                    }

                    for( int bh=nbh-1; bh>=0; bh-- )
                    {
                        if (gam > maxgam[bh])
                        {
                            if( bh<nbh-1 )
                            {
                                maxgam[bh+1]=maxgam[bh];
                                best_hypo[bh+1]=best_hypo[bh];
                            }
                            maxgam[bh]=gam;
                            best_hypo[bh]=h;
                        }
                    }
                    hy.gamma[ell] = gam;

                }

                for( int bh=0; bh<nbh; bh++ )
                    if( best_hypo[bh] > -1 )
                        chosen[best_hypo[bh]]=true;
            }

            // System.out.print( "   From "+ hypo.size() + " hypotheses" );

            int hypos=hypo.size();
            int ind=0;

            for( int h=0; h<hypos; h++ )
                if( !chosen[h] )
                    hypo.removeElementAt( ind );
                else
                    ind++;

            // System.out.println( " I chose "+ hypo.size() + "." );

            //	for( int h=0; h<hypo.size(); h++ )
            //		System.out.println( h+" : \t"+labeling( ( Hypothese)hypo.elementAt( h ) ) );///

        }// END OF SEQUENCE

        i--;
        //System.out.println( "Finally I have "+hypo.size()+" hypotheses" );
        best_sum=Double.NEGATIVE_INFINITY;
        int best_hypo=0;

        for( int h=0; h<hypo.size(); h++ )
        {
            double sum = Double.NEGATIVE_INFINITY;
            for( int ell=0; ell<hmm.nstte; ell++)
            {
                sum=logplus( sum, ( (Hypothese)hypo.elementAt( h ) ).gamma[ell]+ ( Params.ALLOW_END? hmm.getLoga( ell, hmm.nstte-1 ) :0 )  );/////////////////////////////pbagos
            }
            //System.out.println( " Probability for hypothese "+h+" is "+sum );
            if( sum>best_sum )
            {
                best_sum=sum;
                best_hypo=h;
            }
        }
        path=labeling( (Hypothese)hypo.elementAt( best_hypo ) );
    }

    public String getPath()
    {
        return path;
    }

    String labeling( Hypothese hy )
    {
        StringBuffer lab=new StringBuffer();
        for( int ii=1; ii<=i; ii++ )
        {
            lab.append( Model.psym.charAt( hy.lab[ii] ) );
        }
        return lab.toString();
    }

	public double getProb()
	 {
		 return best_sum;
	 }

}
