/*
 * MemoryInputSynapse.java
 *
 * Created on 7 april 2002, 13.32
 */

package org.joone.io;

import org.joone.exception.JooneRuntimeException;
/**
 *
 * @author  pmarrone
 */
public class MemoryInputSynapse extends StreamInputSynapse {
    static final long serialVersionUID = 2066217695979040186L;
    private double[][] inputArray;
    /** Creates a new instance of MemoryInputSynapse */
    public MemoryInputSynapse() {
    }
    
    protected void initInputStream() throws JooneRuntimeException {
        super.setTokens(new MemoryInputTokenizer(inputArray));
    }
    
    /** Setter for property inputArray.
     * Use this method to initialize the input data
     * @param inputArray New value of property inputArray.
     */
    public void setInputArray(double[][] inputArray) {
        this.inputArray = inputArray;
        initInputStream();
    }
    
}
