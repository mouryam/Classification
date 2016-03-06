/**
 * Created by Mourya on 3/3/2016.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;


public class Main {

    public static List<char[]> trainingData;
    public static List<char[]> testData;
    public static Bayesian bayes;
    public static C45 c45;
    public static String[] args;

    public static void main(String[] args) throws IOException {
        Main.args = args;
        if (args.length == 4) {
            start(args);
        }
        else{
            System.out.println("Usage: \n\tjava Main [-c/-b] [training_file] [test_data] [output_filename]");
            System.exit(-1);
        }

    }

    public static void start(String[] args) throws FileNotFoundException {
        trainingData = convertData(args[1]);
        testData = convertData(args[2]);
        switch (args[0]) {
            case "-b":
                bayes = new Bayesian(trainingData, testData, 0);
                System.out.println("Testing using Bayes Theorem...");
                bayes.test();
                break;
            case "-c":
                c45 = new C45(trainingData, testData, 0);
                System.out.println("Testing using C4.5...");
                c45.test();
                break;
            default:
                System.out.println("Invalid option \n Valid options: -c , -b");
                System.exit(-1);
        }
    }


    /**
     * returns converted data to List<char[]> form
     */
    public static List<char[]> convertData(String fileName) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(fileName));
        StringTokenizer line;
        List<char[]> data;
        data = new ArrayList<char[]>();
        do {
            try {
                line = new StringTokenizer(scanner.nextLine());
            } catch (NoSuchElementException e) {
                break;
            }
            int numberOfAttributes = line.countTokens();
            char[] tuple = new char[numberOfAttributes];

            for (int i = 0; i < numberOfAttributes; i++) {
                String value = line.nextToken();
                assert value.length() == 1;
                tuple[i] = value.charAt(0);
            }
            data.add(tuple);
        }
        while (true);

        if (data.size() == 0) {
            System.out.println("File Error");
            System.exit(-1);
        }
        return data;
    }

    public static void printOutput(HashMap<char[], Character> classifiedTuples, int numberCorrect, int size) {
        //Print test data results to output file */
        File f = new File(args[3]);
        try {
            PrintWriter pw = new PrintWriter(f);
            double accuracy = ((double)numberCorrect /size)*100;
            pw.println("Accuracy: " +accuracy+ "%");

            for( char[] tuple : classifiedTuples.keySet()){
                Character value = classifiedTuples.get(tuple);
                pw.println(Arrays.toString(tuple) + " Class: " +value);
            }
            pw.close();
        }
        catch (FileNotFoundException ex) {
            System.out.println(ex);
        }
        System.out.println("DONE");
        System.exit(0);
    }
}
