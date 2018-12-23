/*
 * NestedNeuralLayer.java
 *
 * Created on 29 February 2002
 */

package org.joone.net;

import java.io.*;
import java.util.*;
import org.joone.io.StreamInputSynapse;
import org.joone.io.StreamOutputSynapse;

import org.joone.log.*;
import org.joone.engine.*;

public class NestedNeuralLayer extends Layer {
    /**
     * Logger
     * */
    private static final ILogger log = LoggerFactory.getLogger(NestedNeuralLayer.class);
    
    static final long serialVersionUID = -3697306754884303651L;
    private String sNeuralNet;	// The file name for the stored NeuralNet
    private NeuralNet NestedNeuralNet;
    private LinearLayer lin;
    private transient File embeddedNet = null;
    
    public NestedNeuralLayer() {
        this("");
    }
    
    public NestedNeuralLayer(String ElemName) {
        super();
        NestedNeuralNet = new NeuralNet();
        /* We add a dummy layer to store the connections made before the true NestedNN is set
         * so the NestedNeuralLayer acts as a LinearLayer in absence of an internal NN
         */
        lin = new LinearLayer();
        lin.setLayerName("Nested LinearLayer");
        NestedNeuralNet.addLayer(lin, NeuralNet.INPUT_LAYER);
        sNeuralNet = new String();
        setLayerName(ElemName);
    }
    
    protected void setDimensions() {
        
    }
    
    protected void forward(double[] pattern) {
        
    }
    
    protected void backward(double[] pattern) {
        
    }
    
    public String getNeuralNet() {
        return sNeuralNet;
    }
    
    public void setNeuralNet(String NNFile) {
        sNeuralNet = NNFile;
        try {
            NeuralNet newNeuralNet = readNeuralNet();
            if (newNeuralNet != null)
                this.setNestedNeuralNet(newNeuralNet);
        } catch (Exception e) {
            log.warn( "Exception thrown. Message is : " + e.getMessage(),
                    e );
        }
    }
    
    public void start() {
        NestedNeuralNet.start();
    }
    
    public void stop() {
        NestedNeuralNet.stop();
    }
    
    public int getRows() {
        return NestedNeuralNet.getRows();
    }
    
    public void setRows(int p1) {
        NestedNeuralNet.setRows(p1);
    }
    
    public void addNoise(double p1) {
        if (this.isLearning())
            NestedNeuralNet.addNoise(p1);
    }
    
    public void randomize(double amplitude) {
        if (this.isLearning())
            NestedNeuralNet.randomize(amplitude);
    }
    
    public Matrix getBias() {
        return NestedNeuralNet.getBias();
    }
    
    public Vector getAllOutputs() {
        return NestedNeuralNet.getAllOutputs();
    }
    
    public String getLayerName() {
        return NestedNeuralNet.getLayerName();
    }
    
    public void removeOutputSynapse(OutputPatternListener p1) {
        NestedNeuralNet.removeOutputSynapse(p1);
    }
    
    public void setAllInputs(Vector p1) {
        NestedNeuralNet.setAllInputs(p1);
    }
    
    public void removeAllOutputs() {
        NestedNeuralNet.removeAllOutputs();
    }
    
    public Vector getAllInputs() {
        return NestedNeuralNet.getAllInputs();
    }
    
    public boolean addOutputSynapse(OutputPatternListener p1) {
        return NestedNeuralNet.addOutputSynapse(p1);
    }
    
    public void setBias(Matrix p1) {
        NestedNeuralNet.setBias(p1);
    }
    
    public void removeInputSynapse(InputPatternListener p1) {
        NestedNeuralNet.removeInputSynapse(p1);
    }
    
    public void setLayerName(String p1) {
        NestedNeuralNet.setLayerName(p1);
    }
    
    public boolean addInputSynapse(InputPatternListener p1) {
        return NestedNeuralNet.addInputSynapse(p1);
    }
    
    public void setAllOutputs(Vector p1) {
        NestedNeuralNet.setAllOutputs(p1);
    }
    
    public void setMonitor(Monitor p1) {
        getMonitor().setParent(p1);
    }
    
    public Monitor getMonitor() {
        return NestedNeuralNet.getMonitor();
    }
    
    public void removeAllInputs() {
        NestedNeuralNet.removeAllInputs();
    }
    
    public NeuralLayer copyInto(NeuralLayer p1) {
        return NestedNeuralNet.copyInto(p1);
    }
    
    /**
     * Reads the object of NeuralNet from the file with name NeuralNet
     */
    private NeuralNet readNeuralNet() throws IOException, ClassNotFoundException {
        
        if (sNeuralNet == null)
            return null;
        if (sNeuralNet.equals(new String("")))
            return null;
        
        File NNFile = new File(sNeuralNet);
        FileInputStream fin = new FileInputStream(NNFile);
        ObjectInputStream oin = new ObjectInputStream(fin);        
        NeuralNet newNeuralNet = (NeuralNet)oin.readObject();
        oin.close();
        fin.close();
        return newNeuralNet;
    }
    
    public boolean isRunning() {
        if (NestedNeuralNet == null)
            return false;
        else
            return NestedNeuralNet.isRunning();
    }
    
    /** Getter for property NestedNeuralNet.
     * @return Value of property NestedNeuralNet.
     *
     */
    public NeuralNet getNestedNeuralNet() {
        return NestedNeuralNet;
    }
    
    /** Setter for property NestedNeuralNet.
     * @param NestedNeuralNet New value of property NestedNeuralNet.
     *
     */
    public void setNestedNeuralNet(NeuralNet newNeuralNet) {
        newNeuralNet.removeAllListeners();
        newNeuralNet.setLayerName(NestedNeuralNet.getLayerName());
        newNeuralNet.setTeacher(null); // The nested NN cannot have a own teacher
        newNeuralNet.setAllInputs(NestedNeuralNet.getAllInputs());
        newNeuralNet.setAllOutputs(NestedNeuralNet.getAllOutputs());
        Monitor extMonitor = getMonitor();
        lin = null;
        NestedNeuralNet = newNeuralNet;
        NestedNeuralNet.setMonitor(new Monitor());
        this.setMonitor(extMonitor);
    }
    
    /** Getter for property learning.
     * @return Value of property learning.
     *
     */
    public boolean isLearning() {
        return NestedNeuralNet.getMonitor().isLearning();
    }
    
    /** Setter for property learning.
     * @param learning New value of property learning.
     *
     */
    public void setLearning(boolean learning) {
        NestedNeuralNet.getMonitor().setLearning(learning);
    }
    
    public TreeSet check() {
        return setErrorSource(NestedNeuralNet.check());
    }
    
    public File getEmbeddedNet() {
        return embeddedNet;
    }
    
    public void setEmbeddedNet(File embeddedNet) {
        if (embeddedNet != null) {
            if (!sNeuralNet.equals(embeddedNet.getAbsolutePath())) {
                this.embeddedNet = embeddedNet;
                setNeuralNet(embeddedNet.getAbsolutePath());
            }
        } else {
            this.embeddedNet = embeddedNet;
            sNeuralNet = "";
        }
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.embeddedNet = new File(sNeuralNet);
    }
    
    // Changes the source of the errors generated from internal components
    private TreeSet setErrorSource(TreeSet errors) {
        if (!errors.isEmpty()) {
            Iterator iter = errors.iterator();
            while (iter.hasNext()) {
                NetCheck nc = (NetCheck)iter.next();
                if (!(nc.getSource() instanceof Monitor) &&
                        !(nc.getSource() instanceof StreamInputSynapse) &&
                        !(nc.getSource() instanceof StreamOutputSynapse))
                    nc.setSource(this);
            }
        }
        return errors;
    }
    
    public void fwdRun(Pattern pattIn) {
        NestedNeuralNet.singleStepForward(pattIn);
    }
    
    public void revRun(Pattern pattIn) {
        NestedNeuralNet.singleStepBackward(pattIn);
    }

}
