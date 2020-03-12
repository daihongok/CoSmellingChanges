package cochanges;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.util.ArrayList;

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

    public Git getGit() {
        return git;
    }

    public Repository getRepository() {
        return repository;
    }


}
