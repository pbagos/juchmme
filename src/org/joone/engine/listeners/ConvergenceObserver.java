/*
 * ConvergenceObserver.java
 *
 * Created on October 28, 2004, 3:21 PM
 */

package org.joone.engine.listeners;

import java.util.*;
import org.joone.engine.Monitor;
import org.joone.util.MonitorPlugin;

/**
 * Abstract class for all convergence observer.
 *
 * @author  Boris Jansen
 */
public abstract class ConvergenceObserver extends MonitorPlugin {
    
    /** The next flag indicates if the current convegence should be neglected. This
     * is used in situations where convergence was reached, but the network continues
     * running. If we would not neglect the current convergence event would continue
     * to be generated. This flag is used to disable the event sfor the current convergence. */
    protected boolean disableCurrentConvergence = false;
    
    /** List of <code>ConvergenceListener</code>s. */
    private List listeners = new ArrayList();
    
    /** Creates a new instance of ConvergenceObserver */
    public ConvergenceObserver() {
    }
    
    /**
     * Adds a convergence listener.
     *
     * @param aListener the listener to add.
     */
    public void addConvergenceListener(ConvergenceListener aListener) {
        if(!listeners.contains(aListener)) {
            listeners.add(aListener);
        }
    }
    
    /**
     * Removes a convergence listener.
     *
     * @param aListener the listener to remove.
     */
    public void removeConvergenceListener(ConvergenceListener aListener) {
        listeners.remove(aListener);
    }
    
    /**
     * Fires a net converged event.
     *
     * @param aMonitor a monitor object.
     */
    protected void fireNetConverged(Monitor aMonitor) {
        Object[] myList;
        synchronized (this) {
            myList = listeners.toArray();
        }
        
        ConvergenceEvent myEvent = new ConvergenceEvent(aMonitor);
        for (int i = 0; i < myList.length; ++i) {
            ((ConvergenceListener)myList[i]).netConverged(myEvent, this);
        }
    }
    
    /**
     * Disables current convergence events. Used in situations where convergence 
     * was reached but the network keeps running. By calling this method no events
     * signaling convergence was reached will be greated. Whenever the network
     * moves out of the convergence state, new events will be created again once
     * the system reaches convergence.
     */
    public void disableCurrentConvergence() {
        disableCurrentConvergence = true;
    }
}
