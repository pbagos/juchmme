package hmm;

import java.io.*;

public class Args {
    static String Vesrion = "1.0.1";

    static boolean RUN_TRAINING = false;
    static boolean RUN_CROSSVAL = false;
    static int CROSSVAL_CLUSTSIZE = 0;
    static int CROSSVAL_CLUSTERS = 0;
    static boolean RUN_JACKNIFE = false;
    static boolean RUN_SELFCONS = false;
    static boolean SHOW_PLOT = false;
    static java.util.ArrayList filesThree;
    static java.util.ArrayList filesFasta;
    static String file;
    static String parFile;
    static String mdelFile;                  //model
    static String fileEncode;                // Encoding for HNN
    static String fileA;                     // transitions probabilities
    static String fileE;                     // emission probabilities
    static String fileW;                     // HNN Weights probabilities
    static String fileD;                     // sequences for RpropNN
    static String graphPlotDir;

    /*
        Initialization
     */
    public static final void Init(String[] args) {
        file = "";
        parFile = "";
        mdelFile = "";
        fileA = "";
        fileE = "";
        fileW = "";
        fileD = "";
        fileEncode = "";
        filesThree = new java.util.ArrayList();
        filesFasta = new java.util.ArrayList();

        System.out.println("Preparing System Arguments");

        for (int i = 0; i < args.length; i += 1) {
            //System.out.println("arg "+i+" : "+args[i] +" - "+args[i+1]);
            if (args[i].length() != 2)
                break;

            if (args[i].charAt(0) != '-')
                break;

            char a = args[i].charAt(1);

            switch (a) {
                case 'V':

                    System.err.println("JUCHMME Version" + Vesrion);
                    System.exit(0);

                case 'a':
                    if (args.length > i + 1) {
                        fileA = args[i + 1];
                        i++;
                    } else
                        System.err.println("-" + a + ": Missing argument");
                    break;
                case 'e':
                    if (args.length > i + 1) {
                        fileE = args[i + 1];
                        i++;
                    } else
                        System.err.println("-" + a + ": Missing argument");
                    break;
                case 'w':
                    if (args.length > i + 1) {
                        fileW = args[i + 1];
                        i++;
                    } else
                        System.err.println("-" + a + ": Missing argument");
                    break;
                case 'i':
                    if (args.length > i + 1) {
                        filesThree.add(args[i + 1]);
                        Params.THREELINE = true;
                        Params.FASTA = false;
                        i++;
                    } else
                        System.err.println("-" + a + ": Missing argument");
                    break;
                case 'm':
                    if (args.length > i + 1) {
                        mdelFile = args[i + 1];
                        i++;
                    } else
                        System.err.println("-" + a + ": Missing argument");
                    break;
                case 'x':
                    if (args.length > i + 1) {
                        fileEncode = args[i + 1];
                        i++;
                    } else
                        System.err.println("-" + a + ": Missing argument");
                    break;
                case 'f':
                    if (args.length > i + 1) {
                        filesFasta.add(args[i + 1]);
                        Params.FASTA = true;
                        Params.THREELINE = false;
                        i++;
                    } else
                        System.err.println("-" + a + ": Missing argument");
                    break;
                case 'A':
                    if (args.length > i + 1) {
                        filesFasta.add(args[i + 1]);
                        Params.FASTA = true;
                        Params.THREELINE = false;
                        Params.MSA = true;
                        i++;
                    } else
                        System.err.println("-" + a + ": Missing argument");
                    break;
                case 'I':
                    if (args.length > i + 1) {
                        filesThree.add(args[i + 1]);
                        Params.FASTA = false;
                        Params.THREELINE = true;
                        Params.MSA = true;
                        i++;
                    } else
                        System.err.println("-" + a + ": Missing argument");
                    break;
                case 'd':
                    if (args.length > i + 1) {
                        fileD = args[i + 1];
                        i++;
                    } else
                        System.err.println("-" + a + ": Missing argument");
                    break;
                case 't':
                    if (args.length > i + 1) {
                        file = args[i + 1];
                        i++;
                        RUN_TRAINING = true;
                        Params.THREELINE = true;
                        Params.FASTA = false;
                    } else
                        System.err.println("-" + a + ": Missing argument");
                    break;
                case 'c':
                    if (args.length > i + 1) {
                        parFile = args[i + 1];
                        i++;
                    } else
                        System.err.println("-" + a + ": Missing argument");
                    break;
                case 'v':
                    if (args.length > i + 1) {
                        RUN_CROSSVAL = true;
                        CROSSVAL_CLUSTSIZE = Integer.parseInt(args[i + 1]);
                        i++;
                    } else
                        System.err.println("-" + a + ": Missing argument");
                    break;
                case 'k':
                    if (args.length > i + 1) {
                        RUN_CROSSVAL = true;
                        CROSSVAL_CLUSTERS = Integer.parseInt(args[i + 1]);
                        i++;
                    } else
                        System.err.println("-" + a + ": Missing argument");
                    break;
                case 's':
                    RUN_SELFCONS = true;
                    break;
                case 'j':
                    RUN_JACKNIFE = true;
                    CROSSVAL_CLUSTSIZE = 1;
                    break;
                case 'p':
                    SHOW_PLOT = true;
                    break;
                case 'P':
                    if (args.length > i + 1) {
                        graphPlotDir = (args[i + 1]);
                        i++;

                        File dd = new File(graphPlotDir);
                        if (!dd.isDirectory()) {
                            System.err.println("Directory " + graphPlotDir + " is not accessible");
                            System.exit(-1);
                        }
                    } else
                        System.err.println("-" + a + ": Missing argument");
                    break;
                default:
                    System.out.println("Error with option -" + a);
            }


        }

        if (fileA.equals("") || parFile.equals("") || mdelFile.equals("") || (CROSSVAL_CLUSTSIZE > 1 && RUN_JACKNIFE)) {
            System.err.println("Options:");
            System.err.println("\t-a <file>: transitions");
            System.err.println("\t-e <file>: emissions");
            System.err.println("\t-w <file>: HNN Weights");
            System.err.println("\t-d <file>: sequences to train HNN");
            System.err.println("\t-c <file>: configuration");
            System.err.println("\t-m <file>: model");
            System.err.println("\t-x <file>: HNN Encode");
            System.err.println("\t-i <file>: 3-line sequences to test");
            System.err.println("\t-f <file>: fasta sequences to test");
            System.err.println("\t-A <file>: Aligment fasta sequences to test");
            System.err.println("\t-t <file>: run training with this training set");
            System.err.println("\t-s : run self-consistency");
            System.err.println("\t-j : run jacknife");
            System.err.println("\t-v <num> : run cross validation with cluster size=<num>");
            System.err.println("\t-p : show text plot");
            System.err.println("\t-P <dir> : save graphical plots in directory");
            System.err.println();
            System.exit(-1);
        }
    }
}