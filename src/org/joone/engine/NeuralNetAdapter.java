package org.joone.engine;

public class NeuralNetAdapter implements NeuralNetListener
{
	public void cicleTerminated(NeuralNetEvent e) {}
	public void netStopped(NeuralNetEvent e) {}
        
        public void netStarted(NeuralNetEvent e) {
        }
        
        public void errorChanged(NeuralNetEvent e) {
        }
        
        public void netStoppedError(NeuralNetEvent e,String error){
        }
}