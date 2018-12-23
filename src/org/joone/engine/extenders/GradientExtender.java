package org.joone.engine.extenders;

/**
 * This abstract class describes the methods needed for a gradient extender, 
 * that is, a class that computes / changes the gradient value according to 
 * some algorithm.
 *
 * @author Boris Jansen
 */
public abstract class GradientExtender extends LearnerExtender {
    
    /** Creates a new instance of DeltaExtender */
    public GradientExtender() {
    }
    
    /**
     * Computes the gradient value for a bias.
     *
     * @param currentGradientOuts the back propagated gradients.
     * @param j the index of the bias.
     * @param aPreviousGradient a gradient value calculated by a previous 
     * gradient extender.
     */
    public abstract double getGradientBias(double[] currentGradientOuts, int j, double aPreviousGradient);
     
    /**
     * Computes the gradient value for a weight.
     *
     * @param currentInps the forwarded input.
     * @param j the input index of the weight.
     * @param currentPattern the back propagated gradients.
     * @param k the output index of the weight.
     * @param aPreviousGradient a gradients value calculated by a previous gradients extender.
     */
    public abstract double getGradientWeight(double[] currentInps, int j, double[] currentPattern, int k, double aPreviousGradient);
}
