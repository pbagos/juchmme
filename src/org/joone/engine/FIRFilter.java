package org.joone.engine;

/** Element of a connection representing a FIR filter (Finite Impulse Response).
 * The DelaySynapse object implements a delayed full synapse where each connection
 * is implemented with a FIRFilter object.
 *
 * In this connection is implemented the temporal backpropagation algorithm 
 * by Eric A. Wan, as in 'Time Series Prediction by Using a Connectionist Network 
 * with Internal Delay Lines' in Time Series Prediction. Forecasting the Future and
 * Understanding the Past, by A.Weigend and N.Gershenfeld. Addison-Wesley, 1994. 
 *
 * @author P.Marrone
 * @see org.joone.engine.DelaySynapse
 */
//          1   2       N
// Xk(t) -->O--->O...-->O
//          |    |      |
//          Wk1  Wk2    Wkn
//          |    |      |
//          |    |      |
//          -------------->(+)--> Yk
//
// Where: Yk = Xk(t)Wk1 + Xk(t-1)Wk2 +..+ Xk(t-n+1)Wkn
// n = taps (the delay of the connection)

public class FIRFilter implements java.io.Serializable {
    protected int m_taps;
    protected double[] memory;
    protected double[] backmemory;
    protected double[] outs;
    protected double[] bouts;
    protected Matrix array;
    
    public double lrate;
    public double momentum;
    
    private static final long serialVersionUID = 2539307324689626619L;
    
    public FIRFilter(int taps) {
        outs = new double[taps];
        bouts = new double[taps];
        memory = new double[taps];
        backmemory = new double[taps];
        array = new Matrix(taps, 1);
        m_taps = taps - 1;
    }
    
    public void addNoise(double amplitude) {
        array.addNoise(amplitude);
    }
    
    protected double backDelay(double[] pattern) {
        int y;
        for (y = 0; y < m_taps; ++y) {
            backmemory[y] = backmemory[y + 1];
            backmemory[y] += pattern[y];
        }
        backmemory[m_taps] = pattern[m_taps];
        return backmemory[0];
    }
    
    protected double[] backFilter(double input) {
        int x;
        double dw;
        
        // Weights adj
        for (x=0; x <= m_taps; ++x) {
            bouts[x] = input * array.value[x][0];
            dw = lrate * input * outs[x] + momentum * array.delta[x][0];
            array.value[x][0] += dw;
            array.delta[x][0] = dw;
        }
        return bouts;
    }
    public double backward(double input) {
        return backDelay(backFilter(input));
    }
    
    protected double[] Delay(double input) {
        int y;
        for (y = m_taps; y > 0; --y) {
            memory[y] = memory[y - 1];
            outs[y] = memory[y];
        }
        memory[0] = input;
        outs[0] = input;
        return outs;
    }
    
    protected double Filter(double[] pattern) {
        int x;
        double s = 0;
        
        for (x=0; x <= m_taps; ++x) {
            s += pattern[x] * array.value[x][0];
        }
        return s;
    }
    
    public double forward(double input) {
        return Filter(Delay(input));
    }
}