import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CoChangeDetector {

    public void findCoChanges(ChangeDetector cd) {
        ArrayList<CoChange> coChanges = new ArrayList<>();

        // cd contains `changeHistory`, which is a map between files and versions.
        // Find files with overlapping versions.
        Map<String, ArrayList<RevCommit>> fileChanges = cd.getChangeHistory();
        // We want an ordered collection to avoid duplicate co-changes.
        String[] keys = fileChanges.keySet().toArray(new String[0]);

        for (int i = 0; i < keys.length; i++) {
            for (int j = i+1; j < keys.length; j++) {
                ArrayList<RevCommit> fileChanges1 = fileChanges.get(keys[i]);
                ArrayList<RevCommit> fileChanges2 = fileChanges.get(keys[j]);
                ArrayList<Tuple<RevCommit>> intersection = relatedChanges(fileChanges1, fileChanges2);

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

    private ArrayList<Tuple<RevCommit>> relatedChanges(ArrayList<RevCommit> changes1, ArrayList<RevCommit> changes2) {
        // Stores commits that changed within the overlapping interval.
        ArrayList<Tuple<RevCommit>> relatedChanges = new ArrayList<>();
        // Make sure changes1 begins earlier than changes2 given that both are sorted ascending in time.
        if (changes1.get(0).getCommitterIdent().getWhen().getTime() > changes2.get(0).getCommitterIdent().getWhen().getTime()) {
            ArrayList<RevCommit> changes1old = changes1;
            changes1 = changes2;
            changes2 = changes1old;
        }

        for (RevCommit current1 : changes1) {
            Date date1 = current1.getCommitterIdent().getWhen();
            for (RevCommit current2 : changes2) {
                Date date2 = current2.getCommitterIdent().getWhen();
                if (date2.getTime() < date1.getTime()) {
                    continue; // Fast forward to only compare later commits.
                }
                // Check if these changes fall within the allowed interval
                long diffInMillies = Math.abs(date2.getTime() - date1.getTime());
                long daysDiff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                if (daysDiff <= ConfigurationManager.getMaxDaysBetweenCoChanges()) {
                    relatedChanges.add(new Tuple<>(current1, current2));
                }
            }
        }

        return relatedChanges;
    }
    /*
    private ArrayList<Tuple<RevCommit>> relatedChanges(ArrayList<RevCommit> changes1, ArrayList<RevCommit> changes2) {
        // Stores commits that changed within the overlapping interval.
        ArrayList<Tuple<RevCommit>> relatedChanges = new ArrayList<>();
        int count1 = changes1.size();
        int count2 = changes2.size();

        int index1 = 0;
        int index2 = 0;

        while (index1+1 < count1 || index2+1 < count2) {
            RevCommit current1 = changes1.get(index1);
            Date date1 = current1.getAuthorIdent().getWhen();
            RevCommit current2 = changes2.get(index2);
            Date date2 = current2.getAuthorIdent().getWhen();

            // Check if these changes fall within the allowed interval
            long diffInMillies = Math.abs(date2.getTime() - date1.getTime());
            long daysDiff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            if (daysDiff <= ConfigurationManager.getMaxDaysBetweenCoChanges()) {
                relatedChanges.add(new Tuple<>(current1, current2));
            }

            // Move up the index of the list of the element that occurred earlier.
            if ((date1.getTime() < date2.getTime() || (index2+1) == count2) && (index1+1) != count1) {
                index1++;
            } else if ((index2+1) != count2) {
                index2++;
            }
        }

        return relatedChanges;
    }
    */

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
