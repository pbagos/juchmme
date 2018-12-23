package org.joone.util;

import org.joone.engine.*;

/**
 * logarithmic transfer (base e) input data
 * Creation date: (22/03/2004 23.06.00)
 * @author: yccheok
 *
 */
public class LogarithmicPlugIn extends ConverterPlugIn {
    private static final long serialVersionUID = 4662839350631574552L;
    
    /**
     * LogarithmicPlugIn constructor
     */
    public LogarithmicPlugIn() {
        super();
    }
    
    /**
     * Start the convertion
     */
    protected boolean convert(int serie) {
        int s = getInputVector().size();
        double v = 0, result = 0;
        Pattern currPE = null;
        
        for(int i=0; i<s; ++i) {
            v = getValuePoint(i, serie);
            currPE = (Pattern) getInputVector().elementAt(i);
            
            if (v >= 0)
                result = Math.log(1 + v);
            else
                result = -Math.log(1 - v);
                
            currPE.setValue(serie, result);
        }
        return true;
    }
}
