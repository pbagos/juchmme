package nn;

import java.util.ArrayList;

/*
 * NNDesiredOutput Class
 * Represents the object NNDesiredOutput (the desired output of the Neural Network).
 * Its characteristics are the Desired Label (char desiredLabel), for which we train the neural network,
 * the Desired Output (double[] desiredOutputArray) for every label which is 0 or 1
 * and the list of the Protein Entries of the file (ArrayList<ProteinEntry> proteinEntriesList).
 */

public class NNDesiredOutput {

	public char desiredLabel;
	public double[][] desiredOutputArray;
	public ArrayList<ProteinEntry> proteinEntriesList;
	
	//CONSTRUCTOR
	public NNDesiredOutput(char desiredLabel,ArrayList<ProteinEntry> proteinEntriesList) {
		this.desiredLabel = desiredLabel;
		this.proteinEntriesList = proteinEntriesList;
		createDesiredOutputArray();
	}
	
	/**
	 * Creates an array of all the binary outputs corresponding to each label of the sequences.
	 * First it gets the label list for each protein entry.
	 * Second it creates the binary desired output list putting 0 or 1 for each label.
	 * Third it converts the binary output list to an array (each line of the array has 0 or 1).
	 */
	public void createDesiredOutputArray() 
	{	
		ArrayList<Double> desiredBinaryOutputList = new ArrayList<Double>();
		
		// for each entry of the file
		for(int i=0;i<proteinEntriesList.size();i++) { 
			ArrayList<Character> labelList = proteinEntriesList.get(i).getLabelList(); // gets the label list
			// for each label
			for(int j=0;j<labelList.size();j++){
				if(desiredLabel==labelList.get(j)){
					desiredBinaryOutputList.add(1.0);
				}
				else {
					desiredBinaryOutputList.add(0.0);
				}
			}
		}
		
		/* converts the binary output list to an array */
		
		this.desiredOutputArray = new double [desiredBinaryOutputList.size()][1]; 
		
		// for each element of the list
		for(int i=0;i<desiredBinaryOutputList.size();i++) { 
			// put to each line of the array, the corresponding 0 or 1 of the list
			this.desiredOutputArray[i][0]= desiredBinaryOutputList.get(i);
		}
	}
	
	
}
