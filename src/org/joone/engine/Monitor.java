package org.joone.engine;

import org.joone.net.NetCheck;
import org.joone.net.NeuralNet;

import java.io.*;
import java.util.*;
import org.joone.log.*;

/**
 * The Monitor object is the controller of the behavior of the neural net.
 * It controls the start/stop actions and permits to set the parameters of the net
 * (learning rate, momentum, ecc.).
 * Each component of the neural net (Layers and Synapses) are connected to a monitor object
 * (the monitor can be different or the same for the all components).
 */
public class Monitor implements Serializable {
    private static final long serialVersionUID = 2909501813894146845L;
    
    private int preLearning = 0;
    private boolean learning = false;
    private int currentCicle;
    private int run = 0;
    private int saveCurrentCicle;
    private int saveRun;
        
    // Starting parameters
    private int patterns;  // Training patterns
    private int validationPatterns; // Validation patterns
    private int totCicles;
    private double learningRate;
    private double momentum;
    private double globalError;
    private int batchSize = 0;
    
    /** Use RMSE (if true) for back propagation, MSE (if false) otherwise. */
    private boolean useRMSE = true;
    
    /** The learner factory. If set this factory provides synapses and layers
     * with learners. */
    private LearnerFactory theLearnerFactory = null;
    
    /**
     * @label parent
     */
    private Monitor parent;
    
    /* No removable listeners. They cannot be removed on the removeAllListeners call.
     * This is useful to avoid that permanent internal listeners are removed
     * when the neural network is cloned.
     */
    private transient Vector internalListeners = new Vector();
    
    private transient Vector netListeners = new Vector();
    private transient boolean firstTime = true;
    private transient boolean exporting = false;
    private transient boolean validation = false;
    private transient boolean running = false;
    
    /** The next flag indicates if training data should be used for validation (true), or not (false).
     * The default is false. */
    private transient boolean trainingDataForValidation = false;
    
    private static final ILogger log = LoggerFactory.getLogger( Monitor.class );
    
    private boolean supervisioned = false;
    private boolean singleThreadMode = true;
    
    public int learningMode = 0;
    
    private List learners; // Container of the available Learners
    private Hashtable params;
        
    /**
     * This is the default Constructor.
     */
    public Monitor() {
        firstTime = true;
        netListeners = new Vector();
        internalListeners = new Vector();
        parent = null;
    }
    
    /**
     * adds a neural net event listener the Monitor
     * @param l NeuralNetListener
     */
    public void addNeuralNetListener(NeuralNetListener l) {
        this.addNeuralNetListener(l, true);
    }
    
    /** adds a neural net event listener to the Monitor
     * @param l the new NeuralNetListener
     * @param removable true if the added listener can be removed by the removeAllListeners method call
     */
    public synchronized void addNeuralNetListener(NeuralNetListener l, boolean removable) {
        if (parent != null)
            parent.addNeuralNetListener(l, removable);
        else {
            if (!getListeners().contains(l)) {
                netListeners.addElement(l);
                if (!removable) {
                    if (!getNoDetachListeners().contains(l))
                        getNoDetachListeners().addElement(l);
                }
            }
        }
    }
    
    private Vector getNoDetachListeners() {
        if (internalListeners == null)
            internalListeners = new Vector();
        return internalListeners;
    }
    
    private Vector getListeners() {
        if (netListeners == null)
            netListeners = new Vector();
        return netListeners;
    }
    
    /** Invoked when an epoch finishes
     */
    public void fireCicleTerminated() {
        if (parent != null)
            parent.fireCicleTerminated();
        else {
            int size = getListeners().size();
            if (size == 0)
                return;
            Object[] list;
            synchronized (this) {
                list = getListeners().toArray();
            }
            NeuralNetEvent event = new NeuralNetEvent(this);
            for (int i = 0; i < size; ++i) {
                NeuralNetListener listener = (NeuralNetListener) list[i];
                listener.cicleTerminated(event);
            }
        }
    }
    
    /** Invoked when the net starts
     */
    public void fireNetStarted() {
        if (parent != null)
            parent.fireNetStarted();
        else {
            int size = getListeners().size();
            
            if (size == 0)
                return;
            
            Object[] list;
            synchronized (this) {
                list = getListeners().toArray();
            }
            NeuralNetEvent event = new NeuralNetEvent(this);
            for (int i = 0; i < list.length; ++i) {
                NeuralNetListener listener = (NeuralNetListener) list[i];
                listener.netStarted(event);
            }
        }
    }
    
    /** Invoked when all the epochs finish
     */
    public void fireNetStopped() {
        if (parent != null)
            parent.fireNetStopped();
        else {
            int size = getListeners().size();
            if (size == 0)
                return;
            
            Object[] list;
            synchronized (this) {
                list = getListeners().toArray();
            }
            NeuralNetEvent event = new NeuralNetEvent(this);
            
            for (int i = 0; i < list.length; ++i) {
                NeuralNetListener listener = (NeuralNetListener) list[i];
                listener.netStopped(event);
            }
        }
    }
    
    /** Invoked when an error occurs
     * @param errMsg the thrown error message
     */
    public void fireNetStoppedError(String errMsg) {
        if (parent != null)
            parent.fireNetStoppedError(errMsg);
        else {
            int size = getListeners().size();
            
            if (size == 0)
                return;
            
            Object[] list;
            synchronized (this) {
                list = getListeners().toArray();
            }
            NeuralNetEvent event = new NeuralNetEvent(this);
            
            for (int i = 0; i < list.length; ++i) {
                NeuralNetListener listener = (NeuralNetListener) list[i];
                listener.netStoppedError(event,errMsg);
            }
            if (running) {
                log.error("Neural net stopped due to the following error: "+errMsg);
                log.debug("\tepoch:"+currentCicle);
                log.debug("\tcycle:"+run);
                log.debug("\tlearning:"+isLearning());
                log.debug("\tvalidation:"+isValidation());
                log.debug("\ttrainingPatterns:"+getTrainingPatterns());
                log.debug("\tvalidationPatterns:"+getValidationPatterns());
            }
        }
    }
    
    /** Invoked when the GlobalError changes
     */
    public void fireErrorChanged() {
        if (parent != null)
            parent.fireErrorChanged();
        else {
            int size = getListeners().size();
            
            if (size == 0)
                return;
            
            Object[] list;
            synchronized (this) {
                list = getListeners().toArray();
            }
            NeuralNetEvent event = new NeuralNetEvent(this);
            
            for (int i = 0; i < list.length; ++i) {
                NeuralNetListener listener = (NeuralNetListener) list[i];
                listener.errorChanged(event);
            }
        }
    }
    
    /** Returns the current epoch
     * @return int
     */
    public synchronized int getCurrentCicle() {
        if (parent != null)
            return parent.getCurrentCicle();
        else
            return currentCicle;
    }
    
    /** Returns the actual (R)MSE of the NN
     * @return double
     */
    public double getGlobalError() {
        if (parent != null)
            return parent.getGlobalError();
        else
            return globalError;
    }
    
    /** Returns the learning rate
     * @return double
     */
    public synchronized double getLearningRate() {
        if (parent != null)
            return parent.getLearningRate();
        else
            return learningRate;
    }
    
    /** Returns the momentum
     * @return double
     */
    public double getMomentum() {
        if (parent != null)
            return parent.getMomentum();
        else
            return momentum;
    }
    
    /** Returns the number of the input training patterns
     * @return int
     */
    public int getTrainingPatterns() {
        if (parent != null)
            return parent.getTrainingPatterns();
        else
            return patterns;
    }
    
    /** Sets the number of the input training patterns
     * @param newPatterns int
     */
    public void setTrainingPatterns(int newPatterns) {
        if (parent != null)
            parent.setTrainingPatterns(newPatterns);
        else
            patterns = newPatterns;
    }
    
    /** Returns the initial ignored input patterns (during the training phase)
     * @return int
     */
    public int getPreLearning() {
        if (parent != null)
            return parent.getPreLearning();
        else
            return preLearning;
    }
    
    /** Returns the actual elaborated pattern
     * @return int
     */
    public synchronized int getStep() {
        if (parent != null)
            return parent.getStep();
        else
            return run;
    }
    
    /** Returns the total number of epochs
     * @return int
     */
    public int getTotCicles() {
        if (parent != null)
            return parent.getTotCicles();
        else
            return totCicles;
    }
    
    
    /**
     * Runs the neural net in multi-thread mode.
     * WARNING: AVOID to invoke this method. Use instead NeuralNet.go()
     *
     * @see org.joone.net.NeuralNet.go()
     */
    public synchronized void Go() {
        if (parent != null)
            parent.Go();
        else {
            setSingleThreadMode(false);
            run = getNumOfPatterns();
            currentCicle = totCicles;
            firstTime = false;
            running = true;
            notifyAll();
        }
    }
    
    /**
     * Returns TRUE if the net is into a learning phase
     * @return boolean
     */
    public boolean isLearning() {
        return learning;
    }
    
    /** Returns the phase of the net (learning or not) for the current pattern
     * @return boolean
     * @param num the pattern requested
     */
    public boolean isLearningCicle(int num) {
        if (parent != null) {
            boolean learn = parent.isLearningCicle(num);
            return (learn & isLearning());
        } else
            if (num > preLearning)
                return isLearning();
            else
                return false;
    }
    
    public synchronized void resetCycle()  {
        run = 0;
    }
    
    
    /** Returns if the next pattern must be elaborated
     * @return boolean
     */
    public synchronized boolean nextStep() {
        if (parent != null)
            return parent.nextStep();
        else {
            while (run == 0) {
                try {
                    if (!firstTime) {
                        if (currentCicle > 0) {
                            fireCicleTerminated();
                            --currentCicle;
                            if(currentCicle < 0) {
                                // currentCicle might be smaller than 0 here if someone
                                // calls Monitor.Stop in a cicleTerminated() method (which
                                // is called by the fireCicleTerminated() method)
                                currentCicle = 0;
                            }
                            run = getNumOfPatterns();
                        }
                        if (currentCicle == 0) {
                            running = false;
                            if (!this.isSupervised() || (!this.isLearning() && !this.isValidation()))
                                new NetStoppedEventNotifier(this).start();
                            if (saveRun == 0) {
                                saveRun = getNumOfPatterns();
                                saveCurrentCicle = totCicles;
                            }
                            run = 0;
                            firstTime = true;
                            return false;
                            //wait();
                        }
                    } else
                    /* If it goes here, it means that this method
                     * has been called first to call Go() or runAgain() */
                        wait();
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                    run = 0;
                    firstTime = true;
                    return false;
                }
            }
            if ((run == getNumOfPatterns()) && (currentCicle == totCicles))
                fireNetStarted();
            if (run > 0)
                --run;
            return true;
        }
    }
    
    protected Object readResolve() {
        firstTime = true;
        return this;
    }
    
    /** Removes a listener
     * @param l the listener to be removed
     */
    public synchronized void removeNeuralNetListener(NeuralNetListener l) {
        if (parent != null)
            parent.removeNeuralNetListener(l);
        else {
            getListeners().removeElement(l);
            getNoDetachListeners().removeElement(l);
        }
    }
    
    /**
     * Let continue the net.
     */
    public synchronized void runAgain() {
        if (parent != null)
            parent.runAgain();
        else {
            run = getNumOfPatterns(); // old: run = saveRun;
            currentCicle = saveCurrentCicle;
            firstTime = false;
            running = true;
            notifyAll();
        }
    }
    
    /** Not used
     * @param newCurrentCicle int
     */
    public void setCurrentCicle(int newCurrentCicle) {
        if (parent != null)
            parent.setCurrentCicle(newCurrentCicle);
        else
            currentCicle = newCurrentCicle;
    }
    
    /** Sets the actual error of the NN
     * @param newGlobalError double
     */
    public void setGlobalError(double newGlobalError) {
        if (parent != null)
            parent.setGlobalError(newGlobalError);
        else {
            globalError = newGlobalError;
            this.fireErrorChanged();
        }
    }
    
    /** Sets the phase of the neural network: learning (true) or recall (false)
     * @param newLearning boolean
     */
    public void setLearning(boolean newLearning) {
        learning = newLearning;
    }
    
    /** Sets the learning rate
     * @param newLearningRate double
     */
    public void setLearningRate(double newLearningRate) {
        if (parent != null)
            parent.setLearningRate(newLearningRate);
        else
            learningRate = newLearningRate;
    }
    
    /** Sets the momentum
     * @param newMomentum double
     */
    public void setMomentum(double newMomentum) {
        if (parent != null)
            parent.setMomentum(newMomentum);
        else
            momentum = newMomentum;
    }
    
    /** Sets the initial ignored input patterns (during the training phase)
     * @param newPreLearning int
     */
    public void setPreLearning(int newPreLearning) {
        if (parent != null)
            parent.setPreLearning(newPreLearning);
        else
            preLearning = newPreLearning;
    }
    
    /** Sets the total number of epochs
     * @param newTotCicles int
     */
    public void setTotCicles(int newTotCicles) {
        if (parent != null)
            parent.setTotCicles(newTotCicles);
        else
            totCicles = newTotCicles;
    }
    
    /** Stops the NN when running in multi-thread mode.
     * WARNING: DO NOT INVOKE directly, use instead NeuralNet.stop()
     * 
     * @see org.joone.net.NeuralNet.stop()
     */
    public synchronized void Stop() {
        if (parent != null)
            parent.Stop();
        else {
            saveRun = run;
            saveCurrentCicle = currentCicle;
            run = 0;
            currentCicle = 0;
        }
    }
    
    /** Getter for property exporting.
     * @return Value of property exporting.
     */
    public boolean isExporting() {
        if (parent != null)
            return parent.isExporting();
        else
            return exporting;
    }
    
    /** Setter for property exporting.
     * @param exporting New value of property exporting.
     */
    public void setExporting(boolean exporting) {
        if (parent != null)
            parent.setExporting(exporting);
        else
            this.exporting = exporting;
    }
    
    
    /**
     * Needed for XML saving
     */
    public int getRun() {
        return run;
    }
    
    /** Getter for property validation.
     * @return Value of property validation.
     */
    public boolean isValidation() {
        if (parent != null)
            return parent.isValidation();
        else
            return validation;
    }
    
    /** Setter for property validation.
     * @param validation New value of property validation.
     */
    public void setValidation(boolean validation) {
        if (parent != null)
            parent.setValidation(validation);
        else
            this.validation = validation;
    }
    
    /**
     * Getter for the property trainingDataForValidation, i.e. should
     * the training data be used for validation.
     *
     * @return true if the training data should be used, false otherwise.
     */
    public boolean isTrainingDataForValidation() {
        if (parent != null) {
            return parent.isTrainingDataForValidation();
        } else {
            return trainingDataForValidation;
        }
    }
    
    /**
     * Setter for the property trainingDataForValidation.
     *
     * @param aMode true if the training data should be used for validation,
     * false otherwise.
     */
    public void setTrainingDataForValidation(boolean aMode) {
        if(parent != null) {
            parent.setTrainingDataForValidation(aMode);
        } else {
            trainingDataForValidation = aMode;
        }
    }
    
    /** Removes all the NeuralNetListeners. Removes only the listeners marked as removable
     * @see addNeuralNetListener
     */
    public void removeAllListeners() {
        if (parent != null)
            parent.removeAllListeners();
        else
            if (internalListeners != null)
                netListeners = (Vector)internalListeners.clone();
            else
                netListeners = null;
    }
    
    /** Getter for property parent.
     * @return Value of property parent.
     *
     */
    public Monitor getParent() {
        return parent;
    }
    
    /** Setter for property parent.
     * @param parent New value of property parent.
     *
     */
    public void setParent(Monitor parent) {
        this.parent = parent;
    }
    
    /** Returns the number of the input validation patterns
     * @return int
     */
    public int getValidationPatterns() {
        if (parent != null)
            return parent.getValidationPatterns();
        else
            return validationPatterns;
    }
    
    /** Sets the number of the input validation patterns
     * @param newPatterns int
     */
    public void setValidationPatterns(int newPatterns) {
        if (parent != null)
            parent.setValidationPatterns(newPatterns);
        else
            validationPatterns = newPatterns;
    }
    
    public int getNumOfPatterns() {
        if (parent != null) {
            return parent.getNumOfPatterns();
        } else {
            if (isValidation() && !isTrainingDataForValidation()) {
                return validationPatterns;
            } else {
                return patterns;
            }
        }
    }
    
    public TreeSet check() {
        TreeSet checks = new TreeSet();
        
        if (getLearningRate() <= 0 && isLearning()) {
            checks.add(new NetCheck(NetCheck.FATAL, "Learning Rate must be greater than zero.", this));
        }
        if (isValidation() && getValidationPatterns() <= 0) {
            checks.add(new NetCheck(NetCheck.FATAL, "Validation Patterns not set.", this));
        }
        
        if (isLearning() && getTrainingPatterns() <= 0) {
            checks.add(new NetCheck(NetCheck.FATAL, "Training Patterns not set.", this));
        }
        
        if (!isValidation() && (getTrainingPatterns() <= 0)) {
            checks.add(new NetCheck(NetCheck.FATAL, "Training Patterns not set.", this));
        }
        
        if (getTotCicles() <= 0) {
            checks.add(new NetCheck(NetCheck.FATAL, "TotCicles (epochs) not set.", this));
        }
        
        return checks;
    }
        
    /**
     * Getter for property supervised.
     * @return Value of property supervised.
     */
    public boolean isSupervised() {
        if (parent != null)
            return parent.isSupervised();
        else
            return supervisioned;
    }
    
    /**
     * Setter for property supervised. (default = false)
     * @param supervised New value of property supervised.
     */
    public void setSupervised(boolean supervised) {
        if (parent != null)
            parent.setSupervised(supervised);
        else
            this.supervisioned = supervised;
    }
    
    /** Getter for the property BatchSize
     * @return the size (# of cycles) of the batch mode
     */
    public int getBatchSize() {
        if (parent != null)
            return getBatchSize();
        else
            return batchSize;
    }
    
    /** Setter for the property BatchSize
     * @param the size (# of cycles) of the batch mode
     */
    public void setBatchSize(int i) {
        if (parent != null)
            parent.setBatchSize(i);
        else
            batchSize = i;
    }
    
    /**
     * Getter for property learningMode.
     * The learningMode determines the kind of learning algorithm applied to the
     * neural network.
     * @return Value of property learningMode.
     * @see getLearners()
     */
    public int getLearningMode() {
        return learningMode;
    }
    
    /**
     * Setter for property learningMode.
     * @param learningMode New value of property learningMode.
     */
    public void setLearningMode(int learningMode) {
        this.learningMode = learningMode;
    }
    
    /** Getter for the Learner declared at position 'index'
     * @param the index of the Learner to get
     * @return the Learner at 'index' position
     * @see getLearners()
     */
    public Learner getLearner(int index) {
        Learner myLearner = null;
        
        if(index < getLearners().size() && index >= 0) {
            String myClassName = (String)getLearners().get(index);
            try {
                Class myClass = Class.forName(myClassName);
                myLearner = (Learner)myClass.newInstance();
            } catch (ClassNotFoundException cnfe) {
                log.error("Class " + myClassName + " not found");
            } catch (InstantiationException ie) {
                log.error("Error instantiating the class " + myClassName);
            } catch (IllegalAccessException iae) {
                log.error("Illegal access instantiating the class " + myClassName);
            }
        }
        
        if(myLearner == null) { // set default learner
            // log.warn("No learner is set, use default (basic) learner.");
            myLearner = new BasicLearner();
        }
        
        myLearner.setMonitor(this);
        return myLearner;
    }
    
    /**
     * Gets a learner for a synapse or layer.
     *
     * @return a learner for a synapse or layer.
     */
    public Learner getLearner() {
        Learner myLearner = null;
        
        if(theLearnerFactory != null) {
            myLearner = theLearnerFactory.getLearner(this);
            myLearner.setMonitor(this);
        }
        
        if(myLearner == null) {
            myLearner = getLearner(getLearningMode());
        }
        return myLearner;
    }
    
    /**
     * Getter for property learners.
     * @return Value of property learners.
     */
    protected java.util.List getLearners() {
        if (learners == null)
            learners = new ArrayList(10);
        return learners;
    }
    
    /**
     * Setter for property learners.
     * Used to set all the Learner objects used by this NN.
     * @param learners New value of property learners.
     */
    protected void setLearners(java.util.List learners) {
        this.learners = learners;
    }

    
    /**
     * Adds a new Learner to the Neural Network  
     * Usage:
     *    Monitor.addLearner(0, "org.joone.engine.BasicLearner");
     *    Monitor.addLearner(1, "org.joone.engine.BatchLearner");
     *    Monitor.addLearner(2, "org.joone.engine.RpropLearner");
     * @param i the index of the new Learner
     * @param learner a String containing the class name of the Learner to add
     */
    public void addLearner(int i, String learner) {
        if (!getLearners().contains(learner)) {
            getLearners().add(i, learner);
        }
    }

    /** Gets a custom parameter from the Monitor.
     * The user is free to use the custom parameters as s/he wants.
     * They are useful to store a whatever value that could be used by the nnet's components.
     * It has been introduced to set the parameters of new added Learners.
     * @param key The searched key
     * @return The value of the parameter if found, otherwise null
     */
    public Object getParam(String key) {
        if (params == null)
            return null;
        return params.get(key);
    }
    
    /** Sets a custom parameter of the Monitor.
     * The user is free to use the custom parameters as s/he wants.
     * They are useful to store a whatever value that could be used by the nnet's components.
     * It has been introduced to set the parameters of new added Learners.
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
    
    /** Return all the keys of the parameters contained in this Monitor.
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
     * Uses the RMSE when set to true. Uses MSE when set to false.
     *
     * @param aMode the mode to set true for RMSE, false for MSE.
     */
    public void setUseRMSE(boolean aMode) {
        useRMSE = aMode;
    }
    
    /**
     * Checks if we should use RMSE (true) or MSE false.
     *
     * @return true if we should use RMSE, false if we should use MSE.
     */
    public boolean isUseRMSE() {
        return useRMSE;
    }
    
    
    /**
     * Set learner factory.
     *
     * @param aFactory the learner factory to set.
     */
    public void setLearnerFactory(LearnerFactory aFactory) {
        theLearnerFactory = aFactory;
    }
    
    public boolean isSingleThreadMode() {
        if (parent != null)
            return parent.isSingleThreadMode();
        else
            return singleThreadMode;
    }
    
    public void setSingleThreadMode(boolean singleThreadMode) {
        if (parent != null)
            parent.setSingleThreadMode(singleThreadMode);
        else
            this.singleThreadMode = singleThreadMode;
    }
    
}