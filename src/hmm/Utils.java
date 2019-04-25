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

import java.util.*;
import java.io.*;

public final class Utils {
    public static void notifyParseError(String key) {
        System.err.println("Invalid input for parameter " + key);
        System.exit(1);
    }

    /*
     * Parse a String value as a Boolean
     * @params value The String
     * @return The boolean value
     */
    public static boolean parseBoolean(String value, String key) {
        if (value.equals("true"))
            return true;
        else if (value.equals("false"))
            return false;

        notifyParseError(key);
        return false;
    }

    /*
     * Parse a String value as an Integer
     * @params value The String
     * @return The Integer value
     */
    public static int parseInteger(String value, String key) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            notifyParseError(key);
        }

        return 0;
    }

    /*
     * Parse a String value as a Double
     * @params value The String
     * @return The Double value
     */
    public static double parseDouble(String value, String key) {
        try {
            return Double.valueOf(value);
        } catch (NumberFormatException e) {
            notifyParseError(key);
        }

        return 0.0;
    }

    /*
     * Parse a String delimited value as a Double List
     * @params value The String
     * @return The Double List
     */
    public static double[] stringToDoubleList(String value, String key, String delim) {
        double[] list;

        StringTokenizer st = new StringTokenizer(value, delim);
        int iter = st.countTokens();

        list = new double[iter];

        for (int i = 0; i < iter; i++) {
            try {
                list[i] = Double.parseDouble(st.nextElement().toString());
            } catch (NumberFormatException e) {
                notifyParseError(key);
            }
        }

        return list;
    }

    /*
     * Parse a String delimited value as a String List
     * @params value The String
     * @return The String List
     */
    public static String[] stringToList(String value, String var, String delim) {
        String[] list;

        StringTokenizer st = new StringTokenizer(value, delim);
        int iter = st.countTokens();

        list = new String[iter];

        for (int i = 0; i < iter; i++) {
            list[i] = st.nextElement().toString();
        }

        return list;
    }

    public static String executeCommand(String command) {
        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            int exitVal = p.waitFor();
            //System.out.println("exitVal = "+exitVal);

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line);
            }

        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return output.toString();

    }

}