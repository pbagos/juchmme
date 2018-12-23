/*
 * RbfLayer.java
 *
 * Created on July 21, 2004, 3:32 PM
 */

package org.joone.engine;

/**
 * This is the basis (helper) for radial basis function layers.
 *
 * @author Boris Jansen
 */
public abstract class RbfLayer extends Layer {
    
    /** Creates a new instance of RbfLayer */
    public RbfLayer() {
        super();
    }
    
    /** 
     * Creates a new instance of RbfLayer
     *
     * @param anElemName The name of the Layer
     */
    public RbfLayer(String anElemName) {
        super(anElemName);
    }
    
    protected void setDimensions() {
        // cannot set inps and gradientOuts, unrelated to the number of neurons
        outs = new double[getRows()];
        gradientInps = new double[getRows()];
    }
    
    /**
     * Adjusts the size of a layer if the size of the forward pattern differs.
     *
     * @param aPattern the pattern holding a different size than the layer
     * (dimension of neurons is not in accordance with the dimension of the 
     * pattern that is being forwarded).
     */
    protected void adjustSizeToFwdPattern(double[] aPattern) {
        // In case of a RBF layer the size of a pattern might differ
        // from the size of (number of neurons in) the layer. So we 
        // don't adjust the size of the layer (as is done usually, see Layer), 
        // but we just adjust the pattern size
        inps = new double[aPattern.length];
    }
}
