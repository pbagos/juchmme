/*
 * BatchModeExtender.java
 *
 * Created on September 14, 2004, 11:39 AM
 */

package org.joone.engine.extenders;

import org.joone.engine.*;

/**
 * This class implements the offline learning, that is, batch mode. Weights are 
 * updated after Monitor.getBatchSize() cycles.
 *
 * @author Boris Jansen
 */
public class BatchModeExtender extends UpdateWeightExtender {
    
    /** The batch size. This variable is mainly used for backward compatibility
     * with the old batch learner who had the method setBatchSize. */
    private int theBatchSize = -1; // -1 equals not set and retrieve batch size
                                   // value from monitor
    
    /** The number of rows (biases or input neurons to the synapses). */
    private int theRows = -1;
    
    /** The number of columns (output neurons of the synapses), */
    private int theColumns = -1;
    
    /** The matrix in which we save the updates before storing the weights (or biases)
     * to the network itself. */
    private Matrix theMatrix;
    
    /** The counter to check if we have reached batchsize cycles (if so, we need 
     to store the weights).*/
    private int theCounter = 0;
    
    /** Creates a new instance of BatchModeExtender */
    public BatchModeExtender() {
    }
    
    public void postBiasUpdate(double[] currentGradientOuts) {
        if(storeWeightsBiases()) {
            for(int x = 0; x < theRows; ++x) {
                theMatrix.value[x][0] += theMatrix.delta[x][0]; // adjust bias
            }
            getLearner().getLayer().setBias((Matrix)theMatrix.clone()); // store updated biases
            resetDelta(theMatrix);
            theCounter = 0;
        }
    }
    
    public void postWeightUpdate(double[] currentPattern, double[] currentInps) {
        if(storeWeightsBiases()) {
            for(int x = 0; x < theRows; ++x) {
                for(int y = 0; y < theColumns; ++y) {
                    theMatrix.value[x][y] += theMatrix.delta[x][y]; // adjust weights
                }
            } 
            getLearner().getSynapse().setWeights((Matrix)theMatrix.clone()); // store updated weights
            resetDelta(theMatrix);
            theCounter = 0;
        }
    }
    
    public void preBiasUpdate(double[] currentGradientOuts) {
        if(theRows != getLearner().getLayer().getRows()) { 
            // dimensions have changed, so better start over
            initiateNewBatch();
        }
        theCounter++;
    }
    
    public void preWeightUpdate(double[] currentPattern, double[] currentInps) {
        if(theRows != getLearner().getSynapse().getInputDimension() || 
            theColumns != getLearner().getSynapse().getOutputDimension())
        {
            initiateNewBatch();
        }
        theCounter++;
    }
    
    public void updateBias(int i, double aDelta) {
        theMatrix.delta[i][0] += aDelta; // update the delta in our local copy
    }
    
    public void updateWeight(int j, int k, double aDelta) {
        theMatrix.delta[j][k] += aDelta; // update the delta in our local copy
//        System.out.println("batch updateWeight "+theCounter+" "+aDelta);
    }
    
    /**
     * Resets delta values to zero.
     *
     * @param aMatrix the matrix for which we need to set the delta values to zero.
     */
    protected void resetDelta(Matrix aMatrix) {
        // reset the delta values to 0
        for(int r = 0; r < aMatrix.delta.length; r++) {
            for(int c = 0; c < aMatrix.delta[0].length; c++) {
                aMatrix.delta[r][c] = 0;
            }
        }
    }
    
    /**
     * Initiates a new batch (at the beginning or when the dimensions change).
     */
    protected void initiateNewBatch() {
        if (getLearner().getLayer() != null) {
            theRows = getLearner().getLayer().getRows();
            theMatrix = (Matrix)getLearner().getLayer().getBias().clone(); // get a copy
        } else if (getLearner().getSynapse() != null) {
            theRows = getLearner().getSynapse().getInputDimension();
            theColumns = getLearner().getSynapse().getOutputDimension();
            theMatrix = (Matrix)getLearner().getSynapse().getWeights().clone(); // get a copy
        }
        resetDelta(theMatrix);
        theCounter = 0;
    }
    
    /**
     * Sets the batchsize. Used for backward compatibility. Use monitor.setBatchSize()
     * instead.
     *
     * @param aBatchSize the new batchsize.
     * @deprecated use monitor.setBatchSize()
     */
    public void setBatchSize(int aBatchSize) {
        theBatchSize = aBatchSize;
    }
    
    /**
     * Gets the batchsize. Used for backward compatibility. Use monitor.getBatchSize()
     * instead.
     *
     * @return the batch size.
     * @deprecated use monitor.getBatchSize()
     */
    public int getBatchSize() {
        if(theBatchSize < 0) {
            return getLearner().getMonitor().getBatchSize();
        }
        return theBatchSize;
    }
    
    public boolean storeWeightsBiases() {
        return theCounter >= getBatchSize();
    }
    
}
