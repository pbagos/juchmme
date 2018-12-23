package org.joone.util;



import org.joone.engine.*;
import org.joone.net.NeuralNet;
/**
 * This class represents a generic listener of the net's events.
 * Any new listener can be created simply extending this class
 * and filling the manageXxxx methods with the necessary code.
 *
 * @author: Administrator
 */
public abstract class MonitorPlugin implements  java.io.Serializable, NeuralNetListener {
    
    private String name;
    private static final long serialVersionUID = 951079164859904152L;
    
    private int rate = 1;
    private NeuralNet neuralNet;
    
    /**
     * cicleTerminated method.
     */
    public void cicleTerminated(NeuralNetEvent e) {
        Monitor mon = (Monitor)e.getSource();
        if (toBeManaged(mon))
            manageCycle(mon);
    }
    /**
     * netStopped method comment.
     */
    public void netStopped(NeuralNetEvent e) {
        Monitor mon = (Monitor)e.getSource();
        manageStop(mon);
    }
    
    public void netStarted(NeuralNetEvent e) {
        Monitor mon = (Monitor)e.getSource();
        manageStart(mon);
    }
    
    public void errorChanged(NeuralNetEvent e) {
        Monitor mon = (Monitor)e.getSource();
        if (toBeManaged(mon))
            manageError(mon);
    }
    
    public void netStoppedError(NeuralNetEvent e,String error) {
        Monitor mon = (Monitor)e.getSource();
        manageStopError(mon, error);
    }
    
    protected boolean toBeManaged(Monitor monitor) {
        if (getRate() == 0) // If rate is zero the events are never managed
            return false;
        int currentCycle = monitor.getTotCicles() - monitor.getCurrentCicle() + 1;
        int cl = currentCycle / getRate();
        /* We want manage the events only every rate cycles */
        if ((cl * getRate()) == currentCycle)
            return true;
        else
            return false;
    }
    
    protected abstract void manageStop(Monitor mon);
    protected abstract void manageCycle(Monitor mon);
    protected abstract void manageStart(Monitor mon);
    protected abstract void manageError(Monitor mon);
    protected abstract void manageStopError(Monitor mon, String msgErr);
    
    /** Getter for property name.
     * @return Value of property name.
     */
    public java.lang.String getName() {
        return name;
    }
    
    /** Setter for property name.
     * @param name New value of property name.
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }
    
    /** Getter for property rate.
     * This property represents the interval (# of cycles)
     * between two calls to the manageXxxx methods.
     * @return Value of property rate.
     */
    public int getRate() {
        return rate;
    }
    
    /** Setter for property rate.
     * This property represents the interval (# of cycles)
     * between two calls to the manageXxxx methods.
     * @param rate New value of property rate.
     */
    public void setRate(int rate) {
        this.rate = rate;
    }
    
    public NeuralNet getNeuralNet(){
        return neuralNet;
    }
    
    public void setNeuralNet(NeuralNet neuralNet){
        this.neuralNet = neuralNet;
    }
    
}