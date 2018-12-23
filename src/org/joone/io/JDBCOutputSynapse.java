package org.joone.io;

import java.io.*;
import java.sql.*;
import java.util.*;

import org.joone.net.NetCheck;
import org.joone.exception.JooneRuntimeException;
import org.joone.engine.NetErrorManager;

import org.joone.log.*;

/**
 * <P>The JDBCOutputSynapse provides support for data input to a database.
 * To use this synapse the user should ensure a JDBC Type 4 Driver is in the class path.
 * It is possible to use other JDBC driver types though you will have to refer to the vendors documentation,
 * it may require extra software insallation and this may limit your distributtion to certain Operating Systems.</P>
 * <P>The properties required by this JDBCOutputSynapse Plugin are the following</P>
 * <P>Database Driver Name - e.g sun.jdbc.odbc.JdbcOdbcDriver</P>
 * <P>Database URL - e.g jdbc:mysql://localhost/MyDb?user=myuser&password=mypass
 * <P>SQLAmendment - e.g "INSERT INTO MY_RESULT_TABLE (RESULT1,RESULT2) VALUES(JOONE[1],JOONE[2])" where JOONE[1] is the first value of the current output.</P>
 * <P>Note : The database URL uses specific protocol after the "jdbc:" section check with the jdbc driver vendor for specific info.</P>
 * <P>Some commonly used Driver protocols shown below ...</P>
 * <BR>
 * <P>Driver {com.mysql.jdbc.Driver} </P>
 * <P>Protocol {jdbc:mysql://[hostname][,failoverhost...][:port]/[dbname][?param1=value1][&param2=value2].....} MySQL Protool </P>
 * <P>Example {jdbc:mysql://localhost/test?user=blah&password=blah} </P>
 * <P>Web Site {http://www.mysql.com} </P>
 * <BR>
 * <P>Driver {sun.jdbc.odbc.JdbcOdbcDriver} </P>
 * <P>Protocol { jdbc:odbc:<data-source-name>[;<attribute-name>=<attribute-value>]* }  ODBC Protocol </P>
 * <P>Example {jdbc:odbc:mydb;UID=me;PWD=secret} </P>
 * <P>Web Site {http://www.java.sun.com} </P>
 * <BR>
 * <P>Data Types</P>
 * <BR>
 * <P>Any fields receiving a value should be able to contain a single double.  The
 * data type is not so important it can be text or a number field so long as can hold a double value.</P>
 *
 * @author     Julien Norman
 */
public class JDBCOutputSynapse extends StreamOutputSynapse {
    
    /** The object used when logging debug,errors,warnings and info. */
    private static final ILogger log = LoggerFactory.getLogger(JDBCOutputSynapse.class);
    
    /** The name of the JDBC Database driver. */
    private String driverName = "";
    /** The URL of the database. */
    private String dbURL = "";
    /** The database query to use in order to obtain the input data. */
    private String SQLAmendment = "";
    
    private transient Connection con = null;
    private transient Statement stmt = null;
    
    private static final long serialVersionUID = 2176832390164459511L;
    /**
     *Constructor for the JDBCOutputSynapse object
     */
    public JDBCOutputSynapse() {
        super();
    }
    
    /**
     * <P>Constructor for the JDBCInputSynapse object that allows all options.
     * Allows the user to construct a JDBCInputSynapse in one call.</P>
     * @param newDrivername The class name of the database driver to use.
     * @param newdbURL The Universal Resource Locator to enable connection to the database.
     * @param newSQLAmendment The database SQL amendment to apply to the database.
     * @param buffered Whether this synapse is buffered or not.
     */
    public JDBCOutputSynapse(String newDrivername, String newdbURL, String newSQLAmendment, boolean buffered) {
        super();
        
        setBuffered(buffered);
        setdriverName(newDrivername);
        setdbURL(newdbURL);
        setSQLAmendment(newSQLAmendment);
        
    }
    
    /**
     *  Gets the name of the database jdbc driver used by this JDBC input syanpse.
     *
     * @return    The JDBC Driver name
     */
    public java.lang.String getdriverName() {
        return driverName;
    }
    
    
    /**
     *  Gets the name of the database Universal Resource Location (URL)
     *
     * @return    The database URL used by this JDBC input syanpse.
     */
    public java.lang.String getdbURL() {
        return dbURL;
    }
    
    
    /**
     *  Gets the SQL Query used to select data from the database.
     *
     * @return    The SQLAmendment used by this JDBC input syanpse.
     */
    public java.lang.String getSQLAmendment() {
        return SQLAmendment;
    }
    
    
    
    /**
     *  Sets the name of the database jdbc driver.
     *
     * @param  newDriverName  The JDBC Driver name
     */
    public void setdriverName(java.lang.String newDriverName) {
        // Check that it's actually changed.
        if (!newDriverName.equals(driverName)) {
            driverName = newDriverName;
        }
    }
    
    
    /**
     *  Gets the name of the database Universal Resource Location (URL)
     *
     * @param  newdbURL  The database URL to use for selecting input. See the notes on this class for usage.
     */
    public void setdbURL(java.lang.String newdbURL) {
        // Check it's actually changed.
        if (!dbURL.equals(newdbURL) ) {
            dbURL = newdbURL;
        }
    }
    
    
    /**
     *  Sets the SQLAmendment attribute of the JDBCInputSynapse object
     *
     * @param  newSQLAmendment  The new database SQL Amendment.
     */
    public void setSQLAmendment(java.lang.String newSQLAmendment) {
        // Check that it's actually changed.
        if (!SQLAmendment.equals(newSQLAmendment) ) {
            SQLAmendment = newSQLAmendment;
        }
    }
    
    /**
     *  Connects to the database using Driver name and db URL and selects data using the SQLAmendment.
     */
    protected void initStream() throws JooneRuntimeException {
        
        // This will contain pattern set
        // Ensure all properties have been set
        if ((driverName != null) && (!driverName.equals(new String("")))) {
            // Check that Driver Name has been set
            
            if ((dbURL != null) && (!dbURL.equals(new String("")))) {
                // Check that Database URL has been set
                
                if ((SQLAmendment != null) && (!SQLAmendment.equals(new String("")))) {
                    // Check that SQLAmendment has been set
                    
                    try {
                        Class.forName(driverName);
                        //	E.g sun.jdbc.odbc.JdbcOdbcDriver to use JDBC:ODBC bridge
                        con = DriverManager.getConnection(dbURL);
                        // URL if you have Fred ODBC Data source then e.g jdbc:odbc:Fred
                        stmt = con.createStatement();
                        
                        //ResultSet rs = stmt.executeQuery(SQLAmendment);
                        
                    } catch (ClassNotFoundException ex) {
                        // Use Log4j
                        log.error( "Could not find Database Driver Class while initializing the JDBCInputStream. Message is : " + ex.getMessage(), ex );	// LOG4J
                        if ( getMonitor() != null )
                            new NetErrorManager(getMonitor(),"Could not find Database Driver Class while initializing the JDBCInputStream. Message is : " + ex.getMessage());
                        //System.out.println("Could not find Database Driver Class while initializing the JDBCInputStream. Message is : " + ex.getMessage());
                        // SYS LOG
                    } catch (SQLException sqlex) {
                        // Use Log4j
                        log.error( "SQLException thrown while initializing the JDBCInputStream. Message is : " + sqlex.getMessage(), sqlex );	// LOG4J
                        if ( getMonitor() != null )
                            new NetErrorManager(getMonitor(),"SQLException thrown while initializing the JDBCInputStream. Message is : " + sqlex.getMessage());
                        //System.out.println("SQLException thrown while initializing the JDBCInputStream. Message is : " + sqlex.getMessage());
                        // SYS LOG
                    }
                    
                } else {
                    // Warn the user or app that the SQLAmendment has not been set
                    String err = "The SQL Amendment has not been entered!";
                    log.warn( err );	// LOG4J
                    if ( getMonitor() != null )
                        new NetErrorManager(getMonitor(),"The SQL Amendment has not been entered!");
                }
            } else {
                // Warn the user or app that the Database URL has not been set
                String err = "The Database URL has not been entered!";
                log.warn( err );	// LOG4J
                if ( getMonitor() != null )
                    new NetErrorManager(getMonitor(),"The Database URL has not been entered!");
            }
        } else {
            // Warn user or app that the Driver Name has not been set
            String err = "The Driver Name has not been entered!";
            log.warn( err );		// LOG4J
            if ( getMonitor() != null )
                new NetErrorManager(getMonitor(),"The Driver Name has not been entered!");
        }
    }
    
    /**
     * Check that parameters are set correctly for the this JDBCInputSynapse object.
     *
     * @see Synapse
     * @return validation errors.
     */
    public TreeSet check() {
        
        // Get the parent's check messages.
        TreeSet checks = super.check();
        Connection con; // To test the connection.
        
        // See if the driver name has been entered.
        if ((driverName == null)||(driverName.compareTo("")==0)) {
            checks.add(new NetCheck(NetCheck.FATAL, "Database Driver Name needs to be entered.", this));
        }
        else {
            // Driver Name entered check that it is valid
            try {
                Class.forName(driverName);
                //	Check driver class is correct E.g sun.jdbc.odbc.JdbcOdbcDriver to use JDBC:ODBC bridge
                // See if we can get a connection if there is a URL entered....
                if ((dbURL != null)&&(dbURL.compareTo("")!=0)) {
                    con = DriverManager.getConnection(dbURL);
                    stmt = con.createStatement();
                }
            } catch (ClassNotFoundException ex) {
                // Use Log4j
                checks.add(new NetCheck(NetCheck.FATAL, "Could not find Database Driver Class. Check Database Driver is in the classpath and is readable.",this));	// LOG4J
            }
            catch (SQLException sqlex) {
                checks.add(new NetCheck(NetCheck.FATAL, "The Database URL is incorrect. Connection error is : "+sqlex.toString(),this));	// LOG4J
            }
        }
        
        // Check that the dbURL has been entered.
        if ((dbURL == null)||(dbURL.compareTo("")==0)) {
            checks.add(new NetCheck(NetCheck.FATAL, "Database URL needs to be entered.", this));
        }
        
        // Check that the SQLAmendment has been entered
        if ((SQLAmendment == null)||(SQLAmendment.compareTo("")==0)) {
            checks.add(new NetCheck(NetCheck.FATAL, "Database SQL Amendment needs to be entered.", this));
        }
        
        return checks;
    }
    
    /**
     * Writes the pattern data to the database specified in the dbURL.  The SQLAmendment proprty
     * should contain an SQL amendment using JOONE[X] to obtain the Xth pattern value where X=1 is the first value.
     * E.g. "INSERT INTO MY_RESULTS (RESULT1,RESULT2) VALUES('JOONE[1]','JOONE[2]')"
     * @param pattern The values to write to the database.
     */
    public void write(org.joone.engine.Pattern pattern) throws JooneRuntimeException {
        
        String Amendment = new String(SQLAmendment);
        
        if ( (con == null) || (stmt==null) || (pattern.getCount() == 1))
            initStream();
        
        if ( pattern.getCount() == -1) {
            try{
            if ( stmt != null)
                stmt.close();
            if ( con != null)
                con.close();
            }
            catch(SQLException ex)
            {
                stmt = null;
                con = null;
            }
        }
        else{
            try{
                if ( Amendment != null){
                    if ( !Amendment.equals("") ){
                        
                        // Loop through pattern and replace each occurence of JOONE[X] with appropriate value
                        for (int i=0; i < pattern.getArray().length; i++) {
                            
                            while ( Amendment.indexOf("JOONE["+(i+1)+"]")>=0) { // While we have a JOONE[i] string
                                StringBuffer buf = new StringBuffer(Amendment);
                                Amendment = buf.replace(Amendment.indexOf("JOONE["+(i+1)+"]") ,Amendment.indexOf("JOONE["+(i+1)+"]") + new String("JOONE["+(i+1)+"]").length(), new Double(pattern.getArray()[i]).toString()).toString();
                            }
                        }
                        // Ok we've replace the JOONE[X] strings with the correct values now execute the amendment.
                        stmt.executeUpdate(Amendment);
                    }
                }
            }
            catch(java.sql.SQLException ex){
                String err = new String("An SQL error occurred while trying to execute ["+Amendment+"], error is "+ex.toString());
                log.error( err );	// LOG4J
                if ( getMonitor() != null )
                    new NetErrorManager(getMonitor(),err);
            }
        }
    }
    
}

