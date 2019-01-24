# juchmme
Java Utility for Class Hidden Markov Models and Extensions

JUCHMME, an acronym for Java Utility for Class Hidden Markov Models and Extensions is a tool developed for biological sequence analysis.
The overall aim of this work has been to develop a software tool of capable of offering a large collection of standard algorithms for Hidden Markov Models (HMMs) as well as a number of extensions and to evaluate this model on various biological problems. The JUCHMME framework is characterized by:
Flexibility: Ease of use and customization for various problems. The user can create models of any architecture, any alphabet (DNA, protein or other), all without requiring programming capabilities (settings will be made through a configuration file).

Training methods: JUCHMME integrates a wide range of training algorithms for HMM for labeled sequences. This kind of models are often called “class HMMs” and are commonly trained by the Maximum Likelihood (ML) criterion to model within-class data distributions. The tool has been developed to support the Baum-Welch algorithm [1-3] and its extension necessary to handle labeled data [4]. Other alternatives are also supported, namely the gradient-descent algorithm proposed by Baldi and Chauvin [5] and the Viterbi training (or else “segmental k-means”) [6]. Additionally, the Conditional Maximum Likelihood (CML) criterion, which corresponds to discriminative training, is also supported. The CML training can be performed only with gradient based algorithms, and to this end a fast and robust algorithm for individual learning rate adaptation has been implemented [7]. The same algorithm is available for training the Hidden Neural Networks (HNN, see below).

Decoding: It integrates a wide range of decoding algorithms such as Viterbi, N–Best [8], posterior–Viterbi [9] and Optimal Accuracy Posterior Decoder [10]. Moreover, decoding of partially labeled data is offered with all algorithms in order to allow incorporation of experimental information [11].

Training Procedures: It contains built-in model creation and evaluation procedures, such as options for independent test, self-consistency test, jacknife test, k–fold cross-validation and early stopping. All the prediction algorithms also incorporate the corresponding reliability measures that have been proposed [12] (correlation coefficient, Q, SOV).

HMM Extensions: To overcome HMM limitations, a number of extensions have been developed or developed such as segmental k–means both for Maximum Likelihood (ML) and for Conditional Maximum Likelihood (CML) [6], Hidden Neural Networks [13], models that condition on previous observations [14] and a method for semi-supervised learning of HMMs that can incorporate labeled, unlabeled and partially-labeled data (semi–supervised learning) [15].

1.	Baum, L.E., An equality and associated maximization technique in statistical estimation for probabilistic functions of Markov processes. Inequalities, 1972. 3: p. 1-8.
2.	Durbin, R., et al., Biological sequence analysis: probabilistic models of proteins and nucleic acids. 1998: Cambridge university press.
3.	Rabiner, L.R., A tutorial on hidden Markov models and selected applications in speech recognition. Proceedings of the IEEE, 1989. 77(2): p. 257-286.
4.	Dempster, A.P., N.M. Laird, and D.B. Rubin, Maximum likelihood from incomplete data via the EM algorithm. Journal of the royal statistical society. Series B (methodological), 1977: p. 1-38.
5.	Baldi, P. and Y. Chauvin, Smooth on-line learning algorithms for hidden Markov models. Neural Computation, 1994. 6(2): p. 307-318.
6.	Juang, B.-H. and L.R. Rabiner, The segmental K-means algorithm for estimating parameters of hidden Markov models. IEEE Transactions on acoustics, speech, and signal Processing, 1990. 38(9): p. 1639-1641.
7.	Bagos, P.G., T.D. Liakopoulos, and S.J. Hamodrakas. Faster gradient descent training of hidden Markov models, using individual learning rate adaptation. in International Colloquium on Grammatical Inference. 2004. Springer.
8.	Krogh, A., Two methods for improving performance of an HMM and their application for gene finding. Center for Biological Sequence Analysis. Phone, 1997. 45: p. 4525.
9.	Fariselli, P., P.L. Martelli, and R. Casadio, A new decoding algorithm for hidden Markov models improves the prediction of the topology of all-beta membrane proteins. BMC bioinformatics, 2005. 6(4): p. S12.
10.	Käll, L., A. Krogh, and E.L. Sonnhammer, An HMM posterior decoder for sequence feature prediction that includes homology information. Bioinformatics, 2005. 21(suppl_1): p. i251-i257.
11.	Bagos, P.G., T.D. Liakopoulos, and S.J. Hamodrakas, Algorithms for incorporating prior topological information in HMMs: application to transmembrane proteins. BMC bioinformatics, 2006. 7(1): p. 189.
12.	Bagos, P., et al., Prediction of signal peptides in archaea. Protein Engineering Design and Selection, 2009. 22(1): p. 27-35.
13.	Krogh, A. and S.K. Riis, Hidden neural networks. Neural Computation, 1999. 11(2): p. 541-563.
14.	Tamposis, I.A., et al., Extending Hidden Markov Models to Allow Conditioning on Previous Observations. Journal of Bioinformatics and Computational Biology, 2018.
15.	Tamposis, I.A., et al., Semi-supervised learning of Hidden Markov Models for biological sequence analysis. Bioinformatics, 2018: p. bty910-bty910.

##Getting started 

JUCHMME is an executable file in Java that is executed from the command line. JUCHMME is written in Java and requires a 32-bit or 64-bit Java runtime environment version 7 or later, freely available from http://www.java.org. The Windows and MacOS X installers contain a suitable Java runtime environment that will be used if a suitable Java runtime environment cannot be found on the computer.
Download the program from http://www.compgen.org/tools/juchmme or Github https://github.com/pbagos/juchmme. 

##Compile 

'javac -XDignore.symbol.file -sourcepath src/ -d ./bin src/hmm/juchmme.java'
'javac -XDignore.symbol.file -sourcepath src/ -d ./bin src/hmm/RandomSeq.java'
'javac -XDignore.symbol.file -sourcepath src/ -d ./bin src/nn/Main.java'

##Command Line

The juchmme program is controlled by a list of command-line argument options. The following options control this:
-V: print JUCHMME version and exit
-a: the free emission parameter file. This parameter file is required.
-e: the free transition parameter file
-i: the input sequence three-line file. This file stores the input sequences for decoding or training algorithms in a three-line format. 
-f: the input sequence FASTA file. This file stores the input sequences for decoding algorithms in a fasta format. 
-m: the model file. This parameter file is required.
-x: the HNN encoding file
-t: Training option
-c: the configuration file
-v 10: k–fold cross-validation mode using an integer larger than 0 for k (for instance k=10)
-s: self-consistency test
-j: jacknife test
-p: show plot
-P: graph plot Directory

