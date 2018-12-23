/*
 * User: Harry Glasgow
 * Date: 12/12/2002
 * Time: 18:39:22
 * Class to get bias values for a Layer.
 */

package org.joone.inspection.implementations;

import org.joone.inspection.Inspection;
import org.joone.engine.Pattern;
import java.util.*;

public class InputsInspection implements Inspection {
    
    private Vector inputs = null;
    public InputsInspection(Vector inputsArg) {
        inputs = inputsArg;
    }
    
    public Object[][] getComponent() {
        if ((inputs != null) && (inputs.size() > 0)) {
            Object[][] bigValues = null;
            for (int i = 0; i < inputs.size(); i++) {
                Pattern pattern = (Pattern)inputs.elementAt(i);
                double [] array = pattern.getArray();
                if (bigValues == null) {
                    bigValues =
                    new Object[inputs.size()][array.length + 1];
                }
                for (int j = 1; j < array.length + 1; j++) {
                    Double d = new Double(array[j - 1]);
                    bigValues[i][j] = d;
                }
            }
            for (int i = 0; i < inputs.size(); i++) {
                bigValues[i][0] = new Integer(i + 1);
            }
            return bigValues;
            
        } else {
            return null;
        }
    }
    
    public String getTitle() {
        return "Inputs";
    }
    
        /* (non-Javadoc)
         * @see org.joone.inspection.Inspection#getNames()
         */
    public Object[] getNames() {
        if ((inputs != null) && (inputs.size() > 0)) {
            Object[] names = null;
            for (int i = 0; i < inputs.size(); i++) {
                Pattern pattern = (Pattern)inputs.elementAt(i);
                double[] array = pattern.getArray();
                if (names == null) {
                    names = new String[array.length + 1];
                }
                names[0] = "Row Number";
                for (int j = 1; j < array.length + 1; j++) {
                    names[j] = "Column " + j;
                }
            }
            return names;
            
        } else {
            return null;
        }
    }
    
        /* (non-Javadoc)
         * @see org.joone.inspection.Inspection#rowNumbers()
         */
    public boolean rowNumbers() {
        return true;
    }
    
    public void setComponent(java.lang.Object[][] newValues) {
        for (int x=0; (x < inputs.size()) && (x < newValues.length); ++x) {
            double[] values = ((Pattern)inputs.elementAt(x)).getArray();
            int n = ((Pattern)inputs.elementAt(x)).getCount();
            for (int y=0; (y < values.length) && (y < (newValues[0].length - 1)); ++y) {
                values[y] = ((Double)newValues[x][y+1]).doubleValue();
            }
            Pattern patt = new Pattern(values);
            patt.setCount(n);
            inputs.setElementAt(patt, x);
        }
    }
    
}
