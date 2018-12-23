package org.joone.engine;

public class FreudRuleFullSynapse extends FullSynapse {

	private static final long serialVersionUID = 4391516546875376355L;

	public FreudRuleFullSynapse() {
		super();
	}
	protected void backward(double[] pattern) {
		int x;
		int y;
		double s, fr;
		int m_rows = getInputDimension();
		int m_cols = getOutputDimension();

		// Aggiustamento dei pesi
		for (x = 0; x < m_rows; ++x) {
			s = 0;
			fr = 1 - ((m_rows - x - 1) / m_rows);
			fr = fr * getLearningRate();
			setLearningRate(fr);
			for (y = 0; y < m_cols; ++y) {
				s += pattern[y] * array.value[x][y];
			}
			bouts[x] = s;
		}
		myLearner.requestWeightUpdate(pattern, inps);
	}
}