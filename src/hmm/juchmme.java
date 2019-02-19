/*
 *   Copyright (C) 2018. Greenweaves Software Pty Ltd
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

import java.io.*;

public class Juchmme {

    public static void main(String[] args) throws Exception {
        long startTime = System.currentTimeMillis();

        System.out.println("JUCHMME :: Java Utility for Class Hidden Markov Models and Extensions");
        System.out.println("Version 2.1.1; February 2019");
        System.out.println("Copyright (C) 2018 Pantelis Bagos");
        System.out.println("Freely distributed under the GNU General Public Licence (GPLv3)");
        System.out.println("--------------------------------------------------------------------------");

        //Parse Method Arguments
        Args.Init(args);
        System.out.println("--------------------------------------------------------------------------");
        //Parse Method Parameters
        Params.Init(Args.parFile);
        System.out.println("--------------------------------------------------------------------------");
        //Parse Model
        Model.Init(Args.mdelFile);
        System.out.println("--------------------------------------------------------------------------");
        //Parse HNN Encoding File
        if (!Args.fileEncode.equals("")) {
            NNEncode.Init(Args.fileEncode);
            System.out.println("--------------------------------------------------------------------------");
        }


        /*
         *  TRAINING SET
         */
        Stats stats = new Stats();
        SeqSet trainSet, trainSetLa, trainSetUn;

        trainSet = new SeqSet(0);
        trainSetLa = new SeqSet(0);
        trainSetUn = new SeqSet(0);

        if (Args.RUN_TRAINING) {
            //Initiate and Set the training Set
            trainSet = new SeqSet(Args.file);

            if (trainSet.ExistUnlabeled()) {
                trainSetLa = new SeqSet(trainSet.numOfLabeledSeqs);
                trainSetUn = new SeqSet(trainSet.numOfUnlabeledSeqs);

                int l = 0, Un = 0;
                for (int j = 0; j < trainSet.nseqs; j++) {
                    if (trainSet.seq[j].IsUnlabeled()) {
                        trainSetUn.seq[Un] = trainSet.seq[j];
                        Un++;
                    } else {
                        trainSetLa.seq[l] = trainSet.seq[j];
                        l++;
                    }
                }
            } else {
                trainSetLa = trainSet;
            }
        }

        /*
         *  PROBABILITIES
         *  Parse Probabilities (Transitions / Emissions / Weights)
         */
        Probs tab;
        tab = new Probs(trainSetLa);

        /*  TRAINING / TESTING
         *  Run a Training Procedure OR just Training OR just Testing
         */
        if (Args.RUN_TRAINING && (Args.RUN_SELFCONS || Args.RUN_CROSSVAL || Args.RUN_JACKNIFE)) {
            // TRAINING PROCEDURE
            TrainProc tp = new TrainProc(trainSetLa, trainSetUn, tab);
            stats.calcStats(trainSetLa);
        } else {
            HMM model;

            // TRAINING
            if (Args.RUN_TRAINING) {
                System.out.println("TRAINING");
                Estimator est = new Estimator(trainSetLa, trainSetUn, tab);
                model = est.GetModel();
            } else {
                model = new HMM(tab);
            }

            // DECODING
            if (Args.filesThree.size() > 0) {
                SeqSet testSet = new SeqSet((String) Args.filesThree.get(0));
                Decoding dec = new Decoding(model, testSet, Params.CONSTRAINT, true);
            }

            if (Args.filesFasta.size() > 0) {
                SeqSet testSet = new SeqSet((String) Args.filesFasta.get(0));
                Decoding dec = new Decoding(model, testSet, Params.CONSTRAINT, true);
            }

        }

        long endTime = System.currentTimeMillis();
        float execTime = (endTime - startTime);
        System.out.println("Execution time = " + (endTime - startTime) + " miliseconds");
        System.out.println("Execution time = " + execTime / 1000 + " seconds");
    }
}