/*
 * InputConnectorBeanInfo.java
 *
 * Created on September 21, 2004, 3:09 PM
 */

package org.joone.io;

import java.beans.*;

/**
 * @author drmarpao
 */
public class InputConnectorBeanInfo extends SimpleBeanInfo {
    
    // Bean descriptor //GEN-FIRST:BeanDescriptor
    /*lazy BeanDescriptor*/
    private static BeanDescriptor getBdescriptor(){
        BeanDescriptor beanDescriptor = new BeanDescriptor  ( InputConnector.class , null );//GEN-HEADEREND:BeanDescriptor
        
        // Here you can add code for customizing the BeanDescriptor.
        
        return beanDescriptor;         }//GEN-LAST:BeanDescriptor
    
    
    // Property identifiers //GEN-FIRST:Properties
    private static final int PROPERTY_advancedColumnSelector = 0;
    private static final int PROPERTY_buffered = 1;
    private static final int PROPERTY_enabled = 2;
    private static final int PROPERTY_firstRow = 3;
    private static final int PROPERTY_lastRow = 4;
    private static final int PROPERTY_name = 5;
    private static final int PROPERTY_stepCounter = 6;

    // Property array 
    /*lazy PropertyDescriptor*/
    private static PropertyDescriptor[] getPdescriptor(){
        PropertyDescriptor[] properties = new PropertyDescriptor[7];
    
        try {
            properties[PROPERTY_advancedColumnSelector] = new PropertyDescriptor ( "advancedColumnSelector", InputConnector.class, "getAdvancedColumnSelector", "setAdvancedColumnSelector" );
            properties[PROPERTY_buffered] = new PropertyDescriptor ( "buffered", InputConnector.class, "isBuffered", "setBuffered" );
            properties[PROPERTY_enabled] = new PropertyDescriptor ( "enabled", InputConnector.class, "isEnabled", "setEnabled" );
            properties[PROPERTY_firstRow] = new PropertyDescriptor ( "firstRow", InputConnector.class, "getFirstRow", "setFirstRow" );
            properties[PROPERTY_lastRow] = new PropertyDescriptor ( "lastRow", InputConnector.class, "getLastRow", "setLastRow" );
            properties[PROPERTY_name] = new PropertyDescriptor ( "name", InputConnector.class, "getName", "setName" );
            properties[PROPERTY_stepCounter] = new PropertyDescriptor ( "stepCounter", InputConnector.class, "isStepCounter", "setStepCounter" );
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
    private static final int METHOD_canCountSteps1 = 1;
    private static final int METHOD_check2 = 2;
    private static final int METHOD_fwdGet3 = 3;
    private static final int METHOD_fwdPut4 = 4;
    private static final int METHOD_gotoFirstLine5 = 5;
    private static final int METHOD_gotoLine6 = 6;
    private static final int METHOD_initLearner7 = 7;
    private static final int METHOD_InspectableTitle8 = 8;
    private static final int METHOD_Inspections9 = 9;
    private static final int METHOD_numColumns10 = 10;
    private static final int METHOD_randomize11 = 11;
    private static final int METHOD_readAll12 = 12;
    private static final int METHOD_reset13 = 13;
    private static final int METHOD_resetInput14 = 14;
    private static final int METHOD_revGet15 = 15;
    private static final int METHOD_revPut16 = 16;
    private static final int METHOD_setPlugin17 = 17;

    // Method array 
    /*lazy MethodDescriptor*/
    private static MethodDescriptor[] getMdescriptor(){
        MethodDescriptor[] methods = new MethodDescriptor[18];
    
        try {
            methods[METHOD_addNoise0] = new MethodDescriptor ( org.joone.io.InputConnector.class.getMethod("addNoise", new Class[] {Double.TYPE}));
            methods[METHOD_addNoise0].setDisplayName ( "" );
            methods[METHOD_canCountSteps1] = new MethodDescriptor ( org.joone.io.InputConnector.class.getMethod("canCountSteps", new Class[] {}));
            methods[METHOD_canCountSteps1].setDisplayName ( "" );
            methods[METHOD_check2] = new MethodDescriptor ( org.joone.io.InputConnector.class.getMethod("check", new Class[] {}));
            methods[METHOD_check2].setDisplayName ( "" );
            methods[METHOD_fwdGet3] = new MethodDescriptor ( org.joone.io.InputConnector.class.getMethod("fwdGet", new Class[] {}));
            methods[METHOD_fwdGet3].setDisplayName ( "" );
            methods[METHOD_fwdPut4] = new MethodDescriptor ( org.joone.io.InputConnector.class.getMethod("fwdPut", new Class[] {org.joone.engine.Pattern.class}));
            methods[METHOD_fwdPut4].setDisplayName ( "" );
            methods[METHOD_gotoFirstLine5] = new MethodDescriptor ( org.joone.io.InputConnector.class.getMethod("gotoFirstLine", new Class[] {}));
            methods[METHOD_gotoFirstLine5].setDisplayName ( "" );
            methods[METHOD_gotoLine6] = new MethodDescriptor ( org.joone.io.InputConnector.class.getMethod("gotoLine", new Class[] {Integer.TYPE}));
            methods[METHOD_gotoLine6].setDisplayName ( "" );
            methods[METHOD_initLearner7] = new MethodDescriptor ( org.joone.io.InputConnector.class.getMethod("initLearner", new Class[] {}));
            methods[METHOD_initLearner7].setDisplayName ( "" );
            methods[METHOD_InspectableTitle8] = new MethodDescriptor ( org.joone.io.InputConnector.class.getMethod("InspectableTitle", new Class[] {}));
            methods[METHOD_InspectableTitle8].setDisplayName ( "" );
            methods[METHOD_Inspections9] = new MethodDescriptor ( org.joone.io.InputConnector.class.getMethod("Inspections", new Class[] {}));
            methods[METHOD_Inspections9].setDisplayName ( "" );
            methods[METHOD_numColumns10] = new MethodDescriptor ( org.joone.io.InputConnector.class.getMethod("numColumns", new Class[] {}));
            methods[METHOD_numColumns10].setDisplayName ( "" );
            methods[METHOD_randomize11] = new MethodDescriptor ( org.joone.io.InputConnector.class.getMethod("randomize", new Class[] {Double.TYPE}));
            methods[METHOD_randomize11].setDisplayName ( "" );
            methods[METHOD_readAll12] = new MethodDescriptor ( org.joone.io.InputConnector.class.getMethod("readAll", new Class[] {}));
            methods[METHOD_readAll12].setDisplayName ( "" );
            methods[METHOD_reset13] = new MethodDescriptor ( org.joone.io.InputConnector.class.getMethod("reset", new Class[] {}));
            methods[METHOD_reset13].setDisplayName ( "" );
            methods[METHOD_resetInput14] = new MethodDescriptor ( org.joone.io.InputConnector.class.getMethod("resetInput", new Class[] {}));
            methods[METHOD_resetInput14].setDisplayName ( "" );
            methods[METHOD_revGet15] = new MethodDescriptor ( org.joone.io.InputConnector.class.getMethod("revGet", new Class[] {}));
            methods[METHOD_revGet15].setDisplayName ( "" );
            methods[METHOD_revPut16] = new MethodDescriptor ( org.joone.io.InputConnector.class.getMethod("revPut", new Class[] {org.joone.engine.Pattern.class}));
            methods[METHOD_revPut16].setDisplayName ( "" );
            methods[METHOD_setPlugin17] = new MethodDescriptor ( org.joone.io.InputConnector.class.getMethod("setPlugin", new Class[] {org.joone.util.ConverterPlugIn.class}));
            methods[METHOD_setPlugin17].setDisplayName ( "" );
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

