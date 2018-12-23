package org.joone.engine;

import java.beans.*;

public class SimpleLayerBeanInfo extends SimpleBeanInfo {



    // Bean descriptor//GEN-FIRST:BeanDescriptor
    private static BeanDescriptor beanDescriptor = new BeanDescriptor  ( SimpleLayer.class , null );

    private static BeanDescriptor getBdescriptor(){
        return beanDescriptor;
    }

    static {//GEN-HEADEREND:BeanDescriptor

    // Here you can add code for customizing the BeanDescriptor.

}//GEN-LAST:BeanDescriptor


    // Property identifiers//GEN-FIRST:Properties
    private static final int PROPERTY_allInputs = 0;
    private static final int PROPERTY_allOutputs = 1;
    private static final int PROPERTY_bias = 2;
    private static final int PROPERTY_inputLayer = 3;
    private static final int PROPERTY_layerName = 4;
    private static final int PROPERTY_learner = 5;
    private static final int PROPERTY_monitor = 6;
    private static final int PROPERTY_outputLayer = 7;
    private static final int PROPERTY_rows = 8;

    // Property array 
    private static PropertyDescriptor[] properties = new PropertyDescriptor[9];

    private static PropertyDescriptor[] getPdescriptor(){
        return properties;
    }

    static {
        try {
            properties[PROPERTY_allInputs] = new PropertyDescriptor ( "allInputs", SimpleLayer.class, "getAllInputs", "setAllInputs" );
            properties[PROPERTY_allInputs].setExpert ( true );
            properties[PROPERTY_allOutputs] = new PropertyDescriptor ( "allOutputs", SimpleLayer.class, "getAllOutputs", "setAllOutputs" );
            properties[PROPERTY_allOutputs].setExpert ( true );
            properties[PROPERTY_bias] = new PropertyDescriptor ( "bias", SimpleLayer.class, "getBias", "setBias" );
            properties[PROPERTY_bias].setExpert ( true );
            properties[PROPERTY_inputLayer] = new PropertyDescriptor ( "inputLayer", SimpleLayer.class, "isInputLayer", null );
            properties[PROPERTY_inputLayer].setExpert ( true );
            properties[PROPERTY_layerName] = new PropertyDescriptor ( "layerName", SimpleLayer.class, "getLayerName", "setLayerName" );
            properties[PROPERTY_layerName].setDisplayName ( "Name" );
            properties[PROPERTY_learner] = new PropertyDescriptor ( "learner", SimpleLayer.class, "getLearner", null );
            properties[PROPERTY_learner].setExpert ( true );
            properties[PROPERTY_monitor] = new PropertyDescriptor ( "monitor", SimpleLayer.class, "getMonitor", "setMonitor" );
            properties[PROPERTY_monitor].setExpert ( true );
            properties[PROPERTY_outputLayer] = new PropertyDescriptor ( "outputLayer", SimpleLayer.class, "isOutputLayer", null );
            properties[PROPERTY_outputLayer].setExpert ( true );
            properties[PROPERTY_rows] = new PropertyDescriptor ( "rows", SimpleLayer.class, "getRows", "setRows" );
        }
        catch( IntrospectionException e) {}//GEN-HEADEREND:Properties

    // Here you can add code for customizing the properties array.

}//GEN-LAST:Properties

    // EventSet identifiers//GEN-FIRST:Events

    // EventSet array
    private static EventSetDescriptor[] eventSets = new EventSetDescriptor[0];

    private static EventSetDescriptor[] getEdescriptor(){
        return eventSets;
    }
//GEN-HEADEREND:Events

    // Here you can add code for customizing the event sets array.

//GEN-LAST:Events

    // Method identifiers//GEN-FIRST:Methods

    // Method array 
    private static MethodDescriptor[] methods = new MethodDescriptor[0];

    private static MethodDescriptor[] getMdescriptor(){
        return methods;
    }
//GEN-HEADEREND:Methods

    // Here you can add code for customizing the methods array.
    
//GEN-LAST:Methods


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

