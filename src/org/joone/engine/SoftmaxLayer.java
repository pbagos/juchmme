/*
 * SoftmaxLayer.java
 *
 * Created on 11 January 2006, 22.19
 *
 */

package org.joone.engine;

/**
 * The outputs of the Softmax layer must be interpreted as probabilities.
 * The output of each node, in fact, ranges from 0 and 1, and
 * the sum of all the nodes is always 1.
 * Useful to implement the 1 of C classification network.
 *
 * @author P.Marrone
 */
public class SoftmaxLayer extends LinearLayer {
    
    private static final long serialVersionUID = 2243109263560495355L;
    
    /** Creates a new instance of SoftmaxLayer */
    public SoftmaxLayer() {
        super();
    }
    
    public void forward(double[] pattern) {
        int x;
        int n = getRows();
        double sum = 0;
        for (x = 0; x < n; ++x) {
            outs[x] = Math.exp(getBeta() * pattern[x]);
            sum += outs[x];
        }
        for (x = 0; x < n; ++x) {
            outs[x] = outs[x] / sum;
        }
    }
}
