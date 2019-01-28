package hmm;

import java.util.*;
import java.lang.Math;

public class Encode {
    private String alphabet = "";
    private Map<String, String> symbols;
    private String[] oldSymbols;
    private String[] grpSymbols;
    private String[] grouping;
    private int pastObsNo = 1;
    private String firstObsSymbol = "A";
    private int dimension = 0;
    private int length = 0;

    //Start Position of Unicode Table (Decimal)
    private static int startPosition = 913;

    public Encode(int t, int encodeType) {
        String initialAlphabet = "ACDEFGHIKLMNPQRSTVWY";
        String encodeGrouping, groupSymbols;

        switch (encodeType) {
            case 1:
                encodeGrouping = "10001011011000000111";
                groupSymbols = "10";
                break;
            case 2:
                encodeGrouping = "24331212322444344211";
                groupSymbols = "1234";
                break;
            case 3:
                encodeGrouping = "12558187677424633788";
                groupSymbols = "12345678";
                break;
            case 4:
                encodeGrouping = "ACDEFGHIKLMNPQRSTVWY";
                groupSymbols = "ACDEFGHIKLMNPQRSTVWY";
                break;
            default:
                encodeGrouping = "10001011011000000111";
                groupSymbols = "10";
                break;
        }

        Run(t, initialAlphabet, encodeGrouping, groupSymbols);

    }

    public Encode(int t, String initialAlphabet, String encodeGrouping, String groupSymbols) {

        Run(t, initialAlphabet, encodeGrouping, groupSymbols);
    }

    public void Run(int t, String initialAlphabet, String encodeGrouping, String groupSymbols) {

        if (initialAlphabet.length() != encodeGrouping.length()) {
            System.err.println("ERROR initialAlphabet and encodeGrouping path differ.");
            System.exit(0);
        }

        this.symbols = new HashMap<String, String>();
        this.oldSymbols = initialAlphabet.split("");
        this.grouping = encodeGrouping.split("");
        this.grpSymbols = groupSymbols.split("");
        this.pastObsNo = t;

        // Dimension of additional matrices is (2^t-1) x Group Symbols Number
        // Adding a check for a limit on the number of priors
        this.dimension = grpSymbols.length * (int) Math.pow(2, pastObsNo - 1);
        this.length = initialAlphabet.length() * this.dimension;

        createAlphabet();

        //Output
        System.out.println("Using Encode Extension");
        System.out.println("New esym = " + this.alphabet);

    }

    public String transformSeq(String seq) {
        String newSeq = "";
        String oldSeq = seq;
        String key, prev;

        for (int i = 0; i < pastObsNo; i++) {
            oldSeq = firstObsSymbol.concat(oldSeq);
        }

        for (int i = pastObsNo; i < oldSeq.length(); i++) {
            key = "";
            for (int j = 0; j < pastObsNo; j++) {
                try {
                    prev = grouping[Arrays.asList(this.oldSymbols).indexOf(Character.toString(oldSeq.charAt(i - pastObsNo)))];
                    //System.out.println(i+" "+ Arrays.asList(oldSymbols).indexOf(Character.toString(oldSeq.charAt(i))));
                    key = key.concat(prev);
                } catch (Exception e) {
                    System.err.println("Symbol " + oldSeq.charAt(i - pastObsNo) + " in sequence " + seq
                            + "\n does not exist in the Initial Alphabet " + Arrays.toString(this.oldSymbols));
                    System.exit(0);
                }
            }
            //System.out.println(key);
            key = key.concat(Character.toString(oldSeq.charAt(i)));
            newSeq = newSeq.concat(getAlphabetSymbol(key));
        }

        return newSeq;
    }

    public double[] transformPrior(String prior, String key, String delim) {
        double[] list;
        double priorTemp = 0.0D;
        int listPosition = 0;

        StringTokenizer st = new StringTokenizer(prior, delim);
        int iter = st.countTokens();
        list = new double[iter * this.dimension];

        for (int i = 0; i < iter; i++) {
            try {
                priorTemp = Double.parseDouble(st.nextElement().toString());
            } catch (NumberFormatException e) {
                System.err.println("Invalid input for parameter " + key);
                System.exit(1);
            }

            for (int j = 0; j < this.dimension; j++) {
                list[listPosition] = (double) priorTemp / this.dimension;
                listPosition++;
            }
        }

        return list;

    }

    public String getAlphabet() {
        return this.alphabet;
    }

    public int getDimension() {
        return this.dimension;
    }

    public int getLength() {
        return this.length;
    }

    public int getUnicodeLength() {
        return this.startPosition + this.getLength();
    }

    private String getAlphabetSymbol(String key) {
        if (this.symbols.containsKey(key)) {
            return this.symbols.get(key);
        }

        return "";
    }

    private void createAlphabet() {
        //Find all possible combinations of groupSymbols
        //Check possible limitations
        String[] comb = Combinations(this.grpSymbols, pastObsNo);
        String key = "", val = "";
        int pos = this.startPosition;

        for (int i = 0; i < this.oldSymbols.length; i++) {
            for (int j = 0; j < comb.length; j++) {
                key = comb[j].concat(this.oldSymbols[i]);
                val = hexToChar("\\u0" + Integer.toHexString(pos));
                this.symbols.put(key, val);
                this.alphabet = this.alphabet.concat(val);
                pos++;
            }

        }

        Iterator keyIter = this.symbols.entrySet().iterator();

        while (keyIter.hasNext()) {
            Map.Entry i = (Map.Entry) keyIter.next();
        }

        //System.out.println(alphabet);

    }

    private String hexToChar(String hexCode) {
        char c = (char) Integer.parseInt(hexCode.substring(2), 16);

        return Character.toString(c);

    }

    public static String[] Combinations(String[] elements, int lengthOfList) {
        //initialize our returned list with the number of elements calculated above
        String[] allLists = new String[(int) Math.pow(elements.length, lengthOfList)];

        //lists of length 1 are just the original elements
        if (lengthOfList == 1) return elements;
        else {
            //the recursion--get all lists of length 3, length 2, all the way up to 1
            String[] allSublists = Combinations(elements, lengthOfList - 1);

            //append the sublists to each element
            int arrayIndex = 0;

            for (int i = 0; i < elements.length; i++) {
                for (int j = 0; j < allSublists.length; j++) {
                    //add the newly appended combination to the list
                    allLists[arrayIndex] = elements[i] + allSublists[j];
                    arrayIndex++;
                }
            }
            return allLists;
        }
    }

}