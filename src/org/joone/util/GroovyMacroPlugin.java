package org.joone.util;

import org.joone.engine.*;
import org.joone.script.*;


public class GroovyMacroPlugin extends MonitorPlugin implements MacroInterface {
    
    
    static final long serialVersionUID = -4123807261022916301L;
    private transient JooneGroovyScript jScript;    
    private MacroManager macroManager;
    
    public GroovyMacroPlugin() {
    }
    
    public void set(String name, Object jObject){
        getGroovy().set(name, jObject);
    }
    
    private JooneGroovyScript getGroovy() {
        if (jScript == null) {
            jScript = new JooneGroovyScript();
        }
        return jScript;
    }
    
    protected void manageStart(Monitor mon) {
        getGroovy().set("jMon", mon);
        String macro = getMacroManager().getMacro("netStarted");
        runScript(macro, false);
    }
    
    protected void manageCycle(Monitor mon) {
        //Pass monitor object to script instance to allow access to monitor methods
        getGroovy().set("jMon", mon);
        String macro = getMacroManager().getMacro("cycleTerminated");
        runScript(macro, false);        
    }
    
    protected void manageStop(Monitor mon) {
        getGroovy().set("jMon", mon);
        String macro = getMacroManager().getMacro("netStopped");
        runScript(macro, false);        
    }
    
    protected void manageError(Monitor mon) {
        getGroovy().set("jMon", mon);
        String macro = getMacroManager().getMacro("errorChanged");
        runScript(macro, false);
    }
    
    /** Run a generic macro contained in a text
     * @parameter the text of the macro
     */
    public void runMacro(String text) {
        runScript(text, false);
    }
    
    private void runScript(String eventScript, boolean file){
        if (file){
            jScript.source(eventScript);
        }
        else {
            jScript.eval(eventScript);
        }
    }
    
    /** Getter for property macroManager.
     * @return Value of property macroManager.
     */
    public MacroManager getMacroManager() {
        if (macroManager == null)
            macroManager = new MacroManager();
        return macroManager;
    }
    
    /** Setter for property macroManager.
     * @param macroManager New value of property macroManager.
     */
    public void setMacroManager(MacroManager macroManager) {
        this.macroManager = macroManager;
    }
    
    protected void manageStopError(Monitor mon, String msgErr) {
    }
    
}
