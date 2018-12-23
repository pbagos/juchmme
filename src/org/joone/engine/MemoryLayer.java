package org.joone.engine;

import java.io.*;
import java.util.TreeSet;

public abstract class MemoryLayer extends Layer {
    protected double memory[];
    protected double backmemory[];
    private int taps = 0;
    private static final long serialVersionUID = 5447777678414684948L;

    public MemoryLayer() {
        super();
    }

    public MemoryLayer(String ElemName) {
        super(ElemName);
    }

    public int getDimension() {
        return (getRows() * (getTaps() + 1));

    }

    /**
     * Return the taps value
     * (06/04/00 1.08.26)
     * @return int
     */
    public int getTaps() {
        return taps;
    }

    protected void setDimensions() {
        inps = new double[getRows()];
        outs = new double[getRows() * (getTaps() + 1)];
        gradientInps = new double[getRows() * (getTaps() + 1)];
        gradientOuts = new double[getRows()];
        memory = new double[getRows() * (getTaps() + 1)];
        backmemory = new double[getRows() * (getTaps() + 1)];
    }

    /**
     * Sets the dimansion of the output
     * (22/03/00 1.45.24)
     * @param syn neural.engine.Synapse
     */
    protected void setOutputDimension(OutputPatternListener syn) {
        int n = getRows() * (getTaps() + 1);
        if (syn.getInputDimension() != n)
            syn.setInputDimension(n);
    }

    /**
     * Inserire qui la descrizione del metodo.
     * Data di creazione: (06/04/00 1.08.26)
     * @param newTaps int
     */
    public void setTaps(int newTaps) {
        taps = newTaps;
        setDimensions();
        setConnDimensions();
    }

    protected void sumBackInput(double[] pattern) {
        int x;
        int length = getRows() * (getTaps() + 1);
        for (x = 0; x < length; ++x)
            gradientInps[x] += pattern[x];

    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        setDimensions();
    }

    /** Reverse transfer function of the component.
     * @param pattern double[] - input pattern on wich to apply the transfer function
     *
     */
    protected void backward(double[] pattern) {
    }

    /** Transfer function to recall a result on a trained net
     * @param pattern double[] - input pattern
     *
     */
    protected void forward(double[] pattern) {
    }

    public TreeSet check() {
        return super.check();
    }

}