/*
 * ToBinaryPlugin.java
 *
 * Created on September 24, 2004, 2:36 PM
 */

package org.joone.util;

import java.util.*;
import org.joone.engine.*;
import org.joone.log.*;

/**
 * This plug-in converts 10-base data to binary format. The plug-in ingnores 
 * the broken part (the part after the . ) for non-integer numbers. It works 
 * correct for positive as well as for negative numbers.
 * 
 * @author Boris Jansen
 */
public class ToBinaryPlugin extends ConverterPlugIn {
    
    /** The logger for this class. */
    private static final ILogger log = LoggerFactory.getLogger(ToBinaryPlugin.class);    
    
    /** The sizes of the (binary) arrays of converted series. This way we are able
     * to find the correct position of serie we have to convert related taking into
     * account any previous converted series. */
    private List theConvertedSeries = new ArrayList();
    
    /** The value for the upper bit. */
    private double upperBit = 1.0; // default
    
    /** The value for the lower bit. */
    private double lowerBit = 0.0; // default
    
    /** Creates a new instance of ToBinaryPlugin */
    public ToBinaryPlugin() {
    }
    
    /** 
     * Creates a new instance of ToBinaryPlugin 
     *
     * @param anAdvancedSerieSelector the advanced serie selector to use.
     * @see setAdvancedSerieSelector()
     */
    public ToBinaryPlugin(String anAdvancedSerieSelector) {
        super(anAdvancedSerieSelector);
    }
    
    protected boolean convert(int serie) {
        boolean retValue = false;
        int mySerie = serie, mySignBitLenght;
        boolean myHasPositiveValues = false;
        boolean myHasNegativeValues = false;
        for(int i = 0; i < theConvertedSeries.size(); i++) {
            // get the correct serie, taking into account any previous converted
            // series, by which the serie changes (from integer to binary results
            // in more columns)
            if(((int[])theConvertedSeries.get(i))[0] < serie) {
                mySerie += ((int[])theConvertedSeries.get(i))[1];
            }
        }
        
        int mySize = 0; // the (largest) size of the converted values = binary arrays (#bits)
        double[] myArray;
        double[][] myBinaries = new double[getInputVector().size()][];
        for(int i = 0; i < getInputVector().size(); i++) {
            myArray = ((Pattern)getInputVector().get(i)).getArray();
            
            myBinaries[i] = getBinary(myArray[mySerie]);
            if(myBinaries[i].length > mySize) {
                mySize = myBinaries[i].length;
            }
            if(myArray[mySerie] > 0) {
                myHasPositiveValues = true;
            } else if(myArray[mySerie] < 0) {
                myHasNegativeValues = true;
            }
        }
        
        // if there are positive as well as negative values we should include a sign bit
        mySignBitLenght = (myHasPositiveValues && myHasNegativeValues) ? 1 : 0;
        
        for(int i = 0; i < getInputVector().size(); i++) {
            myArray = ((Pattern)getInputVector().get(i)).getArray();
            // if we have positive and negative numbers we add an extra bit (the sign bit)
            double[] myNewArray = new double[myArray.length -1 + mySize + mySignBitLenght];
            for(int j = 0; j < myArray.length; j++) {
                // copy myArray into myNewArray, but skip the part where we
                // will place the converted (binary) myArray[mySerie]
                if(j < mySerie) {
                    myNewArray[j] = myArray[j];
                } else if(j > mySerie) {
                    myNewArray[j + mySize + mySignBitLenght - 1] = myArray[j]; // -1 added by yccheok
                }
            }
            for(int j = 0; j < mySize + mySignBitLenght; j++) {
                // now we will copy the binary part to the array
                if(j >= myBinaries[i].length) {
                    myNewArray[mySerie + j] = getLowerBit(); 
                    // if it is the sign bit and the/ value is negative we will update it
                    if(j == mySize) {
                        // this is only possible when mySignBitLenght == 1, else always j < mySize
                        if(myArray[mySerie] < 0) {
                            myNewArray[mySerie + j] = getUpperBit();
                        }
                    }
                } else {
                    myNewArray[mySerie + j] = myBinaries[i][j];
                }
            }
            ((Pattern)getInputVector().get(i)).setArray(myNewArray);
            retValue = true;
            
            // debugging (print the original input array and the converted array
            /* debug
            String myTemp = "";
            for(int j = 0; j < myArray.length; j++) {
                myTemp += (int)myArray[j] + " ";
            }
            log.debug(myTemp + " <- original array");
            
            myTemp = "";
            for(int j = 0; j < myNewArray.length; j++) {
                myTemp += myNewArray[j] + " ";
            }
            log.debug(myTemp + " <- converted (including binary part) array");
            end debug */
        }
        // we have converted a serie -> so the positions change, to find the original
        // position of a serie we save the amount of bits changed
        theConvertedSeries.add(new int[] {serie, mySize + mySignBitLenght -1});
        return retValue;
    }
    
    protected boolean apply() {
        // new convertion ->
        theConvertedSeries = new ArrayList();
        
        return super.apply();
    }
    
    /**
     * Converts a number to a binary number (the part after the . (like 348 
     * in 321.348) is ignored).
     *
     * @param aNumber the number to convert.
     * @return the converted number as an array in binary form.
     */
    protected double[] getBinary(double aNumber) {
        aNumber = Math.floor(aNumber); // throw away the part after the .
        aNumber = Math.abs(aNumber); // here we ignore the sign part (we deal with this
                                     // in the convert() method)
        
        double myTemp = aNumber;
        int mySize = 0;
        
        while(myTemp > 0) {
            mySize++;
            myTemp /= 2;
            myTemp = Math.floor(myTemp);
        }
        
        double[] myBinary = new double[mySize];
        for(int i = 0; i < mySize; i++) {
           myTemp = aNumber / 2;
           aNumber = Math.floor(myTemp);
           
           if(myTemp > aNumber) {
               myBinary[i] = getUpperBit();
           } else {
               myBinary[i] = getLowerBit();
           }
        }
        return myBinary;
    }
    
    /**
     * Sets the value for the upper bit. In binary problems it is often better to use 0.3 and 0.7
     * or -0.7 and 0.7 as target instead of 0 and 1 or -1 and 1, because the asymptotes (0 and 1)
     * tend to take a long time to train, worsen generalization and drive the weights to very
     * large values. 
     * By using this function you can set a different value for the upper bit.
     *
     * @param aValue the value to use for the upper bit.
     */
    public void setUpperBit(double aValue) {
        upperBit = aValue;
    }
    
    /**
     * Gets the value used for the upper bit.
     *
     * @returns the value used for the upper bit.
     */
    public double getUpperBit() {
        return upperBit;
    }
    
    /**
     * Sets the value for the lower bit. 
     *
     * @param aValue the value to use for the lower bit.
     */
    public void setLowerBit(double aValue) {
        lowerBit = aValue;
    }
    
    /**
     * Gets the value used for the lower bit.
     *
     * @returns the value used for the lower bit.
     */
    public double getLowerBit() {
        return lowerBit;
    }
}
