/*
 * RpropExtender.java
 *
 * Created on September 14, 2004, 3:29 PM
 */

package org.joone.engine.extenders;

import org.joone.engine.RpropParameters;
import org.joone.log.*;

/**
 * This class changes the delta value in such a way that it implements the
 * RPROP algorithm.
 *
 * @author Boris Jansen
 */
public class RpropExtender extends DeltaRuleExtender {
    
    // Note the gradient passed by the ExtendableLearner is multiplied by
    // the learning rate. However, the RPROP learning algorithm looks only at
    // the sign of the gradient. So as long the learning algorithm is positive
    // there is no problem.
    
    /** Logger */
    private static final ILogger log = LoggerFactory.getLogger(RpropExtender.class);
    
    /**
     * Each weight has its own individual update-value (delta_ij(t)) represented
     * by the next object.
     *
     * The weight update deltaW_ij(t) is defined as follows (dE(t) / dW_ij is the
     * summed gradient for a single epoch):
     *                | -delta_ij(t),    if dE(t) / dW_ij > 0
     * deltaW_ij(t) = | delta_ij(t),     if dE(t) / dW_ij < 0
     *                | 0                otherwise
     *
     * The delta_ij values are updated as follows:
     *               | eta_inc * delta_ij(t-1),     if dE(t-1)/ dW_ij * dE(t)/ dW_ij > 0
     * delta_ij(t) = | eta_dec * delta_ij(t-1),     if dE(t-1)/ dW_ij * dE(t)/ dW_ij < 0
     *               | delta_ij(t-1),               otherwise
     * where 0 < eta_dec < 1 < eta_inc
     */
    protected double[][] theDeltas;
    
    /** The gradient pattern of the previous epoch (dE(t-1)/dW_ij). */
    protected double[][] thePreviousGradients;
    
    /** The parameters for the RPROP learning algorithm. */
    protected RpropParameters theRpropParameters;
    
    /** The current som of the gradients of all patterns seen so far. The number
     * of summed gradients is smaller the "batch size" and will be reset to zero,
     * if the number of sums becomes equal to "batch size" after the weights/biases
     * have been updated. */
    protected double[][] theSummedGradients;
    
    /** Creates a new instance of RpropExtender */
    public RpropExtender() {
    }
    
    /**
     * (Re)Initializes this RPROP learner.
     */
    public void reinit() {
        if(getLearner().getMonitor().getLearningRate() != 1) {
            log.warn("RPROP learning rate should be equal to 1.");
        }
        
        if(getLearner().getLayer() != null) {
            thePreviousGradients = new double[getLearner().getLayer().getRows()][1];
            theSummedGradients = new double[thePreviousGradients.length][1];
            theDeltas = new double[thePreviousGradients.length][1];
        } else if (getLearner().getSynapse() != null) {
            int myRows = getLearner().getSynapse().getInputDimension();
            int myCols = getLearner().getSynapse().getOutputDimension();
            thePreviousGradients = new double[myRows][myCols];
            theSummedGradients = new double[myRows][myCols];
            theDeltas = new double[myRows][myCols];
        }
        
        for(int i = 0; i < theDeltas.length; i++) {
            for(int j = 0; j < theDeltas[0].length; j++) {
                theDeltas[i][j] = getParameters().getInitialDelta(i, j);
            }
        }
    }
    
    public double getDelta(double[] currentGradientOuts, int j, double aPreviousDelta) {
        // we will hold our delta's in memory ourselves only when the weights will be 
        // stored we will pass on the calculated delta value !! Therefore, some 
        // DeltaExtenders executed after this delta might not work correct...
        // Please think about the order of the delta extenders... !!
        double myDelta = 0;
        
        // Note:
        // dE/dw = sum(dE/de * de/dy * ...) *...
        // de/dy = -1, however * -1 is neglected, so aCurrentGradientOuts has a different sign than dE/dw
        // we fix this here by multiplying aCurrentGradientOuts by -1.0
        // remove -> theSummedGradients[i][0] += -1.0 * aCurrentGradientOuts[i];
        // theSummedGradients[i][0] += -1.0 * getGradientBias(aCurrentGradientOuts, i);
        theSummedGradients[j][0] -= aPreviousDelta;
            
        if(getLearner().getUpdateWeightExtender().storeWeightsBiases()) {
            // biases will be stored this cycle
            if(thePreviousGradients[j][0] * theSummedGradients[j][0] > 0) {
                theDeltas[j][0] = Math.min(theDeltas[j][0] * getParameters().getEtaInc(),
                    getParameters().getMaxDelta());
                myDelta = -1.0 * sign(theSummedGradients[j][0]) * theDeltas[j][0];
                thePreviousGradients[j][0] = theSummedGradients[j][0];
            } else if(thePreviousGradients[j][0] * theSummedGradients[j][0] < 0) {
                theDeltas[j][0] = Math.max(theDeltas[j][0] * getParameters().getEtaDec(),
                    getParameters().getMinDelta());
                // sign changed -> the previous step was to large and the minimum was missed,
                // the previous weight-update is reverted
                myDelta = -1.0 * getLearner().getLayer().getBias().delta[j][0];
                // due the backtracking step the derivative is supposed to change its sign
                // again in the following step. To prevent double punishement we set the
                // gradient to 0
                thePreviousGradients[j][0] = 0;
            } else {
                myDelta = -1.0 * sign(theSummedGradients[j][0]) * theDeltas[j][0];
                thePreviousGradients[j][0] = theSummedGradients[j][0];
            }
            theSummedGradients[j][0] = 0; // reset to zero so we can start somming up again...
        }
        return myDelta;
    }
    
    public double getDelta(double[] currentInps, int j, double[] currentPattern, int k, double aPreviousDelta) {
        // read comments getDelta (for bias)...
        double myDelta = 0;
        
        // * -1.0, because de/dy = -1, but is neglected in  aCurrentPattern
        // remove -> theSummedGradients[i][j] += aCurrentPattern[j] * aCurrentInps[i] * - 1.0;
        // theSummedGradients[i][j] += -1.0 * getGradientWeights(aCurrentInps, i, aCurrentPattern, j);
        theSummedGradients[j][k] -= aPreviousDelta;
        
        if(getLearner().getUpdateWeightExtender().storeWeightsBiases()) {
            // weights will be stored this cycle
            if(thePreviousGradients[j][k] * theSummedGradients[j][k] > 0) {
                theDeltas[j][k] = Math.min(theDeltas[j][k] * getParameters().getEtaInc(),
                    getParameters().getMaxDelta());
                myDelta = -1.0 * sign(theSummedGradients[j][k]) * theDeltas[j][k];
                thePreviousGradients[j][k] = theSummedGradients[j][k];
            } else if(thePreviousGradients[j][k] * theSummedGradients[j][k] < 0) {
                theDeltas[j][k] = Math.max(theDeltas[j][k] * getParameters().getEtaDec(),
                    getParameters().getMinDelta());
                // sign changed -> the previous step was to large and the minimum was missed,
                // the previous weight-update is reverted
                myDelta = -1.0 * getLearner().getSynapse().getWeights().delta[j][k];
                // due the backtracking step the derivative is supposed to change its sign
                // again in the following step. To prevent double punishement we set the
                // gradient to 0
                thePreviousGradients[j][k] = 0;
            } else {
                myDelta = -1.0 * sign(theSummedGradients[j][k]) * theDeltas[j][k];
                thePreviousGradients[j][k] = theSummedGradients[j][k];
            }
            theSummedGradients[j][k] = 0;
        }
        return myDelta;
    }
    
    public void postBiasUpdate(double[] currentGradientOuts) {
    }
    
    public void postWeightUpdate(double[] currentPattern, double[] currentInps) {
    }
    
    public void preBiasUpdate(double[] currentGradientOuts) {
        if(theDeltas == null || theDeltas.length != getLearner().getLayer().getRows()) {
            // first time or dimensions have changed
            reinit();
        }
    }
    
    public void preWeightUpdate(double[] currentPattern, double[] currentInps) {
        if(theDeltas == null || theDeltas.length != getLearner().getSynapse().getInputDimension() 
            || theDeltas[0].length != getLearner().getSynapse().getOutputDimension()) 
        {
            reinit();
        }
    }
    
    /**
     * Gets the parameters of this learning algorithm.
     *
     * @return the parameters of this learning algorithm.
     */
    public RpropParameters getParameters() {
        if(theRpropParameters == null) {
            // create default parameters
            theRpropParameters = new RpropParameters();
        }
        return theRpropParameters;
    }
    
    /**
     * Sets the parameters for this learning algorithm.
     *
     * @param aParameters the parameters for this learning algorithm.
     */
    public void setParameters(RpropParameters aParameters) {
        theRpropParameters = aParameters;
    }
    
    /**
     * Gets the sign of a double.
     *
     * return the sign of a double (-1, 0, 1).
     */
    protected double sign(double d) {
        if(d > 0) {
            return 1.0;
        } else if(d < 0) {
            return -1.0;
        }
        return 0;
    }
}
