package org.joone.net;

import org.joone.net.NeuralNet;

import java.util.*;

public class NetChecker {

    /** NeuralNet linked to this instance. */
    final private NeuralNet netToCheck;

    /**
     * Constructor. Sets the NeuralNet for this instance.
     *
     * @param netToCheckArg the NeuralNEt to use.
     */
    public NetChecker(NeuralNet netToCheckArg) {
        netToCheck = netToCheckArg;
    }

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
    public TreeSet check() {
        return netToCheck.check();
    }

    /**
     * Method to determine whether there are validation errors in the net.
     *
     * @return true if errors exist.
     */
    public boolean hasErrors() {
        TreeSet checks = netToCheck.check();
        Iterator iter = checks.iterator();
        while (iter.hasNext()) {
            NetCheck netCheck = (NetCheck) iter.next();
            if (netCheck.isFatal()) {
                return true;
            }
        }

        return false;
    }
}
