package org.joone.io;

import java.util.*;
import java.io.*;
import java.awt.image.PixelGrabber;
import java.awt.Image;
import java.net.URL;
import javax.imageio.ImageIO;

import org.joone.log.*;


/**
 * This tokenizer is responsible for collecting data from a list of Image file names or Image objects and feeding the data 
 * from the Images into a tokenized array of values for feeding into the Neural network.
 * GIF, JPG and PNG image file formats can be read.  The tokenizer operates in two modes, colour and grey scale.
 * <P>
 * <B>Colour Mode</B>
 * In colour mode the tokenizer produces seperate RGB input values in the range 0 to 1 from the 
 * image.  So using an image of width 10 and height 10 there will be 10x10x3 inputs in the range 0 to 1.
 * The individual colour components are calculated by obtaining the RGB values from the image.  These value
 * are initially in an ARGB format.  Transparency is removed and the RGB value extracted and normalised 
 * between 0 and 1.
 * </P>  
 * <P>
 * <B>Non Colour Mode / Grey Scale Mode </B>
 * In this mode the tokenizer treats each input value as a grey scale value for each pixel.  In this mode
 * only Width*Height values are required.  To produce the final image the Red, Green and Blue components
 * are the set to this same value.
 * The grey scale component is calculated by obtaining the RGB values from the image.  These value
 * are initially in an ARGB format.  Transparency is removed and the RGB value extracted, averaged and normalised 
 * to produce one grey scale value between 0 and 1.
 * </P>
 * @author Julien Norman
 */
public class ImageInputTokenizer implements PatternTokenizer {
    
    // Required Image Width and Height
    private int RequiredWidth = 0, RequiredHeight = 0;
    private int TotalTokens = 0;
    
    private double [] ImageTokens = null;
    
    // Holds a Vector or URL's to the required Image
    private Vector ImageFileList = new Vector();
    
    // An Array of Images provided by user code.
    private Image [] ArrayOfInputImages = null;
    
    private int TotalInputImages = 0;
    
    // Indicates if Images come from a list of files, false implies Array of Images.
    private boolean FileMode = true;
    
    // Indicates if 3 tokens (r,g,b) for each pixel, else one grey scale token
    private boolean ColourMode = true;
    
    private int CurrentImageNo = 0;
    
    private int CurrentToken = 0;
    
    private int MarkedImage = 0;
    private int MarkedToken = 0;
        
    /**
     * Logger
     * */
    private static final ILogger log = LoggerFactory.getLogger(ImageInputTokenizer.class);
    
    /** Creates new ImageInputTokenizer
     * @param in
     */
    public ImageInputTokenizer(int req_width,int req_height,Vector file_list,boolean colour) throws java.io.IOException {
        
        FileMode = true;
        RequiredWidth = req_width; RequiredHeight = req_height;
        ColourMode = colour;
        if ( ColourMode )
            TotalTokens = RequiredWidth*RequiredHeight*3;
        else
            TotalTokens = RequiredWidth*RequiredHeight;
        ImageFileList = file_list;
        if ( ImageFileList != null)
            TotalInputImages = ImageFileList.size();
        else
            TotalInputImages = -1;
        
        ImageTokens = new double[TotalTokens];
    }
    
    /** Creates new ImageInputTokenizer
     * @param in
     */
    public ImageInputTokenizer(int req_width,int req_height,Image [] the_images,boolean colour) throws java.io.IOException {
        
        FileMode = false;
        RequiredWidth = req_width; RequiredHeight = req_height;
        ColourMode = colour;
        if ( ColourMode )
            TotalTokens = RequiredWidth*RequiredHeight*3;
        else
            TotalTokens = RequiredWidth*RequiredHeight;
        ArrayOfInputImages = the_images;        
        if ( the_images != null)
            TotalInputImages = the_images.length;
        else
            TotalInputImages = -1;
        
        ImageTokens = new double[TotalTokens];
    }
    
    /** Return the current line number.
     * @return  the current line number
     */
    public int getLineno() {
        return CurrentImageNo;
    }
    
    /**
     * Gets the number of tokens on the current line.
     */
    public int getNumTokens() throws java.io.IOException {
        return TotalTokens;
    }
    
    /**
     * Gets the token (RGB normalised between 0 and 1) at the specified position.
     * @return double The token (combined RGB) at the specified position on the current line.
     * @param posiz An int index into the current lines tokens.
     */
    public double getTokenAt(int posiz) throws java.io.IOException {
        if ( posiz < TotalTokens )
            if ( ImageTokens != null)
                return (ImageTokens[posiz]);
            else
                return(0);
        else
            return(0);
    }
    
    /**
     * Insert the method's description here.
     * Creation date: (17/10/2000 0.13.45)
     * @return float[]
     */
    public double[] getTokensArray() {
        return(ImageTokens);
    }
    
    /**
     * Mark the current position.
     */
    public void mark() throws java.io.IOException {
        MarkedImage = CurrentImageNo;
        MarkedToken = CurrentToken;
    }
    
    /** Fetchs the next line and extracts all the tokens
     * @return false if EOF, otherwise true
     * @throws IOException if an I/O Error occurs
     */
    public boolean nextLine() throws java.io.IOException {
        
        boolean result = false;
        
        if ((CurrentImageNo >= TotalInputImages) || (TotalInputImages <= 0))
            return false;
        else {
            
            if ( FileMode ) // Load the next Image from the file list.
            {
                try {
                    // Try to get the Image
                    
                    URL theFile = (URL)ImageFileList.get(CurrentImageNo);
                    Image tImg = ImageIO.read(theFile);
                    result = processImage(tImg);
                } catch(Exception ex) {
                    log.error("Caught Error processing Image : "+ex.toString());
                }
            } else // Get the Image from the passed in array of images.
            {
                if ( (ArrayOfInputImages != null) && (ArrayOfInputImages.length >= 1)) {
                    result = processImage(ArrayOfInputImages[CurrentImageNo]);
                } 
            }
            CurrentImageNo++;
            return(result);
        }
    }
    
    /** Return the next token's double value in the current line
     * @return the next double value
     */
    private double nextToken() throws java.io.IOException {
        return this.nextToken(null);
    }
    
    /** Return the next token's double value in the current line;
     * tokens are separated by the characters contained in delim
     * @return the next double value
     * @param delim String containing the delimitators characters
     */
    private double nextToken(String delim) throws java.io.IOException {
        
        if ( CurrentToken < TotalTokens) {
            double value = ImageTokens[CurrentToken];
            CurrentToken++;
            return(value);
        } else
            return 0;
    }
    
    /** Go to the last marked position. Begin of input stream if no mark detected.
     */
    public void resetInput() throws java.io.IOException {
        if ( (MarkedImage > 0) || (MarkedToken > 0)) {
            CurrentImageNo = MarkedImage;
            CurrentToken = MarkedToken;
        } else {
            // Goto start of Images again.
            CurrentImageNo = 0;
        }
        if ( FileMode ) // Load the right Image from the file list.
        {
            try {
                URL theFile = (URL)ImageFileList.get(CurrentImageNo);
                    Image tImg = ImageIO.read(theFile.openStream());
                    processImage(tImg);
            } catch(Exception ex) {
                log.error("Error processing/loading image : "+ex.toString());
            }
        } else // Get the Image from the passed in array of images.
        {
            if ( (ArrayOfInputImages != null) && (ArrayOfInputImages.length >= 1)) {
                processImage(ArrayOfInputImages[CurrentImageNo]);
            }
        }
    }
    
    public void setDecimalPoint(char dp) {
        return;
    }
    
    public char getDecimalPoint() {
        return('.');
    }
    
    /**
     * Processes and returns the final image after scaling etc for input to network.
     */
    private boolean processImage(Image theImage) {
        double Red = 0,Green = 0, Blue = 0;
        double Grey = 0;
        int [] TempTokens = null;
        try {
            if ( ColourMode )
                TempTokens = new int[TotalTokens/3];
            else
                TempTokens = new int[TotalTokens];
            
            Image TempImage = theImage.getScaledInstance(RequiredWidth,RequiredHeight,Image.SCALE_AREA_AVERAGING);

            PixelGrabber pixgrab = new java.awt.image.PixelGrabber(TempImage,0,0,TempImage.getWidth(null),TempImage.getHeight(null),TempTokens,0,TempImage.getWidth(null));
            if ( pixgrab.grabPixels() == true) {
                
                // Ok should have sRGB data in TempTokens array, now need to convert the data
                // to normalised values between 0 and 1 for presentation to network.
                // Now loop through TempTokens array and convert
                //double div = 0x00FFFFFF;
                if ( ColourMode )
                {
                    for ( int i=0;i<TempTokens.length;i++) {
                        Red =   ((double)((TempTokens[i]&0x00FF0000)>>16))/255.0;
                        Green = ((double)((TempTokens[i]&0x0000FF00)>>8))/255.0;
                        Blue =  ((double)((TempTokens[i]&0x000000FF)))/255.0;
                        ImageTokens[(i*3)] = Red;
                        ImageTokens[(i*3)+1] = Green;
                        ImageTokens[(i*3)+2] = Blue;
                        //System.out.format("In RGB = %x",TempTokens[i]);
                    }
                }
                else // Grey Scale
                {
                    for ( int i=0;i<TempTokens.length;i++) {
                        Red = ((double)((TempTokens[i]&0x00FF0000)>>16))/255.0;
                        Green = ((double)((TempTokens[i]&0x0000FF00)>>8))/255.0;
                        Blue = ((double)((TempTokens[i]&0x000000FF)))/255.0;
                        Grey = (Red + Blue + Green)/3;
                        ImageTokens[i] = Grey;
                    }
                }
            } else
            {
                log.error("Failed to grab image pixels due to error.");
                return(false);
            }
        } catch(Exception ex) {
            
            log.error("Error processing image : "+ex.toString());
            return(false);
        }
        return(true);
    }
}