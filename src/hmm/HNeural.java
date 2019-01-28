package hmm;

public class HNeural extends NN {

    //Activ act;

    public HNeural(double[][] w12, double[][] w23) {
        super(w12, w23,
                Params.window * NNEncode.encode[0].length,
                Params.nhidden, 1);
    }

    public static double[] CalcIn(char[] inp) {
        int c = 0;
        double[] il = new double[Params.window * NNEncode.encode[0].length];

        for (int i = 0; i < inp.length; i++) {
            boolean found = false;
            for (int j = 0; j < Model.esym.length(); j++)
                if (inp[i] == Model.esym.charAt(j)) {
                    for (int k = 0; k < NNEncode.encode[j].length; k++)
                        il[c++] = NNEncode.encode[j][k];

                    found = true;
                    break;
                }

            if (!found)
                for (int k = 0; k < NNEncode.encode[0].length; k++)
                    il[c++] = 0.0D;
        }
        return il;
    }


    public Activ Calc(char[] inp) {
        double[] il = CalcIn(inp);
        Activ act = calcValue(il);
        return act;
    }

}
 
