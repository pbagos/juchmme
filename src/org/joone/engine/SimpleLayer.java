package org.joone.engine;

import org.joone.log.*;


/** This abstract class represents layers that are composed
 * by neurons that implement some transfer function.
 */
public abstract class SimpleLayer extends Layer {
    private static final ILogger log = LoggerFactory.getLogger(SimpleLayer.class);
    private double lrate;
    private double momentum;
    private static final long serialVersionUID = -2579073586181182767L;
    
    /** The constructor
     */
    public SimpleLayer() {
        super(); // Logging of instanciation made by the Layer
    }
    
    /** The constructor
     * @param ElemName The name of the Layer
     */
    public SimpleLayer(String ElemName) {
        super(ElemName); // Logging of instanciation made by the Layer
    }
    
    
    /**
     *
     * */
    protected void backward(double[] parm1) {
        if (monitor != null) {
            lrate = monitor.getLearningRate();
            momentum = monitor.getMomentum();
        }
    }
    
    /** Returns the value of the learning rate of the Layer
     * @return double
     */
    public double getLearningRate() {
        return lrate;
    }
    /** Returns the value of the momentum of the Layer
     * @return double
     */
    public double getMomentum() {
        return momentum;
    }
    
    protected void setDimensions() {
        inps = new double[getRows()];
        outs = new double[getRows()];
        gradientInps = new double[getRows()];
        gradientOuts = new double[getRows()];
    }
    
    public void setMonitor(Monitor parm1) {
        super.setMonitor( parm1);
        if (parm1 != null) {
            lrate = monitor.getLearningRate();
            momentum = monitor.getMomentum();
        }
    }
    
    
    /**
     * Needed for Save As XML
     */
    public double getLrate() {
        return this.lrate;
    }
    
    /**
     * Needed for Save As XML
     */
    public void setLrate(double newLrate) {
        this.lrate = newLrate;
    }
    
    /**
     * Needed for Save As XML
     */
    public void setMomentum(double newMomentum) {
        this.momentum = newMomentum;
    }
    
}
