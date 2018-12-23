package org.joone.engine;

import java.util.ArrayList;
import java.util.Collection;
import org.joone.exception.JooneRuntimeException;
import org.joone.log.*;
import java.util.TreeSet;
import org.joone.inspection.implementations.BiasInspection;
import org.joone.net.NetCheck;

/** <P>This layer implements the Winner Takes All SOM strategy.  The layer
 * expects to receive euclidean distances between the previous synapse weights and
 * it's input.  The layer simply works out which node is the winner and passes 1.0
 * for that node and 0.0 for the others.</P>
 * @see SimpleLayer parent
 */
public class WTALayer extends SimpleLayer {

    private static final ILogger log = LoggerFactory.getLogger (WTALayer.class);
    private static final long serialVersionUID = -941653911909171430L;
    
    // Width of the map in the this layer.
    private int LayerWidth = 1;
    // Height of the map in the this layer.
    private int LayerHeight = 1;
    // Depth of the map in the this layer.
    private int LayerDepth = 1;     
    
    /** The default constructor for this WTALayer. */    
    public WTALayer() {
        super();
    }
    /** The constructor allowing a name to be specified.
     * @param ElemName The name of the Layer
     */    
    public WTALayer(java.lang.String ElemName) {
        super(ElemName);
    }
    
    /** <P>No biases need updating or setting.  Not implemented / not required.</P>
     * @param pattern The pattern with which to update internal variables.  Not required.
     * @throws JooneRuntimeException The Joone Run time exception.
     */
    public void backward(double[] pattern) 
        throws JooneRuntimeException        
    { 
    }
    
    /** This method accepts an array of values from the input and forwards it
     * according to the Winner Takes All strategy.  See class documentation.
     * @param pattern <P>Should be the euclidean distance between the previous synapse's input vector and
     * weights.</P>
     * @see NeuralLayer#forward (double[])
     * @throws JooneRuntimeException This <code>Exception </code> is  a wrapper Exception when an Exception is thrown
     *                               while doing the maths.
     */
    public void forward (double[] pattern) 
        throws JooneRuntimeException       
    {
        int x = 0;
        double in = 0f;
        int winner = 0;
        double min_dist = 9999999999999f;
        int n = getRows();
        try 
        {
            for ( x = 0; x < n; ++x) {
                in = pattern[x];
                if ( in < min_dist)
                { 
                    min_dist = in; 
                    winner = x; 
                }
            }
            for ( x = 0; x < n; ++x) {   
                if ( x == winner)
                { 
                    outs[x] = 1f;
                }
                else
                    outs[x] = 0f;
            }
        }catch (Exception aioobe) 
        {
            String msg;
            log.error ( msg = "Exception thrown while processing the element " + x + " of the array. Value is : " + pattern[x]
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
     * @param LayerDepth New value of property LayerDepth.
     *
     */
    public void setLayerDepth(int LayerDepth) {
        if ( LayerDepth != getLayerDepth() )
        {
            this.LayerDepth = LayerDepth;
            setRows(getLayerWidth()*getLayerHeight()*getLayerDepth());
            setDimensions();
            setConnDimensions();
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
        }
    }
    
    /**
     * Check that there are no errors or problems with the properties of this WTALayer.
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
        
        return checks;
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