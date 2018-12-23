/*
 * Log4JLogger.java
 *
 * Created on 26 febbraio 2004, 15.26
 */

package org.joone.log;

import org.apache.log4j.Logger;
/**
 * Logger that uses Apache's Log4J to log the messages.
 * Use it by declaring the property 
 * -Dorg.joone.logger="org.joone.log.JooneLogger"
 * @author  PMLMAPA
 */
public class Log4JLogger implements ILogger {
    
    private Logger log = null;
    
    /** Creates a new instance of Log4JLogger */
    public Log4JLogger() {
    }
    
    public void debug(Object obj) {
        log.debug(obj);
    }
    
    public void debug(Object obj, Throwable thr) {
        log.debug(obj, thr);
    }
    
    public void error(Object obj) {
        log.error(obj);
    }
    
    public void error(Object obj, Throwable thr) {
        log.error(obj, thr);
    }
    
    public void fatal(Object obj) {
        log.fatal(obj);
    }
    
    public void fatal(Object obj, Throwable thr) {
        log.fatal(obj, thr);
    }
    
    public void info(Object obj) {
        log.info(obj);
    }
    
    public void info(Object obj, Throwable thr) {
        log.info(obj, thr);
    }
    
    public void warn(Object obj) {
        log.warn(obj);
    }
    
    public void warn(Object obj, Throwable thr) {
        log.warn(obj, thr);
    }
    
    public void setParentClass(Class cls) {
        log = Logger.getLogger( cls );
    }
    
}
