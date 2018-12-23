package org.joone.engine;

import java.io.Serializable;
/*
 * @author dkern
 */

public interface Learner extends Serializable {

  /** The Learnable calls this method to make itself known to the Learner
   */
  public abstract void registerLearnable(Learnable l);

  /** Override this method to implement what should be done to LearnableLayers
   */
  public abstract void requestBiasUpdate(double[] currentGradientOuts); 

  /** Override this method to implement what should be done to LearnableSynapses
   */
  public abstract void requestWeightUpdate(double[] currentPattern, double[] currentInps);  
  
  /** Used to set the parameters used by this Learner
   */
  public void setMonitor(Monitor mon);

}