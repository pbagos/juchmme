package org.joone.io;

import java.io.*;
import java.sql.*;
import java.util.*;

import org.joone.net.NetCheck;
import org.joone.exception.JooneRuntimeException;
import org.joone.engine.NetErrorManager;

import org.joone.log.*;

/**
 * <P>The JDBCInputSynapse provides support for data extraction from a database.
 * To use this synapse the user should ensure a JDBC Type 4 Driver is in the class path.
 * It is possible to use other JDBC driver types though you will have to refer to the vendors documentation,
 * it may require extra software insallation and this may limit your distributtion to certain Operating Systems.</P>
 * <P>The properties required by this JDBCInputSynapse Plugin are the following</P>
 * <P>Database Driver Name - e.g sun.jdbc.odbc.JdbcOdbcDriver</P>
 * <P>Database URL - e.g jdbc:mysql://localhost/MyDb?user=myuser&password=mypass
 * <P>SQLquery - e.g select val1,val2,result from xor;</P>
 * <P>Advanced Column Selector - This selects the values from the query result e.g '1,2' would
 * select val1 and val2. in this case.
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
 * <P>Any fields selected from a database should contain a single double or float format value.  The
 * data type is not so important it can be text or a number field so long as it contains just
 * one double or float format value.</P>
 * <P>E.g Correct = '2.31' Wrong= '3.45;1.21' and Wrong = 'hello' </P>
 *
 * @author     Julien Norman
 * @created    29/11/2002
 */
public class JDBCInputSynapse extends StreamInputSynapse {
    
    /** The object used when logging debug,errors,warnings and info. */
    private static final ILogger log = LoggerFactory.getLogger(JDBCInputSynapse.class);
    
    /** The name of the JDBC Database driver. */
    private String driverName = "";
    /** The URL of the database. */
    private String dbURL = "";
    /** The database query to use in order to obtain the input data. */
    private String SQLQuery = "";
    
    private final static long serialVersionUID =  -4642657913289986240L;
    
    /**
     *Constructor for the JDBCInputSynapse object
     */
    public JDBCInputSynapse() {
        super();
    }
    
    /**
     * <P>Constructor for the JDBCInputSynapse object that allows all options.
     * Allows the user to construct a JDBCInputSynapse in one call.</P>
     * @param newDrivername The class name of the database driver to use.
     * @param newdbURL The Universal Resource Locator to enable connection to the database.
     * @param newSQLQuery The database SQL query to apply to the database.
     * @param newAdvColSel The comma delimited selection of what data columns to use.
     * @param newfirstRow The first row to apply to the network.
     * @param newlastRow The last row to apply to the network.
     * @param buffered Whether this synapse is buffered or not.
     */
    public JDBCInputSynapse(String newDrivername,String newdbURL, String newSQLQuery,String newAdvColSel,int newfirstRow,int newlastRow,boolean buffered) {
        super();
        
        setBuffered(buffered);
        setFirstRow(newfirstRow);
        setLastRow(newlastRow);
        setAdvancedColumnSelector(newAdvColSel);
        setdriverName(newDrivername);
        setdbURL(newdbURL);
        setSQLQuery(newSQLQuery);
        
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
     * @return    The sQLQuery used by this JDBC input syanpse.
     */
    public java.lang.String getSQLQuery() {
        return SQLQuery;
    }
    
    
    /**
     *  Reads this JDBCInputSynapse object into memory
     *
     * @param  in                          The object input stream that this object should be read from
     * @exception  IOException             The Input Output Exception
     * @exception  ClassNotFoundException  The class not found exception
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        super.readObjectBase(in);
        if (in.getClass().getName().indexOf("xstream") == -1) {
            driverName = (String) in.readObject();
            dbURL = (String) in.readObject();
            SQLQuery = (String) in.readObject();
        }
        if (!isBuffered() || (getInputVector().size() == 0)) {
            setdriverName(driverName);
        }
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
            this.resetInput();
            this.setTokens(null);
        }
        
        // Check all params have been set then call the initInputStream to read the data in.
        /*
        if ( (driverName != null) && (!driverName.equals("")) )
            if ( (dbURL != null) && (!dbURL.equals("")) )
                if ( (SQLQuery != null) && (!SQLQuery.equals("")))
                    initInputStream(); */
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
            this.resetInput();
            this.setTokens(null);
        }
        
        // Check all params have been set then call the initInputStream to read the data in.
        /*if ( (driverName != null) && (!driverName.equals("")) )
            if ( (dbURL != null) && (!dbURL.equals("")) )
                if ( (SQLQuery != null) && (!SQLQuery.equals("")))
                    initInputStream(); */
    }
    
    
    /**
     *  Sets the sQLQuery attribute of the JDBCInputSynapse object
     *
     * @param  newSQLQuery  The new database SQL Query.
     */
    public void setSQLQuery(java.lang.String newSQLQuery) {
        // Check that it's actually changed.
        if (!SQLQuery.equals(newSQLQuery) ) {
            SQLQuery = newSQLQuery;
            this.resetInput();
            this.setTokens(null);
        }
        
        // Check all params have been set then call the initInputStream to read the data in.
        /*if ( (driverName != null) && (!driverName.equals("")) )
            if ( (dbURL != null) && (!dbURL.equals("")) )
                if ( (SQLQuery != null) && (!SQLQuery.equals("")))
                    initInputStream(); */
    }
    
    
    /**
     *  Writes this JDBCSynapseInput object to the ObjectOutputStream out.
     *
     * @param  out              The ObjectOutputSteeam that this object should be written to
     * @exception  IOException  The Input Output Exception if any
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        super.writeObjectBase(out);
        if (out.getClass().getName().indexOf("xstream") == -1) {
            out.writeObject(driverName);
            out.writeObject(dbURL);
            out.writeObject(SQLQuery);
        }
    }
    
    
    /**
     *  Connects to the database using Driver name and db URL and selects data using the SQLQuery.
     */
    protected void initInputStream() throws JooneRuntimeException {
        
        Connection con;
        // This will contain pattern set
        // Ensure all properties have been set
        if ((driverName != null) && (!driverName.equals(new String("")))) {
            // Check that Driver Name has been set
            
            if ((dbURL != null) && (!dbURL.equals(new String("")))) {
                // Check that Database URL has been set
                
                if ((SQLQuery != null) && (!SQLQuery.equals(new String("")))) {
                    // Check that SQLQuery has been set
                    
                    try {
                        Class.forName(driverName);
                        //	E.g sun.jdbc.odbc.JdbcOdbcDriver to use JDBC:ODBC bridge
                        con = DriverManager.getConnection(dbURL);
                        // URL if you have Fred ODBC Data source then e.g jdbc:odbc:Fred
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(SQLQuery);
                        
                        StringBuffer SQLNetInput = new StringBuffer();
                        
                        while (rs.next()) {
                            if (rs.getMetaData().getColumnCount() >= 1) {
                                SQLNetInput.append(rs.getDouble(1));
                                // Get first Column
                            }
                            for (int counter = 2; counter <= rs.getMetaData().getColumnCount(); counter++) {
                                // Loop through remaining columns
                                
                                SQLNetInput.append(";");
                                SQLNetInput.append(rs.getDouble(counter));
                                // Add data from ResultSet
                            }
                            SQLNetInput.append('\n');
                            // Add a new line after completion of a pattern
                        }
                        StreamInputTokenizer sit;
                        if (getMaxBufSize() > 0)
                            sit = new StreamInputTokenizer(new StringReader(SQLNetInput.toString()), getMaxBufSize());
                        else
                            sit = new StreamInputTokenizer(new StringReader(SQLNetInput.toString()));
                        super.setTokens(sit);
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
                    } catch (IOException ex) {
                        // Use Log4j
                        log.error( "IOException thrown while initializing the JDBCInputStream. Message is : " + ex.getMessage(), ex );	// LOG4J
                        if ( getMonitor() != null )
                            new NetErrorManager(getMonitor(),"IOException thrown while initializing the JDBCInputStream. Message is : " + ex.getMessage());
                        //System.out.println("IOException thrown while initializing the JDBCInputStream. Message is : " + ex.getMessage());
                        // SYS LOG
                    }
                    
                } else {
                    // Warn the user or app that the SQLQuery has not been set
                    String err = "The SQL Query has not been entered!";
                    log.warn( err );	// LOG4J
                    if ( getMonitor() != null )
                        new NetErrorManager(getMonitor(),"The SQL Query has not been entered!");
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
        } else {
            // Driver Name entered check that it is valid
            try {
                Class.forName(driverName);
                //	Check driver class is correct E.g sun.jdbc.odbc.JdbcOdbcDriver to use JDBC:ODBC bridge
                // See if we can get a connection if there is a URL entered....
                if ((dbURL != null)&&(dbURL.compareTo("")!=0)) {
                    con = DriverManager.getConnection(dbURL);
                }
            } catch (ClassNotFoundException ex) {
                // Use Log4j
                checks.add(new NetCheck(NetCheck.FATAL, "Could not find Database Driver Class. Check Database Driver is in the classpath and is readable.",this));	// LOG4J
            } catch (SQLException sqlex) {
                checks.add(new NetCheck(NetCheck.FATAL, "The Database URL is incorrect. Connection error is : "+sqlex.toString(),this));	// LOG4J
            }
        }
        
        // Check that the dbURL has been entered.
        if ((dbURL == null)||(dbURL.compareTo("")==0)) {
            checks.add(new NetCheck(NetCheck.FATAL, "Database URL needs to be entered.", this));
        }
        
        // Check that the SQLQuery has been entered
        if ((SQLQuery == null)||(SQLQuery.compareTo("")==0)) {
            checks.add(new NetCheck(NetCheck.FATAL, "Database SQL Query needs to be entered.", this));
        }
        
        return checks;
    }
    
}

