/*
 * RbfGaussianLayer.java
 *
 * Created on July 21, 2004, 3:15 PM
 */

package org.joone.engine;

import java.util.ArrayList;
import java.util.Collection;
import org.joone.exception.JooneRuntimeException;
import org.joone.inspection.implementations.BiasInspection;
import org.joone.io.StreamInputSynapse;
import org.joone.log.*;
import org.joone.util.*;

/**
 * This class implements the nonlinear layer in Radial Basis Function (RBF) 
 * networks using Gaussian functions.
 *
 * @author Boris Jansen
 */
public class RbfGaussianLayer extends RbfLayer {
    
    /** Logger for this class. */
    private static final ILogger log = LoggerFactory.getLogger(RbfGaussianLayer.class);
    
    /** The parameters for the different Gaussian RBFs (neurons) */
    private RbfGaussianParameters[] theGaussianParameters;
    
    /** Flag indication if we should use randomly chosen fixed centers. */
    private boolean theUseRandomSelector = true;
    
    /** The random selector (if theUseRandomSelector equals true). */
    private RbfRandomCenterSelector theRandomSelector;
    
    /** Creates a new instance of RbfGaussianLayer */
    public RbfGaussianLayer() {
    }
    
    protected void backward(double[] pattern) throws org.joone.exception.JooneRuntimeException {
        // we don't use back propagation (doesn't make sense for RBF layers), so 
        // the rule is copy gradientInps to gradientOuts
        for(int i = 0; i < gradientInps.length; i++) {
            gradientOuts[i] = gradientInps[i];
        }
    }

    protected void forward(double[] pattern) throws org.joone.exception.JooneRuntimeException {
        if(theUseRandomSelector && theGaussianParameters == null) {
            setGaussianParameters(theRandomSelector.getGaussianParameters());
        }
        
        int i = 0;
        try {
            // for every RBF neuron
            for(i = 0; i < getRows(); i++) {
                // perform Gaussian function on pattern...
                
                // Calculate squared Euclidian distance
                double mySquaredEuclDist = 0;
                double myTemp;
                for(int j = 0; j < pattern.length; j++) {
                    myTemp = pattern[j] - theGaussianParameters[i].getMean()[j];
                    mySquaredEuclDist += (myTemp * myTemp);
                }
                outs[i] = Math.exp(mySquaredEuclDist / 
                    (-2 * theGaussianParameters[i].getStdDeviation() * theGaussianParameters[i].getStdDeviation()));
                //log.debug("Gaussian: " + outs[i]);
            }
        } catch (Exception aioobe) {
            aioobe.printStackTrace();
            String msg;
            log.error(msg = "Exception thrown while processing the element " + i + " of the array. Value is : " + pattern[i]
                + " Exception thrown is " + aioobe.getClass().getName() + ". Message is " + aioobe.getMessage());
            throw new JooneRuntimeException(msg, aioobe);
        }
    }
    
    protected void setDimensions() {
        super.setDimensions();
        
        // we don't use back propagation in RBF layers, but the rule is to
        // copy gradientInps to gradientOuts, so here we set the size of 
        // gradientOuts to gradientInps
        gradientOuts = new double[gradientInps.length];
    }
    
    /**
     * Gets the parameters that define the Gaussian RBFs.
     *
     * @return the Gaussian RBFs parameters.
     */
    public RbfGaussianParameters[] getGaussianParameters() {
        return theGaussianParameters;
    }
    
    /**
     * Sets the parameters that define the Gaussian RBFs.
     *
     * @param aGaussianParameters The new parameters for the RBFs.
     */
    public void setGaussianParameters(RbfGaussianParameters[] aGaussianParameters) {
        if(aGaussianParameters.length != getRows()) {
            setRows(aGaussianParameters.length);
            log.warn("Setting new RBF Gaussian parameters -> # neurons changed.");
        }
        theGaussianParameters = aGaussianParameters;
    }
    
    /**
     * Sets the Gaussian parameters to centers chosen randomly from the input/training data.
     *
     * @param aStreamInput the synapse providing the input, from where we will select random centers.
     */
    public void useRandomCenter(StreamInputSynapse aStreamInput) {
        theUseRandomSelector = true;
        theRandomSelector = new RbfRandomCenterSelector(this);
        // find last plugin of aStreamInput and attach the random selector plug in at the end
        if(aStreamInput.getPlugIn() == null) {
            aStreamInput.setPlugIn(theRandomSelector);
        } else {
            AbstractConverterPlugIn myPlugin = aStreamInput.getPlugIn();
//            while(myPlugin.getNextPlugIn() != null) {
//                myPlugin = myPlugin.getNextPlugIn();
//            }
            myPlugin.addPlugIn(theRandomSelector);
        }
    }
    
    /**
     * It doesn't make sense to return biases for this layer
     * @return null
     */
    public Collection Inspections() {
        Collection col = new ArrayList();
        col.add(new BiasInspection(null));
        return col;
    }
    
}
