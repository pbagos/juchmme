package org.joone.io;

import java.util.*;
import org.joone.engine.*;
import org.joone.util.*;
import org.joone.log.*;

public abstract class StreamOutputSynapse extends Synapse implements PlugInListener {
    /**
     * Logger
     * */
    private static final ILogger log = LoggerFactory.getLogger(StreamOutputSynapse.class);
    private char separator = ';';
    
    private static final long serialVersionUID = 7344684413113722785L;

    // The FIFO used for buffering
    protected transient Fifo fifo;
    // Are we buffering the output.  Can be used for OutputConverters that need all data in order to convert or if a buffer is required.
    // Buffer = true means store all the data then write in one go.
    private boolean buffered = true;
    // If we are using buffered mode then this will hold the buffered patterns.
    private transient Vector buffered_patterns;
    // Support for Output Conversion
    protected OutputConverterPlugIn nextPlugIn = null;
    
    public StreamOutputSynapse() {
        super();
    }
    protected void backward(double[] pattern) {
        // Non usato.
    }
    
    protected void forward(double[] pattern) {
        outs = pattern;
    }
    
    /**
     * The standard fwdPut method.  This method performs buffering of pattern data if required according to the buffered flag.
     * If buffered mode is set then the default multiple writing method write(Pattern [] patterns) method is called to loop through all
     * the patterns calling the single pattern writing abstract method write(Pattern pattern) to write the pattern to the appropriate media.
     * If the synapse is not in buffered mode then the  pattern is simply passed to the single pattern writing abstract
     * method write(Pattern pattern) to write the pattern to the appropriate media.
     *
     * Custom xxxOutputSynapse classes can over ride this method if they would like to perform a custom buffering implementation.
     * In this case it will be the responsiblity of the custom class to ensure all patterns are output correctly and any conversion is done
     * prior to writing data.
     */
    public synchronized void fwdPut(Pattern pattern) {
        
        if (isEnabled()) { // Check that this synapse is enabled or not
            try {
                if ( isBuffered() )  // Check if it's using buffered mode or not
                {
                    if ( pattern.getCount() > -1)  // If we still going through patterns then simply add them to the fifo
                    {
                        // Push Pattern onto buffered FIFO.
                        m_pattern = pattern;
                        inps = (double[]) pattern.getArray();
                        forward(inps);
                        m_pattern.setArray(outs);
                        getFifo().push(m_pattern.clone());
                        items = fifo.size();
                    }
                    else  // Otherwise we have finished so convert all patterns
                    {
                        // We've come to the end so write do any buffer conversion (if applicable)  and output the data
                        // Some xxxOutputSynapse might need to know they are on the last pattern so also add the count=-1 pattern.
                        //m_pattern = pattern;
                        //inps = (double[]) pattern.getArray();
                        //forward(inps);
                        //m_pattern.setArray(outs);     // Don't foward if on a stop pattern
                        getFifo().push(pattern.clone());
                        items = fifo.size();
                        
                        int num_patterns = fifo.size();
                        buffered_patterns = new Vector();
                        for ( int i = 0; i<num_patterns;i++) {
                            buffered_patterns.addElement(((Pattern)fifo.pop()).clone());
                        }
                        // Conversion i.e unnormalizer would have to be done here.  ConverterPlugin.convert(buffered_patterns);
                        // NOTE : Any Raw Data conversion i.e TextConverter will have to be called by the converting xxxOutputSynapse.
                        
                        if ( nextPlugIn != null ){
                            nextPlugIn.setInputVector(buffered_patterns);
                            nextPlugIn.convertPatterns();
                        }
                        write(buffered_patterns);
                    }
                }
                else // We are not in buffered mode so write the single pattern out
                {
                    // We are not in buffered mode so just write the pattern
                    // Also need to do any non-buffered (if applicable) conversion here
                    // Conversion i.e unnormalizer would have to be done here.  E.g ConverterPlugin.convert(pattern);
                    // NOTE : Any Raw Data conversion i.e TextConverter will have to be called by the converting xxxOutputSynapse.
                    // NOTE : Also the RawConverter has not yet been decided at the moment.
                    
                    if ( pattern.getCount() > -1)  // Only foward and convert if not on a stop pattern
                    {
                        inps = (double[]) pattern.getArray();
                        forward(inps);
                        items=1;
                        
                        if ( nextPlugIn != null ){
                            nextPlugIn.setPattern(pattern);
                            nextPlugIn.convertPattern();
                        }
                    }
                    write(pattern);     // Write the pattern
                } // End else not buffered
            } catch (NumberFormatException nfe) {
                String error = "NumberFormatException in "+getName()+". Message is : ";
                log.warn(error + nfe.getMessage());
                if ( getMonitor() != null )
                    new NetErrorManager(getMonitor(),error + nfe.getMessage());
            }
            
        } // End of if enabled
        
        notifyAll();
        
    } // End fwdPut method.
    
    /**
     * This function writes all of the buffered patterns to the appropriate media.  If required this function can be over written to allow
     * custom output synapses greater control of the pattern writing.  This method is only used if the StreamOutputSynapse is buffered.
     */
    private void write(Vector patterns) {
        if ( patterns != null ) {
            for ( int i=0;i<patterns.size();i++)  // Loop through and write pattern
                write((Pattern)patterns.elementAt(i));
        }
    }
    
    /**
     * Custom xxxOutputSynapses need to implement at least this method.  The custom synapse should write the pattern out to the
     * appropriate media.  All patterns including the ending pattern with pattern.getCount()=-1 will be passed to this method so that
     * custom output synapses can perform final processing.
     */
    public abstract void write(Pattern pattern);
    
    /**
     * Returns the column separator
     * creation date: (23/04/00 0.50.18)
     * @return char
     */
    public char getSeparator() {
        return separator;
    }
    public synchronized Pattern revGet() {
        // Non usato.
        return null;
    }
    /**
     * setArrays method comment.
     */
    protected void setArrays(int rows, int cols) {}
    /**
     * Dimensiona l'elemento
     * @param int rows - righe
     * @param int cols - colonne
     */
    protected void setDimensions(int rows, int cols)
    {}
    /**
     * Inserire qui la descrizione del metodo.
     * Data di creazione: (23/04/00 0.50.18)
     * @param newSeparator char
     */
    public void setSeparator(char newSeparator) {
        separator = newSeparator;
    }
    
    /**
     * Sets the buffered status of this synapse.
     */
    public void setBuffered(boolean buf) {
        buffered = buf;
    }
    
    /**
     * Checks the buffered status of this synapse.
     */
    public boolean isBuffered() {
        return(buffered);
    }
    
    public TreeSet check() {
        TreeSet checks = super.check();
        
        //checks.add(new NetCheck(NetCheck.ERROR, "File Name not set." , this));
        // Call all OutputConverterPlugins here
        if ( nextPlugIn != null ) {
            nextPlugIn.check(checks); // Should propogate down the plugins
        }
        return checks;
    }
    
    /** Getter for property fifo.
     * @return Value of property fifo.
     *
     */
    protected Fifo getFifo() {
        if (fifo == null)
            fifo = new Fifo();
        return fifo;
    }
    
    /**
     * @return neural.engine.ConverterPlugIn
     */
    public OutputConverterPlugIn getPlugIn() {
        return nextPlugIn;
    }
    
    /**
     * Sets the plugin for the data postprocessing
     * @param newPlugIn neural.engine.OutputConverterPlugIn
     *
     * @deprecated use {@link #addPlugIn(OutputConverterPlugIn)}
     */
    public boolean setPlugIn(OutputConverterPlugIn newPlugIn) {
        if (newPlugIn == nextPlugIn)
            return false;
        if (newPlugIn == null)
            nextPlugIn.setConnected(false);
        else {
            if (newPlugIn.isConnected())
                return false;
            newPlugIn.setConnected(true);
            newPlugIn.addPlugInListener(this);
            buffered = true;
        }
        nextPlugIn = newPlugIn;
        getFifo().removeAllElements();
        return true;
    }
    
    /**
     * Adds a plug in to the stream output synapse for data preprocessing. If one 
     * or more plug ins are already added to the this synapse, the plug in will 
     * be added at the end of the list of plug ins.
     * 
     * @param aNewPlugIn The new converter plug in to add (at the end of the list).
     * @return <code>true</code> when the plug in is added, <code>false</code> when
     * the plug in is not added, e.g. in case the plug in is already added / 
     * connected to another synapse.
     */
    public boolean addPlugIn(OutputConverterPlugIn aNewPlugIn) {
        if(nextPlugIn == aNewPlugIn) {
            return false;
        }
        
        // The null parameter is used to detach or delete a plugin
        if(aNewPlugIn == null) {
            // We need to declare the next plugin, if existing,
            // as not more used, so it could be used again.
            if (nextPlugIn != null) {
                nextPlugIn.setConnected(false);
            }
            nextPlugIn = null;
            getFifo().removeAllElements();
            return true;
        }
        
        if(aNewPlugIn.isConnected()) {
            // The new plugin is already connected to another plugin,
            // hence cannot be used.
            return false;
        }
                
        if(nextPlugIn == null) {
            aNewPlugIn.setConnected(true);
            aNewPlugIn.addPlugInListener(this);
            setBuffered(true);
            nextPlugIn = aNewPlugIn;
            getFifo().removeAllElements();
            return true;
        } else {
            return nextPlugIn.addPlugIn(aNewPlugIn);
        }      
    }
    
    /**
     * Removes (and disconnects) all (cascading) plug-ins.
     */
    public void removeAllPlugIns() {
        if(nextPlugIn != null) {
            nextPlugIn.setConnected(false);
            nextPlugIn.removeAllPlugIns();
            nextPlugIn = null;
        }
    }
    
    public void dataChanged(PlugInEvent data) {
        getFifo().removeAllElements();
    }
    
}