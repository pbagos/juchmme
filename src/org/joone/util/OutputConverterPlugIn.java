package org.joone.util;

import org.joone.engine.*;
import org.joone.net.*;
import java.util.*;
import java.io.*;
import org.joone.log.*;

/**
 * Abstract class that must be extended to implement plugins for output data preprocessing.
 * The objects extending this class can be inserted into objects that extend the
 * {@link org.joone.io.StreamOutputSynapse}.
 *
 * @author Julien Norman
 */
public abstract class OutputConverterPlugIn extends AbstractConverterPlugIn {
    
    /** For converting on a one pattern basis. */
    private transient Pattern conv_pattern;
    
    private static final long serialVersionUID = 1698511686417967414L;
    
    /** The logger for this class. */
    private static final ILogger log = LoggerFactory.getLogger( OutputConverterPlugIn.class );
    
    /**
     * Constructor of CoverterPlugIn.
     */
    public OutputConverterPlugIn() {
    }
    
    /**
     * Constructor of CoverterPlugIn.
     *
     * @param anAdvancedSerieSelector
     * @see AbstractConverterPlugIn#AbstractConverterPlugIn(String)
     */
    public OutputConverterPlugIn(String anAdvancedSerieSelector) {
        super(anAdvancedSerieSelector);
    }
    
    /**
     * Applies the preprocessing on the Nth serie of the data. This method should be
     * implemented in classes that extend this one.
     *
     * @param serie the serie to be converted
     */
    protected abstract void convert_pattern(int serie);
        
    /**
     * Applies the preprocessing on the patterns contained in the conv_patterns
     */
    public void convertPattern() throws NumberFormatException {
        // Use Advanced Serie Selector to select the serie to convert
        if((getAdvancedSerieSelector() != null ) && (!getAdvancedSerieSelector().equals(new String(""))) ) {
            try {
                int [] mySerieSelected = getSerieSelected();
                for(int i = 0; i < mySerieSelected.length; i++) {
                    convert_pattern(mySerieSelected[i]-1);
                }
            } catch (NumberFormatException nfe) { throw new NumberFormatException(nfe.getMessage()); }
        }
        else
            log.warn(getName()+" : Advanced Serie Selector not populated therefore converting no data.");
        // Recurrent call to other connected plugins
        if (getNextPlugIn() != null) {
            OutputConverterPlugIn myPlugIn = (OutputConverterPlugIn)getNextPlugIn();
            myPlugIn.setPattern(getPattern());
            myPlugIn.convertPattern();
        }
    }
    
    /**
     * Applies the preprocessing on the pattern contained in the conv_pattern
     * @deprecated Use {@link AbstractConverterPlugIn#convertPatterns()}
     */
    public void convertAllPatterns() throws NumberFormatException  {
        convertPatterns();
    }
    
    /**
     * Gets the pattern that will be converted.
     * @return the Pattern to convert when in unbuffered mode
     */
    protected Pattern getPattern() {
        return conv_pattern;
    }
        
    /** Sets the current pattern to convert.
     * @param newPattern The pattern that should be converted by this plugin.
     */
    public void setPattern(Pattern newPattern) {
        conv_pattern = newPattern;
    }
        
    /**
     * @deprecated Use {@link AbstractConverterPlugIn#addPlugInListener(PlugInListener)}
     */
    public synchronized void addOutputPluginListener(OutputPluginListener aListener) {
        addPlugInListener(aListener);
    }
    
    /**
     * @deprecated Use {@link AbstractConverterPlugIn#removePlugInListener(PlugInListener)}
     */
    public synchronized void removeOutputPluginListener(OutputPluginListener aListener) {
        removePlugInListener(aListener);
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        // To maintain the compatibility with the old saved classes
        if (getAdvancedSerieSelector() == null)
            setAdvancedSerieSelector(new String("1"));
        if (getName() == null)
            setName("OutputPlugin 9");
    }
}