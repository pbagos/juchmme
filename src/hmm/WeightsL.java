package hmm;

import java.util.*;

class WeightsL
{
    double[] wtsL;
    private int arrayLen;

    WeightsL(int arrayLen)
    {
        this.arrayLen = arrayLen;
        wtsL = new double[this.arrayLen];
        Initialize();
    }

    public void Initialize()
    {
        for(int i=0; i < this.arrayLen;i++)
            wtsL[i] = 1;
    }

    public double getWeightL( int i )
    {
        return wtsL[i];
    }
    public void setWeightL( int i,double val)
    {
        wtsL[i] = val;
    }
}