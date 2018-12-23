/*
 * LogarithmicLayer.java
 *
 * Created on 1 settembre 2002, 21.19
 */

package org.joone.engine;

import org.joone.log.*;

/**
 * This layer implements a logarithmic transfer function.
 * Used in some NN to avoid to saturate the inputs.
 * @author  P.Marrone
 */
public class LogarithmicLayer extends SimpleLayer implements LearnableLayer {
    
    /**
     * Logger definition
     * */
    private static final ILogger log = LoggerFactory.getLogger(LogarithmicLayer.class);
    
    private static final long serialVersionUID = -4983197905588348060L;
    
    /** Creates a new instance of LogarithmicLayer */
    public LogarithmicLayer() {
        super();
        learnable = true;
    }
    public LogarithmicLayer(String elemName) {
        this();
        this.setLayerName(elemName);
    }
    
    /** Transfer function to recall a result on a trained net
     * @param pattern double[] - input pattern
     */
    protected void forward(double[] pattern) {
        double myNeuronInput;
        int n = getRows();
        for (int x=0; x < n; ++x) {
            myNeuronInput = pattern[x] + getBias().value[x][0];
            if (myNeuronInput >= 0)
                outs[x] = Math.log(1 + myNeuronInput);
            else
                outs[x] = -Math.log(1 - myNeuronInput);
        }
    }
    
    /** Reverse transfer function of the component.
     * @param pattern double[] - input pattern on wich to apply the transfer function
     */
    protected void backward(double[] pattern) {
        double dw, absv;
        super.backward(pattern);
        int n = getRows();
        double deriv;
        for (int x = 0; x < n; ++x) {
            if (outs[x] >= 0)
                deriv = 1 / (1 + outs[x]);
            else
                deriv = 1 / (1 - outs[x]);
            gradientOuts[x] = pattern[x] * deriv;
        }
        myLearner.requestBiasUpdate(gradientOuts);
    }
    
    /** @deprecated - Used only for backward compatibility
     */
    public Learner getLearner() {
        learnable = true;
        return super.getLearner();
    }
    
}
