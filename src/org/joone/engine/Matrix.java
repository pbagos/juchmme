package org.joone.engine;

import java.io.*;
import org.joone.engine.weights.*;
import org.joone.log.*;

/**
 * The Matrix object represents the connection matrix of the weights of a synapse
 * or the biases of a layer. In case of a synapse, it contains the weight of each
 * connection. In case of a layer, it contains the bias of each neuron.
 * <p>
 * Besides the weights or biases, it holds the last modification (update value or
 * delta) and 2 boolean values indicating whether the weight is on or off and 
 * trainable or fixed.
 */

public class Matrix implements Serializable, Cloneable {
    
    /** Logger for this class. */
    private static final ILogger log = LoggerFactory.getLogger(Matrix.class);
    
    /** This constant defines the boundaries of the default domain used for
     * weight initialization. Weights or biases are initialised by default
     * with a random value in the domain 
     * <code>[-DEFAULT_INITIAL, DEFAULT_INITIAL]</code>. Although different 
     * boundaries or even different weight intialization can be used by calling
     * differnt constructors that that a <code>WeightInitializer</code> class
     * as parameter or by calling the method {@link initialize()}.
     */
    public static final double DEFAULT_INITIAL = 0.2;
    
    private static final long serialVersionUID = -1392966842649908366L;
    
    /** The values of the weights / biases. */
    public double[][] value;
    
    /** The value of the last modification, i.e. the last update. */
    public double[][] delta;
    
    /** Flag indicating whether the weight is on or off. */
    public boolean[][] enabled;
    
    /** Flag indicating whether the weight is fixed or trainable / adjustable. */
    public boolean[][] fixed;
    
    /** The number of rows. That is, in case of weights, the number of neurons on 
     * the input side of the synapse. In case of biases, the number of neurons. */
    protected int m_rows;
    
    /** The number of columns. That is, in case of weights, the number of neurons on
     * the output side of the synapse. In case of biases, the value equals 0. */
    protected int m_cols;
    
    /** The weight initializer that is used by this class. */
    protected WeightInitializer weightInitializer;
        
    /**
     * Default constructor
     * Needed for Save as XML
     */
    public Matrix() {
    }
    
    /**
     * This constructur creates a weights or biases according to the values
     * <code>aRows</code> and <code>aColumns</code>. The weights or biases
     * are initialised with a random value in the domain of 
     * <code>[-DEFAULT_INITIAL, DEFAULT_INITIAL]</code>.
     *
     * @param aRows the number of rows (the number of neurons on the input side
     * of a synapse or the number of biases).
     * @param aColumns the number of colums (the number of neurons on the output
     * side of a synapse or zero in case of biases).
     */
    public Matrix(int aRows, int aColumns) {
        this(aRows, aColumns, DEFAULT_INITIAL);
    }
    
    /**
     * This constructur creates a weights or biases according to the values
     * <code>aRows</code> and <code>aColumns</code>. And the weights or biases
     * are initialized with a random value in the domain of 
     * <code>[-anInitial, anInitial]</code>.
     *
     * @param aRows the number of rows (the number of neurons on the input side
     * of a synapse or the number of biases).
     * @param aColumns the number of colums (the number of neurons on the output
     * side of a synapse or zero in case of biases).
     * @param anInitial the boundary of the domain within these weights or biases
     * shoud be randomly initialized.
     */
    public Matrix(int aRows, int aColumns, double anInitial) {
        value = new double[aRows][aColumns];
        delta = new double[aRows][aColumns];
        enabled = new boolean[aRows][aColumns];
        fixed = new boolean[aRows][aColumns];
        
        m_rows = aRows;
        m_cols = aColumns;
        if(anInitial == 0.0) {
            enableAll();
            unfixAll();
            setWeightInitializer(new RandomWeightInitializer(0), false);
            clear();
        } else {
            enableAll();
            unfixAll();
            setWeightInitializer(new RandomWeightInitializer(anInitial));
        }
    }
    
    /**
     * Initializes the weights or biases by making a call to the weight initializer.
     * The weight initializer can be set through {@link setWeightInitializer(WeightInitializer)}
     */
    public void initialize() {
        getWeightInitializer().initialize(this);
    }
    
    /**
     * Sets the weight initializer and initializes the weights. This function calls 
     * setWeightInitializer(aWeightInitializer, true).
     *
     * @param aWeightInitializer the weight initializer to set.
     */
    public void setWeightInitializer(WeightInitializer aWeightInitializer) {
        setWeightInitializer(aWeightInitializer, true);    
    }
    
    /**
     * Sets the weight initializer.
     *
     * @param aWeightInitializer the weight initializer to set.
     * @param anInitialize if true the weights will be initialized by the new
     * weight initializer, if false the weights will not be initialized.
     */
    public void setWeightInitializer(WeightInitializer aWeightInitializer, boolean anInitialize) {
        weightInitializer = aWeightInitializer;
        if(anInitialize) {
            getWeightInitializer().initialize(this);
        }
    }
    
    /**
     * Gets the weight initializer.
     *
     * @return the weight initializer that is set for this matrix.
     */
    public WeightInitializer getWeightInitializer() {
        if (weightInitializer == null)
            // Added for backward compatibility
            weightInitializer = new RandomWeightInitializer(0.2);
        return weightInitializer;
    }
    
    /**
     * Clones this matrix object. It returns a copy of this matrix object.
     *
     * @return a copy of the current matrix object.
     */
    public Object clone() {
        Matrix o = null;
        try {
            o = (Matrix)super.clone();
        } catch(CloneNotSupportedException e) {
            log.error("Matrix can't clone", e);
        }
        o.value = (double[][])o.value.clone();
        o.delta = (double[][])o.delta.clone();
        o.enabled = (boolean[][])o.enabled.clone();
        o.fixed = (boolean[][])o.fixed.clone();
        for (int x = 0; x < m_rows; ++x) {
            o.value[x] = (double[])o.value[x].clone();
            o.delta[x] = (double[])o.delta[x].clone();
            o.enabled[x] = (boolean[])o.enabled[x].clone();
            o.fixed[x] = (boolean[])o.fixed[x].clone();
        }
        return o;
    }
    
    /**
     * Adds noise to the weights. The noise that is added to the weights is
     * within the domain <code>[-amplitude, amplitude]</code>.
     *
     * @param amplitude defines the domain of noise.
     */
    public void addNoise(double amplitude) {
        for (int x = 0; x < m_rows; ++x) {
            for (int y = 0; y < m_cols; ++y) {
                if (enabled[x][y] && !fixed[x][y]) {
                    value[x][y] += (-amplitude + Math.random() * (2 * amplitude));
                }
            }
        }
    }
    
    /**
     * Removes a row.
     *
     * @param aRow the row to remove.
     */
    public void removeRow(int aRow) {
        double [][] myValue = new double[m_rows - 1][];
        double [][] myDelta = new double[m_rows - 1][];
        boolean [][] myEnabled = new boolean[m_rows - 1][];
        boolean [][] myFixed = new boolean[m_rows - 1][];
        
        for(int x = 0; x < m_rows; x++) {
            if(x < aRow) {
                myValue[x] = (double[])value[x].clone();
                myDelta[x] = (double[])delta[x].clone();
                myEnabled[x] = (boolean[])enabled[x].clone();
                myFixed[x] = (boolean[])fixed[x].clone();
            } else if(x > aRow) {
                myValue[x - 1] = (double[])value[x].clone();
                myDelta[x - 1] = (double[])delta[x].clone();
                myEnabled[x - 1] = (boolean[])enabled[x].clone();
                myFixed[x - 1] = (boolean[])fixed[x].clone();
            }
        }
        value = myValue;
        delta = myDelta;
        enabled = myEnabled;
        fixed = myFixed;
        m_rows--;
    }
    
    /**
     * Removes a column.
     *
     * @param aColumn the column to remove.
     */
    public void removeColumn(int aColumn) {
        double [][] myValue = new double[m_rows][m_cols - 1];
        double [][] myDelta = new double[m_rows][m_cols - 1];
        boolean [][] myEnabled = new boolean[m_rows][m_cols - 1];
        boolean [][] myFixed = new boolean[m_rows][m_cols - 1];
        
        for(int x = 0; x < m_rows; x++) {
            for(int y = 0; y < m_cols; y++) {
                if(y < aColumn) {
                    myValue[x][y] = value[x][y];
                    myDelta[x][y] = delta[x][y];
                    myEnabled[x][y] = enabled[x][y];
                    myFixed[x][y] = fixed[x][y];
                } else if(y > aColumn) {
                    myValue[x][y - 1] = value[x][y];
                    myDelta[x][y - 1] = delta[x][y];
                    myEnabled[x][y - 1] = enabled[x][y];
                    myFixed[x][y - 1] = fixed[x][y];
                }
            }
        }
        value = myValue;
        delta = myDelta;
        enabled = myEnabled;
        fixed = myFixed;
        m_cols--;
    }

    /**
     * Clears (resets) the matrix object. The weights/ biases (values) and its
     * delta values are reset to zero.
     */
    public void clear() {
        for (int x = 0; x < m_rows; ++x) {
            for (int y = 0; y < m_cols; ++y) {
                if (enabled[x][y] || !fixed[x][y]) {
                    value[x][y] = 0.0;
                    delta[x][y] = 0.0;
                }
            }
        }
    }
        
    /**
     * Enables all the weights (or biases) of this matrix.
     */
    public void enableAll() {
        for (int x = 0; x < m_rows; ++x) {
            for (int y = 0; y < m_cols; ++y) {
                enabled[x][y] = true;
            }
        }
    }
    
    /**
     * Disables all the weights (or biases) of this matrix.
     */
    public void disableAll() {
        for (int x = 0; x < m_rows; ++x) {
            for (int y = 0; y < m_cols; ++y) {
                enabled[x][y] = false;
            }
        }
    }
    
    /**
     * Fixes all the weights (or biases) of this matrix.
     */
    public void fixAll() {
        for (int x = 0; x < m_rows; ++x) {
            for (int y = 0; y < m_cols; ++y) {
                fixed[x][y] = true;
            }
        }
    }
    
    /**
     * Unfixes all the weights (or biases) of this matrix.
     */
    public void unfixAll() {
        for (int x = 0; x < m_rows; ++x) {
            for (int y = 0; y < m_cols; ++y) {
                fixed[x][y] = false;
            }
        }
    }
        
    /**
     * Gets <code>m_rows</code>. Needed for Save as XML
     *
     * @return <code>m_rows</code>
     */
    public int getM_rows() {
        return m_rows;
    }
    
    /**
     * Sets <code>m_rows</code>. Needed for Save as XML
     *
     * @param newm_rows the new number of rows to set.
     */
    public void setM_rows(int newm_rows) {
        m_rows = newm_rows;
    }
    
    /**
     * Gets <code>m_cols</code>. Needed for Save as XML
     *
     * @return <code>m_cols</code>
     */
    public int getM_cols() {
        return m_cols;
    }
    
    /**
     * Sets <code>m_cols</code>. Needed for Save as XML
     *
     * @param newm_cols the new number of columns to set.
     */
    public void setM_cols(int newm_cols) {
        m_cols = newm_cols;
    }
    
    /**
     * Gets <code>delta[][]</code>. Needed for Save as XML
     *
     * @return <code>delta[][]</code>
     */
    public double[][] getDelta() {
        return delta;
    }
    
    /**
     * Sets <code>delta[][]</code>. Needed for Save as XML
     *
     * @param newdelta the new delta to set.
     */
    public void setDelta(double[][] newdelta) {
        delta = newdelta;
    }
    
    /**
     * Gets <code>value[][]</code>. Needed for Save as XML
     *
     * @return <code>value[][]</code>
     */
    public double[][] getValue() {
        return value;
    }
    
    /**
     * Sets <code>value[][]</code>. Needed for Save as XML
     *
     * @param newvalue the new values to set
     */
    public void setValue(double[][] newvalue) {
        value = newvalue;
    }
    
    /**
     * Gets <code>fixed[][]</code>. Needed for Save as XML
     *
     * @return <code>fixed[][]</code>
     */
    public boolean[][] getFixed() {
        return fixed;
    }
    
    /**
     * Sets <code>fixed</code>. Needed for Save as XML
     *
     * @param newfixed the new fixed values to set
     */
    public void setFixed(boolean[][] newfixed) {
        fixed = newfixed;
    }
    
    /**
     * Gets <code>enabled</code>. Needed for Save as XML
     *
     * @return <code>enabled[][]</code>
     */
    public boolean[][] getEnabled() {
        return enabled;
    }
    
    /**
     * Sets <code>enabled[][]</code>. Needed for Save as XML
     *
     * @param newenabled the new enabled values to set.
     */
    public void setEnabled(boolean[][] newenabled) {
        enabled = newenabled;
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        // The following code is to assure the backward compatibility with the old Matrix object
        if (enabled == null) {
            enabled = new boolean[m_rows][m_cols];
            this.enableAll();
        }
        if (fixed == null) {
            fixed = new boolean[m_rows][m_cols];
            this.unfixAll();
        }
    }
}