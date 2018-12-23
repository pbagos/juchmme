package org.joone.util;



import org.joone.engine.*;

/**
 * Center around the zero all the time series subtracting its average
 * Creation date: (23/10/2000 23.55.34)
 * @author: Administrator
 */
public class CenterOnZeroPlugIn extends ConverterPlugIn {
    
    private static final long serialVersionUID = 8581778588210471901L;
    
    /**
     * CenterOnZeroPlugIn constructor comment.
     */
    public CenterOnZeroPlugIn() {
        super();
    }
    /**
     * Starts the convertion
     */
    protected boolean convert(int serie) {
        boolean retValue = false;
        int s = getInputVector().size();
        int i;
        double v;
        double d = 0;
        Pattern currPE;
        // Calculate average
        for (i = 0; i < s; ++i)
            d += getValuePoint(i, serie);
        d = d / s;
        // Shift average amount
        for (i = 0; i < s; ++i) {
            v = getValuePoint(i, serie);
            v = v - d;
            currPE = (Pattern) getInputVector().elementAt(i);
            currPE.setValue(serie, v);
            retValue = true;
        }
        return retValue;
    }
}