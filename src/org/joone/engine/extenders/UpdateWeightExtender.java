/*
 * UpdateWeightExtender.java
 *
 * Created on September 14, 2004, 10:10 AM
 */

package org.joone.engine.extenders;

import java.util.*;

/**
 * This abstract class describes the methods needed for a update weight extender, that is,
 * a class that updates weights (storing) according to some algorithm (e.g. batch mode).
 *
 * @author Boris Jansen
 */
public abstract class UpdateWeightExtender extends LearnerExtender {
    
    /** Creates a new instance of UpdateWeightExtender */
    public UpdateWeightExtender() {
    }
    
    /**
     * Updates a bias with the calculated delta value.
     *
     * @param i the index of the bias to update.
     * @param aDelta the calculated delta value.
     */
    public abstract void updateBias(int i, double aDelta);
    
    /**
     * Updates a weight with the calculated delta value.
     *
     * @param j the input index of the weight to update.
     * @param k the output index of the weight to update.
     * @param aDelta the calculated delta value.
     */
    public abstract void updateWeight(int j, int k, double aDelta);
    
    /**
     * Checks if the weights or biases will be stored this cycle.
     *
     * @return true if the weights or biases will be stored this cycle, false
     * otherwise.
     */
    public abstract boolean storeWeightsBiases();
}
