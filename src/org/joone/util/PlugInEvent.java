/*
 * PlugInEvent.java
 *
 * Created on October 11, 2004, 4:36 PM
 */

package org.joone.util;

import java.util.EventObject;

/**
 * This event is sent by plug-ins indicating that data is changed. Listeners 
 * (implemening {@link PlugInListener}) will be notified by this event that data
 * is changed.
 *
 * @author  Boris Jansen
 */
public class PlugInEvent extends EventObject {
    
    /** 
     * Creates a new instance of PlugInEvent 
     *
     * @param anObject the object that creates and sends this event to the listeners.
     */
    public PlugInEvent(Object anObject) {
        super(anObject);
    }
}
