package org.joone.exception;

/**
 * This is a wrapper class for the <code>RuntimeException</code> thrown by 
 * the application.
 * 
 * @see java.lang.RuntimeException
 * @author tsmets
 */
public class JooneException 
    extends Exception 
{
    private Exception initialException = null;
	/**
	 * Constructor for JooneRunTimeException.
	 */
	public JooneException() 
    {
		super();
	}

	/**
	 * Constructor for JooneRunTimeException.
	 * @param s
	 */
	public JooneException(String s) 
    {
		super(s);
	}

}
