package org.joone.engine;

import java.util.TreeSet;
import org.joone.net.NetCheck;

/** This Synapse connects the N input neurons with the M output neurons
 * using a matrix of FIRFilter elements of size NxM.
 * A FIRFilter connection is a delayed connection that permits to implement
 * a temporal backprop alg. functionally equivalent to the TDNN (Time Delay
 * Neural Network), but in a more efficient and elegant manner.
 * 
 * @see org.joone.engine.FIRFilter
 * @author P. Marrone
 */
public class DelaySynapse extends Synapse {
	protected FIRFilter[][] fir;
	private int taps;

	private static final long serialVersionUID = 8268129000639124340L;

	public DelaySynapse() {
		super();
	}
	public void addNoise(double amplitude) {
		int x;
		int y;
		int m_cols = getOutputDimension();
		int m_rows = getInputDimension();
		for (y = 0; y < m_cols; ++y)
			for (x = 0; x < m_rows; ++x)
				fir[x][y].addNoise(amplitude);
	}
	protected void backward(double[] pattern) {
		int x;
		int y;
		double s;
		int m_rows = getInputDimension();
		int m_cols = getOutputDimension();
		setLearningRate(getMonitor().getLearningRate());
		// Aggiustamento dei pesi
		for (x = 0; x < m_rows; ++x) {
			s = 0;
			for (y = 0; y < m_cols; ++y) {
				//debug(array.value[x][y], "matrix[" + x + "][" + y + "]");
				fir[x][y].lrate = getLearningRate();
				fir[x][y].momentum = getMomentum();
				s += fir[x][y].backward(pattern[y]);
			}
			bouts[x] = s;
		}
	}
	protected void forward(double[] pattern) {
		int x;
		int y;
		double s;
		int m_rows = getInputDimension();
		int m_cols = getOutputDimension();
		//debug(pattern, "FS1:forward");

		for (y = 0; y < m_cols; ++y) {
			s = 0;
			for (x = 0; x < m_rows; ++x) {
				//debug(array.value[x][y], "matrix[" + x + "][" + y + "]");
				s += fir[x][y].forward(pattern[x]);
			}
			outs[y] = s;
		}
		//debug(outs, "FS2:forward");
	}
	/**
	 * Inserire qui la descrizione del metodo.
	 * Data di creazione: (10/04/00 23.02.20)
	 * @return int
	 */
	public int getTaps() {
		return taps;
	}
	/**
	 * setArrays method comment.
	 */
	protected void setArrays(int rows, int cols) {
		inps = new double[rows];
		outs = new double[cols];
		bouts = new double[rows];
	}
	protected void setDimensions(int rows, int cols) {
		int icols, irows;
		int x, y;
		int m_rows = getInputDimension();
		int m_cols = getOutputDimension();

		if (rows == -1)
			irows = m_rows;
		else
			irows = rows;

		if (cols == -1)
			icols = m_cols;
		else
			icols = cols;
		fir = new FIRFilter[irows][icols];
		for (x = 0; x < irows; ++x)
			for (y = 0; y < icols; ++y) {
				fir[x][y] = new FIRFilter(getTaps());
			}
		setArrays(irows, icols);
	}
	/**
	 * Inserire qui la descrizione del metodo.
	 * Data di creazione: (10/04/00 23.02.20)
	 * @param newTaps int
	 */
	public void setTaps(int newTaps) {
		taps = newTaps;
		this.setDimensions(-1, -1);
	}

	public TreeSet check() {
		TreeSet checks = super.check();
		if (getTaps() == 0) {
			checks.add(
				new NetCheck(
					NetCheck.FATAL,
					"The Taps parameter cannot be equal to zero.",
					this));
		}

		// Return check messages
		return checks;
	}
}