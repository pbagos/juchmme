package nn;

import java.util.ArrayList;

import hmm.NNEncode;
import hmm.Model;
import hmm.Params;

/*
 * ProteinEntry Class
 * Represents the object ProteinEntry.
 * Its characteristics are the proteinName (String), the amino acid sequence (String),
 * the label sequence (String) and the amino acids' number (int). Note that each of
 * the 3 first characteristic is the corresponding field of the sequence file.
 */

public class ProteinEntry {

	public String proteinName;
	public String aminoAcidSequence;
	public String labelSequence;
	final public static int aminoAcidNum = 20;

	//constructor
	public ProteinEntry(String proteinName, String aminoAcidSequence, String labelSequence) {
		this.proteinName = proteinName;
		this.aminoAcidSequence = aminoAcidSequence;
		this.labelSequence = labelSequence;
	}

	/**
	 * Creates a list (ArrayList) whose elements are binary arrays corresponding to amino acid windows
	 * of the amino acid sequence. The amino acid windows are created by sliding along the
	 * amino acid sequence and are of size windowSize. Each amino acid of each sliding window is converted
	 * to binary format and this way the sliding window is appended as a binary array (double[]) into the
	 * output list aaBinaryWindowList (ArrayList).
	 *
	 * @param windowSize is an int corresponding to the desired sliding window size
	 */
	public ArrayList<double[]> getBinaryAAwindows(int windowSize) {

		int startPosition = 0;
		int stopPosition = 0;
		ArrayList<double[]> aaBinaryWindowList = new ArrayList<double[]>(); /* a list whose elements are arrays of integers (binary)
		corresponding to an aa window */

		/* adds fake aa at the beginning and end of the sequence to allow the use of the labels corresponding
		to the first and last N (N=(windowSize-1)/2) amino acids of the amino acid sequence */

		String aminoAcidSeqFlanked = FlankAASeq(Params.windowLeft, Params.windowRight);
		stopPosition = aminoAcidSeqFlanked.length() - windowSize;
		for (int i = startPosition; i <= stopPosition; i++) {
			String temp = aminoAcidSeqFlanked.substring(i, (i + windowSize)); // creates the windows put of the flanked sequence
			double[] binaryWindow = AminoAcidsToBinary(temp); // converts aa window TO binary aa window
			aaBinaryWindowList.add(binaryWindow); // adds binary aa window (as a list element) in the list
		}

		return aaBinaryWindowList;
	}

	/**
	 * Reads the object's labelSequence and returns a list with its labels.
	 *
	 * @return labelList of type ArrayList<Character> labelList
	 */
	public ArrayList<Character> getLabelList() {

		int startPosition = 0;
		int stopPosition = this.labelSequence.length();
		ArrayList<Character> labelList = new ArrayList<Character>();

		for (int i = startPosition; i < stopPosition; i++) {
			labelList.add(this.labelSequence.charAt(i));
		}
		return labelList;
	}

	/**
	 * Takes as input the window size (int windowSize).
	 * Adds fake amino acids (#) in the beginning and end of the amino acid sequence.
	 *
	 * @param windowSize is an int corresponding to the desired sliding window size
	 */
	private String FlankAASeq(int windowLeft, int windowRight) {
		String flankAALeft = "";
		String flankAARight = "";
		String aminoAcidSeqFlanked = this.aminoAcidSequence;
		for (int i = 0; i < windowLeft; i++) {
			flankAALeft = flankAALeft.concat("#");
		}
		for (int i = 0; i < windowRight; i++) {
			flankAARight = flankAARight.concat("#");
		}

		aminoAcidSeqFlanked = flankAALeft.concat(aminoAcidSeqFlanked);
		aminoAcidSeqFlanked = aminoAcidSeqFlanked.concat(flankAARight);
		return aminoAcidSeqFlanked;
	}

	/**
	 * Takes as input an amino acid string (string window) and converts it to an array of doubles.
	 * Since each amino acid of the string corresponds to an array of 0's and 1's, then
	 * the return array is the concatenation of each amino acid's binary array.
	 *
	 * @param window is a string of amino acids
	 */
	private static double[] AminoAcidsToBinary(String window) {
		int counter = 0; //defines in which position of the return array to add the digit
		double[] binaryWindow = new double[window.length() * NNEncode.encode[0].length];

		//for each amino acid
		for (int i = 0; i < window.length(); i++) {
			//find the binary array of the amino acid
			double[] binaryAA = ConvertAAToBinary(window.charAt(i));
			//for each digit of the amino acid binary array
			for (int j = 0; j < binaryAA.length; j++) {
				//concatenate each digit to the final (returning) array
				binaryWindow[counter] = binaryAA[j];
				counter++;
			}
		}

		return binaryWindow;
	}

	/**
	 * Takes as input an amino acid character (char aminoAcid) and uniquely codes it to an array of doubles.
	 *
	 * @param aminoAcid is an amino acid character
	 */
	private static double[] ConvertAAToBinary(char aminoAcid) {
		double[] binary = new double[Model.nesym];
		try {
			if (aminoAcid == '#') {
				for (int i = 0; i < Model.nesym; i++)
					binary[i] = 0;
			} else
				binary = NNEncode.encode[Model.esym.indexOf(aminoAcid)];

		} catch (Exception e) {
			System.out.println("FATAL ERROR: unknown aa" + aminoAcid);
			System.exit(1);
		}

		return binary;
	}
}
