package org.joone.util;

import org.joone.engine.*;
import org.joone.net.*;
import java.util.Vector;
import org.joone.util.CSVParser;
import java.util.*;
import java.io.*;
import org.joone.log.*;

/** 
 * This abstract class must be extended to implement plug-ins for input data 
 * preprocessing. The objects extending this class can be inserted into objects 
 * that extend the <code>org.joone.io.StreamInputSynapse</code>.
 */
public abstract class ConverterPlugIn extends AbstractConverterPlugIn {
    
    /** The object used when logging debug, errors, warnings and info. */
    private static final ILogger log = LoggerFactory.getLogger(ConverterPlugIn.class);
    
    /** The Vector of input patterns which this converter must process. */
    //private transient Vector InputVector;
    
    /** The serial version of this object. */
    private static final long serialVersionUID = 1698511686417967414L;
    
    /** Flag indicating if every cycle the data should be preprocesed. */
    private boolean applyEveryCycle;
    
    /** The default constructor of the ConverterPlugIn. */
    public ConverterPlugIn() {
    }
    
    /** 
     * Constructor of the ConverterPlugIn. 
     *
     * @param anAdvancedSerieSelector
     * @see AbstractConverterPlugIn#AbstractConverterPlugIn(String)
     */
    public ConverterPlugIn(String anAdvancedSerieSelector) {
        super(anAdvancedSerieSelector);
    }    
    
    /**
     * This method is called at the start of a new cycle, and 
     * permit to apply the conversion for the components having
     * the applyEveryCycle property set to true.
     * This different entry point has been added in order to 
     * avoid to applying the conversion for plugins having
     * the applyEveryCycle property set to false.
     *
     * @return true if the input buffer is changed
     */
    public boolean newCycle() {
        boolean retValue = false;
        if (isApplyEveryCycle()) {
            retValue = apply();
        }
        
        if (getNextPlugIn() != null) {
            ConverterPlugIn myPlugIn = (ConverterPlugIn)getNextPlugIn();
            myPlugIn.setInputVector(getInputVector());
            retValue = myPlugIn.newCycle() | retValue;
        }
        return retValue;
    }
    
    /** 
     * Getter for property applyEachCycle.
     *
     * @return Value of property applyEachCycle.
     */
    public boolean isApplyEveryCycle() {
        return applyEveryCycle;
    }
    
    /** 
     * Setter for property applyEachCycle.
     *
     * @param anApplyEachCycle New value of property applyEachCycle.
     */
    public void setApplyEveryCycle(boolean anApplyEveryCycle) {
        applyEveryCycle = anApplyEveryCycle;
    }
        
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        // To maintain the compatibility with the old saved classes
        if (getAdvancedSerieSelector() == null) 
            setAdvancedSerieSelector(new String("1"));
        if (getName() == null)
            setName("InputPlugin 9");
    }
}