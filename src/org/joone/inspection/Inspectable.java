/*
 * User: Harry Glasgow
 * Date: 12/12/2002
 * Time: 18:13:12
 * Interface that defines methods for classes that
 * can expose their internal values for inspection.
 */
package org.joone.inspection;

import java.util.Collection;

public interface Inspectable {

    /**
     * Method to get a collection of inspectable objects.
     * @see org.joone.Inspection
     * @return list of Inspectable objects
     */
    public Collection Inspections();

    /**
     * Method to get the title to show
     * in the InspectionFrame tab.
     * @see org.joone.InspectionFrame
     * @return title of the class.
     */
    public String InspectableTitle();
}
