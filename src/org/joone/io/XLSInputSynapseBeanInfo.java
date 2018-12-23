package org.joone.io;

import java.beans.*;

public class XLSInputSynapseBeanInfo extends SimpleBeanInfo {




    // Bean descriptor//GEN-FIRST:BeanDescriptor
    private static BeanDescriptor beanDescriptor = new BeanDescriptor  ( org.joone.io.XLSInputSynapse.class , null ); // NOI18N

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
    private static final int PROPERTY_monitor = 8;
    private static final int PROPERTY_name = 9;
    private static final int PROPERTY_plugIn = 10;
    private static final int PROPERTY_sheetName = 11;
    private static final int PROPERTY_stepCounter = 12;

    // Property array 
    private static PropertyDescriptor[] properties = new PropertyDescriptor[13];

    private static PropertyDescriptor[] getPdescriptor(){
        return properties;
    }

    static {
        try {
            properties[PROPERTY_advancedColumnSelector] = new PropertyDescriptor ( "advancedColumnSelector", org.joone.io.XLSInputSynapse.class, "getAdvancedColumnSelector", "setAdvancedColumnSelector" ); // NOI18N
            properties[PROPERTY_advancedColumnSelector].setDisplayName ( "Advanced Column Selector" );
            properties[PROPERTY_buffered] = new PropertyDescriptor ( "buffered", org.joone.io.XLSInputSynapse.class, "isBuffered", "setBuffered" ); // NOI18N
            properties[PROPERTY_decimalPoint] = new PropertyDescriptor ( "decimalPoint", org.joone.io.XLSInputSynapse.class, "getDecimalPoint", "setDecimalPoint" ); // NOI18N
            properties[PROPERTY_decimalPoint].setExpert ( true );
            properties[PROPERTY_enabled] = new PropertyDescriptor ( "enabled", org.joone.io.XLSInputSynapse.class, "isEnabled", "setEnabled" ); // NOI18N
            properties[PROPERTY_firstRow] = new PropertyDescriptor ( "firstRow", org.joone.io.XLSInputSynapse.class, "getFirstRow", "setFirstRow" ); // NOI18N
            properties[PROPERTY_inputFile] = new PropertyDescriptor ( "inputFile", org.joone.io.XLSInputSynapse.class, "getInputFile", "setInputFile" ); // NOI18N
            properties[PROPERTY_inputPatterns] = new PropertyDescriptor ( "inputPatterns", org.joone.io.XLSInputSynapse.class, "getInputPatterns", "setInputPatterns" ); // NOI18N
            properties[PROPERTY_inputPatterns].setExpert ( true );
            properties[PROPERTY_lastRow] = new PropertyDescriptor ( "lastRow", org.joone.io.XLSInputSynapse.class, "getLastRow", "setLastRow" ); // NOI18N
            properties[PROPERTY_monitor] = new PropertyDescriptor ( "monitor", org.joone.io.XLSInputSynapse.class, "getMonitor", "setMonitor" ); // NOI18N
            properties[PROPERTY_monitor].setExpert ( true );
            properties[PROPERTY_name] = new PropertyDescriptor ( "name", org.joone.io.XLSInputSynapse.class, "getName", "setName" ); // NOI18N
            properties[PROPERTY_plugIn] = new PropertyDescriptor ( "plugIn", org.joone.io.XLSInputSynapse.class, "getPlugIn", "setPlugIn" ); // NOI18N
            properties[PROPERTY_plugIn].setExpert ( true );
            properties[PROPERTY_sheetName] = new PropertyDescriptor ( "sheetName", org.joone.io.XLSInputSynapse.class, "getSheetName", "setSheetName" ); // NOI18N
            properties[PROPERTY_stepCounter] = new PropertyDescriptor ( "stepCounter", org.joone.io.XLSInputSynapse.class, "isStepCounter", "setStepCounter" ); // NOI18N
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
