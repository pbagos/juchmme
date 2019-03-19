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

import java.util.concurrent.ForkJoinPool;

class Decoding {

    public Decoding(HMM model, SeqSet testSet, boolean free, boolean showResults, boolean parallel) throws Exception {
        Test pred;
        int CPUs;

        //Test set must be have more than 4 sequences
        if (parallel && (testSet.nseqs > 4)) {
            if (Params.processors / testSet.nseqs < 2)
                CPUs = testSet.nseqs / 2;
            else
                CPUs = Params.processors;

            final int threshold = testSet.nseqs / CPUs;

            DecodingParallel dec = new DecodingParallel(model, testSet, free, 0, testSet.nseqs, threshold, showResults);
            ForkJoinPool threadPool = new ForkJoinPool(Params.processors);
            threadPool.invoke(dec);

        } else {
            for (int j = 0; j < testSet.nseqs; j++) {
                pred = new Test(model, testSet.seq[j], free);
                if (showResults)
                    testSet.seq[j].ShowRes();
            }
        }
    }


}