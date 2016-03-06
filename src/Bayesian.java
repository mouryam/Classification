/**
 * Created by Mourya on 3/3/2016.
 */
import java.util.*;

public class Bayesian {

    private List<char[]> trainData;
    private List<char[]> testData;
    List<List<Character>> allAttributes;
    private Map<Character, List<char[]>> classSubsets;
    private Map<Character, Double> classProbabilities;
    private int classIndex;
    private int attributesNum;

    public Bayesian(List<char[]> data, List<char[]> testData, int indexOfClassLabel) {
        trainData = new ArrayList<char[]>();
        allAttributes = new ArrayList<List<Character>>();
        classSubsets = new HashMap<Character, List<char[]>>();
        classProbabilities = new HashMap<Character, Double>();

        this.trainData = data;
        this.testData = testData;
        this.classIndex = indexOfClassLabel;
        attributesNum = data.get(0).length;
        allAttributes = uniqueAttributes(trainData);
        findClassSubsets();
    }


    // tests each tuple in testData
    public void test() {
        HashMap<char[], Character> classifiedTuples = new HashMap<char[], Character>();
        List<Character> classLabels = allAttributes.get(classIndex);
        int correct = 0;

        for (char[] tuple : testData) {
            char trueClassification = tuple[classIndex];
            char testClassification = '\0';
            double oldProb = 0.0;

            for (Character classLabel : classLabels) {
                List<char[]> subset = classSubsets.get(classLabel);
                double priorProb = classProbabilities.get(classLabel);
                double condProb = 1.0;

                for (int indexOfAttribute = 0; indexOfAttribute < attributesNum; indexOfAttribute++) {
                    if (indexOfAttribute == classIndex) {
                        continue;
                    }
                    int count = countAttribute(tuple[indexOfAttribute], subset, indexOfAttribute);
                    condProb *= (double) count / subset.size();
                }
                double newProb = condProb * priorProb;
                if (newProb > oldProb) {
                    oldProb = newProb;
                    testClassification = classLabel;
                }
            }

            classifiedTuples.put(tuple,testClassification);
            if (trueClassification == testClassification) {
                correct++;
            }
        }
        Main.printOutput(classifiedTuples, correct, testData.size());
    }

    // splits up the data so that each unique class's subset is already there
    private void findClassSubsets() {
        List<Character> uniqueAttributes = allAttributes.get(classIndex);
        double probability;
        for (Character attribute : uniqueAttributes) {
            List<char[]> subset = subsetContains(attribute, classIndex);
            probability = (double) subset.size() / trainData.size();
            classSubsets.put(attribute, subset);
            classProbabilities.put(attribute, probability);
        }
    }

    // returns a subset that has a certain attribute value
    private List<char[]> subsetContains(char attribute, int indexOfAttribute) {
        List<char[]> subset = new ArrayList<char[]>();

        for (char[] tuple : trainData) {
            if (tuple[indexOfAttribute] == attribute) {
                subset.add(tuple);
            }
        }

        return subset;
    }

    // count attributes in subset of tuples
    private int countAttribute(char attribute, List<char[]> subset, int indexOfAttribute) {
        int sum = 0;
        for (char[] tuple : subset) {
            if (tuple[indexOfAttribute] == attribute) {
                sum++;
            }
        }
        return sum;
    }

    // finds all unique values for each attribute
    private List<List<Character>> uniqueAttributes(List<char[]> trainData) {
        List<List<Character>> uniqueList = new ArrayList<List<Character>>();

        // create empty lists in uniqueList to fill out
        for (int i = 0; i < attributesNum; i++) {
            List<Character> x = new ArrayList<Character>();
            uniqueList.add(x);
        }
        // go through each tuple and add the unique chars
        for (char[] tuple : trainData) {
            for (int i = 0; i < attributesNum; i++) {
                // if char is not there then add
                if (!uniqueList.get(i).contains(tuple[i])) {
                    uniqueList.get(i).add(tuple[i]);
                }
            }
        }

        return uniqueList;
    }
}
