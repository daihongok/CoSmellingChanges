
import cochanges.*;
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

        CoChangeProject project = CoChangeProject.CreateFromConfig();
        /*
         * Start timing the program.
         */
        startTime = System.currentTimeMillis();

        CoChangeDetector ccd = new CoChangeDetector();
        ArrayList<CoChange> coChanges = ccd.getCoChanges(project.getRepository(), project.getGit());
        for (CoChange c: coChanges) {
            System.out.println(c.toString());
        }

        /*
         * Finish timing and print result.
         */
        final long endTime = System.currentTimeMillis();

        System.out.println("Total execution time: " + (endTime - startTime));

    }




}