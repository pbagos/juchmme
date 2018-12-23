package org.joone.engine.learning;

import org.joone.log.*;
import org.joone.engine.*;
import org.joone.io.*;
import org.joone.net.NetCheck;

import java.io.IOException;
import java.util.TreeSet;


/**
 * Final element of a neural network; it permits to calculate
 * both the error of the last training cycle and the vector
 * containing the error pattern to apply to the net to
 * calculate the backprop algorithm.
 */
public class TeacherSynapse extends AbstractTeacherSynapse {
    
    /**
     * Logger
     **/
    protected static final ILogger log = LoggerFactory.getLogger(TeacherSynapse.class);
    
    /** The error being calculated for the current epoch. */
    protected transient double GlobalError = 0;
    
    private static final long serialVersionUID = -1301682557631180066L;
    
    public TeacherSynapse() {
        super();
    }
    
    protected double calculateError(double aDesired, double anOutput, int anIndex) {
        double myError = aDesired - anOutput;
        
        // myError = Dn - Yn
        // myError^2 = (Dn - yn)^2
        // GlobalError += SUM[ SUM[ 1/2 (Dn - yn)^2]]
        // GlobalError += SUM[ 1/2 SUM[(Dn - yn)^2]]
        
        //GlobalError += (myError * myError) / 2; /*for RMSE*/
        GlobalError -= (aDesired*Math.log(anOutput) + (1-aDesired)*Math.log(1-anOutput)); /*for Cross Entropy*/
        return myError;
    }
    
    protected double calculateGlobalError() {
        double myError = GlobalError / getMonitor().getNumOfPatterns();
        if(getMonitor().isUseRMSE()) {
            myError = Math.sqrt(myError);
        }
        GlobalError = 0;
        return myError;
    }
    
    
    public void fwdPut(Pattern pattern) {
        super.fwdPut(pattern);
        
        if (pattern.getCount() == -1) {
            // reset error
            GlobalError = 0;
        }
    }
    
}