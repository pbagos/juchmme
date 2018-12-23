package org.joone.io;

import java.io.*;
import java.util.TreeSet;

import org.joone.log.*;
import org.joone.net.NetCheck;
import org.joone.exception.JooneRuntimeException;
import org.joone.engine.NetErrorManager;

/** Allows data to be presented to the network from a file.  The file must contain
 * semi-colon seperated values e.g '2;5;7;2.1'.
 */
public class FileInputSynapse extends StreamInputSynapse {
    /** The logger used to log errors and warnings. */
    private static final ILogger log = LoggerFactory.getLogger(FileInputSynapse.class);
    private static final long serialVersionUID = 7456627292136514416L;
    
    /** The name of the file to extract information from. */
    private String fileName = "";
    private transient File inputFile;
    
    public FileInputSynapse() {
        super();
    }
    
    /** Gets the file name that this synapse uses to extract the input data from.
     * @return The file name that this synapse uses to extract the input data from.
     * @deprecated use getInputFile instead
     */
    public java.lang.String getFileName() {
        return fileName;
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        super.readObjectBase(in);
        if (in.getClass().getName().indexOf("xstream") == -1) {
            fileName = (String) in.readObject();
        }
        if ((fileName != null) && (fileName.length() > 0))
            inputFile = new File(fileName);
    }
    
    /** Sets the file name that this synapse should extract data from.
     * @param newFileName The file name that this synapse should extract data from.
     * @deprecated use setInputFile instead
     */
    public void setFileName(java.lang.String newFileName) {
        if (!fileName.equals(newFileName)) {
            fileName = newFileName;
            this.resetInput();
            super.setTokens(null);
            //initInputStream();
        }
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException {
        super.writeObjectBase(out);
        if (out.getClass().getName().indexOf("xstream") == -1) {
            out.writeObject(fileName);
        }
    }
    
    protected void initInputStream() throws JooneRuntimeException {
        if ((fileName != null) && (!fileName.equals(new String("")))) {
            try {
                inputFile = new File(fileName);
                FileInputStream fis = new FileInputStream(inputFile);
                StreamInputTokenizer sit;
                if (getMaxBufSize() > 0)
                    sit = new StreamInputTokenizer(new InputStreamReader(fis), getMaxBufSize());
                else
                    sit = new StreamInputTokenizer(new InputStreamReader(fis));
                super.setTokens(sit);
            } catch (IOException ioe) {
                String error = "IOException in "+getName()+". Message is : ";
                log.warn(error + ioe.getMessage());
                if ( getMonitor() != null)
                    new NetErrorManager(getMonitor(),error+ioe.getMessage());
            }
        }
    }
    
    /** Returns a TreeSet of errors or problems regarding the setup of this synapse.
     * @return A TreeSet of errors or problems regarding the setup of this synapse.
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
                this.resetInput();
                super.setTokens(null);
            }
        } else {
            this.inputFile = inputFile;
            fileName = "";
            this.resetInput();
            super.setTokens(null);
        }
    }
}