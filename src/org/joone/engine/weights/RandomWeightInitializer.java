/*
 * RandomWeightInitializer.java
 *
 * Created on October 15, 2004, 3:30 PM
 */

package org.joone.engine.weights;

import org.joone.engine.Matrix;
import org.joone.log.*;

/**
 * This class initializes weights (and biases) in a random way within a given domain.
 *
 * @author  Boris Jansen
 */
public class RandomWeightInitializer implements WeightInitializer {
    
    /** Logger for this class. */
    private static final ILogger log = LoggerFactory.getLogger(RandomWeightInitializer.class);
    
    private static final long serialVersionUID = 1547731234507850525L;
    
    /** The upper boundery of the domain to initialize the weights with. */
    private double upperBound = 0;
    
    /** The lower boundery of the domain to initialize the weights with. */
    private double lowerBound = 0;
    
    /** 
     * Creates a new instance of RandomWeightInitializer
     *
     * @param aBoundary the boundaries of the domain to initialize the weights with
     * to <code>[-aBoundary, aBoundary]</code>.
     */
    public RandomWeightInitializer(double aBoundary) {
        if(aBoundary < 0) {
            log.warn("Boundary smaller than zero. Domain set to [" + aBoundary + ", " + -aBoundary + "].");
            aBoundary = Math.abs(aBoundary);
        }
        
        upperBound = aBoundary;
        lowerBound = -aBoundary;
    }
    
    /** 
     * Creates a new instance of RandomWeightInitializer and set the domain to initialize
     * the weights with to <code>[aLowerBound, anUpperBound]</code>.
     *
     * @param aLowerBound the lower boundary of the domain to initialize the weights with.
     * @param anUpperBound the upper boundary of the domain to initialize the weights with.
     * to <code>[-aBoundary, aBoundary]</code>.
     */
    public RandomWeightInitializer(double aLowerBound, double anUpperBound) {
        if(aLowerBound > anUpperBound) {
            log.warn("Lower bound is larger than upper bound. Domain set to [" 
                + anUpperBound + ", " + aLowerBound + "].");
            upperBound = aLowerBound;
            lowerBound = anUpperBound;
        } else {
            upperBound = anUpperBound;
            lowerBound = aLowerBound;
        }
    }

    /**
     * Initializes the weights or biases within the domain <code>[lowerBound, upperBound]</code>.
     *
     * @param aMatrix the weights or biases to initialize.
     */
    public void initialize(Matrix aMatrix) {
        for(int x = 0; x < aMatrix.getM_rows(); x++) {
            for(int y = 0; y < aMatrix.getM_cols(); y++) {
                if(aMatrix.enabled[x][y] && !aMatrix.fixed[x][y]) {
                    aMatrix.value[x][y] = lowerBound + Math.random() * (upperBound - lowerBound);
                }
            }
        }
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
