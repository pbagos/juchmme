package org.joone.engine;

import java.io.*;
import java.util.TreeSet;
import java.util.Collection;
import java.util.ArrayList;

import org.joone.log.*;
import org.joone.inspection.Inspectable;
import org.joone.inspection.implementations.WeightsInspection;

/**
 * The Synapse is the connection element between two Layer objects.
 * Its connections are represented by weights that transport the patterns
 * from a layer to another.
 * These weights are modified in the learning cycles, and represent the 'memory'
 * of the trained neural net.
 */
public abstract class Synapse
        implements
        InputPatternListener,
        OutputPatternListener,
        LearnableSynapse,
        Serializable,
        Inspectable {
    
    /**
     * Logger
     * */
    private static final ILogger log = LoggerFactory.getLogger(Synapse.class);
    /** Count of synapses for naming purposes. */
    private static int synapseCount = 0;
    
    /** Name set by default to "Synapse [synapse count]" */
    private String fieldName = "Synapse " + ++synapseCount;
    
    private double learningRate = 0;
    private double momentum = 0;
    private int inputDimension = 0;
    private int outputDimension = 0;
    private boolean inputFull;
    private boolean outputFull;
    private Monitor monitor;
    private int ignoreBefore = -1; // not more used
    private boolean loopBack = false;
    // true if this synapse closes a loop of a recurrent neural network
    
    protected Matrix array;
    protected int m_batch = 0;
    protected boolean enabled = true;
    protected transient double[] inps = null;
    protected transient double[] outs = null;
    protected transient double[] bouts;
    protected transient int items = 0;
    protected transient int bitems = 0;
    
    /**
     * @label revPattern
     */
    protected transient Pattern m_pattern;
    
    // The last fwd pattern read
    /**
     * @label fwdPattern
     */
    protected transient Pattern b_pattern; // The last back pattern read
    protected transient int count = 0;
    protected transient boolean notFirstTime;
    protected transient boolean notFirstTimeB;
    protected transient Learner myLearner = null;
    
    // Objects used for synchronization
    protected transient volatile Object fwdLock = null;
    protected transient volatile Object revLock = null;
    
    /** Contains true if for the current Synapse must be used
     * a Learner instead of a built-in learning algorithm.
     * Set it in the constructor of any inherited class.
     * Used by the getLearner method.
     * @see getLearner
     */
    protected boolean learnable = false;
    
    private static final long serialVersionUID = -5892822057908231022L;
    /** The constructor
     */
    public Synapse() {
        //log.info ("Synapse instanciated");
    }
    
    /** Adds a noise to the weights of the synapse
     * @param amplitude Amplitude of the noise: the value is centered around the zero.
     * e.g.: an amplitude = 0.2 means a noise range from -0.2 to 0.2
     */
    public void addNoise(double amplitude) {
        if (array != null)
            array.addNoise(amplitude);
    }
    
    /** Initializes all the weigths of the synapses with random values
     * @param amplitude Amplitude of the random values: the value is centered around the zero.
     * e.g.: an amplitude = 0.2 means a values' range from -0.2 to 0.2
     */
    public void randomize(double amplitude) {
        if (array != null)
//            array.randomize(-1.0 * amplitude, amplitude);
            array.initialize();
    }
    
    /**
     * Funzione di TRAIN dell'elemento.
     * @param pattern double[] - pattern di input sul quale applicare la funzione di trasferimento
     */
    protected abstract void backward(double[] pattern);
    
    /** Returns TRUE if the synapse calls the method nextStep()
     * on the Monitor object when the fwdGet() method is called
     * @return boolean
     */
    public boolean canCountSteps() {
        return false;
    }
    
    /**
     * Recall function
     * @param pattern double[] - input pattern
     */
    protected abstract void forward(double[] pattern);
    
    public Pattern fwdGet() {
        if (!isEnabled())
            return null;
        synchronized (getFwdLock()) {
            if ((notFirstTime) || (!loopBack)) {
                while (items == 0) {
                    try {
                        fwdLock.wait();
                    } catch (InterruptedException e) {
                        //                    log.warn ( "wait () was interrupted");
                        //e.printStackTrace();
                        reset();
                        fwdLock.notify();
                        return null;
                    }
                }
                --items;
                m_pattern.setArray(outs);
                if (isLoopBack())
                    // To avoid sinc problems
                    m_pattern.setCount(0);
                fwdLock.notify();
                return m_pattern;
            } else {
                items = bitems = count = 0;
                notFirstTime = true;
                fwdLock.notify();
                return null;
            }
        }
    }
    
    public void fwdPut(Pattern pattern) {
        if (isEnabled()) {
            synchronized (getFwdLock()) {
                while (items > 0) {
                    try {
                        fwdLock.wait();
                    } catch (InterruptedException e) {
                        reset();
                        fwdLock.notify();
                        return;
                    } // End of catch
                }
                m_pattern = pattern;
                count = m_pattern.getCount();
                inps = (double[])pattern.getArray();
                forward(inps);
                ++items;
                fwdLock.notify();
            }
        }
    }
    
    /** Resets the internal state to be ready for the next run
     */
    public void reset() {
        items = bitems = 0;
        notFirstTime = false;
        notFirstTimeB = false;
    }
    
    /** Returns the number of the ignored cycles at beginning of each epoch.
     * During these cycles the synapse returns null on the call to the xxxGet methods
     *
     * @return int
     * @see Synapse#setIgnoreBefore
     */
    public int getIgnoreBefore() {
        return ignoreBefore;
    }
    
    /** Returns the input dimension of the synapse.
     * @return int
     */
    public int getInputDimension() {
        return inputDimension;
    }
    
    /** Returns the value of the learning rate
     * @return double
     */
    public double getLearningRate() {
        if (monitor != null)
            return monitor.getLearningRate();
        else
            return 0.0;
    }
    
    /** Returns the value of the momentum
     * @return double
     */
    public double getMomentum() {
        if (monitor != null)
            return monitor.getMomentum();
        else
            return 0.0;
    }
    
    /** Returns the Monitor object attached to the synapse
     * @return neural.engine.Monitor
     */
    public Monitor getMonitor() {
        return monitor;
    }
    
    /** Returns the name of the synapse
     * @return String
     * @see #setName
     */
    public String getName() {
        return fieldName;
    }
    
    /** Returns the output dimension of the synapse.
     * @return int
     */
    public int getOutputDimension() {
        return outputDimension;
    }
    
    protected Object readResolve() {
        setArrays(getInputDimension(), getOutputDimension());
        return this;
    }
    
    public Pattern revGet() {
        if (!isEnabled())
            return null;
        synchronized (getRevLock()) {
            if ((notFirstTimeB) || (!loopBack)) {
                while (bitems == 0) {
                    try {
                        revLock.wait();
                    } catch (InterruptedException e) {
                        //                    log.warn ( "wait () was interrupted");
                        //e.printStackTrace();
                        reset();
                        revLock.notify();
                        return null;
                    }
                }
                --bitems;
                b_pattern.setArray(bouts);
                revLock.notify();
                return b_pattern;
            } else {
                //bitems = 0;
                revLock.notify();
                return null;
            }
        }
    }
    
    public void revPut(Pattern pattern) {
        if (isEnabled()) {
            synchronized (getRevLock()) {
                while (bitems > 0) {
                    try {
                        revLock.wait();
                    } catch (InterruptedException e) {
                        reset();
                        revLock.notify();
                        return;
                    }
                }
                b_pattern = pattern;
                count = b_pattern.getCount();
                backward(pattern.getArray());
                ++bitems;
                notFirstTimeB = true;
                revLock.notify();
            }
        }
    }
    
    /**
     * Insert the method's description here.
     * Creation date: (23/09/2000 12.52.58)
     */
    protected abstract void setArrays(int rows, int cols);
    
    /**
     * Dimensiona l'elemento
     * @param int rows - righe
     * @param int cols - colonne
     */
    protected abstract void setDimensions(int rows, int cols);
    
    /** Sets the number of the ignored cycles at beginning of each epoch.
     * During these cycles the synapse is disabled.
     * Useful when the synapse is attached as the Input2 of a SwitchSynapse
     *
     * @param newIgnoreBefore int
     * @see SwitchSynapse
     */
    public void setIgnoreBefore(int newIgnoreBefore) {
        ignoreBefore = newIgnoreBefore;
    }
    
    /** Sets the input dimension of the synapse
     * @param newInputDimension int
     */
    public void setInputDimension(int newInputDimension) {
        if (inputDimension != newInputDimension) {
            inputDimension = newInputDimension;
            setDimensions(newInputDimension, -1);
        }
    }
    
    /** Sets the value of the learning rate
     * @param newLearningRate double
     */
    public void setLearningRate(double newLearningRate) {
        learningRate = newLearningRate;
    }
    
    /** Sets the value of the momentum rate
     * @param newMomentum double
     */
    public void setMomentum(double newMomentum) {
        momentum = newMomentum;
    }
    
    /** Sets the Monitor object of the synapse
     * @param newMonitor neural.engine.Monitor
     */
    public void setMonitor(Monitor newMonitor) {
        monitor = newMonitor;
        if (monitor != null) {
            setLearningRate(monitor.getLearningRate());
            setMomentum(monitor.getMomentum());
        }
    }
    
    /** Sets the name of the synapse
     * @param name The name of the component.
     * @see #getName
     */
    public void setName(java.lang.String name) {
        fieldName = name;
    }
    
    /** Sets the output dimension of the synapse
     * @param newOutputDimension int
     */
    public void setOutputDimension(int newOutputDimension) {
        if (outputDimension != newOutputDimension) {
            outputDimension = newOutputDimension;
            setDimensions(-1, newOutputDimension);
        }
    }
    
    /** Getter for property enabled.
     * @return Value of property enabled.
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /** Setter for property enabled.
     * @param enabled New value of property enabled.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    /** Getter for property loopBack.
     * @return Value of property loopBack.
     *
     */
    public boolean isLoopBack() {
        return loopBack;
    }
    
    /** Setter for property loopBack.
     * @param loopBack New value of property loopBack.
     *
     */
    public void setLoopBack(boolean loopBack) {
        this.loopBack = loopBack;
    }
    
    /**
     * Base for check messages.
     * Subclasses should call this method from thier own check method.
     *
     * @see InputPaternListener
     * @see OutputPaternListener
     * @return validation errors.
     */
    public TreeSet check() {
        
        // Prepare an empty set for check messages;
        TreeSet checks = new TreeSet();
        
        // Return check messages
        return checks;
    }
    
    public Collection Inspections() {
        Collection col = new ArrayList();
        col.add(new WeightsInspection(array));
        return col;
    }
    
    public String InspectableTitle() {
        return this.getName();
    }
    
    /** Getter for property inputFull.
     * @return Value of property inputFull.
     *
     */
    public boolean isInputFull() {
        return inputFull;
    }
    
    /** Setter for property inputFull.
     * @param inputFull New value of property inputFull.
     *
     */
    public void setInputFull(boolean inputFull) {
        this.inputFull = inputFull;
    }
    
    /** Getter for property outputFull.
     * @return Value of property outputFull.
     *
     */
    public boolean isOutputFull() {
        return outputFull;
    }
    
    /** Setter for property outputFull.
     * @param outputFull New value of property outputFull.
     *
     */
    public void setOutputFull(boolean outputFull) {
        this.outputFull = outputFull;
    }
    
    /** Getter for the internal matrix of weights
     *
     * @return the Matrix containing the 2D array of weights
     */
    public Matrix getWeights() {
        return array;
    }
    
    /** Setter for the internal matrix of weights
     *
     * @param the Matrix containing the 2D array of weights
     */
    public void setWeights(Matrix newWeights) {
        array = newWeights;
    }
    
    /** Returns the appropriate Learner object for this class
     * depending on the Monitor.learningMode property value
     * @return the Learner object if applicable, otherwise null
     * @see org.joone.engine.Learnable#getLearner()
     */
    public Learner getLearner() {
        if (!learnable) {
            return null;
        }
        return getMonitor().getLearner();
    }
    
    /** Initialize the Learner object
     * @see org.joone.engine.Learnable#initLearner()
     */
    public void initLearner() {
        myLearner = getLearner();
        if(myLearner != null) {
            myLearner.registerLearnable(this);
        }
    }
    
    /**
     * Getter for property fwdLock.
     * @return Value of property fwdLock.
     */
    protected Object getFwdLock() {
        if (fwdLock == null)
            fwdLock = new Object();
        return fwdLock;
    }
    
    
    /**
     * Getter for property revLock.
     * @return Value of property revLock.
     */
    protected Object getRevLock() {
        if (revLock == null)
            revLock = new Object();
        return revLock;
    }
    
    /** Synapse's initialization.
     * It needs to be invoked at the starting of the neural network
     * It's called within the Layer.init() method
     */
    public void init() {
        this.initLearner();
        this.getFwdLock();
        this.getRevLock();
    }
    
}