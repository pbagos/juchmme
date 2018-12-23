/*
 * JooneLogger.java
 *
 * Created on 26 febbraio 2004, 15.52
 */

package org.joone.log;

/**
 * Internal logger. Use it instead of LogJ4, by declaring the property
 * -Dorg.joone.logger="org.joone.log.JooneLogger"
 *
 * @author  PMLMAPA
 */
public class JooneLogger implements ILogger {
    protected Class pClass;
    /** Creates a new instance of JooneLogger */
    public JooneLogger() {
    }
    
    public void debug(Object obj) {
        System.out.println(formatMsg("DEBUG", (String)obj));
    }
    
    public void debug(Object obj, Throwable thr) {
        System.out.println(formatMsg("DEBUG", (String)obj));
        thr.printStackTrace();
    }
    
    public void error(Object obj) {
        System.out.println(formatMsg("ERROR", (String)obj));
    }
    
    public void error(Object obj, Throwable thr) {
        System.out.println(formatMsg("ERROR", (String)obj));
        thr.printStackTrace();
    }
    
    public void fatal(Object obj) {
        System.out.println(formatMsg("FATAL", (String)obj));
    }
    
    public void fatal(Object obj, Throwable thr) {
        System.out.println(formatMsg("FATAL", (String)obj));
        thr.printStackTrace();
    }
    
    public void info(Object obj) {
        System.out.println(formatMsg("INFO", (String)obj));
    }
    
    public void info(Object obj, Throwable thr) {
        System.out.println(formatMsg("INFO", (String)obj));
        thr.printStackTrace();
    }
        
    public void warn(Object obj) {
        System.out.println(formatMsg("WARN", (String)obj));
    }
    
    public void warn(Object obj, Throwable thr) {
        System.out.println(formatMsg("WARN", (String)obj));
        thr.printStackTrace();
    }
    
    public void setParentClass(Class cls) {
        pClass = cls;
    }
    
    protected String formatMsg(String sev, String msg) {
        return "["+Thread.currentThread().getName()+"] ["+sev+"] - "
            +pClass.getName()+" - "+msg;
    }
}
