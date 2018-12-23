/*
 * ComparingElement.java
 *
 * Created on 25 may 2003, 16.53
 */

package org.joone.engine.learning;
import org.joone.engine.*;
import org.joone.io.*;
import java.io.Serializable;
/**
 * This interface describes an element that can compare the output of the layer to which it 
 * is connected, with another input derivating from a StreamInputSynapse named 'desired'.
 * To elaborate the result of the comparison, attach to its output a whatever component
 * implementing the OutputPatternListener interface. (use addResultSynapse to do it).
 * Its main purpose is to describe the interface of a component used to teach the 
 * neural network, but it can be used whenever it's necessary to compare two patterns.
 * @author  pmarrone
 */
public interface ComparingElement extends OutputPatternListener, Serializable {

    /**
     * Getter for the desired data set
     */
    public StreamInputSynapse getDesired();
    /**
     * Setter for the desired data set
     */
    public boolean setDesired(StreamInputSynapse desired);
    
    /**
     * Adds an output synapse to which the result must be sent
     */    
    public boolean addResultSynapse(OutputPatternListener listener);
    /**
     * Removes an output synapse 
     */    
    public void removeResultSynapse(OutputPatternListener listener);
    
    /**
     * Returns the internal Layer used to transport the result to the connected output synapse
     */
    public LinearLayer getTheLinearLayer();
    
    /**
     * Resets the internal buffer of the desired StreamInputSynapse
     */
    public void resetInput();
}
