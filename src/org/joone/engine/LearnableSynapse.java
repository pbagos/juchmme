package org.joone.engine;


public interface LearnableSynapse extends Learnable {
    public int getInputDimension();
    public int getOutputDimension();
    public Matrix getWeights(); // getConnections()
    public void setWeights(Matrix newWeights);
    public double getLearningRate();
    public double getMomentum();

}