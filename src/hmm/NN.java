package hmm;

public abstract class NN {
    private double[][] wts12; // no of input, no of hidden (the other way round)
    private double[][] wts23; // no of hidden, no of output (the other way round)


    int len1;
    int len2;
    int len3;

    NN(double[][] w12, double[][] w23, int l1, int l2, int l3) {
        wts12 = w12;
        wts23 = w23;
        len1 = l1;
        len2 = l2;
        len3 = l3;
    }

    Activ calcValue(double[] il) {
        Activ act = new Activ(len1, len2, len3);
        act.layer1[0] = 1;

        for (int i1 = 1; i1 < len1 + 1; i1++) {
            act.layer1[i1] = il[i1 - 1];
        }

        act.layer2[0] = 1;
        for (int i2 = 1; i2 < len2 + 1; i2++) {
            for (int i1 = 0; i1 < len1 + 1; i1++) {
                //System.out.print( act.layer2[i2]+" + "+(act.layer1[i1]*wts12[i2-1][i1])+" = ");
                act.layer2[i2] += act.layer1[i1] * wts12[i2 - 1][i1];
            }

            act.layer2[i2] = 1 / (1 + Math.exp(-act.layer2[i2])) - 0.5;

            //New
            //act.layer2[i2] = ( 1 - Math.exp( -act.layer2[i2] ) ) / ( 1 + Math.exp( -act.layer2[i2] ) ) ;


        }

        for (int i3 = 0; i3 < len3; i3++) {
            for (int i2 = 0; i2 < len2 + 1; i2++)
                act.layer3[i3] += act.layer2[i2] * wts23[i3][i2];

            act.layer3[i3] = 1 / (1 + Math.exp(-act.layer3[i3]));
        }


        return act;
    }

    public String print() {
        String out = "";
        for (int i2 = 0; i2 < len2; i2++) {
            out += ("WTS12\t");
            for (int i1 = 0; i1 < len1 + 1; i1++)
                out += (wts12[i2][i1] + "\t");

            out += "\n";
        }

        for (int i3 = 0; i3 < len3; i3++) {
            out += ("WTS23\t");
            for (int i2 = 0; i2 < len2 + 1; i2++)
                out += (wts23[i3][i2] + "\t");

            out += "\n";
        }

        return out;
    }

    public double getWts23(int m, int n) {
        return wts23[m][n];
    }

}
 
