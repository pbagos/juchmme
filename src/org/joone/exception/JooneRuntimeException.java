package org.joone.exception;

/**
 * This is a wrapper class for the <code>Exception</code> thrown by 
 * the application.
 * 
 * @see java.lang.Exception
 * @author tsmets
 */
public class JooneRuntimeException 
    extends RuntimeException 
{
    private Throwable initialException = null;
    private String msg = null;
    
    /**
     * Constructor for JooneRuntimeException.
     * 
     * @param aMessage to be displayed.
     */
    public JooneRuntimeException (String aMessage) 
    {
        super (aMessage);
    }

	/**
	 * Constructor for JooneRunTimeException.
     * @param anInitialException When applying the Original Exception that was thrown.
	 */
	public JooneRuntimeException (Throwable anInitialException) 
    {
		super();
        initialException = anInitialException;        
	}

	/**
	 * Constructor for JooneRunTimeException.
     * 
	 * @param aMessage The message explaining the origin of the Exception
     * @param anInitialException When applying the Original Exception that was thrown.
     * 
	 */
	public JooneRuntimeException (String aMessage, Throwable anInitialException) 
    {
		super ( aMessage );
        initialException = anInitialException;                
	}
    
    
	/**
	 * @see java.lang.Throwable#getLocalizedMessage()
	 */
	public String getLocalizedMessage () 
    {
		return super.getLocalizedMessage( );
	}

	/**
	 * @see java.lang.Throwable#getMessage()
	 */
	public String getMessage() 
    {
		StringBuffer buf = new StringBuffer (super.getMessage());
        // TO DO 
        // Provide a properly formatted Message.
        // The Message building procedure should hold consideration that 
        // the Exception may not have a reference to a RTE...
        // 
        return buf.toString ();
	}
}
