/*
 * ErrorBasedTerminator.java
 *
 * Created on October 28, 2004, 2:45 PM
 */

package org.joone.engine.listeners;

import org.joone.engine.*;
import org.joone.util.MonitorPlugin;

/**
 * Stops a network whenever the training error of the network falls below a 
 * certain value.
 *
 * @author  Boris Jansen
 */
public class ErrorBasedTerminator extends MonitorPlugin {
    
    /** The error level. Whenever the training error falls below this value 
     * the network should be stopped. */
    private double errorLevel;
    
    /** The cycle the network was stopped. */
    private int stoppedCycle = -1;
    
    /** Has a stop request performed. */
    private boolean stopRequested = false;
    
    /** Creates a new instance of ErrorBasedTerminator */
    public ErrorBasedTerminator() {
    }
    
    /** 
     * Creates a new instance of ErrorBasedTerminator
     *
     * @param anErrorLevel the error level. A network having a training error
     * equal to or below this level will be stopped.
     */
    public ErrorBasedTerminator(double anErrorLevel) {
        errorLevel = anErrorLevel;
    }
    
    /**
     * Sets the error level. A network having a training error
     * equal to or below this level will be stopped.
     *
     * @param anErrorLevel the error level to set.
     */
    public void setErrorLevel(double anErrorLevel) {
        errorLevel = anErrorLevel;
    }
    
    /**
     * Gets the error level.
     *
     * @return the error level.
     */
    public double getErrorLevel() {
        return errorLevel;
    }

    protected void manageStop(Monitor mon) {
        
    }
    
    protected void manageCycle(Monitor mon) {

    }
    
    protected void manageStart(Monitor mon) {
        stoppedCycle = -1;
        stopRequested = false;
    }
    
    protected void manageError(Monitor mon) {
        if(mon.getGlobalError() <= errorLevel) {
            if(!isStopRequestPerformed()) {
                stoppedCycle = mon.getTotCicles() - mon.getCurrentCicle() + 1;
                stopRequested = true;
            }
            getNeuralNet().stop();
        }
    }
    
    protected void manageStopError(Monitor mon, String msgErr) {
        
    }
    
    /**
     * Gets the cycle the network was stopped. 
     *
     * @return the cycle the network was stopped or -1 if the network hasn't been 
     * stopped since it is (re)started.
     */
    public int getStoppedCycle() {
        return stoppedCycle;
    }
    
    /**
     * Checks if this object requested / stopped the neural network.
     *
     * @return <code>true</code> if this object requested the stop of the network
     * since it has been started, <code>false</code> otherwise.
     */
    public boolean isStopRequestPerformed() {
        return stopRequested;
    }
    
}
