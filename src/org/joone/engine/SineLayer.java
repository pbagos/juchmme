/*
 * SineLayer.java
 *
 * Created on October 12, 2004, 4:20 PM
 */

package org.joone.engine;

import org.joone.exception.JooneRuntimeException;
import org.joone.log.*;

/**
 * The output of a sine layer neuron is the sum of the weighted input values,
 * applied to a sine (<code>sin(x)</code>). Neurons with sine activation 
 * problems might be useful in problems with periodicity.
 *
 * @see SimpleLayer parent
 * @see Layer parent
 * @see NeuralLayer implemented interface
 *
 * @author  Boris Jansen
 */
public class SineLayer extends SimpleLayer  implements LearnableLayer {
    
    private static final long serialVersionUID = -2636086679111635756L;
    
    /** The logger for this class. */
    private static final ILogger log = LoggerFactory.getLogger (SineLayer.class);
    
    /** Creates a new instance of SineLayer */
    public SineLayer() {
        super();
        learnable = true;
    }
    
    /** 
     * Creates a new instance of SineLayer 
     *
     * @param aName The name of the layer
     */
    public SineLayer(String aName) {
        this();
        setLayerName(aName);
    }

    protected void forward(double[] aPattern) throws JooneRuntimeException {
        double myNeuronInput;
        int myRows = getRows(), i = 0;
        try {
            for(i = 0; i < myRows; i++) {
                myNeuronInput = aPattern[i] + getBias().value[i][0];
                outs[i] = Math.sin(myNeuronInput);
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
            gradientOuts[i] = aPattern[i] * Math.cos(inps[i]);
        }
	myLearner.requestBiasUpdate(gradientOuts);
    }
}
