/*
 * AbstractTeacherSynapse.java
 *
 * Created on February 26, 2005, 3:51 PM
 */

package org.joone.engine.learning;

import org.joone.log.*;
import org.joone.engine.*;
import org.joone.io.*;
import org.joone.net.NetCheck;

import java.io.*;
import java.util.TreeSet;

/**
 * This class provides a framework to extend in order to implement various teachers,
 * just by overriding or implementing certain functions different functionality can
 * easily implemented.
 *
 * @author Boris Jansen
 */
public abstract class AbstractTeacherSynapse extends Synapse {
    private static final long serialVersionUID = -3501303723175798936L;
    // Developer note:
    // ---------------
    // Basically almost every code from TeacherSynapse is moved to this class and some functions
    // are split up in smaller functions. Whenever you (preferable a Joone developer) want to
    // implement a new teacher a certain methods need to be called based on certain events or
    // states, then add an abstract function to be called here and implement it in your teacher.
    // This way the AbstractTeacherSynapse will become more abstract, creating a framework that
    // enables the creation of more various teachers.
    
    /**
     * Logger
     **/
    protected static final ILogger log = LoggerFactory.getLogger(AbstractTeacherSynapse.class);
    
    /** First time data is passed to this teacher? */
    private transient boolean firstTime = true;
    
    /** Number of patterns seen during the current epoch. */
    private transient int patterns = 0;
    
    /** The stream from where to read the desired input. */
    protected StreamInputSynapse desired;
    
    /** Into this FIFO (first-in-first-out) object, the calculated error (e.g. RMSE) after
     * an epoch will be pushed. This way an (external) application/component is able to read
     * the errors at any moment, providing a loose-coupling mechanism.
     */
    protected transient Fifo error;
    
    /** Creates a new instance of AbstractTeacherSynapse */
    public AbstractTeacherSynapse() {
        super();
        setFirstTime(true);
    }
    
    /**
     * Sets the first time flag (is it the first time data is forwarded to this teacher).
     *
     * @param aValue value for the first time flag.
     */
    protected void setFirstTime(boolean aValue) {
        firstTime = aValue;
    }
    
    /**
     * Checks whether it is the first time data is passed to this teacher or not.
     *
     * @return <code>true</code> if it is the first time data is passed to this teacher,
     * <code>false</code> otherwise.
     */
    protected boolean isFirstTime() {
        return firstTime;
    }
    
    protected void backward(double[] pattern) {
        // Not used.
    }
    
    /**
     * Pushes the calculated array in the FIFO queue at the end of a
     * epoch, that is after all patterns have been seen.
     *
     * @param error, the calculated error.
     * @param count, the cycle of the calculated error.
     */
    protected void pushError(double error, int count) {
        double[] cost = new double[1];
        cost[0] = error;
        Pattern ptnErr = new Pattern(cost);
        ptnErr.setCount(count);
        synchronized (this) {
            getError().push(ptnErr);
            notify();
        }
    }
    
    /**
     * Gets the object holding the errors.
     *
     * @return the FIFO object holding the errors.
     */
    private Fifo getError() {
        if (error == null) {
            error = new Fifo();
        }
        return error;
    }
    
    protected void stopTheNet() {
        pushError(0.0, -1);
        patterns = 0;
        setFirstTime(true);
        if(getMonitor() != null) {
            new NetStoppedEventNotifier(getMonitor()).start();
        }
    }
    
    /**
     * Get the value of the number of patterns seen during the current epoch.
     *
     * @return the patterns seen during the current epoch.
     */
    protected int getSeenPatterns() {
        return patterns;
    }
    
    /**
     * Set the value of the number of patterns seen during the current epoch.
     *
     * @param aValue the new value for the number of patterns seen during the
     * current epoch.
     */
    protected void setSeenPatterns(int aValue) {
        patterns = aValue;
    }
    
    /**
     * Increases the number of seen patterns by one.
     */
    protected void incSeenPatterns() {
        patterns++;
    }
    
    /**
     * Here, it forwards (returns) the pushed error (in FIFO order).
     *
     * @return the pattern holding the error of the network.
     * {@link Synapse#fwdGet()
     */
    public Pattern fwdGet() {
        synchronized (this) {
            while (getError().empty()) {
                try {
                    wait();
                } catch (InterruptedException ie) {
                    //e.printStackTrace();
                    //log.warn("wait() was interrupted. Message is : " + ie.getMessage());
                    return null;
                }
            }
            Pattern errPatt = (Pattern) error.pop();
            notify();
            return errPatt;
        }
    }
    
    /**
     * Gets the stream to read the desired output.
     *
     * @return the desired output stream.
     */
    public StreamInputSynapse getDesired() {
        return desired;
    }
    
    /**
     * Set the input data stream containing desired training data.
     *
     * @param newDesired the stream from where to read the desired output.
     */
    public boolean setDesired(StreamInputSynapse newDesired) {
        if(newDesired == null) {
            if (desired != null) {
                desired.setInputFull(false);
            }
            desired = newDesired;
        } else {
            if (newDesired.isInputFull()) {
                return false;
            }
            desired = newDesired;
            desired.setStepCounter(false);
            desired.setOutputDimension(getInputDimension());
            desired.setInputFull(true);
        }
        return true;
    }
    
    protected Object readResolve() {
        super.readResolve();
        setFirstTime(true);
        if (getMonitor()!= null) {
            getMonitor().setSupervised(true);
        }
        return this;
    }
    
    protected void setArrays(int rows, int cols) {
    }
    
    protected void setDimensions(int rows, int cols) {
    }
    
    public void setInputDimension(int newInputDimension) {
        super.setInputDimension(newInputDimension);
        if (getDesired() != null) {
            getDesired().setOutputDimension(newInputDimension);
        }
    }
    
    /**
     * Reset the input and desired synapses
     */
    public void reset() {
        super.reset();
        setSeenPatterns(0);
        setFirstTime(true);
        if (getDesired() != null) {
            getDesired().reset();
        }
    }
    
    public void resetInput() {
        if(getDesired() != null) {
            getDesired().resetInput();
        }
    }
    
    /**
     * Sets the Monitor object of the Teacher Synapse.
     *
     * @param newMonitor neural.engine.Monitor
     */
    public void setMonitor(Monitor newMonitor) {
        super.setMonitor(newMonitor);
        
        if(getMonitor() != null) {
            this.getMonitor().setSupervised(true);
        }
    }
    
    public void netStoppedError() {
        pushError(0.0, -1);
        setSeenPatterns(0);
        setFirstTime(true);
        reset();
    }
    
    public void init() {
        super.init();
        setSeenPatterns(0);
        setFirstTime(true);
        if(getDesired() != null) {
            getDesired().init();
        }
    }
    
    public TreeSet check() {
        TreeSet checks = super.check();
        
        if(getDesired() == null) {
            checks.add(new NetCheck(NetCheck.FATAL, "Desired Input has not been set.", this));
        } else {
            checks.addAll(getDesired().check());
        }
        return checks;
    }
    
    
    public void revPut(Pattern pattern) {
        // Not used.
    }
    
    public Pattern revGet() {
        if (isEnabled()) {
            return super.fwdGet();
        } else {
            return null;
        }
    }
    
    public void fwdPut(Pattern pattern) {
        int step = pattern.getCount();
        
        if (!getMonitor().isSingleThreadMode()) {
            if((getMonitor() == null) || (!isEnabled())) {
                if (step == -1) {
                    stopTheNet();
                }
                return;
            }
        }
        
        super.fwdPut(pattern);
        
        if (step != -1) {
            if (!getMonitor().isLearningCicle(step)) {
                items = 0;
            }
        } else {
            items = 0;
        }
    }
    
    protected void forward(double[] pattern) {
        Pattern pattDesired;
        double[] pDesired;
        double myGlobalError; // error at the end of an epoch
        
        if ((m_pattern.getCount() == 1) || (m_pattern.getCount() == -1)) {
            // new epoch / end of previous epoch
            try {
                desired.gotoFirstLine();
                if((!isFirstTime()) && (getSeenPatterns() == getMonitor().getNumOfPatterns())) {
                    myGlobalError = calculateGlobalError();
                    pushError(myGlobalError, getMonitor().getTotCicles() - getMonitor().getCurrentCicle());
                    getMonitor().setGlobalError(myGlobalError);
                    epochFinished();
                    setSeenPatterns(0);
                }
            } catch (IOException ioe) {
                new NetErrorManager(getMonitor(),"TeacherSynapse: IOException while forwarding the influx. Message is : " + ioe.getMessage());
                return;
            }
        }
        if (m_pattern.getCount() == -1) {
            if (!getMonitor().isSingleThreadMode()) {
                stopTheNet();
            } else {
                pushError(0.0, -1);
            }
            return;
        }
        setFirstTime(false);
        outs = new double[pattern.length];
        pattDesired = desired.fwdGet();
        if (m_pattern.getCount() != pattDesired.getCount()) {
            try {
                desired.gotoLine(m_pattern.getCount());
                pattDesired = desired.fwdGet();
                if (m_pattern.getCount() != pattDesired.getCount()) {
                    new NetErrorManager(getMonitor(),"TeacherSynapse: No matching patterns - input#" + m_pattern.getCount() + " desired#" + pattDesired.getCount());
                    return;
                }
            } catch (IOException ioe) {
                new NetErrorManager(getMonitor(),"TeacherSynapse: IOException while forwarding the influx. Message is : " + ioe.getMessage());
                return;
            }
        }
        
        // The error calculation starts from the preLearning+1 pattern
        if (getMonitor().getPreLearning() < m_pattern.getCount()) {
            pDesired = pattDesired.getArray();
            if (pDesired != null) {
                if(pDesired.length != outs.length) {
                    // if the desired output differs in size, we will back propagate
                    // an pattern of the same size as the desired output so the output
                    // layer will adjust its size. The error pattern will contain zero
                    // values so no learning takes place during this backward pass.
                    log.warn("Size output pattern mismatches size desired pattern." +
                            " Zero-valued desired pattern sized error pattern will be backpropagated.");
                    outs = new double[pDesired.length];
                } else {
                    constructErrorPattern(pDesired, pattern);
                }
            }
        }
        incSeenPatterns();
    }
    
    
    /**
     * Constructs the error pattern that will be back-propagated.
     *
     * @param aDesired the desired pattern
     * @param anOutput the actual output pattern
     */
    protected void constructErrorPattern(double[] aDesired, double[] anOutput) {
        for(int x = 0; x < aDesired.length; ++x) {
            outs[x] = calculateError(aDesired[x], anOutput[x], x);
        }
        /** For debuging purpose to view the desired output
         * String myText = "Desired: ";
         * for (int x = 0; x < aDesired.length; ++x) {
         * myText += aDesired[x] + " ";
         * }
         * System.out.println(myText);
         * end debug */
    }
    
    /**
     * Calculates the error to be backpropaged for a single output neuron.
     * (The function should also update the global error internally).
     *
     * @param aDesired the desired output
     * @param anOutput the actual output of a single neuron
     * @param anIndex the index of the output neuron
     * @return the error to be back propagated
     */
    protected abstract double calculateError(double aDesired, double anOutput, int anIndex);
    
    /**
     * This method is called after an epoch finished and the global error should
     * be calculated.
     *
     * @return the global error (at the end of an epoch).
     */
    protected abstract double calculateGlobalError();
    
    /**
     * This method is called to signal that an epoch has finished. Better to say is
     * that a new epoch has started, because this method is called when the first pattern
     * of a new epoch arrives at the teacher.
     * New implementations of teachers can overwrite this method for their own use. (Please
     * do call <code>super.epochFinished()</code>).
     */
    protected void epochFinished() {
    }
}
