/*
 * ContextLayer.java
 *
 * Created on 20 settembre 2002, 14.59
 */

package org.joone.engine;

import java.util.ArrayList;
import java.util.Collection;
import org.joone.inspection.implementations.BiasInspection;

/**
 * The context layer is similar to the linear layer except that
 * it has an auto-recurrent connection between its output and input.
 *
 * @author  P.Marrone
 */
public class ContextLayer extends SimpleLayer {
    
    private double beta = 1;    
    private double timeConstant = 0.5;    
    
    private static final long serialVersionUID = -8773800970295287404L;
    
    public ContextLayer() {
        super();
    }
    
    public ContextLayer(java.lang.String name) {
        super(name);
    }
    
    public void backward(double[] pattern) {
        int x;
        int n = getRows();
        for (x = 0; x < n; ++x)
            gradientOuts[x] = pattern[x] * beta;
    }
    public void forward(double[] pattern) {
        int x;
        int n = getRows();
        for (x = 0; x < n; ++x)
            outs[x] = beta * (pattern[x] + (timeConstant * outs[x]));
    }
    
    /** Getter for property beta.
     * @return Value of property beta.
     *
     */
    public double getBeta() {
        return beta;
    }
    
    /** Setter for property beta.
     * @param beta New value of property beta.
     *
     */
    public void setBeta(double beta) {
        this.beta = beta;
    }
    
    /** Getter for property timeConstant.
     * @return Value of property timeConstant.
     *
     */
    public double getTimeConstant() {
        return timeConstant;
    }
    
    /** Setter for property timeConstant.
     * @param timeConstant New value of property timeConstant.
     *
     */
    public void setTimeConstant(double timeConstant) {
        this.timeConstant = timeConstant;
    }

    /**
     * It doesn't make sense to return biases for this layer
     * @return null
     */
    public Collection Inspections() {
        Collection col = new ArrayList();
        col.add(new BiasInspection(null));
        return col;
    }
    
}
