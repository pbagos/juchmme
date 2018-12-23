/*
 * Nakayama.java
 *
 * Created on October 22, 2004, 1:30 PM
 */

package org.joone.structure;

import java.util.*;
import org.joone.engine.*;
import org.joone.engine.listeners.*;
import org.joone.log.*;
import org.joone.net.*;

/**
 * This class performs the method of optimizing activation functions as described
 * in: <p>
 * K.Nakayama and Y.Kimura, <i>"Optimization of activation functions in multilayer 
 * neural network applied to pattern classification"</i>, Proc. IEEE ICNN'94 Florida, 
 * pp.431-436, June 1994.
 * <p>
 * <p>
 * <i>This techniques probably fails whenever the <code>NeuralNet.join()</code> method 
 * is called because this optimization technique stops the network to perform the 
 * optimization, use a <code>NeuralNetListener</code> instead.</i>
 * 
 *
 * @author  Boris Jansen
 */
public class Nakayama implements NeuralNetListener, NeuralValidationListener, ConvergenceListener, java.io.Serializable {
    
    /** Logger for this class. */
    private static final ILogger log = LoggerFactory.getLogger(Nakayama.class);
    
    /** Constant indicating the neuron will not be removed (should be zero). */
    private static final int NO_REMOVE = 0;
    
    /** Constant indicating a neuron will be removed based on its information value Ij. */
    private static final int INFO_REMOVE = 1;
    
    /** Constant indicating a neuron will be removed based on its variance value Vj. */
    private static final int VARIANCE_REMOVE = 2;
    
    /** Constant indicating a neuron MIGHT be removed based on its correlation value Rjmin. */
    private static final int CORRELATION_POSSIBLE_REMOVE = 3;
    
    /** Constant indicating a neuron WILL be removed based on its correlation value Rjmin. */
    private static final int CORRELATION_REMOVE = 4;
    
    /** Constant indicating that a neuron is removed. */
    private static final int REMOVE_DONE = -1;
    
    /** List with layers to be optimized (layers should be on the same (layer) level). */
    private List layers = new ArrayList();
    
    /** Flag to remember if the network was running when the request come to optimize
     * the network. If so we will re-start it after optimization. */
    private boolean isRunning;
    
    /** The net to optimize. */
    private NeuralNet net;
    
    /** The clone of a network to collect information (outputs of neurons after a pattern has been
     * forwarded through the network). */
    private NeuralNet clone;
    
    /** The threshold to decide if the neuron should be deleted or not, i.e. if
     * Cj = Ij / max-j{Ij} * Vj / max-j{Vj} * Rjmin <= epsilon. */
    private double epsilon = 0.05; // default value
    
    /** The original listeners of the neural network that will be temporarely removed.
     * We will add them again after optimization when we restart the network. */
    private List listeners = new ArrayList();
    
    /** Holds the outputs of the neurons after each pattern [pattern->layer->neuron]. */
    private List outputsAfterPattern;
    
    /** Holds the information for every neuron (excluding input and output neurons)
     * to its output layers [layer->neuron]. */
    private List information;
    
    /** The maximum value of the information from a neuron to its output layer. */
    private double infoMax;
    
    /** Holds the average output over all patterns for every neuron (excluding input and
     * output neurons) [layer->neuron]. */
    private List averageOutputs;  
    
    /** Holds the variance for every neuron. */
    private List variance;
    
    /** The maximum variance value [layer->neuron]. */
    private double varianceMax;
    
    /** Holds the gamma value for every neuron [layer->neuron->layer->neuron]. */
    private List gamma;
    
    /** Flag indicating if the network is optimized, i.e. if neurons are removed. */
    private boolean optimized = false;
    
    /** 
     * Creates a new instance of Nakayama.
     *
     * @param aNet the network to be optimized.
     */
    public Nakayama(NeuralNet aNet) {
        net = aNet;
    }
    
    /**
     * Adds layers to this optimizer. The layers will be optimized. Layers should
     * be on the same (layer) level, otherwise the optimization does not make sense.
     * 
     * @param aLayer the layer to be added.
     */
    public void addLayer(Layer aLayer) {
        layers.add(aLayer);
    }
    
    /**
     * Adds all the hidden layers to this optimizer. The layers will be optimized.
     * The neuron network should consist of only one hidden layer, i.e. the hidden
     * layers should be on the same level else this method doesn't make any sense.
     * If the hidden layers are not all on the same level then the layers should be
     * added individually my using {@ling addLayer(Layer)}, adding only the layers
     * that are on the same hidden level.
     *
     * @param aNeuralNet the network holding the hidden layers.
     */
    public void addLayers(NeuralNet aNeuralNet) {
        for(int i = 0; i < aNeuralNet.getLayers().size(); i++) {
            Object myLayer = aNeuralNet.getLayers().get(i);
            if(myLayer != aNeuralNet.getInputLayer() && myLayer != aNeuralNet.getOutputLayer()) {
                layers.add(myLayer);
            }
        }
    }
    
    /**
     * Optimizes the activation functions of the neural network.
     *
     * @return whether the network was optimized or not, i.e. if neurons where deleted
     * or not.
     */
    public boolean optimize() {
        // init (throw away any old values from previous optimization round)
        outputsAfterPattern = new ArrayList();
        information = new ArrayList();
        infoMax = 0;
        averageOutputs = new ArrayList();
        variance = new ArrayList();
        varianceMax = 0;
        gamma = new ArrayList();
        optimized = false;
        
        log.debug("Optimization request [cycle : " + net.getMonitor().getCurrentCicle() + "]");
        
        isRunning = net.isRunning();
        if(isRunning) {
            log.debug("Stopping network...");
            removeAllListeners();
            net.addNeuralNetListener(this);
            net.getMonitor().Stop();
            // runValidation() will be called from cicleTerminated() after the network has been stopped
        } else {
            runValidation();
        }
        return optimized;
    }
    
    /**
     * Runs the network with a validator, this way we are able to collect information
     * related to the different patterns. Everytime a pattern is forwarded to the 
     * network we collect certain info {@link patternFinished()}.
     */
    protected void runValidation() {
        net.getMonitor().setExporting(true);
        clone = net.cloneNet();
        net.getMonitor().setExporting(false);
        
        clone.removeAllListeners();
        // add the following synapse so everytime a pattern has been forwarded through
        // the network patternFinished() is called so we can collect certain info
        clone.getOutputLayer().addOutputSynapse(new PatternForwardedSynapse(this));

        // run the network to collect information
        log.debug("Validating network...");
        NeuralNetValidator myValidator = new NeuralNetValidator(clone);
        myValidator.addValidationListener(this);
        myValidator.useTrainingData(true); // just use the normal training data
        myValidator.start();
    }
    
    /**
     * Optimizes the activation functions of the network.
     */
    protected void doOptimize() {
        log.debug("Optimizing...");
        
        evaluateNeurons();
        selectNeurons();
        
        log.debug("Optimization done.");
        cleanUp();
        
        // debug info
        Layer myLayer;
        for(int i = 0; i < net.getLayers().size(); i++) {
            myLayer = (Layer)net.getLayers().get(i);
            log.debug("Layer [" + myLayer.getClass().getName() + "] - neurons : " + myLayer.getRows());
        }
        // end debug
    }
    
    /**
     * This method is called after optimization, e.g. to restore the listeners.
     */
    protected void cleanUp() {
        log.debug("Cleaning up...");
        outputsAfterPattern = null;
        information = null;
        averageOutputs = null;
        variance = null;
        gamma = null;
        clone = null;

        // remove layers that have no input and output synapses
        Layer myLayer;
        for(int i = 0; i < layers.size(); i++) {
            myLayer = (Layer)layers.get(i);
            if(myLayer.getRows() == 0) {
                log.debug("Remove layer [" + myLayer.getClass().getName() + "]");
                net.removeLayer(myLayer);
                layers.remove(i);
                i--; // layer is removed so index changes
            }
        }
        
        net.removeNeuralNetListener(this);
        restoreAllListeners();
        log.debug("Clean ;)");
        if(isRunning) {
            log.debug("Restarting net...");
            net.start();
            net.getMonitor().runAgain();
        }
        
    }
    
    /**
     * Selects neurons to be deleted based on the information calculated by
     * <code>evaluateNeurons()</code>. It selects neurons to be deleted and 
     * performs the deletion.
     */
    protected void selectNeurons() {
        log.debug("Selecting neurons...");
        
        Layer myLayer;
        List myStatuses = new ArrayList(); // will hold the status for every neuron
        int [] myStatus; // holds the status of neurons for a single layer
        double myScaledInfo, myScaledVariance; // scaled info Ij^ = Ij / max-j{Ij}, scaled variance Vj^ = Vj / max-j{Vj}
        double[] myMinCorrelation; // array holding the minimum correlation, together with the index of the neuron j and j' 
                                   // which have the minimum correlation
        List myMinCorrelationPointers = new ArrayList(); // if the min correlation Rjj' is the lowest of Ij^, Vj^ and Rjj'
                                                         // then we save the min correlation info, because the neuron that
                                                         // is part of this min correlation needs more investigation later
                                                         // to decide if the neuron should be deleted or not
        for(int i = 0; i < layers.size(); i++) {
            myLayer = (Layer)layers.get(i);
            myStatus = new int[myLayer.getRows()]; // initially all elements equal 0, so status is NO_REMOVE
            for(int n = 0; n < myLayer.getRows(); n++) {
                myScaledInfo = ((double[])information.get(i))[n] / infoMax;
                myScaledVariance = ((double[])variance.get(i))[n] / varianceMax;
                myMinCorrelation = getMinCorrelation(i, n);
                
                // log.debug("Info : " + myScaledInfo + ", variance : " + myScaledVariance + ", correlation : " + myMinCorrelation[0]);
                
                if(myScaledInfo * myScaledVariance * myMinCorrelation[0] <= epsilon) {
                    if(myScaledInfo <= myScaledVariance && myScaledInfo <= myMinCorrelation[0]) {
                        myStatus[n] = INFO_REMOVE; // scaled info is the smallest, so neuron should be removed based on its info
                    } else if(myScaledVariance < myScaledInfo && myScaledVariance <= myMinCorrelation[0]) {
                        myStatus[n] = VARIANCE_REMOVE; // scaled variance is the smallest, so neuron should be removed based on its variance
                    } else {
                        myStatus[n] = CORRELATION_POSSIBLE_REMOVE; // set it to possible remove, because it needs more 
                                                                   // examination to decide if it really should be removed. 
                        myMinCorrelationPointers.add(myMinCorrelation); // save the pointer for later investigation
                    }
                }
            }
            myStatuses.add(myStatus);
        }
        
        // now we will investigate which CORRELATION_POSSIBLE_REMOVE neurons should be really removed
        List myCorrelations = new ArrayList(); // this list will hold the arrays indicating the index of the neuron that will be deleted
                                               // together with the index of the neuron it was most closely correlated to (this neuron will
                                               // take over the weights of the other neuron that will be deleted).
        int mySingleStatus; // status of a single neuron
        for(int i = 0; i < myMinCorrelationPointers.size(); i++) {
            myMinCorrelation = (double[])myMinCorrelationPointers.get(i);
            if(myMinCorrelation[5] < 0 && // also check if status is still CORRELATION_POSSIBLE_REMOVE, it might have been changed
                ((int[])myStatuses.get((int)myMinCorrelation[1]))[(int)myMinCorrelation[2]] == CORRELATION_POSSIBLE_REMOVE) 
            {
                // position 1, 2 contain the argument (CORRELATION_POSSIBLE_REMOVE) which should
                // be checked with status neuron argument 3, 4
                mySingleStatus = ((int[])myStatuses.get((int)myMinCorrelation[3]))[(int)myMinCorrelation[4]];
                if(mySingleStatus == INFO_REMOVE || mySingleStatus == VARIANCE_REMOVE || mySingleStatus == CORRELATION_REMOVE) {
                    // neuron that caused the minimum correlation will be removed, so we don't need to remove the other neuron
                    ((int[])myStatuses.get((int)myMinCorrelation[1]))[(int)myMinCorrelation[2]] = NO_REMOVE;
                } else if(((double[])variance.get((int)myMinCorrelation[1]))[(int)myMinCorrelation[2]] <= 
                    ((double[])variance.get((int)myMinCorrelation[3]))[(int)myMinCorrelation[4]]) 
                {
                    ((int[])myStatuses.get((int)myMinCorrelation[1]))[(int)myMinCorrelation[2]] = CORRELATION_REMOVE;
                    myCorrelations.add(new int[] {(int)myMinCorrelation[1], (int)myMinCorrelation[2], (int)myMinCorrelation[3], (int)myMinCorrelation[4]});
                } else {
                    ((int[])myStatuses.get((int)myMinCorrelation[1]))[(int)myMinCorrelation[2]] = NO_REMOVE;
                    ((int[])myStatuses.get((int)myMinCorrelation[3]))[(int)myMinCorrelation[4]] = CORRELATION_REMOVE;
                    myCorrelations.add(new int[] {(int)myMinCorrelation[3], (int)myMinCorrelation[4], (int)myMinCorrelation[1], (int)myMinCorrelation[2]});
                }
            } else if(myMinCorrelation[5] > 0 && // also check if status is still CORRELATION_POSSIBLE_REMOVE, it might have been changed
                ((int[])myStatuses.get((int)myMinCorrelation[3]))[(int)myMinCorrelation[4]] == CORRELATION_POSSIBLE_REMOVE) 
            {
                mySingleStatus = ((int[])myStatuses.get((int)myMinCorrelation[1]))[(int)myMinCorrelation[2]];
                if(mySingleStatus == INFO_REMOVE || mySingleStatus == VARIANCE_REMOVE || mySingleStatus == CORRELATION_REMOVE) {
                    ((int[])myStatuses.get((int)myMinCorrelation[3]))[(int)myMinCorrelation[4]] = NO_REMOVE;
                } else if(((double[])variance.get((int)myMinCorrelation[1]))[(int)myMinCorrelation[2]] >= 
                    ((double[])variance.get((int)myMinCorrelation[3]))[(int)myMinCorrelation[4]]) 
                {
                    ((int[])myStatuses.get((int)myMinCorrelation[3]))[(int)myMinCorrelation[4]] = CORRELATION_REMOVE;
                    myCorrelations.add(new int[] {(int)myMinCorrelation[3], (int)myMinCorrelation[4], (int)myMinCorrelation[1], (int)myMinCorrelation[2]});
                } else {
                    ((int[])myStatuses.get((int)myMinCorrelation[3]))[(int)myMinCorrelation[4]] = NO_REMOVE;
                    ((int[])myStatuses.get((int)myMinCorrelation[1]))[(int)myMinCorrelation[2]] = CORRELATION_REMOVE;
                    myCorrelations.add(new int[] {(int)myMinCorrelation[1], (int)myMinCorrelation[2], (int)myMinCorrelation[3], (int)myMinCorrelation[4]});
                }
            }
        }
        
        int myNeuron; // used to index neurons taking into account the effect of neurons deleted before
        for(int l = 0; l < myStatuses.size(); l++) {
            myStatus = (int[])myStatuses.get(l);
            myNeuron = 0;
            for(int n = 0; n < myStatus.length; n++) {
                if(myStatus[n] == INFO_REMOVE) {
                    log.debug("Remove[info]: " + l + " " + n);
                    removeNeuron(l, myNeuron);
                    optimized = true; // neurons are moved, so the network is really optimized (changed)
                    myStatus[n] = REMOVE_DONE;
                } else if(myStatus[n] == VARIANCE_REMOVE) {
                    log.debug("Remove[variance]: " + l + " " + n);
                    weightsUpdateVariance(l, n, myNeuron);
                    removeNeuron(l, myNeuron);
                    optimized = true;
                    myStatus[n] = REMOVE_DONE;
                } else if(myStatus[n] == CORRELATION_REMOVE) {
                    log.debug("Remove[correlation]: " + l + " " + n);
                    weightsUpdateCorrelation(myStatuses, myCorrelations, l, n);
                    removeNeuron(l, myNeuron);
                    optimized = true;
                    myStatus[n] = REMOVE_DONE;
                } else if(myStatus[n] == NO_REMOVE) {
                    // neuron is not removed so move to next neuron in layer
                    myNeuron++;
                }
            }
        }
        log.debug("Selection done.");
    }
    
    /**
     * Updates weights before a neuron is removed (because of its similar correlation).
     *
     * @param aStatuses the status of the neurons (used to find the correct neuron taking into account
     * any deletions of neurons).
     * @param aCorrelations a list holding all the correlations (neurons to be removed and the
     * correlated neuron (which will take over the weights)).
     * @param aLayer the layer of the neuron to be removed.
     * @param aNeuron the neuron to be removed.
     */
    protected void weightsUpdateCorrelation(List aStatuses, List aCorrelations, int aLayer, int aNeuron) {
        int [] myCorrelatedNeuron = null, myTemp = findCorrelation(aCorrelations, aLayer, aNeuron);

        // the correlated neuron of the neuron to be removed might be part of another correlation and will be removed
        // so we'll search in the chain of correlated neurons for the neuron that will not be removed (myCorrelatedNeuron)
        while(myTemp != null) {
            myCorrelatedNeuron = myTemp;
            myTemp = findCorrelation(aCorrelations, myCorrelatedNeuron[0], myCorrelatedNeuron[1]);
        }
        
        // take into account any deletions of previous neurons
        int myAdjustedNeuron = findIndex(aStatuses, aLayer, aNeuron);
        int myAdjustedCorrelatedNeuron = findIndex(aStatuses, myCorrelatedNeuron[0], myCorrelatedNeuron[1]);
        
        // the weights of the correlated neuron j will be updated in the following way taking into account
        // the effect of the neuron j that will be removed:
        // wjk = wjk + a * wj'k
        // bk = bk + wj'k * (_vj' - a * _vj)
        // a = sign(gammajj') * {Vj' / Vj}^1/2
        
        double myAlpha = (getGamma(aLayer, aNeuron, myCorrelatedNeuron[0], myCorrelatedNeuron[1]) >= 0 ? 1 : -1) * 
            Math.sqrt(((double[])variance.get(aLayer))[aNeuron] / ((double[])variance.get(aLayer))[myCorrelatedNeuron[1]]);
        
        NeuralElement myElement, myElementCorrelation;
        Synapse mySynapse, mySynapseCorrelation = null;
        Matrix myBiases, myWeights, myWeightsCorrelation;
        Layer myOutputLayer, myLayer = (Layer)layers.get(aLayer), myLayerCorrelation = (Layer)layers.get(myCorrelatedNeuron[0]);
        if(myLayer.getAllOutputs().size() != myLayerCorrelation.getAllInputs().size()) {
            throw new org.joone.exception.JooneRuntimeException("Unable to optimize. #output layers for neuron and correlated neuron are not equal.");
        }

        for(int i = 0; i < myLayer.getAllOutputs().size(); i++) {
            myElement = (NeuralElement)myLayer.getAllOutputs().get(i);
            if(!(myElement instanceof Synapse)) {
                // TODO how to deal with outputs that are not synpases?
                throw new org.joone.exception.JooneRuntimeException("Unable to optimize. Output of layer is not a synapse.");
            }
            mySynapse = (Synapse)myElement;
            myOutputLayer = findOutputLayer(mySynapse);
            
            // find synapse from correlation layer to the same output layer
            for(int j = 0; j < myLayerCorrelation.getAllOutputs().size() && mySynapseCorrelation == null; j++) {
                myElementCorrelation = (NeuralElement)myLayerCorrelation.getAllOutputs().get(j);
                if(myElementCorrelation instanceof Synapse) {
                    mySynapseCorrelation = (Synapse)myElementCorrelation;
                    if(findOutputLayer(mySynapseCorrelation) != myOutputLayer) {
                        mySynapseCorrelation = null;
                    }
                }
            }
            if(mySynapseCorrelation == null) {
                throw new org.joone.exception.JooneRuntimeException("Unable to optimize. Unable to find same output layer for correlated layer.");
            }
            
            myBiases = myOutputLayer.getBias();
            myWeights = mySynapse.getWeights();
            myWeightsCorrelation = mySynapseCorrelation.getWeights();
            for(int r = 0; r < myOutputLayer.getRows(); r++) {
                myBiases.value[r][0] += myWeights.value[myAdjustedNeuron][r] * 
                    (((double[])averageOutputs.get(aLayer))[aNeuron] - myAlpha * ((double[])averageOutputs.get(myCorrelatedNeuron[0]))[myCorrelatedNeuron[1]]);
                myBiases.delta[r][0] = 0;
                myWeightsCorrelation.value[myAdjustedCorrelatedNeuron][r] += myWeights.value[myAdjustedNeuron][r];
                myWeightsCorrelation.delta[myAdjustedCorrelatedNeuron][r] = 0;
            } 
        }
    }
    
    /**
     * Gets gammajj' (is equal to gammaj'j).
     *
     * @param aLayer1 the layer of neuron j.
     * @param aNeuron1 the neuron j.
     * @param aLayer2 the layer of neuron j'.
     * @param aNeuron2 the neuron j'.
     * return gammajj'.
     */
    protected double getGamma(int aLayer1, int aNeuron1, int aLayer2, int aNeuron2) {
        int mySwapLayer, mySwapNeuron;
        
        if(aLayer1 > aLayer2 || (aLayer1 == aLayer2 && aNeuron1 > aNeuron2)) {
            mySwapLayer = aLayer1;
            mySwapNeuron = aNeuron1;
            aLayer1 = aLayer2;
            aNeuron1 = aNeuron2;
            aLayer2 = mySwapLayer;
            aNeuron2 = mySwapNeuron;
        }
        return ((double[])((List[])gamma.get(aLayer1))[aNeuron1].get(aLayer2))[aNeuron2];
    }
    
    /**
     * Finds the index of a neuron taking into account the deletion of previous neurons.
     *
     * @param aStatuses the status of neurons.
     * @param aLayer the layer of the neuron.
     * @param aNeuron the index of the neuron, not considering any deletions.
     * @return the index of the neuron taking into account any previous deletions.
     */
    protected int findIndex(List aStatuses, int aLayer, int aNeuron) {
        int[] myStatusLayer = (int[])aStatuses.get(aLayer);
        int myNewIndex = aNeuron;
        
        for(int i = 0; i < aNeuron; i++) {
            if(myStatusLayer[i] == REMOVE_DONE) {
                myNewIndex--;
            }
        }
        return myNewIndex;
    }
    
    /**
     * Finds a correlation from a given list of correlations.
     *
     * @param aCorrelations a list holding correlations.
     * @param aLayer the layer of the neuron to find the correlation for.
     * @param aNeuron the neuron to find the correlation for.
     * @return the index of the neuron of the correlation with <code>(aLayer, aNeuron)</code>,
     * return <code>null</code> in case the correlation is not found.
     */
    protected int[] findCorrelation(List aCorrelations, int aLayer, int aNeuron) {
        int[] myCorrelation;

        for(int i = 0; i < aCorrelations.size(); i++) {
            myCorrelation = (int [])aCorrelations.get(i);
            if(myCorrelation[0] == aLayer && myCorrelation[1] == aNeuron) {
                return new int [] {myCorrelation[2], myCorrelation[3]};
            }
        }
        return null;
    }
    
    /**
     * Updates weights before a neuron is removed (because of its low variance).
     *
     * @param aLayer the index of the layer of the neuron.
     * @param aNeuronOriginal the index of the neuron to be removed (NOT taking into account
     * previous deletions).
     * @param aNeuron the index of the neuron to be removed (taking into account previous
     * deletions).
     */
    protected void weightsUpdateVariance(int aLayer, int aNeuronOriginal, int aNeuron) {
        // the biases of the neurons in the output layer will be updated by taking
        // the effects of the neuron that will be removed into account:
        // bk = bk + wjk * _vj, b = bias, k = index output neuron, j = aNeuron, 
        // _vj is average output neuron aNeuron
        
        NeuralElement myElement;
        Synapse mySynapse;
        Matrix myBiases, myWeights;
        double myAverageOutput;
        Layer myOutputLayer, myLayer = (Layer)layers.get(aLayer);
        for(int i = 0; i < myLayer.getAllOutputs().size(); i++) {
            myElement = (NeuralElement)myLayer.getAllOutputs().get(i);
            if(!(myElement instanceof Synapse)) {
                // TODO how to deal with outputs that are not synpases?
                throw new org.joone.exception.JooneRuntimeException("Unable to optimize. Output of layer is not a synapse.");
            }
            mySynapse = (Synapse)myElement;
            myOutputLayer = findOutputLayer(mySynapse);
            myBiases = myOutputLayer.getBias();
            myWeights = mySynapse.getWeights();
            myAverageOutput = ((double[])averageOutputs.get(aLayer))[aNeuronOriginal];
            for(int r = 0; r < myOutputLayer.getRows(); r++) {
                myBiases.value[r][0] += myWeights.value[aNeuron][r] * myAverageOutput;
                myBiases.delta[r][0] = 0;
            } 
        }
    }
    
    /**
     * Removes a neuron.
     *
     * @param aLayer the index of the layer in which we should remove the neuron.
     * @param aNeuron the index of the neuron to be removed (taking into account previous
     * deletions).
     */
    protected void removeNeuron(int aLayer, int aNeuron) {
        Layer myLayer = (Layer)layers.get(aLayer);
        
        NeuralElement myElement;
        Synapse mySynapse;
        Matrix myWeights;
        if(myLayer.getRows() > 1) {
            for(int i = 0; i < myLayer.getAllInputs().size(); i++) {
                myElement = (NeuralElement)myLayer.getAllInputs().get(i);
                if(!(myElement instanceof Synapse)) {
                    // TODO how to deal with inputs that are not synpases?
                    throw new org.joone.exception.JooneRuntimeException("Unable to optimize. Input of layer is not a synapse.");
                }
                mySynapse = (Synapse)myElement;
                myWeights = mySynapse.getWeights();
                myWeights.removeColumn(aNeuron);
                mySynapse.setOutputDimension(mySynapse.getOutputDimension() - 1);
                mySynapse.setWeights(myWeights);
            }
            for(int i = 0; i < myLayer.getAllOutputs().size(); i++) {
                myElement = (NeuralElement)myLayer.getAllOutputs().get(i);
                if(!(myElement instanceof Synapse)) {
                    // TODO how to deal with outputs that are not synpases?
                    throw new org.joone.exception.JooneRuntimeException("Unable to optimize. Output of layer is not a synapse.");
                }
                mySynapse = (Synapse)myElement;
                myWeights = mySynapse.getWeights();
                myWeights.removeRow(aNeuron);
                mySynapse.setInputDimension(mySynapse.getInputDimension() - 1);
                mySynapse.setWeights(myWeights);
            }
            myWeights = myLayer.getBias();
            myWeights.removeRow(aNeuron);
            myLayer.setRows(myLayer.getRows() - 1);
            myLayer.setBias(myWeights);
        } else {
            // we are going to remove the last neuron so remove the layer and its input an output synapses
            for(int i = 0; i < myLayer.getAllInputs().size(); i++) {
                myElement = (NeuralElement)myLayer.getAllInputs().get(i);
                if(!(myElement instanceof Synapse)) {
                    // TODO how to deal with inputs that are not synpases?
                    throw new org.joone.exception.JooneRuntimeException("Unable to optimize. Input of layer is not a synapse.");
                }
                mySynapse = (Synapse)myElement;
                Layer myInputLayer = findInputLayer(mySynapse);
                myInputLayer.removeOutputSynapse(mySynapse);
            }
            for(int i = 0; i < myLayer.getAllOutputs().size(); i++) {
                myElement = (NeuralElement)myLayer.getAllOutputs().get(i);
                if(!(myElement instanceof Synapse)) {
                    // TODO how to deal with outputs that are not synpases?
                    throw new org.joone.exception.JooneRuntimeException("Unable to optimize. Output of layer is not a synapse.");
                }
                mySynapse = (Synapse)myElement;
                Layer myOutputLayer = findOutputLayer(mySynapse);
                myOutputLayer.removeInputSynapse(mySynapse);
            }
            // if we remove the layer here from the network the index of layers goes out of order,
            // after optimization is done we will remove the layer
            myWeights = myLayer.getBias();
            myWeights.removeRow(aNeuron);
            myLayer.setRows(myLayer.getRows() - 1);
            myLayer.setBias(myWeights);
        }
    }
    
    /**
     * Finds the input layer of a synapse.
     *
     * @param aSynapse the synapse to find the input layer for.
     */
    protected Layer findInputLayer(Synapse aSynapse) {
        Layer myLayer;
        for(int i = 0; i < net.getLayers().size(); i++) {
            myLayer = (Layer)net.getLayers().get(i);
            if(myLayer.getAllOutputs().contains(aSynapse)) {
                return myLayer;
            }
        }
        return null;
    }
    
    /**
     * Finds the output layer of a synapse.
     *
     * @param aSynapse the synapse to find the output layer for.
     */
    protected Layer findOutputLayer(Synapse aSynapse) {
        Layer myLayer;
        for(int i = 0; i < net.getLayers().size(); i++) {
            myLayer = (Layer)net.getLayers().get(i);
            if(myLayer.getAllInputs().contains(aSynapse)) {
                return myLayer;
            }
        }
        return null;
    }
    
    /**
     * Evaluates neurons, that is, this function calculates information related to
     * the contribution of the activation functions, based on the following three
     * criteria: <br>
     * <ul>
     *      <li>Information from neurons to its output layers. </li>
     *      <li>Variance of the output of the neurons.</li>
     *      <li>Correlation between outputs of neurons.</li>
     * </ul>
     * This information will be used in a next stage to select neurons to delete.
     */
    protected void evaluateNeurons() {
        log.debug("Evaluation of neurons...");
        
        Layer myLayer; // help variable
        int myNrOfPatterns = net.getMonitor().getTrainingPatterns(); // number of patterns
        double [] myInfo; // Ij (for a single layer)
        double [] myAvgOutputs; // the average output over all patterns (for a single layer)
        
        // first we will calculate the information from the jth hidden unit to its output layer (Ij)
        // Ij = 1/M * sum{p=1:M}(sum{k=1:No}(|wjk*vpj|)), this is equal to
        //    = 1/M * sum{p=1:M}(|vpj|)) * sum{k=1:No}(|wjk|)
        // M is number of patterns, No is number of output units, 
        // vpj = the output of a neuron j for pattern p, wjk is the weight from neuron j to k
        // during this the calculation of Ij we also calculate the average output for each neuron j
        // _vj = 1/M sum{p=1:M}(vpj)
        
        for(int i = 0; i < layers.size(); i++) {
            myLayer = (Layer)layers.get(i);
            myInfo = new double[myLayer.getRows()];
            myAvgOutputs = new double[myLayer.getRows()]; 
            for(int n = 0; n < myLayer.getRows(); n++) {
                // for all neurons in a layer
                double myTempSumWeights = getSumAbsoluteWeights(myLayer, n); // get sum{k=1:No}(|wjk|)
                double[] myTempSumOutputs = getSumOutputs(i, n); // get sum{p=1:M}(vpj) and sum{p=1:M}(|vpj|)

                myInfo[n] = (myTempSumWeights * myTempSumOutputs[1]) / myNrOfPatterns;
                if(myInfo[n] > infoMax) {
                    // also find max value of the information max-j{Ij}
                    infoMax = myInfo[n];
                }
                myAvgOutputs[n] = myTempSumOutputs[0] / myNrOfPatterns;
            }
            information.add(myInfo);
            averageOutputs.add(myAvgOutputs);
        }
        
        // at this moment we have calculated (A) information from each neuron j to its output 
        // layer (Ij and maxj{Ij}) and we have calculated the average of outputs for each neuron
        // ((B) _vj)
        
        // In the next step we will calculate the variance (B) Vj
        // Vj = sum{p=1:M}(vpj - _vj)^2, _vj the average is calculate above
        // we will also store (vpj - _vj) for all neurons, so in the following step it will be easier 
        // to calculate the correlation
        
        double [] myVariance; // variances for a layer
        double [] myTempDifferences; // differences (output-pattern - avg-output <=> vjp - _vj) for a single layer and single pattern
        List myDifferences = new ArrayList(); // all differences (all layers and over all patterns) 
                                              // vpj - _vj [layer->pattern->neuron], NOTE outputsAfterPattern is [pattern->layer->neuron]
        List myDifferencesForLayer; // differences of all patterns for a single layer [pattern->neuron]
        for(int i = 0; i < layers.size(); i++) {
            myLayer = (Layer)layers.get(i);
            myVariance = new double[myLayer.getRows()]; // Vj
            myDifferencesForLayer = new ArrayList();
            for(int p = 0; p < outputsAfterPattern.size(); p++) {
                myTempDifferences = new double[myLayer.getRows()]; // differences for a single pattern for a single layer
                for(int n = 0; n < myLayer.getRows(); n++) {
                    List myOutputs = (List)outputsAfterPattern.get(p);
                    myTempDifferences[n] = ((double[])myOutputs.get(i))[n] 
                        - ((double[])averageOutputs.get(i))[n]; // vpj - _vj
                    myVariance[n] += myTempDifferences[n] * myTempDifferences[n];
                }
                myDifferencesForLayer.add(myTempDifferences);
            }
            for(int n = 0; n < myLayer.getRows(); n++) {
                // also find max variance
                if(myVariance[n] > varianceMax) {
                    varianceMax = myVariance[n];
                }
            }
            myDifferences.add(myDifferencesForLayer);
            variance.add(myVariance);
        }
        
        // Now we have calculated the variance for each neuron (B) Vj and myDifferences holds the differences
        // between the output of a neuron and the average output over all patterns and all layers. Now we will
        // calculate gamma (C), which is closely related to the correlation, which will be needed later
        // Ajj' = sum{p=1:M}((vpj - _vj) * (vpj' - _vj'))
        // Bjj' = sum{p=1:M}(vpj - _vj)^2 * sum{p=1:M}(vpj' - _vj')^2
        //      = Vj * Vj'
        // Gammajj' = Ajj' / Bjj'^1/2
        // The correlation between units is defined by:
        // Rjj' = 1 - |Gammajj'|, however we will not calculate Rjj' here
        
        Layer myLayer1, myLayer2;
        List myTempDifferencesForLayer1, myTempDifferencesForLayer2; // differences between vpj - _vj and vpj' - _vj' (j = n1, j' = n2)
        // Pointers to construct the tree to save the gammas [gamma->layer1->neuron1->layer2->neuron2]
        List [] myNeurons1Pointer;
        double [] myNeurons2Pointer;
        
        for(int l1 = 0; l1 < layers.size(); l1++) { // l1 = layer 1
            myLayer1 = (Layer)layers.get(l1);
            myNeurons1Pointer = new List[myLayer1.getRows()];
            gamma.add(myNeurons1Pointer); // so gamma.get(an index) gets a neuron pointer of layer index
            for(int n1 = 0; n1 < myLayer1.getRows(); n1++) {
                myNeurons1Pointer[n1] = new ArrayList();
                for(int l2 = 0; l2 < layers.size(); l2++) { // l2 = layer 1
                    myLayer2 = (Layer)layers.get(l2);
                    if(l2 < l1) {
                        myNeurons1Pointer[n1].add(new double[0]); // a little waste of memory, but this way it allows us to 
                                                                  // index layers easily later on, because the index of layers
                                                                  // will be still be matching
                    } else {
                        myNeurons2Pointer = new double[myLayer2.getRows()];
                        myNeurons1Pointer[n1].add(myNeurons2Pointer);
                        for(int n2 = (l1 == l2 ? n1+1 : 0); n2 < myLayer2.getRows(); n2++) {
                            double myA = 0, myB = 0;
                            for(int p = 0; p < myNrOfPatterns; p++) {
                                myTempDifferencesForLayer1 = (List)myDifferences.get(l1);
                                myTempDifferencesForLayer2 = (List)myDifferences.get(l2);

                                myA += ((double[])myTempDifferencesForLayer1.get(p))[n1] *
                                    ((double[])myTempDifferencesForLayer2.get(p))[n2];
                            }
                            myB = ((double[])variance.get(l1))[n1] * ((double[])variance.get(l2))[n2];
                            myNeurons2Pointer[n2] = myA / Math.sqrt(myB);
                        }
                    }
                }
            }
        }
        log.debug("Evaluation done.");
    }
    
    /**
     * Gets the minimum correlation for a certain neuron j. A correlation between neuron
     * j and j' is defined as Rjj' = 1 - |Gammajj'|. Rjmin (which this function calculates
     * is defined as Rjmin = min-j{Rjj'}. It also returns the index of the neuron j that is the
     * argument to this function as well as j' that is the minimum.
     *
     * @param anLayer the index of the layer of the neuron <code>aNeuron</code>.
     * @param aNeuron the neuron within the layer (j).
     * @return the minimum correlation Rjmin, together with the index of the neuron as an argument
     * and the neuron of the minimum (layer and neuron). The lower index of the two neurons is at position 
     * 1, 2 and the higher index is at position 3, 4. The minimum itself is at position 0. Finally at position
     * 5 we will indicate if the argument was the lower index neuron (<0, so it is now at position 1, 2) or 
     * if the argument was the higher index neuron (>0, so now it is at position 3, 4).
     */
    protected double[] getMinCorrelation(int aLayer, int aNeuron) {
        double [] myReturnValue = new double[] {2, -1, -1, -1, -1, 0}; // 2 => 0 <= min <= 1
        List[] myNeurons;
        double myCorrelation;
        
        // check neurons before aLayerIndex and aNeuron
        for(int l = 0; l <= aLayer; l++) {
            myNeurons = (List[])gamma.get(l);
            for(int n = 0; n < (l == aLayer ? aNeuron : myNeurons.length); n++) {
                myCorrelation = 1 - Math.abs(((double[])myNeurons[n].get(aLayer))[aNeuron]);
                if(myReturnValue[0] > myCorrelation) {
                    myReturnValue[0] = myCorrelation;
                    myReturnValue[1] = l;       // the lower index neuron
                    myReturnValue[2] = n;
                    myReturnValue[3] = aLayer;  // the higher index neuron
                    myReturnValue[4] = aNeuron;
                    myReturnValue[5] = 1;       // argument is higher index neuron
                }
            }
        }
        
        List myLayers;
        double[] myNeurons2;
        // check neurons after aLayerIndex and aNeuron
        myLayers = ((List[])gamma.get(aLayer))[aNeuron];
        for(int l = aLayer; l < myLayers.size(); l++) {
            myNeurons2 = (double[])myLayers.get(l);
            for(int n = (l == aLayer ? aNeuron + 1 : 0); n < myNeurons2.length; n++) {
                myCorrelation = 1 - Math.abs(myNeurons2[n]);
                if(myReturnValue[0] > myCorrelation) {
                    myReturnValue[0] = myCorrelation;
                    myReturnValue[1] = aLayer;  // the lower index neuron
                    myReturnValue[2] = aNeuron;
                    myReturnValue[3] = l;       // the higher index neuron
                    myReturnValue[4] = n;
                    myReturnValue[5] = -1;      // argument is lower neuron
                }
            }
        }
        return myReturnValue;
    }
    
    /**
     * Sums up the (normal and absolute) values of the outputs of a neuron over all patterns.
     *
     * @param aLayer an index of the layer to retrieve the outputs of that layer.
     * @param aNeuron the neuron in the layer.
     * @return the sum of (index 0: normal, index 1: absolute) outputs of neuron <code>aNeuron</code>.
     */
    protected double[] getSumOutputs(int aLayer, int aNeuron) {
        List myOutputs;
        double myOutput;
        double [] mySum = new double[2]; // index 0 normal sum, index 1 absolute sum
                
        for(int i = 0; i < outputsAfterPattern.size(); i++) {
            // for all patterns
            myOutputs = (List)outputsAfterPattern.get(i);
            myOutput = ((double[])myOutputs.get(aLayer))[aNeuron];
            mySum[0] += myOutput;
            mySum[1] += Math.abs(myOutput);
        }
        return mySum;
    }
    
    /**
     * Sums up all the absolute values of the output weights of a neuron within a layer.
     *
     * @param aLayer the layer holding neuron <code>aNeuron</code>.
     * @param aNeuron the neuron in the layer.
     * @return the sum of absolute values of the output weights of neuron 
     * <code>aNeuron</code> within layer <code>aLayer</code>.
     */
    protected double getSumAbsoluteWeights(Layer aLayer, int aNeuron) {
        double mySum = 0;
        OutputPatternListener myListener;
        Synapse mySynapse;
        
        for(int i = 0; i < aLayer.getAllOutputs().size(); i++) {
            myListener = (OutputPatternListener)aLayer.getAllOutputs().get(i);
            if(!(myListener instanceof Synapse)) {
                // TODO how to deal with outputs that are not synpases?
                throw new org.joone.exception.JooneRuntimeException("Unable to optimize. Output of layer is not a synapse.");
            }
            mySynapse = (Synapse)myListener;
            for(int j = 0; j < mySynapse.getOutputDimension(); j++) {
                mySum += Math.abs(mySynapse.getWeights().value[aNeuron][j]);
            }
        }
        return mySum;
    }

    public void cicleTerminated(NeuralNetEvent e) {
    }

    public void errorChanged(NeuralNetEvent e) {
    }

    public void netStarted(NeuralNetEvent e) {
    }

    public void netStopped(NeuralNetEvent e) {
        log.debug("Network stopped.");
        runValidation();
    }

    public void netStoppedError(NeuralNetEvent e, String error) {
    }
    
    public void netValidated(NeuralValidationEvent event) {
        // validation is finished, so we should have collected all the information
        // to optimize the activation functions
        log.debug("Network validated.");
        doOptimize();
    }
    
    /**
     * Removes all the listeners from the neural network (temporarely). They will
     * be added again after optimization and the network is restarted.
     */
    protected void removeAllListeners() {
        Vector myListeners = net.getListeners();
        
        while(myListeners.size() > 0) {
            NeuralNetListener myListener = (NeuralNetListener)myListeners.get(myListeners.size() - 1);
            listeners.add(myListener);
            net.removeNeuralNetListener(myListener);
        }
    }
    
    /**
     * Restore all the listeners to the neural network.
     */
    protected void restoreAllListeners() {
        Iterator myIterator = listeners.iterator();
        while(myIterator.hasNext()) {
            NeuralNetListener myListener = (NeuralNetListener)myIterator.next();
            net.addNeuralNetListener(myListener);
        }
        listeners = new Vector(); // clear the list
    }
    
    /**
     * This method is called after every pattern, so we can retrieve information
     * from the network that is related to the pattern that was just forwarded 
     * through the network.
     */
    void patternFinished() {
        Layer myLayer;
        List myOutputs = new ArrayList(); // the outputs of the neurons after a pattern
        
        // log.debug("Single pattern has been forwarded through the network.");
        
        // in this stage we only need to save the outputs of the neurons after
        // each pattern, then later we can calculate all the necessary information
        
        for(int i = 0; i < layers.size(); i++) {
            myLayer = findClonedLayer((Layer)layers.get(i));
            myOutputs.add(myLayer.getLastOutputs());
        }
        outputsAfterPattern.add(myOutputs);
    }
    
    /**
     * Finds the cloned equal layer from the cloned neuron network given its corresponding 
     * layer from the normal neural network.
     *
     * @param aLayer the layer to find its cloned version in <code>clone</code>.
     * @return the cloned layer from <code>clone</code> corresponding to <code>aLayer</code>.
     */
    private Layer findClonedLayer(Layer aLayer) {
        for(int i = 0; i < net.getLayers().size(); i++) {
            if(net.getLayers().get(i) == aLayer) {
                // index of layer found
                return (Layer)clone.getLayers().get(i);
            }
        }
        return null;
    }
    
    /**
     * Gets epsilon, the threshold to decide if a neuron should be deleted or not.
     *
     * @return the threshold epsilon.
     */
    public double getEpsilon() {
        return epsilon;
    }
    
    /**
     * Sets epsilon, the threshold to decide if a neuron should be deleted or not.
     *
     * @param anEpsilon the new epsilon.
     */
    public void setEpsilon(double anEpsilon) {
        epsilon = anEpsilon;
    }
    
    public void netConverged(ConvergenceEvent anEvent, ConvergenceObserver anObserver) {
        // whenever this object is added to a convegence observer, this method will be called
        // when convergence is reached, otherwise the user itself should call optimize()
        // based on some criteria
        if(!optimize()) {
            // the network was not optimized, so the network stayes probably in the same 
            // convergence state, so new event shouldn't be created until we move out of
            // the convergence state
            anObserver.disableCurrentConvergence();
        }
    }
}

/**
 * This class/synapse is only used to inform a Nakayama object whenever a single 
 * patterns has been forwarded through the network.
 */
class PatternForwardedSynapse extends Synapse {
    
    /** The nakayama object that needs to be informed. */
    protected Nakayama nakayama;
    
    /**
     * Constructor
     *
     * @param aNakayama the object that needs to be informed whenever a pattern
     * has been forwarded through the network.
     */
    public PatternForwardedSynapse(Nakayama aNakayama) {
        nakayama = aNakayama;        
    }
    
    public synchronized void fwdPut(Pattern pattern) {
        if(pattern.getCount() > -1) {
            nakayama.patternFinished();
            items++;
        }
    }

    protected void backward(double[] pattern) {
    }

    protected void forward(double[] pattern) {
    }

    protected void setArrays(int rows, int cols) {
    }

    protected void setDimensions(int rows, int cols) {
    }
}
