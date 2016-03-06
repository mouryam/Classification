/**
 * Created by Mourya on 3/3/2016.
 */
import java.util.*;

public class C45 {

    private Tree root;
    private int classIndex;
    private List<char[]> trainData;
    private List<char[]> testData;

    public C45(List<char[]> data, List<char[]> testData, int classIndex) {
        this.classIndex = classIndex;
        this.trainData = data;
        this.testData = testData;
        root = new Tree();
        root.setRoot(trainData, classIndex);
        root.setType(Tree.Type.ROOT);
        buildTree();
    }

    // starts learning from root
    private void buildTree() {
        // begin at root tree
        buildTree(root);
    }

    // build tree from given node
    private void buildTree(Tree node) {
        if (node.getType() != Tree.Type.LEAF) {
            // get the splitting attribute
            int splitAttributeIndex = node.findSplittingAttribute();

            // use it to split the tree
            node.splitTree(splitAttributeIndex);
            // build childrens' trees
            for (Tree child : node.getChildren()) {
                buildTree(child);
            }
        }
    }

    // test each tuple in the test data against the tree
    public void test() {
        HashMap<char[], Character> classifiedTuples = new HashMap<>();
        int numberCorrect = 0, numberIncorrect = 0;

        for (char[] tuple : testData) {
            Tree node = root;
            char trueClassification = tuple[classIndex];
            char testClassification = classify(tuple, node);
            classifiedTuples.put(tuple,testClassification);

            if (testClassification == trueClassification) {
                numberCorrect++;
            }
        }

        Main.printOutput( classifiedTuples, numberCorrect, testData.size());
    }

    // classifies the given tuple based on decision tree path
    private char classify(char[] tuple, Tree node) {
        char testClassification;

        while (node.getType() != Tree.Type.LEAF) {
            List<Tree> children = node.getChildren();
            int index = node.getSplitAttribute();
            for (Tree child : children) {
                if (tuple[index] == child.getBranch()) {
                    node = child;
                    break;
                }
            }
        }

        testClassification = node.getLabel();
        return testClassification;
    }
}
