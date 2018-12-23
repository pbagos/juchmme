package org.joone.engine;

/**
 * This layer consists of linear neurons, i.e. neurons that sum up their inputs 
 * (actually this is done by the (full) synapse in Joone) along with their biases.
 * In the learning process the biases are adjusted in an attempt to output a value
 * closer to the desired output.
 *
 * This layer differs from LinearLayer in two ways:
 *  - This layer uses biases. These biases can/will also be adjusted in the 
 *    learning process.
 *  - It has no scalar beta parameter.  
 *
 * @author Boris Jansen
 */
public class BiasedLinearLayer extends SimpleLayer implements LearnableLayer {
    
    /** Creates a new instance of BiasedLinearLayer */
    public BiasedLinearLayer() {
        super();
    }
    
    /** 
     * Creates a new instance of BiasedLinearLayer. 
     *
     * @param The name of the layer.
     */
    public BiasedLinearLayer(String anElemName) {
        super(anElemName);
    }
    
    public void backward(double[] pattern) {
        int x;
        int n = getRows();
        for (x = 0; x < n; ++x) {
            gradientOuts[x] = pattern[x];
        }
        myLearner.requestBiasUpdate(gradientOuts);
    }
    
    public void forward(double[] pattern) {
        int x;
        int n = getRows();
        for (x = 0; x < n; ++x) {
            outs[x] = pattern[x] + bias.value[x][0];
        }
    }
    
    /** @deprecated - Used only for backward compatibility
     */
    public Learner getLearner() {
        learnable = true;
        return super.getLearner();
    }
}
