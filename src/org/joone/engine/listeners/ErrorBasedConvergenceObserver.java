/*
 * ErrorBasedConvergenceObserver.java
 *
 * Created on October 28, 2004, 3:06 PM
 */

package org.joone.engine.listeners;

import org.joone.engine.*;

/**
 * This observer observes if the network has convergenced based on the
 * sequence of training errors.
 *
 * @author  Boris Jansen
 */
public class ErrorBasedConvergenceObserver extends ConvergenceObserver {
    
    /** Whenever each training error in a sequence of errors decreases less than 
     * this percentage, a <code>ConvergenceEvent</code> will be generated. */
    private double percentage = -1;
    
    /** Sets the size of the cycles (sequence). Whenever the training error of a neural network
     * is smaller than a certain percentage for this number of epochs, the network is considered
     * as converged. */
    private int cycles = 5; // default
    
    /** Counter to check how many cycles the error decreases less than <code>percentage</code>. */
    private int cycleCounter = 0;
    
    /** Variable to remember the previous error. */
    private double lastError = -1;
    
    /** Creates a new instance of ErrorBasedConvergenceObserver */
    public ErrorBasedConvergenceObserver() {
    }
    
    /**
     * Sets the percentage. Whenever a neural network's training error doesn't decrease more
     * than this percentage for a couple of steps in a sequence of training errors, the network
     * is considered as converged.
     *
     * @param aPercentage the percentage to set.
     */
    public void setPercentage(double aPercentage) {
        percentage = aPercentage;
    }
    
    /**
     * Gets the percentage. 
     *
     * @return the percentage.
     */
    public double getPercentage() {
        return percentage;
    }
    
    /**
     * Sets the number of cycles. Whenever the training error of a network doesn't decrease more
     * than a percentage for this number of cycles, the network is considered as converged.
     *
     * @param aCylces
     */
    public void setCycles(int aCylces) {
        cycles = aCylces;
    }
    
    /**
     * Gets the number of cycles over which convergence is checked.
     *
     * @return the number of cycles.
     */
    public int getCycles() {
        return cycles;
    }
    
    protected void manageStop(Monitor mon) {
        
    }
    
    protected void manageCycle(Monitor mon) {
        
    }
    
    protected void manageStart(Monitor mon) {
        
    }
    
    protected void manageError(Monitor mon) {
        if(percentage < 0 || cycles <= 0) {
            return;
        }
        
        double myCurrentError = mon.getGlobalError();
        if(lastError >= 0) { // if lastError < 0, it is the first time and the lastError is unknown
            double myPercentage = (lastError - myCurrentError) * 100 / lastError;
            
            if(myPercentage <= percentage && myPercentage >= 0) {
                cycleCounter++;
            } else {
                disableCurrentConvergence = false; // we are not in a convergence state, so if we were
                                                   // we moved out of it
                cycleCounter = 0; // reset counter
            }
            
            if(cycleCounter == cycles) {
                if(!disableCurrentConvergence) {
                    fireNetConverged(mon);
                }
                cycleCounter = 0;
            }
        }
        lastError = myCurrentError;
    }

    protected void manageStopError(Monitor mon, String msgErr) {
        
    }
}
