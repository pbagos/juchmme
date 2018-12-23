package org.joone.engine;

/**
 * This object holds the global parameters for the RPROP learning 
 * algorithm (RpropLearner).
 *
 * @author Boris Jansen
 */
public class RpropParameters {
    
    /** The initial delta value. */
    private double theInitialDelta = 0.1; // default 0.1
    
    /** The maximum delta value that is allowed. */
    private double theMaxDelta = 50.0; // default 50.0

    /** The minimum delta value that is allowed. */
    private double theMinDelta = 1e-6; // default
    
    /** The incremental learning factor/rate. */
    private double theEtaInc = 1.2; // default

    /** The decremental learning factor/rate. */
    private double theEtaDec = 0.5; // default
    
    /** The batch size. */
    private int theBatchSize = 1;
    
    /** Creates a new instance of RpropParameters */
    public RpropParameters() {
    }
    
    /**
     * Gets the initial delta value.
     *
     * @param i the index (i, j) of the weight/bias for which it should get the
     *        initial value. The RPROP learning algorithm gives every bias/weight
     *        the same initial value, but by passing the index of the weight/bias
     *        to this method, a user is able to give different initial values to 
     *        different weights/biases based on their index by extending this 
     *        class.
     * @param j 
     */
    public double getInitialDelta(int i, int j) {
        return theInitialDelta;
    }
    
    /**
     * Sets the initial delta for all delta's.
     *
     * @param anInitialDelta the initial delta value.
     */
    public void setInitialDelta(double anInitialDelta) {
        theInitialDelta = anInitialDelta;
    }
    
    /**
     * Gets the maximum allowed delta value.
     *
     * @return the maximum allowed delta value.
     */
    public double getMaxDelta() {
        return theMaxDelta;
    }

    /**
     * Sets the maximum allowed delta value.
     *
     * @param aMaxDelta the maximum allowed delta value.
     */
    public void setMaxDelta(double aMaxDelta) {
        theMaxDelta = aMaxDelta;
    }
    
    /**
     * Gets the minimum allowed delta value.
     *
     * @return the minimum allowed delta value.
     */
    public double getMinDelta() {
        return theMinDelta;
    }
    
    /**
     * Sets the minimum allowed delta value.
     *
     * @param aMinDelta the minimum allowed delta value.
     */
    public void setMinDelta(double aMinDelta) {
        theMinDelta = aMinDelta;
    }
    
    /**
     * Gets the incremental learning factor/rate.
     *
     * @return the incremental learning factor/rate.
     */
    public double getEtaInc() {
        return theEtaInc;
    }
    
    /**
     * Sets the incremental learning factor/rate.
     *
     * @param anEtaInc the incremental learning factor/rate.
     */
    public void setEtaInc(double anEtaInc) {
        theEtaInc = anEtaInc;
    }
    
    /**
     * Gets the decremental learning factor/rate.
     *
     * @return the decremental learning factor/rate.
     */
    public double getEtaDec() {
        return theEtaDec;
    }
    
    /**
     * Sets the decremental learning factor/rate.
     *
     * @param anEtaDec the decremental learning factor/rate.
     */
    public void setEtaDec(double anEtaDec) {
        theEtaDec = anEtaDec;
    }
    /**
     * Gets the batchsize.
     *
     * @return the batch size.
     */
    public int getBatchSize() {
        return theBatchSize;
    }
    
    /**
     * Sets the batchsize.
     *
     * param aBatchsize the new batchsize.
     */
    public void setBatchSize(int aBatchsize) {
        theBatchSize = aBatchsize;
    }
}
