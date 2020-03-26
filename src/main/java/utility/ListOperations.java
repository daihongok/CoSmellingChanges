package utility;

import java.util.ArrayList;
import java.util.HashSet;

public class ListOperations {
    /**
     * Gets all unique tuples (a,b) where a < b.
     * If (a,b) is present, (b,a) is not.
     * @param set Set of values to create pairs out of.
     * @return A set of unique pairs.
     */
    public static ArrayList<Tuple<String>> getUniquePairs(HashSet<String> set) {
        ArrayList<Tuple<String>> tuples = new ArrayList<>();
        String[] elements = set.toArray(String[]::new);
        for (int i = 0; i < elements.length; i++) {
            for (int j = i+1; j < elements.length; j++) {
                tuples.add(new Tuple<>(elements[i], elements[j]));
            }
        }
        return tuples;
    }
}
