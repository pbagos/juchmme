/*
 * SimulatedAnnealingExtender.java
 *
 * Created on September 15, 2004, 1:18 PM
 */

package org.joone.engine.extenders;

/**
 * Simulated annealing (SA) refers to the process in which random or thermal
 * noise in a system is systematically decreased over time so as to enhance
 * the system's response.
 *
 * Basically the change of weights and biases in SA is defined as:
 * dW = dw + (n)(r)(2^-kt),
 * where dw is the weight / bias change produced by standard back propagation,
 * n is a constant controlling the initial intensity of the noise, k is the
 * decay constant,t is the generation counter and r is a random number.
 *
 * @author Boris Jansen
 */
public class SimulatedAnnealingExtender extends DeltaRuleExtender {
    
    /** Constant controlling the initial intensity of the noise. */
    private double theN = 0.3; // default value
    
    /** The noise decay constant. */
    private double theK = 0.002; // default value
    
    /** The random number boundary. */
    private double theBoundary = 0.5; // default value, so the random number 
                                      // is between <-0.5, 0.5>
    
    /** Creates a new instance of SimulatedAnnealingExtender */
    public SimulatedAnnealingExtender() {
    }
    
    public double getDelta(double[] currentGradientOuts, int j, double aPreviousDelta) {
        int myCycle;
        if(getLearner().getUpdateWeightExtender().storeWeightsBiases()) {
            // the biases will be stored this cycle, add noise
            myCycle = getLearner().getMonitor().getTotCicles() 
                - getLearner().getMonitor().getCurrentCicle();
            
            aPreviousDelta += getN() * getRandom() * Math.pow(2, -1 * getK() * myCycle);
        }
        
        return aPreviousDelta;
    }
    
    public double getDelta(double[] currentInps, int j, double[] currentPattern, int k, double aPreviousDelta) {
        int myCycle;
        if(getLearner().getUpdateWeightExtender().storeWeightsBiases()) {
            // the weights will be stored this cycle, add noise
            myCycle = getLearner().getMonitor().getTotCicles() 
                - getLearner().getMonitor().getCurrentCicle();
            
            aPreviousDelta += getN() * getRandom() * Math.pow(2, -1 * getK() * myCycle);
        }
        
        return aPreviousDelta;
    }
    
    public void postBiasUpdate(double[] currentGradientOuts) {
    }
    
    public void postWeightUpdate(double[] currentPattern, double[] currentInps) {
    }
    
    public void preBiasUpdate(double[] currentGradientOuts) {
    }
    
    public void preWeightUpdate(double[] currentPattern, double[] currentInps) {
    }
    
    /**
     * Gets the constant controlling the initial noise.
     *
     * @return the constant controlling the initial noise.
     */
    public double getN() {
        return theN;
    }
    
    /**
     * Sets the constant controlling the initial noise.
     *
     * @param aN the constant controlling the initial noise.
     */
    public void setN(double aN) {
        theN = aN;
    }
    
    /**
     * Gets the noise decay constant.
     *
     * @return the noise decay constant.
     */
    public double getK() {
        return theK;
    }
    
    /**
     * Sets the noise decay constant.
     *
     * @param aK the noise decay constant.
     */
    public void setK(double aK) {
        theK = aK;
    }
    
    /**
     * Gets the random number boundary.
     *
     * @return the random number boundary.
     */
    public double getRandomBoundary() {
        return theBoundary;
    }
    
    /**
     * Sets the noise decay constant.
     *
     * @param aK the noise decay constant.
     */
    public void setRandomBoundary(double aBoundary) {
        theBoundary = aBoundary;
    }
    
    /**
     * Gets a random value between the random boundary.
     *
     * @return a random value between the random boundary.
     */
    protected double getRandom() {
        return Math.random() * 2 * getRandomBoundary() - getRandomBoundary();
    }
}
