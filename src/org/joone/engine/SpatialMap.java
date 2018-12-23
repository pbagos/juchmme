/*
 * SpatialMap.java
 *
 */

package org.joone.engine;

/** <P>SpatialMap is intended to be an abstract spatial map for use with a
 * GaussianLayer.  Custom SpatialMap's need to extend the ApplyNeighborhood method
 * and implement it based on their own spatial shape implementation.  
 * The Gaussian spatial size is updated if the current epoch is less than the ordering phase, 
 * it is also reduced over the ordering phase based on the time constant.</P>
 */
public abstract class SpatialMap implements java.io.Serializable {
    
    private double InitialGaussianSize = 1; // The initial Gaussian size.
    
    private double CurrentGaussianSize = 1; // The current Gaussian size.
    
    private int map_width = 1;  // Width of map.
    private int map_height = 1; // Height of map.
    private int map_depth = 1;  // Depth of the map.
    
    private int win_x = 0, win_y = 0, win_z= 0; // The winning neuron.
    
    private int TotalEpochs = 1;
    private int orderingPhase;
    
    // TimeConstant
    double TimeConstant = 1;
    
    /** <P>Initialises this spatial map according to the total number of
     * epochs/cycles.</P>
     * @param total_epochs The total number of epochs that will be used.
     */    
    public final void init(int total_epochs) {
//        TimeConstant = total_epochs / Math.log(getInitialGaussianSize());
//        TotalEpochs = total_epochs;
        updateCurrentGaussianSize(1);
    }
    
    /** <P>Gets the total number of epochs for the current session.</P>
     * @return The total number of epochs for the current session.
     */    
    public final int getTotalEpochs() {
        return(TotalEpochs);
    }
    
    /** <P>Sets the initial guassian size of the spatial neighborhood.</P>
     * @param size The size of the neighborhood.
     */
    public final void setInitialGaussianSize(double size) {
        setCurrentGaussianSize(size);
        InitialGaussianSize = size;
    }
    
    /** <P>Gets the size of the spatial neighborhood.</P>
     * @return The size of the spatial neighborhood.
     */
    public final double getInitialGaussianSize() {
        return(InitialGaussianSize);
    }
    
    /** <P>Sets the current guassian size of the spatial neighborhood.</P>
     * @param size The current guassian size of the spatial neighborhood.
     */
    public final void setCurrentGaussianSize(double size) {
        CurrentGaussianSize = size;
    }
    
    /** <P>Gets the current gaussian size of the spatial neighborhood.</P>
     * @return The current gaussian size of the spatial neighborhood.
     */
    public final double getCurrentGaussianSize() {
        return(CurrentGaussianSize);
    }
    
    /** <P>Updates the current Gaussian Size depending on the current epoch and the time
     * constant.</P>
     * @param current_epoch The current epoch or cycle.
     */
    public final void updateCurrentGaussianSize(int current_epoch) {
        if (current_epoch < getOrderingPhase())
            setCurrentGaussianSize( getInitialGaussianSize() * Math.exp(-(current_epoch/getTimeConstant())) );
        else
            setCurrentGaussianSize(0.01);
    }
    
    /** <P>Applies the neighborhood strategy based on this spatial maps
     * implementation.</P>
     * @param distances The euclidean distances between input and weights calculated by previous
     * synapse.
     * @param n_outs The outputs of this spatial maps neighborhood strategy.
     * @param isLearning Is the network in the learning phase.
     */    
    abstract public void ApplyNeighborhoodFunction(double [] distances, double [] n_outs, boolean isLearning);
    
    /** Extracts the X,Y,Z co-ordinates of the winning neuron in this spatial map.  The co-ordinates are placed into
     * internal variables,  Co-ordinates can be accessed by using the getWinnerX() , getWinnerY() , getWinnerZ() methods.
     * A neuron is considered the winner if it's distance between the input and weights vector is the smallest.
     * @param distances The distances between the input and weights vector, this should be passed in by the
     * previous synapse.
     */
    protected final void extractWinner(double [] distances) {
        int current_output = 0;
        double curDist = 0f;
        double bestDist = 999999999999999f;
        for (int z=0;z<getMapDepth();z++){
            for (int y=0; y<getMapHeight(); y++) {
                for (int x=0; x<getMapWidth(); x++) {
                    current_output = x+(y* getMapWidth())+(z*( getMapWidth()*getMapHeight()));
                    curDist = distances[current_output];
                    if ( curDist < bestDist ) {
                        bestDist = curDist;
                        win_x = x;
                        win_y = y;
                        win_z = z;
                    }
                }
            }
        }
    }
    
    /** <P>Returns the X Co-ordinate of the current winning neuron.</P>
     * @return The X Co-ordinate of the current winning neuron.
     */
    protected final int getWinnerX() {
        return(win_x);
    }
    
    /** <P>Returns the Y Co-ordinate of the current winning neuron.</P>
     * @return The Y Co-ordinate of the current winning neuron.
     */
    protected final int getWinnerY() {
        return(win_y);
    }
    
    /** <P>Returns the Z Co-ordinate of the current winning neuron.</P>
     * @return The Z Co-ordinate of the current winning neuron.
     */
    protected final int getWinnerZ() {
        return(win_z);
    }
    
    /** <P>Sets the dimensions of the spatial map.  Allows dimension setting in one
     * call.</P>
     * @param x The x size or width of the map.
     * @param y The y size or height of the map.
     * @param z The z size of depth of the map.
     */
    public final void setMapDimensions(int x,int y,int z) {
        setMapWidth(x);
        setMapHeight(y);
        setMapDepth(z);
    }
    
    /** Sets the width of this spatial map.
     * @param w The width or x size of the map.
     */
    public final void setMapWidth(int w) {
        if ( w> 0)
            map_width = w;
        else map_width=1;
    }
    
    /** Sets the height of this spatial map.
     * @param h The height or y size of the map.
     */
    public final void setMapHeight(int h) {
        if ( h>0)
            map_height=h;
        else map_height=1;
    }
    
    /** Sets the depth of this spatial map.
     * @param d The depth or z size of the map.
     */
    public final void setMapDepth(int d) {
        if ( d>0)
            map_depth = d;
        else map_depth=1;
    }
    
    /** <P>Gets the width of this spatial map.</P>
     * @return The width of this spatial map.
     */
    public final int getMapWidth() {
        return(map_width);
    }
    
    /** <P>Gets the height of this spatial map.</P>
     * @return The height of this spatial map.
     */
    public final int getMapHeight() {
        return(map_height);
    }
    
    /** <P>Gets the depth of this spatial map.</P>
     * @return The depth of this spatial map.
     */
    public final int getMapDepth() {
        return(map_depth);
    }
    
    /** <P>Calculates the squared distance between vector (x1,y1,z1) and (x2,y2,z2) and returns the
     * result.</P>
     * @return The squared distance between vector (x1,y1,z1) and (x2,y2,z2)
     * @param x1 The x location of the first vector.
     * @param y1 The y location of the first vector.
     * @param z1 The z location of the first vector.
     * @param x2 The x location of the second vector.
     * @param y2 The y location of the second vector.
     * @param z2 The z location of the second vector.
     */
    protected final double distanceBetween(int x1,int y1,int z1,int x2,int y2,int z2) {
        int xleg=0,yleg=0,zleg=0;
        xleg = (x1-x2);
        xleg *= xleg;
        yleg = (y1-y2);
        yleg *= yleg;
        zleg = (z1-z2);
        zleg*=zleg;
        return( xleg + yleg + zleg);
    }
    
    /** Getter for property orderingPhase.
     * @return Value of property orderingPhase.
     *
     */
    public int getOrderingPhase() {
        return orderingPhase;
    }
    
    /** Setter for property orderingPhase.
     * @param orderingPhase New value of property orderingPhase.
     *
     */
    public void setOrderingPhase(int orderingPhase) {
        this.orderingPhase = orderingPhase;
    }
    
    /** Getter for property TimeConstant.
     * @return Value of property TimeConstant.
     *
     */
    public double getTimeConstant() {
        return TimeConstant;
    }
    
    /** Setter for property TimeConstant.
     * @param TimeConstant New value of property TimeConstant.
     *
     */
    public void setTimeConstant(double TimeConstant) {
        this.TimeConstant = TimeConstant;
    }
    
}
