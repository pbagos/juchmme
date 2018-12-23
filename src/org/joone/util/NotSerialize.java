/*
 * NotSerialize.java
 *
 * Created on 29 marzo 2002, 16.03
 */

package org.joone.util;

/**
 * This interface is implemented by all the input/output Synapses that
 * must not be serialized when the NeuralNet is exported.
 * This feature is useful to avoid to serialize the GUI I/O components
 * (for instance the ChartOutputSynapse) along with the core NeuralNet object
 * @author  Paolo Marrone
 */
public interface NotSerialize {

}

