
package org.joone.net;

import java.util.Vector;
import org.joone.engine.*;

/**
 * This class is useful to validate a neural network.
 * It simply sets some parameters of the neural network passed as parameter
 * and starts itself in a separated thread, notifying a listener when the
 * validation step finishes.
 *
 * @author pmarrone
 */
public class NeuralNetValidator implements Runnable, NeuralNetListener {
    
    final private Vector listeners;
    
    /** The network to validate. */
    final private NeuralNet nnet;
    
    private Thread myThread = null;
    private int currentCycle;
    private int totCycles;
    
    /** Flag indicating if we should use the training data for validation (if
     * <code>true</code>) or should we use the validation data (if <code>false</code>)
     * which is the default. */
    private boolean useTrainingData = false;
    
    public NeuralNetValidator(NeuralNet nn) {
        listeners = new Vector();
        nnet = nn;
    }
    
    public synchronized void addValidationListener(NeuralValidationListener newListener){
        if (!listeners.contains(newListener))
            listeners.addElement(newListener);
    }
    
    protected void validate(){
        totCycles = nnet.getMonitor().getTotCicles();
        currentCycle = nnet.getMonitor().getCurrentCicle();
        nnet.getMonitor().addNeuralNetListener(this);
        nnet.getMonitor().setLearning(false);
        nnet.getMonitor().setValidation(true);
        nnet.getMonitor().setTrainingDataForValidation(useTrainingData);
        nnet.getMonitor().setTotCicles(1);
        nnet.go();
    }
    
    public void fireNetValidated(){
        double error = nnet.getMonitor().getGlobalError();
        nnet.getDescriptor().setValidationError(error);
        Object[] list;
        synchronized (this) {
            list = listeners.toArray();
        }
        for (int i=0; i < list.length; ++i) {
            NeuralValidationListener nvl = (NeuralValidationListener)list[i];
            nvl.netValidated(new NeuralValidationEvent(nnet));
        }
    }
    
    /**
     * By default the validator validates a neural network with validation data,
     * however by calling this method before calling the <code>start()</code>
     * method, one can decide if the network should be validated with validation
     * data (the parameter <code>anUse</code> should be <code>false</code>) or
     * by using the training data (the parameter <code>anUse</code> should be 
     * <code>true</code>).
     *
     * @param anUse <code>true</code> if we should use training data for validation,
     * <code>false</code> if we should use the validation data for validation (default).
     */
    public void useTrainingData(boolean anUse) {
        useTrainingData = anUse;
    }
    
    /** Starts the validation into a separated thread
     */
    public void start() {
        if (myThread == null) {
            myThread = new Thread(this, "Validator");
            myThread.start();
        }
    }
    
    public void run() {
        this.validate();
        myThread = null;
    }
    
    public void netStopped(NeuralNetEvent e) {
        this.fireNetValidated();
    }
    
    public void cicleTerminated(NeuralNetEvent e) {
    }
    
    public void netStarted(NeuralNetEvent e) {
    }
    
    public void errorChanged(NeuralNetEvent e) {
    }
    
    public void netStoppedError(NeuralNetEvent e, String error) {
    }
    
    /**
     * Gets the network to validate (or has been validated).
     *
     * @return the netork to validate (or the network that has been validated).
     */
    public NeuralNet getNeuralNet() {
        return nnet;
    }
}
