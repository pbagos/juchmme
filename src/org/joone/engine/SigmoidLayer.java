package org.joone.engine;

import org.joone.exception.JooneRuntimeException;
import org.joone.log.*;



/** The output of a sigmoid layer neuron is the sum of the weighted input values,
 * applied to a sigmoid function. This function is expressed mathematically as:
 * y = 1 / (1 + e^-x)
 * This has the effect of smoothly limiting the output within the range 0 and 1
 *
 * @see SimpleLayer parent
 * @see Layer parent
 * @see NeuralLayer implemented interface
 */
public class SigmoidLayer extends SimpleLayer  implements LearnableLayer {
    
    private static final ILogger log = LoggerFactory.getLogger(SigmoidLayer.class);
    
    private static final long serialVersionUID = -8700747963164046048L;
    
    /** Constant to overcome the "flat spot" problem. This problem is described in: 
     * S.E. Fahlman, "An emperical study of learning speed in backpropagation with 
     * good scaling properties," Dept. Comput. Sci. Carnegie Mellon Univ., Pittsburgh,
     * PA, Tech. Rep., CMU-CS-88-162, 1988.
     * Setting this constant to 0 (default value), the derivative of the sigmoid function
     * is unchanged (normal function). An good value for this constant might be 0.1.
     */
    private double flatSpotConstant = 0.0;
    
    /** The constructor
     */
    public SigmoidLayer() {
        super();
        learnable = true;
    }
    /** The constructor
     * @param ElemName The name of the Layer
     */
    public SigmoidLayer(java.lang.String ElemName) {
        this();
        this.setLayerName(ElemName);
    }
    
    public void backward(double[] pattern)
    throws JooneRuntimeException {
        super.backward(pattern);
        double dw, absv;
        int x;
        int n = getRows();
        for (x = 0; x < n; ++x) {
            gradientOuts[x] = pattern[x] * (outs[x] * (1 - outs[x]) + getFlatSpotConstant());
        }
        myLearner.requestBiasUpdate(gradientOuts);
    }
    
    /**
     * This method accepts an array of values in input and forwards it
     * according to the Sigmoid propagation pattern.
     *
     * @param pattern
     * @see NeuralLayer#forward (double[])
     * @throws JooneRuntimeException This <code>Exception </code> is  a wrapper Exception when an Exception is thrown
     *                               while doing the maths.
     * */
    public void forward(double[] pattern)
    throws JooneRuntimeException {
        int x = 0;
        double in;
        int n = getRows();
        try {
            for ( x = 0; x < n; ++x) {
                in = pattern[x] + bias.value[x][0];
                outs[x] = 1 / (1 + Math.exp(-in));
            }
        }catch (Exception aioobe) {
            String msg;
            log.error( msg = "Exception thrown while processing the element " + x + " of the array. Value is : " + pattern[x]
                    + " Exception thrown is " + aioobe.getClass().getName() + ". Message is " + aioobe.getMessage() );
            throw new JooneRuntimeException(msg, aioobe);
            //aioobe.printStackTrace();
        }
    }
    
    /** @deprecated - Used only for backward compatibility
     */
    public Learner getLearner() {
        learnable = true;
        return super.getLearner();
    }
    
    /**
     * Sets the constant to overcome the flat spot problem.
     * This problem is described in: 
     * S.E. Fahlman, "An emperical study of learning speed in backpropagation with 
     * good scaling properties," Dept. Comput. Sci. Carnegie Mellon Univ., Pittsburgh,
     * PA, Tech. Rep., CMU-CS-88-162, 1988.
     * Setting this constant to 0 (default value), the derivative of the sigmoid function
     * is unchanged (normal function). An good value for this constant might be 0.1.
     *
     * @param aConstant
     */
    public void setFlatSpotConstant(double aConstant) {
        flatSpotConstant = aConstant;
    }
    
    /**
     * Gets the flat spot constant.
     *
     * @return the flat spot constant.
     */
    public double getFlatSpotConstant() {
        return flatSpotConstant;
    }
    
}