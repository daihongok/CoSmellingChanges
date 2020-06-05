
import Config.ConfigurationManager;
import Model.CoChange;
import Model.FilePair;
import cochanges.*;
import utility.CSVExporter;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utility.ListOperations;

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
        coChanges.forEach(c -> c.findPackages(project));
        project.printProjectAnalysisInfo();
        // Post processing of co-changes.
        // Attach begin and end timestamps to each co-change.

        File directory = new File("resources/"+ ConfigurationManager.getProjectName());
        if (! directory.exists()){
            directory.mkdir();
        }
        // Write coChanges to CSV file
        CSVExporter.storeCoChanges("resources/"+ConfigurationManager.getProjectName()+"/cochanges.csv",coChanges);

        /*
         * Finish timing and print result.
         */
        final long endTime = System.currentTimeMillis();

        logger.info("Total execution time: " + (endTime - startTime));

        // total amount of pairs
        HashSet<String> distinctFiles = ccd.getChangedFiles();
        ArrayList<FilePair> filePairs = ListOperations.getUniquePairs(distinctFiles).stream().map(pair -> new FilePair(pair.getItem1(), pair.getItem2())).collect(Collectors.toCollection(ArrayList::new));
        filePairs.forEach(f -> f.findPackages(project));
        CSVExporter.storeFilePairs("resources/"+ConfigurationManager.getProjectName()+"/file_pairs.csv",filePairs);

    }




}