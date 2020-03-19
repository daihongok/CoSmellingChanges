
import cochanges.*;
import graph.GraphBuilder;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

public class Main {
    private final static Logger logger = LoggerFactory.getLogger(Main.class);

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
        GraphBuilder.BuildAndPersist(coChanges);
        for (CoChange c: coChanges) {
            logger.debug(c.toString());
        }

        /*
         * Finish timing and print result.
         */
        final long endTime = System.currentTimeMillis();

        logger.info("Total execution time: " + (endTime - startTime));

    }




}