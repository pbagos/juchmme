/*
 * RbfInputSynapse.java
 *
 * Created on July 21, 2004, 3:58 PM
 */

package org.joone.engine;

/**
 * The synapse to the input of a radial basis function layer should't provide a
 * single value to every neuron in the output (RBF) layer, as is usual the case.
 * It should provide the outputs of all the input neurons as a vector to every
 * neuron in the radial basis function layer.
 *
 * @author Boris Jansen
 */
public class RbfInputSynapse extends Synapse {
    
    /** Creates a new instance of RbfInputSynapse */
    public RbfInputSynapse() {
    }
    
    protected void backward(double[] pattern) {
        // we don't come here... revGet() returns null
        // see command revGet()
    }
    
    /**
    public Pattern revGet() {
        // The correct way is to overwrite revGet() to return null,
        // because this synapse does not perform back propagation,
        // however, there exist somewhere a bug. The patterns (input
        // and desired get out of sink, so for a temporary solutions
        // we don't overwrite revGet and left backward() empty
        return null;
    }
     */
    
    protected void forward(double[] pattern) {
        // We output the input vector. The RBF layer should process
        // this input vector for each neuron.
        outs = pattern;
    }
    
    protected void setArrays(int rows, int cols) {
        inps = new double[rows];
        outs = new double[rows];
        bouts = new double[rows];
    }
    
    protected void setDimensions(int rows, int cols) {
        if (rows == -1) {
            rows = getInputDimension();
        }
        if(cols == -1) {
            cols = getOutputDimension();
        }
        
        setArrays(rows, cols);
    }
    
}
