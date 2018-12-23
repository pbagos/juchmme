package org.joone.util;

import org.joone.engine.*;
import java.util.Vector;

/**
 * <P>UnNormalizes the input data within a predefined range.
 * To enable the UnNormalizer to find the min and max within the input data specify zero
 * values for setInDataMin and setInDataMax.  To set user defined values for the input data
 * max and min in a serie then specify non-zero values for setInDataMin and setInDataMax.</P>
 * The PlugIn supports two modes - Buffered and UnBuffered. </P
 * </BR>
 * <P>Buffered Mode</P>
 * </BR>
 * <P>If the StreamOutputSynapse that this PlugIn is attached to is in buffered mode
 * then the PlugIn can either search for input data min/max values
 * if getInDataMin()==0 and getInDataMax()==0 or if either of these methods returns a
 * non-zero value then it will use these values.</P>
 * </BR>
 * <P>UnBuffered Mode </P>
 * </BR>
 * <P>If the StreamOutputSynapse that this PlugIn is attached to is not in buffered mode
 * then one of the methods {@link #setInDataMin(double newMin) setInDataMin} or {@link #setInDataMax(double newMax) setInDataMax} should have been
 * called with a non-zero value.  If this is not the case then this converter will not convert
 * any data.</P>
 * </BR>
 * @author Julien Norman
 */
public class UnNormalizerOutputPlugIn extends OutputConverterPlugIn {
    
    /** The Out Data Min property. */
    private double min = 0;
    /** The Out Data Max property. */
    private double max = 1;
    /** The In Data Max property. */
    private double datamax = 0;
    /** The In Data Min property. */
    private double datamin = 0;
    private transient double datadiff = 0;
    private transient double tmpmin = 0;
    private transient double tmpmax = 0;
    
    static final long serialVersionUID = 5322361123972428588L;
    /**
     * The default UnNormalizerOutputPlugIn constructor.
     */
    public UnNormalizerOutputPlugIn() {
        super();
    }
    
    /** <P>This constructor enables a new UnNormalizerOutputPlugin to be fully constructed during
     * initialisation.  The format of the Advanced Serie Selector parameter
     * newAdvSerieSel can be found in the javadoc documentation for {@link
     * org.joone.util.OutputConverterPlugIn#setAdvancedSerieSelector(String newAdvSerie)
     * setAdvancedSerieSelector} in the OutputConverterPlugIn class.</P>
     * @param newAdvSerieSel The new range of serie that should be converted by this plugin.
     * @param newInDataMin The minimum value to be found in the input data.
     * @param newInDataMax The maximum value to be found in the input data.
     * @param newOutDataMin The minimum value of the unnormalised output data.
     * @param newOutDataMax The maximum value of the unnormalised output data.
     */
    public UnNormalizerOutputPlugIn(String newAdvSerieSel,double newInDataMin,double newInDataMax,double newOutDataMin,double newOutDataMax) {
        super();
        setAdvancedSerieSelector(newAdvSerieSel);
        setInDataMin(newInDataMin);
        setInDataMax(newInDataMax);
        setOutDataMin(newOutDataMin);
        setOutDataMax(newOutDataMax);
    }
    
    /**
     * Gets the max output value
     * @return double The max output value
     */
    public double getOutDataMax() {
        return max;
    }
    /**
     * Gets the min output value
     * @return double The min output value
     */
    public double getOutDataMin() {
        return min;
    }
    /**
     * Sets the new max value for the output data set.
     * @param newMax double The new max value of the output data serie.
     */
    public void setOutDataMax(double newMax) {
        if ( max != newMax) {
            max = newMax;
            super.fireDataChanged();
        }
    }
    /**
     * Sets the new min value for the output data set.
     * @param newMin double The new min value of the output data serie.
     */
    public void setOutDataMin(double newMin) {
        if ( min != newMin ) {
            min = newMin;
            super.fireDataChanged();
        }
    }
    
    /**
     * Gets the max value of the input data set
     * @return double The max value of the input data set
     */
    public double getInDataMax() {
        return datamax;
    }
    /**
     * Gets the min value of the input data set
     * @return double The min value of the input data set
     */
    public double getInDataMin() {
        return datamin;
    }
    /**
     * Sets the max value of the input data set
     * @param newMax double The new max value of the input data serie.
     */
    public void setInDataMax(double newMax) {
        if ( datamax != newMax ) {
            datamax = newMax;
            super.fireDataChanged();
        }
    }
    /**
     * Sets the min value of the input data set
     * @param newMin double The new min value of the input data serie.
     */
    public void setInDataMin(double newMin) {
        if ( datamin != newMin ) {
            datamin = newMin;
            super.fireDataChanged();
        }
    }
    
    /** Provides buffer conversion support by converting the patterns in the buffer returned
     * by getInputVector().   If both getInDataMax and getInDataMin return 0 then this method will
     * search for the min/max values in the input data serie and it will use these values together
     * with the methods getOutDataMin and getOutDataMax to UnNormalize the serie.
     * @param serie The data serie with in the buffered patterns to convert.
     */
    protected boolean convert(int serie) {
        boolean retValue = false;
        Vector con_pats = getInputVector();
        double v = 0;
        int i = 0;
        int datasize = 0;
        datasize = con_pats.size();
        // Convert if pattern array not null
        if ( con_pats != null) {
            // Only convert if serie is positive or 0
            if ( serie >= 0 ) {
                if ( (datamax == 0) && (datamin == 0) )   // Find the data min max in the Patterns
                {
                    setupMinMax(serie,con_pats);
                }
                else {
                    tmpmin = datamin;
                    tmpmax = datamax;
                }
                datadiff = tmpmax - tmpmin;
                // Do the conversion
                for (i = 0; i < datasize; i++) {
                    if (datadiff != 0.0) {
                        v = getValuePoint(i, serie);
                        v = (v - tmpmin) / datadiff;
                        v = v * (getOutDataMax() - getOutDataMin()) + getOutDataMin();
                    }
                    else {
                        v = getOutDataMin();
                    }
                    ((Pattern)con_pats.elementAt(i)).setValue(serie,v);
                    retValue = true;
                }
            }
        }
        return retValue;
    }
    
    /** Converts a pattern indicated by getPattern() method.  Only if one of the methods
     * setInDataMin and setInDataMax have been called with non-zero values.
     * Note : No conversion will be perfomed if both getInDataMin()==0 and getInDataMax()==0.
     * @param serie The data serie with in the buffered patterns to convert.
     */
    protected void convert_pattern(int serie) {
        
        Pattern con_pat = getPattern();
        double v = 0;
        // Convert if pattern not null
        if ( con_pat != null ) {
            // Only convert if serie is positive or 0
            if ( serie >= 0 ) {
                if ( (datamax != 0) || (datamin != 0) )  // If user has over ridden these vales then we can do on a pattern basis
                {
                    datadiff = datamax - datamin;
                    // Do the conversion
                    if (datadiff != 0.0) {
                        v = (con_pat.getArray())[serie];
                        v = (v - datamin) / datadiff;
                        v = v * (getOutDataMax() - getOutDataMin()) + getOutDataMin();
                    }
                    else
                        v = getOutDataMin();
                    con_pat.setValue(serie,v);
                }
                return;
            }
        }
    }
    
    /**
     * Find the min and max values for the specified serie in the buffer specified
     * by pats_to_convert.
     */
    private void setupMinMax(int serie, Vector pats_to_convert) {
        int datasize = pats_to_convert.size();
        int i;
        double v, d;
        tmpmax = getValuePoint(0, serie);
        tmpmin = tmpmax;
        Pattern currPE;
        for (i = 0; i < datasize; i++) {
            v = getValuePoint(i, serie);
            if (v > tmpmax)
                tmpmax = v;
            else
                if (v < tmpmin)
                    tmpmin = v;
        }
    }
}