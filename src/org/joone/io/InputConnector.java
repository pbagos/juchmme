/*
 * InputConnector.java
 *
 * Created on September 15, 2004, 2:34 PM
 */

package org.joone.io;

import java.util.*;
import org.joone.engine.*;
import org.joone.log.*;
import org.joone.util.*;
import org.joone.net.NetCheck;

/**
 *
 * @author  drmarpao
 */
public class InputConnector extends StreamInputSynapse implements PlugInListener {
    
    /** Logger for this class. */
    private static final ILogger log = LoggerFactory.getLogger(InputConnector.class);
    
    private static final long serialVersionUID = -3316265583123866079L;
    
    /** The input stream this connector is connected to. */
    private StreamInputSynapse inputSynapse;
    
    /** Creates a new instance of InputConnector */
    public InputConnector() {
        setBuffered(false);
    }
    
    protected void initInputStream() throws org.joone.exception.JooneRuntimeException {
        currentRow = getFirstRow();
        EOF = false;
    }
    
    /**
     * Attach a <code>StreamInputSynapse</code> to this connector.
     *
     * @param input the <code>StreamInputSynapse</code> to attach. <code>null</code>
     * to free this connector
     */
    public boolean setInputSynapse(StreamInputSynapse input) {
        if (input != null) {
            input.setMonitor(getMonitor());
            input.setBuffered(true);
            input.setStepCounter(false);
            input.setInputFull(true);
            input.addPlugInListener(this);
            setOutputFull(true);
        } else {
            setOutputFull(false);
            if (inputSynapse != null) {
                inputSynapse.removePlugInListener(this);
                inputSynapse.setInputFull(false);
            }
        }
        inputSynapse = input;
        return true;
    }
    
    protected Pattern getStream() {
        if (inputSynapse == null)
            return null;
        Pattern patt = inputSynapse.fwdGet(this);
        if (patt != null) {
            if (cols == null)
                setColList();
            if (cols == null)
                return null; // In this case we cannot continue
            
            inps = new double[cols.length];
            
            for (int x = 0; x < cols.length; ++x) {
                inps[x] = patt.getArray()[cols[x] - 1];
            }
            // Calculates the new current line
            // and check the EOF
            ++currentRow;
            if (currentRow - getFirstRow() > (getMonitor().getNumOfPatterns() - 1))
                EOF = true;
            if ((getLastRow() > 0) && (getCurrentRow() > getLastRow()))
                EOF = true;
            forward(inps);
            Pattern newPattern = new Pattern(outs);
            newPattern.setCount(getCurrentRow() - getFirstRow());
            return newPattern;
        } else
            return null;
    }
    
    public void setMonitor(Monitor newMonitor) {
        super.setMonitor(newMonitor);
        if (inputSynapse != null)
            inputSynapse.setMonitor(newMonitor);
    }
    
    public void dataChanged(PlugInEvent anEvent) {
        // data has changed so by removing all elements (changed) data will be
        // retrieved again from the underlying stream input
        if (isBuffered()) {
            getInputVector().removeAllElements();
        }
    }
    
    public java.util.TreeSet check() {
        TreeSet checks = super.check();
        // Check that the first row is greater than 0.
        if (inputSynapse == null) {
            checks.add(new NetCheck(NetCheck.FATAL, "Input Synapse not set", this));
        } else if (!inputSynapse.isBuffered()) {
            checks.add(new NetCheck(NetCheck.FATAL, "The Input Synapse must be buffered", this));
        }
        return checks;
    }
    
    public void resetInput() {
        if (inputSynapse != null) {
            inputSynapse.resetInput();
        }
    }
    
    /**
     * Sets the current row. 
     * Note : this method is needed by the MultipleInputSynpase. I wonder if it is
     * needed for any other purpose... So you probably don't need to use this method.
     *
     * @param aRow the new value for the current row.
     */
    protected void setCurrentRow(int aRow) {
        currentRow = aRow;
    }
}
