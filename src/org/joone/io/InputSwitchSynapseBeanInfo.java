package org.joone.io;

import java.beans.*;

public class InputSwitchSynapseBeanInfo extends SimpleBeanInfo {
    
    // Bean descriptor//GEN-FIRST:BeanDescriptor
    /*lazy BeanDescriptor*/
    private static BeanDescriptor getBdescriptor(){
        BeanDescriptor beanDescriptor = new BeanDescriptor  ( org.joone.io.InputSwitchSynapse.class , null ); // NOI18N//GEN-HEADEREND:BeanDescriptor
        
        // Here you can add code for customizing the BeanDescriptor.
        
        return beanDescriptor;     }//GEN-LAST:BeanDescriptor
    
    
    // Property identifiers//GEN-FIRST:Properties
    private static final int PROPERTY_activeInput = 0;
    private static final int PROPERTY_advancedColumnSelector = 1;
    private static final int PROPERTY_allInputs = 2;
    private static final int PROPERTY_defaultInput = 3;
    private static final int PROPERTY_monitor = 4;
    private static final int PROPERTY_name = 5;

    // Property array 
    /*lazy PropertyDescriptor*/
    private static PropertyDescriptor[] getPdescriptor(){
        PropertyDescriptor[] properties = new PropertyDescriptor[6];
    
        try {
            properties[PROPERTY_activeInput] = new PropertyDescriptor ( "activeInput", org.joone.io.InputSwitchSynapse.class, "getActiveInput", "setActiveInput" ); // NOI18N
            properties[PROPERTY_advancedColumnSelector] = new PropertyDescriptor ( "advancedColumnSelector", org.joone.io.InputSwitchSynapse.class, "getAdvancedColumnSelector", "setAdvancedColumnSelector" ); // NOI18N
            properties[PROPERTY_advancedColumnSelector].setHidden ( true );
            properties[PROPERTY_allInputs] = new PropertyDescriptor ( "allInputs", org.joone.io.InputSwitchSynapse.class, "getAllInputs", "setAllInputs" ); // NOI18N
            properties[PROPERTY_allInputs].setExpert ( true );
            properties[PROPERTY_defaultInput] = new PropertyDescriptor ( "defaultInput", org.joone.io.InputSwitchSynapse.class, "getDefaultInput", "setDefaultInput" ); // NOI18N
            properties[PROPERTY_monitor] = new PropertyDescriptor ( "monitor", org.joone.io.InputSwitchSynapse.class, "getMonitor", "setMonitor" ); // NOI18N
            properties[PROPERTY_monitor].setExpert ( true );
            properties[PROPERTY_name] = new PropertyDescriptor ( "name", org.joone.io.InputSwitchSynapse.class, "getName", "setName" ); // NOI18N
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
    private static final int METHOD_addInputSynapse0 = 0;
    private static final int METHOD_addNoise1 = 1;
    private static final int METHOD_canCountSteps2 = 2;
    private static final int METHOD_check3 = 3;
    private static final int METHOD_fwdGet4 = 4;
    private static final int METHOD_fwdPut5 = 5;
    private static final int METHOD_gotoFirstLine6 = 6;
    private static final int METHOD_gotoLine7 = 7;
    private static final int METHOD_randomize8 = 8;
    private static final int METHOD_readAll9 = 9;
    private static final int METHOD_removeAllInputs10 = 10;
    private static final int METHOD_removeInputSynapse11 = 11;
    private static final int METHOD_reset12 = 12;
    private static final int METHOD_resetInput13 = 13;
    private static final int METHOD_revGet14 = 14;
    private static final int METHOD_revPut15 = 15;

    // Method array 
    /*lazy MethodDescriptor*/
    private static MethodDescriptor[] getMdescriptor(){
        MethodDescriptor[] methods = new MethodDescriptor[16];
    
        try {
            methods[METHOD_addInputSynapse0] = new MethodDescriptor ( org.joone.io.InputSwitchSynapse.class.getMethod("addInputSynapse", new Class[] {org.joone.io.StreamInputSynapse.class})); // NOI18N
            methods[METHOD_addInputSynapse0].setDisplayName ( "" );
            methods[METHOD_addNoise1] = new MethodDescriptor ( org.joone.io.InputSwitchSynapse.class.getMethod("addNoise", new Class[] {Double.TYPE})); // NOI18N
            methods[METHOD_addNoise1].setDisplayName ( "" );
            methods[METHOD_canCountSteps2] = new MethodDescriptor ( org.joone.io.InputSwitchSynapse.class.getMethod("canCountSteps", new Class[] {})); // NOI18N
            methods[METHOD_canCountSteps2].setDisplayName ( "" );
            methods[METHOD_check3] = new MethodDescriptor ( org.joone.io.InputSwitchSynapse.class.getMethod("check", new Class[] {})); // NOI18N
            methods[METHOD_check3].setDisplayName ( "" );
            methods[METHOD_fwdGet4] = new MethodDescriptor ( org.joone.io.InputSwitchSynapse.class.getMethod("fwdGet", new Class[] {})); // NOI18N
            methods[METHOD_fwdGet4].setDisplayName ( "" );
            methods[METHOD_fwdPut5] = new MethodDescriptor ( org.joone.io.InputSwitchSynapse.class.getMethod("fwdPut", new Class[] {org.joone.engine.Pattern.class})); // NOI18N
            methods[METHOD_fwdPut5].setDisplayName ( "" );
            methods[METHOD_gotoFirstLine6] = new MethodDescriptor ( org.joone.io.InputSwitchSynapse.class.getMethod("gotoFirstLine", new Class[] {})); // NOI18N
            methods[METHOD_gotoFirstLine6].setDisplayName ( "" );
            methods[METHOD_gotoLine7] = new MethodDescriptor ( org.joone.io.InputSwitchSynapse.class.getMethod("gotoLine", new Class[] {Integer.TYPE})); // NOI18N
            methods[METHOD_gotoLine7].setDisplayName ( "" );
            methods[METHOD_randomize8] = new MethodDescriptor ( org.joone.io.InputSwitchSynapse.class.getMethod("randomize", new Class[] {Double.TYPE})); // NOI18N
            methods[METHOD_randomize8].setDisplayName ( "" );
            methods[METHOD_readAll9] = new MethodDescriptor ( org.joone.io.InputSwitchSynapse.class.getMethod("readAll", new Class[] {})); // NOI18N
            methods[METHOD_readAll9].setDisplayName ( "" );
            methods[METHOD_removeAllInputs10] = new MethodDescriptor ( org.joone.io.InputSwitchSynapse.class.getMethod("removeAllInputs", new Class[] {})); // NOI18N
            methods[METHOD_removeAllInputs10].setDisplayName ( "" );
            methods[METHOD_removeInputSynapse11] = new MethodDescriptor ( org.joone.io.InputSwitchSynapse.class.getMethod("removeInputSynapse", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_removeInputSynapse11].setDisplayName ( "" );
            methods[METHOD_reset12] = new MethodDescriptor ( org.joone.io.InputSwitchSynapse.class.getMethod("reset", new Class[] {})); // NOI18N
            methods[METHOD_reset12].setDisplayName ( "" );
            methods[METHOD_resetInput13] = new MethodDescriptor ( org.joone.io.InputSwitchSynapse.class.getMethod("resetInput", new Class[] {})); // NOI18N
            methods[METHOD_resetInput13].setDisplayName ( "" );
            methods[METHOD_revGet14] = new MethodDescriptor ( org.joone.io.InputSwitchSynapse.class.getMethod("revGet", new Class[] {})); // NOI18N
            methods[METHOD_revGet14].setDisplayName ( "" );
            methods[METHOD_revPut15] = new MethodDescriptor ( org.joone.io.InputSwitchSynapse.class.getMethod("revPut", new Class[] {org.joone.engine.Pattern.class})); // NOI18N
            methods[METHOD_revPut15].setDisplayName ( "" );
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

