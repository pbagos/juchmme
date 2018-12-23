package org.joone.util;

import org.joone.log.*;

import org.joone.engine.Layer;
import org.joone.net.NeuralNet;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;


/**
 * A SnapshotRecorder serves to create and record snapshots of a neural network
 * in a file as a serie of serialized objects graphs. A new clone of the
 * network is generated for each snapshot and only the core part (between the
 * input-layer and the output-layer) is kept.
 *
 * @author Olivier Hussenet
 */
public class SnapshotRecorder extends SnapshotPlugin
  implements java.io.Serializable
{

    private static final long   serialVersionUID = -8151866025667526018L;
    private static final ILogger log = LoggerFactory.getLogger (SnapshotRecorder.class);

    /** The Joone snapshot format (NeuralNet serialized objects graph) */
    public static final String JOONE_FORMAT = "joone";

    /** The VisAD snapshot format (NeuralNetData serialized objects graph). Not yet implemented */
    public static final String VISAD_FORMAT = "visad";
    

    private transient OutputStream       os       = null;
    private transient ObjectOutputStream oos      = null;
    private String filename = "";
    private String format = JOONE_FORMAT;


    /**
     * Creates the output stream used to write snapshots.
     *
     * @param net the current neural network
     */
    protected void doStart () {
        try {
            os      = new FileOutputStream(filename);
            oos     = new ObjectOutputStream(os);
        } catch (IOException e) {
            log.warn ("IOException while opening OutputStream. Message is : " +
                      e.getMessage (), e);
        }
    }

    /**
     * Generates a snapshot of the current state of the network,
     *
     * @param net the current neural network
     */
    protected void doSnapshot ()
    {
            if (oos != null) {
                if (JOONE_FORMAT.equals (format))
                    jooneSnapshot (getNeuralNet());
                //else if (VISAD_FORMAT.equals (format))
                //    visadSnapshot (net);
                else
                    jooneSnapshot (getNeuralNet());
            }
    }
    
    /**
     * Generates a joone snapshot of the current state of the network,
     *
     * @param net the current neural network
     */
    private void jooneSnapshot (NeuralNet net)
    {
        // Write a clone of the core part of
        // the NeuralNet to the output-stream
        try {
            // Clone the neural-net
            NeuralNet clone = net.cloneNet ();

            // Detach inputs
            Layer layer = clone.getInputLayer ();
            if (layer != null)
                layer.removeAllInputs ();

            // Detach outputs
            layer = clone.getOutputLayer ();
            if (layer != null)
                layer.removeAllOutputs ();

            // Write the clone
            oos.writeObject (clone);
            
        } catch (IOException e) {
            log.warn ("IOException while writing to OutputStream. Message is : " +
                      e.getMessage (), e);
        }
    }

    /**
     * Flush the output buffer and close the file.
     *
     * @param net the current neural network
     */
    protected void doStop () {
        try {
            if (oos != null) {
                oos.flush ();
                os.close ();
            }
        } catch (IOException e) {
            log.warn ("IOException while closing OutputStream. Message is : " +
                      e.getMessage (), e);
        }
    }

    /**
     * Set the name of the file to which snapshots will be written.
     *
     * @param name the new snapshots file name.
     */
    public void setFilename (String name) {
        filename = name;
    }

    /**
     * Get the name of the file to which snapshots will be written.
     *
     * @return the current snapshots file name.
     */
    public String getFilename () {
        return filename;
    }

    /**
     * Get the format used for snapshots.
     *
     * @return the current snapshots format
     */
    public String getFormat () {
        return format;
    }

    /**
     * Set the format used for snapshots.
     * Legal values are: 'visad' or 'java'.
     *
     * @param format the new snapshots format
     */
    public void setFormat (String format) {
        this.format = format;
    }
    
    protected void manageStopError(org.joone.engine.Monitor mon, String msgErr) {
    }
    
}
