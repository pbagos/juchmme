package org.joone.io;

import java.io.*;
import java.util.TreeSet;

import org.joone.log.*;
import org.joone.engine.*;
import org.joone.net.NetCheck;

public class FileOutputSynapse extends StreamOutputSynapse {
    /**
     * Logger
     * */
    private static final ILogger log = LoggerFactory.getLogger(FileOutputSynapse.class);
    private String FileName = "";
    private boolean append = false;
    // The default printer
    protected transient PrintWriter printer = null;
    
    private static final long serialVersionUID = 3194671306693862830L;
    
    public FileOutputSynapse() {
        super();
    }
    
    /**
     *
     * Writes to the printer object.
     */
    public synchronized void write(Pattern pattern) {
        if ((printer == null) || (pattern.getCount() == 1))
            setFileName(FileName);
        
        if (pattern.getCount() == -1) {
            flush();
        }
        else {
            double[] array = pattern.getArray();
            for (int i=0; i < array.length; ++i) {
                printer.print(array[i]);
                if (i < (array.length - 1))
                    printer.print(getSeparator());
            }
            printer.println();
            //printer.flush();  // Flush the output after every line, avoid building any large buffers
        } // End else
    }
    
    /**
     * Inserire qui la descrizione del metodo.
     * Data di creazione: (23/04/00 0.58.30)
     * @return java.lang.String
     */
    public java.lang.String getFileName() {
        return FileName;
    }
    
    public void setFileName(String fn) {
        FileName = fn;
        try {
            if (printer != null)
                printer.close();
            printer = new PrintWriter(new FileOutputStream(fn, isAppend()), true);
        } catch (IOException ioe) {
            String error = "IOException in "+getName()+". Message is : ";
            log.error(error + ioe.getMessage());
            if ( getMonitor() != null)
                new NetErrorManager(getMonitor(),error+ioe.getMessage());
        }
    }
    
    public void flush() {
        printer.flush();
        printer.close();
        printer=null;
    }
    
    public TreeSet check() {
        TreeSet checks = super.check();
        
        if (FileName == null || FileName.trim().equals("")) {
            checks.add(new NetCheck(NetCheck.FATAL, "File Name not set." , this));
        }
        
        return checks;
    }
    
    /** Getter for property append.
     * @return Value of property append.
     *
     */
    public boolean isAppend() {
        return append;
    }
    
    /** Setter for property append.
     * @param     append      if <code>true</code>, then bytes will be written
     *                   to the end of the file rather than the beginning
     *
     */
    public void setAppend(boolean append) {
        this.append = append;
    }
    
}