package org.joone.engine;

/**
 * The <code>Fifo</code> class represents a first-in-first-out 
 * (FIFO) stack of objects. 
 */

public class Fifo extends java.util.Vector {
    
    private static final long serialVersionUID = -3937649024771901836L;
    
/**
 * Tests if this stack is empty.
 *
 * @return  <code>true</code> if this stack is empty;
 *          <code>false</code> otherwise.
 */
public boolean empty() {
	return size() == 0;
}
	/**
	 * Looks at the object at the top of this stack without removing it 
	 * from the stack. 
	 *
	 * @return     the object at the top of this stack. 
	 * @exception  EmptyStackException  if this stack is empty.
	 */
	public synchronized Object peek() {
	int	len = size();

	if (len == 0)
	    throw new java.util.EmptyStackException();
	return elementAt(0);
	}
	/**
	 * Removes the object at the top of this stack and returns that 
	 * object as the value of this function. 
	 *
	 * @return     The object at the top of this stack.
	 * @exception  EmptyStackException  if this stack is empty.
	 */
	public synchronized Object pop() {
	Object	obj;

	obj = peek();
	removeElementAt(0);

	return obj;
	}
	/**
	 * Pushes an item onto the top of this stack. 
	 *
	 * @param   item   the item to be pushed onto this stack.
	 * @return  the <code>item</code> argument.
	 */
	public Object push(Object item) {
	addElement(item);
	return item;
	}
	/**
	 * Returns where an object is on this stack. 
	 *
	 * @param   o   the desired object.
	 * @return  the distance from the top of the stack where the object is]
	 *          located; the return value <code>-1</code> indicates that the
	 *          object is not on the stack.
	 */
	public synchronized int search(Object o) {
	int i = lastIndexOf(o);

	if (i >= 0) {
	    return size() - i;
	}
	return -1;
	}
}