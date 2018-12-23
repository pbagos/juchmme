/*
 * LearningSwitch.java
 *
 * Created on 30 aprile 2002, 16.10
 */

package org.joone.util;

import org.joone.engine.*;
import org.joone.io.*;
import org.joone.net.NetCheck;
import java.util.TreeSet;

/**
 * This class is useful to switch the input data set of a neural network
 * from a training set to a validation set depending on the 'validation'
 * parameter contained in the Monitor object.
 * Very useful during a training phase to test the generalization capacity
 * of the neural network with a validation data set never seen before.
 *
 * @author  pmarrone
 */
public class LearningSwitch extends InputSwitchSynapse {
    
    private StreamInputSynapse trainingSet;
    private StreamInputSynapse validationSet;
    private boolean validation = false;
    
    private static final long serialVersionUID = -2339515807277374407L;
    
    /******* Not more used.
     * They are here only to maintain the compatibility with the old
     * serialized classes.
     * Use instead the patterns/validationPatterns parameters of the
     * Monitor object.
     ********/
    private int validationPatterns; // Not more used
    private int trainingPatterns;   // Not more used
    
    /** Creates a new instance of LearningSwitch */
    public LearningSwitch() {
        super();
    }
    
    public synchronized boolean addTrainingSet(StreamInputSynapse tSet) {
        if (trainingSet != null)
            return false;
//            super.removeInputSynapse(trainingSet.getName());
        if (super.addInputSynapse(tSet)) {
            trainingSet = tSet;
            // The training set is the default input data set
            super.setDefaultSynapse(trainingSet);
            super.reset();
            validation = false;
            return true;
        } else
            return false;
    }
    
    public synchronized boolean addValidationSet(StreamInputSynapse vSet) {
        if (validationSet != null)
            return false;
//            super.removeInputSynapse(validationSet.getName());
        if (super.addInputSynapse(vSet)) {
            validationSet = vSet;
            return true;
        } else
            return false;
    }
    
    public synchronized void removeTrainingSet() {
        if (trainingSet != null) {
            super.removeInputSynapse(trainingSet.getName());
            trainingSet = null;
        }
    }
    
    public synchronized void removeValidationSet() {
        if (validationSet != null) {
            super.removeInputSynapse(validationSet.getName());
            validationSet = null;
        }
    }
    
    /** Connects the right input synapse depending on the
     * Monitor's 'validation' parameter
     * @return neural.engine.Pattern
     */
    public Pattern fwdGet() {
        if (getMonitor().isValidation() && !getMonitor().isTrainingDataForValidation())
            super.setActiveSynapse(validationSet);
        else
            super.setActiveSynapse(trainingSet);
        return super.fwdGet();
    }
    
    /** Connects the right input synapse depending on the
     * Monitor's 'validation' parameter. Added to be
     * compatible with the InputConnector class.
     * @return neural.engine.Pattern
     */
    public Pattern fwdGet(InputConnector conn) {
        /** Connects the right input synapse depending on the
         * Monitor's 'validation' parameter
         * @return neural.engine.Pattern
         */
        if (getMonitor().isValidation() && !getMonitor().isTrainingDataForValidation())
            super.setActiveSynapse(validationSet);
        else
            super.setActiveSynapse(trainingSet);
        return super.fwdGet(conn);
    }
    
    public java.util.TreeSet check() {
        TreeSet checks = super.check();
        // Check that the first row is greater than 0.
        if (trainingSet == null) {
            checks.add(new NetCheck(NetCheck.FATAL, "Training set parameter not set", this));
        }
        if (validationSet == null) {
            checks.add(new NetCheck(NetCheck.FATAL, "Validation set parameter not set", this));
        }
        return checks;
    }
    
    /**
     * Getter for property trainingSet.
     * Added for XML serialization
     * @return Value of property trainingSet.
     */
    public org.joone.io.StreamInputSynapse getTrainingSet() {
        return trainingSet;
    }
    
    /**
     * Setter for property trainingSet.
     * Added for XML serialization
     * @param trainingSet New value of property trainingSet.
     */
    public void setTrainingSet(org.joone.io.StreamInputSynapse trainingSet) {
        this.trainingSet = trainingSet;
    }
    
    /**
     * Getter for property validationSet.
     * Added for XML serialization
     * @return Value of property validationSet.
     */
    public org.joone.io.StreamInputSynapse getValidationSet() {
        return validationSet;
    }
    
    /**
     * Setter for property validationSet.
     * Added for XML serialization
     * @param validationSet New value of property validationSet.
     */
    public void setValidationSet(org.joone.io.StreamInputSynapse validationSet) {
        this.validationSet = validationSet;
    }
    
}
