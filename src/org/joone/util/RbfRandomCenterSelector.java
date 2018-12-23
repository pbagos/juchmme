/*
 * RbfRandomCenterSelector.java
 *
 * Created on August 11, 2004, 5:31 PM
 */

package org.joone.util;

import java.util.*;
import org.joone.engine.*;
import org.joone.log.*;

/**
 * This plug in is used to select fixed centers for Gaussian RBF layers randomly
 * from the input data. Therefore, I implemented the selector as a plug in, 
 * because this way I can easily select centers randomly from the input.
 *
 * @author Boris Jansen
 */
public class RbfRandomCenterSelector extends ConverterPlugIn {
    
    /** The logger. */
    private static final ILogger log = LoggerFactory.getLogger(RbfRandomCenterSelector.class);
    
    /** The RBF layer (to get the number of neurons). */
    private RbfGaussianLayer theRbfGaussianLayer;
    
    /** Save the input vectors. */
    private Vector thePatterns = null;
    
    /** 
     * Creates a new instance of RbfRandomCenterSelector 
     *
     * @param aRbfGaussianLayer the RBF Gaussian layer to get the number of nodes.
     */
    public RbfRandomCenterSelector(RbfGaussianLayer aRbfGaussianLayer) {
        theRbfGaussianLayer = aRbfGaussianLayer;
        setAdvancedSerieSelector("1");
    }
    
    protected boolean convert(int serie) {
        // do nothing, we don't convert any data...
        
        // just save the input vectors
        if(thePatterns == null) {
            thePatterns = (Vector)getInputVector();
        }
        return false;
    }
    
    /**
     * Gets the parameters for the different nodes in a RBF layer.
     *
     * @return the parameters for the different nodes in a RBF layer.
     */
    public RbfGaussianParameters[] getGaussianParameters() {
        // There should be no plug ins after this plug in that convert data,
        // otherwise nonsense centers are selected...
        if(thePatterns.size() < theRbfGaussianLayer.getRows()) {
            log.warn("There are more neurons in RBF layer than training patterns -> " + 
                "not all nodes in RBF layer will be assigned a unique center.");
        }
        
        int[] myCenters = new int[theRbfGaussianLayer.getRows()];
        for(int i = 0; i < theRbfGaussianLayer.getRows(); i++) {
            int myCenter = (int)(Math.random() * thePatterns.size());
            if(i < thePatterns.size()) {
                // there exist a non-selected center
                boolean myNonSelected = true;
                do {
                    if(!myNonSelected) { // the selected center is already selected
                        //myCenter = (myCenter + 1) % thePatterns.size(); // THIS IS NOT RANDOM
                        myCenter = (int)(Math.random() * thePatterns.size());
                        myNonSelected = true;
                    }
                    for(int j = 0; j < i; j++) {
                        if(myCenters[j] == myCenter) {
                            myNonSelected = false;
                        }
                    }
                } while(!myNonSelected);
            }
            myCenters[i] = myCenter;
        }
        
        double myD = getMaxDistance(thePatterns, myCenters);
        // the following definition of the standard deviation ensures that the Gaussian
        // functions are not too peaked or too flat; both extremes are to be avoided.
        double myStdDeviation = myD / Math.sqrt(2 * theRbfGaussianLayer.getRows());
        
        RbfGaussianParameters[] myParameters = new RbfGaussianParameters[theRbfGaussianLayer.getRows()];
        for(int i = 0; i < theRbfGaussianLayer.getRows(); i++) {
            double[] myCenter = (double[])((Pattern)thePatterns.get(myCenters[i])).getArray().clone();
            myParameters[i] = new RbfGaussianParameters(myCenter, myStdDeviation);
            
            // info
            /*
            String myText = "Gaussian Parameters [StdDeviation:" + myStdDeviation + ", center:";
            for(int j = 0; j < myCenter.length; j++) {
                myText += myCenter[j];
                if(j != myCenter.length-1) {
                    myText += ",";
                } else {
                    myText += "]";
                }
            }
            log.info(myText);
             */
        }
        return myParameters;
    }
    
    /**
     * Gets the maximum distance between centers.
     *
     * @param aPatterns all the input patterns (which might have been selected
     * to become a center).
     * @param anIndexes the indexes of the selected centers.
     */
    protected double getMaxDistance(Vector aPatterns, int[] anIndexes) {
        double myMax = -1.0;
        double myDistance;
        for(int i = 0; i < anIndexes.length - 1; i++) {
            for(int j = i + 1; j < anIndexes.length; j++) {
                myDistance = getDistance((Pattern)aPatterns.get(anIndexes[i]), (Pattern)aPatterns.get(anIndexes[j]));
                if(myDistance > myMax) {
                    myMax = myDistance;
                }
            }
        }
        return myMax;
    }
    
    /**
     * Gets the distance between two centers.
     *
     * @param aCenter1 the first center.
     * @param aCenter2 the second center.
     * @return the distance between the two centers.
     */
    protected double getDistance(Pattern aCenter1, Pattern aCenter2) {
        double myDistance = 0;
        for(int i = 0; i < aCenter1.getArray().length; i++) {
            double myDiff = aCenter1.getArray()[i] - aCenter2.getArray()[i];
            myDistance += myDiff * myDiff;
        }
        return Math.sqrt(myDistance);
    }
}
