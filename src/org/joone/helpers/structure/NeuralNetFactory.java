/*
 * NeuralNetFactory.java
 *
 * Created on August 18, 2005, 2:41 PM
 *
 */

package org.joone.helpers.structure;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import org.joone.engine.Monitor;
import org.joone.engine.learning.TeachingSynapse;
import org.joone.helpers.factory.JooneTools;
import org.joone.io.FileInputSynapse;
import org.joone.io.StreamInputSynapse;
import org.joone.net.NeuralNet;
import org.joone.util.NormalizerPlugIn;

/**
 * This utility class creates a new neural network according to some parameters
 * @author P.Marrone
 */
public class NeuralNetFactory {
    public static final int CLASSIFICATION = 1;
    public static final int ONEOFC_CLASSIF = 2;
    public static final int APPROXIMIMATION = 3;
    public static final int PREDICTION = 4;
    public static final int CLUSTERING = 5;
    
    private int type;
    private String inputFileName;
    private String inputCols;
    private boolean skipFirstInputRow;
    private String desiredFileName;
    private String desiredCols;
    private boolean skipFirstDesiredRow;
    // Time Series parameters
    private int taps;
    private int predictLength;
    // SOM output map parameters
    private int mapWidth;
    private int mapHeight;
    
    /**
     * Creates a new instance of NeuralNetFactory
     */
    public NeuralNetFactory() {
    }
    
    public NeuralNet getNeuralNetwork() {
        NeuralNet nnet = null;
        switch (getType()) {
            case CLASSIFICATION:
            case ONEOFC_CLASSIF:
            case APPROXIMIMATION: nnet = createFFNN(); break;
            case PREDICTION: nnet = createTimeSeries(); break;
            case CLUSTERING: nnet = createKohonen(); break;
        }
        create_IO(nnet);
        return nnet;
    }
    
    protected NeuralNet createFFNN() {
        int inputRows = getNumOfColumns(inputCols);
        int outputRows = getNumOfColumns(desiredCols);
        int nodes[] = { inputRows, inputRows, outputRows };
        
        int outputType = JooneTools.LINEAR;
        switch (getType()) {
            case CLASSIFICATION:
                outputType = JooneTools.LOGISTIC; break;
            case ONEOFC_CLASSIF:
                outputType = JooneTools.SOFTMAX; break;
            case APPROXIMIMATION:
                outputType = JooneTools.LINEAR; break;
        }
        
        NeuralNet nnet = JooneTools.create_standard(nodes, outputType);
        
        Monitor mon = nnet.getMonitor();
        mon.setTotCicles(5000);
        mon.setTrainingPatterns(getNumOfRows(inputFileName, skipFirstInputRow));
        mon.setLearning(true);
        
        return nnet;
    }
    
    protected NeuralNet createKohonen() {
        int inputRows = getNumOfColumns(inputCols);
        int outputRows = 10;
        int nodes[] = { inputRows, getMapWidth(), getMapHeight() };
        
        int outputType = JooneTools.WTA;
        NeuralNet nnet = JooneTools.create_unsupervised(nodes, outputType);
        
        Monitor mon = nnet.getMonitor();
        mon.setTotCicles(5000);
        mon.setTrainingPatterns(getNumOfRows(inputFileName, skipFirstInputRow));
        mon.setLearning(true);
        
        return nnet;
    }
    
    protected NeuralNet createTimeSeries() {
        int inputRows = getNumOfColumns(inputCols);
        int outputRows = getNumOfColumns(desiredCols);
        int nodes[] = { inputRows, getTaps(), outputRows };
        
        int outputType = JooneTools.LINEAR;
        
        NeuralNet nnet = JooneTools.create_timeDelay(nodes, getTaps()-1, outputType);
        
        Monitor mon = nnet.getMonitor();
        mon.setTotCicles(5000);
        mon.setTrainingPatterns(getNumOfRows(inputFileName, skipFirstInputRow) - getPredictionLength());
        mon.setLearning(true);
        mon.setPreLearning(getTaps());
        return nnet;
    }
    
    protected void create_IO(NeuralNet nnet) {
        StreamInputSynapse inputData = createInput(inputFileName, inputCols, skipFirstInputRow? 2:1);
        nnet.getInputLayer().addInputSynapse(inputData);
        
        if (getType() != CLUSTERING) {
            StreamInputSynapse targetData = createInput(desiredFileName, desiredCols, skipFirstDesiredRow? 2:1);
            if (getPredictionLength() > 0) {
                targetData.setFirstRow(inputData.getFirstRow()+getPredictionLength());
            }
            TeachingSynapse teacher = new TeachingSynapse();
            teacher.setName("Teacher");
            teacher.setDesired(targetData);
            nnet.getOutputLayer().addOutputSynapse(teacher);
            nnet.setTeacher(teacher);
        }
    }
    
    protected StreamInputSynapse createInput(String fileName, String columns, int firstRow) {
        FileInputSynapse in = new FileInputSynapse();
        in.setInputFile(new File(fileName));
        in.setAdvancedColumnSelector(columns);
        in.setFirstRow(firstRow);
        NormalizerPlugIn norm = new NormalizerPlugIn();
        int cols = getNumOfColumns(columns);
        norm.setAdvancedSerieSelector("1-"+cols);
        in.addPlugIn(norm);
        return in;
    }
    
    
    public int getType() {
        return type;
    }
    
    public void setType(int type) {
        this.type = type;
    }
    
    public String getInputFileName() {
        return inputFileName;
    }
    
    public void setInputFileName(String inputFileName) {
        this.inputFileName = inputFileName;
    }
    
    public String getInputCols() {
        return inputCols;
    }
    
    public void setInputCols(String inputCols) {
        this.inputCols = inputCols;
    }
    
    public boolean isSkipFirstInputRow() {
        return skipFirstInputRow;
    }
    
    public void setSkipFirstInputRow(boolean skipFirstInputRow) {
        this.skipFirstInputRow = skipFirstInputRow;
    }
    
    public String getDesiredFileName() {
        return desiredFileName;
    }
    
    public void setDesiredFileName(String desiredFileName) {
        this.desiredFileName = desiredFileName;
    }
    
    public String getDesiredCols() {
        return desiredCols;
    }
    
    public void setDesiredCols(String desiredCols) {
        this.desiredCols = desiredCols;
    }
    
    public boolean isSkipFirstDesiredRow() {
        return skipFirstDesiredRow;
    }
    
    public void setSkipFirstDesiredRow(boolean skipFirstDesiredRow) {
        this.skipFirstDesiredRow = skipFirstDesiredRow;
    }
    
    protected int getNumOfColumns(String columns) {
        int c = 0;
        StringTokenizer tokens = new StringTokenizer(columns, ",");
        int n = tokens.countTokens();
        for (int i=0; i < n; ++i) {
            String t = tokens.nextToken();
            if (t.indexOf('-') == -1)
                ++c;
            else {
                StringTokenizer tt = new StringTokenizer(t, "-");
                int low = Integer.valueOf(tt.nextToken()).intValue();
                int hig = Integer.valueOf(tt.nextToken()).intValue();
                c += hig - low + 1;
            }
        }
        return c;
    }
    
    protected int getNumOfRows(String fileName, boolean skipFirstLine) {
        int c = 0;
        BufferedReader file = null;
        try {
            // get the content of text file into text variable
            file = new BufferedReader(new FileReader(fileName));
            if (skipFirstLine) file.readLine();
            while (file.readLine() != null)
                ++c;
        } catch (IOException ioe) { ioe.printStackTrace(); } finally {
            if (file != null)
                try {
                    file.close();
                } catch (IOException doNothing) { /* Do Nothing */ }
        }
        return c;
    }

    public int getTaps() {
        return taps;
    }

    public void setTaps(int taps) {
        this.taps = taps;
    }

    public int getPredictionLength() {
        return predictLength;
    }

    public void setPredictionLength(int predictLength) {
        this.predictLength = predictLength;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public void setMapWidth(int mapWidth) {
        this.mapWidth = mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public void setMapHeight(int mapHeight) {
        this.mapHeight = mapHeight;
    }
}
