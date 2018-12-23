package org.joone.engine;

/**
 * The pattern object contains the data that must be processed from a neural net.
 * It can contain the input data or the training data.
 */
public class Pattern implements java.lang.Cloneable, java.io.Serializable
{
    private int count;
    private double[] array;
    
    /* outArray: used for some unsupervised learning algorithm, where the
     * receiving synapse needs to know the activation of the output
     * connected layer.
     * Added on Aug 22, 2002 by P.Marrone
     */
    private double[] outArray;
    
    private static final long serialVersionUID = -609786590797838450L;


    /** 
     * Default Constructor
     *  Added for Save As XML
     */
    public Pattern() {
      super();
    }
    
    public Pattern(double[] arr)
    {
        super();
        array = arr;
    }
    public synchronized Object clone()
    {
        Pattern cPat = new Pattern((double[])this.array.clone());
        if (outArray != null)
            cPat.setOutArray((double[])this.outArray.clone());
        cPat.count = this.count;
        return cPat;
    }
    public synchronized double[] getArray()
    {
        // return (double[])array.clone();
        return array;
    }
    public synchronized int getCount()
    {
        return count;
    }
    public synchronized void setArray(double[] arr)
    {
        array = (double[])arr.clone();
    }
    public synchronized void setCount(int n)
    {
        count = n;
    }
    
    public void setValue(int point, double value) {
        array[point] = value;
    }
    
    /** Getter for property outArray.
     * @return Value of property outArray.
     */
    public double[] getOutArray() {
        return (double[])this.outArray.clone();
    }
    
    /** Setter for property outArray.
     * @param outArray New value of property outArray.
     */
    public void setOutArray(double[] outArray) {
        this.outArray = (double[])outArray.clone();
    }
    
    /**
     * Getter for property values.
     * Added for XML serialization
     * @return Value of property values.
     */
    public double[] getValues() {
        return this.array;
    }
    
    /**
     * Setter for property values.
     * Added for XML serialization
     * @param values New value of property values.
     */
    public void setValues(double[] values) {
        this.array = values;
    }
    
}