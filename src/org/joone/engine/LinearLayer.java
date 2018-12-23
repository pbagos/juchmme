package org.joone.engine;

import java.util.ArrayList;
import java.util.Collection;
import org.joone.inspection.implementations.BiasInspection;
import org.joone.log.*;

/** The output of a linear layer neuron is the sum of the weighted input values,
 * scaled by the beta parameter. No transfer function is applied to limit the output value
 */
public class LinearLayer extends SimpleLayer {
    private double beta = 1;
    
    /**
     * Logger
     * */
    private static final ILogger log = LoggerFactory.getLogger (LinearLayer.class);
    
    private static final long serialVersionUID = 2243109263560495304L;
    
    /** The constructor
     */
    public LinearLayer() {
        super();
    }
    /** The constructor
     * @param ElemName The name of the Layer
     */
    public LinearLayer(String ElemName) {
        super(ElemName);
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
            outs[x] = beta * pattern[x]; // + bias.value[x][0];
    }
    /** Returns the value of the beta parameter
     * @return double - The beta parameter
     */
    public double getBeta() {
        return beta;
    }
    /** Sets the beta value
     * @param newBeta double
     */
    public void setBeta(double newBeta) {
        beta = newBeta;
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