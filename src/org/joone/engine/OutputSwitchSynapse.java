package org.joone.engine;

import java.util.Vector;
import java.util.TreeSet;
import java.io.*;

/** This class acts as a switch that can connect its input to one of its connected
 * output synapses.
 * Many output synapses can be attached to the switch calling the method addOutputSynapse,
 * but only one is attached to the input; which one is connected is determined
 * by the call to the method setActiveOutput, passing to it the name of
 * the selected synapse.
 */
public class OutputSwitchSynapse implements OutputPatternListener, Serializable {
    
    protected Vector outputs;
    
    private String name;
    private Monitor mon;
    private int inputDimension;
    private boolean outputFull;
    private boolean enabled = true;
    
    private static final long serialVersionUID = 2906294213180089226L;
    
    private OutputPatternListener activeSynapse;
    
    private OutputPatternListener defaultSynapse;
    
    /** The constructor
     */
    public OutputSwitchSynapse() {
        outputs = new Vector();
        activeSynapse = defaultSynapse = null;
        mon = null;
        inputDimension = 0;
    }
    
    /** Resets the switch, connecting the default synapse to the output
     */
    public void reset() {
        setActiveSynapse(getDefaultSynapse());
    }
    
    /** Removes an output synapse from the switch
     * @param inputName The name of the synapse to remove
     */
    public boolean removeOutputSynapse(String outputName) {
        boolean retValue = false;
        OutputPatternListener opl = getOutputSynapse(outputName);
        if (opl != null) {
            outputs.removeElement(opl);
            opl.setOutputFull(false);
            if (outputs.size() > 0) {
                if (getActiveOutput().equalsIgnoreCase(outputName))
                    setActiveSynapse((OutputPatternListener) outputs.elementAt(0));
                if (getDefaultOutput().equalsIgnoreCase(outputName))
                    setDefaultSynapse((OutputPatternListener) outputs.elementAt(0));
            } else {
                setActiveOutput("");
                setDefaultOutput("");
            }
            retValue = true;
        }
        return retValue;
    }
    
    protected OutputPatternListener getOutputSynapse(String outputName) {
        OutputPatternListener out = null;
        int i;
        for (i = 0; i < outputs.size(); ++i) {
            out = (OutputPatternListener) outputs.elementAt(i);
            if (out.getName().equalsIgnoreCase(outputName))
                break;
        }
        if (i < outputs.size())
            return out;
        else
            return null;
    }
    
    /** Adds an output synapse to the switch
     * @param newOutput the new output synapse
     */
    public boolean addOutputSynapse(OutputPatternListener newOutput) {
        boolean retValue = false;
        if (!outputs.contains(newOutput))
            if (!newOutput.isOutputFull()) {
                outputs.addElement(newOutput);
                newOutput.setInputDimension(inputDimension);
                newOutput.setMonitor(mon);
                newOutput.setOutputFull(true);
                if (outputs.size() == 1) {
                    setDefaultOutput(newOutput.getName());
                    setActiveOutput(newOutput.getName());
                }
                retValue = true;
            }
        return retValue;
    }
    
    /** Returns the name of the actual connected output synapse
     * @return The name of the connected output synapse
     */
    public String getActiveOutput() {
        if (activeSynapse != null)
            return activeSynapse.getName();
        else
            return "";
    }
    
    /** Sets the output synapse connected to the input
     * @param newActiveOutput the name of the output synapse to connect
     */
    public void setActiveOutput(String newActiveOutput) {
        OutputPatternListener out = getOutputSynapse(newActiveOutput);
        this.activeSynapse = out;
    }
    
    /** Returns the name of the default output synapse that is connected
     * when the reset method is called
     * @return the name of the default synapse
     */
    public String getDefaultOutput() {
        if (defaultSynapse != null)
            return defaultSynapse.getName();
        else
            return "";
    }
    
    /** Sets the name of the default output synapse that is connected
     * when the reset method is called
     * @param newDefaultOutput the name of the default output synapse
     */
    public void setDefaultOutput(String newDefaultOutput) {
        OutputPatternListener out = getOutputSynapse(newDefaultOutput);
        defaultSynapse = out;
    }
    
    /**
     * Getter for property activeSynapse. @return Value of property activeSynapse.
     */
    protected OutputPatternListener getActiveSynapse() {
        return activeSynapse;
    }
    
    /**
     * Setter for property activeSynapse. @param activeSynapse New value of property activeSynapse.
     */
    protected void setActiveSynapse(OutputPatternListener activeSynapse) {
        this.activeSynapse = activeSynapse;
    }
    
    /**
     * Getter for property defaultSynapse. @return Value of property defaultSynapse.
     */
    protected OutputPatternListener getDefaultSynapse() {
        return defaultSynapse;
    }
    
    /**
     * Setter for property defaultSynapse. @param defaultSynapse New value of property defaultSynapse.
     */
    protected void setDefaultSynapse(OutputPatternListener defaultSynapse) {
        this.defaultSynapse = defaultSynapse;
    }
    
    /** Returns the name of the output synapse
     * @return String
     */
    public String getName() {
        return name;
    }
    
    /** Sets the name of the output synapse
     * @param name String
     */
    public void setName(String name) {
        this.name = name;
    }
    
    //------ Methods and parameters mapped on the active input synapse -------
    
    /** Sets the dimension of the output synapse
     * @param newOutputDimension int
     */
    public void setInputDimension(int newInputDimension) {
        this.inputDimension = newInputDimension;
        OutputPatternListener out;
        for (int i = 0; i < outputs.size(); ++i) {
            out = (OutputPatternListener) outputs.elementAt(i);
            out.setInputDimension(newInputDimension);
        }
    }
    
    /** Returns the dimension of the output synapse
     * @return int
     */
    public int getInputDimension() {
        return inputDimension;
    }
    
    /** Returns the monitor
     * @return org.joone.engine.Monitor
     */
    public Monitor getMonitor() {
        return mon;
    }
    
    /** Sets the Monitor object of the input synapse
     * @param newMonitor org.joone.engine.Monitor
     */
    public void setMonitor(Monitor newMonitor) {
        this.mon = newMonitor;
        OutputPatternListener out;
        for (int i = 0; i < outputs.size(); ++i) {
            out = (OutputPatternListener) outputs.elementAt(i);
            out.setMonitor(newMonitor);
        }
    }
    
    protected void backward(double[] pattern) {
        // Not used
    }
    
    protected void forward(double[] pattern) {
        // Not used
    }
    
    public Vector getAllOutputs() {
        return outputs;
    }
    
    public void resetOutput() {
        OutputPatternListener out;
        this.reset();
    }
    
    /** Method to put a pattern forward to the next layer
     * @param pattern neural.engine.Pattern
     */
    public void fwdPut(Pattern pattern) {
        if (isEnabled() && (activeSynapse != null))
            activeSynapse.fwdPut(pattern);
    }
    
    /** Returns the error pattern coming from the next layer during the training phase
     * @return neural.engine.Pattern
     */
    public Pattern revGet() {
        if (isEnabled() && (activeSynapse != null))
            return activeSynapse.revGet();
        else
            return null;
    }
    
    /**
     * Base for check messages.
     * Subclasses should call this method from thier own check method.
     *
     * @see OutputPaternListener
     * @return validation errors.
     */
    public TreeSet check() {
        
        // Prepare an empty set for check messages;
        TreeSet checks = new TreeSet();
        
        // Return check messages
        return checks;
    }
    
    /** Getter for property outputFull.
     * @return Value of property outputFull.
     *
     */
    public boolean isOutputFull() {
        return outputFull;
    }
    
    /** Setter for property outputFull.
     * @param outputFull New value of property outputFull.
     *
     */
    public void setOutputFull(boolean outputFull) {
        this.outputFull = outputFull;
        OutputPatternListener out;
        for (int i = 0; i < outputs.size(); ++i) {
            out = (OutputPatternListener) outputs.elementAt(i);
            out.setOutputFull(outputFull);
        }
    }
    
    /** Getter for property enabled.
     * @return Value of property enabled.
     *
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /** Setter for property enabled.
     * @param enabled New value of property enabled.
     *
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
//    /**
//     * @see org.joone.engine.Learnable#getLearner()
//     */
//    public Learner getLearner() {
//        return null;
//    }
//    
//    /** Calls the initLearner() on all the attached output components
//     * @see org.joone.engine.Learnable#initLearner()
//     */
//    public void initLearner() {
//        for (int i = 0; i < outputs.size(); ++i) {
//            if (outputs.elementAt(i) instanceof Learnable)
//                ((Learnable)outputs.elementAt(i)).initLearner();
//        }
//    }
    
    public void init() {
        for (int i = 0; i < outputs.size(); ++i) {
            if (outputs.elementAt(i) instanceof NeuralElement)
                ((NeuralElement)outputs.elementAt(i)).init();
        }
    }
    
    
}
