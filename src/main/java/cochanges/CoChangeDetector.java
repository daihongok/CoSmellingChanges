package cochanges;

import com.google.gson.Gson;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import utility.Tuple;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CoChangeDetector {

    private ArrayList<ObjectId> commitsInOrder;

    public CoChangeDetector(){
        commitsInOrder = new ArrayList<>();
    }

    private ArrayList<CoChange> findCoChanges(ChangeDetector cd) {
        ArrayList<CoChange> coChanges = new ArrayList<>();

        // cd contains `changeHistory`, which is a map between files and versions.
        // Find files with overlapping versions.
        Map<String, FileChange> fileChanges = cd.getChangeHistory();
        // We want an ordered collection to avoid duplicate co-changes.
        String[] keys = fileChanges.keySet().toArray(new String[0]);

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
                    long daysDiff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                    if (daysDiff <= ConfigurationManager.getMaxDaysBetweenCoChanges() && getCommitDistance(current1, current2) <= ConfigurationManager.getMaxCommitsBetweenCommits()) {
                        // Make sure item1 always happened earlier than item2 for efficient duplicate filtering.
                        if (date2.getTime() > date1.getTime()) {
                            relatedChanges.add(new Tuple<>(current1, current2));
                        } else {
                            relatedChanges.add(new Tuple<>(current2, current1));
                        }
                    }
                }
            }
        }else{
            for (RevCommit current1 : changes1) {
                for (RevCommit current2 : changes2) {
                    if(current1.equals(current2)) {
                        relatedChanges.add(new Tuple<>(current1, current2));
                    }
                }
            }
        }

        return relatedChanges;
    }

    /**
     * Calculates the co-changes for a project.
     * @param repository
     * @param git
     * @return the co-changes.
     */
    public ArrayList<CoChange> getCoChanges(Repository repository, Git git) {
        ChangeDetector cd = new ChangeDetector();
        try {
            RevWalk walk = new RevWalk(repository);

            walk.markStart(walk.parseCommit(repository.resolve(ConfigurationManager.getLastCommit())));

            int commitsAnalyzed = 0;

            for (RevCommit currentCommit : walk) {
                // Stop when we hit the cap of commits to analyze.
                if (commitsAnalyzed == ConfigurationManager.getMaxAmountOfCommits()) {
                    break;
                }
                commitsInOrder.add(currentCommit.getId());

                if (currentCommit.getParentCount() == 0) { // Last commit has no parent.
                    continue;
                }

                var directParent = currentCommit.getParent(0);

                TreeWalk parentWalk = new TreeWalk(repository);
                parentWalk.setRecursive(true);
                parentWalk.reset(directParent.getTree());

                TreeWalk childWalk = new TreeWalk(repository);
                childWalk.setRecursive(true);
                childWalk.reset(currentCommit.getTree());

                HashSet<String> files = GetFiles(parentWalk, childWalk);
                cd.calculate(files, repository, directParent, currentCommit);

                commitsAnalyzed++;
                System.out.println(commitsAnalyzed);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return findCoChanges(cd);
    }

    /**
     * Helper method for finding the union of two file collections.
     * @param parentWalk
     * @param childWalk
     * @return
     */
    private static HashSet<String> GetFiles(TreeWalk parentWalk, TreeWalk childWalk) {

        HashSet<String> files = new HashSet<>();

        try {

            while (parentWalk.next()) {
                String path = parentWalk.getPathString();
                if (path.endsWith(".java")) {
                    files.add(path);
                }
            }
            while (childWalk.next()) {
                String path = childWalk.getPathString();
                if (path.endsWith(".java")) {
                    files.add(path);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return files;
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

        System.out.println(indexA + "," + indexB);
        if(aFound && bFound) {
            System.out.println(Math.abs(indexA - indexB) - 1);
            return Math.abs(indexA - indexB) - 1;
        }else{
            return Integer.MAX_VALUE;
        }
    }

    /**
     * Serializes co-changes to JSON and stores them in the given file.
     * @param filePath File to store JSON data in.
     */
    public void storeCoChanges(ArrayList<CoChange> coChanges, String filePath) {
        try {
            File coChangeFile = new File(filePath);
            coChangeFile.createNewFile();
            PrintWriter coChangeFilePrinter = new PrintWriter(coChangeFile);
            Gson gson = new Gson();
            coChangeFilePrinter.println(gson.toJson(coChanges));
        } catch (IOException e) {
            System.out.println("An error occurred while writing co-changes to a file.");
            e.printStackTrace();
        }
    }
}
