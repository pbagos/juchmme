package org.joone.util;

import org.joone.net.NeuralNet;
import org.joone.net.NeuralNetLoader;
import org.joone.engine.NeuralNetListener;
import org.joone.engine.NeuralNetEvent;
import org.joone.engine.Monitor;

import java.io.*;

/**

*/

public class NeuralNetRunner implements NeuralNetListener {
    public NeuralNet nnet;
    public int result = 0;

    private String snetFileName;
    private String snetOutputFileName;

    private long lPrintCicle = 1000;

    public NeuralNetRunner()
    {
    }

    public static void main(String args[]) {
        NeuralNetRunner nnRunner = new NeuralNetRunner();

        if (args.length < 1) {
            System.out.println("Usage: java NeuralNetRunner -snet <snetFile> [-printcicle <integer>] -snetout <snetOutputFile");
            System.out.println("where <snetFile> is the Serialized Output from Joone Edit");
            System.out.println("where <integer> is the Multiple of cicles output should be printed to standard output");
            System.out.println("where <snetOutputFile> is filename for NeuralNetRunner to save the NeurlalNet as it is processing.");
	    System.exit(1);
        }
        else {
	    for (int n = 0; n < args.length; n++) {
      		if (args[n].equals("-snet")) {
            	    nnRunner.snetFileName = args[++n];
      		}
      		else if (args[n].equals("-printcicle")) {
            	    nnRunner.lPrintCicle = Long.parseLong(args[++n]);
      		}
      		else if (args[n].equals("-snetout")) {
            	    nnRunner.snetOutputFileName = args[++n];
      		}
      		else {
        	    throw new IllegalArgumentException("Unknown argument.");
      		}
    	    }
        }

	if (nnRunner.snetFileName == null) {
		System.out.println("ERROR: A snet input parameter is required to run");
		System.exit(1);
	}
	else if (nnRunner.snetFileName.equals(nnRunner.snetOutputFileName)) {
		System.out.println("ERROR: The output snet should not be the same as the input snet .");
		System.exit(1);
	} 

	NeuralNetLoader nnl = new NeuralNetLoader(nnRunner.snetFileName);
	
        nnRunner.setNnet(nnl.getNeuralNet());
        nnRunner.execute();
    }
    
    public void execute() {
        if (nnet != null) 
        {
            /* First of all, registers itself as neural net's listner,
             * so it can receive all the training events.
             */            
            nnet.getMonitor().addNeuralNetListener(this);
            // Runs the neural network's training cycles
            nnet.start();
            nnet.getMonitor().Go();
            /* Waits for the end of the training cycles */
            synchronized(this) {
                try {
                    while (result == 0)
                        wait();
                }
                catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        }
        else
            throw new RuntimeException("Can't work: the neural net is null");
    }
    
    public void cicleTerminated(NeuralNetEvent e) 
    { 
        Monitor mon = (Monitor)e.getSource();
        long c = mon.getCurrentCicle();
        long cl = c / lPrintCicle;
        /* We want print the results every lPrintCicle cycles */
        if ((cl * lPrintCicle) == c)
        {
            System.out.println(c + " cycles remaining - Error = " + mon.getGlobalError());
            writeNnet();
        }

    }
    
    public void netStopped(NeuralNetEvent e) {
        synchronized(this) {
            // Notify the thread that the NN is finished
            result = 1;
            // Notifies the waiting threads
            notifyAll();
        }
    }

    public void writeNnet() 
    {
      if (snetOutputFileName != null) {
        try {
          FileOutputStream stream = new FileOutputStream(snetOutputFileName);
          ObjectOutput output = new ObjectOutputStream(stream);
          output.writeObject(nnet);
          output.close();
        }
        catch (IOException ioe)
        {
          System.err.println("Error writing nnet: " + ioe);
        }
      }
    }
    
    public NeuralNet getNnet() {
        return nnet;
    }
    
    public void setNnet(NeuralNet nnet) {
        this.nnet = nnet;
    }

    public void resetNnet()
    {
      nnet.resetInput();
    }
    
    public void netStarted(NeuralNetEvent e) {
        System.out.println("Running...");
    }
    
    public void errorChanged(NeuralNetEvent e) {
    }
    
    public void netStoppedError(NeuralNetEvent e,String error) {
    }
    
}

