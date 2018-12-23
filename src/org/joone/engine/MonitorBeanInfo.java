package org.joone.engine;

import java.beans.*;

public class MonitorBeanInfo extends SimpleBeanInfo {
    
    // Bean descriptor//GEN-FIRST:BeanDescriptor
    /*lazy BeanDescriptor*/
    private static BeanDescriptor getBdescriptor(){
        BeanDescriptor beanDescriptor = new BeanDescriptor  ( org.joone.engine.Monitor.class , null ); // NOI18N//GEN-HEADEREND:BeanDescriptor
        
        // Here you can add code for customizing the BeanDescriptor.
        
        return beanDescriptor;     }//GEN-LAST:BeanDescriptor
    
    
    // Property identifiers//GEN-FIRST:Properties
    private static final int PROPERTY_batchSize = 0;
    private static final int PROPERTY_currentCicle = 1;
    private static final int PROPERTY_globalError = 2;
    private static final int PROPERTY_learning = 3;
    private static final int PROPERTY_learningMode = 4;
    private static final int PROPERTY_learningRate = 5;
    private static final int PROPERTY_momentum = 6;
    private static final int PROPERTY_preLearning = 7;
    private static final int PROPERTY_singleThreadMode = 8;
    private static final int PROPERTY_supervised = 9;
    private static final int PROPERTY_totCicles = 10;
    private static final int PROPERTY_trainingPatterns = 11;
    private static final int PROPERTY_useRMSE = 12;
    private static final int PROPERTY_validation = 13;
    private static final int PROPERTY_validationPatterns = 14;

    // Property array 
    /*lazy PropertyDescriptor*/
    private static PropertyDescriptor[] getPdescriptor(){
        PropertyDescriptor[] properties = new PropertyDescriptor[15];
    
        try {
            properties[PROPERTY_batchSize] = new PropertyDescriptor ( "batchSize", org.joone.engine.Monitor.class, "getBatchSize", "setBatchSize" ); // NOI18N
            properties[PROPERTY_currentCicle] = new PropertyDescriptor ( "currentCicle", org.joone.engine.Monitor.class, "getCurrentCicle", "setCurrentCicle" ); // NOI18N
            properties[PROPERTY_currentCicle].setExpert ( true );
            properties[PROPERTY_currentCicle].setHidden ( true );
            properties[PROPERTY_globalError] = new PropertyDescriptor ( "globalError", org.joone.engine.Monitor.class, "getGlobalError", "setGlobalError" ); // NOI18N
            properties[PROPERTY_globalError].setExpert ( true );
            properties[PROPERTY_globalError].setHidden ( true );
            properties[PROPERTY_learning] = new PropertyDescriptor ( "learning", org.joone.engine.Monitor.class, "isLearning", "setLearning" ); // NOI18N
            properties[PROPERTY_learningMode] = new PropertyDescriptor ( "learningMode", org.joone.engine.Monitor.class, "getLearningMode", "setLearningMode" ); // NOI18N
            properties[PROPERTY_learningRate] = new PropertyDescriptor ( "learningRate", org.joone.engine.Monitor.class, "getLearningRate", "setLearningRate" ); // NOI18N
            properties[PROPERTY_momentum] = new PropertyDescriptor ( "momentum", org.joone.engine.Monitor.class, "getMomentum", "setMomentum" ); // NOI18N
            properties[PROPERTY_preLearning] = new PropertyDescriptor ( "preLearning", org.joone.engine.Monitor.class, "getPreLearning", "setPreLearning" ); // NOI18N
            properties[PROPERTY_preLearning].setDisplayName ( "pre-learning cycles" );
            properties[PROPERTY_singleThreadMode] = new PropertyDescriptor ( "singleThreadMode", org.joone.engine.Monitor.class, "isSingleThreadMode", "setSingleThreadMode" ); // NOI18N
            properties[PROPERTY_supervised] = new PropertyDescriptor ( "supervised", org.joone.engine.Monitor.class, "isSupervised", "setSupervised" ); // NOI18N
            properties[PROPERTY_totCicles] = new PropertyDescriptor ( "totCicles", org.joone.engine.Monitor.class, "getTotCicles", "setTotCicles" ); // NOI18N
            properties[PROPERTY_totCicles].setDisplayName ( "epochs" );
            properties[PROPERTY_trainingPatterns] = new PropertyDescriptor ( "trainingPatterns", org.joone.engine.Monitor.class, "getTrainingPatterns", "setTrainingPatterns" ); // NOI18N
            properties[PROPERTY_trainingPatterns].setDisplayName ( "training patterns" );
            properties[PROPERTY_useRMSE] = new PropertyDescriptor ( "useRMSE", org.joone.engine.Monitor.class, "isUseRMSE", "setUseRMSE" ); // NOI18N
            properties[PROPERTY_validation] = new PropertyDescriptor ( "validation", org.joone.engine.Monitor.class, "isValidation", "setValidation" ); // NOI18N
            properties[PROPERTY_validationPatterns] = new PropertyDescriptor ( "validationPatterns", org.joone.engine.Monitor.class, "getValidationPatterns", "setValidationPatterns" ); // NOI18N
            properties[PROPERTY_validationPatterns].setDisplayName ( "validation patterns" );
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

    // Method array 
    /*lazy MethodDescriptor*/
    private static MethodDescriptor[] getMdescriptor(){
        MethodDescriptor[] methods = new MethodDescriptor[0];//GEN-HEADEREND:Methods
        
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

