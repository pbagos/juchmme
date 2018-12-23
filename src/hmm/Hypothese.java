package hmm;

class Hypothese
{
	public int[] lab;
	public double[] gamma;
	public double[] gamma0;
	private int len;
	private int nstate;

	Hypothese( int i, int j )
	{
		len    = i;
		nstate = j;
		lab    = new int[len];
		gamma  = new double[nstate];
		gamma0 = new double[nstate];
	}
	
	Hypothese copy()
	{
		Hypothese cp=new Hypothese( len, nstate );
		for( int i=1; i<len; i++ )
			cp.lab[i]=lab[i];

		for( int j=0; j<nstate; j++ )
			cp.gamma0[j]=gamma[j];

		return cp;
	}

}
