import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;

import java.io.File;

public class Main {
    public static void main(String[] args) {

        System.out.println("Test");
        Git git;
        try {
            git = Git.cloneRepository()
                    .setURI("https://github.com/HouariZegai/Calculator.git")
                    .setDirectory(new File("projects/calculator"))
                    .call();

            Repository repository = git.getRepository();
            var x = repository.getWorkTree();
        } catch (Exception e) {
            System.out.println("Something went wrong cloning the repo");
        }


    }
}