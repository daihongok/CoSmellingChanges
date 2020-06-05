package Model;

import cochanges.CoChangeProject;
import utility.FileOperations;

import java.io.File;

public class FilePair {

    protected File file1;
    protected File file2;

    protected String package1;
    protected String package2;

    public FilePair(String file1, String file2) {
        this.file1 = new File(file1);
        this.file2 = new File(file2);
    }

    public String getFile1() {
        return file1.getName();
    }

    public String getFile2() {
        return file2.getName();
    }

    public void findPackages(CoChangeProject project) {
        package1 = FileOperations.GetPackageFromFile(file1.getName(), project);
        package2 = FileOperations.GetPackageFromFile(file2.getName(), project);
    }

    public String getPackage1() {
        return package1;
    }

    public String getPackage2() {
        return package2;
    }
}
