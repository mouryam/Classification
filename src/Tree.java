/**
 * Created by Mourya on 3/3/2016.
 */
import java.util.*;

public class Tree {
    private static final double DIVISOR = Math.log10(2);
    private static int classLabelIndex;
    private static int attributesNum;
    private static List<Integer> usedAttributes;
    private int totalAttributeSize;
    private static List<List<Character>> allAttributes;
    private double entropy;
    private int tuplesNum;
    private int splittingAttribute;
    private char branch;
    private char label;
    private List<char[]> trainData;
    private List<Tree> children;
    private Type type;

    public Tree() {
        tuplesNum = -1;
        entropy = -1;
        splittingAttribute = -1;
        trainData = new ArrayList<char[]>();
        children = new ArrayList<Tree>();
    }

    public char getLabel() {
        return label;
    }

    public int getSplitAttribute() {
        return splittingAttribute;
    }

    public List<Tree> getChildren() {
        return children;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    // returns the branch for this node, which is the outcome that led to this node
    public char getBranch() {
        return branch;
    }

    // set the branch for this node, which is the outcome that leads to this node
    public void setBranch(char branch) {
        this.branch = branch;
    }

    // sets the data for this node
    public void setTrainData(List<char[]> data) {
        this.trainData = data;
        attributesNum = this.trainData.get(0).length;
        tuplesNum = this.trainData.size();
        // calculate Info(D) of the class attribute for the following tree
        entropy = calcEntropy(this.trainData, classLabelIndex);

        if (allSameClass()) {
            type = Type.LEAF;
            label = trainData.get(0)[classLabelIndex];
        }
        else if (attributeListIsEmpty()) {
            type = Type.LEAF;
            label = getMajorityClass();
        }
    }

    // sets the initial dataset, should only be called by root
    public void setRoot(List<char[]> data, int indexOfClassLabel) {
        // fill out info for the current tree
        this.trainData = data;
        tuplesNum = this.trainData.size();
        attributesNum = this.trainData.get(0).length;
        classLabelIndex = indexOfClassLabel;

        // update usedAttribute list
        usedAttributes = new ArrayList<>();
        usedAttributes.add(classLabelIndex);

        // make a list of all attributes ROOT SPECIFIC
        allAttributes = new ArrayList<List<Character>>();
        allAttributes = uniqueAttributes(trainData);
        // only the root will have the total number of attributes
        totalAttributeSize = attributesNum;

        // calculate Info(D) of the class attribute for root (will include all the attributes)
        entropy = calcEntropy(trainData, classLabelIndex);
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

    /**
     *
     * @return returns the index of the attribute that has the highest gain
     */
    public int findSplittingAttribute() {
        int splitAttributeIndex = 0;
        double oldGainRatio = 0;

        for (int curAttribute = 0; curAttribute < attributesNum; curAttribute++) {
            // if curAttribute is the class or has been used to split already, then skip
            if (curAttribute == classLabelIndex ||
                    usedAttributes.contains(curAttribute)) {
                continue;
            }

            // gather unique attributes of the current main attribute
            List<Character> uniqueAttributes = allAttributes.get(curAttribute);
            List<char[]> uniqueSubset = new ArrayList<char[]>();

            double attributeEntropy = 0;

            // for each unique attribute's values:
            for (Character attribute : uniqueAttributes) {
                // create a subset to hold unique attribute's tuples
                List<char[]> attributeSubset = new ArrayList<char[]>();

                for (int i = 0; i < tuplesNum; i++) {
                    // if tuple contains the unique attribute, then add to its subsets
                    if (trainData.get(i)[curAttribute] == attribute) {
                        attributeSubset.add(trainData.get(i));
                        uniqueSubset.add(trainData.get(i));
                    }
                }
                int subsetSize = attributeSubset.size();
                double entropyOfUniqueSubset = calcEntropy(attributeSubset, classLabelIndex);
                double p = (double) subsetSize/tuplesNum;
                attributeEntropy = attributeEntropy + (p*entropyOfUniqueSubset);
            }

            double gain = entropy - attributeEntropy;

            // use splitInfo for C4.5 decision
            double splitInfo = calcEntropy(uniqueSubset, curAttribute);
            double gainRatio = gain/splitInfo;
            if (gainRatio > oldGainRatio) {
                oldGainRatio = gainRatio;
                // update indexOfAttribute to the higher gainRatio
                splitAttributeIndex = curAttribute;
            }
        }

        // assign splitting attribute for the current tree
        splittingAttribute = splitAttributeIndex;
        return splitAttributeIndex;
    }


    /**
     *
     * @param splitAttributeIndex
     * split tree based on given index
     */
    public void splitTree(int splitAttributeIndex) {
        // if its the end, then return
        if (type == Type.LEAF) {
            return;
        }
        List<Character> uniqueAttributes = allAttributes.get(splitAttributeIndex);
        // update usedAttributes
        usedAttributes.add(splitAttributeIndex);

        // gather all the tuples with the unique attribute and allocate them in a subData
        for (Character attribute : uniqueAttributes) {
            List<char[]> subData = new ArrayList<char[]>();
            for (char[] tuple : trainData) {
                // add if tuple contains the attribute
                if (tuple[splitAttributeIndex] == attribute) {
                    subData.add(tuple);
                }
            }
            // add child to each unique attribute
            Tree child = new Tree();
            children.add(child);
            child.setBranch(attribute);
            // if subData is empty then no more tuples, so set child as LEAF
            if (subData.size() == 0) {
                child.setType(Type.LEAF);
                continue;
            }
            else {
                child.setType(Type.INTERNAL);
            }
            // train the child tree with its own subData
            child.setTrainData(subData);
        }
    }

    /**
     *
     * @param subset of tuples for the current tree
     * @param indexOfAttribute to decide which tuples to only work with
     * @return entropy
     */
    private double calcEntropy(List<char[]> subset, int indexOfAttribute) {
        int subsetSize = subset.size();
        if (subsetSize == 0) {
            return 0;
        }
        double entropy = 0;

        // Determines which attribute to calculate entropy
        List<Character> uniqueAttributes = allAttributes.get(indexOfAttribute);

        // info(D) = -sumOf pLOG(p)
        for (Character attribute : uniqueAttributes) {
            int count = countAttributeInSubset(attribute, subset, indexOfAttribute);
            double p = (double)count/subsetSize;
            // subtract to entropy to gather total sum
            entropy = entropy - p * log2(p);
        }
        return entropy;
    }

    /**
     *
     * @param attribute selected attribute to check for
     * @param subset    subset of tuples given
     * @param i  index to look for, for the attribute
     * @return count of tuples with the attribute
     */
    private int countAttributeInSubset(char attribute, List<char[]> subset, int i) {
        int sum = 0;
        for (char[] tuple : subset) {
            if (tuple[i] == attribute) {
                sum++;
            }
        }
        return sum;
    }

    // check if tuples all belong to the same class
    public boolean allSameClass() {
        boolean sameClass = false;

        char value = trainData.get(0)[classLabelIndex];
        for (int i = 1; i < trainData.size(); i++) {
            if (trainData.get(i)[classLabelIndex] == value) {
                sameClass = true;
            } else {
                sameClass = false;
                break;
            }
        }
        return sameClass;
    }

    // check if attribute list is empty
    public boolean attributeListIsEmpty() {
        return usedAttributes.size() == totalAttributeSize;
    }

    // count the majority class label
    private char getMajorityClass() {
        List<Character> uniqueAttributes = allAttributes.get(classLabelIndex);
        Map<Integer, Character> attributePair = new HashMap<Integer, Character>();

        for (Character attribute : uniqueAttributes) {
            int count = countAttributeInSubset(attribute, trainData, classLabelIndex);
            attributePair.put(count,attribute);
        }
        int maxKey = Collections.max(attributePair.keySet());
        return attributePair.get(maxKey);
    }

    // custom log2 method; if 0, returns 0 rather than NaN
    private double log2(double value) {
        return value == 0 ? 0 : (Math.log10(value) / DIVISOR);
    }

    // node type
    public enum Type {
        INTERNAL, LEAF, ROOT
    }
}