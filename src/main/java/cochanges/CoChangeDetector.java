package cochanges;

import Config.ConfigurationManager;
import Model.CoChange;
import Model.GitProject;
import Model.FileChange;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import utility.FileOperations;
import utility.Tuple;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CoChangeDetector {

    private ArrayList<ObjectId> commitsInOrder;

    private HashSet<String> changedFiles;

    public HashSet<String> getChangedFiles() {
        return changedFiles;
    }

    public CoChangeDetector(ArrayList<ObjectId> CommitsInOrder){
        commitsInOrder = CommitsInOrder;
        changedFiles = new HashSet<>();
    }

    public ArrayList<CoChange> findCoChanges(ChangeDetector cd) {
        ArrayList<CoChange> coChanges = new ArrayList<>();

        // cd contains `changeHistory`, which is a map between files and versions.
        // Find files with overlapping versions.
        Map<String, FileChange> fileChanges = cd.getChangeHistory();
        //Set to hashset
        changedFiles.addAll(fileChanges.keySet());
        // We want an ordered collection to avoid duplicate co-changes.
        String[] keys = changedFiles.toArray(new String[0]);

        for (int i = 0; i < keys.length; i++) {
            ArrayList<RevCommit> fileChanges1 = fileChanges.get(keys[i]).getCommits();

            for (int j = i+1; j < keys.length; j++) {
                ArrayList<RevCommit> fileChanges2 = fileChanges.get(keys[j]).getCommits();

                // Skip combinations that cant produce the threshold of overlapping changing
                if (fileChanges1.size()*fileChanges2.size() < ConfigurationManager.getCoChangeThreshold()) {
                    continue;
                }

                ArrayList<Tuple<RevCommit>> intersection = relatedChanges(fileChanges1, fileChanges2);

                // Apply the threshold to determine if these files are to be marked as co-changing.
                if (intersection.size() >= ConfigurationManager.getCoChangeThreshold()) {
                    // Co-change found!
                    coChanges.add(new CoChange(keys[i], keys[j], intersection));
                }
            }
        }
        return coChanges;
    }

    private ArrayList<Tuple<RevCommit>> relatedChanges(ArrayList<RevCommit> changes1, ArrayList<RevCommit> changes2) {
        // Stores commits that changed within the overlapping interval.
        ArrayList<Tuple<RevCommit>> relatedChanges = new ArrayList<>();

        if(ConfigurationManager.getConsiderCommitsOverTime()) {

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

                    // Check if these changes fall within the allowed interval
                    long diffInMillies = Math.abs(date2.getTime() - date1.getTime());
                    long hoursDiff = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                    if (hoursDiff <= ConfigurationManager.getMaxHoursBetweenCommits() && getCommitDistance(current1, current2) <= ConfigurationManager.getMaxCommitsBetweenCommits()) {
                        // Make sure item1 always happened earlier than item2 for efficient duplicate filtering.
                        if (date2.getTime() > date1.getTime()) {
                            addUniqueTuple(relatedChanges, new Tuple<>(current1, current2));
                        } else {
                            addUniqueTuple(relatedChanges, new Tuple<>(current2, current1));
                        }
                    }
                }
            }
        }else{
            for (RevCommit current1 : changes1) {
                for (RevCommit current2 : changes2) {
                    if(current1.equals(current2)) {
                        addUniqueTuple(relatedChanges, new Tuple<>(current1, current2));
                    }
                }
            }
        }

        return relatedChanges;
    }

    private void addUniqueTuple(ArrayList<Tuple<RevCommit>> tuplesList, Tuple tuple){
        Boolean isUnique = true;
        for(Tuple existingTuple : tuplesList) {
            if(existingTuple.getItem1().equals(tuple.getItem1()) &&
               existingTuple.getItem2().equals(tuple.getItem2())) {
                isUnique = false;
                break;
            }
        }
        if(isUnique){
            tuplesList.add(tuple);
        }
    }


    private int getCommitDistance(ObjectId commitA, ObjectId commitB){
        int indexA = 0, indexB = 0,index = 0;
        Boolean aFound = false, bFound = false;

        for(ObjectId commitId : commitsInOrder){
            if(commitId.equals(commitA)){
                indexA = index;
                aFound = true;
            }

            if(commitId.equals(commitB)){
                indexB = index;
                bFound = true;
            }

            if(aFound && bFound){
                break;
            }
            index++;
        }

        if(aFound && bFound) {
            return Math.abs(indexA - indexB) - 1;
        }else{
            return Integer.MAX_VALUE;
        }
    }

}
