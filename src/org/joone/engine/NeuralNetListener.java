package org.joone.engine;

public interface NeuralNetListener extends java.util.EventListener
{
        void netStarted(NeuralNetEvent e);
	void cicleTerminated(NeuralNetEvent e);
	void netStopped(NeuralNetEvent e);
        void errorChanged(NeuralNetEvent e);
        void netStoppedError(NeuralNetEvent e,String error);
}