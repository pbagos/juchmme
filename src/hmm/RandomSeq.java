package hmm;

import java.util.*;
import java.io.*;

public class RandomSeq {
    public static void main(String[] args) {
        String fileA = args[0];
        String fileE = args[1];
        String filePar = args[2];
        String fileModel = args[3];
        int seqNum = Integer.parseInt(args[4]);

        Model.Init(fileModel);
        Params.Init(filePar);

        Probs tab;
        tab = new Probs(fileA, fileE);

        Random rnd = Weights.RandomGen(0);

        System.out.println("Normalizing transitions");
        for (int i = 0; i < tab.aprob.length; i++) {
            double sum = 0;
            for (int j = 0; j < tab.aprob[i].length; j++)
                if (tab.aprob[i][j] > 0)
                    sum += tab.aprob[i][j];

            for (int j = 0; j < tab.aprob[i].length; j++)
                if (tab.aprob[i][j] > 0)
                    tab.aprob[i][j] /= sum;
        }

        try {
            File outputFile = new File("set.txt");
            FileWriter out = new FileWriter(outputFile);

            for (int i = 0; i < seqNum; i++) {
                String sequence = "";
                String topology = "";

                boolean found_end = false;
                double rndProb;
                int seq_state0 = 0;

                for (int j = 1; j <= 9999; j++) {
                    int seq_state = -1;
                    int seq_symbol = -1;

                    rndProb = rnd.nextDouble();

                    double cntProb = 0;

                    for (int k = 0; k < Model.nstate; k++) {
                        cntProb += tab.aprob[seq_state0][k];

                        if (cntProb >= rndProb) {
                            seq_state = k;
                            //System.out.println("Assigned state "+ seq_state );
                            break;
                        }

                    }

                    if (seq_state == -1) {
                        System.out.println("ERROR: Could not assign state after " + seq_state0);
                    }

                    if (seq_state == Model.nstate - 1) {
                        found_end = true;
                        break;
                    }

                    /*
                    Generate random numbers
                    */

                    double sum = 0;

                    for (int k = 0; k < Model.nesym; k++) {
                        sum += tab.eprob[seq_state][k];
                    }

                    // Scale to obtain a discrete probability distribution

                    for (int k = 0; k < Model.nesym; k++)
                        if (sum != 0)
                            tab.eprob[seq_state][k] /= sum;
                        else
                            tab.eprob[seq_state][k] = 0;

                    rndProb = rnd.nextDouble();

                    cntProb = 0;
                    for (int k = 0; k < Model.nesym; k++) {
                        cntProb += tab.eprob[seq_state][k];

                        if (cntProb >= rndProb) {
                            seq_symbol = k;
                            break;
                        }

                    }

                    if (seq_symbol == -1) {
                        System.out.println("ERROR seq_symbol=-1: " + rndProb + " " + cntProb);
                    }

                    seq_state0 = seq_state;

                    sequence += Model.esym.charAt(seq_symbol);
                    //topology += Model.ostate[ seq_state ];
                    topology += Model.pstate[seq_state];
                }

                if (!found_end)
                    System.out.println("INTERRUPTED");

                //Save to the File
                out.write(">SEQ_" + (i + 1) + "\n");
                out.write(sequence + "\n");
                out.write(topology + "\n");
            }

            out.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}