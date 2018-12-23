/*
 * DeltaBasedConvergenceObserver.java
 *
 * Created on October 29, 2004, 12:05 PM
 */

package org.joone.engine.listeners;

import java.util.*;
import org.joone.engine.*;
import org.joone.net.*;
import org.joone.util.MonitorPlugin;

/**
 * This observer observes if the network has convergenced based on the size of the
 * weight updates (deltas).
 *
 * @author  Boris Jansen
 */
public class DeltaBasedConvergenceObserver extends ConvergenceObserver {
    
    /** Whenever each weight update value for some epochs / cycles  is less than 
     * this size, a <code>ConvergenceEvent</code> will be generated. */
    private double size = 0;
    
    /** Sets the size of the cycles (sequence). Whenever the weight update values of a neural network
     * are equal to or smaller than a certain value for this number of epochs, the network is considered
     * as converged. */
    private int cycles = 5; // default
    
    /** Counter to check how many cycles the deltas where equal to or less than <code>size</code>. */
    private int cycleCounter = 0;
    
    /** The network holding the layers and synapses to be checked. */
    private NeuralNet net;
    
    /** Creates a new instance of DeltaBasedConvergenceObserver */
    public DeltaBasedConvergenceObserver() {
    }
    
    /**
     * Sets the size. Whenever the weight (biases) update values (deltas) are smaller
     * than this value for a certain number of cycles ({@link setCycles()}, the network is
     * considered as converged.
     *
     * @param aSize the size to set.
     */
    public void setSize(double aSize) {
        size = aSize;
    }
    
    /**
     * Gets the size (delta bound for convergence).
     *
     * @return the size.
     */
    public double getSize() {
        return size;
    }
    
    /**
     * Sets the number of cycles. Whenever the deltas are equal to or smaller than the set size
     * for this number of cycles, the network is considered as converged.
     *
     * @param aCylces
     */
    public void setCycles(int aCylces) {
        cycles = aCylces;
    }
    
    /**
     * Gets the number of cycles over which convergence is checked.
     *
     * @return the number of cycles.
     */
    public int getCycles() {
        return cycles;
    }
    
    /**
     * Sets the neural network to be checked for convergence.
     *
     * @param aNet the network to set.
     */
    public void setNeuralNet(NeuralNet aNet) {
        net = aNet;
    }
    
    /**
     * Gets the neural net that is being checked for convergence.
     *
     * @return the net that is being checked for convergence.
     */
    public NeuralNet getNeuralNet() {
        return net;
    }
    
    protected void manageStop(Monitor mon) {
        
    }
    
    protected void manageCycle(Monitor mon) {
        
    }
    
    protected void manageStart(Monitor mon) {
        
    }
    
    protected void manageError(Monitor mon) {
        if(cycles <= 0) {
            return;
        }
        
        Layer myLayer;
        Matrix myBiases, myWeights;
        for(int i = 0; i < net.getLayers().size(); i++) {
            myLayer = (Layer)net.getLayers().get(i);
            myBiases = myLayer.getBias();
            for(int b = 0; b < myBiases.getM_rows(); b++) {
                if(myBiases != null && !isConvergence(myBiases)) {
                    cycleCounter = 0;
                    disableCurrentConvergence = false; // we are not in a convergence state, so if we were
                                                       // we moved out of it
                    return;
                }
            }
            for(int s = 0; s < myLayer.getAllOutputs().size(); s++) {
                if(myLayer.getAllOutputs().get(s) instanceof Synapse) {
                    myWeights = ((Synapse)myLayer.getAllOutputs().get(s)).getWeights();
                    if(myWeights != null && !isConvergence(myWeights)) {
                        cycleCounter = 0;
                        disableCurrentConvergence = false;
                        return;
                    }
                }
            }
        }
        
        cycleCounter++;
        if(cycleCounter == cycles) {
            if(!disableCurrentConvergence) {
                fireNetConverged(mon);
            }
            cycleCounter = 0;
        }
    }
    
    /**
     * Checks if the weights or biases have converged, i.e. if the delta weight update
     * value is below size.
     *
     * @param aMatrix the matrix (weights or biases) to check if their deltas are equal
     * to or below size.
     * @return true if the deltas are equal to or below size, false otherwise.
     */
    protected boolean isConvergence(Matrix aMatrix) {
        for(int r = 0; r < aMatrix.getM_rows(); r++) {
            for(int c = 0; c < aMatrix.getM_cols(); c++) {
                if(Math.abs(aMatrix.delta[r][c]) > size) {
                    return false;
                }
            }
        }
        return true;
    }
    
    protected void manageStopError(Monitor mon, String msgErr) {
        
    }
}
