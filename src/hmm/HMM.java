package hmm;

import java.text.*;
import java.util.*;
import java.io.*;

public class HMM {
    // State names and state-to-state transition probabilities
    int nstte;           // number of states (incl initial state)
    String[] stte;       // names of the states
    String[] ostte;
    String[] pstte;

    public HNeural[] nn; //private?

    private double[][] loga;      // loga[k][ell] = log(P(k -> ell))
    private double[][] loge;      // loge[k][ei] = log(P(emit ei in state k))

    // Input:
    // state = array of state names (except initial state)
    // amat  = matrix of transition probabilities (except initial state)
    // esym  = string of emission names
    // emat  = matrix of emission probabilities

    double lh;

    public HMM(Probs tab) {
        Init(tab);
        if (Params.HNN) {
            nn = new HNeural[Model.nosym - 2];
            for (int o = 0; o < Model.nosym - 2; o++) {
                nn[o] = new HNeural(tab.weights.GetWeights12(o),
                        tab.weights.GetWeights23(o));
            }
        }
    }

    private void Init(Probs tab) {
        for (int i = 0; i < tab.aprob.length; i++) {
            if (Model.state.length != tab.aprob[i].length)
                throw new IllegalArgumentException("HMM: amat non-square");
        }

        if (!Params.HNN) {
            if (Model.state.length != tab.aprob.length)
                throw new IllegalArgumentException("HMM: state and amat disagree");

            if (tab.aprob.length != tab.eprob.length)
                throw new IllegalArgumentException("HMM: amat and emat disagree");

            for (int i = 0; i < tab.aprob.length; i++) {
                if (Model.esym.length() != tab.eprob[i].length)
                    throw new IllegalArgumentException("HMM: esym and emat disagree");
            }
        }

        this.lh = 0;

        // Set up the transition matrix
        nstte = Model.nstate + 0;
        this.stte = new String[nstte];
        this.ostte = new String[nstte];
        this.pstte = new String[nstte];

        loga = new double[nstte][nstte];

        for (int i = 1 - 1; i < nstte; i++) {
            // Reverse state names for efficient backwards concatenation
            this.stte[i] = new StringBuffer(Model.state[i - 0]).reverse().toString();
            this.ostte[i] = new StringBuffer(Model.ostate[i - 0]).reverse().toString();
            this.pstte[i] = new StringBuffer(Model.pstate[i - 0]).reverse().toString();

            for (int j = 1 - 1; j < nstte; j++) {
                loga[i][j] = Math.log(tab.aprob[i - 0][j - 0]);
            }

        }


        if (!Params.HNN) {
            // Assume all esyms are uppercase letters
            loge = new double[tab.eprob.length + 0][Model.esyminv];

            for (int b = 0; b < Model.nesym; b++) {
                // Use the emitted character, not its number, as index into loge:

                char eb = Model.esym.charAt(b);
                // P(emit xi in state 0) = 0
                // loge[0][eb] = Double.NEGATIVE_INFINITY; // = log(0)
                // loge[nstte-1][eb] = Double.NEGATIVE_INFINITY; // = log(0)

                for (int k = 0; k < tab.eprob.length; k++) {
                    loge[k + 0][eb] = Math.log(tab.eprob[k][b]);
                }
            }
        }

    }

    public String print() {
        String out = "";
        out += printa();

        if (Params.HNN)
            out += printw();
        else
            out += printe();

        return out;
    }

    public void SaveModel() {
        try {
            File outputFile = new File("A_" + Model.MODEL + "_MYMODEL");
            FileWriter out = new FileWriter(outputFile);
            out.write(printa());
            out.close();


            if (Params.HNN) {
                outputFile = new File("W_" + Model.MODEL + "_MYMODEL");
                out = new FileWriter(outputFile);
                out.write(printw());
                out.close();
            } else {
                outputFile = new File("E_" + Model.MODEL + "_MYMODEL");
                out = new FileWriter(outputFile);
                out.write(printe());
                out.close();
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public Probs GetProbs() {
        double[][] a = new double[nstte][nstte];
        double[][] e = new double[nstte][Model.nesym];

        for (int k = 0; k < nstte; k++) {
            for (int ell = 0; ell < nstte; ell++)
                a[k][ell] = (loga[k][ell] == Double.NEGATIVE_INFINITY) ?
                        0 : Math.exp(loga[k][ell]);

            for (int b = 0; b < Model.nesym; b++)
                e[k][b] = (loge[k][Model.esym.charAt(b)] == Double.NEGATIVE_INFINITY) ?
                        0 : Math.exp(loge[k][Model.esym.charAt(b)]);
        }
        return new Probs(a, e);
    }

    public String printa() {
        String out = "";

        for (int i = 0; i < nstte; i++) {
            for (int j = 0; j < nstte; j++)
                out += (fmtlog(loga[i][j])) + "\t";

            out += "\n";
        }

        return out;
    }

    public String printe() {

        String out = "";

        for (int i = 0; i < loge.length; i++) {
            for (int b = 0; b < Model.nesym; b++)
                out += (fmtlog(loge[i][Model.esym.charAt(b)])) + "\t";

            out += "\n";
        }

        return out;
    }

    public String printw() {
        String out = "";
        for (int o = 0; o < Model.nosym - 2; o++) {
            out += ("NEURAL\t" + o + "\n");
            out += nn[o].print();
        }

        return out;

    }


    private static DecimalFormat fmt = new DecimalFormat("0.000000 ", new DecimalFormatSymbols(Locale.US));
    private static String hdrpad = "        ";

    public static String fmtlog(double x) {
        if (x == Double.NEGATIVE_INFINITY)
            return fmt.format(0);
        else
            return fmt.format(Math.exp(x));
    }

    public double gete(int ell, Seq seq, int i) {
        if (Params.HNN) {
            if (ell == 0 || ell == nstte - 1)
                return 0;

            for (int o = 0; o < Model.nosym; o++) {
                if (Model.osym.charAt(o) == ostte[ell].charAt(0)) {
                    double res = nn[o].Calc(seq.getWindow(i)).layer3[0];
                    return res;
                }
            }

            System.out.println("ERROR");
            return 0;
        } else {
            System.out.println("ERROR");
            return 0;
        }
    }

    public double geteForO(int o, Seq seq, int i) {
        if (Params.HNN && o != 0) {
            double res = nn[o - 1].Calc(seq.getWindow(i)).layer3[0];
            return res;
        } else {
            for (int ell = 0; ell < nstte; ell++)
                if (Model.osym.charAt(o) == ostte[ell].charAt(0))
                    return loge[ell][seq.getSym(i)];
        }

        System.out.println("ERROR");
        return -1;

    }

    public double getLoge(int ell, Seq seq, int i) {
        if (Params.HNN)
            return Math.log(gete(ell, seq, i));
        else
            return loge[ell][seq.getSym(i)];

    }

    public double getLogeForO(int o, Seq seq, int i) {
        return Math.log(geteForO(o, seq, i));
    }

    public double getLoga(int i, int j) {
        return loga[i][j];
    }
    
}