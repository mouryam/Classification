My implementation of Classification uses 4 classes:

1) BAYESIAN: Takes training and test file and the index of the class label. It runs the algorithm once the test() is called and has set up its data. It returns a HashMap<char[], Character> classifiedTuples which is a hashmap of a pair of tuple and its classified class, number correct and the size of the testData set.

2) C45: Takes training and test file and the index of the class label. It runs the algorithm once the test() is called and has set up its data. It returns a HashMap<char[], Character> classifiedTuples which is a hashmap of a pair of tuple and its classified class, number correct and the size of the testData set.

3) TREE: Takes the converted data as a List<char[]> and the index of the classLabel. With this it creates the decision tree using the highest gainRatio as the splitting attribute measurement when builtTree is called. It structure of LEAFS as the end nodes, INTERNAL trees and the ROOT as the main splitting attribute in the beginning.

4) MAIN: Reads in command line arguments, runs the C4.5 or Bayes classifier, and outputs the accuracy and the classified classes to a text file.


To run the program, first compile the 4 classes, then run Main like so when in the src directory,
Make sure the training and test data are in src directory:

        $ javac Main.java
        $ javac Bayesian.java
        $ javac C45.java
        $ javac Tree.java
        $ java Main [-c/-b] [training_file] [test_data] [output_filename]


Classification Types:
C4.5    -c
Bayes   -b