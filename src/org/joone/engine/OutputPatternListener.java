package org.joone.engine;

/** This interface represents an output synapse for a generic layer.
 *  @author: Paolo Marrone
 */
public interface OutputPatternListener extends NeuralElement {
    /** Method to put a pattern forward to the next layer
     * @param pattern neural.engine.Pattern
     */
    public void fwdPut(Pattern pattern);
    
    public boolean isOutputFull();
    public void setOutputFull(boolean outputFull);

    /** Returns the dimension of the output synapse
     * @return int
     */
    public int getInputDimension();

    /** Returns the error pattern coming from the next layer during the training phase
     * @return neural.engine.Pattern
     */
    public Pattern revGet();

    /** Sets the dimension of the output synapse
     * @param newOutputDimension int
     */
    public void setInputDimension(int newInputDimension);

}