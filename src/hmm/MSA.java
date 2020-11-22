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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class MSA {

    public String[] paths;

    public MSA(HMM hmm, SeqSet testSet, boolean free) throws Exception {
        System.out.println("Decoding using MSA");

        //testSet.RemoveDashes();
        testSet.RemoveChars(Params.MSAgapSymbol);

        //Predict labels for alignment sequences
        Decoding dec = new Decoding(hmm, testSet, free, false, true, false);

        paths = new String[testSet.nseqs];
        for (int i = 0; i < testSet.nseqs; i++){
            //System.out.println(testSet.seq[i].header);
            //System.out.println(testSet.seq[i].path[Params.usingMethodSSL]);
            paths[i] = addDashes(testSet.seq[i].getSeqOrig(), testSet.seq[i].path[Params.usingMethodSSL] );
        }
    }

    private String addDashes(String seqWithDashes, String predWithoutDashes){
        Pattern p = Pattern.compile("[\\*]");
        Matcher m = p.matcher(seqWithDashes);

        StringBuilder str = new StringBuilder(predWithoutDashes);
        while (m.find()){
            str.insert(m.start(),"-");
        }

        return str.toString();
    }

}