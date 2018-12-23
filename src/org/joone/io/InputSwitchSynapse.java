package org.joone.io;

import java.util.*;
import java.io.*;
import org.joone.util.*;
import org.joone.engine.Monitor;
import org.joone.engine.Pattern;

/** This class acts as a switch that can connect its output to one of its connected
 * input synapses.
 * Many input synapses can be attached to the switch calling the method addInputSynapse,
 * but only one is attached to the output; which one is comnnected is determined
 * by the call to the method setActiveInput, passing to it the name of
 * the selected synapse.
 */
public class InputSwitchSynapse extends StreamInputSynapse implements Serializable {
    
    protected Vector inputs;
    
    private StreamInputSynapse activeSynapse;
    private StreamInputSynapse defaultSynapse;
    
    private String name;
    private Monitor mon;
    private int outputDimension;
    
    private static final long serialVersionUID = 9025876263540714672L;
    
    /** The constructor
     */
    public InputSwitchSynapse() {
        initSwitch();
        mon = null;
        outputDimension = 0;
    }
    
    protected void initSwitch() {
        inputs = new Vector();
        activeSynapse = defaultSynapse = null;
        
    }
    
    public void init() {
        super.init();
        for (int i=0; i < inputs.size(); ++i) {
            StreamInputSynapse inp = (StreamInputSynapse)inputs.elementAt(i);
            inp.init();
        }
        
    }
    
    /** Resets the switch, connecting the default synapse to the output
     */
    public void resetSwitch()  {
        setActiveSynapse(getDefaultSynapse());
    }
    
    public void reset()  {
        for (int i=0; i < inputs.size(); ++i) {
            StreamInputSynapse inp = (StreamInputSynapse)inputs.elementAt(i);
            inp.reset();
        }
    }
    
    /** Removes an input synapse from the switch
     * @param inputName The name of the synapse to remove
     * @return false if the synapse cannot be found
     */
    public boolean removeInputSynapse(String inputName) {
        boolean retValue = false;
        StreamInputSynapse sis = getInputSynapse(inputName);
        if (sis != null) {
            inputs.removeElement(sis);
            sis.setInputFull(false);
            if (inputs.size() > 0) {
                if (getActiveInput().equalsIgnoreCase(inputName))
                    setActiveSynapse((StreamInputSynapse)inputs.elementAt(0));
                if (getDefaultInput().equalsIgnoreCase(inputName))
                    setDefaultSynapse((StreamInputSynapse)inputs.elementAt(0));
            }
            else {
                setActiveInput("");
                setDefaultInput("");
            }
            retValue = true;
        }
        return retValue;
    }
    
    protected StreamInputSynapse getInputSynapse(String inputName) {
        StreamInputSynapse inp = null;
        for (int i=0; i < inputs.size(); ++i) {
            inp = (StreamInputSynapse)inputs.elementAt(i);
            if (inp.getName().equalsIgnoreCase(inputName)) {
                return inp;
            }
        }
        return null;
    }
    
    /** Adds an input synapse to the switch
     * @param newInput the new input synapse
     */
    public boolean addInputSynapse(StreamInputSynapse newInput) {
        boolean retValue = false;
        if (!inputs.contains(newInput)) {
            if (!newInput.isInputFull()) {
                inputs.addElement(newInput);
                newInput.setOutputDimension(outputDimension);
                newInput.setMonitor(mon);
                newInput.setStepCounter(super.isStepCounter());
                newInput.setInputFull(true);
                if (inputs.size() == 1) {
                    setDefaultInput(newInput.getName());
                    setActiveInput(newInput.getName());
                }
                retValue = true;
            }
        }
        return retValue;
    }
    
    /** Returns the name of the actual connected input synapse
     * @return The name of the connected input synapse
     */
    public String getActiveInput(){
        if (activeSynapse != null)
            return activeSynapse.getName();
        else
            return "";
    }
    
    /** Sets the input synapse connected to the output
     * @param newActiveInput the name of the input synapse to connect
     */
    public void setActiveInput(String newActiveInput) {
        StreamInputSynapse inp = getInputSynapse(newActiveInput);
        this.activeSynapse = inp;
    }
    
    /** Returns the name of the default input synapse that is connected
     * when the reset method is called
     * @return the name of the default synapse
     */
    public String getDefaultInput() {
        if (defaultSynapse != null)
            return defaultSynapse.getName();
        else
            return "";
    }
    
    /** Sets the name of the default input synapse that is connected
     * when the reset method is called
     * @param newDefaultInput the name of the default input synapse
     */
    public void setDefaultInput(String newDefaultInput) {
        StreamInputSynapse inp = getInputSynapse(newDefaultInput);
        defaultSynapse = inp;
    }
    /** Getter for property activeSynapse.
     * @return Value of property activeSynapse.
     */
    protected StreamInputSynapse getActiveSynapse() {
        return activeSynapse;
    }
    
    /** Setter for property activeSynapse.
     * @param activeSynapse New value of property activeSynapse.
     */
    protected void setActiveSynapse(StreamInputSynapse activeSynapse) {
        this.activeSynapse = activeSynapse;
    }
    
    /** Getter for property defaultSynapse.
     * @return Value of property defaultSynapse.
     */
    protected StreamInputSynapse getDefaultSynapse() {
        return defaultSynapse;
    }
    
    /** Setter for property defaultSynapse.
     * @param defaultSynapse New value of property defaultSynapse.
     */
    protected void setDefaultSynapse(StreamInputSynapse defaultSynapse) {
        this.defaultSynapse = defaultSynapse;
    }
    
    
    /** Returns the name of the input synapse
     * @return <{String}>
     */
    public String getName() {
        return name;
    }
    
    /** Sets the name of the input synapse
     * @param name String
     */
    public void setName(String name) {
        this.name = name;
    }
    
    //------ Methods and parameters mapped on the active input synapse -------
    
    /** Sets the dimension of the input synapse
     * @param newOutputDimension int
     */
    public void setOutputDimension(int newOutputDimension) {
        this.outputDimension = newOutputDimension;
        for (int i=0; i < inputs.size(); ++i) {
            StreamInputSynapse inp = (StreamInputSynapse)inputs.elementAt(i);
            inp.setOutputDimension(newOutputDimension);
        }
    }
    
    /** Method to put an error pattern backward to the previous layer
     * @param pattern neural.engine.Pattern
     */
    public void revPut(Pattern pattern) {
        if (activeSynapse != null)
            activeSynapse.revPut(pattern);
    }
    
    /** Returns the pattern coming from the previous layer during the recall phase
     * @return neural.engine.Pattern
     */
    public Pattern fwdGet() {
        if (activeSynapse != null)
            return activeSynapse.fwdGet();
        else
            return null;
    }

     /** Returns the pattern coming from the previous layer during the recall phase.
      *  This method is called by the InputConnector class
      * @return neural.engine.Pattern
      */
    public Pattern fwdGet(InputConnector conn) {
        if (activeSynapse != null)
            return activeSynapse.fwdGet(conn);
        else
            return null;
    }

    /** Returns the dimension of the input synapse
     * @return int
     */
    public int getOutputDimension() {
        return outputDimension;
    }
    
    /** Returns the monitor
     * @return <{Monitor}>
     */
    public Monitor getMonitor() {
        return mon;
    }
    
    /** Sets the Monitor object of the input synapse
     * @param newMonitor org.joone.engine.Monitor
     */
    public void setMonitor(Monitor newMonitor) {
        this.mon = newMonitor;
        for (int i=0; i < inputs.size(); ++i) {
            StreamInputSynapse inp = (StreamInputSynapse)inputs.elementAt(i);
            inp.setMonitor(newMonitor);
        }
    }
    
    protected void backward(double[] pattern) {
        // Not used
    }
    protected void forward(double[] pattern) {
        // Not used
    }
    
    public Vector getAllInputs() {
        return inputs;
    }
    
    public void setAllInputs(Vector inps) {
        inputs = inps;
        if (inputs != null) {
            for (int i=0; i < inputs.size(); ++i) {
                StreamInputSynapse inp = (StreamInputSynapse)inputs.elementAt(i);
                inp.setInputFull(true);
            }
        }
    }
    
    public void resetInput() {
        for (int i=0; i < inputs.size(); ++i) {
            StreamInputSynapse inp = (StreamInputSynapse)inputs.elementAt(i);
            inp.resetInput();
        }
        this.reset();
    }
    
    protected void initInputStream() {
        // Not used
    }
    
    public void setStepCounter(boolean newStepCounter) {
        super.setStepCounter(newStepCounter);
        for (int i=0; i < inputs.size(); ++i) {
            StreamInputSynapse inp = (StreamInputSynapse)inputs.elementAt(i);
            inp.setStepCounter(newStepCounter);
        }
    }
    
    /**
     * Point to the indicated line into the input stream
     */
    public void gotoLine(int numLine) throws IOException {
        if (activeSynapse != null)
            activeSynapse.gotoLine(numLine);
    }
    
    public void dataChanged(PlugInEvent data) {
        // Not used
    }
    
    public void setDecimalPoint(char dp) {
        if (activeSynapse != null)
            activeSynapse.setDecimalPoint(dp);
    }
    
    /**
     * Returns if reached the EOF
     * (10/04/00 23.16.20)
     * @return boolean
     */
    public boolean isEOF() {
        if (activeSynapse != null)
            return activeSynapse.isEOF();
        else
            return false;
    }
    
    public void readAll() {
        if (activeSynapse != null)
            activeSynapse.readAll();
    }
    
    /**
     * @param newBuffered boolean
     */
    public void setBuffered(boolean newBuffered) {
        if (activeSynapse != null)
            activeSynapse.setBuffered(newBuffered);
    }
    
    /**
     * Returns if the input synapse is buffered
     * (10/04/00 23.11.30)
     * @return boolean
     */
    public boolean isBuffered() {
        if (activeSynapse != null)
            return activeSynapse.isBuffered();
        else
            return false;
    }
    
    /**
     * @return neural.engine.ConverterPlugIn
     */
    public ConverterPlugIn getPlugIn() {
        if (activeSynapse != null)
            return activeSynapse.getPlugIn();
        else
            return null;
    }
    
    /**
     * Returns if this input layer is an active counter of the steps.
     * Warning: in a neural net there can be only one StepCounter element!
     * (10/04/00 23.23.26)
     * @return boolean
     */
    public boolean isStepCounter() {
        if (activeSynapse != null)
            return activeSynapse.isStepCounter();
        else
            return false;
    }
    
    public void gotoFirstLine() throws IOException {
        if (activeSynapse != null)
            activeSynapse.gotoFirstLine();
    }
    
    public void removeAllInputs() {
        for (int i=0; i < inputs.size(); ++i) {
            StreamInputSynapse inp = (StreamInputSynapse)inputs.elementAt(i);
            inp.setInputFull(false);
        }
        initSwitch();
    }
    
    /**
     * Check that parameters are set correctly.
     *
     * @see Synapse
     * @return validation errors.
     */
    public TreeSet check() {
        // Get the parent's cehck messages.
        TreeSet checks = new TreeSet();
        StreamInputSynapse inp;
        for (int i=0; i < inputs.size(); ++i) {
            inp = (StreamInputSynapse)inputs.elementAt(i);
            checks.addAll(inp.check());
        }
        return checks;
    }
    
    
    /** @return int
     *
     */
    public int getFirstRow() {
        if (activeSynapse != null)
            return activeSynapse.getFirstRow();
        else
            return 0;
    }
    
    /** @return int
     *
     */
    public int getLastRow() {
        if (activeSynapse != null)
            return activeSynapse.getLastRow();
        else
            return 0;
    }
        
    public Collection getInspections() {
        if (activeSynapse != null)
            return activeSynapse.Inspections();
        else
            return null;
    }
    
    public int numColumns() {
        if (activeSynapse != null)
            return activeSynapse.numColumns();
        else
            return 0;
    }
    
    public String getAdvancedColumnSelector() {
        if (activeSynapse != null)
            return activeSynapse.getAdvancedColumnSelector();
        else
            return "";
    }
    
    
}
