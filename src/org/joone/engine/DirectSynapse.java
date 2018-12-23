package org.joone.engine;

import org.joone.net.NetCheck;

import java.util.TreeSet;

public class DirectSynapse extends Synapse {
    
    private static final long serialVersionUID = 3079898042708755094L;
    
    protected void backward(double[] pattern) {
        // Never called. See revPut()
    }
    
    protected void forward(double[] pattern) {
        outs = pattern;
    }
    /**
     * setArrays method comment.
     */
    protected void setArrays(int rows, int cols) {
        inps = new double[rows];
        outs = new double[rows];
        bouts = new double[rows];
    }
    protected void setDimensions(int rows, int cols) {
        if (rows > -1)
            setArrays(rows, rows);
        else
            if (cols > -1)
                setArrays(cols, cols);
    }
    
    public void revPut(Pattern pattern) {
    }
    
    public Pattern revGet() {
        return null;
    }

    public TreeSet check() {
        TreeSet checks = super.check();

        if (getInputDimension() != getOutputDimension()) {
            checks.add(new NetCheck(NetCheck.FATAL, "Connected layers are not the same size.", this));
        }

        return checks;
    }

}