/*
 * WeightDecayExtender.java
 *
 * Created on March 7, 2006, 4:43 PM
 */

package org.joone.engine.extenders;

/**
 * Weight decay adds a penalty term to the error function. The penalty term 
 * penalizes large weights. The weight decay penalty term causes the weights to 
 * converge to smaller absolute values than they otherwise would. Smaller weights
 * are expected to improve generalization.
 *
 * The update formula is changed in:
 * Dw(t+1) = dw(t+1) - d x w(t)
 * 
 * d is a weight decay value.
 *
 *
 * @author boris
 */
public class WeightDecayExtender extends DeltaRuleExtender {
    
    /** The decay parameter (d). */
    private double decay;
    
    /** Creates a new instance of WeightDecayExtender */
    public WeightDecayExtender() {
    }
 
    public double getDelta(double[] currentGradientOuts, int j, double aPreviousDelta) {
        if(getLearner().getUpdateWeightExtender().storeWeightsBiases()) {
            aPreviousDelta -= getDecay() *  getLearner().getLayer().getBias().value[j][0];
        }
        
        return aPreviousDelta;
    }
    
    public double getDelta(double[] currentInps, int j, double[] currentPattern, int k, double aPreviousDelta) {
        if(getLearner().getUpdateWeightExtender().storeWeightsBiases()) {
            aPreviousDelta -= getDecay() *  getLearner().getSynapse().getWeights().value[j][k];
        }
        return aPreviousDelta;
    }
    
    public void postBiasUpdate(double[] currentGradientOuts) {
    }
    
    public void postWeightUpdate(double[] currentPattern, double[] currentInps) {
    }
    
    public void preBiasUpdate(double[] currentGradientOuts) {
    }
    
    public void preWeightUpdate(double[] currentPattern, double[] currentInps) {
    }
    
    /**
     * Sets the decay parameter.
     *
     * @param aDecay the decay parameter value.
     */
    public void setDecay(double aDecay) {
        decay = aDecay;
    }
    
    /**
     * Gets the decay parameter.
     *
     * @return the decay parameter.
     */
    public double getDecay() {
        return decay;
    }
    
}
