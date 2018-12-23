package org.joone.io;

/** Interface to extract tokens from an input stream
 */
public interface PatternTokenizer {
    /*** Return the current line number.
     * @return  the current line number
     */
    public int getLineno();
    public int getNumTokens() throws java.io.IOException;
    /**
     * Returns the value of the token at 'posiz' column of the current line
     * Creation date: (17/10/2000 0.30.08)
     * @return float
     * @param posiz int
     */
    public double getTokenAt(int posiz) throws java.io.IOException;
    /**
     * Returns an array of values of the current line
     * Creation date: (17/10/2000 0.13.45)
     * @return float[]
     */
    public double[] getTokensArray();
    /** marks the current position.
     * @throws IOException if an I/O Error occurs
     */
    public void mark() throws java.io.IOException;
    /** Go to the next line
     * @return false if EOF, otherwise true
     * @throws IOException if an I/O Error occurs
     */
    public boolean nextLine() throws java.io.IOException;
    /** Go to the last marked position. Begin of input stream if no mark detected.
     */
    public void resetInput() throws java.io.IOException;
    
    public char getDecimalPoint();
    
    public void setDecimalPoint(char decimalPoint);
}