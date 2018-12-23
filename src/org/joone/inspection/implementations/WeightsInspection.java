/*
 * User: Harry Glasgow
 * Date: 12/12/2002
 * Time: 18:39:22
 * Class to get bias values for a Layer.
 */
package org.joone.inspection.implementations;

import org.joone.inspection.Inspection;
import org.joone.engine.Matrix;

public class WeightsInspection implements Inspection {
    
    private Matrix weights;
    
    public WeightsInspection(Matrix weightsArg) {
        weights = weightsArg;
    }
    
    public Object[][] getComponent() {
        if (weights != null && weights.getValue() != null) {
            double[][] values = weights.getValue();
            if (values.length > 0 && values[0].length > 0) {
                Object[][] bigValues = new Object[values.length][values[0].length];
                for (int i = 0; i < values[0].length; i++) {
                    for (int j = 0; j < values.length; j++) {
                        bigValues[j][i] = new Double(values[j][i]);
                    }
                }
                return bigValues;
            }
        }
        return null;
    }
    
    public String getTitle() {
        return "Weights";
    }
        /* (non-Javadoc)
         * @see org.joone.inspection.Inspection#getNames()
         */
    public Object[] getNames() {
        if (weights == null)
            return null;
        double[][] values = weights.getValue();
        if (values.length > 0 && values[0].length > 0) {
            Object[] names = new String[values[0].length];
            for (int i = 0; i < values[0].length; i++) {
                names[i] = "Column "+i;
            }
            return names;
        } else
            return null;
    }
    
        /* (non-Javadoc)
         * @see org.joone.inspection.Inspection#rowNumbers()
         */
    public boolean rowNumbers() {
        return false;
    }
    
    public void setComponent(java.lang.Object[][] newValues) {
        double[][] values = weights.getValue();
        for (int x=0; (x < values.length) && (x < newValues.length); ++x)
            for (int y=0; (y < values[0].length) && (y < newValues[0].length); ++y)
                values[x][y] = ((Double)(newValues[x][y])).doubleValue();
    }
    
}
