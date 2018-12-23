/*
 * CSVParser.java
 *
 * Created on 21 feb 2003, 21.23
 */

package org.joone.util;

import java.util.*;
import org.joone.log.*;
/** Comma Separated Values Parser
 * This helper class parses a string containing comma separated tokens.
 * Each token can contain a single value or a range represented by two values
 * separated by a separator.
 * @author pmarrone
 */
public class CSVParser {
    
    private String m_values;
    private boolean range_allowed = true;
    private static final ILogger log = LoggerFactory.getLogger( CSVParser.class );
    private static final char RANGE_SEPARATOR = '-';
    
    /** Creates a new instance of CSVParser
     * @param values The string containing the values to parse
     */
    public CSVParser(String values) {
        this(values, true);
    }
    
    /** Creates a new instance of CSVParser
     * @param values The string containing the values to parse
     * @param range true (default) if ranges allowed
     */
    public CSVParser(String values, boolean range) {
        m_values = values;
        range_allowed = range;
    }
    
    /** Parse the string and returns an array containing all the values encountered.
     * Let we have a string containing:
     * '1,3-5,8,10-12'
     * The method parseInt() will return an array containing:
     * [1,3,4,5,8,10,11,12]
     * WARNING: A RANGE CANNOT CONTAIN NEGATIVE NUMBERS
     * @return an array of integer containing all the values parsed
     */
    public int[] parseInt() throws NumberFormatException {
        int[] ivalues = null;
        Vector values = new Vector();
        StringTokenizer tokens = new StringTokenizer(m_values, ",");
        while (tokens.hasMoreTokens()) {
            String tk = tokens.nextToken().trim();
            int rpos = tk.indexOf(RANGE_SEPARATOR);
            if (rpos <= 0) // not a range or negative number
                try {
                    values.add(new Integer(Integer.parseInt(tk)));
                }
                catch (NumberFormatException nfe) {
                    String error = "Error parsing '"+m_values+"' : '"+tk+"' is not a valid integer value";
                    throw new NumberFormatException(error);
                }
            else {
                if (range_allowed) {
                    String tkl = tk.substring(0, rpos);
                    String tkr = tk.substring(rpos+1);
                    try {
                        int iv = Integer.parseInt(tkl);
                        int fv = Integer.parseInt(tkr);
                        if (iv > fv) {
                            int ii = fv;
                            fv = iv;
                            iv = ii;
                        }
                        for (int i=iv; i <= fv; ++i)
                            values.add(new Integer(i));
                    }
                    catch (NumberFormatException nfe) {
                        String error = "Error parsing '"+m_values+"' : '"+tk+"' contains not valid integer values";
                        throw new NumberFormatException(error);
                    }
                }
                else {
                    String error = "Error parsing '"+m_values+"' : range not allowed";
                    throw new NumberFormatException(error);
                }
            }
        }
        ivalues = new int[values.size()];
        for (int v=0; v < values.size(); ++v)
            ivalues[v] = ((Integer)values.elementAt(v)).intValue();
        return ivalues;
    }
    
    /** Parse the string and returns an array containing all the values encountered.
     * Let we have a string containing:
     * '1.0,-1.0,0.0'
     * The method parseDouble() will return an array containing:
     * [1.0,-1.0,0.0]
     * WARNING: RANGE NOT ALLOWED, AS IT MAKES NO SENSE IN THIS CASE
     * @return an array of double containing all the values parsed
     */
    public double[] parseDouble() throws NumberFormatException {
        double[] dvalues = null;
        Vector values = new Vector();
        StringTokenizer tokens = new StringTokenizer(m_values, ",");
        while (tokens.hasMoreTokens()) {
            String tk = tokens.nextToken().trim();
            int rpos = tk.indexOf(RANGE_SEPARATOR);
            if (rpos <= 0) // not a range or negative number
                try {
                    values.add(new Double(Double.parseDouble(tk)));
                }
                catch (NumberFormatException nfe) {
                    String error = "Error parsing '"+m_values+"' : '"+tk+"' is not a valid numeric value";
                    throw new NumberFormatException(error);
                }
            else {
                String error = "Error parsing '"+m_values+"' : range not allowed for not integer values";
                throw new NumberFormatException(error);
            }
        }
        dvalues = new double[values.size()];
        for (int v=0; v < values.size(); ++v)
            dvalues[v] = ((Double)values.elementAt(v)).doubleValue();
        return dvalues;
    }
    
    /** Test
     */
    public static void main(String args[]) {
        CSVParser parser = new CSVParser("1.0,-3.6,1.4,15");
        double[] dd = parser.parseDouble();
        log.debug("Double values:");
        if (dd != null)
            for (int i=0; i < dd.length; ++i)
                log.debug("array["+i+"] = "+dd[i]);
        
        parser = new CSVParser("1,-3,4-8,11");
        int[] ii = parser.parseInt();
        log.debug("Integer values:");
        if (ii != null)
            for (int i=0; i < ii.length; ++i)
                log.debug("array["+i+"] = "+ii[i]);
    }
}
