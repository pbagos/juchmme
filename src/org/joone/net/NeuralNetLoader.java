/*
 * NeuralNetLoader.java
 *
 * Created on 15 aprile 2002, 22.54
 */

package org.joone.net;

import java.io.*;

import org.joone.log.*;

/**
 *
 * @author  pmarrone
 */
public class NeuralNetLoader 
{
    /**
     * Logger
     * */
    private static final ILogger log = LoggerFactory.getLogger (NeuralNetLoader.class);
    
    NeuralNet nnet;

    /** Creates a new instance of NeuralNetLoader */
    public NeuralNetLoader(String netName) {
        try {
            nnet = readNeuralNet(netName);
        }
        catch (Exception e) 
        { 
            log.error ( "Cannot create the NeuralNet with the following name : \"" + netName + "\"", 
                        e);
        }
    }

    public NeuralNet getNeuralNet() {
        return nnet;
    }
 
    // Read the object of NeuralNet from the file with name NeuralNet
    private NeuralNet readNeuralNet(String NeuralNet) throws IOException, ClassNotFoundException {
        
        if (NeuralNet == null)
            return null;
        if (NeuralNet.equals(new String("")))
            return null;
        
        File NNFile = new File(NeuralNet);
        FileInputStream fin = new FileInputStream(NNFile);
        ObjectInputStream oin = new ObjectInputStream(fin);
        
        NeuralNet newNeuralNet = (NeuralNet)oin.readObject();
        
        oin.close();
        fin.close();
        return newNeuralNet;
    }   
}
