package org.joone.engine;

public class FullSynapse extends Synapse implements LearnableSynapse 
{
    private static final long serialVersionUID = 5518898101307425554L;    
    
    public FullSynapse() {
        super();
        learnable = true;
    }
    
    protected void backward(double[] pattern) {
        int x;
        int y;
        double s;
        int m_rows = getInputDimension();
        int m_cols = getOutputDimension();
        setLearningRate(getMonitor().getLearningRate());
        // Weights adjustement
        for (x=0; x < m_rows; ++x) {
            s = 0;
            for (y=0; y < m_cols; ++y) {
                s += pattern[y] * array.value[x][y];
            }
            bouts[x] = s;
        }
		myLearner.requestWeightUpdate(pattern, inps);
    }
    
    protected void forward(double[] pattern) {
        int x;
        int y;
        double s;
        int m_rows = getInputDimension();
        int m_cols = getOutputDimension();
        
        for (y=0; y < m_cols; ++y) {
            s = 0;
            for (x=0; x < m_rows; ++x) {
                s += pattern[x] * array.value[x][y];
            }
            outs[y] = s;
        }
    }
    /**
     * setArrays method comment.
     */
    protected void setArrays(int rows, int cols) {
        inps = new double[rows];
        outs = new double[cols];
        bouts = new double[rows];
    }
    
    protected void setDimensions(int rows, int cols) {
        int icols, irows;
        int m_rows = getInputDimension();
        int m_cols = getOutputDimension();
        if (rows == -1)
            irows = m_rows;
        else
            irows = rows;
        
        if (cols == -1)
            icols = m_cols;
        else
            icols = cols;
        array = new Matrix(irows, icols);
        setArrays(irows, icols);
    }
    
    /** @deprecated - Used only for backward compatibility
     */
    public Learner getLearner() {
        learnable = true;
        return super.getLearner();
    }
    
}