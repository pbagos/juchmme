package org.joone.io;

import java.io.*;
import java.util.*;
import org.joone.log.*;

import org.apache.poi.hssf.usermodel.*;

public class XLSInputTokenizer implements PatternTokenizer {
    /**
     * Logger
     * */
    private static final ILogger log = LoggerFactory.getLogger(XLSInputTokenizer.class);
    
    private int numTokens = 0;
    private char m_decimalPoint = '.';
    private double[] tokensArray;
    private int row_no = 0, mark_row = 0;
    final private HSSFSheet sheet;
    
    public XLSInputTokenizer(HSSFSheet i_sheet) throws java.io.IOException {
        sheet = i_sheet;
    }
    
    public int getLineno() {
        return row_no;
    };
    
    public int getNumTokens() throws IOException {
        return numTokens;
    }
    
    public double getTokenAt(int p0) throws java.io.IOException {
        if (tokensArray == null)
            if (!nextLine())
                return 0;
        if (tokensArray.length <= p0)
            return 0;
        return tokensArray[p0];
    }
    
    public double[] getTokensArray() {
        return tokensArray;
    }
    
    public void mark() throws IOException {
        mark_row = row_no;
    }
    
    public boolean nextLine() throws IOException {
        int lastRowNum = sheet.getLastRowNum();
        if (row_no <= lastRowNum){
            HSSFRow row = sheet.getRow(row_no);
            if (row == null) {
                // The row doesn't exist (it's empty)
                numTokens = 0;
                tokensArray = new double[numTokens];
            }
            else {
                int maxCol = 0;
                Hashtable cellist = new Hashtable();
                Iterator iter = row.cellIterator();
                while (iter.hasNext()) {
                    double v = 0;
                    HSSFCell cell = (HSSFCell)iter.next();
                    int i = cell.getCellNum();
                    if (i > maxCol)
                        maxCol = i;
                    switch (cell.getCellType()) {
                        case HSSFCell.CELL_TYPE_STRING:
                            String cellValue = (String)cell.getStringCellValue();
                            try {
                                v = Double.valueOf(cellValue).doubleValue();
                            } catch (NumberFormatException nfe) {
                                log.warn( "Warning: Not numeric cell at ("+row_no+","+i+"): <" + cellValue + "> - Skipping");
                                v = 0;
                            }
                            break;
                        case HSSFCell.CELL_TYPE_NUMERIC:
                            v = (double)cell.getNumericCellValue();
                            break;
                    }
                    cellist.put(new Integer(i), new Double(v));
                }
                tokensArray = new double[maxCol+1];
                Enumeration keys = cellist.keys();
                while (keys.hasMoreElements()) {
                    Integer Icol = (Integer)keys.nextElement();
                    Double Dval = (Double)cellist.get(Icol);
                    tokensArray[Icol.intValue()] = Dval.doubleValue();
                }
            }
            ++row_no ;
            return true;
        }
        else{
            return false;
        }
    }
    
    public void resetInput() throws IOException {
        row_no = mark_row;
    }
    
    public void setDecimalPoint(char dp) {
        m_decimalPoint = dp;
    }
    
    public char getDecimalPoint() {
        return m_decimalPoint;
    }
    
}
