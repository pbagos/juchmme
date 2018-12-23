package org.joone.util;

import org.joone.engine.Monitor;
import org.joone.net.NeuralNet;


/**
 * A SnapshotPlugin manages the generation of snapshots of the network's state
 * at regular intervals during its activity. The effective snapshot generation
 * is deferred to subclasses to allow for misc. storage formats and output
 * destinations.
 *
 * @author Olivier Hussenet
 */
public abstract class SnapshotPlugin extends MonitorPlugin
  implements java.io.Serializable
{

    private static final long serialVersionUID = -4796324031568433167L;


    /**
     * Creates a new SnapshotPlugin object.
     */
    protected SnapshotPlugin () {
        super();
        // Initialize default rate
        setRate (100);
    }


    /**
     * Start a new activity session: calls the doStart method to inform
     * subclasses, then generates a snapshot of the initial state of the
     * network.
     *
     * @param mon the monitor
     */
    protected final void manageStart (Monitor mon) {
        // Check for activation (rate > 0)
        if (getRate () == 0)
            return;

        // Start snapshots generation
        doStart ();
        // Generates a snapshot of the initial state of the network
        doSnapshot ();
    }

    /**
     * Allows subclasses to define specific start processing.
     *
     * @param net the current neural network.
     */
    protected abstract void doStart ();

    /**
     * Process one cycle of activity: calls the doSnapshot method to allow for
     * specific snapshot generation by subclasses.
     *
     * @param mon the monitor
     */
    protected final void manageCycle (Monitor mon) {
        doSnapshot ();
    }

    /**
     * Allows subclasses to define specific snapshot generation.
     *
     * @param net the current neural network.
     */
    protected abstract void doSnapshot ();

    /**
     * Stop an activity session: take a snapshot of the final state of the
     * network, then calls the doStop method to inform subclasses
     *
     * @param mon the monitor
     */
    protected final void manageStop (Monitor mon) {
        // Check for activation (rate > 0)
        if (getRate () == 0)
            return;

        // Generate last snapshot, if necessary
        int current = mon.getTotCicles () - mon.getCurrentCicle ();
        if ((current % getRate ()) != 0)
            doSnapshot ();

        // Stop snapshots recording
        doStop ();
    }

    /**
     * Allows subclasses to define specific stop processing.
     *
     * @param net the current neural network.
     */
    protected abstract void doStop ();

    /**
     * Global error is stored along with the monitor, so this method does
     * nothing.
     *
     * @param mon the monitor
     */
    protected final void manageError (Monitor mon) {
    }
}
