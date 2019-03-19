package hmm;

import java.util.*;
import java.io.*;

import nn.FileEntries;
import nn.NNDesiredOutput;
import nn.NNinput;
import nn.SingleLayerNN_RPROP;

import org.joone.helpers.factory.JooneTools;
import org.joone.engine.FullSynapse;

class Weights {
    private double[][][] wts12;
    private double[][][] wts23;

    private double[][][] dw12;
    private double[][][] dw23;

    double[][][] delta12;
    double[][][] delta23;

    double[][][] deriv12;
    double[][][] deriv23;

    Weights() {
        wts12 = new double[Params.NNclassLabels][Params.nhidden][Params.window * NNEncode.encode[0].length + 1];
        wts23 = new double[Params.NNclassLabels][1][Params.nhidden + 1];

        deriv12 = new double[Params.NNclassLabels][Params.nhidden][Params.window * NNEncode.encode[0].length + 1];
        deriv23 = new double[Params.NNclassLabels][1][Params.nhidden + 1];

        if (!Params.RPROP && !Params.SILVA) {
            dw12 = new double[Params.NNclassLabels][Params.nhidden][Params.window * NNEncode.encode[0].length + 1];
            dw23 = new double[Params.NNclassLabels][1][Params.nhidden + 1];
        } else {
            delta12 = new double[Params.NNclassLabels][Params.nhidden][Params.window * NNEncode.encode[0].length + 1];
            delta23 = new double[Params.NNclassLabels][1][Params.nhidden + 1];

            for (int o = 0; o < Params.NNclassLabels; o++) {
                for (int m = 0; m < Params.window * NNEncode.encode[0].length + 1; m++)
                    for (int n = 0; n < Params.nhidden; n++) {
                        delta12[o][n][m] = Params.kappaE;
                    }

                for (int m = 0; m < Params.nhidden + 1; m++) {
                    delta23[o][0][m] = Params.kappaE;
                }
            }
        }
    }

    public void InitializeByRpropNN(FileEntries fileData) {
        int windowSize = Params.window;
        int hiddenNeuronsNum = Params.nhidden;
        int numberOfCycles = Params.nOfCycles;
        boolean doCrossValidation = Params.crossVal;
        int crossValidationIterations = Params.crossValIter;
        double minimumGEdifference = Params.minGEdiff;
        char[] labelAlphabet = new char[Params.NNclassLabels];

        for (int i = 0; i < Params.NNclassLabels; i++) {
            labelAlphabet[i] = Model.osym.charAt(i);
        }

        double[][][] wtsIH = new double[labelAlphabet.length][][]; // IH weights for HMM (wts12)
        double[][][] wtsHO = new double[labelAlphabet.length][][]; // HO weights for HMM (wts23)

        NNinput input = new NNinput(windowSize, fileData.proteinEntriesList);

        for (int labelIndex = 0; labelIndex < labelAlphabet.length; labelIndex++) {
            char labelForTraining = labelAlphabet[labelIndex];
            double targetGlobalError = 0;
            double finalGE;

            System.out.println("\n########## Label " + labelForTraining + " ###########");
            //Create the desired output for the NN based on the label for which the NN should be trained
            NNDesiredOutput DesiredOutputForLabel = new NNDesiredOutput(labelForTraining, fileData.proteinEntriesList);
            if (doCrossValidation == true) {
                double sumGlobalError = 0;
                for (int i = 0; i < crossValidationIterations; i++) {
                    //Initialize the NN and train it
                    System.out.println("Cross Validation cluster " + (i + 1));
                    SingleLayerNN_RPROP nnToTrain = new SingleLayerNN_RPROP(input.inputArray, DesiredOutputForLabel.desiredOutputArray, hiddenNeuronsNum, numberOfCycles, doCrossValidation, targetGlobalError, minimumGEdifference);
                    nnToTrain.train();
                    nnToTrain.nnet.join(); //wait for threads to finish - do not remove
                    double trainingGlobalError = nnToTrain.getGlobalErrorForTrainingData();
                    System.out.println("Training Global Error = " + trainingGlobalError);
                    double validationGlobalError = nnToTrain.getGlobalErrorForValidationData();
                    System.out.println("Validation Global Error = " + validationGlobalError + "\n");
                    sumGlobalError += validationGlobalError;
                }
                targetGlobalError = sumGlobalError / crossValidationIterations; //take the mean global error for the cross validation phase
                System.out.println("Target Global Error = " + targetGlobalError + "\n");
            }

            // Create neural network and begin training phase
            System.out.println("Starting Neural Network training for label: " + labelForTraining + " ...");
            SingleLayerNN_RPROP nnToTrain = new SingleLayerNN_RPROP(input.inputArray, DesiredOutputForLabel.desiredOutputArray, hiddenNeuronsNum, numberOfCycles, false, targetGlobalError, minimumGEdifference);
            nnToTrain.train();
            nnToTrain.nnet.join(); //wait for threads to finish - do not remove
            finalGE = nnToTrain.nnet.getMonitor().getGlobalError();
            System.out.println("Final Global Error = " + finalGE);

            //Store the weights in the array that will be given to the HMM
            wtsIH[labelIndex] = nnToTrain.getIHWeightsForHMM();
            wtsHO[labelIndex] = nnToTrain.getHOWeightsForHMM();

            wts12 = wtsIH;
            wts23 = wtsHO;
        }

    }

        /*private void InitializeByBootstrap(SeqSet seqs){
        double loglikelihood  = Double.NEGATIVE_INFINITY;
        double loglikelihoodC = Double.NEGATIVE_INFINITY;
        double loglikelihoodF = Double.NEGATIVE_INFINITY;
        double bestl          = Double.NEGATIVE_INFINITY;
        Probs tab0;
        HMM   hmm0;
        Weights bestw = new Weights();
        tab0  =new Probs( Args.fileA, tab0.weights );

        System.out.println( "*** Bootstrapping ***" );
        for( int i=1; i<= Params.BOOT; i++ )
        {
            if( Params.WEIGHTS.equals("RANDOM_NORMAL" ))
                tab.weights.RandomizeNormal( Params.STDEV, 0 );
            else if ( Params.WEIGHTS.equals("RANDOM_UNIFORM" ))
                tab.weights.RandomizeUniform( Params.RANGE, 0 );

            hmm0 = new HMM( tab0 );

            CalcActs(seqs);

            loglikelihoodC = fwdbwd( fwdsC, bwdsC, logPC, false, seqs );

            if( Params.RUN_CML )
            {
                loglikelihoodF = fwdbwd( fwdsF, bwdsF, logPF, true, seqs );
                loglikelihood = loglikelihoodC - loglikelihoodF;
                System.out.println( "\tC="+loglikelihoodC+", F="+loglikelihoodF );//////////
            } else {
                loglikelihood = loglikelihoodC;
            }

            System.out.println( i + "/" +Params.BOOT+"\tlog likelihood = " + loglikelihood);

            if( loglikelihood > bestl )
            {
                bestl = loglikelihood;
                bestw = tab.weights.GetClone();
            }

        }

        if ( bestl > Double.NEGATIVE_INFINITY)
        {
            tab0.weights = bestw;
            System.out.println( "*** Chosen "+loglikelihood+" ***" );
        }

    }*/

    public void Initialize() {
        long seed;
        System.out.println("Initialize weights");
        if (Params.WEIGHT_TIME) {
            seed = 0;
        } else {
            seed = Params.SEED;
        }

        Random rnd = RandomGen(seed);

        for (int o = 0; o < Params.NNclassLabels; o++) {
            for (int m = 0; m < Params.window * NNEncode.encode[0].length + 1; m++)
                for (int n = 0; n < Params.nhidden; n++) {
                    double x = rnd.nextDouble();

                    wts12[o][n][m] = wts12[o][n][m] + Params.WEIGHT_RAND * x;
                }

            for (int m = 0; m < Params.nhidden + 1; m++) {
                double x = rnd.nextDouble();
                wts23[o][0][m] = wts23[o][0][m] + Params.WEIGHT_RAND * x;
            }
        }
    }

    public void print() {
        int len1 = Params.window * NNEncode.encode[0].length;
        int len2 = Params.nhidden;
        int len3 = 1;

        for (int o = 0; o < Params.NNclassLabels; o++) {
            System.out.println("NEURAL\t" + o);

            for (int i2 = 0; i2 < len2; i2++) {
                System.out.print("WTS12\t");
                for (int i1 = 0; i1 < len1 + 1; i1++)
                    System.out.print(wts12[o][i2][i1] + "\t");

                System.out.print("\n");
            }

            for (int i3 = 0; i3 < len3; i3++) {
                System.out.print("WTS23\t");
                for (int i2 = 0; i2 < len2 + 1; i2++)
                    System.out.print(wts23[o][i3][i2] + "\t");

                System.out.print("\n");
            }
        }
    }

    public Weights GetClone() {
        Weights cl = new Weights();
        for (int o = 0; o < Params.NNclassLabels; o++) {
            for (int m = 0; m < Params.window * NNEncode.encode[0].length + 1; m++)
                for (int n = 0; n < Params.nhidden; n++) {
                    cl.wts12[o][n][m] = wts12[o][n][m];
                    cl.deriv12[o][n][m] = deriv12[o][n][m];

                    if (Params.RPROP || Params.SILVA)
                        cl.delta12[o][n][m] = delta12[o][n][m];
                    else
                        cl.dw12[o][n][m] = dw12[o][n][m];
                }

            for (int m = 0; m < Params.nhidden + 1; m++) {
                cl.wts23[o][0][m] = wts23[o][0][m];
                cl.deriv23[o][0][m] = deriv23[o][0][m];
                if (Params.RPROP || Params.SILVA)
                    cl.delta23[o][0][m] = delta23[o][0][m];
                else
                    cl.dw23[o][0][m] = dw23[o][0][m];
            }
        }

        return cl;

    }


    public double GetDW12(int i, int j, int k) {
        return dw12[i][j][k];
    }

    public double GetDW23(int i, int j, int k) {
        return dw23[i][j][k];
    }

    public void AddW12(int i, int j, int k, double l) {
        //System.out.println( l ); /////////
        if (!Params.RPROP && !Params.SILVA)
            dw12[i][j][k] = l;

        wts12[i][j][k] += l;
    }

    public void AddW23(int i, int j, int k, double l) {
        //System.out.println( l ); /////////
        if (!Params.RPROP && !Params.SILVA)
            dw23[i][j][k] = l;

        wts23[i][j][k] += l;
    }

    public double[][] GetWeights12(int o) {
        return wts12[o];
    }

    public double[][] GetWeights23(int o) {
        return wts23[o];
    }


    public void RandomizeUniform(final double range, final long seed) {
        System.out.println("\tRandomize weights (uniform)");
        Random rnd = RandomGen(seed);
        for (int o = 0; o < Params.NNclassLabels; o++) {
            for (int m = 0; m < Params.window * NNEncode.encode[0].length + 1; m++)
                for (int n = 0; n < Params.nhidden; n++) {
                    double x = rnd.nextDouble();
                    wts12[o][n][m] = (x - 0.5D) * range;
                }

            for (int m = 0; m < Params.nhidden + 1; m++) {
                double x = rnd.nextDouble();
                wts23[o][0][m] = (x - 0.5D) * range;
            }
        }
    }

    public void RandomizeNormal(final double stdev, final long seed) {
        System.out.println("\tRandomize weights (normal)");
        Random rnd = RandomGen(seed);

        for (int o = 0; o < Params.NNclassLabels; o++) {
            for (int m = 0; m < Params.window * NNEncode.encode[0].length + 1; m++)
                for (int n = 0; n < Params.nhidden; n++) {
                    double x = rnd.nextDouble();
                    wts12[o][n][m] = stdev * StatUtil.getInvCDF(x, true);
                }

            for (int m = 0; m < Params.nhidden + 1; m++) {
                double x = rnd.nextDouble();
                wts23[o][0][m] = stdev * StatUtil.getInvCDF(x, true);
            }
        }
    }

    public static Random RandomGen(final long seed) {
        Random rnd;
        if (seed == 0) {
            System.out.println("\tNew random generator (time)");
            rnd = new Random();
        } else {
            System.out.println("\tNew random generator (" + seed + ")");
            rnd = new Random(seed);
        }
        return rnd;
    }

    public void initWeightsByFile(String file) {
        String line = "";
        int x = -1, y = 0, z = 0;
        RandomAccessFile in = null;

        try {
            in = new RandomAccessFile(file, "r");
            try {
                while ((line = in.readLine()) != null) {
                    String[] st = line.split("\\t");
                    int count = st.length;

                    switch (st[0]) {
                        case "NEURAL":
                            x++;
                            y = 0;
                            z = 0;
                            break;
                        case "WTS12":
                            for (int i = 0; i < count - 1; i++)
                                wts12[x][y][i] = Double.parseDouble(st[i + 1]);

                            y++;
                            break;
                        case "WTS23":
                            for (int i = 0; i < count - 1; i++)
                                wts23[x][z][i] = Double.parseDouble(st[i + 1]);

                            z++;
                            break;
                        default:
                            System.out.println("Error with option - " + st[0]);
                    }
                }
            } catch (IOException e) {
                System.out.println("File access " + file + " ERROR 1");
            }
        } catch (IOException e) {
            System.out.println("File access " + file + " ERROR 2");
        } finally {
            try {
                in.close();
            } catch (Exception e) {
            }
        }
    }


}
