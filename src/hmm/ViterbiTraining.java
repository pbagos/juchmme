/*
 *   Copyright (C) 2019. Greenweaves Software Pty Ltd
 *   This is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This software is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with GNU Emacs.  If not, see <http://www.gnu.org/licenses/>
 *   REAR 	Reversal Distance
 */

package hmm;

import java.util.*;

public class ViterbiTraining {
    private String[] vPaths;
    private double[] logPs;
    private double loglikelihood = 0.D;

    public ViterbiTraining(HMM hmm, SeqSet seqs, boolean free, WeightsL weightsL) {
        System.out.println("\tComputing Viterbi Training (" + ((free) ? "Free" : "Clumped") + ")");

        vPaths = new String[seqs.nseqs];
        logPs = new double[seqs.nseqs];

        System.out.print("\t");

        for (int i = 0; i < seqs.nseqs; i++)
            System.out.print("-");

        System.out.println("");

        Viterbi v;
        for (int s = 0; s < seqs.nseqs; s++) {
            v = new Viterbi(hmm, seqs.seq[s], free);
            vPaths[s] = v.getPath(); // Viterbi Path
            logPs[s] = v.getProb(); // Viterbi likelihood
            loglikelihood += v.getProb() * weightsL.getWeightL(s);
        }

    }

    public double getLogProb() {
        return this.loglikelihood;
    }

    public String[] getvPaths() {
        return this.vPaths;
    }

    public String getvPath(int s) {
        return this.vPaths[s];
    }

    public double[] getLogPs() {
        return this.logPs;
    }

    public double getvLogP(int s) {
        return this.logPs[s];
    }

    public void Exp(int indexOfPath, Seq x, double[][] A, double[][] E, WeightsL weightsL) {
        String[] vPath;
        int length = x.getLen();// Sequence Length

        //States length. All states must have the same length
        int stateLen = Model.state[0].length();
        vPath = new String[length];

        //Split ViterbiPath to a String Array with path states
        vPath = vPaths[indexOfPath].split("(?<=\\G.{" + stateLen + "})");

        Score(vPath, A, x, E, weightsL);
    }

    private void Score(String seqPath[], double A[][], Seq x, double E[][], WeightsL weightsL) {
        //if exists the begin state, find the next and scoring
        if (Params.ALLOW_BEGIN) {
            int k = Arrays.asList(Model.state).indexOf(seqPath[0]);
            A[0][k] = A[0][k] + (1 * weightsL.getWeightL(x.getIndexID()));
        }

        int seqLen = x.getLen();
        int sym, row, col, i;
        for (i = 1; i <= seqLen - 1; i++) {
            sym = x.getNESym(i - 1);
            row = Arrays.asList(Model.state).indexOf(seqPath[i - 1]);
            col = Arrays.asList(Model.state).indexOf(seqPath[i]);

            A[row][col] = A[row][col] + (1 * weightsL.getWeightL(x.getIndexID()));
            E[row][sym] = E[row][sym] + (1 * weightsL.getWeightL(x.getIndexID()));

        }

        //Score for the last state
        sym = x.getNESym(i - 1);
        row = Arrays.asList(Model.state).indexOf(seqPath[i - 1]);
        E[row][sym] = E[row][sym] + (1 * weightsL.getWeightL(x.getIndexID()));

        //if exists the end state, find the previous state and scoring
        if (Params.ALLOW_END) {
            row = Arrays.asList(Model.state).indexOf(seqPath[i - 1]);
            col = (Model.nstate) - 1;
            A[row][col] = A[row][col] + (1 * weightsL.getWeightL(x.getIndexID()));
        }

    }

}