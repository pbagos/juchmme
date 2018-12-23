package org.joone.io;

import java.beans.*;

public class YahooFinanceInputSynapseBeanInfo extends SimpleBeanInfo {
    
    // Bean descriptor//GEN-FIRST:BeanDescriptor
    /*lazy BeanDescriptor*/
    private static BeanDescriptor getBdescriptor(){
        BeanDescriptor beanDescriptor = new BeanDescriptor  ( org.joone.io.YahooFinanceInputSynapse.class , null ); // NOI18N//GEN-HEADEREND:BeanDescriptor
        
        // Here you can add code for customizing the BeanDescriptor.
        
        return beanDescriptor;     }//GEN-LAST:BeanDescriptor
    
    
    // Property identifiers//GEN-FIRST:Properties
    private static final int PROPERTY_advancedColumnSelector = 0;
    private static final int PROPERTY_buffered = 1;
    private static final int PROPERTY_enabled = 2;
    private static final int PROPERTY_endDate = 3;
    private static final int PROPERTY_firstRow = 4;
    private static final int PROPERTY_lastRow = 5;
    private static final int PROPERTY_maxBufSize = 6;
    private static final int PROPERTY_name = 7;
    private static final int PROPERTY_period = 8;
    private static final int PROPERTY_plugIn = 9;
    private static final int PROPERTY_startDate = 10;
    private static final int PROPERTY_stepCounter = 11;
    private static final int PROPERTY_stockData = 12;
    private static final int PROPERTY_symbol = 13;

    // Property array 
    /*lazy PropertyDescriptor*/
    private static PropertyDescriptor[] getPdescriptor(){
        PropertyDescriptor[] properties = new PropertyDescriptor[14];
    
        try {
            properties[PROPERTY_advancedColumnSelector] = new PropertyDescriptor ( "advancedColumnSelector", org.joone.io.YahooFinanceInputSynapse.class, "getAdvancedColumnSelector", "setAdvancedColumnSelector" ); // NOI18N
            properties[PROPERTY_buffered] = new PropertyDescriptor ( "buffered", org.joone.io.YahooFinanceInputSynapse.class, "isBuffered", "setBuffered" ); // NOI18N
            properties[PROPERTY_enabled] = new PropertyDescriptor ( "enabled", org.joone.io.YahooFinanceInputSynapse.class, "isEnabled", "setEnabled" ); // NOI18N
            properties[PROPERTY_endDate] = new PropertyDescriptor ( "endDate", org.joone.io.YahooFinanceInputSynapse.class, "getEndDate", "setEndDate" ); // NOI18N
            properties[PROPERTY_firstRow] = new PropertyDescriptor ( "firstRow", org.joone.io.YahooFinanceInputSynapse.class, "getFirstRow", "setFirstRow" ); // NOI18N
            properties[PROPERTY_lastRow] = new PropertyDescriptor ( "lastRow", org.joone.io.YahooFinanceInputSynapse.class, "getLastRow", "setLastRow" ); // NOI18N
            properties[PROPERTY_maxBufSize] = new PropertyDescriptor ( "maxBufSize", org.joone.io.YahooFinanceInputSynapse.class, "getMaxBufSize", "setMaxBufSize" ); // NOI18N
            properties[PROPERTY_name] = new PropertyDescriptor ( "name", org.joone.io.YahooFinanceInputSynapse.class, "getName", "setName" ); // NOI18N
            properties[PROPERTY_period] = new PropertyDescriptor ( "period", org.joone.io.YahooFinanceInputSynapse.class, "getPeriod", "setPeriod" ); // NOI18N
            properties[PROPERTY_plugIn] = new PropertyDescriptor ( "plugIn", org.joone.io.YahooFinanceInputSynapse.class, "getPlugIn", "setPlugIn" ); // NOI18N
            properties[PROPERTY_plugIn].setExpert ( true );
            properties[PROPERTY_startDate] = new PropertyDescriptor ( "startDate", org.joone.io.YahooFinanceInputSynapse.class, "getStartDate", "setStartDate" ); // NOI18N
            properties[PROPERTY_stepCounter] = new PropertyDescriptor ( "stepCounter", org.joone.io.YahooFinanceInputSynapse.class, "isStepCounter", "setStepCounter" ); // NOI18N
            properties[PROPERTY_stockData] = new PropertyDescriptor ( "stockData", org.joone.io.YahooFinanceInputSynapse.class, "getStockData", null ); // NOI18N
            properties[PROPERTY_symbol] = new PropertyDescriptor ( "symbol", org.joone.io.YahooFinanceInputSynapse.class, "getSymbol", "setSymbol" ); // NOI18N
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
            methods[METHOD_addNoise0] = new MethodDescriptor ( org.joone.io.YahooFinanceInputSynapse.class.getMethod("addNoise", new Class[] {Double.TYPE})); // NOI18N
            methods[METHOD_addNoise0].setDisplayName ( "" );
            methods[METHOD_canCountSteps1] = new MethodDescriptor ( org.joone.io.YahooFinanceInputSynapse.class.getMethod("canCountSteps", new Class[] {})); // NOI18N
            methods[METHOD_canCountSteps1].setDisplayName ( "" );
            methods[METHOD_check2] = new MethodDescriptor ( org.joone.io.YahooFinanceInputSynapse.class.getMethod("check", new Class[] {})); // NOI18N
            methods[METHOD_check2].setDisplayName ( "" );
            methods[METHOD_fwdGet3] = new MethodDescriptor ( org.joone.io.YahooFinanceInputSynapse.class.getMethod("fwdGet", new Class[] {})); // NOI18N
            methods[METHOD_fwdGet3].setDisplayName ( "" );
            methods[METHOD_fwdPut4] = new MethodDescriptor ( org.joone.io.YahooFinanceInputSynapse.class.getMethod("fwdPut", new Class[] {org.joone.engine.Pattern.class})); // NOI18N
            methods[METHOD_fwdPut4].setDisplayName ( "" );
            methods[METHOD_gotoFirstLine5] = new MethodDescriptor ( org.joone.io.YahooFinanceInputSynapse.class.getMethod("gotoFirstLine", new Class[] {})); // NOI18N
            methods[METHOD_gotoFirstLine5].setDisplayName ( "" );
            methods[METHOD_gotoLine6] = new MethodDescriptor ( org.joone.io.YahooFinanceInputSynapse.class.getMethod("gotoLine", new Class[] {Integer.TYPE})); // NOI18N
            methods[METHOD_gotoLine6].setDisplayName ( "" );
            methods[METHOD_initLearner7] = new MethodDescriptor ( org.joone.io.YahooFinanceInputSynapse.class.getMethod("initLearner", new Class[] {})); // NOI18N
            methods[METHOD_initLearner7].setDisplayName ( "" );
            methods[METHOD_numColumns8] = new MethodDescriptor ( org.joone.io.YahooFinanceInputSynapse.class.getMethod("numColumns", new Class[] {})); // NOI18N
            methods[METHOD_numColumns8].setDisplayName ( "" );
            methods[METHOD_randomize9] = new MethodDescriptor ( org.joone.io.YahooFinanceInputSynapse.class.getMethod("randomize", new Class[] {Double.TYPE})); // NOI18N
            methods[METHOD_randomize9].setDisplayName ( "" );
            methods[METHOD_readAll10] = new MethodDescriptor ( org.joone.io.YahooFinanceInputSynapse.class.getMethod("readAll", new Class[] {})); // NOI18N
            methods[METHOD_readAll10].setDisplayName ( "" );
            methods[METHOD_reset11] = new MethodDescriptor ( org.joone.io.YahooFinanceInputSynapse.class.getMethod("reset", new Class[] {})); // NOI18N
            methods[METHOD_reset11].setDisplayName ( "" );
            methods[METHOD_resetInput12] = new MethodDescriptor ( org.joone.io.YahooFinanceInputSynapse.class.getMethod("resetInput", new Class[] {})); // NOI18N
            methods[METHOD_resetInput12].setDisplayName ( "" );
            methods[METHOD_revGet13] = new MethodDescriptor ( org.joone.io.YahooFinanceInputSynapse.class.getMethod("revGet", new Class[] {})); // NOI18N
            methods[METHOD_revGet13].setDisplayName ( "" );
            methods[METHOD_revPut14] = new MethodDescriptor ( org.joone.io.YahooFinanceInputSynapse.class.getMethod("revPut", new Class[] {org.joone.engine.Pattern.class})); // NOI18N
            methods[METHOD_revPut14].setDisplayName ( "" );
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

