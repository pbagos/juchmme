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
public class GaussianSpatialMap extends SpatialMap {
    
    private static final long serialVersionUID = -5578079370364572387L;
    
    /** Creates a new instance of CircularSpatialMap */
    public GaussianSpatialMap() {
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
        
        //if (isLearning)
        nbhRadius = getCurrentGaussianSize();            // Get Current Neighbourhood Radius
        nbhRadiusSq = nbhRadius * nbhRadius;        // Neighbourhood Radius Squared.
        
        // Loop through the map and set the neighborhood function (individual learning rate) of each neighborhood output.
        for (int z=0;z<getMapDepth();z++){
            for (int y=0; y<getMapHeight(); y++) {
                for (int x=0; x<getMapWidth(); x++) {
                    dist_to_node = distanceBetween(winx,winy,winz,x,y,z);
                    dFalloff = getCircle2DDistanceFalloff(dist_to_node, nbhRadiusSq);
                    current_output = x+(y* getMapWidth())+(z*( getMapWidth()*getMapHeight()));
                    n_outs[current_output] = dFalloff;
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
