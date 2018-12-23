package org.joone.engine;


/**
 * This class provides some basic simple functionality that can be used (extended) by other learners.
 *
 * @author Boris Jansen
 */
public abstract class AbstractLearner implements Learner {
    
    /** The learnable, the object that is subjected to the learning process. */
    protected Learnable learnable = null;
    
    /** The layer (biases) that is subjected to the learning process. */
    protected LearnableLayer learnableLayer = null;
    
    /** The synapse (weights) that is subjected to the learning process. */
    protected LearnableSynapse  learnableSynapse = null;
    
    /** The saved monitor object. */
    protected Monitor monitor;
    
    /** Creates a new instance of AbstractLearner */
    public AbstractLearner() {
    }
    
    /** Learnable makes itself known to the Learner, also the type of Learnable is checked.
     */
    public void registerLearnable(Learnable aLearnable) {
        learnable = aLearnable;
        if (aLearnable instanceof LearnableLayer) {
            learnableLayer = (LearnableLayer) aLearnable; // this reduces the number of casts neccessary later
        } else if (aLearnable instanceof LearnableSynapse) {
            learnableSynapse = (LearnableSynapse) aLearnable;
        }
    }
    
    /** Override this method to get the needed parameters from 
     * the Monitor object passed as parameter
     */
    public void setMonitor(Monitor mon) {
        monitor = mon;
    }
    
    /**
     * Gets the monitor object.
     *
     * @return the monitor object.
     */
    public Monitor getMonitor() {
        return monitor;
    }
    
    /**
     * Gets the layer the learner is associated with.
     *
     * @return the layer the learner is associated with.
     */
    public LearnableLayer getLayer() {
        return learnableLayer;
    }
    
    /**
     * Gets the synapse the learner is associated with.
     *
     * @return the synapse the learner is associated wiht.
     */
    public LearnableSynapse getSynapse() {
        return learnableSynapse;
    }
}
