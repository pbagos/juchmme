package org.joone.io;

import java.util.*;
import java.io.*;

import org.joone.log.*;

public class StreamInputTokenizer implements PatternTokenizer {
    /**
     * Logger
     * */
    private static final ILogger log = LoggerFactory.getLogger(StreamInputTokenizer.class);
    
    private static final int MAX_BUFFER_SIZE = 1048576; // 1 MByte
    private LineNumberReader stream;
    private StringTokenizer tokenizer = null;
    private int numTokens = 0;
    private char m_decimalPoint = '.';
    private String m_delim = "; \t\n\r\f";
    private double[] tokensArray;
    private int maxBufSize;
    
    /** Creates new StreamInputTokenizer
     * @param in The input stream
     */
    public StreamInputTokenizer(Reader in) throws java.io.IOException {
        this(in, MAX_BUFFER_SIZE);
    }
    
    /** Creates new StreamInputTokenizer
     * @param in The input stream
     * @param maxBufSize the max dimension of the input buffer
     */
    public StreamInputTokenizer(Reader in, int maxBufSize) throws java.io.IOException {
        this.maxBufSize = maxBufSize;
        stream = new LineNumberReader(in, maxBufSize);
        stream.mark(maxBufSize);
    }
    
    
    /** Return the current line number.
     * @return  the current line number
     */
    public int getLineno() {
        return stream.getLineNumber();
    }
    public int getNumTokens() throws java.io.IOException {
        return numTokens;
    }
    /**
     * Insert the method's description here.
     * Creation date: (17/10/2000 0.30.08)
     * @return float
     * @param posiz int
     */
    public double getTokenAt(int posiz) throws java.io.IOException {
        if (tokensArray == null)
            if (!nextLine())
                return 0;
        if (tokensArray.length <= posiz)
            return 0;
        return tokensArray[posiz];
    }
    /**
     * Insert the method's description here.
     * Creation date: (17/10/2000 0.13.45)
     * @return float[]
     */
    public double[] getTokensArray() {
        return tokensArray;
    }
    /** mark the current position.
     */
    public void mark() throws java.io.IOException {
        stream.mark(maxBufSize);
    }
    /** Fetchs the next line and extracts all the tokens
     * @return false if EOF, otherwise true
     * @throws IOException if an I/O Error occurs
     */
    public boolean nextLine() throws java.io.IOException {
        String line = stream.readLine();
        if (line != null) {
            tokenizer = new StringTokenizer(line, m_delim, false);
            numTokens = tokenizer.countTokens();
            if (tokensArray == null)
                tokensArray = new double[numTokens];
            else
                if (tokensArray.length != numTokens)
                    tokensArray = new double[numTokens];
            for (int i = 0; i < numTokens; ++i)
                tokensArray[i] = nextToken(m_delim);
            return true;
        }
        else
            return false;
    }
    /** Return the next token's double value in the current line
     * @return the next double value
     */
    private double nextToken() throws java.io.IOException {
        return this.nextToken(null);
    }
    
    /** Return the next token's double value in the current line;
     * tokens are separated by the characters contained in delim
     * @return the next double value
     * @param delim String containing the delimitators characters
     */
    private double nextToken(String delim) throws java.io.IOException {
        double v;
        String nt = null;
        
        if (tokenizer == null)
            nextLine();
        if (delim != null)
            nt = tokenizer.nextToken(delim);
        else
            nt = tokenizer.nextToken();
        
        if (m_decimalPoint != '.')
            nt = nt.replace(m_decimalPoint, '.');
        try {
            v = Double.valueOf(nt).floatValue();
        } catch (NumberFormatException nfe) {
            log.warn("Warning: Not numeric value at row "+getLineno()+": <" + nt + ">");
            v = 0;
        }
        return v;
    }
    /** Go to the last marked position. Begin of input stream if no mark detected.
     */
    public void resetInput() throws java.io.IOException {
        stream.reset();
        tokenizer = null;
    }
    public void setDecimalPoint(char dp) {
        m_decimalPoint = dp;
    }
    
    public char getDecimalPoint() {
        return m_decimalPoint;
    }
    
}