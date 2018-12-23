package org.joone.engine;

/** This interface represents an input synapse for a generic layer.
 * @author: Paolo Marrone
 */
public interface InputPatternListener extends NeuralElement {

    /** Returns the pattern coming from the previous layer during the recall phase
     * @return neural.engine.Pattern
     */
    public Pattern fwdGet();
    
    public boolean isInputFull();
    public void setInputFull(boolean inputFull);

    /** Returns the dimension of the input synapse
     * @return int
     */
    public int getOutputDimension();

    /** Method to put an error pattern backward to the previous layer
     * @param pattern neural.engine.Pattern
     */
    public void revPut(Pattern pattern);

    /** Sets the dimension of the input synapse
     * @param newOutputDimension int
     */
    public void setOutputDimension(int newOutputDimension);

    
    /** reset of the input synapse
     */
    public void reset();

}