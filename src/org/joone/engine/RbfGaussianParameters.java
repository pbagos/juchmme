/*
 * RbfGaussianParameters.java
 *
 * Created on July 23, 2004, 1:46 PM
 */

package org.joone.engine;

import java.io.Serializable;

/**
 * This class defines the parameters, like center, sigma, etc. for the Gaussian RBF.
 *
 * @author Boris Jansen
 */
public class RbfGaussianParameters implements Serializable {
    
    /** The mean (center) of the RBF. */
    private double[] theMean;
    
    /** The standard deviation (sigma). */
    private double theStdDeviation;
    
    /** Creates a new instance of RbfGaussianParameters */
    public RbfGaussianParameters() {
    }
    
    /** 
     * Creates a new instance of RbfGaussianParameters.
     *
     * @param aMean the mean.
     * @param aStdDeviation the standard deviation.
     */
    public RbfGaussianParameters(double[] aMean, double aStdDeviation) {
        theMean = aMean;
        theStdDeviation = aStdDeviation;
    }
    
    /**
     * Gets the mean (center) of the Gaussian RBF.
     *
     * @return the mean of the Gaussian RBF.
     */
    public double[] getMean() {
        return theMean;
    }
    
    /**
     * Sets the mean (center) of the Gaussian RBF.
     *
     * @param aMean the new mean to set.
     */
    public void setMean(double[] aMean) {
        theMean = aMean;
    }
    
    /**
     * Gets the standard deviation (sigma) of the Gaussian RBF.
     *
     * @return the standard deviation of the Gaussian RBF.
     */
    public double getStdDeviation() {
        return theStdDeviation;
    }
    
    /**
     * Sets the standard deviation (sigma) of the Gaussian RBF.
     *
     * @param aStdDeviation the new standard deviation to set.
     */
    public void setStdDeviation(double aStdDeviation) {
        theStdDeviation = aStdDeviation;
    }
    
}
