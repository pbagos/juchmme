package org.joone.engine.learning;

import org.joone.log.*;
import org.joone.engine.*;
import org.joone.io.*;
import org.joone.net.NetCheck;

import java.io.IOException;
import java.util.TreeSet;


/**
 * Final element of a neural network; it permits to compare
 * the outcome of the neural net and the input patterns
 * from a StreamInputSynapse connected to the 'desired'
 * property. Used by the ComparingSynapse object.
 */
public class ComparisonSynapse extends Synapse {
    /**
     * Logger
     * */
    private static final ILogger log = LoggerFactory.getLogger(TeacherSynapse.class);
    
    private StreamInputSynapse desired;
    protected transient Fifo fifo;
    protected transient boolean firstTime = true;
    
    private static final long serialVersionUID = -1301682557631180066L;
    
    public ComparisonSynapse() {
        super();
        firstTime = true;
    }
    
    protected void backward(double[] pattern) {
        // Not used.
    }
    
    protected void forward(double[] pActual) {
        Pattern pattDesired;
        double[] pTarget;
        int x;
        if ((m_pattern.getCount() == 1) || (m_pattern.getCount() == -1)) {
            try {
                desired.gotoFirstLine();
            } catch (IOException ioe) {
                log.warn("IOException while forwarding the influx. Message is : " + ioe.getMessage(),
                ioe);
            }
        }
        if (m_pattern.getCount() == -1) {
            stopTheNet();
            return;
        }
        firstTime = false;
        outs = new double[pActual.length];
        pattDesired = desired.fwdGet();
        if (m_pattern.getCount() != pattDesired.getCount()) {
            new NetErrorManager(getMonitor(),"ComparisonSynapse: No matching patterns - input#" + m_pattern.getCount() + " desired#" + pattDesired.getCount());
            return;
        }
        pTarget = pattDesired.getArray();
        if (pTarget != null) {
            outs = new double[getOutputDimension()];
            int i,n;
            for (i=0, n=0; i < pActual.length; ++i,++n) {
                outs[n] = pActual[i];
            }
            for (i=0; i < pTarget.length; ++i,++n) {
                outs[n] = pTarget[i];
            }
            pushValue(outs, m_pattern.getCount());
        }
    }
    
    protected void stopTheNet() {
        pushStop();
        firstTime = true;
    }
    
    public Pattern fwdGet() {
        synchronized (this) {
            while (getFifo().empty()) {
                try {
                    wait();
                } catch (InterruptedException ie) {   //e.printStackTrace();
                    log.warn("wait() was interrupted. Message is : " + ie.getMessage());
                    return null;
                }
            }
            Pattern errPatt = (Pattern)fifo.pop();
            notifyAll();
            return errPatt;
        }
    }
    
    public void fwdPut(Pattern pattern) {
        int step = pattern.getCount();
        if (!isEnabled()) {
            if (step == -1)
                stopTheNet();
            return;
        }
        super.fwdPut(pattern);
        items = 0;
    }
    
    /**
     * Inserire qui la descrizione del metodo.
     * Data di creazione: (11/04/00 1.12.04)
     * @return neural.engine.StreamInputSynapse
     */
    public StreamInputSynapse getDesired() {
        return desired;
    }
    
    /**
     * Insert the method's description here.
     * Creation date: (23/09/2000 2.16.17)
     * @return neural.engine.Fifo
     */
    private Fifo getFifo() {
        if (fifo == null)
            fifo = new Fifo();
        return fifo;
    }
        
    public Pattern revGet() {
        return null;
    }
    
    public void revPut(Pattern pattern) {
        // Not used.
    }
    
    /**
     * setArrays method comment.
     */
    protected void setArrays(int rows, int cols) {
    }
    
    /**
     * Set the input data stream containing desired training data
     * @param newDesired neural.engine.StreamInputSynapse
     */
    public boolean setDesired(StreamInputSynapse newDesired) {
        if (newDesired == null) {
            if (desired != null)
                desired.setInputFull(false);
            desired = newDesired;
        }
        else {
            if (newDesired.isInputFull())
                return false;
            desired = newDesired;
            desired.setStepCounter(false);
            desired.setOutputDimension(getInputDimension());
            desired.setInputFull(true);
        }
        return true;
    }
    
    public void resetInput() {
        if (getDesired() != null)
            getDesired().resetInput();
    }
    
    protected void setDimensions(int rows, int cols) {
    }
    
    public void setInputDimension(int newInputDimension) {
        super.setInputDimension(newInputDimension);
        if (getDesired() != null)
            getDesired().setOutputDimension(newInputDimension);
    }
        
    public TreeSet check() {
        TreeSet checks = super.check();
        
        if (desired == null) {
            checks.add(new NetCheck(NetCheck.FATAL, "Desired Input has not been set.", this));
        }
        else
            checks.addAll(desired.check());
        
        
        return checks;
    }
    
    /** reset of the input synapse
     *
     */
    public void reset() {
        super.reset();
        if (getDesired() != null)
            getDesired().reset();
    }
    
    /** Sets the Monitor object of the Teacher Synapse.
     * Adds this Techer Synapse as a NeuralNetListener so that it can reset after a critical error.
     * @param newMonitor neural.engine.Monitor
     */
    public void setMonitor(Monitor newMonitor) {
        super.setMonitor(newMonitor);
        if (getMonitor() != null) {
            this.getMonitor().setSupervised(true);
        }
    }
    
    public void netStoppedError(NeuralNetEvent e, String error) {
        pushStop();
        firstTime = true;
        this.reset();
    }
    
    private void pushStop() {
        double[] arr = new double[getOutputDimension()];
        pushValue(arr, -1);
    }
    
    private void pushValue(double[] arr, int count) {
        Pattern patt = new Pattern(arr);
        patt.setCount(count);
        synchronized (this) {
            getFifo().push(patt);
            notify();
        }
    }
    
    /** Returns the output dimension of the synapse.
     * @return int
     *
     */
    public int getOutputDimension() {
        return getInputDimension() * 2;
    }
    
    public void init() {
        super.init();
        if (getDesired() != null)
            getDesired().init();
    }
    
}