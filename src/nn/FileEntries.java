package nn;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;

import hmm.SeqSet;

/*
 * FileEntries Class
 * Represents the object FileEntries (all the entries of the file).
 * Its characteristics are the filename (String) and
 * the labels (Set<Character>) found in the label sequences.
 */

public class FileEntries {
	
	public String fileName;
	public Set<Character> labels;
	public ArrayList<ProteinEntry> proteinEntriesList;
	
	//constructor
	public FileEntries(String fileName) {
		this.fileName = fileName;
		this.labels = new HashSet<Character>();
		this.proteinEntriesList = new ArrayList<ProteinEntry>();
		getProteinEntries(); // creates the list of the file entries
	}

    public FileEntries(SeqSet seqs, String[] vPaths) {
        String header;  //ProteinName
        String seq;   //Sequence
        String label; //Sequence Label

        this.labels = new HashSet<Character>();
        this.proteinEntriesList = new ArrayList<ProteinEntry>();

        for (int s=0; s<seqs.nseqs; s++){
            ProteinEntry entry = new ProteinEntry (seqs.seq[s].getHeader(), seqs.seq[s].getSeq(), vPaths[s]);

            this.proteinEntriesList.add(entry);
            updateLabels(entry); // detects the labels existing in each entry's label sequence
        }

    }
	
	/**
	 * It reads the file with the sequences and returns a list (ArrayList<ProteinEntry>) 
	 * whose each element is a ProteinEntry object with its characteristics 
	 * proteinName, aa sequence, label sequence.
	 *
	 */	
	private void getProteinEntries() {
		String line;  //ProteinName
		String seq;   //Sequence
		String label; //Sequence Label
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(this.fileName));
			while ( (line = br.readLine())!= null){
				if (line.charAt(0)=='>'){ /* check for first line (name) */
					seq   = br.readLine();
					label = br.readLine();
					ProteinEntry entry = new ProteinEntry (line, seq, label);
					
					this.proteinEntriesList.add(entry);
					updateLabels(entry); // detects the labels existing in each entry's label sequence 
				}
			}
			br.close();
		}
		
		catch (IOException e) {
			System.out.println(e.toString());
		}
	}
	
	/**
	 * Takes as input a ProteinEntry object, 
	 * detects the labels existing in the labelSequence and 
	 * updates the labels list (Set<Character>).
	 *
	 * @param entry is a ProteiEntry object
	 */	
	private void updateLabels(ProteinEntry entry){
		for (int i=0;i<entry.labelSequence.length();i++){
			this.labels.add(entry.labelSequence.charAt(i));
		}
	}
	
}
