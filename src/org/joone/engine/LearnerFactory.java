/*
 * LearnerFactory.java
 *
 * Created on September 15, 2004, 3:43 PM
 */

package org.joone.engine;

/**
 * Learner factories are used to provide the synapses and layers, through the
 * monitor object with Leaners. 
 *
 * @author Boris Jansen
 */
public interface LearnerFactory extends java.io.Serializable {
    
    // We used to set learners at the monitor object in the following way:
    // Monitor.getLearners().add(0, "org.joone.engine.BasicLearner");
    // Monitor.getLearners().add(1, "org.joone.engine.BatchLearner");
    // Monitor.setLearningMode(1);
    // This method is still available and is an easy and fast way to set learners.
    //
    // However, thanks to the ExtendableLearner and extenders it is quite easy
    // to create various different learners by combination different extenders. 
    // Furthermore, some people would like to set/change certain parameters for 
    // the learners before they are used. 
    // For those purposes the LearnerFactory is created. Once a learner factory
    // is registered at a monitor, the method getLearner() will be used to get
    // a learner and the user can implement the method as he/she likes.
    
    /**
     * Gets a learner for a synapse or layer.
     *
     * @param aMonitor the monitor.
     */
    public Learner getLearner(Monitor aMonitor);
    
}
