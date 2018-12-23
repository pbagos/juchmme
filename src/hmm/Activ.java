package hmm;

class Activ
{
    double[] layer1;
    double[] layer2;
    double[] layer3;
	 
    Activ(int len1, int len2, int len3)
	{
	    layer1=new double[len1+1];
		layer2=new double[len2+1];
		layer3=new double[len3];

	 }
}
