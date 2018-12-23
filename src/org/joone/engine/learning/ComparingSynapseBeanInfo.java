package org.joone.engine.learning;

import java.beans.*;

public class ComparingSynapseBeanInfo extends SimpleBeanInfo {
    
    // Bean descriptor //GEN-FIRST:BeanDescriptor
    /*lazy BeanDescriptor*/
    private static BeanDescriptor getBdescriptor(){
        BeanDescriptor beanDescriptor = new BeanDescriptor  ( ComparingSynapse.class , null );//GEN-HEADEREND:BeanDescriptor
        
        // Here you can add code for customizing the BeanDescriptor.
        
        return beanDescriptor;         }//GEN-LAST:BeanDescriptor
    
    
    // Property identifiers //GEN-FIRST:Properties
    private static final int PROPERTY_enabled = 0;
    private static final int PROPERTY_name = 1;

    // Property array 
    /*lazy PropertyDescriptor*/
    private static PropertyDescriptor[] getPdescriptor(){
        PropertyDescriptor[] properties = new PropertyDescriptor[2];
    
        try {
            properties[PROPERTY_enabled] = new PropertyDescriptor ( "enabled", ComparingSynapse.class, "isEnabled", "setEnabled" );
            properties[PROPERTY_name] = new PropertyDescriptor ( "name", ComparingSynapse.class, "getName", "setName" );
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
    private static final int METHOD_fwdPut0 = 0;
    private static final int METHOD_revGet1 = 1;
    private static final int METHOD_addResultSynapse2 = 2;
    private static final int METHOD_removeResultSynapse3 = 3;
    private static final int METHOD_start4 = 4;
    private static final int METHOD_stop5 = 5;
    private static final int METHOD_resetInput6 = 6;
    private static final int METHOD_check7 = 7;

    // Method array 
    /*lazy MethodDescriptor*/
    private static MethodDescriptor[] getMdescriptor(){
        MethodDescriptor[] methods = new MethodDescriptor[8];
    
        try {
            methods[METHOD_fwdPut0] = new MethodDescriptor ( org.joone.engine.learning.ComparingSynapse.class.getMethod("fwdPut", new Class[] {org.joone.engine.Pattern.class}));
            methods[METHOD_fwdPut0].setDisplayName ( "" );
            methods[METHOD_revGet1] = new MethodDescriptor ( org.joone.engine.learning.ComparingSynapse.class.getMethod("revGet", new Class[] {}));
            methods[METHOD_revGet1].setDisplayName ( "" );
            methods[METHOD_addResultSynapse2] = new MethodDescriptor ( org.joone.engine.learning.ComparingSynapse.class.getMethod("addResultSynapse", new Class[] {org.joone.engine.OutputPatternListener.class}));
            methods[METHOD_addResultSynapse2].setDisplayName ( "" );
            methods[METHOD_removeResultSynapse3] = new MethodDescriptor ( org.joone.engine.learning.ComparingSynapse.class.getMethod("removeResultSynapse", new Class[] {org.joone.engine.OutputPatternListener.class}));
            methods[METHOD_removeResultSynapse3].setDisplayName ( "" );
            methods[METHOD_start4] = new MethodDescriptor ( org.joone.engine.learning.ComparingSynapse.class.getMethod("start", new Class[] {}));
            methods[METHOD_start4].setDisplayName ( "" );
            methods[METHOD_stop5] = new MethodDescriptor ( org.joone.engine.learning.ComparingSynapse.class.getMethod("stop", new Class[] {}));
            methods[METHOD_stop5].setDisplayName ( "" );
            methods[METHOD_resetInput6] = new MethodDescriptor ( org.joone.engine.learning.ComparingSynapse.class.getMethod("resetInput", new Class[] {}));
            methods[METHOD_resetInput6].setDisplayName ( "" );
            methods[METHOD_check7] = new MethodDescriptor ( org.joone.engine.learning.ComparingSynapse.class.getMethod("check", new Class[] {}));
            methods[METHOD_check7].setDisplayName ( "" );
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

