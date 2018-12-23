/*
 * OnlineExtender.java
 *
 * Created on September 14, 2004, 1:53 PM
 */

package org.joone.engine.extenders;

/**
 * This is the default weight updater (online). It stores the weights after each
 * update.
 *
 * @author Boris Jansen
 */
public class OnlineModeExtender extends UpdateWeightExtender {
    
    /** Creates a new instance of OnlineExtender */
    public OnlineModeExtender() {
    }
    
    public void postBiasUpdate(double[] currentGradientOuts) {
    }
    
    public void postWeightUpdate(double[] currentPattern, double[] currentInps) {
    }
    
    public void preBiasUpdate(double[] currentGradientOuts) {
    }
    
    public void preWeightUpdate(double[] currentPattern, double[] currentInps) {
    }
    
    public void updateBias(int j, double aDelta) {
        getLearner().getLayer().getBias().delta[j][0] = aDelta;
        getLearner().getLayer().getBias().value[j][0] += aDelta;
    }
    
    public void updateWeight(int j, int k, double aDelta) {
        getLearner().getSynapse().getWeights().delta[j][k] = aDelta;
        getLearner().getSynapse().getWeights().value[j][k] += aDelta;

    }
    
    public boolean storeWeightsBiases() {
        return true; // we will always store the weights / biases in the online mode
    }
    
}
