/*
 * AbstractEventNotifier.java
 *
 * Created on 31 gennaio 2003, 21.09
 */

package org.joone.engine;

/**
 * This class raises an event notification invoking the corrisponnding
 * Monitor.fireXXX method. The event is raised from within a separate
 * Thread to avoid the race conditions to happen
 *
 * @author  pmarrone
 */
public abstract class AbstractEventNotifier implements Runnable {
    
    protected Monitor monitor;
    private Thread myThread;
    
    /** Creates a new instance of AbstractEventNotifier */
    public AbstractEventNotifier(Monitor mon) {
        monitor = mon;
    }
    
    /**
     * The inherited classes must to override this method
     * invoking into it the desired monitor.fireXXX method
     */
    public abstract void run();
    
    public synchronized void start() {
        if (myThread == null) {
            myThread = new Thread(this);
            myThread.start();
        }
    }
    
}
