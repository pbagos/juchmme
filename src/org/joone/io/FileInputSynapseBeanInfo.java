package org.joone.io;

import java.beans.*;

public class FileInputSynapseBeanInfo extends SimpleBeanInfo {
    
    
    


    // Bean descriptor//GEN-FIRST:BeanDescriptor
    private static BeanDescriptor beanDescriptor = new BeanDescriptor  ( org.joone.io.FileInputSynapse.class , null ); // NOI18N

    private static BeanDescriptor getBdescriptor(){
        return beanDescriptor;
    }

    static {//GEN-HEADEREND:BeanDescriptor
        
        // Here you can add code for customizing the BeanDescriptor.
        
}//GEN-LAST:BeanDescriptor
    
    // Property identifiers//GEN-FIRST:Properties
    private static final int PROPERTY_advancedColumnSelector = 0;
    private static final int PROPERTY_buffered = 1;
    private static final int PROPERTY_decimalPoint = 2;
    private static final int PROPERTY_enabled = 3;
    private static final int PROPERTY_firstRow = 4;
    private static final int PROPERTY_inputFile = 5;
    private static final int PROPERTY_inputPatterns = 6;
    private static final int PROPERTY_lastRow = 7;
    private static final int PROPERTY_maxBufSize = 8;
    private static final int PROPERTY_monitor = 9;
    private static final int PROPERTY_name = 10;
    private static final int PROPERTY_plugIn = 11;
    private static final int PROPERTY_stepCounter = 12;

    // Property array 
    private static PropertyDescriptor[] properties = new PropertyDescriptor[13];

    private static PropertyDescriptor[] getPdescriptor(){
        return properties;
    }

    static {
        try {
            properties[PROPERTY_advancedColumnSelector] = new PropertyDescriptor ( "advancedColumnSelector", org.joone.io.FileInputSynapse.class, "getAdvancedColumnSelector", "setAdvancedColumnSelector" ); // NOI18N
            properties[PROPERTY_advancedColumnSelector].setDisplayName ( "Advanced Column Selector" );
            properties[PROPERTY_buffered] = new PropertyDescriptor ( "buffered", org.joone.io.FileInputSynapse.class, "isBuffered", "setBuffered" ); // NOI18N
            properties[PROPERTY_decimalPoint] = new PropertyDescriptor ( "decimalPoint", org.joone.io.FileInputSynapse.class, "getDecimalPoint", "setDecimalPoint" ); // NOI18N
            properties[PROPERTY_decimalPoint].setExpert ( true );
            properties[PROPERTY_enabled] = new PropertyDescriptor ( "enabled", org.joone.io.FileInputSynapse.class, "isEnabled", "setEnabled" ); // NOI18N
            properties[PROPERTY_firstRow] = new PropertyDescriptor ( "firstRow", org.joone.io.FileInputSynapse.class, "getFirstRow", "setFirstRow" ); // NOI18N
            properties[PROPERTY_inputFile] = new PropertyDescriptor ( "inputFile", org.joone.io.FileInputSynapse.class, "getInputFile", "setInputFile" ); // NOI18N
            properties[PROPERTY_inputPatterns] = new PropertyDescriptor ( "inputPatterns", org.joone.io.FileInputSynapse.class, "getInputPatterns", "setInputPatterns" ); // NOI18N
            properties[PROPERTY_inputPatterns].setExpert ( true );
            properties[PROPERTY_lastRow] = new PropertyDescriptor ( "lastRow", org.joone.io.FileInputSynapse.class, "getLastRow", "setLastRow" ); // NOI18N
            properties[PROPERTY_maxBufSize] = new PropertyDescriptor ( "maxBufSize", org.joone.io.FileInputSynapse.class, "getMaxBufSize", "setMaxBufSize" ); // NOI18N
            properties[PROPERTY_monitor] = new PropertyDescriptor ( "monitor", org.joone.io.FileInputSynapse.class, "getMonitor", "setMonitor" ); // NOI18N
            properties[PROPERTY_monitor].setExpert ( true );
            properties[PROPERTY_name] = new PropertyDescriptor ( "name", org.joone.io.FileInputSynapse.class, "getName", "setName" ); // NOI18N
            properties[PROPERTY_plugIn] = new PropertyDescriptor ( "plugIn", org.joone.io.FileInputSynapse.class, "getPlugIn", "setPlugIn" ); // NOI18N
            properties[PROPERTY_plugIn].setExpert ( true );
            properties[PROPERTY_stepCounter] = new PropertyDescriptor ( "stepCounter", org.joone.io.FileInputSynapse.class, "isStepCounter", "setStepCounter" ); // NOI18N
        }
        catch(IntrospectionException e) {
            e.printStackTrace();
        }//GEN-HEADEREND:Properties
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
    private static final int METHOD_addNoise0 = 0;
    private static final int METHOD_canCountSteps1 = 1;
    private static final int METHOD_fwdGet2 = 2;
    private static final int METHOD_fwdPut3 = 3;
    private static final int METHOD_gotoFirstLine4 = 4;
    private static final int METHOD_gotoLine5 = 5;
    private static final int METHOD_readAll6 = 6;
    private static final int METHOD_revGet7 = 7;
    private static final int METHOD_revPut8 = 8;

    // Method array 
    private static MethodDescriptor[] methods = new MethodDescriptor[9];

    private static MethodDescriptor[] getMdescriptor(){
        return methods;
    }

    static {
        try {
            methods[METHOD_addNoise0] = new MethodDescriptor ( org.joone.io.FileInputSynapse.class.getMethod("addNoise", new Class[] {Double.TYPE})); // NOI18N
            methods[METHOD_addNoise0].setDisplayName ( "" );
            methods[METHOD_canCountSteps1] = new MethodDescriptor ( org.joone.io.FileInputSynapse.class.getMethod("canCountSteps", new Class[] {})); // NOI18N
            methods[METHOD_canCountSteps1].setDisplayName ( "" );
            methods[METHOD_fwdGet2] = new MethodDescriptor ( org.joone.io.FileInputSynapse.class.getMethod("fwdGet", new Class[] {})); // NOI18N
            methods[METHOD_fwdGet2].setDisplayName ( "" );
            methods[METHOD_fwdPut3] = new MethodDescriptor ( org.joone.io.FileInputSynapse.class.getMethod("fwdPut", new Class[] {org.joone.engine.Pattern.class})); // NOI18N
            methods[METHOD_fwdPut3].setDisplayName ( "" );
            methods[METHOD_gotoFirstLine4] = new MethodDescriptor ( org.joone.io.FileInputSynapse.class.getMethod("gotoFirstLine", new Class[] {})); // NOI18N
            methods[METHOD_gotoFirstLine4].setDisplayName ( "" );
            methods[METHOD_gotoLine5] = new MethodDescriptor ( org.joone.io.FileInputSynapse.class.getMethod("gotoLine", new Class[] {Integer.TYPE})); // NOI18N
            methods[METHOD_gotoLine5].setDisplayName ( "" );
            methods[METHOD_readAll6] = new MethodDescriptor ( org.joone.io.FileInputSynapse.class.getMethod("readAll", new Class[] {})); // NOI18N
            methods[METHOD_readAll6].setDisplayName ( "" );
            methods[METHOD_revGet7] = new MethodDescriptor ( org.joone.io.FileInputSynapse.class.getMethod("revGet", new Class[] {})); // NOI18N
            methods[METHOD_revGet7].setDisplayName ( "" );
            methods[METHOD_revPut8] = new MethodDescriptor ( org.joone.io.FileInputSynapse.class.getMethod("revPut", new Class[] {org.joone.engine.Pattern.class})); // NOI18N
            methods[METHOD_revPut8].setDisplayName ( "" );
        }
        catch( Exception e) {}//GEN-HEADEREND:Methods
        
        // Here you can add code for customizing the methods array.
        
}//GEN-LAST:Methods
    
    private static final int defaultPropertyIndex = -1;//GEN-BEGIN:Idx
    private static final int defaultEventIndex = -1;//GEN-END:Idx
    
    
    /**
     * Gets the bean's <code>BeanDescriptor</code>s.
     *
     * @return BeanDescriptor describing the editable
     * properties of this bean.  May return null if the
     * information should be obtained by automatic analysis.
     */
    public BeanDescriptor getBeanDescriptor() {
        return beanDescriptor;
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
        return properties;
    }
    
    /**
     * Gets the bean's <code>EventSetDescriptor</code>s.
     *
     * @return  An array of EventSetDescriptors describing the kinds of
     * events fired by this bean.  May return null if the information
     * should be obtained by automatic analysis.
     */
    public EventSetDescriptor[] getEventSetDescriptors() {
        return eventSets;
    }
    
    /**
     * Gets the bean's <code>MethodDescriptor</code>s.
     *
     * @return  An array of MethodDescriptors describing the methods
     * implemented by this bean.  May return null if the information
     * should be obtained by automatic analysis.
     */
    public MethodDescriptor[] getMethodDescriptors() {
        return methods;
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

