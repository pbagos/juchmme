/*
 * FanInBasedWeightInitializer.java
 *
 * Created on December 6, 2004, 12:17 PM
 */

package org.joone.engine.weights;

import org.joone.engine.Matrix;

/**
 * The weights are uniformly distributed (that is randomly) within the range <code>[LB/F_i, UB/F_i]</code>. 
 * <code>LB</code> and <code>UB</code> stand for <i>lower bound</i> and <i>upper bound</i>,
 * which is a certain number. Here the bounds will be by default -2.4 and 2.4 as described
 * in <i>Neural Networks - A Comprehensive Foundation, Haykin</i>, chapter 6.7 <i>Some Hints 
 * for Making the Back-Propagation Algorithm Perform Better</i>. <code>F_i</code> is the fan-in, 
 * i.e. the total number of inputs) of neuron i. There is also an option to use instead of 
 * <code>F_i</code> the square root of <code>F_i</code>, which is also used in some cases.
 *
 * @author Boris Jansen
 */
public class FanInBasedWeightInitializer implements WeightInitializer {
    
    /** The lower bound. */
    private double lowerBound = -2.4; // default
    
    /** The upper bound. */
    private double upperBound = 2.4; // default
    
    /** Flag indicating if we should use the square root of the fan-in (<code>true</code>), or should 
     * be use the normal fan-in (<code>false</code>) to determine the interval to init the weights with. */
    private boolean sqrtFanIn = false; // default
    
    /** 
     * Creates a new instance of FanInBasedWeightInitializer. It uses it default values +/- 2.4
     * for the bounds and the normal fan-in.
     *
     */
    public FanInBasedWeightInitializer() {
    }
    
    /** 
     * Creates a new instance of FanInBasedWeightInitializer.
     *
     * @param aBoundary the boundary to use to init the weights 
     * (<code>[-aBoundary/F_i, aBoundary/F_i]</code>, where <code>F_i</code> is
     * the fan-in of neuron i.
     */
    public FanInBasedWeightInitializer(double aBoundary) {
        lowerBound = -aBoundary;
        upperBound = aBoundary;
    }
    
    /** 
     * Creates a new instance of FanInBasedWeightInitializer
     *
     * @param aLowerBound the lower boundary to use divided by the fan-in of a neuron.
     * @param anUpperBound the upper boundary to use divided by the fan-in of a neuron.
     */
    public FanInBasedWeightInitializer(double aLowerBound, double anUpperBound) {
        lowerBound = aLowerBound;
        upperBound = anUpperBound;
    }

    public void initialize(Matrix aMatrix) {
        // fan-in equals the rows of a matrix
        for(int x = 0; x < aMatrix.getM_rows(); x++) {
            for(int y = 0; y < aMatrix.getM_cols(); y++) {
                if(aMatrix.enabled[x][y] && !aMatrix.fixed[x][y]) {
                    aMatrix.value[x][y] = 
                        (lowerBound / (isSqrtFanIn() ? Math.sqrt((double)aMatrix.getM_rows()) : (double)aMatrix.getM_rows())) +
                        Math.random() * 
                            ((upperBound - lowerBound) / (isSqrtFanIn() ? Math.sqrt((double)aMatrix.getM_rows()) : (double)aMatrix.getM_rows()));
                }
            }
        }
    }
    
    /**
     * Sets the flag indicating the mode of the fan-in to use. If set to <code>true</code>
     * the square root of the fan-in will be used, otherwise the normal fan-in will be used
     * (default mode).
     *
     * @param aMode the mode to use, <code>true</code> for the square root of the fan-in,
     * <code>false</code> for the normal fan-in.
     */
    public void setSqrtFanIn(boolean aMode) {
        sqrtFanIn = aMode;
    }
    
    /**
     * Checks if the mode of the fan-in is the square root mode, i.e. the square root
     * of the fan-in is used or if the normal mode, i.e. the normal fan-in is used.
     *
     * @return true if the square root of the fan-in is used, false otherwise.
     */
    public boolean isSqrtFanIn() {
        return sqrtFanIn;
    }
    
    /**
     * Gets the lower bound.
     *
     * @return the lower bound.
     */
    public double getLowerBound() {
        return lowerBound;
    }
    
    /**
     * Sets the lower bound.
     *
     * @param aLowerBound the new lower bound.
     */
    public void setLowerBound(double aLowerBound) {
        lowerBound = aLowerBound;
    }
    
    
    /**
     * Gets the upper bound.
     *
     * @return the upper bound.
     */
    public double getUpperBound() {
        return upperBound;
    }
    
    /**
     * Sets the upper bound.
     *
     * @param anUpperBound the new upper bound.
     */
    public void setUpperBound(double anUpperBound) {
        upperBound = anUpperBound;
    }
}
