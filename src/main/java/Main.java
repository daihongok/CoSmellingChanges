
import cochanges.*;
import export.CSVExporter;
import graph.GraphBuilder;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smells.Smell;
import smells.SmellImporter;
import utility.ListOperations;
import utility.Tuple;

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
        CSVExporter.storeCoChanges("resources/cochanges.csv",coChanges);
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
        HashSet<String> distinctFiles = ccd.getChangedFiles();
        ArrayList<Tuple<String>> tuples = ListOperations.getUniquePairs(distinctFiles);
        CSVExporter.storeFilePairs("resources/file_pairs.csv",tuples);
        /*
        logger.info("total pairs: " + tuples.size());
        // 2) co-changed pairs
        List<Tuple<String>> coChangedPairs = coChanges.stream().map(cc -> new Tuple<>(cc.getFile1(), cc.getFile2())).collect(Collectors.toList());
        logger.info("co-changed pairs: " + coChangedPairs.size());

        // 3) not co-changed pairs
        HashSet<Tuple<String>> notCoChanging = new HashSet<>(tuples);
        notCoChanging.removeAll(coChangedPairs);
        logger.info("not co-changed pairs: " + notCoChanging.size());

        // 4) pairs that occur in a code smell
        ArrayList<Smell> smells = SmellImporter.LoadSmellsFromCSVFile(true);
        HashSet<Tuple<String>> smellingPairs = new HashSet<>();
        smells.forEach(smell -> smellingPairs.addAll(ListOperations.getUniquePairs(new HashSet<>(smell.getAffectedComponents()))));
        logger.info("smelling pairs: " + smellingPairs.size());

        // 5) pairs occureing in both a smell and a co-change
        Set<Tuple<String>> intersection = new HashSet<>(coChangedPairs); // use the copy constructor
        intersection.retainAll(smellingPairs);
        logger.info("[CHI^2] SMELLING AND CO-CHANGED PAIRS: " + intersection.size());

        // 6) pairs occuring in neither a smell nor a co-change
        HashSet<Tuple<String>> notSmellingNotCochanging = new HashSet<>(notCoChanging);
        notSmellingNotCochanging.removeAll(smellingPairs);
        logger.info("[CHI^2] NOT SMELLING AND NOT CO-CHANGED PAIRS: " + notSmellingNotCochanging.size());

        // 7) not smelling and co-changing
        HashSet<Tuple<String>> notSmellingAndCochanging = new HashSet<>(coChangedPairs);
        notSmellingAndCochanging.removeAll(smellingPairs);
        logger.info("[CHI^2] NOT SMELLING AND CO-CHANGED PAIRS: " + notSmellingAndCochanging.size());

        // 8) smelling and not co-changing
        HashSet<Tuple<String>> smellingNotCoChanging = new HashSet<>(smellingPairs);
        smellingNotCoChanging.removeAll(coChangedPairs);
        logger.info("[CHI^2] SMELLING AND NOT CO-CHANGED PAIRS: " + smellingNotCoChanging.size());*/
    }




}