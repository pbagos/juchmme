/*
 * GaussLayer.java
 *
 * Created on October 28, 2004, 11:58 AM
 */

package org.joone.engine;

import org.joone.exception.JooneRuntimeException;
import org.joone.log.*;

/**
 * The output of a Gauss(ian) layer neuron is the sum of the weighted input values,
 * applied to a gaussian curve (<code>exp(- x * x)</code>).
 *
 * @see SimpleLayer parent
 * @see Layer parent
 * @see NeuralLayer implemented interface
 *
 * @author  Boris Jansen
 */
public class GaussLayer extends SimpleLayer  implements LearnableLayer {
    
    /** The logger for this class. */
    private static final ILogger log = LoggerFactory.getLogger(GaussLayer.class);
    
    /** Creates a new instance of GaussLayer */
    public GaussLayer() {
        super();
        learnable = true;
    }
    
    /** 
     * Creates a new instance of GaussLayer
     *
     * @param aName The name of the layer
     */
    public GaussLayer(String aName) {
        this();
        setLayerName(aName);
    }
    
    protected void forward(double[] aPattern) throws JooneRuntimeException {
        double myNeuronInput;
        int myRows = getRows(), i = 0;
        try {
            for(i = 0; i < myRows; i++) {
                myNeuronInput = aPattern[i] + getBias().value[i][0];
                outs[i] = Math.exp(-myNeuronInput * myNeuronInput);
            }
        }catch (Exception aioobe) {
            String msg;
            log.error(msg = "Exception thrown while processing the element " + i + " of the array. Value is : " + aPattern[i]
                        + " Exception thrown is " + aioobe.getClass ().getName () + ". Message is " + aioobe.getMessage());
            throw new JooneRuntimeException (msg, aioobe);
        }
    }
    
    public void backward(double[] aPattern) throws JooneRuntimeException {
        super.backward(aPattern);
        
        int myRows = getRows(), i = 0;
        for(i = 0; i < myRows; i++) {
            gradientOuts[i] = aPattern[i] * -2 * inps[i] * outs[i];
        }
	myLearner.requestBiasUpdate(gradientOuts);
    }
    
}
