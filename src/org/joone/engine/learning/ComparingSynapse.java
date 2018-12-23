package org.joone.engine.learning;

import java.util.Iterator;
import org.joone.engine.*;
import org.joone.io.*;

import java.util.TreeSet;
import org.joone.net.NetCheck;

public class ComparingSynapse implements ComparingElement {
    private ComparisonSynapse theComparisonSynapse;
    private LinearLayer theLinearLayer;
    private boolean enabled = true;
    private boolean outputFull = false;
    /**
     * @label desired
     */
    private StreamInputSynapse desired;
    private Monitor monitor;
    private String name;
    
    private static final long serialVersionUID = -8893181016305737666L;
    
    public ComparingSynapse() {
    }
    
    public void fwdPut(Pattern pattern) {
        if (!getTheLinearLayer().isRunning())
            getTheLinearLayer().start();
        getTheComparisonSynapse().fwdPut(pattern);
    }
    
    /**
     * Insert the method's description here.
     * Creation date: (03/08/2000 22.50.55)
     * @return java.lang.String
     */
    public StreamInputSynapse getDesired() {
        return desired;
    }
    
    /**
     * getInputDimension method comment.
     */
    public int getInputDimension() {
        return getTheComparisonSynapse().getInputDimension();
    }
    
    /**
     * Insert the method's description here.
     * Creation date: (03/08/2000 22.54.48)
     * @return neural.engine.Monitor
     */
    public Monitor getMonitor() {
        return monitor;
    }
    
    /**
     * @return neural.engine.LinearLayer
     * changed to public for Save As XML
     */
    public LinearLayer getTheLinearLayer() {
        if (theLinearLayer == null) {
            theLinearLayer = new LinearLayer();
            theLinearLayer.setLayerName("Comparing LinearLayer");
            if (monitor != null)
                theLinearLayer.setMonitor(monitor);
            theLinearLayer.setRows(1);
            theLinearLayer.addInputSynapse(getTheComparisonSynapse());
        }
        return theLinearLayer;
    }
    
    /**
     * @return neural.engine.TeacherSynapse
     * changed to public for Save As XML
     */
    public ComparisonSynapse getTheComparisonSynapse() {
        if (theComparisonSynapse == null) {
            theComparisonSynapse = new ComparisonSynapse();
            theComparisonSynapse.setName("Teacher Synapse");
            if (monitor != null)
                theComparisonSynapse.setMonitor(monitor);
        }
        return theComparisonSynapse;
    }
    
    public Pattern revGet() {
        return null;
    }
    
    public boolean setDesired(StreamInputSynapse fn) {
        desired = fn;
        if (getTheComparisonSynapse().setDesired(fn)) {
            if ((monitor != null) && (desired != null))
                desired.setMonitor(monitor);
            return true;
        } else
            return false;
    }
    
    
    public boolean addResultSynapse(OutputPatternListener listener) {
        if (listener != null)
            return getTheLinearLayer().addOutputSynapse(listener);
        else
            return false;
    }
    
    public void removeResultSynapse(OutputPatternListener listener) {
        if (listener != null)
            getTheLinearLayer().removeOutputSynapse(listener);
    }
    
    /**
     * setInputDimension method.
     */
    public void setInputDimension(int newInputDimension) {
        getTheComparisonSynapse().setInputDimension(newInputDimension);
        getTheLinearLayer().setRows(newInputDimension * 2);
    }
    
    /**
     * Data di creazione: (06/04/00 23.33.24)
     * @param newMonitor neural.engine.Monitor
     */
    public void setMonitor(Monitor newMonitor) {
        monitor = newMonitor;
        if (monitor != null) {
            getTheComparisonSynapse().setMonitor(newMonitor);
            getTheLinearLayer().setMonitor(newMonitor);
            if (desired != null)
                desired.setMonitor(newMonitor);
        }
    }
        
    public void stop() {
        getTheLinearLayer().stop();
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(java.lang.String newName) {
        name = newName;
    }
    
    /**
     * Recall phase
     * @param pattern double[] - pattern di input sul quale applicare la funzione di trasferimento
     */
    protected void forward(double[] pattern) {
        /* Not used */
    }
    
    /**
     * Insert the method's description here.
     * Creation date: (23/09/2000 12.52.58)
     */
    protected void setArrays(int rows, int cols) {
        /* Not used */
    }
    
    /**
     * @param int rows - righe
     * @param int cols - colonne
     */
    protected void setDimensions(int rows, int cols) {
        /* Not used */
    }
    
    /**
     * Training phase.
     * @param pattern double[] - input pattern
     */
    protected void backward(double[] pattern) {
        /* Not used */
    }
    
    /**
     * Needed for Save as XML
     */
    public void setTheComparisonSynapse(ComparisonSynapse theComparisonSynapse) {
        this.theComparisonSynapse = theComparisonSynapse;
    }
    
    /**
     * Needed for Save as XML
     */
    public void setTheLinearLayer(LinearLayer newTheLinearLayer) {
        this.theLinearLayer = newTheLinearLayer;
    }
    
    public void resetInput() {
        getTheComparisonSynapse().resetInput();
    }
    
    public TreeSet check() {
        // Prepare an empty set for check messages;
        TreeSet checks = new TreeSet();
        
        if (theLinearLayer != null) {
            checks.addAll(setErrorSource(theLinearLayer.check()));
        }
        
        if (theComparisonSynapse != null) {
            checks.addAll(setErrorSource(theComparisonSynapse.check()));
        }
        
        return checks;
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
    }
    
    public void init() {
        if (theComparisonSynapse != null) {
            theComparisonSynapse.init();
        }
    }
    
    // Changes the source of the errors generated from internal components
    private TreeSet setErrorSource(TreeSet errors) {
        if (!errors.isEmpty()) {
            Iterator iter = errors.iterator();
            while (iter.hasNext()) {
                NetCheck nc = (NetCheck)iter.next();
                if (!(nc.getSource() instanceof Monitor) &&
                        !(nc.getSource() instanceof StreamInputSynapse) &&
                        !(nc.getSource() instanceof StreamOutputSynapse))
                    nc.setSource(this);
            }
        }
        return errors;
    }
    
}