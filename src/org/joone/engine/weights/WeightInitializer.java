/*
 * WeightInitializer.java
 *
 * Created on October 15, 2004, 3:20 PM
 */

package org.joone.engine.weights;

import org.joone.engine.Matrix;

/**
 * This interface desribes the methods that needs to be implemented in order to create new 
 * weight (or bias) initializers. Weight initializers can be set by  using the method 
 * {@link org.joone.engine.Matrix#setWeightInitializer(WeightInitializer).
 *
 * @author  Boris Jansen
 */
public interface WeightInitializer extends java.io.Serializable {
    
    /**
     * Initializes weights (biases) represented by the matrix.
     *
     * @param aMatrix the weights (biases) to be initialized.
     */
    public void initialize(Matrix aMatrix);
}
