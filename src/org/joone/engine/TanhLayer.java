package org.joone.engine;

import org.joone.log.*;

/**
 * Layer that applies the tangent hyperbolic transfer function
 * to its input patterns
 */

public class TanhLayer extends SimpleLayer implements LearnableLayer {
    
    private static final long serialVersionUID = -2073914754873517298L;
    
    /**
     * Logger
     * */
    private static final ILogger log = LoggerFactory.getLogger (TanhLayer.class);
    
    /** Constant to overcome the "flat spot" problem. This problem is described in: 
     * S.E. Fahlman, "An emperical study of learning speed in backpropagation with 
     * good scaling properties," Dept. Comput. Sci. Carnegie Mellon Univ., Pittsburgh,
     * PA, Tech. Rep., CMU-CS-88-162, 1988.
     * Setting this constant to 0 (default value), the derivative of the sigmoid function
     * is unchanged (normal function). An good value for this constant might be 0.1.
     */
    private double flatSpotConstant = 0.0;
    
    /**
     * default constructor
     * */
    public TanhLayer() {
        super();
        learnable = true;
    }
        
    public TanhLayer(java.lang.String name) {
        this();
        this.setLayerName(name);
    }
    
    /**
     * 
     * @see SimpleLayer#backward (double[])
     * */
    public void backward(double[] pattern) {
        super.backward(pattern);
        double dw, absv;
        int x;
        int n = getRows();
        for (x = 0; x < n; ++x) {
            gradientOuts[x] = pattern[x] * ((1 + outs[x]) * (1 - outs[x]) + getFlatSpotConstant());
        }
	myLearner.requestBiasUpdate(gradientOuts);
    }
    
    /**
     * @see SimpleLayer#forward (double[])
     * */
    public void forward(double[] pattern) {
        double nExp, pExp;
        int x;
        int n = getRows();
        for (x=0; x < n; ++x) {
            //fast-forward :) A Tanh computation that only needs to call the expensive Math.exp once, saves a little time.
            outs[x] = -1 + (2/ (1+Math.exp(-2* (pattern[x]+bias.value[x][0]) ) ) );
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