package org.joone.util;



import org.joone.engine.*;

/**
 * Normalizes the input data within a predefined range
 * Creation date: (23/10/2000 23.23.25)
 * @author: Administrator
 *
 */
public class NormalizerPlugIn extends ConverterPlugIn {
    private double min = 0;
    private double max = 1;
    private double datamin = 0;
    private double datamax = 0;
    
    private static final long serialVersionUID = 4662839350631576461L;
    
    /**
     * NormalizerPlugIn constructor
     */
    public NormalizerPlugIn() {
        super();
    }
    /**
     * Start the convertion
     */
    protected boolean convert(int serie) {
        boolean retValue = false;
        int s = getInputVector().size();
        int i;
        double v, d;
        double vMax = getValuePoint(0, serie);
        double vMin = vMax;
        Pattern currPE;
        
        // If user has set the datamin and datamax to these special values 0 and 0 respectively then the min max is determined from the data.
        if ( (datamin == 0 ) && ( datamax == 0 ) ) {
            for (i = 0; i < s; ++i) {
                v = getValuePoint(i, serie);
                if (v > vMax)
                    vMax = v;
                else
                    if (v < vMin)
                        vMin = v;
            }
        }
        else	// Otherwise set it to the what the user has requested.
        {
            vMax = datamax;
            vMin = datamin;
        }
        
        d = vMax - vMin;
        for (i = 0; i < s; ++i) {
            if (d != 0.0) {
                v = getValuePoint(i, serie);
                v = (v - vMin) / d;
                v = v * (getMax() - getMin()) + getMin();
            }
            else {
                v = getMin();
            }
            currPE = (Pattern) getInputVector().elementAt(i);
            currPE.setValue(serie, v);
            retValue = true;
        }
        return retValue;
    }
    /**
     * Gets the max value
     * Creation date: (23/10/2000 23.25.55)
     * @return float
     */
    public double getMax() {
        return max;
    }
    /**
     * Gets the min value
     * Creation date: (23/10/2000 23.25.32)
     * @return float
     */
    public double getMin() {
        return min;
    }
    /**
     * Sets the max value of the normalization range
     * Creation date: (23/10/2000 23.25.55)
     * @param newMax float
     */
    public void setMax(double newMax) {
        if (max != newMax ) {
            max = newMax;
            super.fireDataChanged();
        }
    }
    /**
     * Sets the min value of the normalization range
     * Creation date: (23/10/2000 23.25.32)
     * @param newMin float
     */
    public void setMin(double newMin) {
        if ( min != newMin ) {
            min = newMin;
            super.fireDataChanged();
        }
    }
    
    /** Data Min / Max Params **/
    
    /**
     * Gets the max value of the input data
     * Creation date: (23/10/2000 23.25.55)
     * @return double The maximum value of the input data.
     */
    public double getDataMax() {
        return datamax;
    }
    /**
     * Gets the min value of the input data
     * Creation date: (23/10/2000 23.25.32)
     * @return double The minimum value of the input data.
     */
    public double getDataMin() {
        return datamin;
    }
    /**
     * Sets the max value of the input data.
     * Note : The DataMin and DataMax values should be set to 99999 and -99999 respectively if the
     * user requires that this plugin uses the min , max values found in the serie.
     * Creation date: (23/10/2000 23.25.55)
     * @param newMax double The maximum value of the input data.
     */
    public void setDataMax(double newDataMax) {
        if ( datamax != newDataMax ) {
            datamax = newDataMax;
            super.fireDataChanged();
        }
    }
    /**
     * Sets the min value of the input data
     * Note : The DataMin and DataMax values should be set to 99999 and -99999 respectively if the
     * user requires that this plugin uses the min , max values found in the serie.
     * Creation date: (23/10/2000 23.25.32)
     * @param newDataMin double The minimum value of the input data.
     */
    public void setDataMin(double newDataMin) {
        if ( datamin != newDataMin ) {
            datamin = newDataMin;
            super.fireDataChanged();
        }
    }
    
}