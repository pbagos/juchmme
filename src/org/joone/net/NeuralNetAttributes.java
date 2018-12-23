/*
 * NeuralNetDescriptor.java
 *
 * Created on 29 april 2002, 15.14
 * @author pmarrone
 */

package org.joone.net;

/**
 * This class represents a descriptor of a neural network.
 * It stores some parameters of the neural network useful 
 * to manage the training and the validation error of the 
 * net without having it loaded in memory.
 * Created for the distributed environment to store the 'state' 
 * of the training of a net without the necessity to load in
 * memory all the nets belonging to a generation, it can be
 * extended to store more parameters for any other future use.
 * 
 * WARNING: This class must be compatible with the SOAP-Serialization
 * mechanism, hence add ONLY public parameters having:
 * - public getter/setter methods
 * - a basic type, like String, int, ... or a SOAP-Serializable interface
 * 
 * @author  pmarrone
 */
public class NeuralNetAttributes implements java.io.Serializable {

    private static final long serialVersionUID = -3122881040378874490L;
    private double validationError = -1.0;    
    private double trainingError = -1.0;
    private String neuralNetName;
    private int lastEpoch = 0;
        
    /** Creates a new instance of NeuralNetDescriptor */
    public NeuralNetAttributes() {
    }

    /** Getter for property trainingError.
     * @return Value of property trainingError.
     */
    public double getTrainingError() {
        return trainingError;
    }
    
    /** Setter for property trainingError.
     * @param trainingError New value of property trainingError.
     */
    public void setTrainingError(double trainingError) {
        this.trainingError = trainingError;
    }
    
    /** Getter for property validationError.
     * @return Value of property validationError.
     */
    public double getValidationError() {
        return validationError;
    }
    
    /** Setter for property validationError.
     * @param validationError New value of property validationError.
     */
    public void setValidationError(double validationError) {
        this.validationError = validationError;
    }
    
    /** Getter for property neuralNetName.
     * @return Value of property neuralNetName.
     */
    public String getNeuralNetName() {
        return neuralNetName;
    }
    
    /** Setter for property neuralNetName.
     * @param neuralNetName New value of property neuralNetName.
     */
    public void setNeuralNetName(String neuralNetName) {
        this.neuralNetName = neuralNetName;
    }

    public int getLastEpoch() {
        return lastEpoch;
    }

    public void setLastEpoch(int lastEpoch) {
        this.lastEpoch = lastEpoch;
    }
    
}
