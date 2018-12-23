/*
 * ExtendableLearner.java
 *
 * Created on September 14, 2004, 8:30 AM
 */

package org.joone.engine;

import java.util.*;
import org.joone.engine.extenders.*;

/**
 * Learners that extend this class are forced to implement certain functions, a
 * so-called skeleton. The good thing is, because learners extend this class
 * certain plug-ins can be added. For example, plug ins that change the objective
 * function, or the delta-update rule. Still learners that do not fit into this
 * skeleton have to opportunity to implement Learner directly (or extend
 * AbstractLearner), but it won't be able to use the extra plug-ins (unless it
 * is build in the learner by the programmer itself).
 *
 * Basically, this class is the BasicLearner, but by adding extenders it can
 * provide totally different learning algoriths.
 *
 * @author Boris Jansen
 */
public class ExtendableLearner extends AbstractLearner {
    
    /** The list with delta rule extenders, extenders that change the
     * delta w, e.g. momentum term, etc. */
    protected List theDeltaRuleExtenders = new ArrayList();
    
    /** The list with gradient extenders, extenders that change the gradient. */
    protected List theGradientExtenders = new ArrayList();
    
    /** The update weight extender, that is, the way to update
     * the weights, online, batch mode, etc. */
    protected UpdateWeightExtender theUpdateWeightExtender;
    
    /** Creates a new instance of ExtendableLearner */
    public ExtendableLearner() {
    }
    
    public final void requestBiasUpdate(double[] currentGradientOuts) {
        double myDelta;
        preBiasUpdate(currentGradientOuts);
        
        for(int x = 0; x < getLayer().getRows(); x++) {
            myDelta = getDelta(currentGradientOuts, x);
            updateBias(x, myDelta);
        }
        
        postBiasUpdate(currentGradientOuts);
    }
    
    public final void requestWeightUpdate(double[] currentPattern, double[] currentInps) {
        double myDelta;
        preWeightUpdate(currentPattern, currentInps);
        boolean[][] isEnabled = getSynapse().getWeights().getEnabled();
        boolean[][] isFixed = getSynapse().getWeights().getFixed();
        for(int x = 0; x < getSynapse().getInputDimension(); x++) {
            for(int y = 0; y < getSynapse().getOutputDimension(); y++) {
                if (!isFixed[x][y] && isEnabled[x][y]) {
                    myDelta = getDelta(currentInps, x, currentPattern, y);
                    updateWeight(x, y, myDelta);
                }
            }
        }
        
        postWeightUpdate(currentPattern, currentInps);
    }
    
    /**
     * Updates a bias with the calculated delta value.
     *
     * @param j the index of the bias to update.
     * @param aDelta the calculated delta value.
     */
    protected void updateBias(int j, double aDelta) {
        theUpdateWeightExtender.updateBias(j, aDelta);
    }
    
    /**
     * Updates a weight with the calculated delta value.
     *
     * @param j the input index of the weight to update.
     * @param k the output index of the weight to update.
     * @param aDelta the calculated delta value.
     */
    protected void updateWeight(int j, int k, double aDelta) {
        theUpdateWeightExtender.updateWeight(j, k, aDelta);
    }
    
    /**
     * Computes the delta value for a bias.
     *
     * @param currentGradientOuts the back propagated gradients.
     * @param j the index of the bias.
     */
    protected double getDelta(double[] currentGradientOuts, int j) {
        // if this method is overwritten, make sure that no delta extenders can be set
        // by throwing an exception from setDeltaExtender()
        
        // more than one delta extender might be set, this variable is used to pass on
        // the delta value calculated by the previous delta extender to the next one
        double myDelta = getDefaultDelta(currentGradientOuts, j);
        
        for(int i = 0; i < theDeltaRuleExtenders.size(); i++) {
            if(((DeltaRuleExtender)theDeltaRuleExtenders.get(i)).isEnabled()) {
                myDelta = ((DeltaRuleExtender)theDeltaRuleExtenders.get(i)).
                        getDelta(currentGradientOuts, j, myDelta);
            }
        }
        return myDelta;
    }
    
    /**
     * Gets the default (normal calculation of) delta.
     *
     * @param currentGradientOuts the back propagated gradients.
     * @param j the index of the bias.
     */
    public double getDefaultDelta(double[] currentGradientOuts, int j) {
        return getLearningRate(j) * getGradientBias(currentGradientOuts, j);
    }
    
    /**
     * Computes the delta value for a weight.
     *
     * @param currentInps the forwarded input.
     * @param j the input index of the weight.
     * @param currentPattern the back propagated gradients.
     * @param k the output index of the weight.
     */
    protected double getDelta(double[] currentInps, int j, double[] currentPattern, int k) {
        // if this method is overwritten, make sure that no delta extenders can be set
        // by throwing an exception from setDeltaExtender()
        
        // more than one delta extender might be set, this variable is used to pass on
        // the delta value calculated by the previous delta extender to the next one
        double myDelta = getDefaultDelta(currentInps, j, currentPattern, k);
        
        for(int i = 0; i < theDeltaRuleExtenders.size(); i++) {
            if(((DeltaRuleExtender)theDeltaRuleExtenders.get(i)).isEnabled()) {
                myDelta = ((DeltaRuleExtender)theDeltaRuleExtenders.get(i)).
                        getDelta(currentInps, j, currentPattern, k, myDelta);
            }
        }
        return myDelta;
    }
    
    /**
     * Gets the default (normal calculation of) delta.
     *
     * @param currentInps the forwarded input.
     * @param j the input index of the weight.
     * @param currentPattern the back propagated gradients.
     * @param k the output index of the weight.
     */
    public double getDefaultDelta(double[] currentInps, int j, double[] currentPattern, int k) {
        return getLearningRate(j, k) * getGradientWeight(currentInps, j, currentPattern, k);
    }
    
    /**
     * Gets the learning rate.
     *
     * @param j the index of the bias (for which we should get the learning rate).
     * @return the learning rate for a bias.
     */
    protected double getLearningRate(int j) {
        // in future we could add learning rate extenders...
        
        return getMonitor().getLearningRate();
    }
    
    /**
     * Gets the learning rate.
     *
     * @param j the input index of the weight (for which we should get the learning rate).
     * @param k the output index of the weight (for which we should get the learning rate).
     * @return the learning rate for a weight.
     */
    protected double getLearningRate(int j, int k) {
        // in future we could add learning rate extenders...
        
        return getMonitor().getLearningRate();
    }
    
    /**
     * Gets the gradient for biases.
     *
     * @param currentGradientOuts the back protected gradients.
     * @param j the index of the bias.
     * @return the gradient for bias b_i.
     */
    public double getGradientBias(double[] currentGradientOuts, int j) {
        double myGradient = getDefaultGradientBias(currentGradientOuts, j);
        
        for(int i = 0; i < theGradientExtenders.size(); i++) {
            if(((GradientExtender)theGradientExtenders.get(i)).isEnabled()) {
                myGradient = ((GradientExtender)theGradientExtenders.get(i)).
                        getGradientBias(currentGradientOuts, j, myGradient);
            }
        }
        return myGradient;
    }
    
    /**
     * Gets the default (normal calculation of the) gradient for biases.
     *
     * @param currentGradientOuts the back protected gradients.
     * @param j the index of the bias.
     * @return the gradient for bias b_i.
     */
    public double getDefaultGradientBias(double[] currentGradientOuts, int j) {
        return currentGradientOuts[j];
    }
    
    /**
     * Gets the gradient for weights.
     *
     * @param aCurrentInps the forwarded input.
     * @param j the input index of the weight.
     * @param currentPattern the back propagated gradients.
     * @param k the output index of the weight.
     *
     * @return the gradient for the weight w_j_k
     */
    public double getGradientWeight(double[] currentInps, int j, double[] currentPattern, int k) {
        double myGradient = getDefaultGradientWeight(currentInps, j, currentPattern, k);
        
        for(int i = 0; i < theGradientExtenders.size(); i++) {
            if(((GradientExtender)theGradientExtenders.get(i)).isEnabled()) {
                myGradient = ((GradientExtender)theGradientExtenders.get(i)).
                        getGradientWeight(currentInps, j, currentPattern, k, myGradient);
            }
        }
        return myGradient;
    }
    
    /**
     * Gets the default (normal calculation of the) gradient for weights.
     *
     * @param aCurrentInps the forwarded input.
     * @param j the input index of the weight.
     * @param currentPattern the back propagated gradients.
     * @param k the output index of the weight.
     *
     * @return the gradient for the weight w_j_k
     */
    public double getDefaultGradientWeight(double[] currentInps, int j, double[] currentPattern, int k) {
        return currentInps[j] * currentPattern[k];
    }
    
    /**
     * Gives learners and extenders a change to do some pre-computing before the
     * biases are updated.
     *
     * @param currentGradientOuts the back propagated gradients.
     */
    protected final void preBiasUpdate(double[] currentGradientOuts) {
        preBiasUpdateImpl(currentGradientOuts);
        
        // update weight extender...
        if(theUpdateWeightExtender != null && theUpdateWeightExtender.isEnabled()) {
            theUpdateWeightExtender.preBiasUpdate(currentGradientOuts);
        }
        
        // delta rule extenders...
        for(int i = 0; i < theDeltaRuleExtenders.size(); i++) {
            if(((DeltaRuleExtender)theDeltaRuleExtenders.get(i)).isEnabled()) {
                ((DeltaRuleExtender)theDeltaRuleExtenders.get(i)).
                        preBiasUpdate(currentGradientOuts);
            }
        }
        
        // gradient extenders...
        for(int i = 0; i < theGradientExtenders.size(); i++) {
            if(((GradientExtender)theGradientExtenders.get(i)).isEnabled()) {
                ((GradientExtender)theGradientExtenders.get(i)).
                        preBiasUpdate(currentGradientOuts);
            }
        }
    }
    
    /**
     * Gives learners a change to do some pre-computing before the biases are
     * updated.
     *
     * @param currentGradientOuts
     */
    protected void preBiasUpdateImpl(double[] currentGradientOuts) {
        
    }
    
    /**
     * Gives learners and extenders a change to do some pre-computing before the
     * weights are updated.
     *
     * @param currentPattern the back propagated gradients.
     * @param currentInps the forwarded input.
     */
    protected final void preWeightUpdate(double[] currentPattern, double[] currentInps) {
        preWeightUpdateImpl(currentPattern, currentInps);
        
        // update weight extender...
        if(theUpdateWeightExtender != null && theUpdateWeightExtender.isEnabled()) {
            theUpdateWeightExtender.preWeightUpdate(currentInps, currentPattern);
        }
        
        // delta rule extenders...
        for(int i = 0; i < theDeltaRuleExtenders.size(); i++) {
            if(((DeltaRuleExtender)theDeltaRuleExtenders.get(i)).isEnabled()) {
                ((DeltaRuleExtender)theDeltaRuleExtenders.get(i)).
                        preWeightUpdate(currentInps, currentPattern);
            }
        }
        
        // gradient extenders...
        for(int i = 0; i < theGradientExtenders.size(); i++) {
            if(((GradientExtender)theGradientExtenders.get(i)).isEnabled()) {
                ((GradientExtender)theGradientExtenders.get(i)).
                        preWeightUpdate(currentInps, currentPattern);
            }
        }
    }
    
    /**
     * Gives learners a change to do some pre-computing before the weights are
     * updated.
     *
     * @param currentPattern the back propagated gradients.
     * @param currentInps the forwarded input.
     */
    protected void preWeightUpdateImpl(double[] currentPattern, double[] currentInps) {
        
    }
    
    /**
     * Gives learners and extenders a change to do some post-computing after the
     * biases are updated.
     *
     * @param currentGradientOuts the back propagated gradients.
     */
    protected final void postBiasUpdate(double[] currentGradientOuts) {
        // gradient extenders...
        for(int i = 0; i < theGradientExtenders.size(); i++) {
            if(((GradientExtender)theGradientExtenders.get(i)).isEnabled()) {
                ((GradientExtender)theGradientExtenders.get(i)).
                        postBiasUpdate(currentGradientOuts);
            }
        }
        
        // delta rule extenders...
        for(int i = 0; i < theDeltaRuleExtenders.size(); i++) {
            if(((DeltaRuleExtender)theDeltaRuleExtenders.get(i)).isEnabled()) {
                ((DeltaRuleExtender)theDeltaRuleExtenders.get(i)).
                        postBiasUpdate(currentGradientOuts);
            }
        }
        
        // update weight extenders...
        if(theUpdateWeightExtender != null && theUpdateWeightExtender.isEnabled()) {
            theUpdateWeightExtender.postBiasUpdate(currentGradientOuts);
        }
        
        postBiasUpdateImpl(currentGradientOuts);
    }
    
    /**
     * Gives learners a change to do some post-computing after the biases are
     * updated.
     *
     * @param currentGradientOuts the back propagated gradients.
     */
    protected void postBiasUpdateImpl(double[] currentGradientOuts) {
        
    }
    
    /**
     * Gives learners and extenders a change to do some post-computing after the
     * weights are updated.
     *
     * @param currentPattern the back propagated gradients.
     * @param currentInps the forwarded input.
     */
    protected final void postWeightUpdate(double[] currentPattern, double[] currentInps) {
        // gradient extenders...
        for(int i = 0; i < theGradientExtenders.size(); i++) {
            if(((GradientExtender)theGradientExtenders.get(i)).isEnabled()) {
                ((GradientExtender)theGradientExtenders.get(i)).
                        postWeightUpdate(currentInps, currentPattern);
            }
        }
        
        // delta extenders...
        for(int i = 0; i < theDeltaRuleExtenders.size(); i++) {
            if(((DeltaRuleExtender)theDeltaRuleExtenders.get(i)).isEnabled()) {
                ((DeltaRuleExtender)theDeltaRuleExtenders.get(i)).
                        postWeightUpdate(currentInps, currentPattern);
            }
        }
        
        // update weight extenders...
        if(theUpdateWeightExtender != null && theUpdateWeightExtender.isEnabled()) {
            theUpdateWeightExtender.postWeightUpdate(currentInps, currentPattern);
        }
        
        postWeightUpdateImpl(currentInps, currentInps);
    }
    
    /**
     * Gives learners a change to do some post-computing after the weights are
     * updated.
     *
     * @param currentPattern the back propagated gradients.
     * @param currentInps the forwarded input.
     */
    protected void postWeightUpdateImpl(double[] currentPattern, double[] currentInps) {
        
    }
    
    /**
     * Adds a delta extender.
     *
     * @param aDeltaRuleExtender the delta rule extender to add.
     */
    public void addDeltaRuleExtender(DeltaRuleExtender aDeltaRuleExtender) {
        // Note one needs to be careful to the order of the extenders,
        // also note that basic and batch learner add a delta (momentum)
        // extender in their constructor
        
        theDeltaRuleExtenders.add(aDeltaRuleExtender);
        
        aDeltaRuleExtender.setLearner(this);
    }
    
    /**
     * Adds a gradient extender.
     *
     * @param aGradientExtender the gradient extender to add.
     */
    public void addGradientExtender(GradientExtender aGradientExtender) {
        theGradientExtenders.add(aGradientExtender);
        
        aGradientExtender.setLearner(this);
    }
    
    
    /**
     * Sets an update weight extender.
     *
     * @param anUpdateWeightExtender the update weight extender to set.
     */
    public void setUpdateWeightExtender(UpdateWeightExtender anUpdateWeightExtender) {
        theUpdateWeightExtender = anUpdateWeightExtender;
        
        theUpdateWeightExtender.setLearner(this);
    }
    
    /**
     * Gets the update weight extender.
     *
     * @return the update weight extender.
     */
    public UpdateWeightExtender getUpdateWeightExtender() {
        return theUpdateWeightExtender;
    }
}
