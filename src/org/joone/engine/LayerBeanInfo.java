package org.joone.engine;

import java.beans.*;

public class LayerBeanInfo extends SimpleBeanInfo {


    // Bean descriptor//GEN-FIRST:BeanDescriptor
    /*lazy BeanDescriptor*/
    private static BeanDescriptor getBdescriptor(){
        BeanDescriptor beanDescriptor = new BeanDescriptor  ( org.joone.engine.Layer.class , null );//GEN-HEADEREND:BeanDescriptor

    // Here you can add code for customizing the BeanDescriptor.

        return beanDescriptor;         }//GEN-LAST:BeanDescriptor


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
    private static final int PROPERTY_running = 9;

    // Property array 
    /*lazy PropertyDescriptor*/
    private static PropertyDescriptor[] getPdescriptor(){
        PropertyDescriptor[] properties = new PropertyDescriptor[10];
    
        try {
            properties[PROPERTY_allInputs] = new PropertyDescriptor ( "allInputs", org.joone.engine.Layer.class, "getAllInputs", "setAllInputs" );
            properties[PROPERTY_allInputs].setExpert ( true );
            properties[PROPERTY_allOutputs] = new PropertyDescriptor ( "allOutputs", org.joone.engine.Layer.class, "getAllOutputs", "setAllOutputs" );
            properties[PROPERTY_allOutputs].setExpert ( true );
            properties[PROPERTY_bias] = new PropertyDescriptor ( "bias", org.joone.engine.Layer.class, "getBias", "setBias" );
            properties[PROPERTY_bias].setExpert ( true );
            properties[PROPERTY_inputLayer] = new PropertyDescriptor ( "inputLayer", org.joone.engine.Layer.class, "isInputLayer", null );
            properties[PROPERTY_inputLayer].setExpert ( true );
            properties[PROPERTY_layerName] = new PropertyDescriptor ( "layerName", org.joone.engine.Layer.class, "getLayerName", "setLayerName" );
            properties[PROPERTY_layerName].setDisplayName ( "Name" );
            properties[PROPERTY_learner] = new PropertyDescriptor ( "learner", org.joone.engine.Layer.class, "getLearner", null );
            properties[PROPERTY_learner].setExpert ( true );
            properties[PROPERTY_monitor] = new PropertyDescriptor ( "monitor", org.joone.engine.Layer.class, "getMonitor", "setMonitor" );
            properties[PROPERTY_monitor].setExpert ( true );
            properties[PROPERTY_outputLayer] = new PropertyDescriptor ( "outputLayer", org.joone.engine.Layer.class, "isOutputLayer", null );
            properties[PROPERTY_outputLayer].setExpert ( true );
            properties[PROPERTY_rows] = new PropertyDescriptor ( "rows", org.joone.engine.Layer.class, "getRows", "setRows" );
            properties[PROPERTY_running] = new PropertyDescriptor ( "running", org.joone.engine.Layer.class, "isRunning", null );
            properties[PROPERTY_running].setExpert ( true );
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

    // Method identifiers//GEN-FIRST:Methods
    private static final int METHOD_addInputSynapse0 = 0;
    private static final int METHOD_addNoise1 = 1;
    private static final int METHOD_addOutputSynapse2 = 2;
    private static final int METHOD_copyInto3 = 3;
    private static final int METHOD_randomize4 = 4;
    private static final int METHOD_removeAllInputs5 = 5;
    private static final int METHOD_removeAllOutputs6 = 6;
    private static final int METHOD_removeInputSynapse7 = 7;
    private static final int METHOD_removeOutputSynapse8 = 8;
    private static final int METHOD_run9 = 9;
    private static final int METHOD_start10 = 10;
    private static final int METHOD_stop11 = 11;

    // Method array 
    /*lazy MethodDescriptor*/
    private static MethodDescriptor[] getMdescriptor(){
        MethodDescriptor[] methods = new MethodDescriptor[12];
    
        try {
            methods[METHOD_addInputSynapse0] = new MethodDescriptor ( org.joone.engine.Layer.class.getMethod("addInputSynapse", new Class[] {org.joone.engine.InputPatternListener.class}));
            methods[METHOD_addInputSynapse0].setDisplayName ( "" );
            methods[METHOD_addNoise1] = new MethodDescriptor ( org.joone.engine.Layer.class.getMethod("addNoise", new Class[] {Double.TYPE}));
            methods[METHOD_addNoise1].setDisplayName ( "" );
            methods[METHOD_addOutputSynapse2] = new MethodDescriptor ( org.joone.engine.Layer.class.getMethod("addOutputSynapse", new Class[] {org.joone.engine.OutputPatternListener.class}));
            methods[METHOD_addOutputSynapse2].setDisplayName ( "" );
            methods[METHOD_copyInto3] = new MethodDescriptor ( org.joone.engine.Layer.class.getMethod("copyInto", new Class[] {org.joone.engine.NeuralLayer.class}));
            methods[METHOD_copyInto3].setDisplayName ( "" );
            methods[METHOD_randomize4] = new MethodDescriptor ( org.joone.engine.Layer.class.getMethod("randomize", new Class[] {Double.TYPE}));
            methods[METHOD_randomize4].setDisplayName ( "" );
            methods[METHOD_removeAllInputs5] = new MethodDescriptor ( org.joone.engine.Layer.class.getMethod("removeAllInputs", new Class[] {}));
            methods[METHOD_removeAllInputs5].setDisplayName ( "" );
            methods[METHOD_removeAllOutputs6] = new MethodDescriptor ( org.joone.engine.Layer.class.getMethod("removeAllOutputs", new Class[] {}));
            methods[METHOD_removeAllOutputs6].setDisplayName ( "" );
            methods[METHOD_removeInputSynapse7] = new MethodDescriptor ( org.joone.engine.Layer.class.getMethod("removeInputSynapse", new Class[] {org.joone.engine.InputPatternListener.class}));
            methods[METHOD_removeInputSynapse7].setDisplayName ( "" );
            methods[METHOD_removeOutputSynapse8] = new MethodDescriptor ( org.joone.engine.Layer.class.getMethod("removeOutputSynapse", new Class[] {org.joone.engine.OutputPatternListener.class}));
            methods[METHOD_removeOutputSynapse8].setDisplayName ( "" );
            methods[METHOD_run9] = new MethodDescriptor ( org.joone.engine.Layer.class.getMethod("run", new Class[] {}));
            methods[METHOD_run9].setDisplayName ( "" );
            methods[METHOD_start10] = new MethodDescriptor ( org.joone.engine.Layer.class.getMethod("start", new Class[] {}));
            methods[METHOD_start10].setDisplayName ( "" );
            methods[METHOD_stop11] = new MethodDescriptor ( org.joone.engine.Layer.class.getMethod("stop", new Class[] {}));
            methods[METHOD_stop11].setDisplayName ( "" );
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

