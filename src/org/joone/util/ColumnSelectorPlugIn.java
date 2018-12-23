/*
 * ColumnSelectorPlugIn.java
 *
 * Created on November 4, 2004, 4:26 PM
 */

package org.joone.util;

import org.joone.engine.Pattern;
import org.joone.log.*;

/**
 * Certain plug-ins change the number of columns during their conversion, for example
 * the <code>ToBinaryPlugin</code> increases the number of columns. This plug in can 
 * be used to select certain columns from all available columns at that moment.
 * It just uses an advanced column selector as <code>StreamInputSynapse</code> and
 * all plug-ins do, but this plug-in does not work on certain columns as most plug-ins
 * do, but it selects columns like the <code>StreamInputSynapse</code> does.
 *
 * @author  Boris Jansen
 */
public class ColumnSelectorPlugIn extends ConverterPlugIn {
    
    /** The logger for this class. */
    private static final ILogger log = LoggerFactory.getLogger(ColumnSelectorPlugIn.class); 
    
    /** Creates a new instance of ColumnSelectorPlugIn */
    public ColumnSelectorPlugIn() {
    }
    
    /** 
     * Creates a new instance of ColumnSelectorPlugIn 
     * 
     * @param anAdvancedSerieSelector the advanced serie selector to use.
     * @see setAdvancedSerieSelector()
     */
    public ColumnSelectorPlugIn(String anAdvancedSerieSelector) {
        super(anAdvancedSerieSelector);
    }
    
    protected boolean convert(int serie) {
        // do nothing, we select the columns in applyOnRows
        return false;
    }
    
    protected boolean applyOnRows() {  
        boolean retValue = false;
        if (getAdvancedSerieSelector() != null && !getAdvancedSerieSelector().equals(new String(""))) {
            int [] mySerieSelected = getSerieSelected();
            for(int i = 0; i < getInputVector().size(); i++) {
                double[] mySelected = new double[mySerieSelected.length];
                Pattern myPattern = (Pattern)getInputVector().get(i);
                for(int j = 0; j < mySerieSelected.length; j++) {
                    if(mySerieSelected[j] - 1 < myPattern.getArray().length) {  // Check we don't go over array bounds.
                        mySelected[j] = myPattern.getArray()[mySerieSelected[j] - 1];
                    } else {
                        log.warn(getName() + " : Advanced Serie Selector contains too many serie. " +
                            "Check the number of columns in the appropriate input synapse or previous plug in.");
                    }
                }
                myPattern.setArray(mySelected);
                retValue = true;
            }
        }
        return retValue;
    }
}
