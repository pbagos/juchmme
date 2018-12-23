package org.joone.engine;

import java.util.TreeSet;

/** This interface represents a generic element of a neural network
 *  @author: Paolo Marrone
 */
public interface NeuralElement {

    public boolean isEnabled();
    public void setEnabled(boolean enabled);
    
    /** Sets the Monitor object of the output synapse
     * @param newMonitor org.joone.engine.Monitor
     */
    public void setMonitor(Monitor newMonitor);

    /** Returns the monitor
     * @return org.joone.engine.Monitor
     */
    public Monitor getMonitor();

    /** Returns the name of the output synapse
     * @return String
     */
    public String getName();

    /** Sets the name of the output synapse
     * @param name String
     */
    public void setName(java.lang.String name);
    
    public void init();

    /**
     * Validation checks for invalid parameter values, misconfiguration, etc.
     * All network components should include a check method that firstly calls its ancestor check method and
     * adds these to any check messages it produces. This allows check messages to be collected from all levels
     * of a component to be returned to the caller's check method. Using a TreeSet ensures that
     * duplicate messages are removed. Check messages should be produced using the generateValidationErrorMessage
     * method of the NetChecker class.
     *
     * @return validation errors.
     */
    public TreeSet check();
}