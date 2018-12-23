package org.joone.util;



import org.joone.engine.*;

/**
 * Extracts from the time series the inversion points, setting its range
 * from MIN_VALUE to MAX_VALUE.
 * Example:
 * Input series: (doesn't matter the range of the input values)
 *        /\
 *  /\   /  \/
 * /  \/
 * /
 *
 * Output series: (normalized in the range MIN_VALUE - MAX_VALUE)
 * MAX_VALUE------------
 *  /\    /\
 * / \   / \
 * /   \/    \/
 * MIN_VALUE------------
 *
 * (29/09/00 13.47.22)
 * @author: Administrator
 */
public class MinMaxExtractorPlugIn extends ConverterPlugIn {
    private final double MIN_VALUE = 0.05;
    private final double MAX_VALUE = 0.95;
    private final double MID_VALUE = (MAX_VALUE - MIN_VALUE) / 2 + MIN_VALUE;
    private double minPerc = 0.01;
    
    private static final long serialVersionUID = 6835532403570359948L;
    
    class PointValue {
        double value;
        int point;
        PointValue() {
        }
        PointValue(PointValue c) {
            value = c.value;
            point = c.point;
        }
        PointValue(int p, double v) {
            value = v;
            point = p;
        }
        boolean equal(PointValue compare) {
            if (compare == null)
                return false;
            else
                return (compare.point == point);
        }
    }
    /**
     * constructor MinMaxExtractorSynapse.
     */
    public MinMaxExtractorPlugIn() {
        super();
    }
    /**
     * Executes the extraction of the inversion points
     * (29/09/00 15.12.32)
     * @param serie int
     */
    protected boolean convert(int serie) {
        boolean retValue = false;
        int pf = 0;
        double[] values;
        boolean up;
        PointValue lastInserted = null;
        PointValue lastInserting;
        java.util.Vector changePoints = new java.util.Vector();
        PointValue pMax = new PointValue(0, getValuePoint(0, serie));
        PointValue pMin = new PointValue(0, getValuePoint(0, serie));
        PointValue pCur = new PointValue();
        int s = getInputVector().size();
        if (s > 1) {
            // Init
            if (getValuePoint(1, serie) > pMax.value)
                up = true;
            else
                up = false;
        } else
            return false;
        while (pf < (s - 1)) {
            if (up) {
                pCur.point = pf;
                pCur.value = getValuePoint(pf, serie);
                pf = findMax(pCur, serie);
                if (!pMin.equal(lastInserted)) {
                    if (minPerc(getValuePoint(pf, serie), pMin.value)) {
                        // Il pMin points to a valid relative min
                        lastInserting = new PointValue(pMin.point, MIN_VALUE);
                        changePoints.addElement(lastInserting);
                        writeMidPoints(lastInserted, lastInserting, serie);
                        retValue = true;
                        lastInserted = new PointValue(lastInserting);
                        // The found turning point becomes a possible pMax
                        pMax.point = pf;
                        pMax.value = getValuePoint(pf, serie);
                    }
                }
                if (pMax.value < getValuePoint(pf, serie)) {
                    pMax.point = pf;
                    pMax.value = getValuePoint(pf, serie);
                }
            }
            up = false;
            pCur.point = pf;
            pCur.value = getValuePoint(pf, serie);
            pf = findMin(pCur, serie);
            if (!pMax.equal(lastInserted)) {
                if (minPerc(getValuePoint(pf, serie), pMax.value)) {
                    // pMax points to a valid relative max
                    lastInserting = new PointValue(pMax.point, MAX_VALUE);
                    changePoints.addElement(lastInserting);
                    writeMidPoints(lastInserted, lastInserting, serie);
                    retValue = true;
                    lastInserted = new PointValue(lastInserting);
                    // il punto di inversione trovato diventa un pMin presunto
                    pMin.point = pf;
                    pMin.value = getValuePoint(pf, serie);
                }
            }
            if (pMin.value > getValuePoint(pf, serie)) {
                pMin.point = pf;
                pMin.value = getValuePoint(pf, serie);
            }
            up = true;
        }
        // Converts the values on the last line
        Pattern currPE;
        if (lastInserted.point < (s - 1)) {
            writeMidPoints(lastInserted, new PointValue(s - 1, MID_VALUE), serie);
            currPE = (Pattern) getInputVector().elementAt(s - 1);
            currPE.setValue(serie, MID_VALUE);
            retValue = true;
        } else {
            currPE = (Pattern) getInputVector().elementAt(s - 1);
            currPE.setValue(serie, lastInserted.value);
            retValue = true;
        }
        return retValue;
    }
    /**
     * Find the next down-turning point starting from the pInit.point
     * Returns the next point having the max value
     *                /\ --> return
     *               /  \
     *              /
     *    pInit -->/
     * Data di creazione: (29/09/00 15.50.35)
     */
    private int findMax(PointValue pInit, int serie) {
        int i = pInit.point + 1;
        int s = getInputVector().size();
        double lastValue = pInit.value;
        while (i < s) {
            if (getValuePoint(i, serie) >= lastValue) {
                lastValue = getValuePoint(i, serie);
                ++i;
            } else
                break;
        }
        return --i;
    }
    /**
     * Find the next up-turning point starting from the pInit.point
     * Returns the next point having the min value
     *    pInit --> \
     *               \
     *                \  /
     *                 \/ --> return
     * (29/09/00 15.50.35)
     */
    private int findMin(PointValue pInit, int serie) {
        int i = pInit.point + 1;
        int s = getInputVector().size();
        double lastValue = pInit.value;
        while (i < s) {
            if (getValuePoint(i, serie) <= lastValue) {
                lastValue = getValuePoint(i, serie);
                ++i;
            } else
                break;
        }
        return --i;
    }
    /**
     * Get the min percentage of change accepted to consider
     * a point as a turning point
     * (29/09/00 18.46.53)
     * @return double
     */
    public double getMinChangePercentage() {
        return minPerc;
    }
    /**
     * Return TRUE if the percentage of change from first to last points is 
     * greater than minPerc
     * (29/09/00 18.48.20)
     * @return boolean
     * @param last double
     * @param first double
     */
    private boolean minPerc(double last, double first) {
        double perc = (last - first) / first;
        if (perc < 0.0)
            perc = perc * -1;
        if (perc < minPerc)
            return false;
        else
            return true;
    }
    /**
     * Set the min percentage of change accepted to consider
     * a point as a turning point
     * (29/09/00 18.46.53)
     * @param newMinPerc double
     */
    public void setMinChangePercentage(double newMinPerc) {
	if ( minPerc != newMinPerc )
	{
        		minPerc = newMinPerc;
        		super.fireDataChanged();
	}
    }
    /**
     * Fill all the points from pointA to pointB calculating the intermediate values
     * (01/10/2000 19.18.11)
     */
    private void writeMidPoints(PointValue pointA, PointValue pointB, int serie) {
        int i;
        double vi;
        double vmin, vmax, vdif;
        double rmin, rmax, rdif;
        Pattern currPE;
        if (pointA == null) {
            i = 0;
            vi = MID_VALUE;
        } else {
            i = pointA.point;
            vi = pointA.value;
        }
        if (vi < pointB.value) {
            rmin = vi;
            rmax = pointB.value;
        } else {
            rmax = vi;
            rmin = pointB.value;
        }
        if (getValuePoint(i, serie) < getValuePoint(pointB.point, serie)) {
            vmin = getValuePoint(i, serie);
            vmax = getValuePoint(pointB.point, serie);
        } else {
            vmax = getValuePoint(i, serie);
            vmin = getValuePoint(pointB.point, serie);
        }
        rdif = rmax - rmin;
        vdif = vmax - vmin;
        while (i < pointB.point) {
            vi = (getValuePoint(i, serie) - vmin) / vdif;
            vi = vi * rdif + rmin;
            currPE = (Pattern) getInputVector().elementAt(i);
            currPE.setValue(serie, vi);
            ++i;
        }
    }
}