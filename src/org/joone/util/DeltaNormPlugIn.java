/*
 * DeltaNormPlugin.java
 *
 * Created on September 10, 2004, 11:13 AM
 */

package org.joone.util;

import org.joone.engine.Pattern;
/**
 * This plugin calculates the Delta Normalization on a time series.
 * the Delta Normalization technique permits to feed a neural network
 * with the delta values instead of the absolute values of the series,
 * permitting in this manner to avoid the problems correlated with both
 * ascending and descending trends.
 * The normalization is obtained by dividing the delta value by
 * the dynamic range (named Probability Volatility Windows) calculated
 * on the given period. This plugin is very useful for financial predictions
 * when used in combination with the MinMaxExtractorPlugin.
 * To learn more on this technique read the excellent book:
 * "Financial Prediction Using Neural Networks" by Joseph S. Zirilli
 * (you can buy it in electronic format at http://www.mjfutures.com/Book.htm)
 *
 * @author  P.Marrone
 */
public class DeltaNormPlugIn extends ConverterPlugIn {
    private static final long serialVersionUID = 1698511686417955514L;
    private transient double[] probVolWin; // Probablity Volatility Window
    
    /** Creates a new instance of DeltaNormPlugin */
    public DeltaNormPlugIn() {
    }
    
    protected boolean convert(int serie) {
        boolean retValue = false;
        int currInput = getSerieIndexNumber(serie);
        if (getProbVolWin()[currInput] == 0)
            getProbVolWin()[currInput] = calculatePVW(currInput+1, serie);
        int init = getSerieSelected().length;
        for (int i=getInputVector().size()-1; i >= init ; --i) {
            double vi = getDelta(i, currInput+1, serie, false);
            vi /= getProbVolWin()[currInput];
            Pattern currPE = (Pattern) getInputVector().elementAt(i);
            currPE.setValue(serie, vi);
            retValue = true;
        }
        return retValue;
    }
    
    /* Calculates the Probability Volatility Windows for the serie
     * pvw = 2 * avg(sum[]((Ci - f(delay,i))/Ci))
     */
    protected double calculatePVW(int delay, int serie) {
        int init = getSerieSelected().length;
        double sum = 0;
        for (int i=init; i < getInputVector().size(); ++i) {
            sum += getDelta(i, delay, serie, true);
        }
        int numPoints = getInputVector().size() - init;
        sum = sum / numPoints; // Average
        return sum * 2;
    }
    
    /* Calculates the formula (Ci - f(i,delay))/Ci */
    protected double getDelta(int index, int delay, int serie, boolean abs) {
        double Ci = getValuePoint(index, serie);
        double fd = funcDelta(index, delay, serie);
        if (Ci == 0) {
            if (!abs)
                return -fd/Math.abs(fd);
            else
                return fd/fd;
        }
        if (!abs)
            return (Ci - fd)/Ci;
        else
            return Math.abs(Ci-fd)/Ci;
    }
    
    /** Calculates f(i,delay) used by getDelta
     * >>>> This method can be overriden in order to implement
     * different volatility window algorithms
     */
    protected double funcDelta(int index, int delay, int serie) {
        return getValuePoint(index - delay, serie);
    }
    
    /**
     * Getter for property probVolWin, the
     * array containing the Probability Volatility Window
     * @return Value of property probVolWin.
     */
    private double[] getProbVolWin() {
        if ((probVolWin == null) || (probVolWin.length != getSerieSelected().length)) {
            probVolWin = new double[getSerieSelected().length];
        }
        return this.probVolWin;
    }
}
