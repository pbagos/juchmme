package nn;

import org.joone.engine.*;
import org.joone.engine.learning.*;
import org.joone.helpers.factory.JooneTools;
import org.joone.io.*;
import org.joone.net.*;
import org.joone.util.LearningSwitch;
import java.io.*;
import java.lang.Math;

/*
 * SingleLayerNN_RPROP Class
 * Neural Network with one hidden layer that implements the PPROP algorithm.
 * Its attributes consist of the number(int) of neurons in the hidden layer,
 * the input array(double[][]) and the desired output array(double[][]).
 */

public class SingleLayerNN_RPROP implements NeuralNetListener, NeuralValidationListener, Serializable
{
	private int hiddenNeuronsNum;
	private int numberOfCycles;
	private double[][] inputArray, desiredOutputArray;
	private boolean doCrossValidation;
	private CrossValidation cv;
	private double previousValidationGlobalError = 1000;
	private double previousGlobalError = 1000;
	private double twotimespreviousGlobalError = 1000;
	private double targetGE = 0;
	private double minimumGEdifference = 0;
	private int cycles_after_min_validation_error_default = 10;
	private int cycles_after_min_validation_error = cycles_after_min_validation_error_default;
	
	private int outputNeuronsNum=1;
	public NeuralNet nnet = new NeuralNet();
	public NeuralNet savedNN = new NeuralNet(); /* specifies the NN with the best generalization */
    private MemoryInputSynapse  biasInputSynapse, inputSynapse, desiredOutputSynapse, inputValidationSynapse, desiredValidationOutputSynapse;
    private MemoryOutputSynapse outputSynapse;
    private FullSynapse synapse_BH = new FullSynapse();
    private FullSynapse synapse_IH = new FullSynapse();	/* create synapse : input -> hidden conn. */
    private FullSynapse synapse_BO = new FullSynapse();
    private FullSynapse synapse_HO = new FullSynapse();	/* create synapse : hidden -> output conn. */
    
    /* constructor */	
	public SingleLayerNN_RPROP(double[][] inputArray, double[][] desiredOutputArray, int hiddenNeuronsNum, int numberOfCycles, boolean doCrossValidation, double targetGE, double minimumGEdifference)
	{
		this.inputArray = inputArray;
		this.desiredOutputArray = desiredOutputArray;
		this.hiddenNeuronsNum = hiddenNeuronsNum;
		this.numberOfCycles = numberOfCycles;
		this.doCrossValidation = doCrossValidation;
		if (this.doCrossValidation == true){
			cv = new CrossValidation(this.inputArray,this.desiredOutputArray);
		}
		this.targetGE = targetGE;
		this.minimumGEdifference = minimumGEdifference;
		
		initNeuralNet();
	}
	
	/* setters methods */
	/**
	 * Set the number of neurons in the hidden layer.
	 */	
	public void setHiddenNeuronsNum(int hiddenNeuronsNum) 
	{
		this.hiddenNeuronsNum = hiddenNeuronsNum;
	}
	
	/**
	 * Set the number of cycles in training process.
	 */	
	public void setNumberOfCycles(int numberOfCycles) 
	{
		this.numberOfCycles = numberOfCycles;
	}
	
	/**
	 * Puts commas between the input columns to fix the input format.
	 * Takes as input a start and stop point. 
	 * Returns a columnSelectorString (String)
	 * @param start (int)
	 * @param stop (int)
	*/
	public String createColumnSelectorString (int start, int stop){
		
		int stringLength=2*(stop-start+1)-1; // every stringLength chars, it will put commas
		
		StringBuilder inputColumns = new StringBuilder(stringLength);
        for (int i=start;i<stop;i++){ 
        	inputColumns.append(i+",");
		}
        inputColumns.append(stop);
        String columnSelectorString = inputColumns.toString(); // StringBuilder TO String
        
        return columnSelectorString;
	}
	
	/**
	 * Print the training set in a file.
	 * 
	 * @param double[][] inputArray 
	 * @param String trainingSet2File = the file where the training set is saved
	 */	
	public void printIOdata(String trainingSetFile){
	    	    
	    try {
	    	PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(trainingSetFile)));	
	    	for (int i=0;i<inputArray.length;i++) {
	    		pw.print(i+"\t");
	    		for (int j=0;j<inputArray[i].length;j++) {
	    			double trainSet = inputArray[i][j];
	    			pw.print(trainSet + "\t");
	    		}
	    		
	    		for (int j=0;j<desiredOutputArray[i].length;j++) {
	    			double outputSet = desiredOutputArray[i][j];
	    			pw.println(outputSet);
	    		}
	    	}
	    	pw.close();
	    }
		catch (IOException e) {
			System.out.println(e.toString());
		}
	}
	
	
	/**
	 * Creates a weight matrix of the Neural Network.
	 */	
	private Matrix initializeWeights(Matrix matrix, double value){
	    double temp[][] = new double[matrix.getM_rows()][matrix.getM_cols()];
	    for (int x = 0; x < matrix.getM_rows(); x++) {
	        for (int y = 0; y < matrix.getM_cols(); y++) {
	        	temp[x][y] = value;
	        }
	    }
	    matrix.setValue(temp);
	    return matrix;
	}
	
	/**
	 * Set the weights used in the Input-Hidden(IH) synapse.
	 */	
	public void setWeightsForIHsynapse(double value){
	    synapse_IH.setWeights(initializeWeights(synapse_IH.getWeights(),value));
	}
	
	/**
	 * Print the weights of the provided synapse.
	 */	
	private void printWeigtsForSynapse(FullSynapse synapse){
	    double[][] temp=synapse.getWeights().value;
	    for (int i=0;i<temp.length;i++) {
	    	for (int j=0;j<temp[i].length;j++) {
	    		System.out.print(temp[i][j] + "   ");
	    	}
	    	System.out.println();
	    }
	}
	
	/**
	 * Print the weights of the provided synapse in a file.
	 * 
	 * @param FullSynapse synapse 
	 * @param String weightsFile = the file where the weights are saved
	 */	
	private void printWeigtsForSynapse(FullSynapse synapse, String weightsFile){
	    double[][] temp=synapse.getWeights().value;
	    
	    try {
	    	/* prints the weights one by one in a file*/
	    	PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(weightsFile)));	
	    	for (int i=0;i<temp.length;i++) {
	    		for (int j=0;j<temp[i].length;j++) {
	    			double weight = temp[i][j];
	    			pw.print(weight + "\t");
	    		}
	    		pw.println();
	    	}
	    	pw.close();
	    }
		catch (IOException e) {
			System.out.println(e.toString());
		}
	}
	
	
	/**
	 * Transpose Matrix from [rows][cols] to [cols][rows]
	 */	

	private double [][] transposeMatrix(double [][] m){
		int r = m.length;
		int c = m[0].length;
		double [][] t = new double[c][r];
		for(int i = 0; i < r; ++i){
			for(int j = 0; j < c; ++j){
				t[j][i] = m[i][j];
			}
		}
		return t;
	}
	
	/**
	 * Concatenate 2 1Darrays
	 */	
	public double[] concat(double[] A, double[] B) {
		double[] C= new double[A.length+B.length];
		System.arraycopy(A, 0, C, 0, A.length);
		System.arraycopy(B, 0, C, A.length, B.length);
		return C;
	} 
	
	/**
	 * Concatenate 2 2Darrays
	 */	
	public double[][] concat(double[][] A2D, double[][]B2D) {
		double[][] C2D= new double[A2D.length][];
		for (int i= 0; i < A2D.length; i++){
			C2D[i]= concat(A2D[i],B2D[i]);
		}
		return C2D;
	}

	
	/**
	 * Return IH weights for HMM.
	 * 
	 * @param FullSynapse synapse
	 */	
	public double[][] getIHWeightsForHMM(){
	    double[][] bias      = transposeMatrix(synapse_BH.getWeights().value);
	    double[][] weights   = transposeMatrix(synapse_IH.getWeights().value);
	    double[][] IHweights = concat(bias,weights);
	    return IHweights;
	}
	
	/**
	 * Return HO weights for HMM.
	 * 
	 * @param FullSynapse synapse
	 */	
	public double[][] getHOWeightsForHMM(){
	    double[][] bias      = transposeMatrix(synapse_BO.getWeights().value);
	    double[][] weights   = transposeMatrix(synapse_HO.getWeights().value);
	    double[][] HOweights = concat(bias,weights);

	    return HOweights;
	}
	

	/**
	 * Print the weights of the provided synapse in a file NP-like.
	 * 
	 * @param FullSynapse synapse 
	 * @param String weightsFile = the file where the weights are saved

	 */	
	public void printWeightsForAllSynapse(String weightsFile){
	    double[][] temp1 = synapse_BH.getWeights().value;
	    double[][] temp2 = synapse_IH.getWeights().value;
	    double[][] temp3 = synapse_BO.getWeights().value;
	    double[][] temp4 = synapse_HO.getWeights().value;
	    
	    try {
	    	/* prints the weights one by one in a file*/
	    	PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(weightsFile)));	
	    	
	    	for (int i=0;i<temp1[0].length;i++) { // number of hidden layers
	    		//pw.print("hidden" + "\t" + "bias" + "\t" + "weight" + "\n");
	    		for (int j=0;j<temp1.length;j++) { // bias layer
	    			double weight = temp1[j][i];
	    			pw.print((i+1) + "\t" + j + "\t" + weight);
	    		}
	    		pw.println();
	    		//pw.print("hidden" + "\t" + "input" + "\t" + "weight" + "\n");
	    		for (int j=0;j<temp2.length;j++) { // number of input layers
	    			double weight = temp2[j][i];
	    			pw.print((i+1) + "\t" + (j+1) + "\t" + weight + "\n");
	    		}
	    	}
	    	
	    	//pw.print("output" + "\t" + "bias" + "\t" + "weight" + "\n");
	    	for (int i=0;i<temp3[0].length;i++) { // number of hidden layers
	    		for (int j=0;j<temp3.length;j++) { // bias layer
	    			double weight = temp3[j][i];
	    			pw.print((i+1) + "\t" + j + "\t" + weight);
	    		}
	    		pw.println();
	    	}
	    	
	    	//pw.print("output" + "\t" + "hidden" + "\t" + "weight" + "\n");
	    	for (int i=0;i<temp4[0].length;i++) { // number of hidden layers
	    		for (int j=0;j<temp4.length;j++) { // number of output layers
	    			double weight = temp4[j][i];
	    			pw.print((i+1) + "\t" + (j+1) + "\t" + weight + "\n");
	    		}
	    		pw.println();
	    	}
	    	
	    	pw.close();
	    }
		catch (IOException e) {
			System.out.println(e.toString());
		}
	}
	
	/**
	 * Take weights in an array.
	 */	
	private double[][] getSynapseWeightsInArray(FullSynapse synapse){
		return synapse.getWeights().value; // returns the synapse weights!!!
	}
	
	/**
	 * Take HO weights in an array.
	 */	
	public void getHOweightsInArray(){
		getSynapseWeightsInArray(synapse_HO);
	}
		
	
	/**
	 * Print the weights of the Bias-Hidden(BH) synapse.
	 */	
	public void printWeigtsForBHsynapse(){
		System.out.println("bias-hidden synapse weights");
		printWeigtsForSynapse(synapse_BH);
	}
	
	/**
	 * Print the weights of the Bias-Hidden(BH) synapse in a file.
	 * 
	 * @param String weightsFile = the file where the weights are saved
	 */	
	public void printWeigtsForBHsynapse(String weightsFile){
		printWeigtsForSynapse(synapse_BH, weightsFile);
	}	
	
	/**
	 * Print the weights of the Bias-Output(BO) synapse.
	 */	
	public void printWeigtsForBOsynapse(){
		System.out.println("bias-output synapse weights");
		printWeigtsForSynapse(synapse_BO);
	}
	
	/**
	 * Print the weights of the Bias-Output(BO) synapse in a file.
	 *
	 * @param String weightsFile = the file where the weights are saved
	 */	
	public void printWeigtsForBOsynapse(String weightsFile){
		printWeigtsForSynapse(synapse_BO, weightsFile);
	}	
	
	
	/**
	 * Print the weights of the Input-Hidden(IH) synapse.
	 */	
	public void printWeigtsForIHsynapse(){
		System.out.println("input-hidden synapse weights");
		printWeigtsForSynapse(synapse_IH);
	}
	
	/**
	 * Print the weights of the Input-Hidden(IH) synapse in a file.
	 * 
	 * @param String weightsFile = the file where the weights are saved
	 */	
	public void printWeigtsForIHsynapse(String weightsFile){
		printWeigtsForSynapse(synapse_IH, weightsFile);
		//System.out.println("input-hidden synapse weights saved in file " + weightsFile);
	}	
	
	/**
	 * Print the weights of the Hidden-Output(HO) synapse.
	 * 
	 * @author D.Sarantopoulou
	 */	
	public void printWeigtsForHOsynapse(){
		System.out.println("hidden-output synapse weights");
		printWeigtsForSynapse(synapse_HO);
	}
	
	/**
	 * Print the weights of the Hidden-Output(HO) synapse in a file.
	 * 
	 * @param String weightsFile = the file where the weights are saved
	 */	
	public void printWeigtsForHOsynapse(String weightsFile){
		printWeigtsForSynapse(synapse_HO, weightsFile);
		//System.out.println("HO synapse weights saved in file " + weightsFile + "\n\n");
	}
	
	
	
	/*################################## PRINTING OUTPUT  ###############################*/
	
	 /**
     * Prints the output of the NN vs the desired output in a file.
     */
    private void printPredictedVsDesiredOutput(String outputFile,double [][] inputSet, double [][] desiredSet){
    	nnet.getMonitor().setExporting(true);
		NeuralNet newNet = nnet.cloneNet();
		nnet.getMonitor().setExporting(false);
		
		Layer input  = newNet.getInputLayer();
		input.removeAllInputs();
		MemoryInputSynapse inSynapse=createInput(inputSet,1,inputSet[0].length);
		input.addInputSynapse(inSynapse);
		
		Layer output = newNet.getOutputLayer();
    	output.removeAllOutputs();
    	MemoryOutputSynapse outSynapse = new MemoryOutputSynapse();
    	output.addOutputSynapse(outSynapse);
    	
    	newNet.getMonitor().setTotCicles(1);
    	newNet.getMonitor().setLearning(false);
    	newNet.getMonitor().setTrainingPatterns(inputSet.length);
    	
    	newNet.go();
    	
    	try {
	    	// prints the outputs in a file 
	    	PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));	
	    	pw.println("Predicted\tDesired");
	    	for (int i=0; i < inputSet.length; i++) {
	           	double[] pattern = outSynapse.getNextPattern();
	        	pw.println(pattern[0] + "\t" + desiredSet[i][0]);
	        }
	    	pw.close();
	    }
		catch (IOException e) {
			System.out.println(e.toString());
		}
	
		newNet.stop();
    }
    
    /**
     * Prints the training predicted output vs desired one.
     * @param outputFile
     */
    public void printPredictedVsDesiredTrainingOutput(String outputFile){
    	if(doCrossValidation == true) {
    		printPredictedVsDesiredOutput(outputFile,cv.getInputTrainingSet(),cv.getDesiredOutputTrainingArray());
    	}
    	else {
    		printPredictedVsDesiredOutput(outputFile,inputArray,desiredOutputArray);
    	}
    }
    
    /**
     * Prints the validation predicted output vs desired one.
     * @param outputFile
     */
    public void printPredictedVsDesiredValidationOutput(String outputFile){
    	if(doCrossValidation == true) {
    		printPredictedVsDesiredOutput(outputFile,cv.getInputValidationSet(), cv.getDesiredOutputValidationArray());
    	}
    	else {
    		System.out.println("ERROR: No validation data set...");
    	}
    }
	

    /**
     * Returns the Global Error for the training patterns.
     */
    public double getGlobalErrorForTrainingData(){
    	double finalRMSE =0;
    	nnet.getMonitor().setExporting(true);
    	NeuralNet newNet = nnet.cloneNet();
    	nnet.getMonitor().setExporting(false);
    	newNet.getMonitor().setValidationPatterns(0);
    	newNet.getMonitor().setTrainingPatterns(0);
    	newNet.removeAllInputs();
    	newNet.removeAllOutputs();
    	newNet.removeAllListeners();
    	if(doCrossValidation == true) {
			finalRMSE = JooneTools.test(newNet, cv.getInputTrainingSet(), cv.getDesiredOutputTrainingArray());
    	}
    	else {
    		finalRMSE = JooneTools.test(newNet, inputArray, desiredOutputArray);
    	}
    	return finalRMSE;
    }
    
    /**
     * Returns the Global Error for the validation patterns.
     */
    public double getGlobalErrorForValidationData(){
    	double finalRMSE = 0;
    	if(doCrossValidation == true) {	
    		nnet.getMonitor().setExporting(true);
    		NeuralNet newNet = nnet.cloneNet();
    		nnet.getMonitor().setExporting(false);
    		finalRMSE = JooneTools.test(newNet, cv.getInputValidationSet(), cv.getDesiredOutputValidationArray());
    	}
    	return finalRMSE;
    }

    /** ############################## SYNAPSES ################################# **/                            
    
    /** Creates a BiasedInputSynapse */
	private MemoryInputSynapse createBiasedInput(int trainingExamplesNum, double value) {
		double[][] biasArray = new double [trainingExamplesNum][1];
		for (int i=0; i<biasArray.length; i++){
			biasArray[i][0] = value;
		}
		MemoryInputSynapse bias = new MemoryInputSynapse();		
		bias.setInputArray(biasArray);
		bias.setAdvancedColumnSelector("1");
        
		return bias;
	}
    
	/** Creates a FileInputSynapse */
	private MemoryInputSynapse createInput(double [][] dataArray, int firstCol, int lastCol) {
		MemoryInputSynapse input = new MemoryInputSynapse();
		String inputColumnSelectorString=createColumnSelectorString(firstCol,lastCol);
		
		input.setInputArray(dataArray);
		input.setAdvancedColumnSelector(inputColumnSelectorString);
        
		return input;
	}
	
	/* Creates a LearningSwitch and attach to it both the training and the desired input synapses */
	private LearningSwitch createSwitch(StreamInputSynapse IT, StreamInputSynapse IV) {
		LearningSwitch lsw = new LearningSwitch();
		lsw.addTrainingSet(IT);
		lsw.addValidationSet(IV);
		return lsw;
	}
	
	
	/**
	 * Initializes the neural network's parameters. 
	 * The layers (input, output, hidden).
	 * The dimensions of the layers (setRows).
	 * Defines synapses between layers, the input and 
	 * the output of the neural network, the trainer and 
	 * finally the neural network object (NeuralNet).
	 */
	protected void initNeuralNet() {

        // First create the three layers + 2 biased layers
		LinearLayer bias  		        = new LinearLayer();
        LinearLayer	input          		= new LinearLayer();
        //TanhLayer hidden            	= new TanhLayer();
        SigmoidLayerModified hidden     = new SigmoidLayerModified();
        //SigmoidLayer hidden   			= new SigmoidLayer();
        SigmoidLayer output         	= new SigmoidLayer();
        
        // set the dimensions of the layers
        bias.setRows(1);
        input.setRows(inputArray[0].length);
        hidden.setRows(hiddenNeuronsNum);
        output.setRows(outputNeuronsNum);
       
        // Connect the input layer with the hidden layer
        bias.addOutputSynapse(synapse_BH);
        bias.addOutputSynapse(synapse_BO);
        input.addOutputSynapse(synapse_IH);
        hidden.addInputSynapse(synapse_BH);
        hidden.addInputSynapse(synapse_IH);
        
        // Connect the hidden layer with the output layer
        hidden.addOutputSynapse(synapse_HO);
        output.addInputSynapse(synapse_BO);
        output.addInputSynapse(synapse_HO);

        // set the inputs and outputs appropriately
        if (doCrossValidation == true) {
        	biasInputSynapse			   = createBiasedInput(cv.getInputTrainingSet().length,1.0);
        	inputSynapse                   = createInput(cv.getInputTrainingSet(),1,cv.getInputTrainingSet()[0].length);
        	desiredOutputSynapse           = createInput(cv.getDesiredOutputTrainingArray(),1,1);
        	inputValidationSynapse         = createInput(cv.getInputValidationSet(),1,cv.getInputValidationSet()[0].length);
        	desiredValidationOutputSynapse = createInput(cv.getDesiredOutputValidationArray(),1,1);
        	nnet.getMonitor().setTrainingPatterns(cv.getInputTrainingSet().length);
        	nnet.getMonitor().setValidationPatterns(cv.getInputValidationSet().length);
        }
        else {
        	biasInputSynapse   = createBiasedInput(inputArray.length,1.0);
        	inputSynapse         =createInput(inputArray,1,inputArray[0].length);
        	desiredOutputSynapse =createInput(desiredOutputArray,1,1);
        	inputValidationSynapse = new MemoryInputSynapse();
        	desiredValidationOutputSynapse = new MemoryInputSynapse();
        	nnet.getMonitor().setTrainingPatterns(inputArray.length);
        } 
        
        // Creates and attach the input learning switch
        bias.addInputSynapse(biasInputSynapse);
        
        LearningSwitch Ilsw = createSwitch(inputSynapse, inputValidationSynapse);
        input.addInputSynapse(Ilsw);
       
        // Creates and attach the desired learning switch
        LearningSwitch Dlsw = createSwitch(desiredOutputSynapse, desiredValidationOutputSynapse);
        TeachingSynapse trainer = new TeachingSynapse(); // The teacher of the net
        trainer.setDesired(Dlsw);
        output.addOutputSynapse(trainer);
        
        // the output of the neural net
        outputSynapse = new MemoryOutputSynapse();
        output.addOutputSynapse(outputSynapse);
        
        nnet.addLayer(bias, NeuralNet.INPUT_LAYER);
        nnet.addLayer(input, NeuralNet.INPUT_LAYER);
         nnet.addLayer(hidden, NeuralNet.HIDDEN_LAYER);
        nnet.addLayer(output, NeuralNet.OUTPUT_LAYER);
        nnet.setTeacher(trainer);
    }
	
	/**
	 * Trains the neural network. 
	 * Sets the input and the desired output of the neural network.
	 * Gets the monitor object to train or feed forward. 
	 * Sets the monitor and RPROP parameters.
	 */
	public void train() {
		// get the monitor object to train or feed forward
        Monitor monitor = nnet.getMonitor();
        
        // set the monitor parameters
        /* RPROP needs a positive LR, because ExtendableLearner multiplies the gradient with the LR before passing
        it to the RpropExtender. RPROP only looks at the sign of the gradient so as long as the lR is positive, RPROP works correctly.*/
        monitor.setLearningRate(1); 
        
        monitor.setTotCicles(numberOfCycles);
        
        // RPROP parameters
        monitor.addLearner(0, "org.joone.engine.RpropLearner");
        monitor.setBatchSize(monitor.getTrainingPatterns());
        monitor.setLearningMode(0);
                
        monitor.setLearning(true);
        nnet.addNeuralNetListener(this);
                 
        nnet.go(false);
    }
    
    public void cicleTerminated(NeuralNetEvent e) {
    	Monitor mon = (Monitor)e.getSource();
    	// Prints out the current epoch and the training error
    	
    	
    	if (mon.getCurrentCicle() % 1 == 0 && mon.getCurrentCicle() != mon.getTotCicles() && doCrossValidation == true) {
    		System.out.print("Epoch: "+(mon.getTotCicles()-mon.getCurrentCicle()));
    		System.out.print("\tTraining Error:"+mon.getGlobalError());
    		// Creates a copy of the neural network
    		nnet.getMonitor().setExporting(true);
    		NeuralNet newNet = nnet.cloneNet();
    		nnet.getMonitor().setExporting(false);
    		// Cleans the old listeners
    		// This is a fundamental action to avoid that the validating net
    		// calls the cicleTerminated method of this class
    		newNet.removeAllListeners();
    		// Set all the parameters for the validation
    		NeuralNetValidator nnv = new NeuralNetValidator(newNet);
    		nnv.addValidationListener(this);
    		nnv.start(); // Validates the net
    	}
    }
    
    public void errorChanged(NeuralNetEvent e) {
        Monitor mon = (Monitor)e.getSource();
        if (mon.getCurrentCicle() % 1 == 0 && doCrossValidation == false) {
            System.out.println("Epoch: "+(mon.getTotCicles()-mon.getCurrentCicle())+" Global error:"+mon.getGlobalError());
    	}
      	
        //If the user has specified a target global error OR if this global error has been produced by the crossvalidation request to avoid over-training
        if (mon.getGlobalError() <= targetGE && nnet.isRunning()) {
        	nnet.stop();
        	mon.setExporting(true);
        	nnet = nnet.cloneNet();
    		mon.setExporting(false);
        }
        
      	//If the user has specified a minimum improvement in the global error between two contiguous epochs
      	if ( (Math.abs(mon.getGlobalError()-previousGlobalError) < minimumGEdifference) && targetGE==0) {
       		nnet.stop();
        }
      	
      	//Training stops when Global Error increases for two adjacent epochs
      	if ((mon.getGlobalError() > previousGlobalError) && (previousGlobalError > twotimespreviousGlobalError)){
       		nnet.stop();
        }
      	
      	twotimespreviousGlobalError = previousGlobalError;
      	previousGlobalError = mon.getGlobalError();
    }
    
    public void netStarted(NeuralNetEvent e) {
    }
    
    public void netStopped(NeuralNetEvent e) {
    	//  Monitor mon = (Monitor)e.getSource();
    	if (doCrossValidation == true) {
    		nnet = savedNN;
    	}
    }
    
    public void netStoppedError(NeuralNetEvent e, String error) {
    }
    
    public void netValidated(NeuralValidationEvent event) {
    	//Shows the RMSE at the end of the cycle
    	NeuralNet NN = (NeuralNet)event.getSource();
    	Monitor mon = NN.getMonitor();
    	
    	System.out.println("\tValidation Error: "+mon.getGlobalError());
    	if (mon.getGlobalError() < previousValidationGlobalError){
    		mon.setExporting(true);
    		savedNN = NN.cloneNet(); /* saves the NN which generalizes the best */
    		mon.setExporting(false);
    		previousValidationGlobalError = mon.getGlobalError();
    		cycles_after_min_validation_error = cycles_after_min_validation_error_default;
    	}
    	else {
    		cycles_after_min_validation_error--;
    	}
    	if (cycles_after_min_validation_error == 0) {
    		nnet.stop();
    	}
    }
}