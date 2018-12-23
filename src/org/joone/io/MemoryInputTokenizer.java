/*
 * MemoryInputTokenizer.java
 *
 * Created on 7 april 2002, 13.37
 */

package org.joone.io;

/**
 *
 * @author  pmarrone
 */
public class MemoryInputTokenizer implements PatternTokenizer {
    
    private double[][] inputArray;
    private int lineNo = 0;
    private int mark = 0;
    private char decimalPoint;
    
    /** Creates a new instance of MemoryInputTokenizer */
    public MemoryInputTokenizer() {
    }
    
    public MemoryInputTokenizer(double[][] array) {
        inputArray = array;
    }
    
    /** Go to the last marked position. Begin of input stream if no mark detected.
     */
    public void resetInput() throws java.io.IOException {
        lineNo = mark;
    }
    
    /** Return the current line number.
     * @return  the current line number
     */
    public int getLineno() {
        return lineNo;
    }
    
    public char getDecimalPoint() {
        return decimalPoint;
    }
    
    /** Go to the next line
     * @return false if EOF, otherwise true
     * @throws IOException if an I/O Error occurs
     */
    public boolean nextLine() throws java.io.IOException {
        ++lineNo;
        if (lineNo > inputArray.length)
            return false;
        else
            return true;
    }
    
    public int getNumTokens() throws java.io.IOException {
        double[] line = this.getTokensArray();
        return line.length;
    }
    
    /** marks the current position.
     * @throws IOException if an I/O Error occurs
     */
    public void mark() throws java.io.IOException {
        mark = lineNo;
    }
    
    /**
     * Returns the value of the token at 'posiz' column of the current line
     * Creation date: (17/10/2000 0.30.08)
     * @return float
     * @param posiz int
     */
    public double getTokenAt(int posiz) throws java.io.IOException {
        double[] line = this.getTokensArray();
        return line[posiz];
        
    }
    
    /**
     * Returns an array of values of the current line
     * Creation date: (17/10/2000 0.13.45)
     * @return float[]
     */
    public double[] getTokensArray() {
        return inputArray[lineNo - 1];
    }
    
    public void setDecimalPoint(char decimalPoint) {
        this.decimalPoint = decimalPoint;
    }
    
}
