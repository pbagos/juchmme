package org.joone.engine.learning;

import java.beans.*;

public class TeacherSynapseBeanInfo extends SimpleBeanInfo {


    // Bean descriptor //GEN-FIRST:BeanDescriptor
    /*lazy BeanDescriptor*/;
    private static BeanDescriptor getBdescriptor(){
        BeanDescriptor beanDescriptor = new BeanDescriptor  ( TeacherSynapse.class , null );//GEN-HEADEREND:BeanDescriptor

    // Here you can add code for customizing the BeanDescriptor.

        return beanDescriptor;         }//GEN-LAST:BeanDescriptor


    // Property identifiers //GEN-FIRST:Properties
    private static final int PROPERTY_inputDimension = 0;
    private static final int PROPERTY_name = 1;
    private static final int PROPERTY_desired = 2;
    private static final int PROPERTY_outputDimension = 3;
    private static final int PROPERTY_momentum = 4;
    private static final int PROPERTY_learningRate = 5;
    private static final int PROPERTY_ignoreBefore = 6;
    private static final int PROPERTY_enabled = 7;
    private static final int PROPERTY_monitor = 8;

    // Property array 
    /*lazy PropertyDescriptor*/;
    private static PropertyDescriptor[] getPdescriptor(){
        PropertyDescriptor[] properties = new PropertyDescriptor[9];
    
        try {
            properties[PROPERTY_inputDimension] = new PropertyDescriptor ( "inputDimension", TeacherSynapse.class, "getInputDimension", "setInputDimension" );
            properties[PROPERTY_inputDimension].setExpert ( true );
            properties[PROPERTY_name] = new PropertyDescriptor ( "name", TeacherSynapse.class, "getName", "setName" );
            properties[PROPERTY_desired] = new PropertyDescriptor ( "desired", TeacherSynapse.class, "getDesired", "setDesired" );
            properties[PROPERTY_desired].setExpert ( true );
            properties[PROPERTY_outputDimension] = new PropertyDescriptor ( "outputDimension", TeacherSynapse.class, "getOutputDimension", "setOutputDimension" );
            properties[PROPERTY_outputDimension].setExpert ( true );
            properties[PROPERTY_momentum] = new PropertyDescriptor ( "momentum", TeacherSynapse.class, "getMomentum", "setMomentum" );
            properties[PROPERTY_learningRate] = new PropertyDescriptor ( "learningRate", TeacherSynapse.class, "getLearningRate", "setLearningRate" );
            properties[PROPERTY_ignoreBefore] = new PropertyDescriptor ( "ignoreBefore", TeacherSynapse.class, "getIgnoreBefore", "setIgnoreBefore" );
            properties[PROPERTY_enabled] = new PropertyDescriptor ( "enabled", TeacherSynapse.class, "isEnabled", "setEnabled" );
            properties[PROPERTY_monitor] = new PropertyDescriptor ( "monitor", TeacherSynapse.class, "getMonitor", "setMonitor" );
            properties[PROPERTY_monitor].setExpert ( true );
        }
        catch( IntrospectionException e) {}//GEN-HEADEREND:Properties

    // Here you can add code for customizing the properties array.

        return properties;         }//GEN-LAST:Properties

    // EventSet identifiers//GEN-FIRST:Events

    // EventSet array
    /*lazy EventSetDescriptor*/;
    private static EventSetDescriptor[] getEdescriptor(){
        EventSetDescriptor[] eventSets = new EventSetDescriptor[0];//GEN-HEADEREND:Events

    // Here you can add code for customizing the event sets array.

        return eventSets;         }//GEN-LAST:Events

    // Method identifiers //GEN-FIRST:Methods
    private static final int METHOD_fwdGet0 = 0;
    private static final int METHOD_fwdPut1 = 1;
    private static final int METHOD_revGet2 = 2;
    private static final int METHOD_revPut3 = 3;
    private static final int METHOD_addNoise4 = 4;
    private static final int METHOD_randomize5 = 5;
    private static final int METHOD_canCountSteps6 = 6;

    // Method array 
    /*lazy MethodDescriptor*/;
    private static MethodDescriptor[] getMdescriptor(){
        MethodDescriptor[] methods = new MethodDescriptor[7];
    
        try {
            methods[METHOD_fwdGet0] = new MethodDescriptor ( org.joone.engine.learning.TeacherSynapse.class.getMethod("fwdGet", new Class[] {}));
            methods[METHOD_fwdGet0].setDisplayName ( "" );
            methods[METHOD_fwdPut1] = new MethodDescriptor ( org.joone.engine.learning.TeacherSynapse.class.getMethod("fwdPut", new Class[] {org.joone.engine.Pattern.class}));
            methods[METHOD_fwdPut1].setDisplayName ( "" );
            methods[METHOD_revGet2] = new MethodDescriptor ( org.joone.engine.learning.TeacherSynapse.class.getMethod("revGet", new Class[] {}));
            methods[METHOD_revGet2].setDisplayName ( "" );
            methods[METHOD_revPut3] = new MethodDescriptor ( org.joone.engine.learning.TeacherSynapse.class.getMethod("revPut", new Class[] {org.joone.engine.Pattern.class}));
            methods[METHOD_revPut3].setDisplayName ( "" );
            methods[METHOD_addNoise4] = new MethodDescriptor ( org.joone.engine.learning.TeacherSynapse.class.getMethod("addNoise", new Class[] {Double.TYPE}));
            methods[METHOD_addNoise4].setDisplayName ( "" );
            methods[METHOD_randomize5] = new MethodDescriptor ( org.joone.engine.learning.TeacherSynapse.class.getMethod("randomize", new Class[] {Double.TYPE}));
            methods[METHOD_randomize5].setDisplayName ( "" );
            methods[METHOD_canCountSteps6] = new MethodDescriptor ( org.joone.engine.learning.TeacherSynapse.class.getMethod("canCountSteps", new Class[] {}));
            methods[METHOD_canCountSteps6].setDisplayName ( "" );
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

