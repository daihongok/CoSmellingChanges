package Model;

import utility.FileOperations;

import java.io.File;
import java.util.Objects;

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

    public String getFile1Path() {
        return file1.getPath();
    }

    public String getFile2Path() {
        return file2.getPath();
    }

    public void findPackages(GitProject project) {
        package1 = FileOperations.GetPackageFromFile(file1.getPath(), project);
        package2 = FileOperations.GetPackageFromFile(file2.getPath(), project);
    }

    public String getPackage1() {
        return package1;
    }

    public String getPackage2() {
        return package2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FilePair filePair = (FilePair) o;
        return Objects.equals(file1, filePair.file1) &&
                Objects.equals(file2, filePair.file2) &&
                Objects.equals(package1, filePair.package1) &&
                Objects.equals(package2, filePair.package2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file1, file2, package1, package2);
    }
}
