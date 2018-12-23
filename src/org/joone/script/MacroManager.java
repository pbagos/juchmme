/*
 * MacroManager.java
 *
 * Created on 25 agosto 2002, 20.20
 */

package org.joone.script;

import java.util.Hashtable;
/**
 * This class is a manager of the macros of a Neural Network
 * @author  P.Marrone
 */
public class MacroManager implements java.io.Serializable {
    
    static final long serialVersionUID = 2855350620727616763L;
    private Hashtable macros; // The container of the macros
    /** Creates a new instance of MacroManager */
    public MacroManager() {
        macros = new Hashtable();
        macros = initMacro(macros);
    }
    
    protected Hashtable initMacro(Hashtable mm) {
        JooneMacro macro;
        macro = new JooneMacro();
        macro.setName("cycleTerminated");
        macro.setText("");
        macro.setEventMacro(true);
        mm.put(macro.getName(), macro);
        
        macro = new JooneMacro();
        macro.setName("errorChanged");
        macro.setText("");
        macro.setEventMacro(true);
        mm.put(macro.getName(), macro);
        
        macro = new JooneMacro();
        macro.setName("netStarted");
        macro.setText("");
        macro.setEventMacro(true);
        mm.put(macro.getName(), macro);
        
        macro = new JooneMacro();
        macro.setName("netStopped");
        macro.setText("");
        macro.setEventMacro(true);
        mm.put(macro.getName(), macro);
        
        return mm;
    }
    
    public synchronized void addMacro(String name, String text) {
        /* All the macros added by the user can't be event macro.
         * To insert an event macro, override the initMacro method. */
        boolean oldEvent = false;
        JooneMacro macro;
        if (macros.containsKey(name)) {
            macro = (JooneMacro)macros.get(name);
            oldEvent = macro.isEventMacro();
        }
        else
            macro = new JooneMacro();
        macro.setName(name);
        macro.setText(text);
        macro.setEventMacro(oldEvent);
        if (!macros.containsKey(name))
            macros.put(name, macro);
    }
    
    public String getMacro(String name) {
        JooneMacro macro = (JooneMacro)macros.get(name);
        if (macro != null)
            return macro.getText();
        else
            return null;
    }
    
    public boolean isEventMacro(String name) {
        JooneMacro macro = (JooneMacro)macros.get(name);
        if (macro != null)
            return macro.isEventMacro();
        else
            return false;
    }
    
    /** Removes a macro.
     * @return false if the macro doesn't exist or it's a system macro. Oterwise true
     */
    public boolean removeMacro(String name) {
        JooneMacro macro = (JooneMacro)macros.get(name);
        if (macro != null) {
            if (macro.isEventMacro())
                return false;
            else {
                macros.remove(name);
                return true;
            }
        }
        else
            return false;
    }
    
    /** Renames a macro.
     * @return false if the macro doesn't exist or it's a system macro. Oterwise true
     */
    public boolean renameMacro(String oldName, String newName) {
        JooneMacro macro = (JooneMacro)macros.get(oldName);
        if (macro != null) {
            if (macro.isEventMacro())
                return false;
            else {
                macros.remove(oldName);
                this.addMacro(newName, macro.getText());
                return true;
            }
        }
        else
            return false;
    }
    
    /** Getter for property macros.
     * @return a clone of the internal Hashtable
     */
    public Hashtable getMacros() {
        return (Hashtable)macros.clone();
    }
    
}
