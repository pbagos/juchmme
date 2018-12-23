/*
 * AbstractConverterPlugIn.java
 *
 * Created on October 11, 2004, 3:52 PM
 */

package org.joone.util;

import java.util.*;
import java.io.*;
import org.joone.net.NetCheck;
import org.joone.engine.*;
import org.joone.log.*;

/**
 * This abstract class must be extended to implement plug-ins for input or output
 * data pre- or post-processing.
 *
 * <!-- Note
 *      This class is created to remove differences and duplicated code between
 * the ConverterPlugIn and the OutputConverterPlugIn.
 * -->
 *
 * @author  Boris Jansen
 */
public abstract class AbstractConverterPlugIn implements java.io.Serializable, PlugInListener {
    
    /** The serial version of this object. */
    private static final long serialVersionUID = 5698511686417862414L;
    /** The object used when logging debug, errors, warnings and info. */
    private static final ILogger log = LoggerFactory.getLogger(AbstractConverterPlugIn.class);
    
    /** The next plugin in this series of cascading plugins. */
    private AbstractConverterPlugIn nextPlugIn = null;
    
    /** The name of this plug-in object. */
    private String name;
    
    /** This flag indicates if this plug-in is connected. Whenever a plug in is connected
     * it cannot be connected / added to another input stream / plug-in. */
    private boolean connected;
    
    /** A vector of objects that are listening to this object for plug-in (data changed) events. */
    protected Vector pluginListeners;
    
    /** The Vector of input patterns which this converter must process. */
    private transient Vector InputVector;
    
    /**
     * The <code>AdvancedSerieSelector</code> instructs this plug-in what serie/columns
     * it should process. The format of this specification is a common separated list of
     * values and ranges. E.g '1,2,5,7' will instruct the converter to convert serie 1
     * and 2 and 5 and 7. A range can also be used e.g '2,4,5-8,9' will instruct the
     * converter to process serie 2 and 4 and 5 and 6 and 7 and 8 and 9.  A range is specifed
     * using a '-' character with the number of the serie on either side.
     * <P>Note <b>NO</b> negative numbers can be used in the <code>AdvancedSerieSelector</code>.</P>
     */
    private String AdvancedSerieSelector = new String("");
    
    /** The series to be converted. */
    private transient int [] serieSelected;
    
    /** Creates a new instance of AbstractConverterPlugIn */
    public AbstractConverterPlugIn() {
    }
    
    /**
     * Creates a new instance of AbstractConverterPlugIn
     *
     * @param anAdvancedSerieSelector the advanced serie selector to use.
     * @see setAdvancedSerieSelector()
     */
    public AbstractConverterPlugIn(String anAdvancedSerieSelector) {
        setAdvancedSerieSelector(anAdvancedSerieSelector);
    }
    
    /**
     * Converts all the patterns contained by {@link #InputVector} and on the
     * serie specifed by the call to {@link setAdvancedSerieSelector#setAdvancedSerieSelector}.
     * It cascades also the conversion to the next-plugin connected in the chain.
     */
    public void convertPatterns() {
        apply();
        cascade();
    }
    
    /**
     * Applies all the conversions on the patterns contained by {@link #InputVector} 
     * @return true if the input buffer is changed
     */
    protected boolean apply() {
        boolean retValue = false;
        if ((getInputVector() != null) && (getInputVector().size() > 0)) {
            retValue = applyOnColumns() | applyOnRows();
        } else {
            log.warn( getName()+" : Plugin has no input data to convert." );
        }
        return retValue;
    }
    
    /**
     * Applies the conversion on the patterns contained by {@link #InputVector} and on the
     * columns specifed by the call to {@link setAdvancedSerieSelector#setAdvancedSerieSelector}.
     */
    protected boolean applyOnColumns() {
        boolean retValue = false;
        Pattern currPE = (Pattern) getInputVector().elementAt(0);
        int aSize = currPE.getArray().length;
        
        // Use Advanced Serie Selector to select the serie to convert
        if ( (getAdvancedSerieSelector() != null ) && (!getAdvancedSerieSelector().equals(new String(""))) ) {
            int [] mySerieSelected = getSerieSelected();
            for(int i = 0; i < mySerieSelected.length; i++) {
                if(mySerieSelected[i]-1 < aSize) {  // Check we don't go over array bounds.
                    retValue = convert(mySerieSelected[i]-1) | retValue;
                } else {
                    log.warn(getName() + " : Advanced Serie Selector contains too many serie. Check the number of columns in the appropriate input synapse.");
                }
            }
        }
        return retValue;
    }
    
    /**
     * Applies the conversion on the patterns contained by {@link #InputVector} 
     * on all the rows. Override this empty method to apply any change to the 
     * order of the input vector's rows.
     */
    protected boolean applyOnRows() {
        return false;
    }
    
    /**
     * Cascades the <code>convertPatterns()</code> method call to the next plug-in.
     */
    protected void cascade() {
        if (getNextPlugIn() != null) { // Loop through other cascading plugins
            AbstractConverterPlugIn myPlugIn = getNextPlugIn();
            myPlugIn.setInputVector(getInputVector());
            myPlugIn.convertPatterns();
        }
    }
    
    /**
     * Applies the conversion on the Nth serie of the buffered pattern data. The method is abstract
     * and should be overridden by the implementing class. Implementing classes can obtain the
     * input patterns by calling the {@link #getInputVector()} method. The result is a
     * <code>Vector</code> of <code>Pattern</code> objects which this method should use by converting
     * the requested serie.
     *
     * @param serie the serie to convert
     */
    protected abstract boolean convert(int serie);
    
    /**
     * Gets the double value at the specified row (point) in the specifed serie /
     * column.
     *
     * @param point The row at which to get the pattern's double value.
     * @param serie The serie or column from which to obtain the value.
     * @return The value at the specified point in the input vector.
     */
    protected double getValuePoint(int point, int serie) {
        Pattern currPE = (Pattern) getInputVector().elementAt(point);
        return currPE.getArray()[serie];
    }
    
    /**
     * Gets the name of this plug-in object.
     *
     * @return The name of this plug-in.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the name of this plug-in object.
     *
     * @param aName New name for this object.
     */
    public void setName(String aName) {
        name = aName;
    }
    
    /**
     * Getter for property connected.
     * This property is true when this plugin has been
     * attached either to a StreamInputSynapse or to
     * another plugin.
     * @return Value of property connected.
     */
    public boolean isConnected() {
        return connected;
    }
    
    /**
     * Setter for property connected.
     * This property is true when this plugin has been
     * attached either to a StreamInputSynapse or to
     * another plugin.
     * @param aConnected New value of property connected.
     */
    public void setConnected(boolean aConnected) {
        connected = aConnected;
    }
    
    /**
     * Adds an {@link PlugInListener} to this plug-in. Usually this will be the
     * previous plug-in in the series of cascading plug-ins or the stream
     * input/output synapse.
     *
     * @param aListener The listener that requires notification of events from
     * this plug-in whenever data changes.
     */
    public synchronized void addPlugInListener(PlugInListener aListener) {
        if(!getPluginListeners().contains(aListener)) {
            getPluginListeners().add(aListener);
        }
    }
    
    /**
     * Removes a {@link PlugInListener} that was previously registered to receive
     * plugin (data changed) events.
     *
     * @param aListener The listener that does not want to receive any events
     * anymore from this plug-in.
     */
    public synchronized void removePlugInListener(PlugInListener aListener) {
        if (getPluginListeners().contains(aListener)) {
            getPluginListeners().remove(aListener);
        }
    }
    
    /**
     * Gets a vector of all the {@link PlugInListener}s that have been registerd
     * to receive events from this plug-in.
     *
     * @return The vector of <code>PlugInListener</code>s listening to this
     * converter plug-in object.
     */
    protected Vector getPluginListeners() {
        if (pluginListeners == null) {
            pluginListeners = new Vector();
        }
        return pluginListeners;
    }
    
    public void dataChanged(PlugInEvent anEvent) {
        fireDataChanged();
    }
    
    /**
     * Fires a data changed event to all {@link PlugInListeners} that are registered
     * to receive events from this plug-in object. This method calls the
     * {@link InputPlugInListener#dataChanged()} method in all registered listeners.
     */
    protected void fireDataChanged() {
        Object[] myList;
        synchronized (this) {
            myList = getPluginListeners().toArray();
        }
        
        for (int i=0; i < myList.length; ++i) {
            PlugInListener myListener = (PlugInListener)myList[i];
            if (myListener != null) {
                myListener.dataChanged(new PlugInEvent(this));
            }
        }
    }
    
    /**
     * Gets the AdvancedSerieSelector.
     *
     * @return Value of property AdvancedSerieSelector.
     */
    public String getAdvancedSerieSelector() {
        return AdvancedSerieSelector;
    }
    
    /**
     * Sets the AdvancedSerieSelector for this plugin.
     * <P>The AdvancedSerieSelector instructs this plug-in what serie/columns it
     * should process. The format of this specification is a common seperated list of
     * values and ranges. E.g '1,2,5,7' will instruct the converter to convert serie 1
     * and 2 and 5 and 7. A range can also be used e.g '2,4,5-8,9' will instruct the
     * converter to process serie 2 and 4 and 5 and 6 and 7 and 8 and 9. A range is specifed
     * using a '-' character with the number of the serie on either side.
     * <P>Note <b>NO</b> negative numbers can be used in the <code>AdvancedSerieSelector</code>.</P>
     *
     * @param aNewSerieSelector New value for the <code>AdvancedSerieSelector</code>.
     */
    public void setAdvancedSerieSelector(String aNewSerieSelector) {
        if((AdvancedSerieSelector == null) || (AdvancedSerieSelector.compareTo(aNewSerieSelector) != 0)) {
            AdvancedSerieSelector = aNewSerieSelector;
            serieSelected = null;
            fireDataChanged();
        }
    }
    
    /**
     * Getter for property <code>serieSelected</code>. Returns the list of
     * selected columns to elaborate.
     *
     * @return Value of property <code>serieSelected</code>.
     */
    protected int[] getSerieSelected() {
        if(serieSelected == null) {
            // if the advanced serie selected string is not parsed yet, then parse
            // it now to obtain the serie selected
            CSVParser myParser = new CSVParser(getAdvancedSerieSelector(), true);
            serieSelected = myParser.parseInt();
        }
        return serieSelected;
    }
    
    /**
     * Adds a plug-in at the end of the list of plug-ins.
     *
     * @param aNewPlugIn the new plug in to add at the end of plug ins.
     * @return <code>true</code> when the plug in is added succesfully,
     * <code>false</code> when the plug in is not added, e.g. in case the
     * plug in is already added / connected to another synapse / plug-in.
     */
    public boolean addPlugIn(AbstractConverterPlugIn aNewPlugIn) {
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
            fireDataChanged();
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
            nextPlugIn = aNewPlugIn;
            fireDataChanged();
            return true;
        } else {
            return nextPlugIn.addPlugIn(aNewPlugIn);
        }
    }
    
    /**
     * Removes (and disconnects) all (cascading) plug ins.
     */
    public void removeAllPlugIns() {
        if(nextPlugIn != null) {
            nextPlugIn.setConnected(false);
            nextPlugIn.removeAllPlugIns();
            nextPlugIn = null;
        } else {
            // this is the last plug-in in a chain of plug ins that are removed
            // just only one time it should be notified that these plug-ins are
            // not used anymore (the data should not be converted anymore), so
            // here we fire a data changed event JUST ONCE.
            fireDataChanged();
        }
    }
    
    /**
     * Sets the next plug-in in a cascading series of plugins.
     *
     * @param aNewNextPlugIn The next plug-in in the series.
     * @return <code>true</code> when the plug-in is successfully added,
     * <code>false</code> otherwise.
     * @deprecated {@link addPlugIn(AbstractConverterPlugIn)}
     */
    public boolean setNextPlugin(AbstractConverterPlugIn aNewNextPlugIn) {
        if (aNewNextPlugIn == nextPlugIn) {
            return false;
        }
        
        if (aNewNextPlugIn == null) {
            nextPlugIn.setConnected(false);
        } else {
            if (aNewNextPlugIn.isConnected()) {
                return false;
            }
            aNewNextPlugIn.setConnected(true);
            aNewNextPlugIn.addPlugInListener(this);
        }
        nextPlugIn = aNewNextPlugIn;
        fireDataChanged();
        return true;
    }
    
    /**
     * Gets the next converter plug-in within this cascading series of plug-ins.
     *
     * @return the next plug-in within this cascading series of plug-ins.
     */
    public AbstractConverterPlugIn getNextPlugIn() {
        return nextPlugIn;
    }
    
    /**
     * Added for XML serialization
     * <p><b> **** DO NOT USE **** </b>
     * <p>Use {@link #addPlugIn(AbstractConverterPlugIn)}
     */
    public void setNextPlugIn(AbstractConverterPlugIn newNextPlugIn) {
        addPlugIn(newNextPlugIn);
    }
    
    /**
     * Sets the input vector of <code>Patterns</code> that this converter plugin should process.
     * @param newInputVector The vector of Pattern objects to process.
     */
    public void setInputVector(java.util.Vector newInputVector) {
        InputVector = newInputVector;
    }
    /**
     * Gets the input vector of <code>Patterns</code> with which this converter must process.
     *
     * @return the vector with patterns that this converter processes.
     */
    protected Vector getInputVector() {
        return InputVector;
    }
    
    /**
     * This method is called to perform a check on this converter's properties to
     * ensure there are no errors or problems. If there is an error or problem with
     * one of the properties then the issues are returned in a <code>TreeSet</code>
     * object.
     *
     * @param checks A <code>TreeSet</code> of issues that should be added to by this
     * plug-in.
     * @return A <code>TreeSet</code> of errors or problems relating to the setup of
     * this converter plug-in object.
     * @see Synapse
     */
    public TreeSet check(TreeSet checks) {
        if(AdvancedSerieSelector == null || AdvancedSerieSelector.equals(new String(""))) {
            checks.add(new NetCheck(NetCheck.FATAL, "Advanced Serie Selector should be populated, e.g 1,2,4." , this));
        }
        
        // Call next converter plug-in in the chain of converter plug-ins
        if(getNextPlugIn() != null) {
            getNextPlugIn().check(checks);
        }
        return checks;
    }
    
    /**
     * Gets the index of the current serie number.
     * @return int -1 if the serie could not be found in the serie specification.
     */
    protected int getSerieIndexNumber(int serie) {
        CSVParser Parse = new CSVParser(getAdvancedSerieSelector(),true);
        int [] checker = Parse.parseInt();
        for ( int i=0; i<checker.length;i++) {
            if(checker[i] == serie+1)
                return(i); // Returns index in array
        }
        return(-1); // Serie not found
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (getAdvancedSerieSelector() == null) // To maintain the compatibility with the old saved classes
            setAdvancedSerieSelector(new String("1"));
    }
}
