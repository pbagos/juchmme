/*
 * SangerSynapse.java
 *
 * Created on 10 ottobre 2002, 23.26
 */

package org.joone.engine;

/**
 * This is the synapse useful to extract the principal components 
 * from an input data set.
 * This synapse implements the so called Sanger PCA algorithm.
 * @author  pmarrone
 */
public class SangerSynapse extends FullSynapse {
    
    private static final long serialVersionUID = 1417085683178232377L;
    
    /** Creates a new instance of SangerSynapse */
    public SangerSynapse() {
        super();
        learnable = false;
    }
    
    /** Training Function
     * @param pattern double[] - Input pattern used to calculate the weight's modifications
     *
     */
    protected void backward(double[] pattern) {
        int x, y;
        double dw, s;
        double[] outArray;
        
        outArray = b_pattern.getOutArray();
        // Weights adjustement
        int m_rows = getInputDimension();
        int m_cols = getOutputDimension();
        for (x = 0; x < m_rows; ++x) {
            for (s=0, y=0; y < m_cols; ++y) {
                s += array.value[x][y] * outArray[y];
                dw = getLearningRate() * outArray[y];
                dw = dw * (inps[x] - s);
                array.value[x][y] += dw;
                array.delta[x][y] = dw;
            }
        }
    }
    
    /** @deprecated - Used only for backward compatibility
     */
    public Learner getLearner() {
        learnable = false;
        return super.getLearner();
    }
    
}
