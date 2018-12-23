/*
 * Connection.java
 *
 * Created on 21 gennaio 2004, 16.39
 */

package org.joone.helpers.structure;
import org.joone.engine.*;
/**
 * This class represents a container for a Synapse during
 * the process of transforming a neural network to a 
 * suitable form for the XML serialization. 
 *
 * @see org.joone.joonepad.NeuralNetMatrix
 * @author  P.Marrone
 */
public class Connection {
    Synapse synapse;
    int input;
    int output;
    int inpIndex, outIndex;
    
    public Connection() {
    }
    
    /** Getter for property input.
     * @return Value of property input.
     *
     */
    public int getInput() {
        return input;
    }
    
    /** Setter for property input.
     * @param input New value of property input.
     *
     */
    public void setInput(int input) {
        this.input = input;
    }
    
    /** Getter for property output.
     * @return Value of property output.
     *
     */
    public int getOutput() {
        return output;
    }
    
    /** Setter for property output.
     * @param output New value of property output.
     *
     */
    public void setOutput(int output) {
        this.output = output;
    }
    
    /** Getter for property inpIndex.
     * @return Value of property inpIndex.
     *
     */
    public int getInpIndex() {
        return inpIndex;
    }
    
    /** Setter for property inpIndex.
     * @param inpIndex New value of property inpIndex.
     *
     */
    public void setInpIndex(int inpIndex) {
        this.inpIndex = inpIndex;
    }
    
    /** Getter for property outIndex.
     * @return Value of property outIndex.
     *
     */
    public int getOutIndex() {
        return outIndex;
    }
    
    /** Setter for property outIndex.
     * @param outIndex New value of property outIndex.
     *
     */
    public void setOutIndex(int outIndex) {
        this.outIndex = outIndex;
    }
    
    /** Getter for property synapse.
     * @return Value of property synapse.
     *
     */
    public Synapse getSynapse() {
        return synapse;
    }
    
    /** Setter for property synapse.
     * @param synapse New value of property synapse.
     *
     */
    public void setSynapse(Synapse synapse) {
        this.synapse = synapse;
    }
    
}
