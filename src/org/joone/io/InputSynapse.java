package org.joone.io;

import org.joone.util.*;

public interface InputSynapse extends PlugInListener {
    
    public char getDecimalPoint();
    
    public int getFirstRow();
    
    public int getLastRow();
    public void gotoFirstLine() throws java.io.IOException;
    
    public void gotoLine(int numLine) throws java.io.IOException;
    
    public boolean isBuffered();
    
    public boolean isEOF();
    // Read all input values and fullfills the buffer
    public void readAll();
    
    public void setBuffered(boolean newBuffered);
    public void setDecimalPoint(char dp);
    
    public void setFirstRow(int newFirstRow);

    public void setLastRow(int newLastRow);    
    
    public void setFirstCol(int numcol)  throws IllegalArgumentException;
    
    public void setLastCol(int numcol) throws IllegalArgumentException;
    
    public int getFirstCol();
    
    public int getLastCol();
    
    public void resetInput();
    
    /** Returns true if this input layer is an active counter of the steps.
     * Warning: in a neural net there can be only one StepCounter element!
     * (10/04/00 23.23.26)
     * @return boolean
     *
     */
    public boolean isStepCounter();
    
    /** Set to true if this input layer is an active counter of the steps.
     * Warning: in a neural net there can be only one StepCounter element!
     * (10/04/00 23.23.26)
     * @param newStepCounter boolean
     *
     */
    public void setStepCounter(boolean newStepCounter);
    
}