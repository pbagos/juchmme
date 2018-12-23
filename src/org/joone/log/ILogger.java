/*
 * Logger.java
 *
 * Created on 26 feb 2004, 15.17
 */

package org.joone.log;

/**
 * Interface that defines the public methods of the logger object.
 * To be fully compatible with apache Log4j, its interface derivates
 * from the org.apache.log4j.Category class.
 *
 * @author  P.Marrone
 */
public interface ILogger {
    void setParentClass(Class cls);
    void debug(Object obj);
    void debug(Object obj, Throwable thr);
    void error(Object obj);
    void error(Object obj, Throwable thr);
    void fatal(Object obj);
    void fatal(Object obj, Throwable thr);
    void info(Object obj);
    void info(Object obj, Throwable thr);
    void warn(Object obj);
    void warn(Object obj, Throwable thr);
    
}
