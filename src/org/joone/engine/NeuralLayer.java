package org.joone.engine;

import java.util.*;

/**
 * This is the interface for all the layer objects of the neural network
 */
public interface NeuralLayer {
    /** Adds a noise to the biases of the layer and to all the input synapses connected
     * @param amplitude the noise's amplitude in terms of distance from zero;
     * e.g.: a value equal 0.3 means a noise from -0.3 to 0.3
     */
    public void addNoise(double amplitude);

    /** Copies a Layer into another one, to obtain a type-transformation
     * from a kind of Layer to another.
     * The old Layer is disconnected from the net, while the new Layer
     * takes its place.
     * @param newLayer neural.engine.Layer
     * @return The new layer
     */
    public NeuralLayer copyInto(NeuralLayer newLayer);

    /** Returns the vector of the input listeners
     * @return java.util.Vector
     */
    public java.util.Vector getAllInputs();

    /** Returns the vector of the input listeners
     * @return java.util.Vector
     */
    public java.util.Vector getAllOutputs();

    /** Return the bias matrix
     * @return neural.engine.Matrix
     */
    public Matrix getBias();

    /** Returns the name of the layer
     * @return java.lang.String
     */
    public java.lang.String getLayerName();

    /** Returns the dimension (# of neurons) of the Layer
     * @return int
     */
    public int getRows();

    /** Remove all the input listeners of the net
     */
    public void removeAllInputs();

    /** Remove all the output listeners of the net
     */
    public void removeAllOutputs();

    /** Remove an input Listener
     * @param newListener the input listener to remove
     */
    public void removeInputSynapse(InputPatternListener newListener);

    /** Remove an output listener from the layer
     * @param newListener the output listener to remove
     */
    public void removeOutputSynapse(OutputPatternListener newListener);

    /** Sets the vector that contains all the input listeners. Can be useful to set the input synapses taken from another Layer
     * @param newAInputPatternListener The vector containing the list of input synapses
     */
    public void setAllInputs(java.util.Vector newAInputPatternListener);

    /** Sets the vector that contains all the output listeners. Can be useful to set the output synapses taken from another Layer
     * @param newAOutputPatternListener The vector containing the list of output synapses
     */
    public void setAllOutputs(java.util.Vector newAOutputPatternListener);

    /** Sets the matrix of biases
     * @param newBias The Matrix object containing the biases
     */
    public void setBias(Matrix newBias);

    /** Adds a new input synapse to the layer
     * @param newListener The new input synapse
     * @return true if the input synapse has been attached sucessfully
     */
    public boolean addInputSynapse(InputPatternListener newListener);

    /** Sets the name of the layer
     * @param newLayerName The name
     */
    public void setLayerName(java.lang.String newLayerName);

    /** Adds a new output synapse to the layer
     * @param newListener The new output synapse
     * @return true if the output synapse has been attached sucessfully
     */
    public boolean addOutputSynapse(OutputPatternListener newListener);

    /** Sets the dimension (# of neurons) of the Layer
     * @param newRows The number of the neurons contained in the Layer
     */
    public void setRows(int newRows);

    /** Starts the Layer
     */
    public void start();

    /** Sets the monitor object
     * @param newMonitor The Monitor to be set
     */
    public void setMonitor(Monitor newMonitor);

    /** Returns the monitor object
     * @return java.engine.Monitor
     */
    public Monitor getMonitor();

    /** Returns true if the layer is running
     * @return boolean
     */
    public boolean isRunning();

    /**
     * Validation checks for invalid parameter values, misconfiguration, etc.
     * All network components should include a check method that firstly calls its ancestor check method and
     * adds these to any check messages it produces. This allows check messages to be collected from all levels
     * of a component to be returned to the caller's check method. Using a TreeSet ensures that
     * duplicate messages are removed. Check messages should be produced using the generateValidationErrorMessage
     * method of the NetChecker class.
     *
     * @return validation errors.
     */
    public TreeSet check();
}