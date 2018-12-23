package org.joone.util;

import org.joone.engine.*;

/**
 * This plugin changes linearly the values of the learning rate and of the momentum parameters.
 * The values go from an initial value to a final value linearly and the step is determined
 * by the formula: step = (FinalValue - InitValue) / numEphocs
 * Creation date: (26/10/2000 23.47.58)
 * @author: pmarrone
 */
public class LinearAnnealing extends MonitorPlugin {
	private double learningRateInitial;
	private double learningRateFinal;
	private double momentumInitial;
	private double momentumFinal;
        
        private static final long serialVersionUID = -3786770944656325519L;
        
/**
 * Insert the method's description here.
 * Creation date: (26/10/2000 23.49.52)
 * @return double
 */
public double getLearningRateFinal() {
	return learningRateFinal;
}
/**
 * Insert the method's description here.
 * Creation date: (26/10/2000 23.49.26)
 * @return double
 */
public double getLearningRateInitial() {
	return learningRateInitial;
}
/**
 * Insert the method's description here.
 * Creation date: (26/10/2000 23.50.29)
 * @return double
 */
public double getMomentumFinal() {
	return momentumFinal;
}
/**
 * Insert the method's description here.
 * Creation date: (26/10/2000 23.50.14)
 * @return double
 */
public double getMomentumInitial() {
	return momentumInitial;
}

/**
 * Insert the method's description here.
 * Creation date: (26/10/2000 23.49.52)
 * @param newLearningRateFinal double
 */
public void setLearningRateFinal(double newLearningRateFinal) {
	learningRateFinal = newLearningRateFinal;
}
/**
 * Insert the method's description here.
 * Creation date: (26/10/2000 23.49.26)
 * @param newLearningRateInitial double
 */
public void setLearningRateInitial(double newLearningRateInitial) {
	learningRateInitial = newLearningRateInitial;
}
/**
 * Insert the method's description here.
 * Creation date: (26/10/2000 23.50.29)
 * @param newMomentumFinal double
 */
public void setMomentumFinal(double newMomentumFinal) {
	momentumFinal = newMomentumFinal;
}
/**
 * Insert the method's description here.
 * Creation date: (26/10/2000 23.50.14)
 * @param newMomentumInitial double
 */
public void setMomentumInitial(double newMomentumInitial) {
	momentumInitial = newMomentumInitial;
}

protected void manageCycle(Monitor mon) {
	double stepLR = (getLearningRateInitial() - getLearningRateFinal()) / mon.getTotCicles();
	double stepMom = (getMomentumInitial() - getMomentumFinal()) / mon.getTotCicles();
	int currCicle = mon.getTotCicles() - mon.getCurrentCicle();
	mon.setLearningRate(getLearningRateInitial() - (stepLR * currCicle));
	mon.setMomentum(getMomentumInitial() - (stepMom * currCicle));
}

protected void manageStop(Monitor mon) {
}

protected void manageStart(Monitor mon) {
}

protected void manageError(Monitor mon) {
}

protected void manageStopError(Monitor mon, String msgErr) {
}

}