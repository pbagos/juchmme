/*
 * ImageInputSynapse.java
 *
 * Created on 16 October 2005, 08:50
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.joone.io;

import java.util.Vector;
import java.awt.Image;
import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.TreeSet;

import org.joone.log.*;
import org.joone.net.NetCheck;

/**
 * This synapse collects data from Image files or Image objects and feeds the data from the Images into the Neural network.
 * GIF, JPG and PNG image file formats can be read.  The synapse operates in two modes, colour and grey scale.
 * <P>
 * <B>Colour Mode</B>
 * In colour mode ImageInputSynapse produces seperate RGB input values in the range 0 to 1 from the
 * image.  So using an image of width 10 and height 10 there will be 10x10x3 inputs in the range 0 to 1.
 * The individual colour components are calculated by obtaining the RGB values from the image.  These value
 * are initially in an ARGB format.  Transparency is removed and the RGB value extracted and normalised
 * between 0 and 1.
 * </P>
 * <P>
 * <B>Non Colour Mode / Grey Scale Mode </B>
 * In this mode the synapse treats each input value as a grey scale value for each pixel.  In this mode
 * only Width*Height values are required.  To produce the final image the Red, Green and Blue components
 * are the set to this same value.
 * The grey scale component is calculated by obtaining the RGB values from the image.  These value
 * are initially in an ARGB format.  Transparency is removed and the RGB value extracted, averaged and normalised
 * to produce one grey scale value between 0 and 1.
 * </P>
 * @author Julien Norman
 */
public class ImageInputSynapse extends StreamInputSynapse {
    
    /** The object used when logging debug,errors,warnings and info. */
    private static final ILogger log = LoggerFactory.getLogger(ImageInputSynapse.class);
    
    static final long serialVersionUID = -1396287146739057882L;
    
    private File imageDirectory = new File(System.getProperty("user.dir"));
    
    private String theFileFilter = new String(".*[jJ][pP][gG]");
    
    private Vector FileNameList = new Vector();
    
    private Image [] MultiImages = null;
    
    private int DesiredWidth = 10;
    
    private int DesiredHeight = 10;
    
    /**
     *  If in Colour Mode RGB values are input seperately.
     *  Otherwise RGB values are averaged and input as grey scale.
     */
    private boolean ColourMode = true;
    
    /** Creates a new instance of ImageInputSynapse */
    public ImageInputSynapse() {
        this.calculateNewACS();
    }
    
    /* Specific ImageInputSynapse Methods */
    
    public void setFileFilter(String newFileFilter) {
        theFileFilter = newFileFilter;
    }
    
    public String getFileFilter() {
        return(theFileFilter);
    }
    
    public void setImageInput(Image [] theImages) {
        MultiImages = theImages;
    }
    
    protected void initInputStream() throws org.joone.exception.JooneRuntimeException {
        
        ImageInputTokenizer toks = null;
        
        try {
            if ( (MultiImages != null) && (MultiImages.length > 0)) {
                toks = new ImageInputTokenizer(getDesiredWidth(), getDesiredHeight(), MultiImages,ColourMode );
                super.setTokens(toks);
            } else {
                FileNameList = new Vector();
                
                String [] thelist = imageDirectory.list(
                        new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return name.matches(theFileFilter);
                    }
                });
                
                for (int i=0;i<thelist.length;i++) {
                    URL theurl = new File(imageDirectory.getPath() +
                            System.getProperty("file.separator") +
                            thelist[i]).toURL();
                    FileNameList.add(theurl);
                }

                toks = new ImageInputTokenizer(getDesiredWidth(), getDesiredHeight(), FileNameList,ColourMode );
                super.setTokens(toks);
            }
        } catch(Exception ex) {
            // Report the error.
            log.error("Error initialising the input stream : "+ex.toString());
        }
    }
    
    public int getDesiredWidth() {
        return DesiredWidth;
    }
    
    public void setDesiredWidth(int DesiredWidth) {
        this.DesiredWidth = DesiredWidth;
        this.calculateNewACS();
    }
    
    public int getDesiredHeight() {
        return DesiredHeight;
    }
    
    public void setDesiredHeight(int DesiredHeight) {
        this.DesiredHeight = DesiredHeight;
        this.calculateNewACS();
    }
    
    public File getImageDirectory() {
        return imageDirectory;
    }
    
    public void setImageDirectory(File imgDir) {
        File tmpDir = imgDir;
        if ((tmpDir != null) && (!tmpDir.isDirectory())) {
            String name = tmpDir.getName();
            int pos = tmpDir.getPath().indexOf(name);
            if (pos > -1) {
                String dir = tmpDir.getPath().substring(0, pos);
                tmpDir = new File(dir);
            }
        }
        this.imageDirectory = tmpDir;
    }
    
    public boolean getColourMode() {
        return ColourMode;
    }
    
    public void setColourMode(boolean ColourMode) {
        this.ColourMode = ColourMode;
        this.calculateNewACS();
    }
    
    /** Checks and returns any problems found with the settings of this synapse.
     * @return A TreeSet of problems or errors found with this synapse.
     */
    public TreeSet check() {
        TreeSet checks = super.check();
        
        if (theFileFilter.equals("")) {
            checks.add(new NetCheck(NetCheck.FATAL, "No File Filter set e.g. '.*[jJ][pP][gG]'." , this));
        }
        if ( imageDirectory == null ) {
            checks.add(new NetCheck(NetCheck.FATAL, "No image input directory set." , this));
        }
        
        return checks;
    }
    
    /** Calculates the new value for AdvancedColumnSelector
     */
    protected void calculateNewACS() {
        int tokens = this.DesiredWidth*this.DesiredHeight;
        if ( ColourMode )
            tokens = tokens * 3;
        this.setAdvancedColumnSelector("1-"+tokens);
    }
}
