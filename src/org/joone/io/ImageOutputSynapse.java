/*
 * ImageOutputSynapse.java
 *
 * Created on 23 October 2005, 19:13
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.joone.io;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.TreeSet;

import org.joone.log.*;
import org.joone.net.NetCheck;

/**
 * This class collects the output from the connected layer and places it into an image file.
 * Images can be produced in GIF, JPG and PNG formats.
 * <P>
 * <B>Colour Mode</B>
 * In colour mode ImageOutputSynapse expects seperate RGB input values in the range 0 to 1 from the
 * connected layer.  So to produce an image of width 10 and height 10 there must be 10x10x3 inputs
 * from the previous layer/s.  The red, green and blue components in the final image are set using the
 * individual component values from the input.
 * </P>
 * <P>
 * <B>Non Colour Mode / Grey Scale Mode </B>
 * In this mode the synapse treats each input value as a grey scale value for each pixel.  In this mode
 * only Width*Height values are required.  To produce the final image the Red, Green and Blue components
 * are the set to this same value.
 * </P>
 * @author Julien Norman
 */
public class ImageOutputSynapse extends StreamOutputSynapse {
    
    /** The object used when logging debug,errors,warnings and info. */
    private static final ILogger log = LoggerFactory.getLogger(ImageOutputSynapse.class);
    
    static final long serialVersionUID = 1287752494948170142L;
    
    public final int JPG = 1;
    public final int GIF = 2;
    public final int PNG = 3;
    
    private String OutputDirectory = System.getProperty("user.dir");
    
    private int ImageFileType = JPG;
    
    private int Width = 10;
    
    private int Height = 10;
    
    private boolean ColourMode = true;
    
    /** Creates a new instance of ImageOutputSynapse */
    public ImageOutputSynapse() {
    }
    
    /**
     * Writes the Neural Network pattern to an Image file, the Image type is specified by
     * the ImageFileType property.
     */
    public void write(org.joone.engine.Pattern pattern) {
        
        if ( pattern.getCount() != -1) {
            String formatName;
            switch(getImageFileType()) {
                case JPG :
                    formatName = "jpg";
                    break;
                case GIF :
                    formatName = "gif";
                    break;
                case PNG :
                    formatName = "png";
                    break;
                default:
                    formatName = "jpg";                    
            }
            try {
                String outDir = getOutputDirectory();
                if (!outDir.endsWith(System.getProperty("file.separator")))
                    outDir += System.getProperty("file.separator");
                File theOutFile = new File(outDir+"Image"+pattern.getCount()+"."+formatName);
                
                BufferedImage outImage = patternToImage(pattern);
                if ( outImage != null) {
                    javax.imageio.ImageIO.write(outImage, formatName, theOutFile);
                }
            } catch(Exception ex) {
                log.error("Caught exception when trying to write Image : "+ex.toString());
            }
        }
    }
    
    /**
     * Converts the given Neural Network pattern to a BufferedImage.
     * @param pat The Neural Network pattern to convert.
     * @return The pattern converted to a BufferedImage.
     */
    private BufferedImage patternToImage(org.joone.engine.Pattern pat) {
        BufferedImage theImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        double [] array = pat.getArray();
        int Red = 0,Green= 0,Blue = 0;
        int rgb = 0, x=0, y=0, j=0;
        if ( ColourMode ) {
            if ( array.length == getWidth()*getHeight()*3) {
                //for ( y=0;y<getHeight();y++)
                //    for ( x=0;x<getWidth()*3;x+=3) {
                for ( j=0;j<array.length;j+=3){
                    
                    Red = (int)(array[j]*255.0);
                    Green = (int)(array[j+1]*255.0);
                    Blue = (int)(array[j+2]*255.0);
                    rgb = 0 + (Red<<16) + (Green<<8) + Blue;
                    theImage.setRGB(x,y,rgb);
                    x++;
                    if ( x >= getWidth()) {
                        x=0;
                        y++;
                    }
                }
            } else {
                log.error("Pattern Contains "+pat.getCount()+" RGB Values - Image Contains "+(getWidth()*getHeight())+" RGB Values , Size Mismatch.");
            }
        } else {
            
            if ( array.length == getWidth()*getHeight()) {
                for ( j=0;j<array.length;j++){
                    
                    rgb = (int)(array[j]*(double)255);
                    rgb = 0 + (rgb << 16) + (rgb << 8) + rgb;
                    theImage.setRGB(x,y,rgb);
                    x++;
                    if ( x >= getWidth()) {
                        x=0;
                        y++;
                    }
                }
            } else {
                log.error("Pattern Contains "+pat.getCount()+" RGB Values - Image Contains "+(getWidth()*getHeight())+" RGB Values , Size Mismatch.");
            }
        }
        return(theImage);
    }
    
    /**
     * Gets the directory where Image files will be created.
     */
    public String getOutputDirectory() {
        return OutputDirectory;
    }
    
    /**
     * Sets the name of the directory where Image files will be created.
     * @param OutputDirectory The directory used to store generated image files.
     */
    public void setOutputDirectory(String OutputDirectory) {
        this.OutputDirectory = OutputDirectory;
    }
    
    /**
     * Obtains the image type JPG = 1, GIF = 2, PNG = 3 of the format of the output files.
     */
    public int getImageFileType() {
        return ImageFileType;
    }
    
    /**
     * Sets the image type JPG = 1, GIF = 2, PNG = 3 of the format of the generated image files.
     * @param ImageFileType JPG = 1, GIF = 2, PNG = 3.
     */
    public void setImageFileType(int ImageFileType) {
        this.ImageFileType = ImageFileType;
        if ( this.ImageFileType < 1)
            this.ImageFileType = 1;
        if ( this.ImageFileType > 3)
            this.ImageFileType = 3;
    }
    
    /**
     * Gets the desired width of the generated image files.
     */
    public int getWidth() {
        return Width;
    }
    
    /**
     * Sets the desired width of the generated image files.
     */
    public void setWidth(int Width) {
        this.Width = Width;
    }
    
    /**
     * Gets the desired height of the generated image files.
     */
    public int getHeight() {
        return Height;
    }
    
    /**
     * Sets the desired height of the generated image files.
     */
    public void setHeight(int Height) {
        this.Height = Height;
    }
    
    /**
     * Determines if this synapse is in colour mode. false if in grey scale mode.
     * @return The ColourMode if this synapse.
     */
    public boolean getColourMode() {
        return ColourMode;
    }
    
    /**
     * Sets the colour mode of this synapse.
     * @param ColourMode true if in colour mode, false if in grey scale mode.
     */
    public void setColourMode(boolean ColourMode) {
        this.ColourMode = ColourMode;
    }
    
    /** Checks and returns any problems found with the settings of this synapse.
     * @return A TreeSet of problems or errors found with this synapse.
     */
    public TreeSet check() {
        TreeSet checks = super.check();
        if (ColourMode ) {
            if (Width*Height*3 != this.getInputDimension()) {
                checks.add(new NetCheck(NetCheck.FATAL, "Image Width["+getWidth()+"]*Height["+getHeight()+"]*3 not equal to number of inputs from connected layer/s ["+this.getInputDimension()+"]." , this));
            }
        } else {
            if (Width*Height != this.getInputDimension()) {
                checks.add(new NetCheck(NetCheck.FATAL, "Image Width["+getWidth()+"]*Height["+getHeight()+"] not equal to number of inputs from connected layer/s ["+this.getInputDimension()+"]." , this));
            }
        }
        if ( OutputDirectory.equals("") ) {
            checks.add(new NetCheck(NetCheck.FATAL, "No image output directory set." , this));
        }
        
        return checks;
    }
}
