/*
 * User: Harry Glasgow
 * Date: 12/12/2002
 * Time: 18:19:16
 * Interface that defines classes of data that
 * an Inspectable object can produce.
 * The constructor should  parameters sufficient to
 * be able to create the Component.
 */
package org.joone.inspection;

public interface Inspection {

    /**
     * Method to get the inspectable values of the Inspectable class.
     * @see org.joone.Inspectable
     * @return representation of the Inspectable class data.
     */
    public Object[][] getComponent();


    /**
     * Method to get the title of the object for
     * display in the InspectionFrame title bar.
     * @see org.joone.InspectionFrame
     * @return title of the object.
     */
    public String getTitle();
    
    /** Method to get the necessity to display a 
     *  column containing the row's numbers for 
     *  these inspection values.
     * @return true if the rows numbers must be displayed
     */
    public boolean rowNumbers();
    
    /** Method to get the names of each column
     * 
     * @return the columns' names
     */
    public Object[] getNames();
    
    /** Sets the array of values for this component
     * @param newValues Array of new values
     */    
    public void setComponent(final java.lang.Object[][] newValues);
    
}
