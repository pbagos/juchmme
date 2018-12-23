package org.joone.io;

import java.io.*;
import java.text.*;
import java.net.URL;
import java.util.*;

import org.joone.log.*;
import org.joone.net.NetCheck;
import org.joone.exception.JooneRuntimeException;
import org.joone.engine.NetErrorManager;

/**
 * <P>The YahooFinanceInputSynapse provides support for financial data input from financial markets.  The
 * synapse contacts YahooFinance services and downloads historical data for the chosen symbol and date range.
 * Finally the data is presented to the network in reverse date order i.e oldest first.  </P>
 * <P>This synapse provides the following info .. </P>
 * <P>Open as column 1</P>
 * <P>High as column 2</P>
 * <P>Low as column 3</P>
 * <P>Close as column 4.</P>
 * <P>Volume as column 5.</P>
 * <P>Adj.Close as column 6.</P>
 * <P>For the particular stock symbol.</P>
 * <BR>
 * <P> Developer Notes : </P>
 * <P> This YahooFinanceInputSynapse uses the following format to extract stock financial information from the Yahoo Network</P>.
 * <P>http://table.finance.yahoo.com/table.csv?a=8&b=1&c=2002&d=11&e=3&f=2002&s=tsco.l&y=0&g=d&ignore=.csv</P>
 * <BR>
 * <P>a = From Month 0 - 11</P>
 * <P>b = From Day 1-31</P>
 * <P>c = From Year XXXX</P>
 * <P>d = To Month 0-11</P>
 * <P>e = To Day 1-31</P>
 * <P>f = To Year XXXX</P>
 * <P>s = Symbol</P>
 * <P>y = [record] from record to + 200 records</P>
 * <P>g=[d] or[m] or [y]		- daily or monthly or yearly</P>
 * <P>ignore = .csv</P>
 *
 */
public class YahooFinanceInputSynapse extends StreamInputSynapse {
    
    /** The object used when logging debug,errors,warnings and info. */
    private static final ILogger log = LoggerFactory.getLogger(YahooFinanceInputSynapse.class);
    
    String [] months = new String [] {"January","February","March","April","May","June","July","August","September","October","November","December"};
    String [] frequency = new String [] {"Daily","Weekly","Monthly"};
    String [] freq_conv = new String [] {"d","w","m"};
    String Symbol = new String("");
    DateFormat date_formater = DateFormat.getDateInstance(DateFormat.MEDIUM);
    Calendar CalendarStart = Calendar.getInstance();
    Calendar CalendarEnd = Calendar.getInstance();
    String DateStart = new String(date_formater.format(CalendarStart.getTime()));
    String DateEnd = new String(date_formater.format(CalendarEnd.getTime()));
    String Period = new String("Daily");
    private transient Date startDate = CalendarStart.getTime();
    private transient Date endDate = CalendarEnd.getTime();
    private Vector StockData [] = new Vector [6];	// Holds all the data + optional data, oldest first.
    private Vector StockDates = new Vector();           // Holds the Dates associated with each row of data.
    
    String [] ColumnNames	= new String [] {"Date","Open","High","Low","Close","Volume","Adj. Close"};		// Ignore lines with these names in
    
    
    static final long serialVersionUID = 1301769209320717393L;
    /**
     * Constructor for the YahooFinanceInputSynapse object
     */
    public YahooFinanceInputSynapse() {
        super();
    }
    
    /**
     *  Gets the name of the symbol
     *
     * @return    The Symbol name
     */
    public String getSymbol() {
        return Symbol;
    }
    
    
    /**
     *  Gets year to start data retrieval from.
     *
     * @return    The year to start from
     * @deprecated Use getStartDate instead
     */
    public String getDateStart() {
        return DateStart;
    }
    
    
    /**
     *  Gets year to end data retrieval from
     *
     * @return    The year to end on
     * @deprecated Use getEndDate instead
     */
    public String getDateEnd() {
        return DateEnd;
    }
    
    
    /**
     *  Gets the period for data retrieval.
     *
     * @return    The month to end on
     */
    public String getPeriod() {
        return Period;
    }
    
    /**
     * Gets the dates associated with each row of data.
     */
    public Vector getStockDates() {
        return(StockDates);
    }
    
    /**
     * <P>Gets the stock data retrieived by this synapse.  Returns the data in a Vector array of length 5.</P>
     * <P>In column 0 Open data.</P>
     * <P>In column 1 High</P>
     * <P>In column 2 Low</P>
     * <P>In column 3 Close</P>
     * <P>In column 4 Volume</P>
     * <P>In column 5 Adj.Close</P>
     *
     */
    public Vector [] getStockData() {
        return(StockData);
    }
    
    /**
     *  Reads this YahooFinanceInputSynapse object into memory from the specified object stream.
     *
     * @param  in                          The object input stream that this object should be read from
     * @exception  IOException             The Input Output Exception
     * @exception  ClassNotFoundException  The class not found exception
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        super.readObjectBase(in);
        if (in.getClass().getName().indexOf("xstream") == -1) {
            Symbol = (String) in.readObject();
            DateStart = (String) in.readObject();
            DateEnd = (String) in.readObject();
            Period = (String) in.readObject();
        }
        if (!isBuffered() || (getInputVector().size() == 0)) {
            initInputStream();
        }
        try {
            startDate = date_formater.parse(DateStart);
            endDate = date_formater.parse(DateEnd);
        } catch (ParseException ex) {
            log.error("Invalid Date: start:"+DateStart+" end:"+DateEnd);
        }
    }
    
    
    /**
     *  Sets the name of the database jdbc driver.
     *
     * @param  newSymbol The new stock symbol to retrieve the data with.
     * @deprecated Use setEndDate instead
     */
    public void setSymbol(java.lang.String newSymbol) {
        // Only set if it actually has changed
        if ( !Symbol.equals(newSymbol)) {
            Symbol = newSymbol;
            this.resetInput();
            this.setTokens(null);
        }
    }
    
    
    /**
     *  Gets the the data from which data is retrieved.
     *
     * @param  newDataStart The data from which data is retrieved.
     * @deprecated Use setStartDate instead
     */
    public void setDateStart(String newDateStart) {
        // Only set if it actually has changed
        if (!DateStart.equals(newDateStart)) {
            DateStart = newDateStart;
            this.resetInput();
            this.setTokens(null);
        }
    }
    
    
    /**
     *  Gets the the data to which data is retrieved.
     *
     * @param  newDateEnd The date to which data is retrieved.
     */
    public void setDateEnd(String newDateEnd) {
        // Only set if it actually has changed
        if (!DateEnd.equals(newDateEnd)) {
            DateEnd = newDateEnd;
            this.resetInput();
            this.setTokens(null);
        }
    }
    
    /**
     *  Sets the period with which to retrieve data should be one of "Daily" or "Monthly" or "Yearly".
     *
     * @param  newPeriod The period with which data is retieved.
     */
    public void setPeriod(String newPeriod) {
        // Only set if it actually has changed
        if (!Period.equals(newPeriod)) {
            Period = newPeriod;
            this.resetInput();
            this.setTokens(null);
        }
    }
    
    /**
     *  Writes this YahooFInanceSynapseInput object to the ObjectOutputStream out.
     *
     * @param  out              The ObjectOutputSteeam that this object should be written to
     * @exception  IOException  The Input Output Exception if any
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        super.writeObjectBase(out);
        if (out.getClass().getName().indexOf("xstream") == -1) {
            out.writeObject(Symbol);
            out.writeObject(DateStart);
            out.writeObject(DateEnd);
            out.writeObject(Period);
        }
    }
    
    /**
     *  Connects to Yahoo FInancial Services and obtains the historical data for the specifed symbol and data range.
     */
    protected void initInputStream() throws JooneRuntimeException {
        
        String final_patterns = new String("");     // A buffer of final_patterns to present to the Tokenizer
        int start=0;
        String myUrl = new String("");     // Used to construct the URL to obtain the data
        
        String cperiod = "d";   // Default char period to 'd' for Daily
        
        for ( int i=0;i<frequency.length;i++) {
            if ( Period.equals(frequency[i]) )
                cperiod = freq_conv[i];  // Get the period character d,w or m
        }
        
        StockData = null;
        StockDates = null;
        
        if ((Symbol != null) && (!Symbol.equals(new String("")))) {
            try {
                CalendarStart = Calendar.getInstance();
                CalendarEnd = Calendar.getInstance();
                CalendarStart.setTime( date_formater.parse(DateStart) );
                CalendarEnd.setTime( date_formater.parse(DateEnd) );
            } catch ( ParseException ex ) {	// Could not parse one of the date strings.
                log.error( "YahooFinanceInputSynapse could not parse date string. Message is : = "+ex.getMessage());	// LOG4J
                CalendarStart = null;
                CalendarEnd = null;
                //throw new JooneRuntimeException(ex.getMessage());
                if ( getMonitor() != null )
                    new NetErrorManager(getMonitor(),"YahooFinanceInputSynapse could not parse date string. "+ex.getMessage());
                return;
            }
            int addedData = 1;
            if (CalendarStart != null) {
                if (CalendarEnd != null) {
                    if (Period != null) {
                        log.info("Contacting Yahoo Finance Network.");
                        try {
                            while ((addedData > 0) && (addedData <= 200)) {
                                myUrl = new String("http://table.finance.yahoo.com/table.csv?a="+CalendarStart.get(Calendar.MONTH)+"&b="+CalendarStart.get(Calendar.DAY_OF_MONTH)+"&c="+CalendarStart.get(Calendar.YEAR)+"&d="+CalendarEnd.get(Calendar.MONTH)+"&e="+CalendarEnd.get(Calendar.DAY_OF_MONTH)+"&f="+CalendarEnd.get(Calendar.YEAR)+"&s="+Symbol+"&g="+cperiod+"&z=200&y="+start+"&ignore=.csv");
                                addedData = addURLToMemory(new URL(myUrl));
                                start += 200;
                            }
                        } catch ( IOException ex ) {
                            log.error( "Error obtaining data from YahooFInance. Error message is "+ex.getMessage());
                            //throw new JooneRuntimeException(ex.getMessage());
                            if ( getMonitor() != null )
                                new NetErrorManager(getMonitor(),"Error obtaining data from YahooFInance."+ex.getMessage());
                            return;
                        }
                        log.info("Loaded Yahoo Fianance data ok.");
                        // Compose final pattern to give to network, also give in reverse order so oldest date first
                        for ( int i=StockData[0].size()-1;i>=0;i--) {
                            
                            // OPEN
                            final_patterns += ((Double)StockData[0].elementAt(i)).toString();
                            
                            // HIGH
                            final_patterns += ";"+((Double)StockData[1].elementAt(i)).toString();
                            
                            // LOW
                            final_patterns += ";"+((Double)StockData[2].elementAt(i)).toString();
                            
                            // CLOSE
                            final_patterns += ";"+((Double)StockData[3].elementAt(i)).toString();
                            
                            // VOLUME
                            final_patterns += ";"+((Double)StockData[4].elementAt(i)).toString();
                            
                            // ADJ. CLOSE
                            final_patterns += ";"+((Double)StockData[5].elementAt(i)).toString();
                            final_patterns += '\n';
                        }
                        try{
                            StreamInputTokenizer sit;
                            if (getMaxBufSize() > 0)
                                sit = new StreamInputTokenizer(new StringReader(final_patterns), getMaxBufSize());
                            else
                                sit = new StreamInputTokenizer(new StringReader(final_patterns));
                            super.setTokens(sit);
                        } catch (IOException ex) {
                            // Use Log4j
                            log.error( "IOException thrown while initializing the YahooFinanceInputStream. Message is : " + ex.getMessage());	// LOG4J
                            if ( getMonitor() != null )
                                new NetErrorManager(getMonitor(),"Error while trying to parse Yahoo Finance data. Message is : "+ex.getMessage());
                        }
                    } // End of if period is null
                } // End if not Year Start
            } // End if not Day Start
        } // end if no Symbol
    } // End method initInputStream
    
    /**
     * Adds data from a URL in CSV format to Memory
     * @return the number of lines added to the buffer
     */
    private int addURLToMemory(URL Data) throws IOException {
        LineNumberReader url_reader = null;
        String Line = new String("");
        boolean IgnoreHeader = true;
        boolean added_data = true;
        boolean Docontinue = true;
        
        log.debug(Data.getProtocol()+"://"+Data.getHost()+Data.getFile());
        int initSize = 0;
        // Check that the URL is not null
        if ( Data != null) {
            url_reader = new LineNumberReader(new InputStreamReader(new BufferedInputStream(Data.openStream())));
            // Read the first line
            Line = url_reader.readLine();
            if ( StockDates == null)StockDates = new Vector();
            if ( (StockData == null)||(StockData.length < 6))StockData = new Vector[6];
            // Init Pattern list
            if ( StockData[0] == null )StockData[0] = new Vector(); // Open
            if ( StockData[1] == null )StockData[1] = new Vector(); // High
            if ( StockData[2] == null )StockData[2] = new Vector(); // Low
            if ( StockData[3] == null )StockData[3] = new Vector(); // Close
            if ( StockData[4] == null )StockData[4] = new Vector(); // Volume
            if ( StockData[5] == null )StockData[5] = new Vector(); // Adj.Close
            initSize = StockData[0].size();
            while ( (Line != null) && (!Line.equals("")) && (Docontinue == true)) {
                if ( IgnoreHeader == true) // Just throw header line away
                {
                    if ( Line.indexOf(ColumnNames[0]) < 0) {
                        if ( Line.indexOf(",")!=-1 ) // Check for bad data
                        {
                            StringTokenizer tokens = new StringTokenizer(Line, ",\n");
                            if (tokens.countTokens() >= 7)  {
                                added_data = true;
                                String _data = tokens.nextToken();
                                StockDates.addElement(_data); // Get Stock Date for row
                                // log.debug(_data);
                                StockData[0].add(Double.valueOf(tokens.nextToken()));	// Add OPEN to pattern vector
                                StockData[1].add(Double.valueOf(tokens.nextToken()));	// Add HIGH to pattern vector
                                StockData[2].add(Double.valueOf(tokens.nextToken()));	// Add LOW to pattern vector
                                StockData[3].add(Double.valueOf(tokens.nextToken()));	// Add CLOSE to pattern vector
                                StockData[4].add(Double.valueOf(tokens.nextToken()));	// Add VOLUME to pattern vector
                                StockData[5].add(Double.valueOf(tokens.nextToken()));	// Add Adj.Close to pattern vector
                            }
                        } // End check for bad data
                        else
                            Docontinue = false;
                    }
                }
                Line = url_reader.readLine();
            }
        }
        int addedData = StockData[0].size() - initSize;
        //if ( url_reader != null)
        //    if ( url_reader.getLineNumber() <= 2) added_data=false; // Consider the data not added if only one line of data
        return addedData;
    } // End Add URL To Memory
    
    
    /**
     * Check that there are no errors or problems with the properties of this YahooFinanceInputSynapse.
     * @return The TreeSet of errors / problems if any.
     */
    public TreeSet check() {
        TreeSet checks = super.check();
        
        if ( (getDateStart() != null) && (!getDateStart().equals("")) ) {
            try {
                date_formater.parse(DateStart);
            } catch ( ParseException ex ) {
                checks.add(new NetCheck(NetCheck.FATAL, "Format for Start Date is invalid." , this));
            }
        } else
            checks.add(new NetCheck(NetCheck.FATAL, "Start Date should be populated." , this));
        
        if ( (getDateEnd() != null) && (!getDateEnd().equals("")) ) {
            try {
                date_formater.parse(DateEnd);
            } catch ( ParseException ex ) {
                checks.add(new NetCheck(NetCheck.FATAL, "Format for End Date is invalid." , this));
            }
        } else
            checks.add(new NetCheck(NetCheck.FATAL, "End Date should be populated." , this));
        
        if ( getPeriod()!=null) {
            if ( !getPeriod().equals("Daily") && !getPeriod().equals("Weekly") && !getPeriod().equals("Monthly") ) {
                checks.add(new NetCheck(NetCheck.FATAL, "Period should be one of 'Daily' , 'Weekly' , 'Monthly'." , this));
            }
        } else
            checks.add(new NetCheck(NetCheck.FATAL, "Period should be one of 'Daily' , 'Weekly' , 'Monthly'." , this));
        
        if ( (getSymbol()== null)||(getSymbol().equals("")))
            checks.add(new NetCheck(NetCheck.FATAL, "Symbol should be populated." , this));
        
        
        return checks;
    }
    
    public Date getStartDate() {
        return startDate;
    }
    
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
        DateStart = new String(date_formater.format(startDate));
        this.resetInput();
        this.setTokens(null);
    }
    
    public Date getEndDate() {
        return endDate;
    }
    
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
        DateEnd = new String(date_formater.format(endDate));
        this.resetInput();
        this.setTokens(null);
    }
}

