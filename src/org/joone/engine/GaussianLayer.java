package org.joone.engine;

import java.util.ArrayList;
import java.util.Collection;
import org.joone.exception.JooneRuntimeException;
import org.joone.log.*;
import java.util.TreeSet;
import org.joone.inspection.implementations.BiasInspection;
import org.joone.net.NetCheck;

/** <P>This layer implements the Gaussian Neighborhood SOM strategy.  It receives
 * the euclidean distances between the input vector and weights and calculates the
 * distance fall off between the winning node and all other nodes.  These are
 * passed back allowing the previous synapse to adjust it's weights.</P>
 * <P>The distance fall off is calculated according to a Gaussian distribution from
 * the winning node.  This layer uses implemtations of SpatialMap in order to
 * calculate these distances.  Currently this layer uses the GaussianSpatialMap
 * which calculates the Gaussian distance for all nodes in the SOM map.  Future
 * maps will allow distance calculations based on a specific shape such as a circle
 * , square or diamond.  Currently the GuassianLayer supports 3D SOM maps.</P>
 * @see SimpleLayer parent
 */
public class GaussianLayer extends SimpleLayer implements NeuralNetListener {

    private static final ILogger log = LoggerFactory.getLogger (GaussianLayer.class);
    private static final long serialVersionUID = -941653911909171430L;
    
    // Width of the map in the this layer.
    private int LayerWidth = 1;
    // Height of the map in the this layer.
    private int LayerHeight = 1;
    // Depth of the map in the this layer.
    private int LayerDepth = 1;     
    
    private SpatialMap space_map;
    private double timeConstant = 200.0;
    private int orderingPhase = 1000;
    private double initialGaussianSize = 10;

    /** <P>The default constructor for this GaussianLayer.</P> */    
    public GaussianLayer() {
        super();
    }
    /** The constructor that takes a name of the layer.
     * @param ElemName The name of the Layer
     */    
    public GaussianLayer(java.lang.String ElemName) {
        super(ElemName);
    }
    
    /** <P>This method has a blank body as there are no biases to adjust.</P>
     * @param pattern Not used. The pattern to process and pass back.
     * @throws JooneRuntimeException The run time exception.
     */
    public void backward(double[] pattern) 
        throws JooneRuntimeException        
    { 
    }
    
    /** <P>This method takes as input an array of euclidean distances between the input and
     * weights calculated by the previous synapse.  This method calculates the Gaussian
     * distance fall off between the winning neuron and all other nodes.  These distances are passed on to the next synapse.</P>
     * @param pattern The pattern containing the euclidean distances from the previous synapse.
     * @see NeuralLayer#forward (double[])
     * @throws JooneRuntimeException This <code>Exception </code> is  a wrapper Exception when an Exception is thrown
     *                               while doing the maths.
     */
    public void forward (double[] pattern) 
        throws JooneRuntimeException       
    {
        try
        {
            getSpace_map().ApplyNeighborhoodFunction(pattern,outs, getMonitor().isLearning());
        }
        catch (Exception aioobe) 
        {
            String msg;
            log.error ( msg = "Exception thrown while processing the pattern " + pattern.toString()
                        + " Exception thrown is " + aioobe.getClass ().getName () + ". Message is " + aioobe.getMessage() );
            throw new JooneRuntimeException (msg, aioobe);
            //aioobe.printStackTrace(); 
        }
    }
    
    /** Getter for property LayerDepth.
     * @return Value of property LayerDepth.
     *
     */
    public int getLayerDepth() {
        return LayerDepth;
    }
    
    /** Setter for property LayerDepth.
     * @param layerDepth New value of property LayerDepth.
     *
     */
    public void setLayerDepth(int layerDepth) {
        if ( layerDepth != getLayerDepth() )
        {
            this.LayerDepth = layerDepth;
            setRows(getLayerWidth()*getLayerHeight()*getLayerDepth());
            setDimensions();
            setConnDimensions();
            getSpace_map().setMapDepth(layerDepth);
        }
    }
    
    /** Getter for property LayerHeight.
     * @return Value of property LayerHeight.
     *
     */
    public int getLayerHeight() {
        return LayerHeight;
    }
    
    /** Setter for property LayerHeight.
     * @param LayerHeight New value of property LayerHeight.
     *
     */
    public void setLayerHeight(int LayerHeight) {
        if ( LayerHeight != getLayerHeight() )
        {
            this.LayerHeight = LayerHeight;
            setRows(getLayerWidth()*getLayerHeight()*getLayerDepth());
            setDimensions();
            setConnDimensions();
            getSpace_map().setMapHeight(LayerHeight);
        }
    }
    
    /** Getter for property LayerWidth.
     * @return Value of property LayerWidth.
     *
     */
    public int getLayerWidth() {
        return LayerWidth;
    }
    
    /** Setter for property LayerWidth.
     * @param LayerWidth New value of property LayerWidth.
     *
     */
    public void setLayerWidth(int LayerWidth) {
        if ( LayerWidth != getLayerWidth() )
        {
            this.LayerWidth = LayerWidth;
            setRows(getLayerWidth()*getLayerHeight()*getLayerDepth());
            setDimensions();
            setConnDimensions();
            getSpace_map().setMapWidth(LayerWidth);
        }
    }

    /** Gets the largest layer dimension size.
     * @return The size of the largest dimension, width , height or depth.
     */
    public int getLargestDimension()
    {
        int max = 1;
        if ( getLayerWidth() > max)
            max = getLayerWidth();
        if ( getLayerHeight() > max)
            max = getLayerHeight();
        if ( getLayerDepth() > max)
            max = getLayerDepth();
        
        return(max);
    }
    
    /** <P>Check that there are no errors or problems with the properties of this
     * GaussianLayer.</P>
     * @return The TreeSet of errors / problems if any.
     */
    public TreeSet check() {
        TreeSet checks = super.check();
        
        if ( getLayerWidth() < 1 )
            checks.add(new NetCheck(NetCheck.FATAL, "Layer width should be greater than or equal to 1." , this));

        if ( getLayerHeight() < 1 )
            checks.add(new NetCheck(NetCheck.FATAL, "Layer height should be greater than or equal to 1." , this));
        
        if ( getLayerDepth() < 1 )
            checks.add(new NetCheck(NetCheck.FATAL, "Layer depth should be greater than or equal to 1." , this));
        
        if (getOrderingPhase() > getMonitor().getTotCicles())
            checks.add(new NetCheck(NetCheck.WARNING, "Ordering phase should be lesser than or equal to the number of epochs" , this));
        return checks;
    }

    public void start() {
        if (getMonitor() != null) {
            getMonitor().addNeuralNetListener(this, false);
        }
        super.start();
    }
    
    /** <P>Initialises the time constant used to decrease the size of the spatial
     * map.</P>
     * @param e The original Net Event.
     */
    public void netStarted(NeuralNetEvent e) {
        getSpace_map().init( getMonitor().getTotCicles());
        space_map.setInitialGaussianSize(getLargestDimension());
    }
    
    /** <P>Updates the Gaussian Size if in learning mode.</P>
     * @param e The original Net Event.
     */
    public void cicleTerminated(NeuralNetEvent e) {
        if ( getMonitor().isLearning() )
        {
           getSpace_map().updateCurrentGaussianSize(getMonitor().getTotCicles()-getMonitor().getCurrentCicle()); 
        }
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
        getSpace_map().setOrderingPhase(orderingPhase);
    }
    
    /** Getter for property timeConstant.
     * @return Value of property timeConstant.
     *
     */
    public double getTimeConstant() {
        return timeConstant;
    }
    
    /** Setter for property timeConstant.
     * @param timeConstant New value of property timeConstant.
     *
     */
    public void setTimeConstant(double timeConstant) {
        this.timeConstant = timeConstant;
        getSpace_map().setTimeConstant(timeConstant);
    }
    
    /** Getter for property space_map.
     * @return Value of property space_map.
     *
     */
    protected org.joone.engine.SpatialMap getSpace_map() {
        if (space_map == null) {
            space_map = new GaussianSpatialMap();
            space_map.setMapDepth(getLayerDepth());
            space_map.setMapHeight(getLayerHeight());
            space_map.setMapWidth(getLayerWidth());
            space_map.setInitialGaussianSize(getInitialGaussianSize());
            space_map.setOrderingPhase(getOrderingPhase());
            space_map.setTimeConstant(getTimeConstant());
    }
        return space_map;
    }
        
    /** Getter for property initialGaussianSize.
     * @return Value of property initialGaussianSize.
     *
     */
    public double getInitialGaussianSize() {
        return initialGaussianSize;
    }
    
    /** Setter for property initialGaussianSize.
     * @param initialGaussianSize New value of property initialGaussianSize.
     *
     */
    public void setInitialGaussianSize(double initialGaussianSize) {
        this.initialGaussianSize = initialGaussianSize;
        getSpace_map().setInitialGaussianSize(initialGaussianSize);
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

    public void netStoppedError(NeuralNetEvent e, String error) {
    }

    public void errorChanged(NeuralNetEvent e) {
    }

    public void netStopped(NeuralNetEvent e) {
    }
        
}