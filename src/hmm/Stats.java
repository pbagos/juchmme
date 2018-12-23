package hmm;
import java.text.*;
import java.util.*;

class Stats
{
	double Q2;
  	double Qa;
  	double Qna;
  	double Pa;
  	double Pna;
  	double Ca;
  	double SM;
  	double sov;
  	int TP;
  	int FP;
  	int FN;
  	int correctTop, correctOri;
  	int barrelSize;

  	String mem;
  	String instr;
  	String outstr;

  	double Qfas;

  	private static DecimalFormat fmt = new DecimalFormat("0.000", new DecimalFormatSymbols( Locale.US  ) );

  	void Print()
  	{
		System.out.println("Q2:"+ fmt.format( Q2 )
			+"\tQa:"+ fmt.format( Qa )
			+"\tQna:"+fmt.format( Qna )
			+"\tPa:"+ fmt.format( Pa )
			+"\tPna:"+fmt.format( Pna )
			+"\tQfas:"+fmt.format( Qfas )
			+"\nCa:"+ fmt.format( Ca )
			+"\tSM:"+ fmt.format( SM )
			+"\tTP:"+ ( TP )
			+"\tFP:"+ ( FP )
			+"\tFN:"+ ( FN )
			+"\tCorrect Top:"+ ( correctTop )
			+"\tCorrect Ori:"+ ( correctOri ) 
					+(( instr==null || outstr==null )? " (in/out not defined)": "")
			+"\tAvg SOV:"+ fmt.format( sov )
			+"\tbarrel size (all-1) :"+( barrelSize )
			+"\n" );

  	}

  	void PrintCols()
 	 {
		 System.out.print( fmt.format( Q2 )
				 +"\t"+ fmt.format( Ca )
				 +"\t"+ ( correctTop )
				 +"\t"+ fmt.format( sov )
				 +"\t"+( barrelSize )   );
  	}

  	public Stats(){

	}
	 /*
	 	final boolean obsP, final boolean predP
	 	obsP: an to observed einai P-path
	 	predP: an to predicted einai P-path
	 */

	 public Stats( String[] obsSeqs, String[] predSeqs, String _mem, String _instr, String _outstr )
	 {
		 Run( obsSeqs, predSeqs, _mem, _instr, _outstr );
	 }


	 public void Run( String[] obsSeqs, String[] predSeqs, String _mem, String _instr, String _outstr )
	 {
		 mem=_mem;
		 instr=_instr;
		 outstr=_outstr;

		 double goodPred    = 0;  // w
		 double goodNonPred = 0;  // x
		 double missedPred  = 0;  // y
		 double wrongPred   = 0;  // z

		 correctTop   = 0;
		 correctOri   = 0;
		 barrelSize   = 0;

		 int inPredTM = 0;
		 int inObsTM  = 0;

     	 /*
	  	 to compute the correlation factor M & C from the paper :
      	 "Prediction of transmembrane a-helices in prokaryotic membrane proteins:
       	 the dense alignment surface method"
	 	 */

     	 int nbObsTM  = 0;  // number of experimental TM segments (TP+FN)
     	 int nbExpM   = 0;  // number of experimental TM segment that overlap with prediction (...)
     	 int nbPredTM = 0;  // number of predicted TM segments (TP+FP)
     	 int nbPredM  = 0;  // number of predicted TM segment that overlap with experimental (TP)
	 	 // TP = nbPredM;
	 	 // FP = nbPredTM - nbPredM;
	 	 // FN = nbObsTM - nbPredM;

		 int expMatch = 0;
         int predMatch = 0;
		 int total = 0;

		 sov=0.0D;
        
		 for( int s=0; s<obsSeqs.length; s++ )
		 {
			 String obs_ ="";
			 String pred_="";

	     	 int nbAA=obsSeqs[s].length();
	     	 if( nbAA != predSeqs[s].length() )
				 System.out.println( "Sequence "+(s+1)+": obs "+nbAA+", pred "+predSeqs[s].length()+"\n"
				     +"PRED: "+predSeqs[s] ); //was System.err

	     	 for (int i = 0 ; i < nbAA ; i++ )
	     	 {
				 char rTM=predSeqs[s].charAt( i );
		   		 char rTMInSwiss=obsSeqs[s].charAt( i );

				 obs_+=rTMInSwiss;
				 pred_+=rTM;

				 int TM=( mem.lastIndexOf( rTM )>-1 )? 1:0;
				 int TMInSwiss=( mem.lastIndexOf( rTMInSwiss )>-1 )? 1:0;

				 if ((TM == 1) && (TMInSwiss == 1)) goodPred++;
				 if ((TM == 0) && (TMInSwiss == 0)) goodNonPred++;
		  		 if ((TM == 0) && (TMInSwiss == 1)) missedPred++;
	   	  		 if ((TM == 1) && (TMInSwiss == 0)) wrongPred++;

				 if ((inObsTM == 0) && (TMInSwiss == 1) )
		   		 {
					 nbObsTM++;
					 inObsTM = 1;
					 expMatch = 0;
				 }

    		  	 if ((inPredTM == 0) && (TM == 1) )
				 {
					 nbPredTM++;
      	    	 	 inPredTM = 1;
					 predMatch = 0;
        	  	 }

        	  	 if ((inObsTM == 1) && (expMatch == 0) && (TM == 1))
	     	  	 {
					 expMatch = 1;
					 nbExpM++;
        	  	 }

        	  	 if ((inPredTM == 1) && (predMatch == 0) && (TMInSwiss == 1))
    		  	 {
					 predMatch = 1;
					 nbPredM++;
         	  	 }

        	  	 if ((inObsTM == 1) && (TMInSwiss == 0) ) inObsTM = 0;
        	  	 if ((inPredTM == 1) && (TM == 0) ) inPredTM = 0;

          	  	 total++;
        	 }
    	     
			 int nCorrTop = Seq.CorrectTop( obs_, pred_, mem );
			 if( nCorrTop==0 )
			 {
				 correctTop++;
			  	 if( instr != null && outstr != null )
				 if( Seq.NTermIn( obs_, instr, outstr ) == Seq.NTermIn( pred_, instr, outstr ) )
					 correctOri++;
 							
			 }

			 if( nCorrTop==0 || nCorrTop==1 )
			 {
				 barrelSize++;
			 }
    	
    		 Sov sv = new Sov( obs_, pred_, mem );
    		 sov += sv.GetScore();

	 	 }

	 	 if (nbObsTM == 0) return;  //false
	
	 	 sov/=obsSeqs.length;

	 	 Qa = goodPred / (goodPred + missedPred);
	 	 Qna = goodNonPred / (goodNonPred + wrongPred);
	 	 Pa = goodPred / (goodPred + wrongPred);
	 	 Pna = goodNonPred / (goodNonPred + missedPred);
	 
	 	 Qfas = (Qa + Qna)/2; //Chou-Fasman
	 
	 	 Q2 =( goodPred+goodNonPred ) / ( goodPred+goodNonPred+missedPred+wrongPred );
	
		 double S = (goodPred+missedPred)/total;
	 	 double P = (goodPred+wrongPred)/total;
	 	 if ((P==0) || (S==0)) Ca = 0;
	 	 else Ca = ((goodPred / total) - (P * S)) / Math.sqrt(P * S * (1 - P) * ( 1 - S ));
	
	
	 	 TP = nbPredM;
	 	 FP = nbPredTM - nbPredM;
	 	 FN = nbObsTM - nbPredM;
	 	 double M = (nbObsTM == 0) ? 0 : (double)nbExpM/nbObsTM;
	 	 double C = (nbPredTM == 0) ? 0 : (double)nbPredM/nbPredTM;
	 	 SM = Math.sqrt(M*C);

	 	 return;
     }


     public void calcStats( SeqSet testSet )
	 {
		 String[] pp = new String[testSet.nseqs];

		 if( Params.VITERBI>-1 )
		 {
			 for( int j=0; j<testSet.nseqs; j++ )
			 	pp[j]=testSet.seq[j].path[Params.VITERBI];

			 Run( testSet.getOrigPath(), pp, Model.transmLabels, Model.inLabels, Model.outLabels );
			 System.out.println( "VITERBI:\n" );
			 Print();
		 }

		 if( Params.POSVIT>-1 )
		 {
			 for( int j=0; j<testSet.nseqs; j++ )
				 pp[j]=testSet.seq[j].path[Params.POSVIT];

			 Run( testSet.getOrigPath(), pp, Model.transmLabels, Model.inLabels, Model.outLabels );
			 System.out.println( "POSVIT:\n" );
			 Print();
		 }

		 if( Params.PLP>-1 )
		 {
			 for( int j=0; j<testSet.nseqs; j++ )
			 	pp[j]=testSet.seq[j].path[Params.PLP];

			Run( testSet.getOrigPath(), pp, Model.transmLabels, Model.inLabels, Model.outLabels );
			System.out.println( "PLP:\n" );
			Print();
		}

		if( Params.NBEST>-1 )
		{
			for( int j=0; j<testSet.nseqs; j++ )
				pp[j]=testSet.seq[j].path[Params.NBEST];

			Run( testSet.getOrigPath(), pp, Model.transmLabels, Model.inLabels, Model.outLabels );
			System.out.println( "NBEST:\n" );
			Print();
		}

		if( Params.DYNAMIC>-1 )
		{
			for( int j=0; j<testSet.nseqs; j++ )
				pp[j]=testSet.seq[j].path[Params.DYNAMIC];

			Run( testSet.getOrigPath(), pp, Model.transmLabels, Model.inLabels, Model.outLabels );
			System.out.println( "DYNAMIC:\n" );
			Print();
		}

	}
}