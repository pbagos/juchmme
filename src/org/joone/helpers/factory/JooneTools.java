/*
 * JooneTools.java
 *
 * Created on January 16, 2006, 4:19 PM
 *
 * Copyright @2005 by Paolo Marrone and the Joone team
 * Licensed under the Lesser General Public License (LGPL);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.gnu.org/
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.joone.helpers.factory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.TreeSet;
import java.util.Vector;
import org.joone.engine.DelayLayer;
import org.joone.engine.DirectSynapse;
import org.joone.engine.FullSynapse;
import org.joone.engine.GaussianLayer;
import org.joone.engine.KohonenSynapse;
import org.joone.engine.Layer;
import org.joone.engine.LinearLayer;
import org.joone.engine.Monitor;
import org.joone.engine.NeuralNetEvent;
import org.joone.engine.NeuralNetListener;
import org.joone.engine.Pattern;
import org.joone.engine.SigmoidLayer;
import org.joone.engine.SoftmaxLayer;
import org.joone.engine.Synapse;
import org.joone.engine.WTALayer;
import org.joone.engine.learning.ComparingSynapse;
import org.joone.engine.learning.TeachingSynapse;
import org.joone.engine.listeners.ErrorBasedTerminator;
import org.joone.io.MemoryInputSynapse;
import org.joone.io.MemoryOutputSynapse;
import org.joone.io.StreamInputSynapse;
import org.joone.net.NeuralNet;
import org.joone.net.NeuralNetAttributes;

/**
 * Utility class to build/train/interrogate neural networks.
 * By using this class, it's possible to easily build/train and interrogate
 * a neural network with only 3 rows of code, as in this example:
 *
 * // Create an MLP network with 3 layers [2,2,1 nodes] with a logistic output layer
 * NeuralNet nnet = JooneTools.create_standard(new int[]{2,2,1}, JooneTools.LOGISTIC);
 * // Train the network for 5000 epochs, or until the rmse < 0.01
 * double rmse = JooneTools.train(nnet, inputArray, desiredArray, 5000, 0.01, 0, null);
 * // Interrogate the network
 * double[] output = JooneTools.interrogate(nnet, testArray);
 *
 * @author paolo
 */
public class JooneTools {
    // Kinds of output layer
    /**
     * Linear output layer
     */
    public static final int LINEAR = 1;
    /**
     * Logistic (sigmoid) output layer
     */
    public static final int LOGISTIC = 2;
    /**
     * Softmax output layer
     */
    public static final int SOFTMAX = 3;
    /**
     * WTA output layer (unsupervised Kohonen)
     */
    public static final int WTA = 4;
    /**
     * Gaussian output layer (unsupervised Kohonen)
     */
    public static final int GAUSSIAN = 5;
    
    // Training algorithms
    /**
     * Backprop on-line (incremental) learning algorithm
     */
    public static final int BPROP_ONLINE = 0;
    /**
     * Backprop batch learning algorithm
     */
    public static final int BPROP_BATCH = 1;
    /**
     * Resilient Backprop learning algorithm
     */
    public static final int RPROP = 2;
    
    /**
     * Creates a feed forward neural network without I/O components.
     * @param nodes array of integers containing the nodes of each layer
     * @param outputType the type of output layer. One of 'LINEAR', 'SOFTMAX', 'LOGISTIC'
     * @return The neural network created
     * @throws java.lang.IllegalArgumentException .
     */
    public static NeuralNet create_standard(int nodes[], int outputType) throws IllegalArgumentException {
        NeuralNet nnet = new NeuralNet();
        if ((nodes == null) || (nodes.length < 2)) {
            throw new IllegalArgumentException("create_standard: Nodes is empty");
        }
        
        Layer[] layers = new Layer[nodes.length];
        
        // Input layer
        layers[0] = new LinearLayer();
        layers[0].setRows(nodes[0]);
        layers[0].setLayerName("input");
        nnet.addLayer(layers[0], NeuralNet.INPUT_LAYER);
        
        // Hidden layers
        if (nodes.length > 2) {
            for (int i=1; i < nodes.length - 1; ++i) {
                layers[i] = new SigmoidLayer();
                layers[i].setRows(nodes[i]);
                layers[i].setLayerName("hidden"+i);
                nnet.addLayer(layers[i], NeuralNet.HIDDEN_LAYER);
            }
        }
        
        // Output layer
        int outp = nodes.length - 1;
        switch (outputType) {
            case LINEAR:
                layers[outp] = new LinearLayer(); break;
            case LOGISTIC:
                layers[outp] = new SigmoidLayer(); break;
            case SOFTMAX:
                layers[outp] = new SoftmaxLayer(); break;
            default:
                throw new IllegalArgumentException("create_standard: output type not supported");
        }
        layers[outp].setRows(nodes[outp]);
        layers[outp].setLayerName("output");
        nnet.addLayer(layers[outp], NeuralNet.OUTPUT_LAYER);
        
        // Internal connections
        for (int i=0; i < layers.length - 1; ++i) {
            connect(layers[i], new FullSynapse(), layers[i+1]);
        }
        
        // Prepares the learning parameters
        Monitor mon = nnet.getMonitor();
        mon.addLearner(BPROP_ONLINE, "org.joone.engine.BasicLearner"); // Default
        mon.addLearner(BPROP_BATCH, "org.joone.engine.BatchLearner");
        mon.addLearner(RPROP, "org.joone.engine.RpropLearner");
        mon.setLearningRate(0.7);
        mon.setMomentum(0.7);
        
        return nnet;
    }
    
    /**
     * Creates a feed forward neural network without I/O components.
     * @param nodes array of integers containing the nodes of each layer
     * @param outputType the type of output layer. One of 'LINEAR', 'SOFTMAX', 'LOGISTIC'
     * @return The neural network created
     * @throws java.lang.IllegalArgumentException .
     */
    public static NeuralNet create_timeDelay(int nodes[], int taps, int outputType) throws IllegalArgumentException {
        NeuralNet nnet = new NeuralNet();
        if ((nodes == null) || (nodes.length < 2)) {
            throw new IllegalArgumentException("create_standard: nodes: not enough elements");
        }
        
        Layer[] layers = new Layer[nodes.length];
        
        // Input layer
        layers[0] = new DelayLayer();
        layers[0].setRows(nodes[0]);
        ((DelayLayer)layers[0]).setTaps(taps);
        layers[0].setLayerName("input");
        nnet.addLayer(layers[0], NeuralNet.INPUT_LAYER);
        
        // Hidden layers
        if (nodes.length > 2) {
            for (int i=1; i < nodes.length - 1; ++i) {
                layers[i] = new SigmoidLayer();
                layers[i].setRows(nodes[i]);
                layers[i].setLayerName("hidden"+i);
                nnet.addLayer(layers[i], NeuralNet.HIDDEN_LAYER);
            }
        }
        
        // Output layer
        int outp = nodes.length - 1;
        switch (outputType) {
            case LINEAR:
                layers[outp] = new LinearLayer(); break;
            case LOGISTIC:
                layers[outp] = new SigmoidLayer(); break;
            case SOFTMAX:
                layers[outp] = new SoftmaxLayer(); break;
            default:
                throw new IllegalArgumentException("create_standard: output type not supported");
        }
        layers[outp].setRows(nodes[outp]);
        layers[outp].setLayerName("output");
        nnet.addLayer(layers[outp], NeuralNet.OUTPUT_LAYER);
        
        // Internal connections
        for (int i=0; i < layers.length - 1; ++i) {
            connect(layers[i], new FullSynapse(), layers[i+1]);
        }
        
        // Prepares the learning parameters
        Monitor mon = nnet.getMonitor();
        mon.addLearner(BPROP_ONLINE, "org.joone.engine.BasicLearner"); // Default
        mon.addLearner(BPROP_BATCH, "org.joone.engine.BatchLearner");
        mon.addLearner(RPROP, "org.joone.engine.RpropLearner");
        mon.setLearningRate(0.7);
        mon.setMomentum(0.7);
        
        return nnet;
    }
    
    /**
     * Creates an unsupervised neural network without I/O components.
     * This method is able to build the following kind of networks, depending on the 'outputType' parameter:
     * WTA - Kohonen network with a WinnerTakeAll output layer 
     * GAUSSIAN - Kohonen network with a gaussian output layer 
     * The nodes array must contain 3 elements, with the following meaning:
     * nodes[0] = Rows of the input layer
     * nodes[1] = Width of the output map
     * nodes[2] = Height of the output map
     * @param nodes array of integers containing the nodes of each layer (see note above)
     * @param outputType the type of output layer. One of 'WTA', 'GAUSSIAN'
     * @return The neural network created
     * @throws java.lang.IllegalArgumentException 
     */
    public static NeuralNet create_unsupervised(int nodes[], int outputType) throws IllegalArgumentException {
        NeuralNet nnet = new NeuralNet();
        if ((nodes == null) || (nodes.length < 3)) {
            throw new IllegalArgumentException("create_unsupervised: nodes: not enough elements");
        }
        
        // A Kohonen network contains 2 layers
        Layer[] layers = new Layer[2];
        
        // Input layer
        layers[0] = new LinearLayer();
        layers[0].setRows(nodes[0]);
        layers[0].setLayerName("input");
        nnet.addLayer(layers[0], NeuralNet.INPUT_LAYER);
                
        // Output layer
        switch (outputType) {
            case WTA:
                layers[1] = new WTALayer(); 
                ((WTALayer)layers[1]).setLayerWidth(nodes[1]);
                ((WTALayer)layers[1]).setLayerHeight(nodes[2]);
                break;
            case GAUSSIAN:
                layers[1] = new GaussianLayer(); 
                ((GaussianLayer)layers[1]).setLayerWidth(nodes[1]);
                ((GaussianLayer)layers[1]).setLayerHeight(nodes[2]);
                break;
            default:
                throw new IllegalArgumentException("create_unsupervised: output type not supported");
        }
        layers[1].setLayerName("output");
        nnet.addLayer(layers[1], NeuralNet.OUTPUT_LAYER);
        
        // Synapse
        connect(layers[0], new KohonenSynapse(), layers[1]);
                
        Monitor mon = nnet.getMonitor();
        mon.setLearningRate(0.7);
        
        return nnet;
    }
    
    /**
     * Interrogate a neural network with an array of doubles and returns the output
     * of the neural network.
     * @param nnet The neural network to interrogate
     * @param input The input pattern (must have the size = # of input nodes)
     * @return An array of double having size = # of output nodes
     */
    public static double[] interrogate(NeuralNet nnet, double[] input) {
        nnet.removeAllInputs();
        nnet.removeAllOutputs();
        
        DirectSynapse inputSynapse = new DirectSynapse();
        DirectSynapse outputSynapse = new DirectSynapse();
        
        nnet.addInputSynapse(inputSynapse);
        nnet.addOutputSynapse(outputSynapse);
        
        Pattern inputPattern = new Pattern(input);
        inputPattern.setCount(1);
        
        nnet.getMonitor().setLearning(false);
        // Start the network
        // TODO: Adapt to the single-thread mode
        nnet.start();
        // Interrogate the network
        inputSynapse.fwdPut(inputPattern);
        Pattern outputPattern = outputSynapse.fwdGet();
        // Stop the network
        inputSynapse.fwdPut(stopPattern(input.length));
        outputSynapse.fwdGet();
        nnet.join();
        return outputPattern.getArray();
    }
    
    /**
     * Trains a neural network using the input/desired pairs contained in 2D arrays of double.
     * If Monitor.trainingPatterns = 0, all the input array's rows will be used for training.
     * @param nnet The neural network to train
     * @param input 2D array of double containing the training data. The # of columns must be equal to the # of input nodes
     * @param desired 2D array of double containing the target data. The # of columns must be equal to the # of output nodes
     * @param epochs Number of max training epochs
     * @param stopRMSE The desired min error at which the training must stop. If zero, the training continues until the last epoch is reached.
     * @param epochs_btw_reports Number of epochs between the notifications on the stdOut
     * @param stdOut The object representing the output. It can be either a PrintStream or a NeuralNetListener instance. If null, no notifications will be made.
     * @param async if true, the method returns after having stated the network, without waiting for the completition. In this case, the value returned is zero.
     * @return The final training RMSE (or MSE)
     */
    public static double train(NeuralNet nnet,
            double[][] input, double[][] desired,
            int epochs, double stopRMSE,
            int epochs_btw_reports, Object stdOut,
            boolean async) {
        MemoryInputSynapse memInput = new MemoryInputSynapse();
        memInput.setInputArray(input);
        memInput.setAdvancedColumnSelector("1-"+input[0].length);
        
        MemoryInputSynapse memTarget = null;
        if (desired != null) {
            memTarget = new MemoryInputSynapse();
            memTarget.setInputArray(desired);
            memTarget.setAdvancedColumnSelector("1-"+desired[0].length);
        }
        
        Monitor mon = nnet.getMonitor();
        if (mon.isValidation()) {
            if (mon.getValidationPatterns() == 0)
                mon.setValidationPatterns(input.length);
        } else {
            if (mon.getTrainingPatterns() == 0)
                mon.setTrainingPatterns(input.length);
        }
        
        return train_on_stream(nnet, memInput, memTarget,
                epochs, stopRMSE, epochs_btw_reports, stdOut, async);
    }
    
    /**
     * Trains a neural network in unsupervised mode (SOM and PCA networks)
     * using the input contained in a 2D array of double.
     * @param nnet The neural network to train
     * @param input 2D array of double containing the training data. The # of columns must be equal to the # of input nodes
     * @param epochs Number of max training epochs
     * @param epochs_btw_reports Number of epochs between the notifications on the stdOut
     * @param stdOut The object representing the output. It can be either the System.out or a NeuralNetListener instance.
     * @param async if true, the method returns after having stated the network, without waiting for the completition. In this case, the value returned is zero.
     */
    public static void train_unsupervised(NeuralNet nnet,
            double[][] input,
            int epochs,
            int epochs_btw_reports, Object stdOut,
            boolean async) {
        nnet.getMonitor().setSupervised(false);
        train(nnet, input, null, epochs, 0, epochs_btw_reports, stdOut, async);
    }
    
    /**
     * Trains a neural network using StreamInputSynapses as the input/desired data sources.
     * The Monitor.trainingPatterns must be set before to call this method.
     * @param nnet The neural network to train
     * @param input the StreamInputSynapse containing the training data. The advColumnSelector must be set according to the # of input nodes
     * @param desired the StreamInputSynapse containing the target data. The advColumnSelector must be set according to the # of output nodes
     * @param epochs Number of max training epochs
     * @param stopRMSE The desired min error at which the training must stop. If zero, the training continues until the last epoch is reached.
     * @param epochs_btw_reports Number of epochs between the notifications on the stdOut
     * @param stdOut The object representing the output. It can be either a PrintStream or a NeuralNetListener instance. If null, no notifications will be made.
     * @param async if true, the method returns after having stated the network, without waiting for the completition. In this case, the value returned is zero.
     * @return The final training RMSE (or MSE)
     */
    public static double train_on_stream(NeuralNet nnet,
            StreamInputSynapse input, StreamInputSynapse desired,
            int epochs, double stopRMSE,
            int epochs_btw_reports, Object stdOut,
            boolean async) {
        nnet.removeAllInputs();
        nnet.removeAllOutputs();
        
        nnet.addInputSynapse(input);
        if (desired != null) {
            TeachingSynapse teacher = new TeachingSynapse();
            teacher.setDesired(desired);
            nnet.addOutputSynapse(teacher);
            nnet.setTeacher(teacher);
        }
        
        return train_complete(nnet, epochs, stopRMSE, epochs_btw_reports, stdOut, async);
    }
    
    /**
     * Trains a complete neural network, i.e. a network having
     * all the parameters and the I/O components already set.
     * @param nnet The neural network to train
     * @param epochs Number of max training epochs
     * @param stopRMSE The desired min error at which the training must stop. If zero, the training continues until the last epoch is reached.
     * @param epochs_btw_reports Number of epochs between the notifications on the stdOut
     * @param stdOut The object representing the output. It can be either a PrintStream or a NeuralNetListener instance. If null, no notifications will be made.
     * @param async if true, the method returns after having stated the network, without waiting for the completition. In this case, the value returned is zero.
     * @return The final training RMSE (or MSE)
     */
    public static double train_complete(NeuralNet nnet,
            int epochs, double stopRMSE,
            int epochs_btw_reports, Object stdOut,
            boolean async) {
        nnet.removeAllListeners();
        Monitor mon = nnet.getMonitor();
        
        if (stdOut != null) {
            mon.addNeuralNetListener(createListener(nnet, stdOut, epochs_btw_reports));
        }
        
        ErrorBasedTerminator term = null;
        if (stopRMSE > 0) {
            term = new ErrorBasedTerminator(stopRMSE);
            term.setNeuralNet(nnet);
            mon.addNeuralNetListener(term);
        }
        
        mon.setTotCicles(epochs);
        mon.setLearning(!mon.isValidation());
        TreeSet tree = nnet.check();
        if (tree.isEmpty()) {            
            nnet.go(!async);
            // Returns if async=true
            if (async)
                return 0.0d;
            
            NeuralNetAttributes attrib = nnet.getDescriptor();
            if (term != null) {
                if (term.isStopRequestPerformed()) {
                    attrib.setLastEpoch(term.getStoppedCycle());
                } else {
                    attrib.setLastEpoch(mon.getTotCicles());
                }
            }
            
            if (mon.isValidation()) {
                attrib.setValidationError(mon.getGlobalError());
            } else {
                attrib.setTrainingError(mon.getGlobalError());
            }
            
            return mon.getGlobalError();
        } else {
            throw new IllegalArgumentException("Cannot start, errors found:"+tree.toString());
        }
    }
    
    /**
     * Tests a neural network using the input/desired pairs contained in 2D arrays of double.
     * This method doesn't change the weights, but calculates only the RMSE.
     * If Monitor.validationPatterns = 0, all the input array's rows will be used for testing.
     * @param nnet The neural network to test
     * @param input 2D array of double containing the test data. The # of columns must be equal to the # of input nodes
     * @param desired 2D array of double containing the target data. The # of columns must be equal to the # of output nodes
     * @return The test RMSE (or MSE)
     */
    public static double test(NeuralNet nnet,
            double[][] input, double[][] desired) {
        nnet.getMonitor().setValidation(true);
        return train(nnet, input, desired, 1, 0, 0, null, false);
    }
    
    /**
     * Tests a neural network using using StreamInputSynapses as the input/desired data sources.
     * This method doesn't change the weights, but calculates only the RMSE.
     * The Monitor.validationPatterns must be set before the call to this method.
     * @param nnet The neural network to test
     * @param input the StreamInputSynapse containing the test data. The advColumnSelector must be set according to the # of input nodes
     * @param desired the StreamInputSynapse containing the target data. The advColumnSelector must be set according to the # of output nodes
     * @return The test RMSE (or MSE)
     */
    public static double test_on_stream(NeuralNet nnet,
            StreamInputSynapse input, StreamInputSynapse desired) {
        nnet.getMonitor().setValidation(true);
        return train_on_stream(nnet, input, desired, 1, 0, 0, null, false);
    }
      
    
    /**
     * Permits to compare the output and target data of a trained neural network using 2D array of double as the input/desired data sources.
     * If Monitor.validationPatterns = 0, all the input array's rows will be used for testing.
     * @param nnet The neural network to test
     * @param input 2D array of double containing the test data. The # of columns must be equal to the # of input nodes
     * @param desired 2D array of double containing the target data. The # of columns must be equal to the # of output nodes
     * @return a 2D of double containing the output+desired data for each pattern.
     */
    public static double[][] compare(NeuralNet nnet,
            double[][] input, double[][] desired) {
        MemoryInputSynapse memInput = new MemoryInputSynapse();
        memInput.setInputArray(input);
        memInput.setAdvancedColumnSelector("1-"+input[0].length);
        
        MemoryInputSynapse memTarget = null;
        if (desired != null) {
            memTarget = new MemoryInputSynapse();
            memTarget.setInputArray(desired);
            memTarget.setAdvancedColumnSelector("1-"+desired[0].length);
        }
        
        Monitor mon = nnet.getMonitor();
        nnet.getMonitor().setValidation(true);
        if (mon.getValidationPatterns() == 0)
            mon.setValidationPatterns(input.length);
        
        return compare_on_stream(nnet, memInput, memTarget);
    }
    
    /**
     * Permits to compare the output and target data of a trained neural network using StreamInputSynapses as the input/desired data sources.
     *
     * @param nnet The neural network to train
     * @param input the StreamInputSynapse containing the training data. The advColumnSelector must be set according to the # of input nodes
     * @param desired the StreamInputSynapse containing the target data. The advColumnSelector must be set according to the # of output nodes
     * @return a 2D of double containing the output+desired data for each pattern.
     */
    public static double[][] compare_on_stream(NeuralNet nnet,
            StreamInputSynapse input, StreamInputSynapse desired) {
        nnet.removeAllInputs();
        nnet.removeAllOutputs();
        
        nnet.addInputSynapse(input);
        ComparingSynapse teacher = new ComparingSynapse();
        teacher.setDesired(desired);
        nnet.addOutputSynapse(teacher);
        MemoryOutputSynapse outStream = new MemoryOutputSynapse();
        teacher.addResultSynapse(outStream);
        
        train_complete(nnet, 1, 0, 0, null, false);
        Vector results = outStream.getAllPatterns();
        int rows = results.size();
        int columns = ((Pattern)results.get(0)).getArray().length;
        double[][] output = new double[rows][columns];
        
        for (int i=0; i < rows; ++i) {
            output[i] = ((Pattern)results.get(i)).getArray();
        }
        return output;
    }
    
    /**
     * Extracts a subset of data from the StreamInputSynapse passed as parameter.
     * @return A 2D array of double containing the extracted data
     * @param dataSet The input StreamInputSynapse. Must be buffered.
     * @param firstRow The first row (relative to the internal buffer) to extract
     * @param lastRow The last row (relative to the internal buffer) to extract
     * @param firstCol The first column (relative to the internal buffer) to extract
     * @param lastCol The last column (relative to the internal buffer) to extract
     */
    public static double[][] getDataFromStream(StreamInputSynapse dataSet,
            int firstRow, int lastRow,
            int firstCol, int lastCol) {
        // Force the reading of all the input data
        dataSet.Inspections();
        Vector data = dataSet.getInputPatterns();
        int rows = lastRow - firstRow + 1;
        int columns = lastCol - firstCol + 1;
        double[][] array = new double[rows][columns];
        
        for (int r=0; r < rows; ++r) {
            double[] temp = ((Pattern)data.get(r + firstRow - 1)).getArray();
            for (int c=0; c < columns; ++c) {
                array[r][c] = temp[c + firstCol - 1];
            }
        }
        
        return array;
    }
    
    /**
     * Saves a neural network to a file
     * @param nnet The network to save
     * @param fileName the file name on which the network is saved
     * @throws java.io.FileNotFoundException if the file name is invalid
     * @throws java.io.IOException when an IO error occurs
     */
    public static void save(NeuralNet nnet, String fileName) throws FileNotFoundException, IOException {
        FileOutputStream stream = new FileOutputStream(fileName);
        save_toStream(nnet, stream);
    }
    
    /**
     * Saves a neural network to a file
     * @param nnet The network to save
     * @param fileName the file on which the network is saved
     * @throws java.io.FileNotFoundException if the file name is invalid
     * @throws java.io.IOException when an IO error occurs
     */
    public static void save(NeuralNet nnet, File fileName) throws FileNotFoundException, IOException {
        FileOutputStream stream = new FileOutputStream(fileName);
        save_toStream(nnet, stream);
    }
    
    /**
     * Saves a neural network to an OutputStream
     * @param nnet The neural network to save
     * @param stream The OutputStream on which the network is saved
     * @throws java.io.IOException when an IO error occurs
     */
    public static void save_toStream(NeuralNet nnet, OutputStream stream) throws IOException {
        ObjectOutput output = new ObjectOutputStream(stream);
        output.writeObject(nnet);
        output.close();
    }
    
    /**
     * Loads a neural network from a file
     * @param fileName the name of the file from which the network is loaded
     * @throws java.io.IOException when an IO error occurs
     * @throws java.io.FileNotFoundException if the file name is invalid
     * @throws java.lang.ClassNotFoundException if some neural network's object is not found in the classpath
     * @return The loaded neural network
     */
    public static NeuralNet load(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException {
        File NNFile = new File(fileName);
        FileInputStream fin = new FileInputStream(NNFile);
        NeuralNet nnet = load_fromStream(fin);
        fin.close();
        return nnet;
    }
    
    /**
     * Loads a neural network from an InputStream
     * @param stream The InputStream from which the network is loaded
     * @throws java.io.IOException when an IO error occurs
     * @throws java.lang.ClassNotFoundException some neural network's object is not found in the classpath
     * @return The loaded neural network
     */
    public static NeuralNet load_fromStream(InputStream stream) throws IOException, ClassNotFoundException {
        ObjectInputStream oin = new ObjectInputStream(stream);
        NeuralNet nnet = (NeuralNet)oin.readObject();
        oin.close();
        return nnet;
    }
    
    /**
     * Connects two layers with the given synapse
     * @param l1 The source layer
     * @param syn The synapse to use to connect the two layers
     * @param l2 The destination layer
     */
    protected static void connect(Layer l1, Synapse syn, Layer l2) {
        l1.addOutputSynapse(syn);
        l2.addInputSynapse(syn);
    }
    
    /**
     * Creates a stop pattern (i.e. a Pattern with counter = -1)
     * @param size The size of the Pattern's array
     * @return the created stop Pattern
     */
    protected static Pattern stopPattern(int size) {
        Pattern stop = new Pattern(new double[size]);
        stop.setCount(-1);
        return stop;
    }
    
    /**
     * Creates a listener for a NeuralNet object.
     * The listener writes the results to the stdOut object every 'interval' epochs.
     * If stdOut points to a NeuralNetListener instance, the corresponding methods are invoked.
     * If stdOut points to a PrintStream instance, a corresponding message is written.
     * @param nnet The NeuralNetwork to which the listener will be attached
     * @param stdOut the NeuralNetListener, or the PrintStream instance to which the notifications will be made
     * @param interval The interval of epochs between two calls to the cyclic events cycleTerminated and errorChanged
     * @return The created listener
     */
    protected static NeuralNetListener createListener(final NeuralNet nnet,
            final Object stdOut, final int interval) {
        NeuralNetListener listener = new NeuralNetListener() {
            Object output = stdOut;
            int interv = interval;
            NeuralNet neuralNet = nnet;
            
            public void netStarted(NeuralNetEvent e) {
                if (output == null) {
                    return;
                }
                if (output instanceof PrintStream) {
                    ((PrintStream)output).println("Network started");
                } else if (output instanceof NeuralNetListener) {
                    e.setNeuralNet(neuralNet);
                    ((NeuralNetListener)output).netStarted(e);
                }
            }
            
            public void cicleTerminated(NeuralNetEvent e) {
                if (output == null) {
                    return;
                }
                Monitor mon = (Monitor)e.getSource();
                int epoch = mon.getCurrentCicle() - 1;
                if ((interval == 0) || (epoch % interval > 0))
                    return;
                
                if (output instanceof PrintStream) {
                    ((PrintStream)output).print("Epoch n."+(mon.getTotCicles()-epoch)+" terminated");
                    if (mon.isSupervised()) {
                        ((PrintStream)output).print(" - rmse: "+mon.getGlobalError());
                    }
                    ((PrintStream)output).println("");
                } else if (output instanceof NeuralNetListener) {
                    e.setNeuralNet(neuralNet);
                    ((NeuralNetListener)output).cicleTerminated(e);
                }
            }
            
            public void errorChanged(NeuralNetEvent e) {
                if (output == null) {
                    return;
                }
                Monitor mon = (Monitor)e.getSource();
                int epoch = mon.getCurrentCicle() - 1;
                if ((interval == 0) || (epoch % interval > 0))
                    return;
                
                if (output instanceof NeuralNetListener) {
                    e.setNeuralNet(neuralNet);
                    ((NeuralNetListener)output).errorChanged(e);
                }
            }
            
            public void netStopped(NeuralNetEvent e) {
                if (output == null) {
                    return;
                }
                if (output instanceof PrintStream) {
                    ((PrintStream)output).println("Network stopped");
                } else if (output instanceof NeuralNetListener) {
                    e.setNeuralNet(neuralNet);
                    ((NeuralNetListener)output).netStopped(e);
                }
            }
            
            public void netStoppedError(NeuralNetEvent e,String error) {
                if (output == null) {
                    return;
                }
                if (output instanceof PrintStream) {
                    ((PrintStream)output).println("Network stopped with error:"+error);
                } else if (output instanceof NeuralNetListener) {
                    e.setNeuralNet(neuralNet);
                    ((NeuralNetListener)output).netStoppedError(e, error);
                }
            }
        };
        
        return listener;
    }
}
