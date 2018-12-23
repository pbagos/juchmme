package nn;

import java.util.Random;
import java.io.*;

public class CrossValidation implements Serializable{

	private int [] randomlySelectedElements=null;
	private double [][] inputArray;
	private double [][] desiredOutputArray;
	private double [][] inputTrainingArray=null;
	private double [][] inputValidationArray=null;
	private double [][] desiredOutputTrainingArray=null;
	private double [][] desiredOutputValidationArray=null;
	
	/**
	 * Creates an object of the class CrossValidation.
	 * @param inputArray
	 * @param desiredOutputArray
	 */
	public CrossValidation(double[][] inputArray, double [][] desiredOutputArray) {
		this.inputArray = inputArray;
		this.desiredOutputArray = desiredOutputArray;
		getInputTrainingSet();
		getInputValidationSet();
		getDesiredOutputTrainingArray();
		getDesiredOutputValidationArray();
	}
	
	/** 
	 * Updates the input array.
	 */
	public void setInputArray(double[][] inputArray) {
		this.inputArray = inputArray;
	}
	
	/** 
	 * Updates the desired output array.
	 */
	public void setDesiredOutputArray(double[][] desiredOutputArray) {
		this.desiredOutputArray = desiredOutputArray;
	}
	
	
	/** 
	 * Creates an array of size arraySize and assigns the value 0 to all elements.
	 * Then it chooses randomly numberOfAces positions and assigns them the value 1.
	 * Generally it is used to choose random training examples from the input set.
	 * @param numberOfAces
	 * @param arraySize
	 */
	private int[] assignAcesRandomlyInArray(int numberOfAces, int arraySize) 
	{
		Random generator = new Random();
		int randomInteger;
		int [] AcesRandomlyInArray = new int [arraySize];
		
		for(int i=0; i<AcesRandomlyInArray.length; i++){
			AcesRandomlyInArray[i]=0;
		}
		
		for(int i=0; i<numberOfAces; i++)
		{
			randomInteger = generator.nextInt(AcesRandomlyInArray.length);
			while(AcesRandomlyInArray[randomInteger]==1){
				randomInteger = generator.nextInt(AcesRandomlyInArray.length);
			}
			AcesRandomlyInArray[randomInteger]=1;
		}
		
		return AcesRandomlyInArray;
	}
	
	
	/* ######################### INPUT ######################### */
	
	/** 
	 * Chooses randomly half of the training examples and creates the training set.
	 */
	public double[][] getInputTrainingSet() 
	{
		if(inputTrainingArray != null){
			return inputTrainingArray;
		}
		else{
			int numberOfElements=(int) Math.floor((double) inputArray.length/2); // takes the closer integer not greater than the argument
			
			if (randomlySelectedElements == null){
				randomlySelectedElements=assignAcesRandomlyInArray(numberOfElements,inputArray.length);
			}
			
			double[][] inputTrainingArray = new double[numberOfElements][];
			int counter=0;
			
			for (int i=0;i<inputArray.length;i++){
				if(randomlySelectedElements[i]==1){
					inputTrainingArray[counter]=inputArray[i];
					counter++;
				}
			}
			return inputTrainingArray;
		}
	}
	
	/** 
	 * Chooses randomly half of the training examples and creates the test set.
	 */
	public double[][] getInputValidationSet() 
	{
		if(inputValidationArray != null){
			return inputValidationArray;
		}
		else{
			// if this method runs before the training one then take the closer integer not smaller than the argument
			int numberOfElements=(int) Math.ceil((double) inputArray.length/2);
			
			if (randomlySelectedElements == null){
				randomlySelectedElements=assignAcesRandomlyInArray((int) Math.floor((double) inputArray.length/2),inputArray.length); // takes the closer integer not greater than the argument
			}
			
			double[][] inputValidationArray = new double[numberOfElements][];
			int counter=0;
			
			for (int i=0;i<inputArray.length;i++){
				if(randomlySelectedElements[i]==0){
					inputValidationArray[counter]=inputArray[i];
					counter++;
				}
			}
			return inputValidationArray;
		}
	}
	
	/* ############################# DESIRED OUTPUT ############################# */
		
	/** 
	 * Creates the corresponding desired output array for the training examples.
	 */
	public double[][] getDesiredOutputTrainingArray() 
	{
		if(desiredOutputTrainingArray != null){
			return desiredOutputTrainingArray;
		}
		else{
			int numberOfElements=(int) Math.floor((double) desiredOutputArray.length/2); // takes the closer integer not greater than the argument
			
			if (randomlySelectedElements == null){
				System.out.println("Fatal error :: No cookie for you today!!!");
			}
			double[][] desiredOutputTrainingArray = new double[numberOfElements][];
			int counter=0;
			
			for (int i=0;i<desiredOutputArray.length;i++){
				if(randomlySelectedElements[i]==1){
					desiredOutputTrainingArray[counter]=desiredOutputArray[i];
					counter++;
				}
			}
			return desiredOutputTrainingArray;
		}
	}
	
	/** 
	 * Creates the corresponding desired output array for the test examples.
	 */
	public double[][] getDesiredOutputValidationArray() 
	{	

		if(desiredOutputValidationArray != null){
			return desiredOutputValidationArray;
		}
		else{
			int numberOfElements=(int) Math.ceil((double) desiredOutputArray.length/2); // takes the closer integer not smaller than the argument
			
			if (randomlySelectedElements == null){
				System.out.println("Fatal error :: No cookie for you today!!!");
			}
			double[][] desiredOutputValidationArray = new double[numberOfElements][];
			int counter=0;
			
			for (int i=0;i<desiredOutputArray.length;i++){
				if(randomlySelectedElements[i]==0){
					desiredOutputValidationArray[counter]=desiredOutputArray[i];
					counter++;
				}
			}
			return desiredOutputValidationArray;
		}
	}

}
