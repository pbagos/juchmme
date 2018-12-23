/*
 * NeuralValidationEvent.java
 *
 * Created on 28 aprile 2002, 16.07
 */

package org.joone.net;

/**
 *
 * @author  pmarrone
 */
public class NeuralValidationEvent extends java.util.EventObject {

    /** Creates a new instance of NeuralValidationEvent */
    public NeuralValidationEvent(NeuralNet event) {
        super(event);
    }

}
