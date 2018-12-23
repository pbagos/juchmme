/*
 * Logger.java
 *
 * Created on 26 febbraio 2004, 15.03
 */

package org.joone.log;

/**
 * Class used to decouple the engine's logging requests from
 * the libraries that expose the logging service.
 *
 * @author  P.Marrone
 */
public class LoggerFactory {
    
    /** Method to get the Logger to use to print out the log messages.
     * @return The instance of the Logger
     * @param cls The Class of the calling object
     */
    public static ILogger getLogger(Class cls) {
        ILogger iLog = null;
        String logger = null;
        
        // If JOONE is loaded in applet environment, we need
        // to take into consideration on the security exception issues.
        try {
            logger = System.getProperty("org.joone.logger");
            if (logger != null) {
                iLog = (ILogger)Class.forName(logger).newInstance();
                iLog.setParentClass(cls);
            }
        }
        catch(java.security.AccessControlException e) {
            // Do nothing. Let it falls through the below code
            // to become JooneLogger object.
        }
        catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
            return  null;
        }
        catch (InstantiationException ie) {
            ie.printStackTrace();
            return  null;
        }
        catch (IllegalAccessException iae) {
            iae.printStackTrace();
            return  null;
        }
        
        if (logger == null) {
            iLog = new JooneLogger();
            iLog.setParentClass(cls);
        }
        return iLog;
        
    }
}