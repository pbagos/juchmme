package org.joone.engine;

import org.joone.engine.extenders.*;

/*
 * BasicLeaner implements Joone's standard learning (simple gradient descent, 
 * "incremental" ( or "pattern-by-pattern", "online") learning with momentum)
 *
 * @author dkern
 * @author Boris Jansen
 */

public class BasicLearner extends ExtendableLearner {
    
    public BasicLearner() {
        setUpdateWeightExtender(new OnlineModeExtender());
        // please be careful of the order of extenders...
        addDeltaRuleExtender(new MomentumExtender());
    }
}