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
import java.util.*;

public class NNEncode {
    public static double[][] encode;

    public static final void Init(String file) {
        System.out.println("Preparing NN Encoding file (" + file + ")");
        String linea = "";
        int row = 0;
        encode = new double[Model.nesym][Model.nesym];

        RandomAccessFile in = null;

        try {
            in = new RandomAccessFile(file, "r");

            try {
                row = 0;

                while ((linea = in.readLine()) != null) {
                    if (row > Model.nesym)
                        System.err.println(file + ": more rows than " + Model.esym);

                    StringTokenizer st = new StringTokenizer(linea, " \t");
                    int cols = st.countTokens();

                    if (cols != Model.nesym)
                        System.err.println(file + ": " + cols + "columns instead of " + Model.esym + " at line " + (row + 1));

                    for (int col = 0; col < cols; col++) {
                        Double val = Double.valueOf(st.nextToken());
                        encode[row][col] = val.doubleValue();
                        //System.out.println(val.doubleValue()+" "+encode[row][col]);
                    }
                    row++;
                }
            } catch (IOException e) {
                System.out.println("File access for Encode ERROR 1");
            }
        } catch (IOException e) {
            System.out.println("File access for Encode ERROR 2");
        } finally {
            try {
                in.close();
            } catch (Exception e) {
            }
        }
    }

    public static void printEncode() {
        for (int i = 0; i < encode.length; i++) {
            for (int j = 0; j < encode[i].length; j++) {
                System.out.print(encode[i][j] + "\t");
            }
            System.out.println("");
        }
    }

}