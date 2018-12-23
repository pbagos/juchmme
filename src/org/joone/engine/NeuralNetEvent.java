package org.joone.engine;

import org.joone.net.NeuralNet;

/**
 * Transport class used to notify the events raised from a neural network
 */
public class NeuralNetEvent extends java.util.EventObject {
    
    private static final long serialVersionUID = -2307998901508765401L;
    private NeuralNet nnet;
    
    /**
     * The event constructor
     * @param source The object generating this event. Normally it is the neural network's Monitor
     */
    public NeuralNetEvent(Monitor source) {
        super(source);
    }
    
    /**
     * Getter for the NeuralNet generating this event.
     * Warning: Use this method ONLY if the event has been raised by
     * an org.joone.helpers class, otherwise you could get a null value.
     * @return The neural network generating this event
     * @since 1.2.2
     */
    public NeuralNet getNeuralNet() {
        return nnet;
    }
    
    /**
     * Setter for the NeuralNet generating this event.
     * @param nnet The neural network generating this event
     * @since 1.2.2
     */
    public void setNeuralNet(NeuralNet nnet) {
        this.nnet = nnet;
    }
    
}