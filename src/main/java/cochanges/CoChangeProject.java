package cochanges;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class CoChangeProject {
    private Git git;
    private Repository repository;

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
            File projectPath = new File(projectDir + "/" + projectName);

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
            walk.markStart(walk.parseCommit(repository.resolve(ConfigurationManager.getLastCommit())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return walk;
    }

    public HashSet<String> getDistinctFiles(RevWalk commitList, int commitsToAnalyze) {
        HashSet<String> distinctFiles = new HashSet<>();
        int commitsAnalyzed = 0;

        for (RevCommit commit : commitList) {
            if (commitsAnalyzed == commitsToAnalyze) {
                break;
            }
            HashSet<String> filesAffected = getAffectedFilesOfCommit(commit);
            distinctFiles.addAll(filesAffected);

            commitsAnalyzed++;
        }

        return distinctFiles;
    }

    /**
     * Gets a set of files affected (changed) by this commit.
     * @param commit RevCommit
     * @return Distinct affected files.
     */
    private HashSet<String> getAffectedFilesOfCommit(RevCommit commit) {
        HashSet<String> filesAffected = new HashSet<>();
        TreeWalk commitTreeWalk = new TreeWalk(repository);
        commitTreeWalk.setRecursive(true);
        try {
            commitTreeWalk.reset(commit.getTree());

            while (commitTreeWalk.next()) {
                String path = commitTreeWalk.getPathString();
                if (path.endsWith(".java")) {
                    filesAffected.add(path);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return filesAffected;
    }

    public Git getGit() {
        return git;
    }

    public Repository getRepository() {
        return repository;
    }


}
