/*
 * MultipleInputSynapse.java
 *
 * Created on 10 gennaio 2005, 21.23
 */

package org.joone.io;

import java.io.IOException;
import org.joone.engine.*;
/**
 * This class reads sequentially all the connected input synapses,
 * in order to be able to use multiple sources as inputs.
 * It reads all the rows of the first input synapse, then all the
 * rows of the second one, and so on. The Stop Pattern is injected
 * only after the last row of the last input synapse is read.
 *
 * When this class is used, the monitor.trainingPatterns must be set
 * to the sum of the rows of all the attached input synapses.
 *
 * @author  P.Marrone
 */
public class MultipleInputSynapse extends InputSwitchSynapse {
    private int currentInput = 0;
    private int currentPatt = 0;
    
    /** Creates a new instance of MultipleInputSynapse */
    public MultipleInputSynapse() {
        super();
    }
    
    public Pattern fwdGet() {
        super.setActiveSynapse((StreamInputSynapse)inputs.get(currentInput));
        Pattern patt = super.fwdGet();
        return elaboratePattern(patt);
    }
    
    public Pattern fwdGet(InputConnector conn) {
        StreamInputSynapse myInput = null;        
        InputConnector myInputConnector;
        Pattern myPattern;
        
        // myFirstRow, myCurrentRow and myLastRow will hold the relative
        // values of the input connector (which holds the absolute values ) 
        // w.r.t. the input synpase holding the current row
        int myFirstRow = conn.getFirstRow();
        int myCurrentRow = conn.getCurrentRow();
        int myLastRow = conn.getLastRow();
        int myNumberOfPatterns;
                
        // search for the synpase holding the current row of the connector
        for (int i=0; i < inputs.size(); ++i) {
            myInput = (StreamInputSynapse)inputs.elementAt(i);
            myNumberOfPatterns = getNumberOfPatterns(myInput);
            
            if(myFirstRow > myNumberOfPatterns) {
                // current input synpase does not contain the first (and therefore,
                // also not the current and last) row
                myFirstRow -= myNumberOfPatterns;
                myCurrentRow -= myNumberOfPatterns;
                if(myLastRow > 0) {
                    myLastRow -= myNumberOfPatterns;
                }
            } else {
                // the current input synpase contains the first row
                if(myCurrentRow > myNumberOfPatterns) {
                    // the current input synpase does not contain the current row
                    myFirstRow = 1;
                    myCurrentRow -= myNumberOfPatterns;
                    if(myLastRow > 0) {
                        myLastRow -= myNumberOfPatterns;
                    }
                } else {
                    // the current input synapse contains the current row
                    if(myLastRow > myNumberOfPatterns) {
                        // the current input synpase does not contain the last row
                        myLastRow = 0;
                    }
                    // Synapse that contains the current row located. Create a new input
                    // connector with the relative values for first, current and last row
                    // w.r.t. the located input synpase.
                    myInputConnector = new InputConnector();
                    myInputConnector.setFirstRow(myFirstRow);
                    myInputConnector.setCurrentRow(myCurrentRow);
                    myInputConnector.setLastRow(myLastRow);
                    setActiveSynapse(myInput);
                    myPattern = super.fwdGet(myInputConnector);
                    if(myPattern.getCount() != -1) {
                        myPattern.setCount(conn.getCurrentRow() - conn.getFirstRow());
                    }
                    return myPattern;
                }
            }
        }
        return null;
    }
       
    /**
     * Gets (finds out) how many patters an input synpase holds.
     *
     * @param anInputSynpase the number of patterns for this input synpase will be
     * returned.
     * @return the number of patterns the <code>anInputSynpase</code> holds.
     */
    protected int getNumberOfPatterns(StreamInputSynapse anInputSynpase) {
        if(anInputSynpase.getLastRow() != 0) {
            return anInputSynpase.getLastRow() - anInputSynpase.getFirstRow() + 1;
        } else if(anInputSynpase.getInputVector().size() != 0) {
            return anInputSynpase.getInputVector().size();
        } else {
            // this method is only called if an input connector is connector to this
            // multiple input synpase. An input connector requires that its underlying
            // input synpase operates in buffer mode. Therefore, to be sure, we turn on 
            // the buffermode if it is not on already...
            if(!anInputSynpase.isBuffered()) {
                anInputSynpase.setBuffered(true);
            }
            anInputSynpase.readAll();
            return anInputSynpase.getInputVector().size();
        }
    }
    
    private Pattern elaboratePattern(Pattern patt) {
        int count = patt.getCount();
        if (count == -1) {
            currentInput = currentPatt = 0;
        }
        else {
            patt.setCount(++currentPatt);
            if (getActiveSynapse().isEOF()) {
                // The current Input Synapse reached the EOF,
                // then swicth to the next
                ++currentInput;
                if (currentInput == inputs.size()) {
                    currentInput = currentPatt = 0; // Cycles if end of all the input synapses
                }
            }
        }
        return patt;
    }
    
    public void reset() {
        super.reset();
        currentInput = currentPatt = 0;
    }
    
    /**
     * @param newBuffered boolean
     */
    public void setBuffered(boolean newBuffered) {
        StreamInputSynapse myInput;
    
        for (int i=0; i < inputs.size(); ++i) {
            myInput = (StreamInputSynapse)inputs.elementAt(i);
            myInput.setBuffered(newBuffered);
        }
    }
    
    public void gotoLine(int aNumLine) throws IOException {
        StreamInputSynapse myInput = null;
        int myNumberOfPatterns;
        
        for (int i=0; i < inputs.size(); ++i) {
            myInput = (StreamInputSynapse)inputs.elementAt(i);
            myNumberOfPatterns = getNumberOfPatterns(myInput);
            
            if(aNumLine < myNumberOfPatterns + myInput.getFirstRow() - 1) {
                setActiveSynapse(myInput);
                myInput.gotoLine(aNumLine);
                return; // done
            } else {
                aNumLine -= myNumberOfPatterns;
            }
        }
    }
    
    public void setFirstRow(int newFirstRow) {
        // It doesn't make much sense to change the first row of this multiple input synpase
        // Control the first row either throught the underlying source or the 'above lying' 
        // input connector
        throw new UnsupportedOperationException("Control the first row through the underlying source or any " +
                "connected input connectors");
    }
    
    public void setLastRow(int newLastRow) {
        throw new UnsupportedOperationException("Control the last row through the underlying source or any " +
                "connected input connectors");
    }
}
