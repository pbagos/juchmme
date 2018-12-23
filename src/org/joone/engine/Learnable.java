package org.joone.engine;

/*
 * @author dkern
 */

public interface Learnable {

    Learner getLearner();
    Monitor getMonitor();
    void initLearner();

}