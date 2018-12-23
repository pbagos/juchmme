package org.joone.io;

import java.io.*;
import java.util.TreeSet;

import org.joone.log.*;

import org.apache.poi.poifs.filesystem.*;
import org.apache.poi.hssf.usermodel.*;
import org.joone.net.NetCheck;
import org.joone.exception.JooneRuntimeException;
import org.joone.engine.NetErrorManager;

/** This class allows data to be presented to the network from an Excel XLS
 * formatted file.  The XLS file name must be specified and a worksheet name is optional.
 */
public class XLSInputSynapse extends StreamInputSynapse {
    /**
     * Logger
     * */
    private static final ILogger log = LoggerFactory.getLogger(XLSInputSynapse.class);
        
    /** The name of the XLS worksheet from the XLS file to be used as input data. */
    private String  i_sheet_name = "";
    /** The internal HSSF workbook model to read XLS data into for presentation to the
     * network.
     */
    private transient HSSFSheet i_sheet;
    
    /** The work sheet index number. */
    private int i_sheet_index = 0;
    
    /** The default sheet index. */
    /** The file input stream used to read the XLS file. */
    private transient FileInputStream i_stream;
    private transient POIFSFileSystem i_fs;
    private transient HSSFWorkbook i_workbook;
    /** Flag to check the file name has been set. */
    private boolean file_chk = false; // used to confirm if filname has been chosen yet
    
    /** The name of the XLS file to read data from. */
    private String fileName = "";
    private transient File inputFile;
    
    /** The serial ID of this object. */
    private static final long serialVersionUID = 8625369117101456178L;
    
    /** The default constructor for this XLSInputSynapse. */
    public XLSInputSynapse() {
        super();
    }
    
    /** Gets the XLS file name used as input to the network.
     * @return The XLS input file name.
     * @deprecated use getInputFile instead
     */
    public java.lang.String getFileName() {
        return fileName;
    }
    
    
    /** Sets the XLS file name that should be used to obtain input data from.
     * @param newFileName The XLS file name that should be used to obtain input data from.
     * @deprecated use setInputFile instead
     */
    public void setFileName(java.lang.String newFileName) {
        if (!fileName.equals(newFileName)) {
            fileName = newFileName;
            file_chk = true;
            this.resetInput();
            this.setTokens(null);
            //initInputStream();
        }
    }
    
    
    /** Reads data from the XLS file into this synapse. */
    protected void initInputStream() throws JooneRuntimeException {
        // check for sheet called j_input
        // get sheet number of j_output
        // get first available sheet
        if (new File(fileName).exists()) {
            if ((i_sheet = getSheet(i_sheet_name)) != null)
                try {
                    super.setTokens(new XLSInputTokenizer(i_sheet));
                } catch (IOException ioe) {
                    log.error("Error creating XLSInputTokenizer. Message is : " + ioe.getMessage());
                    if ( getMonitor() != null )
                        new NetErrorManager(getMonitor(),"Error creating XLSInputTokenizer. Message is : "+ioe.getMessage());
                }
        } else {
            String err = "Excel XLS File '"+fileName+"' does not exist.";
            log.error(err);
            if ( getMonitor() != null )
                new NetErrorManager(getMonitor(),"Excel XLS File '"+fileName+"' does not exist.");
        }
    }
    
    /** Gets the HSSF sheet from the XLS file using the specified sheet name.
     * @return The HSSFSheet
     */
    protected HSSFSheet getSheet(String sheetName) {
        HSSFSheet r_sheet = null;
        try {
            i_stream = new FileInputStream(fileName);
            i_fs = new POIFSFileSystem(i_stream);
            i_workbook = new HSSFWorkbook(i_fs);
            i_stream.close(); // Handle on workbook is now attained
            i_sheet_index = i_workbook.getSheetIndex(sheetName);
            //o_sheet_index = i_workbook.getSheetIndex("j_output");
            
            if (i_sheet_index > -1) {
                r_sheet = i_workbook.getSheetAt(i_sheet_index);
            } else {
              /*  if (o_sheet_index == 0) {
                    r_sheet = i_workbook.getSheetAt(o_sheet_index + 1);
                } else { */
                r_sheet = i_workbook.getSheetAt(0);
            }
            
            return r_sheet;
        } catch (IOException io_err) {
            log.error("Could not open worksheet '"+sheetName+"' from XLS file. Message is : " + io_err.getMessage());
            if ( getMonitor() != null )
                new NetErrorManager(getMonitor(),"Could not open worksheet '"+sheetName+"' from XLS file. Message is : "+io_err.getMessage());
        }
        return null;
    }
    
    /** Sets the name of the sheet within the XLS file to extract input data from.
     * @param sheetName The name of the sheet within the XLS file to extract input data from.
     */
    public void setSheetName(String sheetName) {
        if (!i_sheet_name.equals(sheetName)) {
            i_sheet_name = sheetName;
            this.resetInput();
            this.setTokens(null);
        /*    if (file_chk) {
                if (i_workbook == null)
                    // This is needed only to reinitialize all the transient variables
                    getSheet(i_sheet_name);
                if (i_workbook.getSheetIndex(sheetName) != -1) {
                    initInputStream();
                } else {
                    log.warn("Invalid input sheet name please choose another!");
                }
            } else {
                log.warn("Please choose valid file first");
            } */
        }
    }
    
    /** Gets a list of available sheets from the XLS file.
     * @return The list of available sheets from the XLS file or null if the file name has not
     * been set.
     */
    public String[] getAvailableSheetList() {
        int sheetCount = i_workbook.getNumberOfSheets();
        String[] availableSheetList = null;
        for (int i = 0; i > sheetCount; i++) {
            availableSheetList[i] = i_workbook.getSheetName(i);
        }
        return availableSheetList;
    }
    
    /** Gets the name of the sheet within the XLS file to extract data from for input to
     * the network.
     * @return The name of the sheet within the XLS file to extract data from for input to
     * the network.
     */
    public String getSheetName() {
        return i_sheet_name;
    }
    
    /** Reads this XLSInputSynpase object from the specified object stream.
     * @param in The object stream to read this XLSInputSynapse object from.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        super.readObjectBase(in);
        if (in.getClass().getName().indexOf("xstream") == -1) {
            fileName = (String) in.readObject();
        }
        if (!isBuffered() || (getInputVector().size() == 0))
            setFileName(fileName);
        if ((fileName != null) && (fileName.length() > 0))
            inputFile = new File(fileName);
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException {
        super.writeObjectBase(out);
        if (out.getClass().getName().indexOf("xstream") == -1) {
            out.writeObject(fileName);
        }
    }
    
    /** Returns a TreeSet of problems or errors with the setup of this XLSInputSynapse
     * object.
     * @return A TreeSet of problems or errors with the setup of this XLSInputSynapse
     * object.
     */
    public TreeSet check() {
        TreeSet checks = super.check();
        
        if (fileName == null || fileName.trim().equals("")) {
            checks.add(new NetCheck(NetCheck.FATAL, "File Name not set." , this));
        } else {
            if (!getInputFile().exists()) {
                NetCheck error = new NetCheck(NetCheck.WARNING, "Input File doesn't exist." , this);
                if (getInputPatterns().isEmpty())
                    error.setSeverity(NetCheck.FATAL);
                checks.add(error);
            }
        }
        
        return checks;
    }
    
    public File getInputFile() {
        return inputFile;
    }
    
    public void setInputFile(File inputFile) {
        if (inputFile != null) {
            if (!fileName.equals(inputFile.getAbsolutePath())) {
                this.inputFile = inputFile;
                fileName = inputFile.getAbsolutePath();
                file_chk = true;
                this.resetInput();
                super.setTokens(null);
            }
        } else {
            this.inputFile = inputFile;
            fileName = "";
            file_chk = true;
            this.resetInput();
            super.setTokens(null);
        }
    }
    
}