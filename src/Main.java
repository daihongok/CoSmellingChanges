import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.TreeFilter;

import java.io.File;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        ChangeDetector cd = new ChangeDetector();
        Repository repository;
        Git git;
        long startTime = 0;

        try {
            File projectPath = new File(ConfigurationManager.getProjectsDirectory() + "/" + ConfigurationManager.getProjectName());

            if (projectPath.exists()) {
                FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
                repository = repositoryBuilder.setGitDir(new File(projectPath + "/.git"))
                        .readEnvironment() // scan environment GIT_* variables
                        .findGitDir() // scan up the file system tree
                        .setMustExist(true)
                        .build();
            } else { // Clone and load
                git = Git.cloneRepository()
                        .setURI("https://github.com/"+ConfigurationManager.getProjectOwner()+"/"+ConfigurationManager.getProjectName()+".git")
                        .setDirectory(projectPath)
                        .setBranch(ConfigurationManager.getProjectBranch())
                        .call();
                repository = git.getRepository();
            }

            /*
            * Start timing the program.
            */
            startTime = System.currentTimeMillis();

            RevWalk walk = new RevWalk(repository);

            walk.markStart(walk.parseCommit(repository.resolve("HEAD")));

            RevCommit child = null;
            boolean first = true;
            int commitsAnalyzed = 0;

            for(RevCommit parent : walk) {
                // Stop when we hit the cap of commits to analyze.
                if (commitsAnalyzed == ConfigurationManager.getMaxAmountOfCommits()) {
                    break;
                }

                if (first) {
                    first = false;
                } else {
                    //if(child.getParent(0).equals(parent.getId())) {
                        System.out.println(parent.getAuthorIdent().getWhen());
                        TreeWalk parentWalk = new TreeWalk(repository);
                        parentWalk.setRecursive(true);
                        parentWalk.reset(parent.getTree());

                        TreeWalk childWalk = new TreeWalk(repository);
                        childWalk.setRecursive(true);
                        childWalk.reset(child.getTree());
                        HashSet<String> files = GetFiles(parentWalk, childWalk);
                        cd.calculate(files, repository, child.getParent(0), child);
                    //}
                }
                commitsAnalyzed++;
                System.out.println(commitsAnalyzed);
                child = parent;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        CoChangeDetector ccd = new CoChangeDetector();
        ccd.findCoChanges(cd);

        /*
         * Finish timing and print result.
         */
        final long endTime = System.currentTimeMillis();

        System.out.println("Total execution time: " + (endTime - startTime));

    }

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


}