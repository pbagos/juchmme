/*
 * NetErrorManager.java
 *
 */

package org.joone.engine;

import org.joone.engine.Monitor;

/**
 * <P>This class should be used when ever a critical error occurs that would impact on the training or running of the network.</P>
 * Joone classes should construct a new NetErrorManager when an error occurs.  By doing so this will stop and reset the network 
 * so the user can perform corrective action and re-start.
 * <P>E.g</P>
 * <P>new NetErrorManager(monitor,"An error has occurred.!");
 * <BR>
 * <P> The constructor of this class calls the monitors fireNetStoppedError event method which propogates the event to all the 
 * net listeners.  This in turn stops and resets the network to allow correction and continuation.
 * @author  Julien Norman
 */
public class NetErrorManager {
        
    /**
     * Constructor that stops and resets the neural network.
     * @param mon The monitor that should be made aware of the error.
     * @param errMsg The string containing the critical network error.
     */
    public NetErrorManager(Monitor mon, String errMsg) {
        if ( mon != null )
            mon.fireNetStoppedError(errMsg);    // Raises the error in the monitor.
    }
    
}
