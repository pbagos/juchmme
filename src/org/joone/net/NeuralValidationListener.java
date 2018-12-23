/*
 * NeuralValidatorListener.java
 *
 * Created on 28 aprile 2002, 15.59
 */

package org.joone.net;

import java.util.EventListener;
/**
 *
 * @author  pmarrone
 */
public interface NeuralValidationListener extends EventListener {
    public void netValidated(NeuralValidationEvent event);
}

