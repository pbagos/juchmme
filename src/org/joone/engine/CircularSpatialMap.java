/*
 * CircularSpatialMap.java
 *
 * Created on 2003/6/13 11:34
 */

package org.joone.engine;

/**
 * This class implements the SpatialMap interface providing a circular spatial map for use with the GaussianLayer and Kohonen Networks.
 * The radius of the circle is equal to the initial Gaussian Size and is reduced if training is currently in process.
 */
public class CircularSpatialMap extends SpatialMap {

    private static final long serialVersionUID = 442118480555350769L;
    
    /** Creates a new instance of CircularSpatialMap */
    public CircularSpatialMap() {
    }
    
    public void ApplyNeighborhoodFunction(double[] distances, double[] n_outs, boolean isLearning) {
        
        double dFalloff=0;
        double nbhRadius=1;         // Neighbourhood radius
        double nbhRadiusSq = 1;
        double dist_to_node=0;
        int current_output = 0;
        
        // Extract the winning neuron from the distances passed in by the synapse/layer.
        extractWinner(distances);
        
        int winx = getWinnerX();
        int winy = getWinnerY();
        int winz = getWinnerZ();
        
        nbhRadius = getCurrentGaussianSize();            // Get Current Neighbourhood Radius 
        nbhRadiusSq = nbhRadius * nbhRadius;        // Neighbourhood Radius Squared.

         // Loop through the map and set the neighborhood function (individual learning rate) of each neighborhood output.
        for (int z=0;z<getMapDepth();z++){   
             for (int y=0; y<getMapHeight(); y++) {
                for (int x=0; x<getMapWidth(); x++) {  
                    dist_to_node = distanceBetween(winx,winy,winz,x,y,z);
                    if (dist_to_node <= nbhRadiusSq) {
                        dFalloff = getCircle2DDistanceFalloff(dist_to_node, nbhRadiusSq);
                        current_output = x+(y* getMapWidth())+(z*( getMapWidth()*getMapHeight()));
                        n_outs[current_output] = dFalloff;
                    }
                    else    // Set to 0
                    {
                        current_output = x+(y* getMapWidth())+(z*( getMapWidth()*getMapHeight()));
                        n_outs[current_output] = 0;
                    }
                }
            }
         }  
    }

    /**
     * Gets the fall off distance from the edge of the radius.
     * @param distSq The square of the distance to the output/node being measured.
     * @param radiusSq The square of the radius of the current circular spatial neighborhood.
     * @return The fall off distance between the distSq and the radiusSq. 
     */
    private double getCircle2DDistanceFalloff(double distSq, double radiusSq) {
            return Math.exp(-(distSq)/(2 * radiusSq));
    }
    
}
