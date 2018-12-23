package org.joone.io;

import java.util.*;
import java.io.*;
import org.joone.log.*;
import org.joone.engine.*;
import org.joone.util.*;
import org.joone.net.NetCheck;
import org.joone.inspection.Inspectable;
import org.joone.inspection.implementations.InputsInspection;
import org.joone.exception.JooneRuntimeException;

public abstract class StreamInputSynapse extends Synapse implements InputSynapse, Inspectable {
    /**
     * Logger
     * */
    private static final ILogger log = LoggerFactory.getLogger(StreamInputSynapse.class);
    
    //protected Vector column;
    //private boolean[] colList;
    
    private int firstRow = 1;
    private int lastRow = 0;
    private int firstCol = 0;
    private int lastCol = 0;
    private String advColumnsSel = "";
    
    /** Flag indicating if the stream buffers the data of not. */
    private boolean buffered = true;
    
    private char decimalPoint = '.';
    private boolean StepCounter = true;
    
    protected transient int[] cols;
    protected transient Vector InputVector;
    protected transient int currentRow = 0;
    protected transient PatternTokenizer tokens;
    protected transient boolean EOF = false;
    
    private ConverterPlugIn plugIn;
    private int maxBufSize = 0;
    private static final long serialVersionUID = -3316265583083866079L;
    private transient int startFrom = 0;
    
    /** List of plug-in listeners (often input connectors). */
    List plugInListeners = new ArrayList();
    
    public StreamInputSynapse() {
        super();
    }
    
    protected void backward(double[] pattern) {
        // Not used.
    }
    
    protected void forward(double[] pattern) {
        outs = pattern;
    }
    
    public synchronized Pattern fwdGet() {
        if (!isEnabled())
            return null;
        if ((EOF) || (outs == null)) {
            try {
                if ( (EOF) && (getMonitor() != null )) {
                    if (isStepCounter())
                        getMonitor().resetCycle();
                }
                gotoFirstLine();
                outs = new double[1]; // Remember that it already has been here
            } catch (Exception ioe) {
                log.error("Exception while executing the \"fwdGet\". Message is : " + ioe.getMessage());
                if ( getMonitor() != null )
                    new NetErrorManager(getMonitor(),"Exception while executing the \"fwdGet\" method. Message is : " + ioe.getMessage());
                return zeroPattern();
            }
        }
        
        if (currentRow - firstRow > (getMonitor().getNumOfPatterns() - 1)) {
            try {
                gotoFirstLine();
            } catch (Exception ioe) {
                log.error("Exception while executing the \"fwdGet\". Message is : " + ioe.getMessage());
                if ( getMonitor() != null )
                    new NetErrorManager(getMonitor(),"Exception while attempting to access the first line. Message is : " + ioe.getMessage());
                return zeroPattern();
            }
        }
        
        if (isStepCounter()) {
            // Checks if the next step can be elaborated
            boolean cont = getMonitor().nextStep();
            if (!cont) {
                /* If not, then creates and returns a zero pattern,
                 * a pattern having count = -1
                 * this does mean that the net must stop */
                reset();
                return zeroPattern();
            }
        }
        
        // Reads the next input pattern
        if (isBuffered()) {
            int actualRow = currentRow - firstRow + (startFrom==0 ? 0 : startFrom-1);
            if (getInputVector().size() == 0)
                readAll();
            else {
                if ((currentRow == firstRow) && (getPlugIn() != null) && !skipNewCycle) {
                    getPlugIn().setInputVector(getInputVector());
                    if (getPlugIn().newCycle()) {
                        fireDataChanged();
                    }
                }
            }
            if (actualRow < InputVector.size()) {
                m_pattern = (Pattern) InputVector.elementAt(actualRow);
            }
            
            // Checks if EOF
            if ((lastRow > 0) && (currentRow >= lastRow))
                EOF = true;
            if ((actualRow + 1) >= InputVector.size())
                EOF = true;
            ++currentRow;
        } else {
            try {
                m_pattern = getStream();
            } catch (Exception ioe) {
                String error = "Exception in "+getName()+". Message is : ";
                log.error(error + ioe.getMessage());
                ioe.printStackTrace();
                if ( getMonitor() != null )
                    new NetErrorManager(getMonitor(),error + ioe.getMessage());
                return zeroPattern();
            }
        }
        m_pattern.setCount(currentRow - firstRow);
        return m_pattern;
    }
    
    /**
     * Returns the decimal point accepted
     * (19/04/00 0.23.56)
     * @return char
     */
    public char getDecimalPoint() {
        return decimalPoint;
    }
    
    /**
     * @return int
     */
    public int getFirstRow() {
        return firstRow;
    }
    
    /**
     * @return java.util.Vector
     */
    protected java.util.Vector getInputVector() {
        if (InputVector == null)
            InputVector = new Vector();
        return InputVector;
    }
    
    /**
     * @return int
     */
    public int getLastRow() {
        return lastRow;
    }
    
    protected Pattern getStream() throws java.io.IOException {
        int x = 0;
        
        EOF = (!tokens.nextLine());
        if (!EOF) {
            if (cols == null) {
                setColList();
            }
            
            if (cols == null) {
                return null; // In this case we cannot continue
            }
            
            // even though the output dimension might differ, we create a input pattern
            // of the size equaling the number of input columns. Plug-ins might adjust
            // the dimension and otherwise the layer will adjust its dimenstion. This is
            // beter than creating an input pattern of a size equal to the output dimenstion
            // in case the number of columns is smaller and fill the pattern with 0, for
            // the columns where no imput data is available for.
            inps = new double[cols.length];
            
            for (x = 0; x < cols.length; ++x)
                inps[x] = tokens.getTokenAt(cols[x] - 1);
            // Calculates the new current line
            // and check the EOF
            ++currentRow;
            if ((lastRow > 0) && (currentRow > lastRow))
                EOF = true;
            forward(inps);
            m_pattern = new Pattern(outs);
            m_pattern.setCount(currentRow - firstRow);
            return m_pattern;
        } else
            return null;
    }
    
    public void gotoFirstLine() throws java.io.IOException {
        this.gotoLine(firstRow);
    }
    
    /**
     * Point to the indicated line into the input stream
     */
    public void gotoLine(int numLine) throws java.io.IOException {
        EOF = false;
        if (!isBuffered() || (getInputVector().size() == 0)) {
            PatternTokenizer tk = getTokens();
            if (tk != null) {
                if (isBuffered())
                    tk.resetInput();
                else // patched by Razvan Surdulescu
                    initInputStream();
                EOF = false;
                currentRow = 1;
                while ((currentRow < numLine) && (!EOF)) {
                    if (tokens.nextLine())
                        ++currentRow;
                    else
                        EOF = true;
                }
            }
        }
        currentRow = numLine;
        if ((lastRow > 0) && (currentRow >= lastRow))
            EOF = true;
        else
            EOF = false;
    }
    
    /**
     * Checks if the input synapse is buffered or not
     *
     * @return <code>true</code> in case the input stream is buffered, false otherwise.
     */
    public boolean isBuffered() {
        return buffered;
    }
    
    /**
     * Returns if reached the EOF
     * (10/04/00 23.16.20)
     * @return boolean
     */
    public boolean isEOF() {
        return EOF;
    }
    
    /**
     * Returns if this input layer is an active counter of the steps.
     * Warning: in a neural net there can be only one StepCounter element!
     * (10/04/00 23.23.26)
     * @return boolean
     */
    public boolean isStepCounter() {
        if (getMonitor() != null) 
            if (getMonitor().isSingleThreadMode())
                return false;
        return StepCounter;
    }
    
    public int numColumns() {
        if (cols == null)
            setColList();
        if (cols == null)
            return 0;
        else
            return cols.length;
    }
    
    // Read all input values and fullfills the buffer
    public void readAll() {
        Pattern ptn;
        getInputVector().removeAllElements();
        try {
            gotoFirstLine();
            ptn = getStream();
            while (ptn != null) {
                InputVector.addElement(ptn);
                if (EOF)
                    break;
                ptn = getStream();
            }
            if (plugIn != null) {
                plugIn.setInputVector(InputVector);
                plugIn.convertPatterns();
            }
            gotoFirstLine();
        } catch (java.io.IOException ioe) {
            String error = "IOException in "+getName()+". Message is : ";
            log.warn(error + ioe.getMessage());
            if ( getMonitor() != null )
                new NetErrorManager(getMonitor(),error + ioe.getMessage());
        } catch (NumberFormatException nfe) {
            String error = "IOException in "+getName()+". Message is : ";
            log.warn(error + nfe.getMessage());
            if ( getMonitor() != null )
                new NetErrorManager(getMonitor(),error + nfe.getMessage());
        }
    }
    
    public synchronized void revPut(Pattern array) {
        // Not used.
    }
    
    /**
     * setArrays method.
     */
    protected void setArrays(int rows, int cols) {
    }
    
    /**
     * Sets the buffer-mode for this input synapse.
     *
     * @param aNewBuffered <code>true</code> if the input should be buffered.
     * <code>false</code> if the input should not be buffered, the input will be
     * retrieved from the input source every cycle again.
     * <p><b>Whenever any converter plug in is added, the buffer-mode will be set
     * to <code>true</code>, regardless of the parameter's argument. </b></p>
     */
    public void setBuffered(boolean aNewBuffered) {
        if(plugIn == null) {
            buffered = aNewBuffered;
        } else {
            // If a plugin exists, the input synapse must be buffered
            // to permit to the plugin to work correctly.
            buffered = true;
        }
    }
    
    /**
     * Sets the list of columns that must be returned as the pattern
     * Creation date: (18/10/2000 0.45.52)
     * @param cols java.util.Vector
     */
    protected void setColList() {
        int i;
        if (getAdvancedColumnSelector().trim().length() > 0) {
            CSVParser parser = new CSVParser(getAdvancedColumnSelector().trim());
            try {
                cols = parser.parseInt();
            } catch (NumberFormatException nfe) {
                new NetErrorManager(getMonitor(), nfe.getMessage());  }
        } else {
            if ((getFirstCol() == 0) || (getLastCol() == 0))
                return;
            cols = new int[getLastCol() - getFirstCol() + 1];
            for (i=getFirstCol(); i <= getLastCol(); ++i)
                cols[i - getFirstCol()] = i;
        }
    }
    
    public void setDecimalPoint(char dp) {
        decimalPoint = dp;
        if (tokens != null)
            tokens.setDecimalPoint(dp);
    }
    
    protected void setDimensions(int rows, int cols) {
    }
    
    protected void setEOF(boolean newEOF) {
        EOF = newEOF;
    }
    
    
    /**
     * Inserire qui la descrizione del metodo.
     * Data di creazione: (11/04/00 1.22.28)
     * @param newFirstRow int
     */
    public void setFirstRow(int newFirstRow) {
        myFirstRow = firstRow;
        if (firstRow != newFirstRow) {
            firstRow = newFirstRow;
            this.resetInput();
        }
    }
    
    
    /**
     * Reset the input stream to read its content again
     */
    public synchronized void resetInput() {
        restart();
        tokens = null;
        notifyAll();
    }
    
    private void restart() {
        getInputVector().removeAllElements();
        EOF = false;
        cols = null;
    }
    
    /**
     * Inserire qui la descrizione del metodo.
     * Data di creazione: (11/04/00 1.22.34)
     * @param newLastRow int
     *
     */
    public void setLastRow(int newLastRow) {
        myLastRow = lastRow;
        if (lastRow != newLastRow) {
            lastRow = newLastRow;
            this.resetInput();
        }
    }
    
    /**
     * Adds a plug in to the stream input synapse for data preprocessing. If one
     * or more plug ins are already added to the this synapse, the plug in will
     * be added at the end of the list of plug ins.
     *
     * @param aNewPlugIn The new converter plug in to add (at the end of the list).
     * @return <code>true</code> when the plug in is added, <code>false</code> when
     * the plug in is not added, e.g. in case the plug in is already added /
     * connected to another synapse.
     */
    public boolean addPlugIn(ConverterPlugIn aNewPlugIn) {
        if(plugIn == aNewPlugIn) {
            return false;
        }
        
        // The null parameter is used to detach or delete a plugin
        if(aNewPlugIn == null) {
            // We need to declare the next plugin, if existing,
            // as not more used, so it could be used again.
            if (plugIn != null) {
                plugIn.setConnected(false);
            }
            plugIn = null;
            resetInput();
            return true;
        }
        
        if(aNewPlugIn.isConnected()) {
            // The new plugin is already connected to another plugin,
            // hence cannot be used.
            return false;
        }
        
        if(plugIn == null) {
            aNewPlugIn.setConnected(true);
            aNewPlugIn.addPlugInListener(this);
            setBuffered(true);
            plugIn = aNewPlugIn;
            resetInput();
            return true;
        } else {
            return plugIn.addPlugIn(aNewPlugIn);
        }
    }
    
    /**
     * Removes (and disconnects) all (cascading) plug-ins.
     */
    public void removeAllPlugIns() {
        if(plugIn != null) {
            plugIn.setConnected(false);
            plugIn.removeAllPlugIns();
            plugIn = null;
        }
    }
    
    /**
     * Sets the plugin for the data preprocessing.
     * @param newPlugIn the plug in to set
     *
     * @deprecated {@link #addPlugIn(ConverterPlugIn)}. If you want to replace
     * the plug in by setting a new plug in please use
     * {@link #removeAllPlugIns(ConverterPlugIn)} and {@link #addPlugIn(ConverterPlugIn)}.
     */
    public boolean setPlugin(ConverterPlugIn newPlugIn) {
        if (newPlugIn == plugIn)
            return false;
        if (newPlugIn == null)
            plugIn.setConnected(false);
        else {
            if (newPlugIn.isConnected())
                return false;
            newPlugIn.setConnected(true);
            newPlugIn.addPlugInListener(this);
            buffered = true;
        }
        plugIn = newPlugIn;
        this.resetInput();
        return true;
    }
    
    /** Gets the attached ConverterPlugin, if any
     * @return neural.engine.ConverterPlugIn
     */
    public ConverterPlugIn getPlugIn() {
        return plugIn;
    }
    
    /** Added for XML serialization
     *  **** DO NOT USE ****
     * Use getPlugin instead
     */
    public void setPlugIn(ConverterPlugIn newPlugIn) {
        this.setPlugin(newPlugIn);
    }
    
    /**
     * Inserire qui la descrizione del metodo.
     * Data di creazione: (10/04/00 23.23.26)
     * @param newStepCounter boolean
     */
    public void setStepCounter(boolean newStepCounter) {
        StepCounter = newStepCounter;
    }
    
    protected void writeObjectBase(ObjectOutputStream out) throws IOException {
        int s = 0;
        if (isBuffered()) {
            s = getInputVector().size();
            if ((s == 0) && (tokens != null)) {
                gotoFirstLine();
                readAll();
            }
        }
        if (out.getClass().getName().indexOf("xstream") != -1) {
            out.defaultWriteObject();
            if (isBuffered()) {
                out.writeObject(InputVector);
            }
        } else {
            out.defaultWriteObject();
            if (isBuffered()) {
                s = getInputVector().size();
                out.writeInt(s);
                for (int i = 0; i < s; ++i) {
                    out.writeObject(InputVector.elementAt(i));
                }
            }
        }
    }
    
    protected void readObjectBase(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (in.getClass().getName().indexOf("xstream") != -1) {
            if (isBuffered()) {
                InputVector = (Vector)in.readObject();
            }
        } else {
            if (isBuffered()) {
                Pattern ptn;
                getInputVector().removeAllElements();
                int s = in.readInt();
                for (int i = 0; i < s; ++i) {
                    ptn = (Pattern) in.readObject();
                    InputVector.addElement(ptn);
                }
            }
        }
        if (advColumnsSel == null) {
            advColumnsSel = "";
        }
        if (plugInListeners == null) {
            plugInListeners = new ArrayList();
        }
    }
    
    /** Getter for property lastCol.
     * @return Value of property lastCol.
     * @deprecated
     */
    public int getLastCol() {
        return lastCol;
    }
    
    /** Setter for property lastCol.
     * @param lastCol New value of property lastCol.
     * @deprecated
     */
    public void setLastCol(int lastCol) throws IllegalArgumentException {
        if (this.lastCol != lastCol) {
            this.lastCol = lastCol;
            cols = null;
        }
    }
    
    /** Getter for property firstCol.
     * @return Value of property firstCol.
     * @deprecated
     */
    public int getFirstCol() {
        return firstCol;
    }
    
    /** Setter for property firstCol.
     * @param firstCol New value of property firstCol.
     * @deprecated
     */
    public void setFirstCol(int firstCol) throws IllegalArgumentException {
        if (this.firstCol != firstCol) {
            this.firstCol = firstCol;
            cols = null;
        }
    }
    
    public String getAdvancedColumnSelector() {
        return advColumnsSel;
    }
    
    public void setAdvancedColumnSelector(String newAdvColSel) {
        if (advColumnsSel.compareTo(newAdvColSel) != 0) {
            advColumnsSel = newAdvColSel;
            this.resetInput();
        }
    }
    
    public void dataChanged(PlugInEvent data) {
        resetInput();
    }
    
    /** Getter for property tokens.
     * @return Value of property tokens.
     */
    protected PatternTokenizer getTokens() throws JooneRuntimeException {
        if (tokens == null)
            initInputStream();
        return tokens;
    }
    
    protected void setTokens(PatternTokenizer tkn) {
        tokens = tkn;
        if (tokens != null)
            tokens.setDecimalPoint(this.getDecimalPoint());
        restart();
    }
    
    protected Pattern zeroPattern() {
        Pattern pat = new Pattern(new double[getOutputDimension()]);
        pat.setCount(-1);
        return pat;
    }
    
    // You must override this method and insert here the setting of the object tokens.
    protected abstract void initInputStream() throws JooneRuntimeException;
    
    /**
     * Check that parameters are set correctly.
     *
     * @see Synapse
     * @return validation errors.
     */
    public TreeSet check() {
        
        // Get the parent's cehck messages.
        TreeSet checks = super.check();
        
        // Check that the first row is greater than 0.
        if (firstRow <= 0) {
            checks.add(new NetCheck(NetCheck.FATAL, "First Row parameter cannot be less than 1.", this));
        }
        
        if ( advColumnsSel == null )
            if ((firstCol <= 0 || lastCol <= 0)) {
            checks.add(new NetCheck(NetCheck.FATAL, "Input columns not set.", this));
            } else
                //                if ( advColumnsSel != null ) {
                if ( advColumnsSel.trim().length() == 0 )
                    checks.add(new NetCheck(NetCheck.FATAL, "Input columns not set.", this));
                else {
            // Check if it can be parsed here...
                }
        //                }
        if (isBuffered()) {
            if (getInputVector().size() == 0)
                try {
                    getTokens();
                } catch (JooneRuntimeException jre) {
                    checks.add(new NetCheck(NetCheck.FATAL, "Cannot initialize the input stream: "+jre.getMessage(), this));
                }
        }
        if ( getPlugIn() != null ) {
            getPlugIn().check(checks); // Should propogate down the plugins
        }
        
        return checks;
    }
    
    public Collection Inspections() {
        Collection col = new ArrayList();
        if (isBuffered()) {
            if (getInputVector().size() == 0)
                if (getTokens() != null)
                    readAll();
            col.add(new InputsInspection(getInputVector()));
        } else {
            col.add(new InputsInspection(null));
        }
        return col;
    }
    
    public String InspectableTitle() {
        return getName();
    }
    
    /** reset the state of the input synapse
     *
     */
    public void reset() {
        super.reset();
        outs = null; // This will force the call to gotoFirstLine when fwdGet is called
    }
    
    /** Getter for property maxBufSize.
     * @return Value of property maxBufSize.
     *
     */
    public int getMaxBufSize() {
        return maxBufSize;
    }
    
    /** Setter for property maxBufSize.
     * @param maxBufSize New value of property maxBufSize.
     *
     */
    public void setMaxBufSize(int maxBufSize) {
        this.maxBufSize = maxBufSize;
    }
    
    /**
     * Getter for property inputPatterns.
     * Added for XML serialization
     * @return Value of property inputPatterns.
     */
    public Vector getInputPatterns() {
        return InputVector;
    }
    
    /**
     * Setter for property inputPatterns.
     * Added for XML serialization
     * @param inputPatterns New value of property inputPatterns.
     */
    public void setInputPatterns(Vector inputPatterns) {
        this.InputVector = inputPatterns;
    }
    
    /* **************************************************************
     * Starting from here there is all the code added in order to
     * implement the new I/O framework (see the InputConnector class)
     * by P. Marrone (13/09/2004)
     */
    
    private int myFirstRow = 1;
    private int myLastRow = 0;
    // Used to indicate if the plugin.newCycle method must be invoked
    private transient boolean skipNewCycle = false;
    
    /** This method is called by the InputConnector in order
     * to get the next input pattern available for that connector
     */
    public synchronized Pattern fwdGet(InputConnector conn) {
        if (isBuffered() && (getInputVector().size() == 0)) {
            // It'll enter here only the first time, to fill the internal buffer
            firstRow = myFirstRow;
            lastRow = myLastRow;
            startFrom = 0;
            skipNewCycle = false;
            readAll();
        }
        // Context switching
        if (conn == null)
            return null;
        // as only one InputConnector can have stepCounter=true,
        // the plugin.newCycle is called only once
        skipNewCycle = !conn.isStepCounter();
        firstRow = conn.getFirstRow();
        lastRow = conn.getLastRow();
        EOF = conn.isEOF();
        currentRow = conn.getCurrentRow();
        startFrom = firstRow;
        Pattern retValue = fwdGet();
        // Reset the context
        firstRow = myFirstRow;
        lastRow = myLastRow;
        startFrom = 0;
        return retValue;
    }
    
    /**
     * Getter for property currentRow.
     * @return Value of property currentRow.
     */
    public int getCurrentRow() {
        return currentRow;
    }
    
    /**
     * Adds a plug-in lsitener to this input stream.
     *
     * @param aListener the listener to add
     */
    public void addPlugInListener(PlugInListener aListener) {
        if(!plugInListeners.contains(aListener)) {
            plugInListeners.add(aListener);
        }
    }
    
    /**
     * Removes a plug-in listener from this input stream.
     *
     * @param aListener the listener to remove
     */
    public void removePlugInListener(PlugInListener aListener) {
        plugInListeners.remove(aListener);
    }
    
    /**
     * Gets all the plug-in listeners.
     *
     * @return the plug-in listeners.
     */
    public List getAllPlugInListeners() {
        return plugInListeners;
    }
    
    /**
     * Fires an event to the plug-in listeners notifying that the underlying data
     * has changed.
     */
    protected void fireDataChanged() {
        Object[] myList;
        synchronized (this) {
            myList = getAllPlugInListeners().toArray();
        }
        
        for (int i=0; i < myList.length; ++i) {
            if (myList[i] != null) {
                ((PlugInListener)myList[i]).dataChanged(new PlugInEvent(this));
            }
        }
    }
        
}

