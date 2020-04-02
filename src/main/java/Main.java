
import cochanges.*;
import export.CSVExporter;
import graph.GraphBuilder;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smells.SmellGraph;
import utility.ListOperations;
import utility.Tuple;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

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
        ArrayList<CoChange> coChanges = ccd.getCoChanges(project);
        // Write coChanges to CSV file
        CSVExporter.writeToCSV("resources/cochanges.csv",coChanges);
        GraphBuilder.BuildAndPersist(coChanges);
        for (CoChange c: coChanges) {
            logger.debug(c.toString());
        }

        /*
         * Finish timing and print result.
         */
        final long endTime = System.currentTimeMillis();

        logger.info("Total execution time: " + (endTime - startTime));

        /*
         * Info for chi^2 test
         */
        // 1) total amount of pairs
        HashSet<String> distinctFiles = project.getDistinctFiles(project.getWalk(), ConfigurationManager.getMaxAmountOfCommits());
        ArrayList<Tuple<String>> tuples = ListOperations.getUniquePairs(distinctFiles);
        logger.info("[CHI^2] TOTAL PAIRS: " + tuples.size());
        // 2) co-changed pairs
        List<Tuple<String>> coChangedPairs = coChanges.stream().map(cc -> new Tuple<>(cc.getFile1(), cc.getFile2())).collect(Collectors.toList());
        logger.info("[CHI^2] CO-CHANGED PAIRS: " + coChangedPairs.size());
        // 3) not co-changed pairs
        logger.info("[CHI^2] NOT CO-CHANGED PAIRS: " + (tuples.size() - coChangedPairs.size()));
        // 4) pairs that occur in a code smell
        SmellGraph smellGraph = SmellGraph.BuildFromFile("resources/swagger-core-smells.graphml");
    }




}