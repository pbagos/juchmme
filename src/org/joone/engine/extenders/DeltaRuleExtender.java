/*
 * DeltaRuleExtender.java
 *
 * Created on September 14, 2004, 9:32 AM
 */

package org.joone.engine.extenders;

/**
 * This abstract class describes the methods needed for a delta rule extender, 
 * that is, a class that computes / changes the delta (update weight) value 
 * according to some algorithm.
 *
 * @author Boris Jansen
 */
public abstract class DeltaRuleExtender extends LearnerExtender {
    
    /** Creates a new instance of DeltaExtender */
    public DeltaRuleExtender() {
    }
    
    /**
     * Computes the delta value for a bias.
     *
     * @param currentGradientOuts the back propagated gradients.
     * @param j the index of the bias.
     * @param aPreviousDelta a delta value calculated by a previous delta extender.
     */
    public abstract double getDelta(double[] currentGradientOuts, int j, double aPreviousDelta);
     
    /**
     * Computes the delta value for a weight.
     *
     * @param currentInps the forwarded input.
     * @param j the input index of the weight.
     * @param currentPattern the back propagated gradients.
     * @param k the output index of the weight.
     * @param aPreviousDelta a delta value calculated by a previous delta extender.
     */
    public abstract double getDelta(double[] currentInps, int j, double[] currentPattern, int k, double aPreviousDelta);
}
