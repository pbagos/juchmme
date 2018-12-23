package org.joone.util;

import org.joone.engine.*;
import org.joone.util.CSVParser;

/** <P>Changes the specified input serie data so that it becomes a moving average of
 * itself.  This plugin operates on specified serie/s of data in a vertical fashion.</P>
 * <BR>
 * <P>For example if the serie to be converted contained the following data
 * ....</P>
 * <BR>5<BR>15<BR>5<BR> <P> and the requested moving average was set at 2 then the
 * serie would become <BR>0<BR>10<BR>12.5<BR> <P> Any data prior to the moving
 * average spec is set at 0 as there is not enough data to calculate the actual
 * moving average.  The data is NOT
 * normalised.  To normalise the data use a {@link
 * org.joone.util#NormalizerConverterPlugIn NormalizerConverterPlugIn}.</P>
 * @author Julien Norman
 */
public class MovingAveragePlugIn extends ConverterPlugIn {
    
    static final long serialVersionUID = -5679399800426091523L;
    
    private String AdvancedMovAvgSpec = new String("");
    
    /**
     * Default MovingAveragePlugIn constructor.
     */
    public MovingAveragePlugIn() {
        super();
    }
    
    /**
     * MovingAveragePlugIn constructor that allows specification of the Advanced Serie Selector
     * and the Moving Average Specification.
     */
    public MovingAveragePlugIn(String newAdvSerieSel,String newMovAvgSpec) {
        super();
        setAdvancedMovAvgSpec(newMovAvgSpec);
        setAdvancedSerieSelector(newAdvSerieSel);
    }
    
    /**
     * Start the convertion to a moving average for the required serie.
     */
    protected boolean convert(int serie) {
        boolean retValue = false;
        int s = getInputVector().size();
        int i;
        double CurrentMovingAvgerage = 0;
        double Sum = 0;
        int MovingAverageSpec = 0;
        int CurrentItem = 0;
        CSVParser MovParse = new CSVParser(AdvancedMovAvgSpec,false);
        int [] MovAvgArray = MovParse.parseInt();
        int index = getSerieIndexNumber(serie);
        Pattern currPE;
        
        if ( index > -1 )  // If the serie was found in the spec list
        {
            if ( index < MovAvgArray.length )	// Check that we have an appropriate average.
            {
                MovingAverageSpec = MovAvgArray[index];
            }
        }
        if ( MovingAverageSpec > 0 )  // If we have found a moving average spec for this serie then start to convert
        {
            if ( getInputVector().size() > MovingAverageSpec )	// Check that there is enough data
            {
                Sum = 0;
                CurrentMovingAvgerage = 0;
                // Loop through data starting at the end
                for (i=getInputVector().size()-1;i>-1;i--) {
                    currPE = (Pattern) getInputVector().elementAt(i); // Set current pattern
                    if ( i<MovingAverageSpec-1 ) {
                        // Set any data less than MovingAverageSpec  to 0
                        CurrentMovingAvgerage = 0;
                    }
                    else {
                        // Sum all data and average
                        Sum = 0;
                        for (int j=i;j>i-MovingAverageSpec;j--)
                            Sum = Sum + getValuePoint(j, serie);
                        CurrentMovingAvgerage = Sum / MovingAverageSpec;
                    }
                    currPE.setValue(serie, CurrentMovingAvgerage );
                    retValue = true;
                }
            }
        }
        //double vMax = getValuePoint(0, serie);
        //currPE = (Pattern) getInputVector().elementAt(i);
        //currPE.setValue(serie, v);
        return retValue;
    }
    /**
     * Gets the Moving Average value/s requested by the user.
     * @return double The moving average .
     */
    public String getAdvancedMovAvgSpec() {
        return(AdvancedMovAvgSpec);
    }
    
    /**
     * Sets the Moving Average value/s requested by the user.  It must be a comma delimeted list of moving average values.
     * E.g 10,20,12 would request a moving average 10 in the first specified serie as in
     * the Advanced Serie Selector then a moving average of 20 on the second and a
     * moving average of 12 on the third.
     * @param newMovAvg double
     */
    public void setAdvancedMovAvgSpec(String newAdvancedMovAvgSpec ) {
        if ( AdvancedMovAvgSpec.compareTo(newAdvancedMovAvgSpec ) != 0 ) {
            AdvancedMovAvgSpec = newAdvancedMovAvgSpec ;
            this.fireDataChanged();
        }
    }
    
    
}