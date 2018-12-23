package nn;

import java.util.ArrayList;
import hmm.NNEncode;

/*
 * NNinput Class
 * Represents the object NNinput (the input for the Neural Network).
 * Its characteristics are the windowSize (int) of the amino acid sequence and
 * the input array (double[][] inputArray) of all the binary windows.
 */

public class NNinput {
	
	public int windowSize;
	public double[][] inputArray;
	public ArrayList<ProteinEntry> proteinEntriesList;
	
	//constructor
	public NNinput(int windowSize,ArrayList<ProteinEntry> proteinEntriesList) {
		this.windowSize         = windowSize;
		this.proteinEntriesList = proteinEntriesList;
		createInputArray();
	}

	/**
	 * Creates an array of all the binary windows of all the sequences of the file.
	 * Takes as input the list of all the entries (ArrayList<ProteinEntry> proteinEntriesList).
	 * First it creates a list (ArrayList<double []> binaryWindowList) of all the binary windows of all the entries.
	 * Second it converts the list of all binary windows to a 2d array (double[][] inputArray).
	 */
	public void createInputArray() 
	{	
		ArrayList<double []> binaryWindowList = new ArrayList<double []>();
		
		// for each entry of the file
		for(int i=0;i<proteinEntriesList.size();i++) { 
			// adds all its windows to the list
			binaryWindowList.addAll(proteinEntriesList.get(i).getBinaryAAwindows(windowSize)); // proteinEntriesList.get(i) is a ProteinEntry object
		}
		
		/* converts the list of all binary windows to a 2d array */
		
		/* creates input array of binary aa for the NN */
		this.inputArray = new double [binaryWindowList.size()][windowSize*NNEncode.encode[0].length]; /* its height is the size of the list
		and its width is 20*windowSize */
		
		// for each element - window of the list
		for(int i=0;i<binaryWindowList.size();i++) { 
			// put to each line of the array, the corresponding array - element of the list :: each line of the array contains a double[] array
			inputArray[i]= binaryWindowList.get(i); // inputArray[i] :: represents a single row of 2d array
		}
	}

}
