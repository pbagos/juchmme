package org.joone.engine;

import org.joone.engine.extenders.*;

/** BatchLearner stores the weight/bias changes during the batch and updates them
 *  after the batch is done.
 *
 * IMPORTANT:   If you want to have standard batch learning, i.e. the BatchSize equals
 *              the number of training patterns available, just use monitor.
 *              setBatchSize(monitor.getTrainingPatterns());
 */
public class BatchLearner extends ExtendableLearner {
    
    public BatchLearner() {
        setUpdateWeightExtender(new BatchModeExtender());
        // please be careful of the order of extenders...
        addDeltaRuleExtender(new MomentumExtender());
    }
    
    /**
     * @deprecated use BatchLearner() and set the batch size
     * with monitor.setBatchSize()
     */
    public BatchLearner(int batchSize) {
        super();
        setBatchSize(batchSize);
    }    
    
    /**
     * @deprecated not used, the BatchModeExtender takes care of everything
     */
    public void initiateNewBatch() {
        // if you want to call it any, probably the next lines are the best...
        if (learnable instanceof LearnableLayer) {
            theUpdateWeightExtender.preBiasUpdate(null);
        } else if (learnable instanceof LearnableSynapse) {
            theUpdateWeightExtender.preWeightUpdate(null, null);
        }
    }
            
    /**
     * @deprecated use monitor.setBatchSize()
     */
    public void setBatchSize(int newBatchSize) {
        ((BatchModeExtender)theUpdateWeightExtender).setBatchSize(newBatchSize);
    }
    
    /**
     * @deprecated use monitor.getBatchSize()
     */
    public int getBatchSize() {
        return ((BatchModeExtender)theUpdateWeightExtender).getBatchSize();
    }
}

