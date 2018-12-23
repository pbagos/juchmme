/*
 * NeuralNet.java
 *
 * Created on 17 april 2001, 12.08
 */

package org.joone.net;

import java.util.*;
import java.io.*;
import org.joone.helpers.structure.ConnectionHelper;
import org.joone.helpers.structure.NeuralNetMatrix;
import org.joone.log.*;

import org.joone.engine.*;
import org.joone.engine.learning.*;
import org.joone.io.*;
import org.joone.script.MacroInterface;
import org.joone.exception.JooneRuntimeException;

/** This object represents a container of a neural network,
 * giving to the developer the possibility to manage a
 * neural network as a whole.
 * Thanks to it, a neural network can be saved and restored
 * using an unique writeObject and readObject command, without
 * be worried about its internal composition.
 * Not only this, because using a NeuralNet object, we can
 * also easily transport a neural network on remote machines
 * and runnit there, writing only few and generalized java code.
 *
 */
public class NeuralNet implements NeuralLayer, NeuralNetListener, Serializable {
    
    private static final int MAJOR_RELEASE = 2;
    private static final int MINOR_RELEASE = 0;
    private static final int BUILD = 0;
    private static final String SUFFIX = "RC1";
    
    private static final ILogger log = LoggerFactory.getLogger( NeuralNet.class );
    private Vector layers;
    private String NetName;
    private Monitor mon;
    private Layer inputLayer;
    private Layer outputLayer;
    private ComparingElement teacher;
    
    private static final long serialVersionUID = 8351124226081783962L;
    public static final int INPUT_LAYER = 0;
    public static final int HIDDEN_LAYER = 1;
    public static final int OUTPUT_LAYER = 2;
    
    protected Vector listeners;
    private MacroInterface macroPlugin;
    private boolean scriptingEnabled = false;
    
    private NeuralNetAttributes descriptor = null;
    
    private Hashtable params;
    
    private Layer[] orderedLayers = null;
    private transient Layer[] intOrderedLayers = null;
    
    /** Creates new NeuralNet */
    public NeuralNet() {
        layers = new Vector();
        // Creates a monitor with a back-reference to this neural-net
        mon = new Monitor();
    }
    
    /**
     * Starts all the Layers' threads, in order
     * to prepare the launch of the neural network
     * in multi-thread mode.
     * DO NOT use for single-thread mode.
     */
    public void start() {
        this.terminate(false);
        if (readyToStart()) {
            getMonitor().addNeuralNetListener(this, false);
            Layer ly = null;
            int i;
            try {
                for (i=0; i < layers.size(); ++i) {
                    ly = (Layer)layers.elementAt(i);
                    ly.start();
                }
            } catch (RuntimeException rte) {
                // If some layer can't start, resets the state of the NN
                this.stop();
                String msg;
                log.error(msg = "RuntimeException was thrown while starting the neural network. Message is:" + rte.getMessage(), rte);
                throw new JooneRuntimeException(msg, rte);
            }
        } else {
            String msg;
            log.warn(msg = "NeuralNet: The neural net is already running");
            throw new JooneRuntimeException(msg);
        }
    }
    
    
    /**
     * Check if the neural net is ready to be started again
     * @return true only if there isn't any running layer
     */
    private boolean readyToStart() {
        for (int i=0; i < 100; ++i) {
            if (!this.isRunning())
                return true;
            else
                try {
                    // waits for 10 millisecs
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    return false;
                }
        }
        return false;
    }
    
    /** Waits for all the termination of all running Threads
     * @see Thread.join()
     */
    public void join() {
        if (getMonitor().isSingleThreadMode()) {
            if (getSingleThread() != null) {
                try {
                    getSingleThread().join();
                } catch (InterruptedException doNothing) { }
            }
        } else {
            for (int i=0; i < layers.size(); ++i) {
                Layer ly = (Layer)layers.elementAt(i);
                ly.join();
            }
            if (teacher != null)
                teacher.getTheLinearLayer().join();
        }
    }
    
    /** Terminates the execution of this NeuralNet
     * independently from the threading mode activated.
     */
    public void stop() {
        if (getMonitor().isSingleThreadMode()) {
            this.stopFastRun();
        } else {
            getMonitor().Stop();
        }
    }
    
    /**
     * Terminates the execution of all the threads
     * of the neural network.
     * Used to force a neural network independently
     * from its internal state.
     * Use ONLY in multi-thread mode, and ONLY when
     * the call to the stop() method doesn't give
     * the expected results.
     * @param notify if true, the netStopped event is raised
     */
    public void terminate(boolean notify) {
        if (isRunning()) {
            Layer ly = null;
            int i;
            for (i=0; i < layers.size(); ++i) {
                ly = (Layer)layers.elementAt(i);
                ly.stop();
            }
            if (teacher != null) {
                teacher.getTheLinearLayer().stop();
                if (teacher instanceof AbstractTeacherSynapse) {
                    ((AbstractTeacherSynapse)teacher).netStoppedError();
                }
            }
            if ((getMonitor() != null) && (notify))
                new NetStoppedEventNotifier(getMonitor()).start();
        }
    }
    
    /**
     * Terminates the execution of all the threads
     *  of the neural network.
     * @see this.terminate(boolean notify)
     */
    public void terminate() {
        this.terminate(true);
    }
    
    protected int getNumOfstepCounters() {
        int count = 0;
        for (int i=0; i < layers.size(); ++i) {
            Layer ly = (Layer)layers.elementAt(i);
            if (ly.hasStepCounter())
                ++count;
        }
        if (teacher != null) {
            if ((teacher.getDesired() != null) && (teacher.getDesired().isStepCounter()))
                ++count;
        }
        return count;
    }
    
    /** Returns the input layer of the network.
     * If the method setInputLayer has been never invoked, the
     * input layer is found, set and returned.
     */
    public Layer getInputLayer() {
        if (inputLayer != null)
            return inputLayer;
        setInputLayer(findInputLayer());
        return inputLayer;
    }
    
    /** Returns the input layer, by searching for it following
     * the rules written in Layer.isInputLayer. Ignores any
     * previous call made to setInputLayer.
     */
    public Layer findInputLayer() {
        Layer input = null;
        if (layers == null)
            return null;
        for (int i=0; i < layers.size(); ++i) {
            Layer ly = (Layer)layers.elementAt(i);
            if (ly.isInputLayer()) {
                input = ly;
                break;
            }
        }
        return input;
    }
    
    /** Returns the output layer of the network.
     * If the method setOutputLayer has been never invoked, the
     * output layer is found, set and returned.
     */
    public Layer getOutputLayer() {
        if (outputLayer != null)
            return outputLayer;
        setOutputLayer(findOutputLayer());
        return outputLayer;
    }
    
    /** Returns the output layer by searching for it following
     * the rules written in Layer.isOutputLayer. Ignores any
     * previous call made to setOutputLayer.
     */
    public Layer findOutputLayer() {
        Layer output = null;
        if (layers == null)
            return null;
        for (int i=0; i < layers.size(); ++i) {
            Layer ly = (Layer)layers.elementAt(i);
            if (ly.isOutputLayer()) {
                output = ly;
                break;
            }
        }
        return output;
    }
    
    
    public int getRows() {
        Layer ly = this.getInputLayer();
        if (ly != null)
            return ly.getRows();
        else
            return 0;
    }
    
    public void setRows(int p1) {
        Layer ly = this.getInputLayer();
        if (ly != null)
            ly.setRows(p1);
    }
    
    public void addNoise(double p1) {
        Layer ly;
        int i;
        for (i=0; i < layers.size(); ++i) {
            ly = (Layer)layers.elementAt(i);
            ly.addNoise(p1);
        }
    }
    
    public void randomize(double amplitude) {
        Layer ly;
        int i;
        for (i=0; i < layers.size(); ++i) {
            ly = (Layer)layers.elementAt(i);
            ly.randomize(amplitude);
        }
    }
    
    public Matrix getBias() {
        Layer ly = this.getInputLayer();
        if (ly != null)
            return ly.getBias();
        else
            return null;
    }
    
    public Vector getAllOutputs() {
        Layer ly = this.getOutputLayer();
        if (ly != null)
            return ly.getAllOutputs();
        else
            return null;
    }
    
    public String getLayerName() {
        return NetName;
    }
    
    public void removeOutputSynapse(OutputPatternListener p1) {
        Layer ly = this.getOutputLayer();
        if (ly != null)
            ly.removeOutputSynapse(p1);
    }
    
    public void setAllInputs(Vector p1) {
        Layer ly = this.getInputLayer();
        if (ly != null)
            ly.setAllInputs(p1);
    }
    
    public void removeAllOutputs() {
        Layer ly = this.getOutputLayer();
        if (ly != null)
            ly.removeAllOutputs();
        setTeacher(null);
    }
    
    public Vector getAllInputs() {
        Layer ly = this.getInputLayer();
        if (ly != null)
            return ly.getAllInputs();
        else
            return null;
    }
    
    public boolean addOutputSynapse(OutputPatternListener p1) {
        Layer ly = this.getOutputLayer();
        if (ly != null)
            return ly.addOutputSynapse(p1);
        else
            return false;
    }
    
    public void setBias(Matrix p1) {
        Layer ly = this.getInputLayer();
        if (ly != null)
            ly.setBias(p1);
    }
    
    public void removeInputSynapse(InputPatternListener p1) {
        Layer ly = this.getInputLayer();
        if (ly != null)
            ly.removeInputSynapse(p1);
    }
    
    public void setLayerName(String p1) {
        NetName = p1;
    }
    
    public boolean addInputSynapse(InputPatternListener p1) {
        Layer ly = this.getInputLayer();
        if (ly != null)
            return ly.addInputSynapse(p1);
        else
            return false;
    }
    
    public void setAllOutputs(Vector p1) {
        Layer ly = this.getOutputLayer();
        if (ly != null)
            ly.setAllOutputs(p1);
    }
    
    public void setMonitor(Monitor p1) {
        mon = p1;
        for (int i=0; i < layers.size(); ++i) {
            Layer ly = (Layer)layers.elementAt(i);
            ly.setMonitor(mon);
        }
        setScriptingEnabled(isScriptingEnabled());
        if (getTeacher() != null)
            getTeacher().setMonitor(p1);
    }
    
    public Monitor getMonitor() {
        return mon;
    }
    
    public void removeAllInputs() {
        Layer ly = this.getInputLayer();
        if (ly != null)
            ly.removeAllInputs();
    }
    
    public NeuralLayer copyInto(NeuralLayer p1) {
        return null;
    }
    
    public void addLayer(Layer layer) {
        this.addLayer(layer, HIDDEN_LAYER);
    }
    
    public void addLayer(Layer layer, int tier) {
        if (!layers.contains(layer)) {
            layer.setMonitor(mon);
            layers.addElement(layer);
        }
        if (tier == INPUT_LAYER)
            setInputLayer(layer);
        if (tier == OUTPUT_LAYER)
            setOutputLayer(layer);
    }
    
    public void removeLayer(Layer layer) {
        if (layers.contains(layer)) {
            layers.removeElement(layer);
            // Remove the synapses
            NeuralNetMatrix matrix = new NeuralNetMatrix(this);
            Synapse[][] conn = matrix.getConnectionMatrix();
            removeSynapses(matrix.getLayerInd(layer), conn);
            
            if (layer == inputLayer)
                setInputLayer(null);
            else
                if (layer == outputLayer)
                    setOutputLayer(null);
        }
    }
    
    private void removeSynapses(int ind, Synapse[][] conn) {
        if (ind >= 0) {
            // Removes input synapses
            for (int i=0; i < conn.length; ++i) {
                if (conn[i][ind] != null) {
                    ConnectionHelper.disconnect(layers.get(i), layers.get(ind));
                }
            }
            // Removes output synapses
            for (int i=0; i < conn[0].length; ++i) {
                if (conn[ind][i] != null) {
                    ConnectionHelper.disconnect(layers.get(ind), layers.get(i));
                }
            }
        }
    }
    
    /**
     * Resets all the StreamInputLayer of the net
     */
    public void resetInput() {
        Layer ly = null;
        int i;
        for (i=0; i < layers.size(); ++i) {
            ly = (Layer)layers.elementAt(i);
            Vector inputs = ly.getAllInputs();
            if (inputs != null)
                for (int x=0; x < inputs.size(); ++x) {
                InputPatternListener syn = (InputPatternListener)inputs.elementAt(x);
                if (syn instanceof StreamInputSynapse)
                    ((StreamInputSynapse)syn).resetInput();
                //                    if (syn instanceof InputSwitchSynapse)
                //                        ((InputSwitchSynapse)syn).resetInput();
                }
        }
        if (getTeacher() != null)
            getTeacher().resetInput();
    }
    
    public void addNeuralNetListener(NeuralNetListener listener) {
        if (getListeners().contains(listener))
            return;
        listeners.addElement(listener);
        if (getMonitor() != null)
            getMonitor().addNeuralNetListener(listener);
    }
    
    public Vector getListeners() {
        if (listeners == null)
            listeners = new Vector();
        return listeners;
    }
    
    public void removeNeuralNetListener(NeuralNetListener listener) {
        getListeners().removeElement(listener);
        if (getMonitor() != null)
            getMonitor().removeNeuralNetListener(listener);
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        /* Since the listeners' vector in the Monitor object is transient,
         * we must fill it from the NeuralNet.listeners vector
         */
        Vector lst = getListeners();
        if (getMonitor() != null)
            for (int i=0; i < lst.size(); ++i) {
            getMonitor().addNeuralNetListener((NeuralNetListener)lst.elementAt(i));
            }
        // Restores the exported variables jNet and jMon
        setMacroPlugin(macroPlugin);
    }
    
    /**
     * Method to get the version.
     * @return A string containing the version of joone's engine in the format xx.yy.zz
     */
    public static String getVersion() {
        return MAJOR_RELEASE +
                "." +
                MINOR_RELEASE +
                "." +
                BUILD + SUFFIX;
    }
    
    /**
     * Method to get the numeric version.
     * @return an integer containing the joone's engine version
     */
    public static Integer getNumericVersion() {
        return new Integer(MAJOR_RELEASE * 1000000 +
                MINOR_RELEASE * 1000 +
                BUILD);
    }
    
    public Layer getLayer(String layerName) {
        Layer ly = null;
        for (int i=0; i < layers.size(); ++i) {
            ly = (Layer)layers.elementAt(i);
            if (ly.getLayerName().compareToIgnoreCase(layerName) == 0)
                return ly;
        }
        return null;
    }
    
    public Vector getLayers() {
        return this.layers;
    }
    
    /** Permits to initialize a neural network with a Vector
     *  containing layers.
     *
     */
    public void setLayers(Vector newlayers) {
        this.layers = newlayers;
    }
    
    /** Permits to initialize a neural network with an
     *  ArrayList containing layers. Added for Spring.
     *
     */
    public void setLayersList(ArrayList list) {
        this.setLayers(new Vector(list));
    }
    
    /** Sets the Teacher for this NeuralNet object
     * @param TeachingSynapse - the new teacher. It can be null to make unsupervised this neural network
     */
    public void setTeacher(ComparingElement ts) {
        if (getMonitor() != null)
            if (ts != null)
                getMonitor().setSupervised(true);
            else
                getMonitor().setSupervised(false);
        teacher = ts;
    }
    
    public ComparingElement getTeacher() {
        return teacher;
    }
    
    public void setListeners(Vector listeners) {
        //addNeuralNetListener(listeners);
    }
    
    public void setInputLayer(Layer newLayer) {
        inputLayer = newLayer;
    }
    
    public void setOutputLayer(Layer newLayer) {
        outputLayer = newLayer;
    }
    
    public NeuralNetAttributes getDescriptor() {
        if (descriptor == null) {
            descriptor = new NeuralNetAttributes();
            descriptor.setNeuralNetName(this.getLayerName());
        }
        return descriptor;
    }
    
    public void setDescriptor(NeuralNetAttributes newdescriptor) {
        this.descriptor = newdescriptor;
    }
    
    /**
     * Returns true if the network is running
     * @return boolean
     */
    public boolean isRunning() {
        if (getMonitor().isSingleThreadMode()) {
            if ((getSingleThread() != null) && getSingleThread().isAlive()) {
                return true;
            }
        } else {
            Layer ly = null;
            for (int i=0; i < layers.size(); ++i) {
                ly = (Layer)layers.elementAt(i);
                if (ly.isRunning()) {
                    return true;
                }
            }
            if ((teacher != null) && (teacher.getTheLinearLayer().isRunning())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Creates a copy of the contained neural network
     *
     * @return the cloned NeuralNet
     */
    public NeuralNet cloneNet() {
        NeuralNet newnet = null;
        try {
            ByteArrayOutputStream f = new ByteArrayOutputStream();
            ObjectOutput s = new ObjectOutputStream(f);
            s.writeObject(this);
            s.flush();
            ByteArrayInputStream fi = new ByteArrayInputStream(f.toByteArray());
            ObjectInput oi = new ObjectInputStream(fi);
            newnet = (NeuralNet)oi.readObject();
        } catch (Exception ioe) {
            log.warn( "IOException while cloning the Net. Message is : " + ioe.getMessage(),
                    ioe );
        }
        return newnet;
    }
    
    public void removeAllListeners() {
        listeners = null;
        if (getMonitor() != null)
            getMonitor().removeAllListeners();
    }
    
    /** Enable/disable the scripting engine for the net.
     * If disabled, all the event-driven macros will be not run
     * @param enabled true to enable the scripting, otherwise false
     */
    public void setScriptingEnabled(boolean enabled) {
        scriptingEnabled = enabled;
        if (enabled) {
            NeuralNetListener listener = getMacroPlugin();
            if (listener == null)
                log.info("MacroPlugin not set: Impossible to enable the scripting");
            else
                this.addNeuralNetListener(getMacroPlugin());
        } else {
            if (macroPlugin != null)
                this.removeNeuralNetListener(macroPlugin);
        }
    }
    
    /** Gets if the scripting engine is enabled
     * @return true if enabled
     */
    public boolean isScriptingEnabled() {
        return scriptingEnabled;
    }
    
    /** Getter for property macroPlugin.
     * @return Value of property macroPlugin.
     */
    public MacroInterface getMacroPlugin() {
        return macroPlugin;
    }
    
    /** Setter for property macroPlugin.
     * @param macroPlugin New value of property macroPlugin.
     */
    public void setMacroPlugin(MacroInterface macroPlugin) {
        if(macroPlugin != null) {
            // Unregister the old listener
            this.removeNeuralNetListener(this.macroPlugin);
            
            // Should we register the new listener?
            if(scriptingEnabled) {
                this.addNeuralNetListener(macroPlugin);
            }
        }
        
        this.macroPlugin = macroPlugin;
        if (macroPlugin != null) {
            macroPlugin.set("jNet", this);
            macroPlugin.set("jMon", getMonitor());
        }
    }
    
    /** Gets a custom parameter from the neural net.
     * The user is free to use the custom parameters as s/he wants.
     * They are useful to transport a whatever value along with the net.
     * @param key The searched key
     * @return The value of the parameter if found, otherwise null
     */
    public Object getParam(String key) {
        if (params == null)
            return null;
        return params.get(key);
    }
    
    /** Sets a custom parameter of the neural net.
     * The user is free to use the custom parameters as s/he wants.
     * They are useful to transport a whatever value along with the net.
     * @param key The key of the param
     * @param obj The value of the param
     */
    public void setParam(String key, Object obj) {
        if (params == null)
            params = new Hashtable();
        if (params.containsKey(key))
            params.remove(key);
        params.put(key, obj);
    }
    
    /** Return all the keys of the parameters contained in the net.
     * @return An array of Strings containing all the keys if found, otherwise null
     */
    public String[] getKeys() {
        if (params == null)
            return null;
        String[] keys = new String[params.keySet().size()];
        Enumeration myEnum = params.keys();
        for (int i=0; myEnum.hasMoreElements(); ++i) {
            keys[i] = (String)myEnum.nextElement();
        }
        return keys;
    }
    
    /**
     * Compiles all layers' check messages.
     *
     * @see NeuralLayer
     * @return validation errors.
     */
    public TreeSet check() {
        
        // Prepare an empty set for check messages;
        TreeSet checks = new TreeSet();
        
        // Check for an empty neural network
        if ((layers == null) || layers.isEmpty()) {
            checks.add(new NetCheck(NetCheck.FATAL, "The Neural Network doesn't contain any layer", mon));
            // If empty it makes no sense to continue the check
            return checks;
        } else
            // Check for the presence of more than one InputSynpase having stepCounter set to true
            if (getNumOfstepCounters() > 1)
                checks.add(new NetCheck(NetCheck.FATAL, "More than one InputSynapse having stepCounter set to true is present", mon));
        
        // Check all layers.
        for (int i = 0; i < layers.size(); i++) {
            Layer layer = (Layer) layers.elementAt(i);
            checks.addAll(layer.check());
        }
        
        // Check the teacher (only if it exists and this is not a nested neural network)
        if (mon.getParent() == null) {
            if (teacher != null) {
                checks.addAll(teacher.check());
                if (mon != null && mon.isLearning() && !mon.isSupervised())
                    checks.add(new NetCheck(NetCheck.WARNING, "Teacher is present: the supervised property should be set to true", mon));
            } else {
                if (mon != null && mon.isLearning() && mon.isSupervised())
                    checks.add(new NetCheck(NetCheck.FATAL, "Teacher not present: set to false the supervised property", mon));
            }
        }
        
        
        // Check the Monitor.
        if (mon != null) {
            checks.addAll(mon.check());
        }
        
        // Return check messages.
        return checks;
    }
    
    // NET LISTENER METHODS
    
    /**
     * Not implemented.
     * @param e
     */
    public void netStarted(NeuralNetEvent e) {
    }
    
    /**
     * Not implemented.
     * @param e
     */
    public void cicleTerminated(NeuralNetEvent e) {
    }
    
    /**
     * Not implemented.
     * @param e
     */
    public void netStopped(NeuralNetEvent e) {
    }
    
    /**
     * Not implemented.
     * @param e
     */
    public void errorChanged(NeuralNetEvent e) {
    }
    
    /**
     * Stops the execution threads and resets all the layers
     * in the event of an crtitical network error.
     * @param error The error message.
     * @param e The event source of this event.
     */
    public void netStoppedError(NeuralNetEvent e, String error) {
        // Stop and reset all the Layers.
        this.terminate(false);
    }
        
    /**
     * This method permits to set externally a particular order to
     * traverse the Layers. If not used, the order will be calculated
     * automatically. Use this method in cases where the automatic
     * ordering doesn't work (e.g. in case of complex recurrent connections)
     * NOTE: if you set this property, you're responsible to update the array
     * whenever a layer is added/removed.
     * @param orderedLayers an array containing the ordered layers
     */
    public void setOrderedLayers(Layer[] orderedLayers) {
        this.orderedLayers = orderedLayers;
    }
    
    public Layer[] getOrderedLayers() {
        return orderedLayers;
    }
    
    /**
     * This method calculates the order of the layers of the network, from the input to the output.
     * If the setOrderedLayers method has been invoked before, that array will be returned, otherwise
     * the order will be calculated automatically.
     * @return An array containing the ordered Layers, from the input to the output (i.e. layers[0]=input layer, layers[n-1]=output layer.
     */
    public Layer[] calculateOrderedLayers() {
        if (getOrderedLayers() == null) {
            if (intOrderedLayers == null) {
                NeuralNetMatrix matrix = new NeuralNetMatrix(this);
                intOrderedLayers = matrix.getOrderedLayers();
            }
        } else {
            intOrderedLayers = getOrderedLayers();
        }
        return intOrderedLayers;
    }
    
    /** Runs the network.
     * @param singleThreadMode If true, runs the network in single thread mode
     * @param sync If true, runs the network in a separated thread and returns immediately.
     */
    public void go(boolean singleThreadMode, boolean sync) {
        getMonitor().setSingleThreadMode(singleThreadMode);
        this.go(sync);
    }
    
    private transient Thread singleThread = null;
    
    /** Runs the network. The running mode is determined by
     * the value of the singleThreadMode property.
     * @param sync If true, runs the network in a separated thread and returns immediately.
     */
    public void go(boolean sync) {
        if (getMonitor().isSingleThreadMode()) {
            Runnable runner = new Runnable() {
                public void run() {
                    fastRun();
                }
            };
            setSingleThread(new Thread(runner));
            getSingleThread().start();
        } else {
            this.start();
            getMonitor().Go();
        }
        // If launched in synch mode, waits for the thread completition
        if (sync) {
            this.join();
        }
    }
    
    /** Runs the network in async mode (i.e. equivalent to go(false) ).
     * The running mode is determined by the value of the singleThreadMode property.
     */
    public void go() {
        this.go(false);
    }
    
    /** Continue the execution of the network after the stop() method is called.
     */
    public void restore() {
        if (getMonitor().isSingleThreadMode()) {
            Runnable runner = new Runnable() {
                public void run() {
                    fastContinue();
                }
            };
            setSingleThread(new Thread(runner));
            getSingleThread().start();
        } else {
            this.start();
            getMonitor().runAgain();
        }
    }
    
    /*********************************************************
     * Implementation code for the single-thread version of Joone
     *********************************************************/
    private boolean stopFastRun;
    
    /* This method runs the neural network in single-thread mode.
     */
    protected void fastRun() {
        this.fastRun(getMonitor().getTotCicles());
    }
    
    /* This method restore the running of the neural network
     * in single-thread mode, starting from the epoch at which
     * it had been previously stopped.
     */
    protected void fastContinue() {
        this.fastRun(getMonitor().getCurrentCicle());
    }
    
    /* This method runs the neural network in single-thread mode
     * starting from the epoch passed as parameter.
     * NOTE: the network will count-down from firstEpoch to 0.
     * @param firstEpoch the epoch from which the network will start.
     */
    protected void fastRun(int firstEpoch) {
        Monitor mon = getMonitor();
        mon.setSingleThreadMode(true);
        int epochs = firstEpoch;
        int patterns = mon.getNumOfPatterns();
        Layer[] ordLayers = calculateOrderedLayers();
        int layers = ordLayers.length;
        // Calls the init method for all the Layers
        for (int ly=0; ly < layers; ++ly) {
            ordLayers[ly].init();
        }
        stopFastRun = false;
        mon.fireNetStarted();
        for (int epoch=epochs; epoch > 0 ; --epoch) {
            mon.setCurrentCicle(epoch);
            for (int p=0; p < patterns; ++p) {
                // Forward
                stepForward(null);
                if (getMonitor().isLearningCicle(p+1)) {
                    // Backward
                    stepBackward(null);
                }
            }
            mon.fireCicleTerminated();
            if (stopFastRun) {
                break;
            }
        }
        Pattern stop = new Pattern(new double[ordLayers[0].getRows()]);
        stop.setCount(-1);
        stepForward(stop);
        mon.fireNetStopped();
    }
    
    /* Use this method to perform a single step forward.
     * The network is interrogated using the next available
     * input pattern (only one).
     * @param pattern The input pattern to use. If null, the input pattern is read from the input synapse connected to the input layer.
     */
    protected void singleStepForward(Pattern pattern) {
        getMonitor().setSingleThreadMode(true);
        Layer[] ordLayers = calculateOrderedLayers();
        int layers = ordLayers.length;
        // Calls the init method for all the layers
        for (int ly=0; ly < layers; ++ly) {
            ordLayers[ly].init();
        }
        this.stepForward(pattern);
    }
    
    /* Use this method to perform a single step backward.
     * The pattern passed as parameter, that is backpropagated, must contain
     * the error in terms of differences from the desired pattern.
     * @param pattern The error pattern to backpropagate. If null, the pattern is read from the teacher connected to the output layer.    */
    protected void singleStepBackward(Pattern error) {
        getMonitor().setSingleThreadMode(true);
        this.stepBackward(error);
    }
    
    protected void stepForward(Pattern pattern) {
        Layer[] ordLayers = calculateOrderedLayers();
        int layers = ordLayers.length;
        ordLayers[0].fwdRun(pattern);
        for (int ly=1; ly < layers; ++ly) {
            ordLayers[ly].fwdRun(null);
        }
    }
    
    protected void stepBackward(Pattern error) {
        Layer[] ordLayers = calculateOrderedLayers();
        int layers = ordLayers.length;
        for (int ly=layers; ly > 0; --ly) {
            ordLayers[ly-1].revRun(error);
        }
    }
    
    
    /* This method serves to stop the network
     * when running in single-thread mode.
     * It DOES NOT affect the multi-thread running
     * (i.e. a network launched with Monitor.Go() method.
     */
    protected void stopFastRun() {
        stopFastRun = true;
    }
    
    protected Thread getSingleThread() {
        return singleThread;
    }
    
    protected void setSingleThread(Thread singleThread) {
        this.singleThread = singleThread;
    }
        
}
