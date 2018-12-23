package org.joone.io;

import java.io.*;
import java.net.*;
import org.joone.exception.JooneRuntimeException;
import org.joone.log.*;
import org.joone.engine.NetErrorManager;

/** Allows data extraction from the internet or a file specified by a Universal
 * Resource Locator or URL.
 */
public class URLInputSynapse extends StreamInputSynapse {
    /** The logger used to log warning or errors. */
    private static final ILogger log = LoggerFactory.getLogger(URLInputSynapse.class);
    /** The string of the URL used to extract input data. */
    private String URL = "http://";
    /** The actual URL used to extract input data. */
    private URL cURL;
    
    private static final long serialVersionUID = -1871585397469526608L;
    
    /** The default constructor for this class. */
    public URLInputSynapse() {
        super();
    }
    /** Gets the URL used to extract input data from.
     * @return The URL used to extract input data from.
     */
    public String getURL() {
        return URL;
    }
    /** Reads this URLInputSynapse object from the specified object stream. */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        super.readObjectBase(in);
        if (in.getClass().getName().indexOf("xstream") == -1) {
            URL = (String) in.readObject();
        }
        if (!isBuffered() || (getInputVector().size() == 0))
            setURL(URL);
    }
    /** Sets the URL to extract input data from.
     * @param newURL The new URL used to extract input data from.
     */
    public void setURL(java.lang.String newURL) {
        if (!URL.equals(newURL)) {
            this.resetInput();
            this.setTokens(null);
        }
        //initInputStream();
    }
    /** Writes this URLInputSynapse object into the specified object stream. */
    private void writeObject(ObjectOutputStream out) throws IOException {
        super.writeObjectBase(out);
        if (out.getClass().getName().indexOf("xstream") == -1) {
            out.writeObject(URL);
        }
    }
    
    /** Reads the data from the URL specified in this URLInputSynapse. */
    protected void initInputStream() throws JooneRuntimeException {
        if ((URL != null) && (URL != "")) {
            try {
                cURL = new URL(URL);
                InputStream is = cURL.openStream();
                StreamInputTokenizer sit;
                if (getMaxBufSize() > 0)
                    sit = new StreamInputTokenizer(new InputStreamReader(is), getMaxBufSize());
                else
                    sit = new StreamInputTokenizer(new InputStreamReader(is));
                super.setTokens(sit);
            } catch (IOException ioe) {
                log.warn("Could not extract data from the URL '"+URL+"' Message is : " + ioe.getMessage());
                if ( getMonitor() != null )
                    new NetErrorManager(getMonitor(),"Could not extract data from the URL '"+URL+"' Message is : "+ioe.getMessage());
            }
        }
    }
    
}