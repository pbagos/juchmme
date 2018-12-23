package nn;

import java.util.*;
import java.io.*;

import org.joone.helpers.factory.JooneTools;
import org.joone.engine.FullSynapse;

public class Main{

	public static void main (String[] args){
		String sequencesFile=args[0];
		
		//params
		int windowSize = 7;
		int hiddenNeuronsNum = 3;
		int numberOfCycles = 150;
		boolean doCrossValidation = true;
		int crossValidationIterations = 5;
		double minimumGEdifference = 0; // the min error difference between 2 adjacent iterations so as to continue training
		char[] labelAlphabet = {'I','i','M','m','n','O','o'};//labels for b-barrels
		//char[] labelAlphabet = {'I','i','M','N','m','O','o'};//labels for a-helices
		
		
		double[][][] wtsIH = new double [labelAlphabet.length][][]; // IH weights for HMM (wts12)
		double[][][] wtsHO = new double [labelAlphabet.length][][]; // HO weights for HMM (wts23)
				
		FileEntries fileData = new FileEntries(sequencesFile);
		NNinput input = new NNinput (windowSize,fileData.proteinEntriesList);
				
		for (int labelIndex=0; labelIndex<labelAlphabet.length; labelIndex++) {
			char labelForTraining = labelAlphabet[labelIndex];
			double targetGlobalError = 0;
			double finalGE;
			
			System.out.println("\n########## Label " + labelForTraining + " ###########");
			//Create the desired output for the NN based on the label for which the NN should be trained
			NNDesiredOutput DesiredOutputForLabel = new NNDesiredOutput(labelForTraining,fileData.proteinEntriesList);
			if (doCrossValidation == true){
				double sumGlobalError = 0;
				for (int i=0; i<crossValidationIterations; i++){
					//Initialize the NN and train it
					System.out.println("Cross Validation iteration number " + (i+1));
					SingleLayerNN_RPROP nnToTrain = new SingleLayerNN_RPROP (input.inputArray, DesiredOutputForLabel.desiredOutputArray, hiddenNeuronsNum, numberOfCycles, doCrossValidation, targetGlobalError, minimumGEdifference);
					nnToTrain.train();
					nnToTrain.nnet.join(); //wait for threads to finish - do not remove
					double trainingGlobalError = nnToTrain.getGlobalErrorForTrainingData();
					System.out.println("Training Global Error = " + trainingGlobalError);
					double validationGlobalError = nnToTrain.getGlobalErrorForValidationData();
					System.out.println("Validation Global Error = " + validationGlobalError + "\n");
					sumGlobalError += validationGlobalError;
					
					// Uncomment to print the predicted output vs the desired output for the validation set
					//String validationOutputFile = "results/RpropNN/validation/output_"+ labelForTraining + ".txt";
					//nnToTrain.printPredictedVsDesiredValidationOutput(validationOutputFile);
				}
				targetGlobalError = sumGlobalError/crossValidationIterations; //take the mean global error for the cross validation phase
				System.out.println("Target Global Error = " + targetGlobalError + "\n");
			}
			
			// Create neural network and begin training phase
			System.out.println("Starting Neural Network training for label: " + labelForTraining + " ...");
			SingleLayerNN_RPROP nnToTrain = new SingleLayerNN_RPROP (input.inputArray, DesiredOutputForLabel.desiredOutputArray, hiddenNeuronsNum, numberOfCycles, false, targetGlobalError, minimumGEdifference);
			nnToTrain.train();
			nnToTrain.nnet.join(); //wait for threads to finish - do not remove
			finalGE = nnToTrain.nnet.getMonitor().getGlobalError();
			System.out.println("Final Global Error = " + finalGE);
			
			
			//Print the weights in a NevProp like file
			String allWeights = "results/RpropNN/weights/allWeights_"+ labelForTraining + ".txt";
			nnToTrain.printWeightsForAllSynapse(allWeights);
			
			//Store the weights in the array that will be given to the HMM
			wtsIH[labelIndex]= nnToTrain.getIHWeightsForHMM();
			wtsHO[labelIndex]= nnToTrain.getHOWeightsForHMM();
			
			//Save the input and output data in a file suitable for input in the NevProp nets
			String IOdataFile = "results/RpropNN/IOdata/"+ labelForTraining + ".txt";
			nnToTrain.printIOdata(IOdataFile);
			
			//Print the predicted vs the desired output in a file
			String outputsFile = "results/RpropNN/training/output_"+ labelForTraining + ".txt";
			nnToTrain.printPredictedVsDesiredTrainingOutput(outputsFile);

			
		 	//Uncomment to save the NN in a file suitable to be read by joone methods
			//String saveNNFile = "data/savedNN.snet";
			//try {
			//	JooneTools.save(nnToTrain.nnet, saveNNFile);
			//}
			//catch (IOException e) {
			//	System.out.println(e.toString());
			//}
		}
		
		//Print the 3D arrays with the weights for the HMM
		print3Darray(wtsIH);
		print3Darray(wtsHO);
		//print3DarrayinFile(wtsIH,"hmmweightsIH.txt");
		//print3DarrayinFile(wtsHO,"hmmweightsHO.txt");
	}
	
	/**
	 * Print 3d array
	 * @author D.Sarantopoulou
	 */	
	static void print3Darray(double[][][] A3D){
		for (int l=0; l<A3D.length; l++){
			System.out.println("{ ");
			for (int m=0; m<A3D[l].length; m++){
				System.out.print("{ ");
				for (int n=0; n<A3D[l][m].length-1; n++){
					//System.out.println(l + " "+ m + " "+ n + " " + A3D[l][m][n]);
					System.out.print(A3D[l][m][n] +", ");
				}
				System.out.print(A3D[l][m][A3D[l][m].length-1]);
				System.out.println(" },");
			}
			System.out.println(" },");
		} 
	}
	
	/**
	 * Print 3d array in file
	 * @author D.Sarantopoulou
	 */	
	static void print3DarrayinFile(double[][][] A3D, String weightsFile){
		try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(weightsFile)));	
			for (int l=0; l<A3D.length; l++){
				pw.println("{ ");
				for (int m=0; m<A3D[l].length; m++){
					pw.print("{ ");
					for (int n=0; n<A3D[l][m].length-1; n++){
						pw.print(A3D[l][m][n] +", ");
					}
					pw.print(A3D[l][m][A3D[l][m].length-1]);
					pw.println(" },");
				}
				pw.println(" },");
			} 
			pw.close();
		}
		catch (IOException e) {
			System.out.println(e.toString());
		}
		
	}
	
}