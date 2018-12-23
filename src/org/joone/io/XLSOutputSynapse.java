package org.joone.io;

import java.io.*;
import java.util.TreeSet;

import org.joone.log.*;
import org.joone.engine.*;
import org.joone.net.NetCheck;

import org.apache.poi.poifs.filesystem.*;
import org.apache.poi.hssf.usermodel.*;


/** This class allows data to be read from an Excel XLS formatted file.  The class
 * requires the specification of a file name and a worksheet name is optional.
 */
public class XLSOutputSynapse extends StreamOutputSynapse {
    /**
     * Logger
     * */
    private static final ILogger log = LoggerFactory.getLogger(XLSOutputSynapse.class);
    
    /** The serial version ID for this object. */
    static final long serialVersionUID = -9167076940131905606L;
    
    /** The name of the XLS File to write network output data to. */
    private String FileName = "";
    /** The stream to be used for writing. */
    private transient FileOutputStream o_stream;
    /** The input stream of an XLS file that already exists. */
    private transient FileInputStream i_stream;
    /** The Internal HSSF sheet to output data to. */
    private transient HSSFSheet o_sheet;
    
    /** The worksheet index number. */
    private int o_sheet_index = -1 ;
    
    /** Internal row count. */
    private int  row_no = 0;
    /** Starting position when the first pattern being written. */
    private int  startCol;
    private int  startRow;

    /** The working copy of the HSSF Workbook used to output data. */
    private transient HSSFWorkbook workbook;
    /** The name of the XLS sheet within the XLS File that will be used to write data
     * to.
     */
    private String o_sheet_name = new String("j_output");
    
    /** The default constructor for this XLSOutputSynapse. */
    public XLSOutputSynapse() {
        super();
        /* Default value. */
        startCol = 0;
        startRow = 0;
    }
        
    /** Write any remaining data to the XLS file. */
    public void flush() {
        try {
            workbook.write(o_stream);
        } catch (IOException ioe) {
            String error = "IOException in "+getName()+". Message is : ";
            log.warn(error + ioe);
            if ( getMonitor() != null )
                new NetErrorManager(getMonitor(),error + ioe.getMessage());
        }
    }
    
    /** Writes a Pattern to the XLS file.
     * @param pattern The Pattern to write to the XLS file.
     */
    public synchronized void write(Pattern pattern) {
        if ((workbook == null) || (pattern.getCount() == 1)) {
            try {
                this.initOutputStream(); // get handle on workbook
            } catch (IOException ioe) {
                String error = "IOException in "+getName()+". Message is : ";
                log.warn(error + ioe);
                if ( getMonitor() != null )
                    new NetErrorManager(getMonitor(),error + ioe.getMessage());
            }
        }
        if (pattern.getCount() == -1) {
            //open outstream
            try {
                o_stream = new FileOutputStream(FileName);
                workbook.write(o_stream);
                o_stream.close(); // close stream to be clean
                row_no = startRow;  // reset count
                workbook = null; //reset workbook to force reread of file
            } catch (IOException ioe) {
                String error = "IOException in "+getName()+". Message is : ";
                log.warn(error + ioe);
                if ( getMonitor() != null )
                    new NetErrorManager(getMonitor(),error + ioe.getMessage());
            }
        } else {
            HSSFRow row;
            HSSFCell cell;
            double[] array = pattern.getArray();
            if (o_sheet.getRow(row_no) == null) {
                row = o_sheet.createRow((short) row_no);
                
            } else {
                row = o_sheet.getRow(row_no);
                
            }
            for (int i = 0; i < array.length; ++i) {
                if (!(row.getCell((short) (i+startCol)) == null)) {
                    row.removeCell(row.getCell((short) (i+startCol))); //remove original cell
//                    log.debug("Removing " + row_no + "," + (i+startCol));
                }
                
                cell = row.createCell((short) (i+startCol), 0); // type 0 numeric
                
                cell.setCellValue((double) array[i]);
            }
            ++row_no;
        }
    }
    
    /** Returns the XLS file name used by this synapse.
     * @return The XLS file name used by this synapse to output data to.
     */
    public java.lang.String getFileName() {
        return FileName;
    }
    
    /** Sets the XLS file name that this synapse should output data to.
     * @param fn The XLS file name that this synapse should output data to.
     */
    public void setFileName(String fn) {
        FileName = fn;
    }
    
    /** Initialises the data stream. */
    private void initOutputStream()
    throws IOException {
        if (new File(FileName).exists()) {
            i_stream = new FileInputStream(FileName);
            POIFSFileSystem i_fs = new POIFSFileSystem(i_stream);
            workbook = new HSSFWorkbook(i_fs);
            i_stream.close(); // handle on workbook has been attained
            
        } else {
            o_stream = new FileOutputStream(FileName); // generate file for later use
            workbook = new HSSFWorkbook();
            workbook.write(o_stream); // create valid workbook
            o_stream.close(); // handle on workbook has been attained
            
        }
        o_sheet_index = workbook.getSheetIndex(o_sheet_name); //find valid sheet
        if (o_sheet_index != -1) {
            o_sheet = workbook.getSheetAt(o_sheet_index);
        } else {
            o_sheet = workbook.createSheet(o_sheet_name);
        }
        row_no = startRow;  // reset count
        
    }
    
    /** Sets the sheet name within the XLS file that this synapse should write data to.
     * @param sheetName The sheet name within the XLS file that this synapse should write data to.
     */
    public void setSheetName(String sheetName) {
        o_sheet_name = sheetName;
    }
    
    /** Obtains a list of available sheet names from the XLS file.
     * @return An array of sheet names found with in the XLS file or null if the file has not
     * been read yet.
     */
    public String[] getAvailableSheetList() {
        int sheetCount = workbook.getNumberOfSheets();
        String[] availableSheetList = null;
        for (int i = 0; i > sheetCount; i++) {
            availableSheetList[i] = workbook.getSheetName(i);
        }
        return availableSheetList;
    }
    
    /** Gets the name of sheet within the XLS file that data should be written to.
     * @return The name of sheet within the XLS file that data should be written to.
     */
    public String getSheetName() {
        return o_sheet_name;
    }
    /** Checks and returns any problems found with the settings of this synapse.
     * @return A TreeSet of problems or errors found with this synapse.
     */
    public TreeSet check() {
        TreeSet checks = super.check();
        
        if (FileName == null || FileName.trim().equals("")) {
            checks.add(new NetCheck(NetCheck.FATAL, "File Name not set." , this));
        }
        
        return checks;
    }

    /** Gets the starting row (0 based) of the XLS sheet.
     * @return Starting row (0 based) of the XLS sheet.
     */
    public int getStartRow(int startRow) {
        return startRow;
    }

    /** Gets the starting col (0 based) of the XLS sheet.
     * @return Starting col (0 based) of the XLS sheet.
     */
    public int getStartCol(int startCol) {
        return startCol;
    }

    /** Set the starting row (0 based) of the XLS sheet.
     */
    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    /** Set the starting col (0 based) of the XLS sheet.
     */
    public void setStartCol(int startCol) {
        this.startCol = startCol;
    }    
}
