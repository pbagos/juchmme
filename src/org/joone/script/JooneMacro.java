/*
 * JooneMacro.java
 *
 * Created on 25 august 2002, 17.52
 */

package org.joone.script;

/**
 * This class represents a runnable BeanShell macro
 * @author  P.Marrone
 */
public class JooneMacro implements java.io.Serializable, java.lang.Cloneable {
    
    static final long serialVersionUID = 6361561451436197429L;
    private String text;  // the text of the macro
    private boolean event;
    private String name;
    
    /** Creates a new instance of JooneMacro */
    public JooneMacro() {
    }
    
    public String toString() {
        return text;
    }
    
    /** Getter for property text.
     * @return Value of property text.
     */
    public java.lang.String getText() {
        return text;
    }
    
    /** Setter for property text.
     * @param text New value of property text.
     */
    public void setText(java.lang.String text) {
        this.text = text;
    }
    
    /** Getter for property system.
     * This property indicates if the macro is bound to a neural net's event.
     * An event macro can't be deleted, nor renamed.
     * @return Value of property system.
     */
    public boolean isEventMacro() {
        return event;
    }
    
    /** Setter for property system.
     * This property indicates if the macro is bound to a neural net's event.
     * An event macro can't be deleted, nor renamed.
     * @param system New value of property system.
     */
    public void setEventMacro(boolean newValue) {
        this.event = newValue;
    }
    
    /** Getter for property name.
     * @return Value of property name.
     */
    public java.lang.String getName() {
        return name;
    }
    
    /** Setter for property name.
     * @param name New value of property name.
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }
    
    public synchronized Object clone() {
        JooneMacro newMacro = new JooneMacro();
        newMacro.setText(text);
        newMacro.setName(name);
        newMacro.setEventMacro(event);
        return newMacro;
    }
    
}
