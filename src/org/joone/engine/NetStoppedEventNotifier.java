/*
 * netStoppedEventNotifier.java
 *
 * Created on 31 gennaio 2003, 21.19
 */

package org.joone.engine;

/**
 * Raises the netStopped event from within a separate Thread
 * @author  root
 */
public class NetStoppedEventNotifier extends AbstractEventNotifier {
    
    /** Creates a new instance of netStoppedEventNotifier */
    public NetStoppedEventNotifier(Monitor mon) {
        super(mon);
    }
    
    /**
     * Raises the netStopped event
     *
     */
    public void run() {
        if (monitor != null)
            monitor.fireNetStopped();
    }
    
}
