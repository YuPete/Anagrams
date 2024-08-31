import java.io.*;
import java.util.Iterator;
import java.util.Arrays;

/**
 * Class that finds the most number of anagrams in a given text file
 * @author Peter Yu 
 * @UNI hy2846
 */
public class MostAnagramsFinder {

    public static void main(String[] args) {
       try{
        // Step 1: Prepare Command Line argument for parseArgs method
        String [] result = parseArgs(args);
        File f = new File(result[0]);
        
        // Step 2: Makes proper map structure with instruction from parseArgs
        String structure = result[1];
        MyMap<String, MyList<String>> map = createMap(structure);
        MyList<MyList<String>> maxAna = findAnagrams(f, map);
        sortAnagrams(maxAna);
        insSort(maxAna);

        // Step 3: Prints the results and catches/ deasl with various possible exceptions
        printResults(maxAna);
    } catch (FileNotFoundException fnf){
        System.err.println(fnf.getMessage());
        System.exit(1);
    } catch (IllegalArgumentException e){
        System.err.println(e.getMessage());
        System.exit(1);
    } catch(IOException ioe){
        ioe.printStackTrace();
    }
    }
    /** 
     * Processes and parses command line input from user
     * 
     * This Method first checks if format is correct, providing correct format if not. 
     * Then it creates a new file with the user supplied name, displaying error message
     * if name does not refer to existing file or if it's a directory
     * Finally, method parses second command argument user supplies,
     *  displaying error message if invalid. 
     * 
     * @param args The string array that contains user input
     * @return A string array with filtered user input 
     * @throws IllegalArgumentException If incorrect format or input for map type is invalid
     * @throws FileNotFoundException If supplied file does not exist or is a directory  
    */
    private static String[] parseArgs(String[] args) throws FileNotFoundException {
        if (args.length != 2) {
           throw new IllegalArgumentException
                ("Usage: java MostAnagramsFinder <dictionary file> <bst|rbt|hash>");
        }

        File f = new File(args[0]);
        //consulted https://docs.oracle.com/javase/8/docs/api/java/io/File.html for file methods
        if (!f.exists() || f.isDirectory()) {
            throw new FileNotFoundException
                ("Error: Cannot open file '" + args[0] + "' for input.");
        }
        String structure = args[1];
        if (!structure.equals("bst") && 
            !structure.equals("rbt") &&
            !structure.equals("hash")) {
                throw new IllegalArgumentException
                    ("Error: Invalid data structure '" + args[1] + "' received.");
        }

        return new String[]{args[0], structure};
    }

    /**
     * Creates a map based on the specified type
     * 
     * Method switches constructor types to allow for interface implementation
     * of map constructor in main method. Null return technically unreachable
     * because input is filtered.
     * 
     * @param structure The type of map to create. Expected inputs are "bst", "rbt", or "hash" 
     * @return Blank map object with String keys and list values or null if input is invalid (unreachable)
     */

    private static MyMap<String, MyList<String>> createMap(String structure) {
        switch (structure) {
            case "bst":
                return new BSTreeMap<>();
            case "rbt":
                return new RBTreeMap<>();
            case "hash":
                return new MyHashMap<>();
            default:
                return null;
        }
    }


    /**
     * Processes provided file and identifies the group(s) of word(s) with the most anagrams
     * 
     * Method reads words from specified file, sorts each word's letter, and uses sorted 
     * version as key in their storage. The "values" stored in map is a list of words that 
     * are anagrams of each other. Simultaneously, method keeps track of groups with the most
     * number of anagrams in a list, returning this list upon completion.   
     * 
     * @param f The file with the words to process
     * @param map The Map that stores the lists of anagrams, using their sorted letters as keys
     * @return A list of lists, which contains the group(s) of words with the most anagrams, with
     * each group being words that are anagrams of each other. 
     * @throws IOException If an IO error occurs when reader reads the file
     */

    private static MyList<MyList<String>> findAnagrams
        (File f, MyMap<String, MyList<String>> map) throws IOException{
 
            MyList<MyList<String>> maxAna = new MyLinkedList<>();
            int currentMax = 0;
            BufferedReader input = null;

                input = new BufferedReader(new FileReader(f));
                String line;
                while ((line = input.readLine()) != null) {
                    String sorted = insSort(line);
                    MyList<String> words = map.get(sorted);
                    if (words == null) {
                        words = new MyLinkedList<>();
                        map.put(sorted, words);
                    }
                    words.add(line);
                    int currentCount = words.size();
                    if (currentCount > currentMax) {
                        maxAna.clear();
                        maxAna.add(words);
                        currentMax = currentCount;
                    } else if (currentCount == currentMax) {
                        maxAna.add(words);
                    }
                } 
            input.close();
            return maxAna;
        }
    /**
     * Sorts each group internally and returns the sorted list.
     * 
     * Method iterates through each group in the provided list,
     * sorts each group internally using the `sortList` method, 
     * and then adds the sorted groups to a new list.
     * 
     * @param maxAna The list of lists of anagram groups to be sorted
     * @return A new list of lists with the groups sorted internally
     */
        private static MyList<MyList<String>> sortAnagrams(MyList<MyList<String>> maxAna){
            Iterator<MyList<String>> i = maxAna.iterator();
            MyList<MyList<String>> temp = new MyLinkedList<>();
            while (i.hasNext()){
                MyList<String> group = i.next();
                sortList(group);
                temp.add(group);
            }
            return temp; 
        }
        /**
     * Sorts the anagram groups by their first word lexicographically.
     * 
     * The insertion sort algorithm sorts the provided list of lists of strings
     * based on the first word of each inner list lexicographically.
     * 
     * @param maxAna The list of lists of strings to be sorted
     */
        public static void insSort(MyList<MyList<String>> maxAna) {
            for (int i = 1; i < maxAna.size(); i++) {
                MyList<String> key = maxAna.get(i);
                String keyFirstWord = key.get(0);
                int j = i - 1;
                while (j >= 0 && maxAna.get(j).get(0).compareTo(keyFirstWord) > 0) {
                    maxAna.set(j + 1, maxAna.get(j));
                    j--;
                }
                maxAna.set(j + 1, key);
            }
        }

    /**
     * Sorts each group internally lexicographically.
     * 
     * Method converts the provided list of strings into an array,
     * sorts the array, and then places the sorted elements back
     * into the list. This achieves an internal lexicographic sorting
     * of the group.
     * 
     * @param list The list of strings to be sorted
     */
        private static void sortList(MyList<String> list){ //sorts each group internally lexicographically
            String [] array = new String[list.size()];
            for (int i = 0; i<array.length; i++){
                array[i] = list.get(i);
            }
            Arrays.sort(array); 
            list.clear();
            for (String element : array){
                list.add(element);
            }

        }
    /**
     * Prints the group(s) with the most anagrams from maxAna list
     * 
     * Method first prints out the number of group(s) with the most amount of anagrams,
     * then it displays the number of anagrams in each group. Finally, it iterates through
     * the maxAna list, which contains lists of words that have the most amount of anagrams, 
     * and prints them out, formatting them to specification. Utilizes StringBuilder to 
     * build each group, resetting after each group is finished. 
     * 
     * 
     * @param maxAna The linked list with the group(s) of the word(s) with the most anagrams
     */

    private static void printResults(MyList<MyList<String>> maxAna) {
    int currentMax = 0;
    if (maxAna.isEmpty()) {
        System.out.println("No anagrams found.");
    } else{
        currentMax = maxAna.get(0).size();
    }

    System.out.println("Groups: " + maxAna.size() + ", Anagram count: " + currentMax);
    Iterator<MyList<String>> itr = maxAna.iterator();
    StringBuilder b = new StringBuilder();

    while (itr.hasNext()) {
        MyList<String> group = itr.next();
        b.append("[");
        Iterator<String> individualItr = group.iterator();
        while (individualItr.hasNext()) {
            b.append(individualItr.next());
            if (individualItr.hasNext()) {
                b.append(", ");
            } else {
                b.append("]");
            }
        }
        System.out.println(b.toString());
        b.setLength(0); // resetting StringBuilder for each group
    }
}
    /**
     * Performs insertion sort algorithm on given string
     * 
     * Method converts given string's characters to lower case 
     * and inserts each character into an char array. Then it sorts said array
     * with the insertion sort algorithm, which was adapted from the general approach 
     * discussed on GeeksforGeeks(https://www.geeksforgeeks.org/insertion-sort/)
     *  
     * @param s The string that will be sorted
     * @return A new string with characters sorted in alphabetical order and lower cased
     */
    public static String insSort(String s) {

        //String Methods: https://docs.oracle.com/javase/8/docs/api/java/lang/String.html
        char[] temp = s.toLowerCase().toCharArray();
        for (int i = 1; i < temp.length; i++) {
            char key = temp[i];
            int j = i - 1;
            while (j >= 0 && temp[j] > key) {
                temp[j + 1] = temp[j];
                j--;
            }
            temp[j + 1] = key;
        }
        return new String(temp);
    }
}


