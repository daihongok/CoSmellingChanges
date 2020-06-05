package cochanges;

import Config.ConfigurationManager;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CoChangeProject {
    private Git git;
    private Repository repository;

    private List<String> files = null;

    private String projectLocation;

    private CoChangeProject() {

    }
    /**
     * Creates a co-change project based on the config settings.
     * @return The initialized project.
     */
    public static CoChangeProject CreateFromConfig() {
        CoChangeProject project = new CoChangeProject();
        project.InitializeProject(ConfigurationManager.getProjectsDirectory(),
                                    ConfigurationManager.getProjectName(),
                                    ConfigurationManager.getProjectOwner(),
                                    ConfigurationManager.getProjectBranch());
        return project;
    }

    private void InitializeProject(String projectDir, String projectName, String projectOwner, String projectBranch) {
        try {
            projectLocation = projectDir + "/" + projectName;
            File projectPath = new File(projectLocation);

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
                        .setURI("https://github.com/"+projectOwner+"/"+projectName+".git")
                        .setDirectory(projectPath)
                        .setBranch(projectBranch)
                        .call();
                repository = git.getRepository();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a walk over commits from the configured start, skipping merges.
     * @return The walk.
     */
    public RevWalk getWalk() {
        RevWalk walk = new RevWalk(repository);
        //Ignore merges
        walk.setRevFilter(RevFilter.NO_MERGES);

        try {
            var x = repository.resolve(ConfigurationManager.getLastCommit());
            walk.markStart(walk.parseCommit(x));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return walk;
    }


    public void printProjectAnalysisInfo() {
        System.out.println("--- Info on analysis ---");
        try {
            RevWalk walk = getWalk();

            int commitsAnalyzed = 0;

            for (RevCommit currentCommit : walk) {
                // If first or last commit
                if (commitsAnalyzed == 0 || (commitsAnalyzed+1) == ConfigurationManager.getMaxAmountOfCommits()) {
                    Date date = currentCommit.getCommitterIdent().getWhen();
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    String strDate = dateFormat.format(date);
                    System.out.println("date "+commitsAnalyzed+": " + strDate);
                }
                commitsAnalyzed++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Git getGit() {
        return git;
    }

    public Repository getRepository() {
        return repository;
    }


    public String getProjectLocation() {
        return projectLocation;
    }

    public List<String> getProjectFiles() {
        if (files == null) {
            files = new ArrayList<>();

            try (Stream<Path> walk = Files.walk(Paths.get(projectLocation))) {

                files = walk.filter(Files::isRegularFile)
                        .map(Path::toString).collect(Collectors.toList());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return files;
    }
}
