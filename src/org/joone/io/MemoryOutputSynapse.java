/*
 * MemoryOutputSynapse.java
 *
 * Created on 6 maggio 2002, 21.45
 */

package org.joone.io;

import org.joone.engine.*;
import java.util.Vector;
import org.joone.log.*;
/**
 *
 * @author  pmarrone
 */
public class MemoryOutputSynapse extends StreamOutputSynapse {
    static final long serialVersionUID = 1342649629529016627L;
    
    /**
     * Logger
     * */
    private static final ILogger log = LoggerFactory.getLogger(Synapse.class);
    private Fifo patterns;
    private boolean zeroPattern = false;
        
    /** Creates a new instance of MemoryOutputSynapse */
    public MemoryOutputSynapse() {
    }
    
    /**
     * Returns all the patterns received in the last cycle
     * @return a vector containing the output patterns
     */
    private synchronized Vector getPatterns() {
        if (patterns == null)
            patterns = new Fifo();
        return (Vector)patterns.clone();
    }
    
    /**
     * This method waits for the zeroPattern and returns the last valid pattern received
     *
     * @return the last pattern received
     */
    public synchronized double[] getLastPattern() {
        Pattern pOut;
        // Wait until the 'stop pattern' is received
        while (!zeroPattern) {
            try {
                wait();
            } catch (InterruptedException e) { }
        }
        int size = patterns.size();
        zeroPattern = false;
        if (size > 0) {
            pOut = (Pattern)patterns.elementAt(size - 1);
            return pOut.getArray();
        }
        else
            return null;
    }
        
    /**
     * Waits for the next pattern and returns it
     *
     * @return the next pattern
     */
    public synchronized double[] getNextPattern() {
        while (getPatterns().isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) { }
        }
        Pattern pOut = (Pattern)patterns.pop();
        return pOut.getArray();
    }
    
    /**
     * Waits for the stopPattern and then returns all the patterns received in
     * the last cycle
     *
     * @return all the patterns received in the last cycle
     */
    public synchronized Vector getAllPatterns() {
        getLastPattern();
        return getPatterns();
    }
    
    /** Custom Synapses need to implement at least this method.  The custom synapse should write the pattern out to the
     * appropriate media.  All patterns including the ending pattern with pattern.getCount()=-1 will be passed to this method so that
     * custom output synapses can perform final processing.
     *
     */
    public synchronized void write(Pattern pattern) {
        count = pattern.getCount();
        if (count == 1) {
            patterns = new Fifo();
        }
        if ( count > -1)  // If we still going through patterns then simply add them to the memory buffer
        {
            patterns.push(pattern);
            zeroPattern = false;
        }
        else
            zeroPattern = true;
        notifyAll();
    }
    
}
