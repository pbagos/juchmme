package org.joone.engine;

import org.joone.engine.extenders.*;

/**
 * This class implements the RPROP learning algorithm.
 *
 * @author Boris Jansen
 */
public class RpropLearner extends ExtendableLearner {
    
    /** The RPROP extender. Only used to make back compatibility possible. */
    private RpropExtender theRpropExtender;
    
    /** Creates a new instance of RpropLearner */
    public RpropLearner() {
        setUpdateWeightExtender(new BatchModeExtender());
        
        theRpropExtender = new RpropExtender();
        addDeltaRuleExtender(theRpropExtender);
    }
    
    /**
     * Creates a new instance of RpropLearner.
     *
     * @param aParameters the parameter for this learning algorithm.
     */
    public RpropLearner(RpropParameters aParameters) {
        super();
        
        theRpropExtender.setParameters(aParameters);
    }
    
    /**
     * @deprecated used for backward compatibility
     */
    protected void reinit() {
	theRpropExtender.reinit();
    }
    
    public RpropParameters getParameters() {
        return theRpropExtender.getParameters();
    }
    
    public void setParameters(RpropParameters aParameters) {
        theRpropExtender.setParameters(aParameters);
    }
    
    /**
     * Gets the sign of a double.
     *
     * return the sign of a double (-1, 0, 1).
     */
    protected double sign(double d) {
        if(d > 0) {
            return 1.0;
        } else if(d < 0) {
            return -1.0;
        }
        return 0;
    }
}
