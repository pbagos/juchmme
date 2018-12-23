/*
 * ConvergenceListener.java
 *
 * Created on October 28, 2004, 3:16 PM
 */

package org.joone.engine.listeners;

/**
 * Listens for convergence events.
 *
 * @author  Boris Jansen
 */
public interface ConvergenceListener extends java.util.EventListener {
    
    /**
     * This method is called whenever the network has converged according to some
     * <code>ConvergenceObserver</code>
     *
     * @param anEvent the event that is generated.
     * @param anObserver the observer that generated the event.
     */
    void netConverged(ConvergenceEvent anEvent, ConvergenceObserver anObserver);
    
}
