/*
 * MonitorLRManager.java
 *
 * Created on 1 febbraio 2002, 17.06
 */

package org.joone.util;

import org.joone.log.*;
import org.joone.engine.Monitor;
/**
 * This plugin controls the change of the learning rate based on the difference
 * between the last two global error (E) values:
 * if E(t) > E(t-1) then LR = LR * (1 - step/100)
 *           Note: step/100 because step is inserted as a % value from the user
 * if E(t) <= E(t-1) then LR is unchanged
 *
 * @author  pmarrone
 */
public class DynamicAnnealing extends MonitorPlugin 
{
    private static final ILogger log = LoggerFactory.getLogger (DynamicAnnealing.class);
    private double lastError = 0.0;
    private double step = 0.0;
    
    private static final long serialVersionUID = -5494365758818313237L;
    
    /** Creates a new instance of DynamicAnnealing */
    public DynamicAnnealing() {
        super();
    }
    
    protected void manageCycle(Monitor mon) {
        double actError = mon.getGlobalError();
        if ((actError > lastError) && (lastError > 0.0) && (step > 0.0)) {
            double err = mon.getLearningRate() * (1 - step/100);
            mon.setLearningRate(err);
            int currentCycle = mon.getTotCicles() - mon.getCurrentCicle() + 1;
            log.info ("DynamicAnnealing: changed the learning rate to " + err + " at cycle n." + currentCycle);
        }
        lastError = actError;
    }
    
    protected void manageStop(Monitor mon) {
    }
    
    /** Getter for property step.
     * @return Value of property step.
     */
    public double getStep() {
        return step;
    }
    
    /** Setter for property step.
     * @param step New value of property step.
     */
    public void setStep(double step) {
        this.step = step;
        if (step >= 100)
            this.step = 99;
    }
    
    protected void manageStart(Monitor mon) {
    }
    
    protected void manageError(Monitor mon) {
    }
    
    protected void manageStopError(Monitor mon, String msgErr) {
    }
    
}
