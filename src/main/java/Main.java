
import Config.ConfigurationManager;
import Model.CoChange;
import Model.GitProject;
import Model.FilePair;
import cochanges.*;
import org.eclipse.jgit.lib.ObjectId;
import utility.CSVExporter;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

        GitProject project = GitProject.CreateFromConfig();
        /*
         * Start timing the program.
         */
        startTime = System.currentTimeMillis();

        ChangeDetector cd = new ChangeDetector();
        ArrayList<ObjectId> commitsInOrder = cd.InitialiseChangeHistory(project);
        CoChangeDetector ccd = new CoChangeDetector(commitsInOrder);
        ArrayList<CoChange> coChanges = ccd.findCoChanges(cd);
        coChanges.forEach(c -> c.findPackages(project));
        project.printProjectAnalysisInfo();

        File directory = new File("resources/"+ ConfigurationManager.getProjectName());
        if (! directory.exists()){
            directory.mkdir();
        }
        // Write changed files to CSV file
        CSVExporter.storeChanges("resources/"+ConfigurationManager.getProjectName()+"/changes.csv",cd.getChangeHistory(), project);
        // Write coChanges to CSV file
        CSVExporter.storeCoChanges("resources/"+ConfigurationManager.getProjectName()+"/cochanges.csv",coChanges);

        /*
         * Finish timing and print result.
         */
        final long endTime = System.currentTimeMillis();

        logger.info("Total execution time: " + (endTime - startTime));

        // total amount of pairs
        String exportFileName = "resources/"+ConfigurationManager.getProjectName()+"/file_pairs.csv";
        // Start with removing out the export file
        File toDelete = new File(exportFileName);
        toDelete.delete();
        // Create the file and the header row
        CSVExporter.createFileAndHeader(exportFileName);

        HashSet<String> distinctFiles = ccd.getChangedFiles();
        ArrayList<Tuple<String>> uniquePairs = ListOperations.getUniquePairs(distinctFiles);
        int BATCH_SIZE = 1000;
        for (int i = 0; i < uniquePairs.size(); i+=BATCH_SIZE) {
            // get current batch
            ArrayList<Tuple<String>> batch = new ArrayList<>(uniquePairs.subList(i, Math.min(i+BATCH_SIZE,uniquePairs.size())));
            ArrayList<FilePair> filePairs = batch.stream().map(pair -> new FilePair(pair.getItem1(), pair.getItem2())).collect(Collectors.toCollection(ArrayList::new));
            filePairs.forEach(f -> f.findPackages(project));
            CSVExporter.storeFilePairs(exportFileName,filePairs);
        }
    }
}