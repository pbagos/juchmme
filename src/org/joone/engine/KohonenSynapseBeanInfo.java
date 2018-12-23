package org.joone.engine;

import java.beans.*;

public class KohonenSynapseBeanInfo extends SimpleBeanInfo {
    
    // Bean descriptor//GEN-FIRST:BeanDescriptor
    /*lazy BeanDescriptor*/
    private static BeanDescriptor getBdescriptor(){
        BeanDescriptor beanDescriptor = new BeanDescriptor  ( org.joone.engine.KohonenSynapse.class , null ); // NOI18N//GEN-HEADEREND:BeanDescriptor
        
        // Here you can add code for customizing the BeanDescriptor.
        
        return beanDescriptor;     }//GEN-LAST:BeanDescriptor
    
    
    // Property identifiers//GEN-FIRST:Properties
    private static final int PROPERTY_enabled = 0;
    private static final int PROPERTY_loopBack = 1;
    private static final int PROPERTY_monitor = 2;
    private static final int PROPERTY_name = 3;
    private static final int PROPERTY_orderingPhase = 4;
    private static final int PROPERTY_timeConstant = 5;
    private static final int PROPERTY_weights = 6;

    // Property array 
    /*lazy PropertyDescriptor*/
    private static PropertyDescriptor[] getPdescriptor(){
        PropertyDescriptor[] properties = new PropertyDescriptor[7];
    
        try {
            properties[PROPERTY_enabled] = new PropertyDescriptor ( "enabled", org.joone.engine.KohonenSynapse.class, "isEnabled", "setEnabled" ); // NOI18N
            properties[PROPERTY_loopBack] = new PropertyDescriptor ( "loopBack", org.joone.engine.KohonenSynapse.class, "isLoopBack", "setLoopBack" ); // NOI18N
            properties[PROPERTY_monitor] = new PropertyDescriptor ( "monitor", org.joone.engine.KohonenSynapse.class, "getMonitor", "setMonitor" ); // NOI18N
            properties[PROPERTY_monitor].setHidden ( true );
            properties[PROPERTY_name] = new PropertyDescriptor ( "name", org.joone.engine.KohonenSynapse.class, "getName", "setName" ); // NOI18N
            properties[PROPERTY_orderingPhase] = new PropertyDescriptor ( "orderingPhase", org.joone.engine.KohonenSynapse.class, "getOrderingPhase", "setOrderingPhase" ); // NOI18N
            properties[PROPERTY_orderingPhase].setDisplayName ( "ordering phase (epochs)" );
            properties[PROPERTY_timeConstant] = new PropertyDescriptor ( "timeConstant", org.joone.engine.KohonenSynapse.class, "getTimeConstant", "setTimeConstant" ); // NOI18N
            properties[PROPERTY_weights] = new PropertyDescriptor ( "weights", org.joone.engine.KohonenSynapse.class, "getWeights", "setWeights" ); // NOI18N
            properties[PROPERTY_weights].setHidden ( true );
        }
        catch(IntrospectionException e) {
            e.printStackTrace();
        }//GEN-HEADEREND:Properties
        
        // Here you can add code for customizing the properties array.
        
        return properties;     }//GEN-LAST:Properties
    
    // EventSet identifiers//GEN-FIRST:Events

    // EventSet array
    /*lazy EventSetDescriptor*/
    private static EventSetDescriptor[] getEdescriptor(){
        EventSetDescriptor[] eventSets = new EventSetDescriptor[0];//GEN-HEADEREND:Events
        
        // Here you can add code for customizing the event sets array.
        
        return eventSets;     }//GEN-LAST:Events
    
    // Method identifiers//GEN-FIRST:Methods
    private static final int METHOD_addNoise0 = 0;
    private static final int METHOD_canCountSteps1 = 1;
    private static final int METHOD_check2 = 2;
    private static final int METHOD_cicleTerminated3 = 3;
    private static final int METHOD_errorChanged4 = 4;
    private static final int METHOD_fwdGet5 = 5;
    private static final int METHOD_fwdPut6 = 6;
    private static final int METHOD_netStarted7 = 7;
    private static final int METHOD_netStopped8 = 8;
    private static final int METHOD_netStoppedError9 = 9;
    private static final int METHOD_randomize10 = 10;
    private static final int METHOD_reset11 = 11;
    private static final int METHOD_revGet12 = 12;
    private static final int METHOD_revPut13 = 13;

    // Method array 
    /*lazy MethodDescriptor*/
    private static MethodDescriptor[] getMdescriptor(){
        MethodDescriptor[] methods = new MethodDescriptor[14];
    
        try {
            methods[METHOD_addNoise0] = new MethodDescriptor ( org.joone.engine.KohonenSynapse.class.getMethod("addNoise", new Class[] {Double.TYPE})); // NOI18N
            methods[METHOD_addNoise0].setDisplayName ( "" );
            methods[METHOD_canCountSteps1] = new MethodDescriptor ( org.joone.engine.KohonenSynapse.class.getMethod("canCountSteps", new Class[] {})); // NOI18N
            methods[METHOD_canCountSteps1].setDisplayName ( "" );
            methods[METHOD_check2] = new MethodDescriptor ( org.joone.engine.KohonenSynapse.class.getMethod("check", new Class[] {})); // NOI18N
            methods[METHOD_check2].setDisplayName ( "" );
            methods[METHOD_cicleTerminated3] = new MethodDescriptor ( org.joone.engine.KohonenSynapse.class.getMethod("cicleTerminated", new Class[] {org.joone.engine.NeuralNetEvent.class})); // NOI18N
            methods[METHOD_cicleTerminated3].setDisplayName ( "" );
            methods[METHOD_errorChanged4] = new MethodDescriptor ( org.joone.engine.KohonenSynapse.class.getMethod("errorChanged", new Class[] {org.joone.engine.NeuralNetEvent.class})); // NOI18N
            methods[METHOD_errorChanged4].setDisplayName ( "" );
            methods[METHOD_fwdGet5] = new MethodDescriptor ( org.joone.engine.KohonenSynapse.class.getMethod("fwdGet", new Class[] {})); // NOI18N
            methods[METHOD_fwdGet5].setDisplayName ( "" );
            methods[METHOD_fwdPut6] = new MethodDescriptor ( org.joone.engine.KohonenSynapse.class.getMethod("fwdPut", new Class[] {org.joone.engine.Pattern.class})); // NOI18N
            methods[METHOD_fwdPut6].setDisplayName ( "" );
            methods[METHOD_netStarted7] = new MethodDescriptor ( org.joone.engine.KohonenSynapse.class.getMethod("netStarted", new Class[] {org.joone.engine.NeuralNetEvent.class})); // NOI18N
            methods[METHOD_netStarted7].setDisplayName ( "" );
            methods[METHOD_netStopped8] = new MethodDescriptor ( org.joone.engine.KohonenSynapse.class.getMethod("netStopped", new Class[] {org.joone.engine.NeuralNetEvent.class})); // NOI18N
            methods[METHOD_netStopped8].setDisplayName ( "" );
            methods[METHOD_netStoppedError9] = new MethodDescriptor ( org.joone.engine.KohonenSynapse.class.getMethod("netStoppedError", new Class[] {org.joone.engine.NeuralNetEvent.class, java.lang.String.class})); // NOI18N
            methods[METHOD_netStoppedError9].setDisplayName ( "" );
            methods[METHOD_randomize10] = new MethodDescriptor ( org.joone.engine.KohonenSynapse.class.getMethod("randomize", new Class[] {Double.TYPE})); // NOI18N
            methods[METHOD_randomize10].setDisplayName ( "" );
            methods[METHOD_reset11] = new MethodDescriptor ( org.joone.engine.KohonenSynapse.class.getMethod("reset", new Class[] {})); // NOI18N
            methods[METHOD_reset11].setDisplayName ( "" );
            methods[METHOD_revGet12] = new MethodDescriptor ( org.joone.engine.KohonenSynapse.class.getMethod("revGet", new Class[] {})); // NOI18N
            methods[METHOD_revGet12].setDisplayName ( "" );
            methods[METHOD_revPut13] = new MethodDescriptor ( org.joone.engine.KohonenSynapse.class.getMethod("revPut", new Class[] {org.joone.engine.Pattern.class})); // NOI18N
            methods[METHOD_revPut13].setDisplayName ( "" );
        }
        catch( Exception e) {}//GEN-HEADEREND:Methods
        
        // Here you can add code for customizing the methods array.
        
        return methods;     }//GEN-LAST:Methods
    
    
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

