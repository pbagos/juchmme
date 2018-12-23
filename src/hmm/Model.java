package hmm;

import java.io.*;
import java.util.*;

public class Model
{
    public static String MODEL="";

    public static String esym;
	public static String osym;
	public static String psym;
	public static String[] state;
	public static String[] ostate;
	public static String[] pstate;

	public static String transmLabels;
	public static String inLabels;
	public static String outLabels;

	public static double[] prior;
    public static double[] cprior;
	public static double[] prior1;
	public static double[] prior2;
	public static double[] prior3;

    static int[] slab, plab;
    static boolean[] bstate;
    static boolean[] estate;

    public static int nstate;
    public static int npsym;
    public static int nesym;
    public static int nosym;
    public static int esyminv=255;

    static Encode  enc;

    public static final void Init(String modelFile)
    {
        System.out.println("Preparing System Model ("+modelFile+")");
        File mdelFile = new File(modelFile);

        try {
            FileReader reader = new FileReader(mdelFile);
            Properties props = new Properties();
            props.load(reader);
            MODEL        = props.getProperty("MODEL");
            psym         = props.getProperty("PSYM");
            osym         = props.getProperty("OSYM");
            esym         = props.getProperty("ESYM");
            transmLabels = props.getProperty("transmLabels");
            inLabels     = props.getProperty("inLabels");
            outLabels    = props.getProperty("outLabels");
            state        = Utils.stringToList(props.getProperty("STATE"),"STATE"," ");
            ostate       = Utils.stringToList(props.getProperty("OSTATE"),"OSTATE"," ");
            pstate       = Utils.stringToList(props.getProperty("PSTATE"),"PSTATE"," ");

            if (Params.PAST_OBS_EXT){
                if (Params.ENCODE_TYPE>0)
                    enc = new Encode(Params.PAST_OBS_NO, Params.ENCODE_TYPE);
                else
                    enc = new Encode(Params.PAST_OBS_NO, esym, Params.GROUPING, Params.GROUP_SYMBOLS);

                prior  = enc.transformPrior(props.getProperty("PRIOR"),"PRIOR"," ");
                esym   = enc.getAlphabet();
                esyminv= enc.getUnicodeLength();
            }
            else{
                prior  = Utils.stringToDoubleList(props.getProperty("PRIOR"),"PRIOR"," ");
            }


            prior1 = Utils.stringToDoubleList(props.getProperty("PRIOR1"),"PRIOR1"," ");
            prior2 = Utils.stringToDoubleList(props.getProperty("PRIOR2"),"PRIOR2"," ");
            prior3 = Utils.stringToDoubleList(props.getProperty("PRIOR3"),"PRIOR3"," ");

            cprior = prior;

            reader.close();
        } catch (FileNotFoundException ex) {
            // file does not exist
            System.out.println("Model file "+modelFile+" not found.");
        } catch (IOException ex) {
            // I/O error
            System.out.println("ERROR with Model file "+modelFile);
        }

        nstate = state.length;
        nesym  = esym.length();
        npsym  = psym.length();
        nosym  = osym.length();

        slab = new int[nstate];
        plab = new int[nstate];

        for( int k=0; k<nstate; k++ )
        {
            slab[k]=osym.lastIndexOf( ostate[k].charAt( 0 ) );
            plab[k]=psym.lastIndexOf( pstate[k].charAt( 0 ) );
        }

        //Output
        System.out.println( "Using model "+MODEL );
        System.out.println( "Model has "+nstate+" states." );

    }

    public static double[] putPriorEM( int n, double[]mat, double[]mat0, int k )
    {
        double p1,p2,p3,p=0.0D;

        for(int i = 0; i < n; i++) {
            //Normalize prior values
            p1 = Model.prior1[Model.osym.indexOf(Model.ostate[k])];
            p2 = Model.prior2[Model.osym.indexOf(Model.ostate[k])];
            p3 = Model.prior3[Model.osym.indexOf(Model.ostate[k])];

            p = p1+p2+p3;

            if (p>0) {
                p1 /= p;
                p2 /= p;
                p3 /= p;
            }

            mat[i] =  p1 * mat[i] + p2 * mat0[i] + p3 * Model.prior[i];

        }

        // Scale to obtain a discrete probability distribution
        double sum = 0.0D;
        for(int i = 0; i < n; i++)
            sum += mat[i];

        for (int i=0; i<n; i++)
        {
            if (sum==0)
                mat[i]=0;
            else
                mat[i] /= sum;
        }

        return mat;
    }

}
