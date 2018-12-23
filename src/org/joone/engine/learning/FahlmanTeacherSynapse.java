/*
 * FahlmanTeacherSynapse.java
 *
 * Created on February 28, 2005, 1:55 PM
 */

package org.joone.engine.learning;

import org.joone.engine.Monitor;
import org.joone.log.*;
import org.joone.engine.listeners.*;

/**
 * <p>
 * This class extends the normal Teacher synapse and implements the Fahlman
 * 40-20-40 criterion (the values can be changed). This teacher makes only sense
 * in case of binary outputs.
 * <p>
 * In case of the default values (40-20-40) and considering [0,1] binary outputs
 * the criterion is fullfilled if for all patterns the output is
 * - within [0, 0.4] in case the desired output is 0
 * - within [0.6, 1] in case the desired output is 1.
 * <p>
 * More about this criterion can be found at 
 * {@link http://citeseer.ist.psu.edu/fahlman88empirical.html}.
 *
 * @author Boris Jansen
 */
public class FahlmanTeacherSynapse extends TeacherSynapse {
    
    /** Constant to indicate (key) the parameter for checking (in the monitor object) 
     * if the criterion has been forfilled or not. */
    public static final String CRITERION = "FAHLMAN_CRITERION";
    
    /**
     * Logger
     **/
    protected static final ILogger log = LoggerFactory.getLogger(FahlmanTeacherSynapse.class);
    
    /** The upperbit value (of the desired output), by default 1. */
    private double upperBit = 1.0;
    
    /** The lowerbit value (of the desired output), by default 0. */
    private double lowerBit = 0.0;
    
    /** The percentage that is considered as a lowerbit, by default 0.4. 
     * In case of desired output bits {0, 1} any output within [0, 0.4]
     * is considered a lower bit
     */
    private double lowerBitPercentage = 0.4;
    
    /** The percentage that is considered as a upperbit, by default 0.4. 
     * In case of desired output bits {0, 1} any output within [0.6, 1]
     * is considered a upper bit
     */
    private double upperBitPercentage = 0.4;
       
    /** Creates a new instance of FahlmanTeacherSynapse */
    public FahlmanTeacherSynapse() {
    }
    
    /**
     * Sets the upper bit.
     *
     * @param aValue sets the upper bit to <code>aValue</code>.
     */
    public void setUpperBit(double aValue) {
        upperBit = aValue;
    }
    
    /**
     * Gets the upper bit value.
     *
     * @return the upper bit value.
     */
    public double getUpperBit() {
        return upperBit;
    }
    
    /**
     * Sets the lower bit.
     *
     * @param aValue sets the lower bit to <code>aValue</code>.
     */
    public void setLowerBit(double aValue) {
        lowerBit = aValue;
    }
    
    /**
     * Gets the lower bit value.
     *
     * @return the lower bit value.
     */
    public double getLowerBit() {
        return lowerBit;
    }
    
    /**
     * Sets the upper bit percentage.
     *
     * @param aValue sets the upper bit percentage to <code>aValue</code>.
     */
    public void setUpperBitPercentage(double aValue) {
        upperBitPercentage = aValue;
    }
    
    /**
     * Gets the upper bit percentage.
     *
     * @return the upper bit percentage.
     */
    public double getUpperBitPercentage() {
        return upperBitPercentage;
    }
    
    /**
     * Sets the lower bit percentage.
     *
     * @param aValue sets the lower bit percentage to <code>aValue</code>.
     */
    public void setLowerBitPercentage(double aValue) {
        lowerBitPercentage = aValue;
    }
    
    /**
     * Gets the lower bit percentage.
     *
     * @return the lower bit percentage.
     */
    public double getLowerBitPercentage() {
        return lowerBitPercentage;
    }
    
    protected double calculateError(double aDesired, double anOutput, int anIndex) {
        if(getMonitor().isValidation()) {
            double myRange = upperBit - lowerBit;
            if(aDesired == lowerBit) {
                myRange *= lowerBitPercentage;
                if(!(anOutput >= lowerBit && anOutput <= lowerBit + myRange)) {
                    getMonitor().setParam(CRITERION, Boolean.FALSE);
                }
            } else if(aDesired == upperBit) {
                myRange *= upperBitPercentage;
                if(!(anOutput >= upperBit - myRange && anOutput <= upperBit)) {
                    getMonitor().setParam(CRITERION, Boolean.FALSE);
                }
            } else {
                log.warn("The values for upper and/or lower bit are not correctly set. No match for desired output " 
                    + aDesired + ".");
                getMonitor().setParam(CRITERION, Boolean.FALSE);
            }
        }
        
        return super.calculateError(aDesired, anOutput, anIndex);
    }   
}
