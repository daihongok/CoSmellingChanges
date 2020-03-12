
import cochanges.ChangeDetector;
import cochanges.CoChange;
import cochanges.CoChangeDetector;
import cochanges.ConfigurationManager;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;

import java.io.File;
import java.util.*;

public class Main {

    public static void main(String[] args) {

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
                git = new Git(repository);
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

            CoChangeDetector ccd = new CoChangeDetector();
            ArrayList<CoChange> coChanges = ccd.getCoChanges(repository, git);
            for (CoChange c: coChanges) {
                System.out.println(c.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }



        /*
         * Finish timing and print result.
         */
        final long endTime = System.currentTimeMillis();

        System.out.println("Total execution time: " + (endTime - startTime));

    }




}