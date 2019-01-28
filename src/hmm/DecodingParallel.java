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

import java.util.concurrent.RecursiveAction;

public class DecodingParallel extends RecursiveAction {
    private int start;
    private int end;
    private int threshold;
    HMM model;
    SeqSet testSet;
    boolean free;
    boolean showResults;

    public DecodingParallel(HMM model, SeqSet testSet, boolean free, int start, int end, int threshold, boolean showResults) {
        this.start = start;
        this.end = end;
        this.threshold = threshold;
        this.model = model;
        this.testSet = testSet;
        this.free = free;
        this.showResults = showResults;
    }

    @Override
    protected void compute() {
        if (end - start < threshold) {
            computeDirectly(this.model, this.testSet, this.free);
        } else {
            int middle = (start + end) / 2;
            DecodingParallel subTask1 = new DecodingParallel(model, testSet, free, start, middle, threshold, showResults);
            DecodingParallel subTask2 = new DecodingParallel(model, testSet, free, middle, end, threshold, showResults);
            invokeAll(subTask1, subTask2);
        }
        testSet = this.testSet;
    }

    protected void computeDirectly(HMM model, SeqSet testSet, boolean free) {
        try {
            Test pred;

            for (int i = start; i < end; i++) {
                pred = new Test(model, testSet.seq[i], free);
                if (this.showResults)
                    testSet.seq[i].ShowRes();
            }
        } catch (Exception e) {
            System.out.println("DecodingParallel: ERROR ");
        }

    }

    /**
     * Method to determine current thread id in the created pool
     *
     * @param processors
     * @return --Returns the result of getId mod the number of processors to keep it in the range of 0 - processors-1
     */
    public static long getThreadID(int processors) {
        return Thread.currentThread().getId() % processors;
    }


}