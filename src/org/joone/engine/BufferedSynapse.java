package org.joone.engine;

/** This class implements a synapse that permits to have asynchronous
 * methods to write output patterns.
 * The <CODE>fwdPut</CODE> method, infact, uses a FIFO structure to
 * store the patterns and to separate the writing from the reading layers.
 */
public class BufferedSynapse extends Synapse {
    private transient Fifo fifo;
    
    private static final long serialVersionUID = -8067243400677466498L;
    
    /** BufferedOutputSynapse constructor.
     */
    public BufferedSynapse() {
        super();
    }
    /**
     */
    protected void backward(double[] pattern) {
        // Not used
    }
    /**
     */
    protected void forward(double[] pattern) {
        outs = pattern;
    }
    /** Return the first element of the FIFO structure, if exists.
     * @return Pattern
     */
    public Pattern fwdGet() {
        Pattern pat;
        synchronized (getFwdLock()) {
            while (items == 0) {
                try {
                    fwdLock.wait();
                } catch (InterruptedException e) {
                    return null;
                }
            }
            pat = (Pattern)fifo.pop();
            items = fifo.size();
            fwdLock.notifyAll();
            return pat;
        }
    }
    /** Writes the input pattern into the FIFO structure.
     * The layer that calls this methos will wait only
     * the time needed to put the input data into the pipeline.
     * @param pattern The Pattern object to write in the FIFO structure
     */
    public synchronized void fwdPut(Pattern pattern) {
        m_pattern = pattern;
        inps = pattern.getArray();
        forward(inps);
        m_pattern.setArray(outs);
        fifo.push(m_pattern);
        items = fifo.size();
        notifyAll();
    }
    /** Not used
     * @return Pattern
     */
    public synchronized Pattern revGet() {
        // Not used
        return null;
    }
    /** Not used
     * @param pattern
     */
    public synchronized void revPut(Pattern pattern) {
        // Not used
    }
    /**
     * setArrays method comment.
     */
    protected void setArrays(int rows, int cols) {}
    /**
     * setDimensions method comment.
     */
    protected void setDimensions(int rows, int cols) {}
}