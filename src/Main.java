import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;

import java.io.File;
import java.util.*;

public class Main {
    static String PROJECT_PATH = "projects/Material-Animations";
    static String PROJECT_GIT_FILE = PROJECT_PATH + "/.git";
    static boolean CLONE = false;

    public static void main(String[] args) {


        ChangeDetector cd = new ChangeDetector("change detector");
        Repository repository;
        System.out.println("Test");
        Git git;
        try {
            if (CLONE) {
                git = Git.cloneRepository()
                        .setURI("https://github.com/lgvalle/Material-Animations.git")
                        .setDirectory(new File(PROJECT_PATH))
                        .setBranch("refs/heads/master")
                        .call();
                repository = git.getRepository();
            } else {
                FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
                repository = repositoryBuilder.setGitDir(new File(PROJECT_GIT_FILE))
                        .readEnvironment() // scan environment GIT_* variables
                        .findGitDir() // scan up the file system tree
                        .setMustExist(true)
                        .build();
            }

            RevWalk walk = new RevWalk(repository);

            walk.markStart(walk.parseCommit(repository.resolve("HEAD")));
            //walk.sort(RevSort.REVERSE);

            RevCommit child = null;
            boolean first = true;
            for(RevCommit parent : walk) {
                if (first) {
                    first = false;
                } else {

                    TreeWalk parentWalk = new TreeWalk(repository);
                    parentWalk.setRecursive(true);
                    parentWalk.reset(parent.getTree());

                    TreeWalk childWalk = new TreeWalk(repository);
                    childWalk.setRecursive(true);
                    childWalk.reset(child.getTree());

                    HashSet<String> files = GetFiles(parentWalk, childWalk);

                    for (String path : files) {
                        if (path.endsWith(".java")) {
                            cd.calculate(path, repository, parent.getId(),child.getId());
                        }
                    }

                }

                child = parent;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        var x = 5;
        findCoChanges(cd);
    }

    private static HashSet<String> GetFiles(TreeWalk parentWalk, TreeWalk childWalk) {

        HashSet<String> files = new HashSet<>();

        try {

            while (parentWalk.next()) {
                String path = parentWalk.getPathString();
                files.add(path);
            }
            while (childWalk.next()) {
                String path = childWalk.getPathString();
                files.add(path);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return files;
    }

    private static void findCoChanges(ChangeDetector cd) {
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
                if (intersection.size() > 0) {
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

    public static <T> List<T> intersection(List<T> list1, List<T> list2) {
        List<T> list = new ArrayList<T>();

        for (T t : list1) {
            if(list2.contains(t)) {
                list.add(t);
            }
        }

        return list;
    }
}