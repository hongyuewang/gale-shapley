/**
 * Name: Hong Yue Wang
 * Student ID: 300105373
 */

import java.util.Scanner;
import java.util.Stack;
import java.io.*;

@SuppressWarnings("rawtypes")
public class GaleShapley {
    private static int numberOfMatches;
    private static String[] empList;
    private static String[] studList;
    private static Entry[][] m;

    private static int[] employers;
    private static int[] students;

    private static Stack<Integer> Sue;
    private static int[][] A;
    private static HeapPriorityQueue[] PQ;

    public static void initialize(String filename) throws IOException {
        FileReader f = new FileReader(filename);
        BufferedReader br = new BufferedReader(f);
        numberOfMatches = Integer.parseInt(br.readLine());
        employers = new int[numberOfMatches];
        students = new int[numberOfMatches];

        for (int i = 0; i < numberOfMatches; i++) {
            employers[i] = -1;
            students[i] = -1;
        }

        empList = new String[numberOfMatches];

        // Initialize stack
        Sue = new Stack<Integer>();
        // Initialize 2D array
        A = new int[numberOfMatches][numberOfMatches];
        //Initialize the PQ of (employer ranking, student)
        PQ = new HeapPriorityQueue[numberOfMatches];

        for (int i = 0; i < numberOfMatches; i++) {
            empList[i] = br.readLine();
            // Push employers to stack
            Sue.push(i);
        }

        studList = new String[numberOfMatches];
        for (int i = 0; i < numberOfMatches; i++) {
            studList[i] = br.readLine();
        }

        m = new Entry[numberOfMatches][numberOfMatches];
        for (int i = 0; i < numberOfMatches; i++) { // for each student
            PQ[i] = new HeapPriorityQueue();
            String[] pairStringArray = br.readLine().split(" ");
            for (int j = 0; j < numberOfMatches; j++) { // for each employer
                 String[] keyValueString = pairStringArray[j].split(",");
                 int k = Integer.parseInt(keyValueString[0]);
                 int v = Integer.parseInt(keyValueString[1]);
                m[i][j] = new Entry(k, v);

                // Adding entries to A[s][e]
                A[j][i] = (int) m[i][j].getValue();

                // Adding entries to PQ[i]
                PQ[i].insert(m[i][j].getKey(), j);
            }
        }
    }

    public static void execute() {
        while (!Sue.empty()) {
            int e = Sue.pop();
            int s = (int) PQ[e].removeMin().getValue(); // The student the employer wants most

            int ePrime = students[s];
            if (students[s] == -1) { // If the student is unmatched
                students[s] = e;
                employers[e] = s; // Match student and employer
            } else if (A[s][e] < A[s][ePrime]) { // If s prefers e to ePrime
                students[s] = e;
                employers[e] = s; // Replace the match
                employers[ePrime] = -1; // Previous employer is now unmatched
                Sue.push(ePrime);
            } else { // s rejects offer from e
                Sue.push(e);
            }
        }
    }

    public static void save(String filename) {
        try {
            File file = new File(filename);
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
            } else {
                System.out.println("File already exists.");
            }
            try {
                FileWriter writer = new FileWriter(filename);
                for (int i = 0; i < numberOfMatches; i++) {
                    writer.write("Match " + i + ": " + empList[i] + " - " + studList[employers[i]] +"\n");
                }
                writer.close();
            } catch (IOException e) {
                System.out.println("An error occurred while writing to file.");
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.out.println("An error occurred while creating new file.");
            e.printStackTrace();
        }


    }

    public static void main(String[] args) throws IOException {
        String fn ="";

        try {
            fn = args[0];
        } catch(Exception e) {
            if(args.length != 0)
                System.out.println("Invalid arguments on command line");
                Scanner fnScanner = new Scanner(System.in);
                System.out.println("Enter the file name (filename.txt):");
                fn = fnScanner.nextLine().trim();
        }
        initialize(fn);
        execute();
        save("matches_" + fn);
    }
}
