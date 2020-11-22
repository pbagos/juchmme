package hmm;

import java.io.*;
import java.util.*;

public class Params {
    public static final java.text.DecimalFormat fmt =
            new java.text.DecimalFormat("0.000", new java.text.DecimalFormatSymbols(Locale.US));

    static boolean FASTA;             // Fasta File
    static boolean THREELINE;         // Three Line File

    //    Configuration
    static int MINHLEN = 7;
    static int MINLLEN = 1;
    static int MAXHLEN = 17;
    static int MAXNSTRAND = 22;
    static double MINSSC = 3;
    static int STRDIV = 9;

    //PROBABILITIES
    //FILE,RANDOM,UNIFORM,VITERBI
    static String TRANSITIONS = "FILE";
    static String EMISSIONS = "FILE";
    //FILE, RANDOM_NORMAL, RANDOM_UNIFORM, RPROP, BOOT
    static String WEIGHTS = "FILE";

    //Semi-supervised Learning
    static boolean enabledSSL = true;
    // SSL (standard Semi-supervised Method) or GEM (Generalized EM)
    static String methodSSL = "SSL";
    //#1: Use all, 2: Use weight (Constant) for each sequence, 3: Use weight (Reliability) for each sequence, 4: Use a few most confident
    static int addMethodSSL = 2;
    //1:VITERBI, 2:NBEST, 3:POSVIT, 4:PLP
    static int usingMethodSSL = 1;
    static double thresholdSSL = 0.02;
    static int maxIterSSL = 200;
    static double relscoreSSL = 0.90;
    static double weightSSL = 0.20;

    //ESTIMATION
    static int FLANK = 5;
    static boolean REFINE = false;
    static boolean ML_INIT = false;
    static boolean ALLOW_END = true;
    static boolean ALLOW_BEGIN = true;  //conf R:starting stage

    // DECODING OPTIONS
    static boolean VITERBI_p = true;
    static boolean NBEST_p = false;
    static boolean DYNAMIC_p = false;
    static boolean POSVIT_p = true;
    static boolean PLP_p = true;
    static boolean CONSTRAINT = false;
    static int VITERBI = 1;
    static int DYNAMIC = -2;
    static int POSVIT = 3;
    static int PLP = 4;
    static int NBEST = -5;
    static boolean MSA = false;             // MSA
    static String MSAgapSymbol = "*";

    //TRAINING OPTIONS
    static boolean RUN_CML = false;
    static boolean RUN_GRADIENT = false;
    static boolean HNN = false;
    static boolean RUN_ViterbiTraining;

    //EARLY STOPPING
    static boolean EARLY = false;        //  false
    static double CUSTOM_STOP = 0.0D; //
    static int NTRAIN = 15;          // Number of Sequences in the training (the other sequences used for validation)
    static int NROUND = 5;           // iterations
    static int ITER = 2;             // meta apo posa iterations arxizei na metraei to EARLY

    // GRADIENT DESCENT OPTIONS
    // R: ektimisi parametrvn hmm
    static boolean JACOBI = false;
    static boolean RPROP = true;
    static boolean SILVA = true;
    static double momentum = 0.0D;
    static double kappaA = 0.01D;      //the Learning rate of A 0.01D;
    static double kappaAmin = 1E-20D;
    static double kappaAmax = 1.0D;
    static double kappaE = 0.01D;      //the Learning rate of E
    static double kappaEmin = 1E-20D;
    static double kappaEmax = 1.0D;
    static double NPLUS = 1.2D;       //rprop, silva
    static double NMINUS = 0.5D;       //rprop, silva

    static double ni = 50.0D;              //Linesearch

    //PRIOR OPTIONS
    static boolean NOISE_TR = true;
    public static boolean NOISE_EM = true;
    static double PRIOR_TRANS = 0.001D;
    static double DECAY = 0.001D;


    static double threshold = 0.02;
    static int maxIter = 200;

    // HNN OPTIONS
    public static int window = 0;
    public static int windowLeft=3;
    public static int windowRight=3;
    static int nhidden = 3;
    static double ADD_GRAD = 0.0D;
    static int hiddenLayerFunction = 1; //Sigmoid
    static int inputLayerLen = 0;
    static int NNclassLabels = 0;

    //BOOTSTRAP OPTIONS (HNN ONLY)
    static int BOOT = 0;
    static double STDEV = 1.5D;
    static double RANGE = 5.0D;
    static long SEED = 568381;
    static double WEIGHT_RAND = 0.0D;    //add noise to weights initialization
    static boolean WEIGHT_TIME = false;

    //RPROPNN OPTIONS (HNN ONLY)
    static int nOfCycles = 50;
    static boolean crossVal = true;
    static int crossValIter = 5;
    static double minGEdiff = 0; // the min error difference between 2 adjacent iterations so as to continue training
    public static String globalError = "CE";
    public static double initialDelta = 0.1;
    public static double maxDelta = 50.0;
    public static double minDelta = 1e-6;
    public static double etaInc = 1.2;
    public static double etaDec = 0.5;

    //Multithreaded parallelization for multicores
    static boolean parallel = true;
    static boolean defCPU = true;
    static int nOfCPU = 2;
    static int processors;

    //Past Observation Extension
    static boolean PAST_OBS_EXT = false;
    static int ENCODE_TYPE = 1;
    static String GROUP_SYMBOLS = "10";
    static String GROUPING = "10001011011000000111";
    static int PAST_OBS_NO = 1;

    //static final String PN_AA = "ACDEFGHIKLMNPQRSTVWY";

    public static boolean isOk(int n) {
        //return( n==8 || n==10 || n==12 || n==16 || n==18 || n==22 );
        //return true;
        return (n % 2 == 0);
    }

    /*
     *******************************************************
        Initialization - Parse Configuration File
     *******************************************************
     */
    public static final void Init(String file) {
        System.out.println("Preparing System Configuration (" + file + ")");
        File configFile = new File(file);

        try {
            FileReader reader = new FileReader(configFile);
            Properties props = new Properties();
            props.load(reader);

            //TRAINING OPTIONS
            RUN_CML = Utils.parseBoolean(props.getProperty("RUN_CML"), "RUN_CML");
            RUN_GRADIENT = Utils.parseBoolean(props.getProperty("RUN_GRADIENT"), "RUN_GRADIENT");
            HNN = Utils.parseBoolean(props.getProperty("HNN"), "HNN");
            ALLOW_END = Utils.parseBoolean(props.getProperty("ALLOW_END"), "ALLOW_END");
            ALLOW_BEGIN = Utils.parseBoolean(props.getProperty("ALLOW_BEGIN"), "ALLOW_BEGIN");
            RUN_ViterbiTraining = Utils.parseBoolean(props.getProperty("RUN_ViterbiTraining"), "RUN_ViterbiTraining");

            //PROBABILITIES
            TRANSITIONS = props.getProperty("TRANSITIONS");
            EMISSIONS = props.getProperty("EMISSIONS");
            WEIGHTS = props.getProperty("WEIGHTS");

            //Multithreaded parallelization for multicores
            parallel = Utils.parseBoolean(props.getProperty("PARALLEL"), "PARALLEL");
            defCPU = Utils.parseBoolean(props.getProperty("defCPU"), "defCPU");
            nOfCPU = Utils.parseInteger(props.getProperty("nCPU"), "nCPU");
            processors = (defCPU) ? getSystemProcessors() : nOfCPU;

            //TESTING OPTIONS
            CONSTRAINT = Utils.parseBoolean(props.getProperty("CONSTRAINT"), "CONSTRAINT");

            //Semi-supervised Learning
            enabledSSL = Utils.parseBoolean(props.getProperty("SSL_ENABLED"), "SSL_ENABLED");
            methodSSL = props.getProperty("SSL_METHOD");
            addMethodSSL = Utils.parseInteger(props.getProperty("SSL_ADD_METHOD"), "SSL_ADD_METHOD");
            usingMethodSSL = Utils.parseInteger(props.getProperty("SSL_USING_METHOD"), "SSL_USING_METHOD");
            thresholdSSL = Utils.parseDouble(props.getProperty("SSL_THRESHOLD"), "SSL_THRESHOLD");
            maxIterSSL = Utils.parseInteger(props.getProperty("SSL_maxIter"), "SSL_maxIter");
            relscoreSSL = Utils.parseDouble(props.getProperty("SSL_relscore"), "SSL_relscore");
            weightSSL = Utils.parseDouble(props.getProperty("SSL_WEIGHT"), "SSL_WEIGHT");

            //DYNAMIC - Maximum number of predicted helices
            MINHLEN = Utils.parseInteger(props.getProperty("MINHLEN"), "MINHLEN");
            MINLLEN = Utils.parseInteger(props.getProperty("MINLLEN"), "MINLLEN");
            MAXHLEN = Utils.parseInteger(props.getProperty("MAXHLEN"), "MAXHLEN");
            MAXNSTRAND = Utils.parseInteger(props.getProperty("MAXNSTRAND"), "MAXNSTRAND");
            MINSSC = Utils.parseInteger(props.getProperty("MINSSC"), "MINSSC");
            STRDIV = Utils.parseInteger(props.getProperty("STRDIV"), "STRDIV");

            //EXTENDED PAST OBSERVATIONS
            PAST_OBS_EXT = Utils.parseBoolean(props.getProperty("PAST_OBS_EXTENSION"), "PAST_OBS_EXTENSION");
            ENCODE_TYPE = Utils.parseInteger(props.getProperty("ENCODE_TYPE"), "ENCODE_TYPE");
            GROUP_SYMBOLS = props.getProperty("GROUP_SYMBOLS");
            GROUPING = props.getProperty("GROUPING");
            PAST_OBS_NO = Utils.parseInteger(props.getProperty("PAST_OBS_NO"), "PAST_OBS_NO");

            //Estimation Options
            FLANK = Utils.parseInteger(props.getProperty("FLANK"), "FLANK");
            REFINE = Utils.parseBoolean(props.getProperty("REFINE"), "REFINE");
            ML_INIT = Utils.parseBoolean(props.getProperty("ML_INIT"), "ML_INIT");

            // DECODING OPTIONS
            VITERBI_p = Utils.parseBoolean(props.getProperty("VITERBI"), "VITERBI");
            VITERBI = (VITERBI_p) ? 1 : -1;
            NBEST_p = Utils.parseBoolean(props.getProperty("NBEST"), "NBEST");
            NBEST = (NBEST_p) ? 5 : -5;
            DYNAMIC_p = Utils.parseBoolean(props.getProperty("DYNAMIC"), "DYNAMIC");
            DYNAMIC = (DYNAMIC_p) ? 2 : -2;
            POSVIT_p = Utils.parseBoolean(props.getProperty("POSVIT"), "POSVIT");
            POSVIT = (POSVIT_p) ? 3 : -3;
            PLP_p = Utils.parseBoolean(props.getProperty("PLP"), "PLP");
            PLP = (PLP_p) ? 4 : -4;


            //EARLY STOPPING
            EARLY = Utils.parseBoolean(props.getProperty("EARLY"), "EARLY");
            CUSTOM_STOP = Utils.parseDouble(props.getProperty("CUSTOM_STOP"), "CUSTOM_STOP");
            NTRAIN = Utils.parseInteger(props.getProperty("NTRAIN"), "NTRAIN");
            NROUND = Utils.parseInteger(props.getProperty("NROUND"), "NROUND");
            ITER = Utils.parseInteger(props.getProperty("ITER"), "ITER");

            //BOOTSTRAP OPTIONS (HNN ONLY)
            BOOT = Utils.parseInteger(props.getProperty("BOOT"), "BOOT");
            STDEV = Utils.parseDouble(props.getProperty("STDEV"), "STDEV");
            RANGE = Utils.parseDouble(props.getProperty("RANGE"), "RANGE");
            SEED = Utils.parseInteger(props.getProperty("SEED"), "SEED");
            WEIGHT_RAND = Utils.parseDouble(props.getProperty("WEIGHT_RAND"), "WEIGHT_RAND");
            WEIGHT_TIME = Utils.parseBoolean(props.getProperty("WEIGHT_TIME"), "WEIGHT_TIME");

            //GRADIENT DESCENT OPTIONS R: ektimisi parametrvn hmm
            JACOBI = Utils.parseBoolean(props.getProperty("JACOBI"), "JACOBI");
            RPROP = Utils.parseBoolean(props.getProperty("RPROP"), "RPROP");
            SILVA = Utils.parseBoolean(props.getProperty("SILVA"), "SILVA");
            momentum = Utils.parseDouble(props.getProperty("momentum"), "momentum");
            kappaA = Utils.parseDouble(props.getProperty("kappaA"), "kappaA");
            kappaAmin = Utils.parseDouble(props.getProperty("kappaAmin"), "kappaAmin");
            kappaAmax = Utils.parseDouble(props.getProperty("kappaAmax"), "kappaAmax");
            kappaE = Utils.parseDouble(props.getProperty("kappaE"), "kappaE");
            kappaEmin = Utils.parseDouble(props.getProperty("kappaEmin"), "kappaEmin");
            kappaEmax = Utils.parseDouble(props.getProperty("kappaEmax"), "kappaEmax");
            NPLUS = Utils.parseDouble(props.getProperty("NPLUS"), "NPLUS");
            NMINUS = Utils.parseDouble(props.getProperty("NMINUS"), "NMINUS");

            ni = Utils.parseDouble(props.getProperty("ni"), "ni");
            ADD_GRAD = Utils.parseDouble(props.getProperty("ADD_GRAD"), "ADD_GRAD");

            //PRIOR OPTIONS
            NOISE_TR = Utils.parseBoolean(props.getProperty("NOISE_TR"), "NOISE_TR");
            NOISE_EM = Utils.parseBoolean(props.getProperty("NOISE_EM"), "NOISE_EM");
            PRIOR_TRANS = Utils.parseDouble(props.getProperty("PRIOR_TRANS"), "PRIOR_TRANS");
            DECAY = Utils.parseDouble(props.getProperty("DECAY"), "DECAY");
            threshold = Utils.parseDouble(props.getProperty("threshold"), "threshold");
            maxIter = Utils.parseInteger(props.getProperty("maxIter"), "maxIter");

            //HNN OPTIONS
            //window = Utils.parseInteger(props.getProperty("window"), "window");
            windowLeft = Utils.parseInteger(props.getProperty("windowLeft"), "windowLeft");
            windowRight = Utils.parseInteger(props.getProperty("windowRight"), "windowRight");
            nhidden = Utils.parseInteger(props.getProperty("nhidden"), "nhidden");
            hiddenLayerFunction = Utils.parseInteger(props.getProperty("hiddenLayerFunction"), "hiddenLayerFunction");

            //RPROPNN OPTIONS  (HNN ONLY)
            nOfCycles = Utils.parseInteger(props.getProperty("numberOfCycles"), "numberOfCycles");
            crossVal = Utils.parseBoolean(props.getProperty("doCrossVal"), "doCrossVal");
            crossValIter = Utils.parseInteger(props.getProperty("crossValIter"), "crossValIter");
            minGEdiff = Utils.parseDouble(props.getProperty("minGEdiff"), "minGEdiff");
            globalError = props.getProperty("globalError");
            initialDelta = Utils.parseDouble(props.getProperty("initialDelta"), "initialDelta");;
            maxDelta = Utils.parseDouble(props.getProperty("maxDelta"), "maxDelta");;
            minDelta = Utils.parseDouble(props.getProperty("minDelta"), "minDelta");;
            etaInc = Utils.parseDouble(props.getProperty("etaInc"), "etaInc");;
            etaDec = Utils.parseDouble(props.getProperty("etaDec"), "etaDec");;

            reader.close();
        } catch (FileNotFoundException ex) {
            // file does not exist
            System.out.println("Configuration file " + file + " not found.");
        } catch (IOException ex) {
            // I/O error
            System.out.println("ERROR with Configuration file " + file);
        }

        if (HNN){
            window = windowLeft + 1 + windowRight;
        }

        //output
        System.out.println("Multithreaded parallelization = " + parallel);
        if (parallel)
            System.out.println("Processors : " + processors);


        if (Args.RUN_TRAINING) {
            System.out.print("TRAINING OPTIONS ");

            if (HNN) {
                System.out.print("HNN \n");
                System.out.println("Window = " + window + "\tnhidden = " + nhidden + "\thiddenLayerFunction = " + hiddenLayerFunction);

                if (RPROP)
                    System.out.println("RPROP = " + RPROP + "\tminGEdiff = " + minGEdiff + "\tcrossVal = " + crossVal);

            } else if (RUN_CML)
                System.out.print("CML \n");
            else {
                System.out.print("ML \n");
                System.out.println("RUN_GRADIENT = " + RUN_GRADIENT);
            }

            System.out.println("Ka = " + kappaA + "\tmin = " + kappaAmin + "\tmax = " + kappaAmax);
            System.out.println("Ke = " + kappaE + "\tmin = " + kappaEmin + "\tmax = " + kappaEmax);
            System.out.println("momentum = " + momentum);
            System.out.print("RPROP = " + RPROP + "\tSILVA =" + SILVA + "\t");
            System.out.println("Weight Decay = " + DECAY);

            if (NOISE_TR)
                System.out.println("PRIOR_TRANS= " + PRIOR_TRANS);
            else
                System.out.println("NO PRIORS FOR TRANSITIONS");

            System.out.println("REFINE = " + REFINE + "\tFLANK=" + FLANK);
            System.out.println("CONSTRAINT = " + CONSTRAINT);

        }
    }

    /**
     * Method to get current active processors
     *
     * @return --number of system's processors
     */
    public static int getSystemProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }

}
