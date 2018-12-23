package org.joone.engine;

import java.util.ArrayList;
import java.util.Collection;
import org.joone.net.NetCheck;

import java.util.TreeSet;
import org.joone.inspection.implementations.BiasInspection;


/** Delay unit to create temporal windows from time series <BR>
 * <CODE>
 * O---> Yk(t-N)      <BR>
 * |                  <BR>
 * ...                <BR>
 * |                  <BR>
 * O---> Yk(t-1)      <BR>
 * |                  <BR>
 * O---> Yk(t)        <BR>
 * |                  <BR>
 * |<--------- Xk(t)  <BR>
 * </CODE>
 * <BR>
 * Where:<BR>
 * Xk = Input signal <BR>
 * Yk(t)...Yk(t-N+1) = Values of the output temporal window <BR>
 * N = taps
 */
public class DelayLayer extends MemoryLayer {

    private static final long serialVersionUID = 1547734529107850525L;

    /** Constructor method
     */
    public DelayLayer() {
        super();
    }

    /** Constructor method
     * @param ElemName The layer's name
     */
    public DelayLayer(java.lang.String ElemName) {
        super(ElemName);
    }

    protected void backward(double[] pattern) {
        int x;
        int y;
        int ncell;
        int length = getRows();
        for (x = 0; x < length; ++x) {
            ncell = x;
            for (y = 0; y < getTaps(); ++y) {
                backmemory[ncell] = backmemory[ncell + length];
                backmemory[ncell] += pattern[ncell];
                ncell += length;
            }
            backmemory[ncell] = pattern[ncell];
            gradientOuts[x] = backmemory[x];
        }
    }

    protected void forward(double[] pattern) {
        int x;
        int y;
        int ncell;
        int length = getRows();
        for (x = 0; x < length; ++x) {
            ncell = x + getTaps() * length;
            for (y = getTaps(); y > 0; --y) {
                memory[ncell] = memory[ncell - length];
                outs[ncell] = memory[ncell];
                ncell -= length;
            }
            memory[x] = pattern[x];
            outs[x] = memory[x];
        }
    }

    public TreeSet check() {
        TreeSet checks = super.check();
        if (getTaps() == 0) {
            checks.add(new NetCheck(NetCheck.FATAL, "The Taps parameter cannot be equal to zero.", this));
        }

        if (monitor != null && monitor.getPreLearning() != getTaps() + 1) {
            checks.add(new NetCheck(NetCheck.WARNING, "The correct value for the Monitor PreLearning parameter is Taps + 1", this));
        }

        return checks;
    }
    
    /**
     * It doesn't make sense to return biases for this layer
     * @return null
     */
    public Collection Inspections() {
        Collection col = new ArrayList();
        col.add(new BiasInspection(null));
        return col;
    }
    
}