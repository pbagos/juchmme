/*
 * MatrixBeanInfo.java
 *
 * Created on 28 aprile 2004, 23.40
 */

package org.joone.engine;

import java.beans.*;

/**
 * @author paolo
 */
public class MatrixBeanInfo extends SimpleBeanInfo {
    
    // Bean descriptor //GEN-FIRST:BeanDescriptor
    /*lazy BeanDescriptor*/
    private static BeanDescriptor getBdescriptor(){
        BeanDescriptor beanDescriptor = new BeanDescriptor  ( Matrix.class , null );//GEN-HEADEREND:BeanDescriptor
        
        // Here you can add code for customizing the BeanDescriptor.
        
        return beanDescriptor;         }//GEN-LAST:BeanDescriptor
    
    
    // Property identifiers //GEN-FIRST:Properties
    private static final int PROPERTY_enabled = 0;
    private static final int PROPERTY_fixed = 1;
    private static final int PROPERTY_m_cols = 2;
    private static final int PROPERTY_m_rows = 3;
    private static final int PROPERTY_value = 4;

    // Property array 
    /*lazy PropertyDescriptor*/
    private static PropertyDescriptor[] getPdescriptor(){
        PropertyDescriptor[] properties = new PropertyDescriptor[5];
    
        try {
            properties[PROPERTY_enabled] = new PropertyDescriptor ( "enabled", Matrix.class, "getEnabled", "setEnabled" );
            properties[PROPERTY_fixed] = new PropertyDescriptor ( "fixed", Matrix.class, "getFixed", "setFixed" );
            properties[PROPERTY_m_cols] = new PropertyDescriptor ( "m_cols", Matrix.class, "getM_cols", "setM_cols" );
            properties[PROPERTY_m_rows] = new PropertyDescriptor ( "m_rows", Matrix.class, "getM_rows", "setM_rows" );
            properties[PROPERTY_value] = new PropertyDescriptor ( "value", Matrix.class, "getValue", "setValue" );
        }
        catch( IntrospectionException e) {}//GEN-HEADEREND:Properties
        
        // Here you can add code for customizing the properties array.
        
        return properties;         }//GEN-LAST:Properties
    
    // EventSet identifiers//GEN-FIRST:Events

    // EventSet array
    /*lazy EventSetDescriptor*/
    private static EventSetDescriptor[] getEdescriptor(){
        EventSetDescriptor[] eventSets = new EventSetDescriptor[0];//GEN-HEADEREND:Events
        
        // Here you can add code for customizing the event sets array.
        
        return eventSets;         }//GEN-LAST:Events
    
    // Method identifiers //GEN-FIRST:Methods
    private static final int METHOD_addNoise0 = 0;
    private static final int METHOD_clear1 = 1;
    private static final int METHOD_clone2 = 2;
    private static final int METHOD_disableAll3 = 3;
    private static final int METHOD_enableAll4 = 4;
    private static final int METHOD_fixAll5 = 5;
    private static final int METHOD_randomize6 = 6;
    private static final int METHOD_unfixAll7 = 7;

    // Method array 
    /*lazy MethodDescriptor*/
    private static MethodDescriptor[] getMdescriptor(){
        MethodDescriptor[] methods = new MethodDescriptor[8];
    
        try {
            methods[METHOD_addNoise0] = new MethodDescriptor ( org.joone.engine.Matrix.class.getMethod("addNoise", new Class[] {Double.TYPE}));
            methods[METHOD_addNoise0].setDisplayName ( "" );
            methods[METHOD_clear1] = new MethodDescriptor ( org.joone.engine.Matrix.class.getMethod("clear", new Class[] {}));
            methods[METHOD_clear1].setDisplayName ( "" );
            methods[METHOD_clone2] = new MethodDescriptor ( org.joone.engine.Matrix.class.getMethod("clone", new Class[] {}));
            methods[METHOD_clone2].setDisplayName ( "" );
            methods[METHOD_disableAll3] = new MethodDescriptor ( org.joone.engine.Matrix.class.getMethod("disableAll", new Class[] {}));
            methods[METHOD_disableAll3].setDisplayName ( "" );
            methods[METHOD_enableAll4] = new MethodDescriptor ( org.joone.engine.Matrix.class.getMethod("enableAll", new Class[] {}));
            methods[METHOD_enableAll4].setDisplayName ( "" );
            methods[METHOD_fixAll5] = new MethodDescriptor ( org.joone.engine.Matrix.class.getMethod("fixAll", new Class[] {}));
            methods[METHOD_fixAll5].setDisplayName ( "" );
            methods[METHOD_randomize6] = new MethodDescriptor ( org.joone.engine.Matrix.class.getMethod("randomize", new Class[] {Double.TYPE, Double.TYPE}));
            methods[METHOD_randomize6].setDisplayName ( "" );
            methods[METHOD_unfixAll7] = new MethodDescriptor ( org.joone.engine.Matrix.class.getMethod("unfixAll", new Class[] {}));
            methods[METHOD_unfixAll7].setDisplayName ( "" );
        }
        catch( Exception e) {}//GEN-HEADEREND:Methods
        
        // Here you can add code for customizing the methods array.
        
        return methods;         }//GEN-LAST:Methods
    
    
    private static final int defaultPropertyIndex = -1;//GEN-BEGIN:Idx
    private static final int defaultEventIndex = -1;//GEN-END:Idx
    
    
 //GEN-FIRST:Superclass
    
    // Here you can add code for customizing the Superclass BeanInfo.
    
 //GEN-LAST:Superclass
    
    /**
     * Gets the bean's <code>BeanDescriptor</code>s.
     *
     * @return BeanDescriptor describing the editable
     * properties of this bean.  May return null if the
     * information should be obtained by automatic analysis.
     */
    public BeanDescriptor getBeanDescriptor() {
        return getBdescriptor();
    }
    
    /**
     * Gets the bean's <code>PropertyDescriptor</code>s.
     *
     * @return An array of PropertyDescriptors describing the editable
     * properties supported by this bean.  May return null if the
     * information should be obtained by automatic analysis.
     * <p>
     * If a property is indexed, then its entry in the result array will
     * belong to the IndexedPropertyDescriptor subclass of PropertyDescriptor.
     * A client of getPropertyDescriptors can use "instanceof" to check
     * if a given PropertyDescriptor is an IndexedPropertyDescriptor.
     */
    public PropertyDescriptor[] getPropertyDescriptors() {
        return getPdescriptor();
    }
    
    /**
     * Gets the bean's <code>EventSetDescriptor</code>s.
     *
     * @return  An array of EventSetDescriptors describing the kinds of
     * events fired by this bean.  May return null if the information
     * should be obtained by automatic analysis.
     */
    public EventSetDescriptor[] getEventSetDescriptors() {
        return getEdescriptor();
    }
    
    /**
     * Gets the bean's <code>MethodDescriptor</code>s.
     *
     * @return  An array of MethodDescriptors describing the methods
     * implemented by this bean.  May return null if the information
     * should be obtained by automatic analysis.
     */
    public MethodDescriptor[] getMethodDescriptors() {
        return getMdescriptor();
    }
    
    /**
     * A bean may have a "default" property that is the property that will
     * mostly commonly be initially chosen for update by human's who are
     * customizing the bean.
     * @return  Index of default property in the PropertyDescriptor array
     * 		returned by getPropertyDescriptors.
     * <P>	Returns -1 if there is no default property.
     */
    public int getDefaultPropertyIndex() {
        return defaultPropertyIndex;
    }
    
    /**
     * A bean may have a "default" event that is the event that will
     * mostly commonly be used by human's when using the bean.
     * @return Index of default event in the EventSetDescriptor array
     *		returned by getEventSetDescriptors.
     * <P>	Returns -1 if there is no default event.
     */
    public int getDefaultEventIndex() {
        return defaultEventIndex;
    }
}

