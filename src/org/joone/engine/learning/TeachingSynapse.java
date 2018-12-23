package org.joone.engine.learning;

import java.util.Iterator;
import org.joone.engine.*;
import org.joone.io.*;
import org.joone.net.NetCheck;

import java.util.TreeSet;

public class TeachingSynapse implements ComparingElement {
    protected AbstractTeacherSynapse theTeacherSynapse;
    private LinearLayer theLinearLayer;
    private boolean enabled = true;
    private boolean outputFull = false;
    
    /** The teacher to use. If null a normal <code>TeacherSynapse</code> will be used.
     * The moment the teacher (<code>theTeacherSynapse</code>) is initialized is done
     * the first time <code>getTheTeacherSynapse()</code> method is called. At that
     * moment we are also able to set the monitor object.
     */
    private AbstractTeacherSynapse theTeacherToUse = null;
    
    /**
     * @label desired
     */
    private StreamInputSynapse desired;
    private Monitor monitor;
    private String name;
    
    private static final long serialVersionUID = -8893181016305737666L;
    
    public TeachingSynapse() {
    }
    
    /**
     * Creates a TeachingSynapse
     *
     * @param aTeacher the teacher to use. The default constructor
     * (<code>TeachingSynapse()</code>) uses a normal <code>TeacherSynapse</code>.
     */
    public TeachingSynapse(TeacherSynapse aTeacher) {
        theTeacherToUse = aTeacher;
    }
    
    public void fwdPut(Pattern pattern) {
        Monitor mon = getMonitor();
        // In interrogation mode, the Teacher must not be used
        if (!mon.isLearning() && !mon.isValidation())
            return;
        
        if (!mon.isSingleThreadMode()) 
            if (!getTheLinearLayer().isRunning())
                getTheLinearLayer().start();
        boolean firstTime = getTheTeacherSynapse().getSeenPatterns() == 0;
        getTheTeacherSynapse().fwdPut(pattern);
        if (mon.isSingleThreadMode()) {
            if (pattern.getCount() == -1)
                getTheLinearLayer().fwdRun(null);
            if ((pattern.getCount() == 1) && !firstTime)
                getTheLinearLayer().fwdRun(null);
        }
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
        return getTheTeacherSynapse().getInputDimension();
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
            theLinearLayer.setLayerName("(R)MSE Layer");
            if (monitor != null)
                theLinearLayer.setMonitor(monitor);
            theLinearLayer.setRows(1);
            theLinearLayer.addInputSynapse(getTheTeacherSynapse());
        }
        return theLinearLayer;
    }
    
    /**
     * @return neural.engine.TeacherSynapse
     * changed to public for Save As XML
     */
    public AbstractTeacherSynapse getTheTeacherSynapse() {
        if (theTeacherSynapse == null) {
            if(theTeacherToUse != null) {
                theTeacherSynapse = theTeacherToUse;
            } else {
                theTeacherSynapse = new TeacherSynapse();
                theTeacherSynapse.setName("Teacher Synapse");
            }
            if (monitor != null) {
                theTeacherSynapse.setMonitor(monitor);
            }
        }
        return theTeacherSynapse;
    }
    
    public Pattern revGet() {
        return getTheTeacherSynapse().revGet();
    }
    
    public boolean setDesired(StreamInputSynapse fn) {
        desired = fn;
        if (getTheTeacherSynapse().setDesired(fn)) {
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
        getTheTeacherSynapse().setInputDimension(newInputDimension);
    }
    
    /**
     * Inserire qui la descrizione del metodo.
     * Data di creazione: (06/04/00 23.33.24)
     * @param newMonitor neural.engine.Monitor
     */
    public void setMonitor(Monitor newMonitor) {
        monitor = newMonitor;
        if (theTeacherSynapse != null)
            theTeacherSynapse.setMonitor(newMonitor);
        if (theLinearLayer != null)
            theLinearLayer.setMonitor(newMonitor);
        if (desired != null)
            desired.setMonitor(newMonitor);
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
    public void setTheTeacherSynapse(TeacherSynapse newTheTeacherSynapse) {
        this.theTeacherSynapse = newTheTeacherSynapse;
    }
    
    /**
     * Needed for Save as XML
     */
    public void setTheLinearLayer(LinearLayer newTheLinearLayer) {
        this.theLinearLayer = newTheLinearLayer;
    }
    
    public void resetInput() {
        getTheTeacherSynapse().resetInput();
    }
    
    public TreeSet check() {
        // Prepare an empty set for check messages;
        TreeSet checks = new TreeSet();
        
        if (!isOutputFull())
            checks.add(new NetCheck(NetCheck.FATAL, "the Teacher seems to be not attached", this));
        
        if (theLinearLayer != null) {
            checks.addAll(setErrorSource(theLinearLayer.check()));
        }
        
        if (theTeacherSynapse != null) {
            checks.addAll(setErrorSource(theTeacherSynapse.check()));
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
        getTheTeacherSynapse().setEnabled(enabled);
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
        if (theTeacherSynapse != null) {
            theTeacherSynapse.init();
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