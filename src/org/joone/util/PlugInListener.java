/*
 * PlugInListener.java
 *
 * Created on October 11, 2004, 4:29 PM
 */

package org.joone.util;

/**
 * This interface defines the methods needed to be implemented by listeners that
 * listens to plug-ins that might send data changed / plug-in events.
 * 
 * <!-- This class replaces InputPluginListener and OutputPluginListener -->
 *
 * @author  Boris Jansen
 */
public interface  PlugInListener {
    
    /**
     * This method is called by plug-ins whenever data is changed.
     *
     * @param anEvent the event that is send, i.e. the event indicating that the
     * data is changed.
     */
    public void dataChanged(PlugInEvent anEvent);
}
