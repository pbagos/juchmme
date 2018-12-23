/*
 * NeuralNetMatrix.java
 *
 * Created on 20 gennaio 2004, 22.11
 */

package org.joone.helpers.structure;

import org.joone.engine.*;
import org.joone.engine.learning.*;
import org.joone.net.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;



/**
 * Utility class that performs several useful 'decompositions'
 * of a NeuralNet object, making very simple to handle its internal
 * structure.
 * This class can be useful to analyze or transfor a neural network,
 * because it transforms the network to a 'flat' structure,
 * where there isn't any object pointed more than once.
 * It, also, by means of the getConnectionMatrix public method,
 * returns a 2D representation of the internal Synapses of
 * a neural network.
 *
 * @author  P.Marrone
 */
public class NeuralNetMatrix {
    private ArrayList layers;
    private ArrayList connectionSet;
    private Monitor monitor;
    private Layer inputLayer = null;
    private Layer outputLayer = null;
    private int inputLayerInd = -1;
    private int outputLayerInd = -1;
    
    transient Hashtable synTemp;
    
    /** Creates a new instance of NeuralNetMatrix */
    public NeuralNetMatrix() {
    }
    
    /** Creates a new instance of NeuralNetMatrix */
    public NeuralNetMatrix(NeuralNet net) {
        this.setNeuralNet(net);
    }
    
    /** Method used to set the neural network to dissect
     */
    public void setNeuralNet(NeuralNet net) {
        int n = net.getLayers().size();
        
        inputLayer = net.findInputLayer();
        outputLayer = net.findOutputLayer();
        
        // Extract and save the Monitor
        monitor = net.getMonitor();
        
        /* Puts the layers into an ArrayList and extracts
         * the synapses by inserting them into a hashtable
         */
        layers = new ArrayList(net.getLayers());
        synTemp = new Hashtable();
        for (int i=0; i < n; ++i) {
            Layer ly = (Layer)layers.get(i);
            checkInputs(i, ly);
            checkOutputs(i, ly);
        }
        
        Enumeration enumerat = synTemp.keys();
        connectionSet = new ArrayList();
        while (enumerat.hasMoreElements()) {
            Object key = enumerat.nextElement();
            Connection tsyn = (Connection)synTemp.get(key);
            int x = tsyn.getInput();
            int y = tsyn.getOutput();
            if (x * y > 0) {
                connectionSet.add(tsyn);
            }
        }
    }
    
    /** Converts the neural structure to a matrix
     * containing all the synapses of the neural network.
     * At each not-null [x][y] element of the 2D array,
     * there is a pointer to the Synapse connecting
     * the Layer[x] to the Layer[y].
     * The returned 2D array of Synapses could be used, for instance,
     * to draw a graphical representation of the neural network.
     */
    public Synapse[][] getConnectionMatrix() {
        Synapse[][] connectionMatrix = new Synapse[layers.size()][layers.size()];
        for (int n=0; n < connectionSet.size(); ++n) {
            Connection tsyn = (Connection)connectionSet.get(n);
            int x = tsyn.getInput();
            int y = tsyn.getOutput();
            connectionMatrix[x-1][y-1] = tsyn.getSynapse();
        }
        return connectionMatrix;
    }
    
    /** Searches a path between two layers.
     * Useful in order to discover recurrent networks
     * 
     * @return true if there is a path from fromLayer to toLayer
     */
    public boolean isThereAnyPath(Layer fromLayer, Layer toLayer) {
        boolean retValue = false;
        int iFrom = getLayerInd(fromLayer);
        int iTo = getLayerInd(toLayer);
        retValue = isThereAnyPath(iFrom, iTo, getConnectionMatrix());
        return retValue;
    }
    
    /* Same as the above method, but with layers' indexes instead of pointers
     * Used recursively to discover paths
     */
    private boolean isThereAnyPath(int iFrom, int iTo, Synapse[][] matrix) {
        boolean retValue = false;
        for (int t=0; (t < layers.size()) && !retValue; ++t) {
            Synapse conn = matrix[iFrom][t];
            if ((conn != null) && (!conn.isLoopBack())) {
                if (t == iTo)
                    retValue = true;
                else 
                    retValue = isThereAnyPath(t, iTo, matrix);
            }
        }
        return retValue;
    }
    
    /** Converts the neural structure to a matrix
     * containing all the synapses of the neural network.
     * The indexes indicates the layer in Layer[] getOrderedLayers().
     * At each not-null [x][y] element of the 2D array,
     * there is a pointer to the Synapse connecting
     * the (ordered)Layer[x] to the (ordered)Layer[y].
     * The returned 2D array of Synapses could be used, for instance,
     * to draw a graphical representation of the neural network.
     *
     * @return a matrix where the indexes x,y point to the Layers
     * returned by getOrderedLayers().
     */
    public Synapse[][] getOrderedConnectionMatrix() {
        // Just to fill the translation array
        getOrderedLayers();
        
        Synapse[][] connectionMatrix = new Synapse[layers.size()][layers.size()];
        for (int n=0; n < connectionSet.size(); ++n) {
            Connection tsyn = (Connection)connectionSet.get(n);
            int x = tsyn.getInput();
            int y = tsyn.getOutput();
            connectionMatrix[translation[x-1]][translation[y-1]] = tsyn.getSynapse();
        }
        return connectionMatrix;
    }
    
    /** Converts the neural structure to a matrix
     * containing all the synapses of the neural network.
     * The index indicates the layer in Layer[] getOrderedLayers().
     * True means connection between the corresponding layers.
     *
     * @return a matrix where the indexes x,y point to the Layers
     * returned by getOrderedLayers().
     */
    public boolean[][] getBinaryOrderedConnectionMatrix() {
        // Just to fill the translation array
        getOrderedLayers();
        
        boolean[][] booleanConnectionMatrix = new boolean[layers.size()][layers.size()];
        for (int n=0; n < connectionSet.size(); ++n) {
            Connection tsyn = (Connection)connectionSet.get(n);
            int x = tsyn.getInput();
            int y = tsyn.getOutput();
            booleanConnectionMatrix[translation[x-1]][translation[y-1]] = true;
        }
        return booleanConnectionMatrix;
    }
    
    // This array, after the call to getOrderedLayers, contains
    // the correspondence between the old and the new layers' order
    int[] translation = null;
    
    /**
     * This method calculates the order of the layers
     * of a neural network, from the input to the output.
     * This method fills also the translations array.
     * 
     * @return An array containing the ordered Layers, from the input to the output (i.e. layers[0]=input layer, layers[n-1]=output layer.
     */
    public Layer[] getOrderedLayers() {
        // TODO: To adapt to recurrent networks
        Synapse[][] connMatrix = getConnectionMatrix();
        if (connMatrix == null)
            return null;
        // An array containing the index of each layer
        int[] ord = new int[layers.size()];
        // First of all, finds the input layers, and assign them the order #1
        ArrayList inputLayers = getInputLayers(connMatrix);
        for (int i=0; i < inputLayers.size(); ++i) {
            int ind = ((Integer)inputLayers.get(i)).intValue();
            ord[ind] = 1;
        }
        boolean changed = assignOrderToLayers(ord, connMatrix);
        // Calculate the order until the array is OK (it didn't change)
        while (changed) {
            changed = assignOrderToLayers(ord, connMatrix);
        }
        /* Now puts the layers into ordLayers according to
         * the order contained in the ord[] array, and
         * fills the translation array.
         */
        translation = new int[layers.size()];
        Layer[] ordLayers = new Layer[layers.size()];
        int n=1; // the current order number to find within ord[]
        for (int d=0; d < layers.size(); ++n) {
            // Searches in ord[] the elements containing n
            for (int x=0; x < ord.length; ++x) {
                if (ord[x] == n) {
                    ordLayers[d] = (Layer)layers.get(x);
                    translation[x] = d; // Layers[x] ==> orderedLayers[d]
                    ++d;
                }
            }
        }
        return ordLayers;
    }
    
    /*
     * This routine assignes the correct order to each layer,
     * depending on the order of the layer that feeds it.
     * If the Layer[n] feeds the Layer[m], then the Layer[m] is assigned
     * the Layer[n]'s order + 1.
     * Returns false only if no order is changed (to understand when to stop).
     */
    private boolean assignOrderToLayers(int[] ord, Synapse[][] connMatrix) {
        boolean changed = false;
        for (int x=0; x < ord.length; ++x) {
            int currLayer = ord[x];
            if (currLayer > 0) {
                for (int y=0; y < connMatrix[x].length; ++y) {
                    if ((connMatrix[x][y] != null) && !connMatrix[x][y].isLoopBack()) {
                        if (currLayer >= ord[y]) {
                            ord[y] = currLayer + 1;
                            changed = true;
                        }
                    }
                }
            }
        }
        return changed;
    }
    
    /** Searches for all the input layers of the network.
     * An input layer is represented by each column in
     * connectionMatrix that doesn't contain any Synapse
     *
     * @return an ArrayList containing Integers that point to the indexes of the input layers
     */
    public ArrayList getInputLayers(Synapse[][] connMatrix) {
        ArrayList inputs = new ArrayList();
        for (int y=0; y < connMatrix.length; ++y) {
            boolean found = false;
            for (int x=0; x < connMatrix[y].length; ++x) {
                if (connMatrix[x][y] != null) {
                    // Recurrent connections are ignored
                    if (!connMatrix[x][y].isLoopBack()) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                inputs.add(new Integer(y));
            }
        }
        return inputs;
    }
    
    /**
     * Clones a neural element.
     * @return the clone of the element passed as parameter
     * @param element The element to clone
     */
    public Serializable cloneElement(Serializable element){
        try {
            //Serialize to a byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
            ObjectOutput out = new ObjectOutputStream(bos) ;
            out.writeObject(element);
            out.close();
            byte[] buf = bos.toByteArray();
            // Deserialize from a byte array
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buf));
            Object theCLone =  in.readObject();
            in.close();
            return (Serializable)theCLone;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    
//    private void insertChild(int x, Synapse[][] connMatrix, Integer[] ord) {
//        for (int y=0; y < connMatrix[x].length; ++y) {
//            if (connMatrix[x][y] != null) {
//                insertOrderedLayer(ord, y+1);
//            }
//        }
//    }
//
//    private void insertOrderedLayer(Integer[] aInd, int ind) {
//        for (int i=0; i < aInd.length; ++i) {
//            if ((aInd[i] != null) && (aInd[i].intValue() == ind))
//                break;
//            if ((aInd[i] == null) || (aInd[i].intValue() == 0)) {
//                aInd[i] = new Integer(ind);
//                break;
//            }
//        }
//    }
    
    private void checkInputs(int n, Layer ly) {
        Vector inps = ly.getAllInputs();
        if (inps == null)
            return;
        for (int i=0; i < inps.size(); ++i) {
            InputPatternListener ipl = (InputPatternListener)inps.elementAt(i);
            if ((ipl != null) && (ipl instanceof Synapse)) {
                Connection temp = getSynapse((Synapse)ipl);
                temp.setOutput(n+1);
                temp.setOutIndex(i);
            }
        }
    }
    
    private void checkOutputs(int n, Layer ly) {
        Vector outs = ly.getAllOutputs();
        if (outs == null)
            return;
        for (int i=0; i < outs.size(); ++i) {
            OutputPatternListener opl = (OutputPatternListener)outs.elementAt(i);
            if ((opl != null) && (opl instanceof Synapse)) {
                Connection temp = getSynapse((Synapse)opl);
                temp.setInput(n+1);
                temp.setInpIndex(i);
            }
        }
    }
    
    /** Gets a Connection from the hashtable, and if it doesn't
     * exist, it is created and put into the hashtable
     */
    private Connection getSynapse(Synapse s) {
        Connection temp = (Connection)synTemp.get(s);
        if (temp == null) {
            temp = new Connection();
            temp.setSynapse(s);
            synTemp.put(s, temp);
        }
        return temp;
    }
    
    /** Getter for property layers.
     * @return Value of property layers.
     *
     */
    public ArrayList getLayers() {
        return this.layers;
    }
    
    /** Setter for property layers.
     * @param layers New value of property layers.
     *
     */
    public void setLayers(ArrayList layers) {
        this.layers = layers;
    }
    
    /** Getter for property connectionSet.
     * @return Value of property connectionSet.
     *
     */
    public ArrayList getConnectionSet() {
        return this.connectionSet;
    }
    
    /** Setter for property connectionSet.
     * @param connectionSet New value of property connectionSet.
     *
     */
    public void setConnectionSet(ArrayList connectionSet) {
        this.connectionSet = connectionSet;
    }
    
    /** Getter for property monitor.
     * @return Value of property monitor.
     *
     */
    public Monitor getMonitor() {
        return monitor;
    }
    
    /** Setter for property monitor.
     * @param monitor New value of property monitor.
     *
     */
    public void setMonitor(Monitor monitor) {
        this.monitor = monitor;
    }
    
    public Layer getInputLayer() {
        return inputLayer;
    }
    
    public void setInputLayer(Layer inputLayer) {
        this.inputLayer = inputLayer;
    }
    
    public Layer getOutputLayer() {
        return outputLayer;
    }
    
    public void setOutputLayer(Layer outputLayer) {
        this.outputLayer = outputLayer;
    }
    
    public int getNumLayers() {
        return layers.size();
    }
    
    /**
     * Calculates the index of the input layer
     * @return The index, within NeuralNet.layers[], of the input layer
     */
    public int getInputLayerInd() {
        if (inputLayerInd == -1) {
            inputLayerInd = getLayerInd(inputLayer);
        }
        return inputLayerInd;
    }
    
    public int getOutputLayerInd() {
        if (outputLayerInd == -1) {
            outputLayerInd = getLayerInd(outputLayer);
        }
        return outputLayerInd;
    }
    
    /** Calculates the index of a layer within the layers array.
     *  This method uses the NeuralNet's 
     * @return the Layer's index starting from 0. Returns -1 if not found
     */
    public int getLayerInd(Layer layer) {
        int layerInd = -1;
        for (int i=0; i < layers.size(); ++i) {
            Layer ly = (Layer)layers.get(i);
            if (ly == layer) {
                layerInd = i;
                break;
            }
        }
        return layerInd;
    }
    
}

