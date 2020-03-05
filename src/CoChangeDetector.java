import org.eclipse.jgit.lib.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CoChangeDetector {

    public void findCoChanges(ChangeDetector cd) {
        ArrayList<CoChange> coChanges = new ArrayList<>();

        // cd contains `changeHistory`, which is a map between files and versions.
        // Find files with overlapping versions.
        Map<String, ArrayList<ObjectId>> fileChanges = cd.getChangeHistory();
        // We want an ordered collection to avoid duplicate co-changes.
        String[] keys = fileChanges.keySet().toArray(new String[0]);

        for (int i = 0; i < keys.length; i++) {
            for (int j = i+1; j < keys.length; j++) {
                ArrayList<ObjectId> fileChanges1 = fileChanges.get(keys[i]);
                ArrayList<ObjectId> fileChanges2 = fileChanges.get(keys[j]);
                List<ObjectId> intersection = intersection(fileChanges1, fileChanges2);

                // Apply the threshold to determine if these files are to be marked as co-changing.
                if (intersection.size() >= ConfigurationManager.getCoChangeThreshold()) {
                    // Co-change found!
                    coChanges.add(new CoChange(keys[i], keys[j], intersection));
                }
            }
        }
        // Print results
        for (CoChange c : coChanges) {
            System.out.println(c.toString());
        }
    }

    private <T> List<T> intersection(List<T> list1, List<T> list2) {
        List<T> list = new ArrayList<T>();

        for (T t : list1) {
            if(list2.contains(t)) {
                list.add(t);
            }
        }

        return list;
    }
}
