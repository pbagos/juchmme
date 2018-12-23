/*
 * LearnerExtender.java
 *
 * Created on September 14, 2004, 9:32 AM
 */

package org.joone.engine.extenders;

import org.joone.engine.*;

/**
 * This abstract class describes the methods that any learner extender must
 * provide.
 *
 * @author Boris Jansen
 */
public abstract class LearnerExtender {
    
    /** This flag holds the mode of the learner extender (true for enabled, 
     false for disabled. */
    private boolean theMode = true;
    
    /** The learner this object is extending. */
    private ExtendableLearner theLearner;
    
    /**
     * Sets the learner. This way the extender has a reference to the learner.
     *
     * @param aLearner the learner this object is extending.
     */
    public void setLearner(ExtendableLearner aLearner) {
        theLearner = aLearner;
    }
    
    /**
     * Gets the learner this object is extending.
     *
     * @return the learner this object is extending.
     */
    protected ExtendableLearner getLearner() {
        return theLearner;
    }
    
    /**
     * Checks if the learner extender is enabled.
     *
     * @return true if the extender is enabled, false otherwise.
     */
    public boolean isEnabled() {
        return theMode;
    }
    
    /**
     * Sets the mode of this extender.
     *
     * @param aMode true for enabled, false for disabled.
     */
    public void setEnabled(boolean aMode) {
        theMode = aMode;
    }
    
    /**
     * Gives extenders a change to do some pre-computing before the 
     * biases are updated.
     *
     * @param currentGradientOuts the back propagated gradients.
     */
    public abstract void preBiasUpdate(double[] currentGradientOuts);
    
    /**
     * Gives extenders a change to do some post-computing after the 
     * biases are updated.
     *
     * @param currentGradientOuts the back propagated gradients.
     */
    public abstract void postBiasUpdate(double[] currentGradientOuts);
    
    /**
     * Gives extenders a change to do some pre-computing before the 
     * weights are updated.
     *
     * @param currentPattern the back propagated gradients.
     * @param currentInps the forwarded input.
     */
    public abstract void preWeightUpdate(double[] currentPattern, double[] currentInps);
    
    /**
     * Gives extenders a change to do some post-computing after the 
     * weights are updated.
     *
     * @param currentPattern the back propagated gradients.
     * @param currentInps the forwarded input.
     */
    public abstract void postWeightUpdate(double[] currentPattern, double[] currentInps);
}
