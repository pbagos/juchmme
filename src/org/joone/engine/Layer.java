package org.joone.engine;

import org.joone.log.*;

import org.joone.engine.learning.*;
import org.joone.exception.*;
import org.joone.inspection.*;
import org.joone.inspection.implementations.*;
import org.joone.io.*;
import org.joone.net.*;
import org.joone.util.*;

import java.io.*;
import java.util.*;

/**
 * The Layer object is the basic element forming the neural net.
 * Primarily it consists of a number of neurons that apply a transfer
 * function to the sum of a number of input patterns and convey the result
 * to the output pattern. The input patterns are received from connected
 * input listeners and the transformed results are passed to connected output
 * listeners. The component also handles learning by accepting patterns of error
 * gradients from output listeners, applying a reverse (inverse) transfer function
 * and passing the result to the input listeners. Layers execute their own
 * Threads to perform the perform the pattern conveyance, so that a network
 * of Layers can operate in a multi-threaded manner. The execution and termination
 * of the Thread is controlled by a Monitor object.
 */
public abstract class Layer implements NeuralLayer, Runnable, Serializable,
        Inspectable, LearnableLayer {
    
    /** Stop flag. If the step has this value, the execution thread terminates. */
    public static final int STOP_FLAG = -1;
    
    /** Serial version ID for this class */
    private static final long serialVersionUID = -1572591602454639355L;
    
    /** The name of the layer */
    private String LayerName;
    
    /** The number of neurons in the layer */
    private int rows = 0;
    
    /** Holds the bias of neurons of the layer */
    protected Matrix bias;
    
    /**
     * The monitor of the layer.
     * Contains all parameters needed to the learning phase
     */
    protected Monitor monitor;
    
    /** Not used but maintained for backward serialization compatability. */
    protected int m_batch;
    
    /** The Net's phase: false == recall; true == learning */
    protected boolean learning;
    
    /** Contains true if for the Layer must be used
     * a Learner instead of a built-in learning algorithm.
     * Set it in the constructor of any inherited class.
     * Used by the getLearner method.
     * @see getLearner
     */
    protected boolean learnable = false;
    
    /** Contains the list of input connected listeners (InputPatternListener) */
    protected Vector inputPatternListeners = null;
    
    /** Contains the list of output connected listeners (OutputPatternListener) */
    protected Vector outputPatternListeners = null;
    
    /** The execution Thread for this layer. */
    private transient Thread myThread = null;
    /** The monitor used to control read/write access to myThread */
    private transient volatile Object myThreadMonitor;
    
    /**
     * Set of output values passed from this layer
     * to connected OutputListeners durng the recall phase.
     */
    protected transient double[] outs;
    
    /**
     * Set of input values passed to this layer
     * from connected InputListeners during the recall phase.
     */
    protected transient double[] inps;
    
    /**
     * Set of input error gradient values passed to this layer
     * from connected OutputListenrs during the learning phase.
     */
    protected transient double[] gradientInps;
    
    /**
     * Set of output error gradient values passed from this layer
     * to connected InputListenrs during the learning phase.
     */
    protected transient double[] gradientOuts;
    
    /** The step number of the network run. */
    protected transient int step = 0;
    
    /** Whether the layer is running */
    protected transient volatile boolean running = false;
    
    /** The Learner for this layer. */
    protected transient Learner myLearner = null;
    
    /** Logger for this class */
    private static final ILogger log = LoggerFactory.getLogger(Layer.class);
    
    /** The empty constructor */
    public Layer() {
    }
    
    /**
     * Creates a named layer
     * @param ElemName The name of the layer
     */
    public Layer(String ElemName) {
        this.setLayerName(ElemName);
    }
    
    /**
     * Adds a noise componentto the biases of the layer
     * and to all the input connected synapses.
     * @param amplitude the noise's amplitude in terms of distance from zero;
     * e.g. a value equal 0.3 means a noise range from -0.3 to 0.3
     */
    public void addNoise(double amplitude) {
        InputPatternListener elem;
        bias.addNoise(amplitude);
        
        if (inputPatternListeners == null) {
            return;
        }
        int currentSize = inputPatternListeners.size();
        for (int index = 0; index < currentSize; index++) {
            elem = (InputPatternListener) inputPatternListeners.elementAt(index);
            if (elem != null) {
                if (elem instanceof Synapse)
                    ((Synapse) elem).addNoise(amplitude);
            }
        }
    }
    
    /**
     * Initialize the weights of the biases and of all the connected synapses
     * @param amplitude the amplitude of the applied noise
     */
    public void randomize(double amplitude) {
        InputPatternListener elem;
        //        bias.randomize(-1.0 * amplitude, amplitude);
        bias.initialize();
        
        if (inputPatternListeners == null) {
            return;
        }
        int currentSize = inputPatternListeners.size();
        for (int index = 0; index < currentSize; index++) {
            elem = (InputPatternListener) inputPatternListeners.elementAt(index);
            if (elem != null) {
                if (elem instanceof Synapse)
                    ((Synapse) elem).randomize(amplitude);
            }
        }
    }
    
    /**
     * Reverse transfer function of the component.
     * @param pattern input pattern on which to apply the transfer function
     * @throws JooneRuntimeException
     */
    protected abstract void backward(double[] pattern)
    throws JooneRuntimeException;
    
    /**
     * Copies one layer into another, to obtain a type-transformation
     * from one kind of Layer to another.
     * The old Layer is disconnected from the net, and the new Layer
     * takes its place.
     * @param newLayer the new layer with which to replace this one
     * @return The new layer
     */
    public NeuralLayer copyInto(NeuralLayer newLayer) {
        newLayer.setMonitor(getMonitor());
        newLayer.setRows(getRows());
        newLayer.setBias(getBias());
        newLayer.setLayerName(getLayerName());
        newLayer.setAllInputs((Vector) getAllInputs().clone());
        newLayer.setAllOutputs((Vector) getAllOutputs().clone());
        removeAllInputs();
        removeAllOutputs();
        return newLayer;
    }
    
    /**
     * Calls all the fwdGet methods on the input synapses to get the input patterns
     */
    protected void fireFwdGet() {
        double[] patt;
        Pattern tPatt;
        InputPatternListener tempListener = null;
        int currentSize = inputPatternListeners.size();
        step = 0;
        for (int index = 0; (index < currentSize) && running; index++) {
            tempListener =
                    (InputPatternListener) inputPatternListeners.elementAt(index);
            if (tempListener != null) {
                tPatt = tempListener.fwdGet();
                if (tPatt != null) {
                    patt = tPatt.getArray();
                    if (patt.length != inps.length) {
                        adjustSizeToFwdPattern(patt);
                    }
                    
                    //Sum the received pattern into inps.
                    sumInput(patt);
                    if (step != STOP_FLAG)
                    /* In case of a recurrent network, the layer could receive
                     * patterns with different sequence numbers.
                     * The stored sequence number is the higher one. */
                        if ((step < tPatt.getCount())
                        || (tPatt.getCount() == STOP_FLAG)) // The stop is guaranteed
                            step = tPatt.getCount();
                }
            }
        }
    }
    
    /**
     * Calls all the fwdPut methods on the output synapses to pass
     * them the calculated patterns
     * @param pattern the Pattern to pass to the output synapses
     */
    protected void fireFwdPut(Pattern pattern) {
        if (outputPatternListeners == null) {
            return;
        }
        int currentSize = outputPatternListeners.size();
        OutputPatternListener tempListener = null;
        for (int index = 0; (index < currentSize) && running; index++) {
            tempListener =
                    (OutputPatternListener) outputPatternListeners.elementAt(index);
            if (tempListener != null) {
                boolean loop = false;
                if (tempListener instanceof Synapse)
                    loop = ((Synapse)tempListener).isLoopBack();
                if ((currentSize == 1)
                && getMonitor().isLearningCicle(pattern.getCount())
                && !loop)
                    tempListener.fwdPut(pattern);
                else
                    tempListener.fwdPut((Pattern) pattern.clone());
            }
        }
    }
    
    /**
     * Calls all the revGet methods on the output synapses to get the error gradients
     */
    protected void fireRevGet() {
        if (outputPatternListeners == null) {
            return;
        }
        
        double[] patt;
        Pattern tPatt;
        int currentSize = outputPatternListeners.size();
        OutputPatternListener tempListener = null;
        for (int index = 0; (index < currentSize) && running; index++) {
            tempListener =
                    (OutputPatternListener) outputPatternListeners.elementAt(index);
            if (tempListener != null) {
                tPatt = tempListener.revGet();
                if (tPatt != null) {
                    patt = tPatt.getArray();
                    if (patt.length != gradientInps.length) {
                        adjustSizeToRevPattern(patt);
                    }
                    
                    //Sum the received error gradient pattern into outs.
                    sumBackInput(patt);
                }
            }
        }
    }
    
    /**
     * Calls all the revPut methods on the input synapses to get the input patterns
     * and pass them the resulting calculated gradients
     * @param pattern the Pattern to pass to the input listeners
     */
    protected void fireRevPut(Pattern pattern) {
        if (inputPatternListeners == null) {
            return;
        }
        int currentSize = inputPatternListeners.size();
        InputPatternListener tempListener = null;
        for (int index = 0; (index < currentSize) && running; index++) {
            tempListener =
                    (InputPatternListener) inputPatternListeners.elementAt(index);
            if (tempListener != null) {
                boolean loop = false;
                if (tempListener instanceof Synapse)
                    loop = ((Synapse)tempListener).isLoopBack();
                if ((currentSize == 1) && !loop)
                    tempListener.revPut(pattern);
                else
                    tempListener.revPut((Pattern) pattern.clone());
            }
        }
    }
    
    /**
     * Adjusts the size of a layer if the size of the forward pattern differs.
     *
     * @param aPattern the pattern holding a different size than the layer
     * (dimension of neurons is not in accordance with the dimension of the
     * pattern that is being forwarded).
     */
    protected void adjustSizeToFwdPattern(double[] aPattern) {
        // this function is included to give layers (e.g. Rbf layers) a
        // change to take different actions (by overwriting this function)
        // in case the pattern has a different size than the layer
        
        int myOldSize = getRows();
        setRows(aPattern.length);
        log.warn("Pattern size mismatches #neurons. #neurons in layer '"
                + getLayerName() +"' adjusted [fwd pass, "
                + myOldSize + " -> " + getRows() + "].");
    }
    
    /**
     * Adjusts the size of a layer if the size of the reverse pattern differs.
     *
     * @param aPattern the pattern holding a different size than the layer
     * (dimension of neurons is not in accordance with the dimension of the
     * pattern that is being reversed).
     */
    protected void adjustSizeToRevPattern(double[] aPattern) {
        // this function is included to give layers (e.g. Rbf layers) a
        // change to take different actions (by overwriting this function)
        // in case the pattern has a different size than the layer
        
        int myOldSize = getRows();
        setRows(aPattern.length);
        log.warn("Pattern size mismatches #neurons. #neurons in layer '"
                + getLayerName() +"' adjusted [rev pass, "
                + myOldSize + " -> " + getRows() + "].");
    }
    
    /**
     * Transfer function to recall a result on a trained net
     * @param pattern input pattern to which to apply the rtransfer function
     * @throws JooneRuntimeException
     */
    // TO DO: Transform the JooneRuntimeException to JoonePropagationException
    protected abstract void forward(double[] pattern)
    throws JooneRuntimeException;
    
    /**
     * Returns the vector of the input listeners
     * @return the connected input pattern listeners
     */
    public Vector getAllInputs() {
        return inputPatternListeners;
    }
    
    /**
     * Returns the vector of the output listeners
     * @return the connected output pattern listeners
     */
    public Vector getAllOutputs() {
        return outputPatternListeners;
    }
    
    /**
     * Return the bias matrix
     * @return the layer biases
     */
    public Matrix getBias() {
        return bias;
    }
    
    /**
     * Returns the number of neurons contained in the layer
     * @return the number of neurons in the layer.
     */
    public int getDimension() {
        return getRows();
    }
    
    /**
     * Returns the name of the layer
     * @return the name of the layer
     */
    public String getLayerName() {
        return LayerName;
    }
    
    /**
     * Returns the monitor object
     * @return the layer's Monitor object
     */
    public Monitor getMonitor() {
        return monitor;
    }
    
    /**
     * Returns the dimension (# of neurons) of the Layer
     * @return the number of neurons in the layer
     */
    public int getRows() {
        return rows;
    }
    
    /**
     * Remove all the input listeners of the layer
     */
    public void removeAllInputs() {
        if (inputPatternListeners != null) {
            Vector tempVect = (Vector) inputPatternListeners.clone();
            for (int i = 0; i < tempVect.size(); ++i)
                this.removeInputSynapse(
                        (InputPatternListener) tempVect.elementAt(i));
            inputPatternListeners = null;
        }
    }
    
    /**
     * Remove all the output listeners of the layer
     */
    public void removeAllOutputs() {
        if (outputPatternListeners != null) {
            Vector tempVect = (Vector) outputPatternListeners.clone();
            for (int i = 0; i < tempVect.size(); ++i)
                this.removeOutputSynapse(
                        (OutputPatternListener) tempVect.elementAt(i));
            outputPatternListeners = null;
        }
    }
    
    /**
     * Remove an input Listener
     * @param newListener the input listener to remove
     */
    public void removeInputSynapse(InputPatternListener newListener) {
        if (inputPatternListeners != null) {
            inputPatternListeners.removeElement(newListener);
            newListener.setInputFull(false);
            if (newListener instanceof NeuralNetListener) {
                removeListener((NeuralNetListener)newListener);
            }
            if (inputPatternListeners.size() == 0)
                inputPatternListeners = null;
        }
    }
    
    /**
     * Remove an output listener from the layer
     * @param newListener the output listener to remove
     */
    public void removeOutputSynapse(OutputPatternListener newListener) {
        if (outputPatternListeners != null) {
            outputPatternListeners.removeElement(newListener);
            newListener.setOutputFull(false);
            if (newListener instanceof NeuralNetListener) {
                removeListener((NeuralNetListener)newListener);
            }
            if (outputPatternListeners.size() == 0)
                outputPatternListeners = null;
        }
    }
    
    protected void removeListener(NeuralNetListener listener) {
        if (getMonitor() != null)
            getMonitor().removeNeuralNetListener(listener);
    }
    
    /**
     * Gets the values lastly outputed by the neurons of this layer.
     *
     * @return the values lastly outputed.
     */
    public double[] getLastOutputs() {
        return (double[])outs.clone();
    }
    
    /**
     * The core running engine of the layer.
     * Called from the method <CODE>start()</CODE>
     * @throws JooneRuntimeException
     */
    public void run() throws JooneRuntimeException {
        Pattern patt = new Pattern();
        while (running) {
            // Recall phase
            inps = new double[getRows()];
            try {
                fireFwdGet();
                if (running) {
                    forward(inps);
                    patt.setArray(outs);
                    patt.setCount(step);
                    fireFwdPut(patt);
                }
                if (step != STOP_FLAG)
                    if (monitor != null) {
                    // Gets if the next step is a learning step
                    learning = monitor.isLearningCicle(step);
                    } else
                        learning = false;
                else
                    // Stops the layer
                    running = false;
            } catch (JooneRuntimeException jre) {
                String msg = "JooneException thrown in run() method."
                        + jre.getMessage();
                log.error(msg);
                running = false;
                new NetErrorManager(getMonitor(), msg);
            }
            
            // Learning phase
            if (learning && running) {
                gradientInps = new double[getDimension()];
                try {
                    fireRevGet();
                    backward(gradientInps);
                    patt.setArray(gradientOuts);
                    patt.setOutArray(outs);
                    // Added for some unsupervised learning algorithm (See org.joone.engine.Pattern)
                    patt.setCount(step);
                    fireRevPut(patt);
                } catch (JooneRuntimeException jre) {
                    String msg = "In run() JooneException thrown." + jre.getMessage();
                    log.error(msg);
                    running = false;
                    new NetErrorManager(getMonitor(), msg);
                }
            }
        } // END while (running = false)
        resetInputListeners();
        synchronized(getThreadMonitor()) { myThread = null;}
    }
    
    /**
     * Sets the Vector that contains all the input listeners.
     * Can be useful to set the input synapses taken from another Layer
     * @param newInputPatternListeners The vector containing the list of input synapses
     */
    public synchronized void setAllInputs(Vector newInputPatternListeners) {
        inputPatternListeners = newInputPatternListeners;
        if (inputPatternListeners != null)
            for (int i = 0; i < inputPatternListeners.size(); ++i)
                this.setInputDimension(
                        (InputPatternListener) inputPatternListeners.elementAt(i));
        notifyAll();
    }
    
    /**
     * Sets the Vector that contains all the input listeners.
     * It accepts an ArrayList as parameter. Added for Spring
     * Can be useful to set the input synapses taken from another Layer
     * @param newInputPatternListeners The vector containing the list of input synapses
     */
    public void setInputSynapses(ArrayList newInputPatternListeners) {
        this.setAllInputs(new Vector(newInputPatternListeners));
    }
    
    /**
     * Sets the Vector that contains all the output listeners.
     * Can be useful to set the output synapses taken from another Layer
     * @param newOutputPatternListeners The vector containing the list of output synapses
     */
    public void setAllOutputs(Vector newOutputPatternListeners) {
        outputPatternListeners = newOutputPatternListeners;
        if (outputPatternListeners != null)
            for (int i = 0; i < outputPatternListeners.size(); ++i)
                this.setOutputDimension(
                        (OutputPatternListener) outputPatternListeners.elementAt(i));
    }
    
    /**
     * Sets the Vector that contains all the output listeners.
     * It accepts an ArrayList as parameter. Added for Spring
     * Can be useful to set the output synapses taken from another Layer
     * @param newOutputPatternListeners The vector containing the list of output synapses
     */
    public void setOutputSynapses(ArrayList newOutputPatternListeners) {
        this.setAllOutputs(new Vector(newOutputPatternListeners));
    }
    
    /**
     * Sets the matrix of biases
     * @param newBias The Matrix object containing the biases
     */
    public void setBias(Matrix newBias) {
        bias = newBias;
    }
    
    /**
     * Sets the dimension of the layer.
     * Override to define how the internal buffers must be sized.
     */
    protected abstract void setDimensions();
    
    /**
     * Sets the dimension of the listener passed as parameter.
     * Called after a new input listener is added.
     * @param syn the listener to be affected
     */
    protected void setInputDimension(InputPatternListener syn) {
        if (syn.getOutputDimension() != getRows())
            syn.setOutputDimension(getRows());
    }
    
    /**
     * Adds a new input synapse to the layer
     * @param newListener The new input synapse to add
     * @return whether the listener was added
     */
    public synchronized boolean addInputSynapse(InputPatternListener newListener) {
        if (inputPatternListeners == null) {
            inputPatternListeners = new Vector();
        }
        boolean retValue = false;
        if (!inputPatternListeners.contains(newListener))
            if (!newListener.isInputFull()) {
            inputPatternListeners.addElement(newListener);
            if (newListener.getMonitor() == null)
                newListener.setMonitor(getMonitor());
            newListener.setInputFull(true);
            this.setInputDimension(newListener);
            retValue = true;
            }
        notifyAll();
        return retValue;
    }
    
    /**
     * Sets the name of the layer
     * @param newLayerName The name
     */
    public void setLayerName(String newLayerName) {
        LayerName = newLayerName;
    }
    
    /**
     * Sets the monitor object
     * @param mon The Monitor
     */
    public void setMonitor(Monitor mon) {
        monitor = mon;
        // Sets the Monitor object of all input and output synapses
        setVectMonitor(inputPatternListeners, mon);
        setVectMonitor(outputPatternListeners, mon);
    }
    
    /**
     * Set the monitor object for all pattern listeners in a Vector
     * @param vect the Vector of pattern listeners
     * @param mon the Monitor to be set
     */
    private void setVectMonitor(Vector vect, Monitor mon) {
        if (vect != null) {
            int currentSize = vect.size();
            Object tempListener = null;
            for (int index = 0; index < currentSize; index++) {
                tempListener = vect.elementAt(index);
                if (tempListener != null)
                    ((NeuralElement) tempListener).setMonitor(mon);
            }
        }
    }
    
    /**
     * Sets the dimension of the listener passed as parameter.
     * Called after a new output listener is added.
     * @param syn the OutputPatternListener to affect
     */
    protected void setOutputDimension(OutputPatternListener syn) {
        if (syn.getInputDimension() != getRows())
            syn.setInputDimension(getRows());
    }
    
    /**
     * Adds a new output synapse to the layer
     * @param newListener The new output synapse
     * @return whether the listener was added
     */
    public boolean addOutputSynapse(OutputPatternListener newListener) {
        if (outputPatternListeners == null)
            outputPatternListeners = new Vector();
        boolean retValue = false;
        if (!outputPatternListeners.contains(newListener))
            if (!newListener.isOutputFull()) {
            outputPatternListeners.addElement(newListener);
            newListener.setMonitor(getMonitor());
            newListener.setOutputFull(true);
            this.setOutputDimension(newListener);
            retValue = true;
            }
        return retValue;
    }
    
    /**
     * Sets the dimension (# of neurons) of the Layer
     * @param newRows The number of the neurons contained in the Layer
     */
    public void setRows(int newRows) {
        if (rows != newRows) {
            rows = newRows;
            setDimensions();
            setConnDimensions();
            bias = new Matrix(getRows(), 1);
        }
    }
    
    /**
     * Starts the Layer
     */
    public void start() {
        synchronized(getThreadMonitor()) {
            if (myThread == null) {
                // Check if some input synapse is connected
                if (inputPatternListeners != null) {
                    if (checkInputEnabled()) {
                        // If all the input synapses are disabled, the layer doesn't start
                        running = true;
                        if (getLayerName() != null)
                            myThread = new Thread(this, getLayerName());
                        else
                            myThread = new Thread(this);
                        this.init();
                        myThread.start();
                    } else {
                        String msg = "Can't start: '"
                                + getLayerName()
                                + "' has not input synapses connected and/or enabled";
                        log.error(msg);
                        throw new JooneRuntimeException(msg);
                    }
                } else {
                    String msg = "Can't start: '"
                            + getLayerName()
                            + "' has not input synapses connected";
                    log.error(msg);
                    throw new JooneRuntimeException(msg);
                }
            }
        }
    }
    
    public void init() {
        this.initLearner();
        // initialize all the output synapses
        if (outputPatternListeners != null) {
            Vector tempVect = (Vector) outputPatternListeners.clone();
            for (int i = 0; i < tempVect.size(); ++i) {
                if (tempVect.elementAt(i) instanceof NeuralElement)
                    ((NeuralElement) tempVect.elementAt(i)).init();
            }
        }
    }
    
    /**
     * Checks if at least one input synapse is enabled
     * @return false if all the input synapses are disabled
     */
    protected boolean checkInputEnabled() {
        for (int i = 0; i < inputPatternListeners.size(); ++i) {
            InputPatternListener iPatt =
                    (InputPatternListener) inputPatternListeners.elementAt(i);
            if (iPatt.isEnabled())
                return true;
        }
        return false;
    }
    
    /**
     * Stops the Layer
     */
    public void stop() {
        synchronized(getThreadMonitor()) {
            if (myThread != null) {
                running = false;
                myThread.interrupt();
            }
        }
    }
    
    /**
     * Reset all the input listeners
     */
    protected void resetInputListeners() {
        int currentSize = inputPatternListeners.size();
        for (int index = 0; index < currentSize; index++) {
            InputPatternListener tempListener =
                    (InputPatternListener) inputPatternListeners.elementAt(index);
            if (tempListener != null)
                tempListener.reset();
        }
    }
    
    /**
     * Calculates the net input of the error gradents during the learning phase
     * @param pattern array of input values
     */
    protected void sumBackInput(double[] pattern) {
        int x = 0;
        try {
            for (; x < gradientInps.length; ++x)
                gradientInps[x] += pattern[x];
        } catch (IndexOutOfBoundsException iobe) {
            log.warn(
                    getLayerName()
                    + " gradInps.size:"
                    + gradientInps.length
                    + " pattern.size:"
                    + pattern.length
                    + " x:"
                    + x);
        }
    }
    
    /**
     * Calculates the net input of the values in the recall phase
     * @param pattern array of input values
     */
    protected void sumInput(double[] pattern) {
        for (int x = 0; x < inps.length; ++x) {
            inps[x] += pattern[x];
        }
    }
    
    /**
     * Read in a serialised version of this layer
     * @param in the serialised stream
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException {
        if (in.getClass().getName().indexOf("xstream") != -1) {
            in.defaultReadObject();
        } else {
            LayerName = (String) in.readObject();
            rows = in.readInt();
            bias = (Matrix) in.readObject();
            monitor = (Monitor) in.readObject();
            m_batch = in.readInt();
            learning = in.readBoolean();
            inputPatternListeners = readVector(in);
            outputPatternListeners = readVector(in);
        }
        setDimensions();
    }
    
    /**
     * Write a serialized version of this layer
     * @param out the output stream to write this layer to
     * @throws IOException
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        if (out.getClass().getName().indexOf("xstream") != -1) {
            out.defaultWriteObject();
        } else {
            out.writeObject(LayerName);
            out.writeInt(rows);
            out.writeObject(bias);
            out.writeObject(monitor);
            out.writeInt(m_batch);
            out.writeBoolean(learning);
            writeVector(out, inputPatternListeners);
            writeVector(out, outputPatternListeners);
        }
    }
    
    /**
     * This method is useful to serialize only the vector's
     * elements that don't implement the Serialize interface,
     * only when the Monitor.isExporting returns the value TRUE.
     * @param out the output stream to write to
     * @param vect the Vector to serialize
     * @throws IOException
     */
    private void writeVector(ObjectOutputStream out, Vector vect)
    throws IOException {
        if (vect != null) {
            boolean exporting = false;
            if ((monitor != null) && (monitor.isExporting()))
                exporting = true;
            for (int i = 0; i < vect.size(); ++i) {
                Object obj = vect.elementAt(i);
                if (!(obj instanceof NotSerialize) || !(exporting))
                    out.writeObject(obj);
            }
        }
        out.writeObject(null);
    }
    
    /**
     * Create a Vector from a serialized version
     * @param in the input stream serialized version
     * @return the deserialized Vector
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private Vector readVector(ObjectInputStream in)
    throws IOException, ClassNotFoundException {
        Vector vect = new Vector();
        Object obj = in.readObject();
        while (obj != null) {
            vect.addElement(obj);
            obj = in.readObject();
        }
        return vect;
    }
    
    /**
     * Sets the input and output synapses' dimensions
     */
    protected void setConnDimensions() {
        if (inputPatternListeners != null) {
            int currentSize = inputPatternListeners.size();
            InputPatternListener tempListener = null;
            for (int index = 0; index < currentSize; index++) {
                tempListener =
                        (InputPatternListener) inputPatternListeners.elementAt(index);
                if (tempListener != null) {
                    setInputDimension(tempListener);
                }
            }
        }
        if (outputPatternListeners != null) {
            int currentSize = outputPatternListeners.size();
            OutputPatternListener tempListener = null;
            for (int index = 0; index < currentSize; index++) {
                tempListener =
                        (OutputPatternListener) outputPatternListeners.elementAt(index);
                if (tempListener != null) {
                    setOutputDimension(tempListener);
                }
            }
        }
    }
    
    /**
     * Determine whether the execution thread is running
     * @return whether it is running
     */
    public boolean isRunning() {
        synchronized(getThreadMonitor()) {
            if (myThread != null && myThread.isAlive()) {
                return true;
            }
            return false;
        }
    }
    
    /**
     * Get check messages from listeners.
     * Subclasses should call this method from thier own check method.
     *
     * @see NeuralLayer
     * @return validation errors.
     */
    public TreeSet check() {
        
        // Prepare an empty set for check messages;
        TreeSet checks = new TreeSet();
        
        // All layers must have at least one input patern listener.
        // The absense of an output patern listener is acceptable.
        if ((inputPatternListeners == null) || (inputPatternListeners.size() == 0)) {
            checks.add(new NetCheck(NetCheck.FATAL,
                    "Layer has no input synapses attached.",
                    this));
        }
        
        // Get the input patern listener check messages;
        if (inputPatternListeners != null) {
            for (int i = 0; i < inputPatternListeners.size(); i++) {
                InputPatternListener listener =
                        (InputPatternListener) inputPatternListeners.elementAt(i);
                checks.addAll(listener.check());
                if (listener instanceof StreamInputSynapse) {
                    StreamInputSynapse sis = (StreamInputSynapse) listener;
                    int cols = sis.numColumns();
                    if (cols != rows) {
                        checks.add(new NetCheck(NetCheck.FATAL,
                                "Rows parameter does not match the number of columns for the attached input stream .",
                                this));
                    }
                }
            }
        }
        
        // Get the input patern listener check messages;
        if (outputPatternListeners != null) {
            for (int i = 0; i < outputPatternListeners.size(); i++) {
                OutputPatternListener listener =
                        (OutputPatternListener) outputPatternListeners.elementAt(i);
                checks.addAll(listener.check());
            }
        }
        
        // Return check messages
        return checks;
    }
    
    /**
     * Produce a String representation of this layer
     * @see Object#toString()
     * @return string representation of the layer
     */
    public String toString() {
        return getLayerName();
//        StringBuffer buf = new StringBuffer();
//        buf.append("Name : ")
//        .append(LayerName)
//        .append(", rows : ")
//        .append(rows)
//        .append(", Bias : ")
//        .append(bias)
//        .append(", Monitor : ")
//        .append(monitor);
//
//        return buf.toString();
    }
    
    /**
     * Method to help remove disused references quickly
     * when the layer goes out of scope.
     * @see Object#finalize()
     * @throws Throwable
     */
    public void finalize() throws Throwable {
        super.finalize();
        LayerName = null;
        bias = null;
        monitor = null;
        if(inputPatternListeners != null) {
            inputPatternListeners.clear();
            inputPatternListeners = null;
        }
        if(outputPatternListeners != null) {
            outputPatternListeners.clear();
            outputPatternListeners = null;
        }
    }
    
    /**
     * Method to get a collection of bias inspections for this layer
     * @return
     */
    public Collection Inspections() {
        Collection col = new ArrayList();
        col.add(new BiasInspection(bias));
        return col;
    }
    
    /**
     * Get the title for the inspectable interface
     * @return
     */
    public String InspectableTitle() {
        return getLayerName();
    }
    
    /**
     * Determine whether this layer has an input synapse attached
     * that is a step counter.
     * @return whether it is a step counter.
     */
    public boolean hasStepCounter() {
        Vector inps = getAllInputs();
        if (inps == null)
            return false;
        for (int x = 0; x < inps.size(); ++x) {
            if (inps.elementAt(x) instanceof InputSynapse) {
                InputSynapse inp = (InputSynapse) inps.elementAt(x);
                if (inp.isStepCounter())
                    return true;
            }
        }
        return false;
    }
    
    /**
     * Determine whether this is an input layer.
     * @return whether this is an input layer
     */
    public boolean isInputLayer() {
        Vector inputListeners = getAllInputs();
        return checkInputs(inputListeners);
    }
    
    /**
     * Determine whether ther are any stream input synapses attached.
     * @param inputListeners Vector to check.
     * @return whether there are any attached StreamInputSynapses
     */
    protected boolean checkInputs(Vector inputListeners) {
        if (inputListeners == null || inputListeners.size() == 0) {
            return true;
        }
        
        for (int x = 0; x < inputListeners.size(); ++x) {
            if (inputListeners.elementAt(x) instanceof StreamInputSynapse) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Determine whether this is an output layer.
     * @return whether this is an output layer
     */
    public boolean isOutputLayer() {
        Vector outputVectors = getAllOutputs();
        return checkOutputs(outputVectors);
    }
    
    /**
     * Determine whether ther are any stream output or teach synapses attached.
     * Also checks the attached listeners of OutputSwitchSynapses.
     * Also checks for loopback condition.
     * All connected synapses must be of this type.
     * @param outputListeners Vector to check.
     * @return whether there are any attached StreamOutputSynapses
     */
    protected boolean checkOutputs(Vector outputListeners) {
        boolean lastListener = false;
        if (outputListeners == null || outputListeners.size() == 0) {
            return true;
        }
        for (int x = 0; x < outputListeners.size(); ++x) {
            if ((outputListeners.elementAt(x) instanceof StreamOutputSynapse)
            || (outputListeners.elementAt(x) instanceof TeachingSynapse)
            || (outputListeners.elementAt(x) instanceof TeacherSynapse))
                lastListener = true;
            else if (outputListeners.elementAt(x) instanceof OutputSwitchSynapse) {
                OutputSwitchSynapse os = (OutputSwitchSynapse) outputListeners.elementAt(x);
                if (checkOutputs(os.getAllOutputs()))
                    lastListener = true;
                else
                    return false;
            } else if (outputListeners.elementAt(x) instanceof Synapse) {
                Synapse syn = (Synapse) outputListeners.elementAt(x);
                if (syn.isLoopBack())
                    lastListener = true;
                else
                    return false;
            }
        }
        return lastListener;
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
    
    /**
     * Initialize the Learner object of this layer
     * @see org.joone.engine.Learnable#initLearner()
     */
    public void initLearner() {
        myLearner = getLearner();
        if(myLearner != null) {
            myLearner.registerLearnable(this);
        }
    }
    
    /**
     * Getter for property myThreadMonitor.
     * @return Value of property myThreadMonitor.
     */
    protected Object getThreadMonitor() {
        if (myThreadMonitor == null)
            myThreadMonitor = new Object();
        return myThreadMonitor;
    }
    
    /** Waits for the current layer's thread to stop
     */
    public void join() {
        try {
            if (myThread != null)
                myThread.join();
        } catch (InterruptedException doNothing) { } catch (NullPointerException doNothing) {
         /* As we cannot synchronize this method, we could get
          * a NullPointerException on calling myThread.join()
          */
        }
    }
    
    /*********************************************************
     * Implementation code for the single-thread version of Joone
     * /*********************************************************
     *
     * /**
     * This method serves to a single forward step
     * when the Layer is called from an external thread
     */
    public void fwdRun(Pattern pattIn) {
        Pattern patt = new Pattern();
        inps = new double[getRows()];
        running = true;
        if (pattIn == null) {
            fireFwdGet();
        } else {
            inps = pattIn.getArray();
        }
        if (running) {
            forward(inps);
            patt.setArray(outs);
            if ((pattIn == null) || (pattIn.getCount() != -1)) {
                patt.setCount(step);
            } else {
                patt.setCount(-1);
            }
            fireFwdPut(patt);
        }
        running = false;
    }
    
    /**
     * This method serves to a single backward step
     * when the Layer is called from an external thread
     */
    public void revRun(Pattern pattIn) {
        Pattern patt = new Pattern();
        gradientInps = new double[getDimension()];
        running = true;
        if (pattIn == null) {
            fireRevGet();
        } else {
            gradientInps = pattIn.getArray();
        }
        if (running) {
            backward(gradientInps);
            patt.setArray(gradientOuts);
            patt.setOutArray(outs);
            patt.setCount(step);
            fireRevPut(patt);
        }
        running = false;
    }
    
}
