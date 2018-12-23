/*
 * ConvergenceEvent.java
 *
 * Created on October 28, 2004, 3:13 PM
 */

package org.joone.engine.listeners;

import org.joone.engine.Monitor;

/**
 * This event will be generated whenever convergence has reached according to 
 * some criteria.
 *
 * @author  Boris Jansen
 */
public class ConvergenceEvent extends java.util.EventObject {
    
    /** 
     * Creates a new instance of ConvergenceEvent
     *
     * @param aSource the source that caused this event.
     */
    public ConvergenceEvent(Monitor aSource) {
        super(aSource);
    }
    
}
