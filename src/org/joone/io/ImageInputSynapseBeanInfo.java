package org.joone.io;

import java.beans.*;

public class ImageInputSynapseBeanInfo extends SimpleBeanInfo {
    
    // Bean descriptor //GEN-FIRST:BeanDescriptor
    /*lazy BeanDescriptor*/
    private static BeanDescriptor getBdescriptor(){
        BeanDescriptor beanDescriptor = new BeanDescriptor  ( ImageInputSynapse.class , null );//GEN-HEADEREND:BeanDescriptor
        
        // Here you can add code for customizing the BeanDescriptor.
        
        return beanDescriptor;         }//GEN-LAST:BeanDescriptor
    
    
    // Property identifiers //GEN-FIRST:Properties
    private static final int PROPERTY_buffered = 0;
    private static final int PROPERTY_enabled = 1;
    private static final int PROPERTY_firstRow = 2;
    private static final int PROPERTY_lastRow = 3;
    private static final int PROPERTY_maxBufSize = 4;
    private static final int PROPERTY_name = 5;
    private static final int PROPERTY_plugIn = 6;
    private static final int PROPERTY_stepCounter = 7;
    private static final int PROPERTY_fileFilter = 8;
    private static final int PROPERTY_desiredWidth = 9;
    private static final int PROPERTY_desiredHeight = 10;
    private static final int PROPERTY_imageInput = 11;
    private static final int PROPERTY_imageDirectory = 12;
    private static final int PROPERTY_colourMode = 13;
	

    // Property array 
    /*lazy PropertyDescriptor*/
    private static PropertyDescriptor[] getPdescriptor(){
        PropertyDescriptor[] properties = new PropertyDescriptor[14];
    
        try {
            properties[PROPERTY_buffered] = new PropertyDescriptor ( "buffered", ImageInputSynapse.class, "isBuffered", "setBuffered" );
		properties[PROPERTY_enabled] = new PropertyDescriptor ( "enabled", ImageInputSynapse.class, "isEnabled", "setEnabled" );
            properties[PROPERTY_firstRow] = new PropertyDescriptor ( "firstRow", ImageInputSynapse.class, "getFirstRow", "setFirstRow" );
            properties[PROPERTY_lastRow] = new PropertyDescriptor ( "lastRow", ImageInputSynapse.class, "getLastRow", "setLastRow" );
            properties[PROPERTY_maxBufSize] = new PropertyDescriptor ( "maxBufSize", ImageInputSynapse.class, "getMaxBufSize", "setMaxBufSize" );
            properties[PROPERTY_name] = new PropertyDescriptor ( "name", ImageInputSynapse.class, "getName", "setName" );
            properties[PROPERTY_plugIn] = new PropertyDescriptor ( "plugIn", ImageInputSynapse.class, "getPlugIn", "setPlugIn" );
            properties[PROPERTY_plugIn].setExpert ( true );
            properties[PROPERTY_stepCounter] = new PropertyDescriptor ( "stepCounter", ImageInputSynapse.class, "isStepCounter", "setStepCounter" );
		properties[PROPERTY_fileFilter] = new PropertyDescriptor ( "fileFilter", ImageInputSynapse.class, "getFileFilter", "setFileFilter" );
            properties[PROPERTY_desiredWidth] = new PropertyDescriptor ( "scaleToWidth", ImageInputSynapse.class, "getDesiredWidth", "setDesiredWidth" );
		properties[PROPERTY_desiredHeight] = new PropertyDescriptor ( "scaleToHeight", ImageInputSynapse.class, "getDesiredHeight", "setDesiredHeight" );
		properties[PROPERTY_imageInput] = new PropertyDescriptor ( "imageInput", ImageInputSynapse.class, null, "setImageInput" );
		properties[PROPERTY_imageInput].setExpert ( true );
		properties[PROPERTY_imageDirectory] = new PropertyDescriptor ( "imageDirectory", ImageInputSynapse.class, "getImageDirectory", "setImageDirectory" );
		properties[PROPERTY_colourMode] = new PropertyDescriptor ( "colourMode", ImageInputSynapse.class, "getColourMode", "setColourMode" );
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
    private static final int METHOD_numColumns8 = 8;
    private static final int METHOD_randomize9 = 9;
    private static final int METHOD_readAll10 = 10;
    private static final int METHOD_reset11 = 11;
    private static final int METHOD_resetInput12 = 12;
    private static final int METHOD_revGet13 = 13;
    private static final int METHOD_revPut14 = 14;

    // Method array 
    /*lazy MethodDescriptor*/
    private static MethodDescriptor[] getMdescriptor(){
        MethodDescriptor[] methods = new MethodDescriptor[15];
    
        try {
            methods[METHOD_addNoise0] = new MethodDescriptor ( org.joone.io.ImageInputSynapse.class.getMethod("addNoise", new Class[] {Double.TYPE}));
            methods[METHOD_addNoise0].setDisplayName ( "" );
            methods[METHOD_canCountSteps1] = new MethodDescriptor ( org.joone.io.ImageInputSynapse.class.getMethod("canCountSteps", new Class[] {}));
            methods[METHOD_canCountSteps1].setDisplayName ( "" );
            methods[METHOD_check2] = new MethodDescriptor ( org.joone.io.ImageInputSynapse.class.getMethod("check", new Class[] {}));
            methods[METHOD_check2].setDisplayName ( "" );
            methods[METHOD_fwdGet3] = new MethodDescriptor ( org.joone.io.ImageInputSynapse.class.getMethod("fwdGet", new Class[] {}));
            methods[METHOD_fwdGet3].setDisplayName ( "" );
            methods[METHOD_fwdPut4] = new MethodDescriptor ( org.joone.io.ImageInputSynapse.class.getMethod("fwdPut", new Class[] {org.joone.engine.Pattern.class}));
            methods[METHOD_fwdPut4].setDisplayName ( "" );
            methods[METHOD_gotoFirstLine5] = new MethodDescriptor ( org.joone.io.ImageInputSynapse.class.getMethod("gotoFirstLine", new Class[] {}));
            methods[METHOD_gotoFirstLine5].setDisplayName ( "" );
            methods[METHOD_gotoLine6] = new MethodDescriptor ( org.joone.io.ImageInputSynapse.class.getMethod("gotoLine", new Class[] {Integer.TYPE}));
            methods[METHOD_gotoLine6].setDisplayName ( "" );
            methods[METHOD_initLearner7] = new MethodDescriptor ( org.joone.io.ImageInputSynapse.class.getMethod("initLearner", new Class[] {}));
            methods[METHOD_initLearner7].setDisplayName ( "" );
            methods[METHOD_numColumns8] = new MethodDescriptor ( org.joone.io.ImageInputSynapse.class.getMethod("numColumns", new Class[] {}));
            methods[METHOD_numColumns8].setDisplayName ( "" );
            methods[METHOD_randomize9] = new MethodDescriptor ( org.joone.io.ImageInputSynapse.class.getMethod("randomize", new Class[] {Double.TYPE}));
            methods[METHOD_randomize9].setDisplayName ( "" );
            methods[METHOD_readAll10] = new MethodDescriptor ( org.joone.io.ImageInputSynapse.class.getMethod("readAll", new Class[] {}));
            methods[METHOD_readAll10].setDisplayName ( "" );
            methods[METHOD_reset11] = new MethodDescriptor ( org.joone.io.ImageInputSynapse.class.getMethod("reset", new Class[] {}));
            methods[METHOD_reset11].setDisplayName ( "" );
            methods[METHOD_resetInput12] = new MethodDescriptor ( org.joone.io.ImageInputSynapse.class.getMethod("resetInput", new Class[] {}));
            methods[METHOD_resetInput12].setDisplayName ( "" );
            methods[METHOD_revGet13] = new MethodDescriptor ( org.joone.io.ImageInputSynapse.class.getMethod("revGet", new Class[] {}));
            methods[METHOD_revGet13].setDisplayName ( "" );
            methods[METHOD_revPut14] = new MethodDescriptor ( org.joone.io.ImageInputSynapse.class.getMethod("revPut", new Class[] {org.joone.engine.Pattern.class}));
            methods[METHOD_revPut14].setDisplayName ( "" );
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

