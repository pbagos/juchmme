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

public class ForwardBackward
{
    private double loglikelihood = 0.D;
    private double[] logP;
    private Forward[] fwds;
    private Backward[] bwds;

    public ForwardBackward(HMM hmm, boolean free, SeqSet fbseqs, WeightsL weightsL) throws Exception {
        this.fwds = new Forward[fbseqs.nseqs];
        this.bwds = new Backward[fbseqs.nseqs];
        this.logP = new double[fbseqs.nseqs];
        Activ[][][] acts = null;

        Run(hmm, this.fwds, this.bwds, this.logP, free, fbseqs, acts, weightsL);
    }

    public ForwardBackward(HMM hmm, boolean free, SeqSet fbseqs) throws Exception {
        this.fwds = new Forward[fbseqs.nseqs];
        this.bwds = new Backward[fbseqs.nseqs];
        this.logP = new double[fbseqs.nseqs];
        Activ[][][] acts = null;
        WeightsL weightsL = new WeightsL(fbseqs.nseqs);

        Run(hmm, this.fwds, this.bwds, this.logP, free, fbseqs, acts, weightsL);

    }

    public ForwardBackward(HMM hmm, boolean free, SeqSet fbseqs, Activ[][][] fbacts) throws Exception {
        this.fwds = new Forward[fbseqs.nseqs];
        this.bwds = new Backward[fbseqs.nseqs];
        this.logP = new double[fbseqs.nseqs];
        WeightsL weightsL = new WeightsL(fbseqs.nseqs);

        Run(hmm, this.fwds, this.bwds, this.logP, free, fbseqs, fbacts, weightsL);

    }

    public ForwardBackward(HMM hmm, boolean free, SeqSet fbSeqs, Activ[][][] fbActs, WeightsL weightsL) throws Exception {
        this.fwds = new Forward[fbSeqs.nseqs];
        this.bwds = new Backward[fbSeqs.nseqs];
        this.logP = new double[fbSeqs.nseqs];

        Run(hmm, this.fwds, this.bwds, this.logP, free, fbSeqs, fbActs, weightsL);
    }

    public void Run(HMM hmm, Forward[] fwds, Backward[] bwds, double[] logP, boolean free,
                    SeqSet fbSeqs, Activ[][][] fbActs, WeightsL weightsL) throws Exception {
        System.out.println("\tComputing Forward+Backward (" + ((free) ? "Free" : "Clumped") + ")");
        System.out.print("\t");

        for (int i = 0; i < fbSeqs.nseqs; i++)
            System.out.print("-");

        System.out.println("");
        System.out.print("\t");

        boolean errors = false;
        for (int s = 0; s < fbSeqs.nseqs; s++) {
            if (Params.HNN) {
                double[][] lge = new double[hmm.nstte][fbSeqs.seq[s].getLen()];
                for (int i = 0; i < fbSeqs.seq[s].getLen(); i++) {
                    lge[0][i] = hmm.getLoge(0, fbSeqs.seq[s], i);
                    lge[hmm.nstte - 1][i] = hmm.getLoge(0, fbSeqs.seq[s], i);

                    for (int k = 1; k < hmm.nstte - 1; k++) {
                        lge[k][i] = Math.log(fbActs[Model.slab[k]][s][i].layer3[0]);
                    }
                }

                fwds[s] = new Forward(hmm, fbSeqs.seq[s], lge, free);
                bwds[s] = new Backward(hmm, fbSeqs.seq[s], lge, free);

            } else {
                try {
                    fwds[s] = new Forward(hmm, fbSeqs.seq[s], free);
                    bwds[s] = new Backward(hmm, fbSeqs.seq[s], free);
                } catch (Exception e) {
                    System.out.println(e.getMessage() + "The error occured at sequence " + (s + 1) + " " + fbSeqs.seq[s].header);
                    errors = true;
                    continue;
                }
            }

            System.out.print("*");

            logP[s] = fwds[s].logprob();

            if (s < fbSeqs.nseqs) {
                loglikelihood += logP[s] * weightsL.getWeightL(fbSeqs.seq[s].getIndexID());
            }

        }
        System.out.println();

        if (errors)
            throw new Exception("Errors ocurred...");

    }

    public double getLogProb(){
        return this.loglikelihood;
    }

    public double[] getLogP(){
        return this.logP;
    }

    public double getLogP(int s){
        return this.logP[s];
    }

    public Forward[] getFwds(){
        return this.fwds;
    }

    public Forward getFwds(int s){
        return this.fwds[s];
    }

    public Backward[] getBwds(){
        return this.bwds;
    }

    public Backward getBwds(int s){
        return this.bwds[s];
    }
}