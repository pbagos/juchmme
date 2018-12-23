/*
 * ShufflePlugin.java
 *
 * Created on 11 febbraio 2004, 14.59
 */

package org.joone.util;
import org.joone.engine.Pattern;

/** This plugin shuffles the content of the input buffer
 * by mixing randomly the rows.
 *
 * @author  P.Marrone
 */
public class ShufflePlugin extends ConverterPlugIn {
    
     static final long serialVersionUID = 7118539833128121178L;
     
    /** Creates a new instance of ShufflePlugin */
    public ShufflePlugin() {
        setApplyEveryCycle(true);
        setAdvancedSerieSelector("1");
    }
    
    protected boolean convert(int serie) {
        // We don't need to apply any conversion to the columns
        return false;
    }
    
    protected boolean applyOnRows() {
        boolean retValue = false;
        int s = getInputVector().size();
        for (int i=0; i < s; ++i) {
            int n = i;
            while (n == i) {
                // Extracts an integer between 0 and s - 1
                n = Math.round((float)(Math.random() * s));
                n = (n > 0) ? n - 1: 0;
            }
            // Now exchanges the elements at n,i
            Pattern p1 = (Pattern)getInputVector().elementAt(i);
            Pattern p2 = (Pattern)getInputVector().elementAt(n);
            getInputVector().set(n, p1);
            getInputVector().set(i, p2);
            retValue = true;
        }
        return retValue;
    }
    
}
