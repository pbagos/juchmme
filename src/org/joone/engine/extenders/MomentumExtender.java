/*
 * MomentumExtender.java
 *
 * Created on September 14, 2004, 11:18 AM
 */

package org.joone.engine.extenders;

// import org.joone.log.*;

/**
 * This extender implements the momentum term.
 *
 * @author Boris Jansen
 */
public class MomentumExtender extends DeltaRuleExtender {
    
    /** Logger */
    //private static final ILogger log = LoggerFactory.getLogger(MomentumExtender.class);
    
    /** Creates a new instance of MomentumExtender */
    public MomentumExtender() {
    }
    
    public double getDelta(double[] currentGradientOuts, int j, double aPreviousDelta) {
        // log.debug("Add momentum for bias.");
        
        if(getLearner().getUpdateWeightExtender().storeWeightsBiases()) {
            // the biases will be stored this cycle, add momentum
            aPreviousDelta += 
                getLearner().getMonitor().getMomentum() * getLearner().getLayer().getBias().delta[j][0];
        }
        
        return aPreviousDelta;
    }
    
    public double getDelta(double[] currentInps, int j, double[] currentPattern, int k, double aPreviousDelta) {
        // log.debug("Add momentum for weight.");
        
        if(getLearner().getUpdateWeightExtender().storeWeightsBiases()) {
            // the weights will be stored this cycle, add momentum
            aPreviousDelta += 
                getLearner().getMonitor().getMomentum() * getLearner().getSynapse().getWeights().delta[j][k];
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
    
}
