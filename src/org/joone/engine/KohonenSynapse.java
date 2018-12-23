package org.joone.engine;

import java.util.TreeSet;

/** <P>This is an unsupervised Kohonen Synapse which is a Self Organising Map.</P>
 * <P>This KohonenSynapse works in conjunction with the next layer which should
 * implement a SOM strategy such as a GuassianLayer or WTALayer (Winner Takes All).  This synapse
 * should connect to one of these layers, without a SOM Strategy in the next layer
 * this component will not function correctly.</P>
 * <P>This KohonenSynapse takes a pattern from the previous layer, calculates the
 * distance between the input vector and the weights and passes this on to the next
 * layer.  In the learning phase the next layer should calculate the distance fall
 * off between the winner and all other nodes (1.0 being the closest distance and
 * 0.0 being furthest away and not being considered near to the winner).  These
 * distances are passed back to this KohonenSynapse and used to adjust the
 * weights.</P>
 * <P>The weights are adjusted based on the current learning rate and distance fall off.</P>
 * <P>At each epoch/cycel the learning rate is adjusted in the following way ...
 * </P>
 * <P>If the current cycle is within the ordering phase then the learning rate is set to </P>
 * <P> User setup learning rate * exp(-(double)(Current Cycle/Time Constant)).</P>
 */
public class KohonenSynapse extends FullSynapse implements NeuralNetListener {
    
    private static final long serialVersionUID = -4966435217407942471L;
    
    double currentLearningRate = 1;
    private double timeConstant = 200.0;
    private int orderingPhase = 1000;
    
    /** <P>The default constructor for the KohonenSynapse class.</P> */    
    public KohonenSynapse() {
        super();
        learnable = false;
    }
    
    /** <P>Adjusts the weights of this Kohonen Synapse according to the neighborhood fall off distance calculated by the next
     * layer.</P>
     * @param pattern The pattern with the distance fall off's between the winner and all other nodes.
     * (1.0 is the winner through 0.0 having no similarity to the original input
     * vector)
     */
    protected void backward(double[] pattern) {
        // Adjust weights
        //        double [][] weights = array.getValue();
        double dFalloff = 0;
        int num_outs = this.getOutputDimension();
        double[] o_pattern = b_pattern.getOutArray();
        // Loop through the map and adjust the weights of each neighborhood output.
        for (int x=0;x<num_outs;x++) {
            dFalloff = o_pattern[x];
            adjustNodeWeight(x, currentLearningRate, dFalloff, inps);
        }
        
    }
    
    /** </P>Fowards the euclidean distance squared between the input vector and the weight vector to the next
     * layer.  If the learning phase is currently active then the next layer should
     * process this and pass back the distance fall off between the winning output and
     * all other outputs.</P>
     * @param pattern The pattern containg the euclidean distance squared between each weight and the
     * input.
     */
    protected void forward(double[] pattern) {
        double temp = 0f;
        double curDist = 0f;
        int num_outs = this.getOutputDimension();
        for (int x=0;x<num_outs;x++)    // Loop through outputs
        {
            curDist = 0f;
            for (int inputs=0;inputs<pattern.length;inputs++){
                temp =  array.value[inputs][x] - pattern[inputs];
                temp *= temp;
                curDist += temp;
            }
            outs[x] = curDist; // Output = distance between input and weights.
        }
    }
    
    /* -- Map Size -- */
    
    /* -- Generic Functions -- */
    
    /**
     * Adjusts the weights for the node located at x,y,z using the given distance and learning rate.
     */
    private void adjustNodeWeight(int curnode, double learningRate, double distanceFalloff,double[] pattern) {
        double wt, vw;
        
        int output = curnode;
        for (int w=0; w < getInputDimension(); w++) {
            wt = array.value[w][output];
            vw = pattern[w];
            wt += distanceFalloff * learningRate * (vw - wt);
            array.value[w][output] = wt;
        }
    }
    
    /* -- Net Listener Methods -- */
    
    /** Sets the Monitor object of the synapse
     * @param newMonitor neural.engine.Monitor
     */
    public void setMonitor(Monitor newMonitor) {
        super.setMonitor(newMonitor);
        if (getMonitor() != null) {
            getMonitor().addNeuralNetListener(this,false);
        }
    }
    
    /** <P>Changes the learning rate for this synapse depending in the current epoch number.
     * The learning rate is changed in the following way ... </P>
     * <P>User setup learning rate * exp(-(double)(Current Cycle/Time Constant)).</P>
     * @param e The original Net Event.
     */
    public void cicleTerminated(NeuralNetEvent e) {
        int currentCycle = getMonitor().getTotCicles() - getMonitor().getCurrentCicle();
        if (currentCycle < getOrderingPhase())
            // This method will start at the user defined learning rate then reduce exponentially.
            currentLearningRate = getMonitor().getLearningRate() * Math.exp(-(currentCycle/getTimeConstant()));
        else
            currentLearningRate = 0.01;
    }
    
    /** Not implemented.
     * @param e The original Net Event.
     */
    public void errorChanged(NeuralNetEvent e) {
    }
    
    /** Initialises any shape sizes such as circular radius and time constant before possible training.
     * @param e The original Net Event.
     */
    public void netStarted(NeuralNetEvent e) {
        currentLearningRate = getMonitor().getLearningRate();
    }
    
    /** Not implemented.
     * @param e The original Net Event.
     */
    public void netStopped(NeuralNetEvent e) {
    }
    
    /** Not implemented.
     * @param e The original Net Event.
     * @param error The error that caused this NetStoppedError event.
     */
    public void netStoppedError(NeuralNetEvent e, String error) {
    }
    
    /** <P>Check that there are no errors or problems with the properties of this
     * KohonenSynapse.</P>
     * @return The TreeSet of errors / problems if any.
     */
    public TreeSet check() {
        TreeSet checks = super.check();
        return checks;
    }
        
    /** Getter for property orderingPhase.
     * @return Value of property orderingPhase.
     *
     */
    public int getOrderingPhase() {
        return orderingPhase;
    }
    
    /** Setter for property orderingPhase.
     * @param orderingPhase New value of property orderingPhase.
     *
     */
    public void setOrderingPhase(int orderingPhase) {
        this.orderingPhase = orderingPhase;
    }
    
    /** Getter for property timeConstant.
     * @return Value of property timeConstant.
     *
     */
    public double getTimeConstant() {
        return timeConstant;
    }
    
    /** Setter for property timeConstant.
     * @param timeConstant New value of property timeConstant.
     *
     */
    public void setTimeConstant(double timeConstant) {
        this.timeConstant = timeConstant;
    }
    
    /** @deprecated - Used only for backward compatibility
     */
    public Learner getLearner() {
        learnable = false;
        return super.getLearner();
    }
    
    private void readObject(java.io.ObjectInputStream in)
    throws java.io.IOException, java.lang.ClassNotFoundException {
        in.defaultReadObject();
        if (getMonitor()!=null) {
            getMonitor().addNeuralNetListener(this, false); // Add this synapse as a net listener.
        }
    }
    
}