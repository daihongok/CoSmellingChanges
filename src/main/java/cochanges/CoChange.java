package cochanges;

import org.eclipse.jgit.revwalk.RevCommit;
import utility.FileOperations;
import utility.Tuple;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CoChange {
    private File file1;
    private File file2;
    private ArrayList<Tuple<RevCommit>> coVersions;

    private String package1;
    private String package2;

    CoChange(String file1, String file2, ArrayList<Tuple<RevCommit>> coVersions) {
        this.file1 = new File(file1);
        this.file2 = new File(file2);
        this.coVersions = coVersions;
    }

    public String toString() {
        return "CoCh: " + file1.getName() + " and " + file2.getName() + ". Overlap: \r\n"
                // Add the commit pairs the account to the co-change
                + coVersions.stream()
                            .map(tuple -> "("+tuple.getItem1()+", "+tuple.getItem2()+")\r\n")
                            .reduce("",(curr,next) -> curr + next);
    }

    public String getFile1() {
        return file1.getName();
    }

    public String getFile2() {
        return file2.getName();
    }

    public ArrayList<Tuple<RevCommit>> getCoVersions() {
        return coVersions;
    }

    /**
     * Gets the earliest date among the overlapping commits.
     * @return The earliest date.
     */
    public String getStartDate() {
        Date minDate = new Date(Long.MAX_VALUE);

        for (Tuple<RevCommit> c : coVersions) {
            Date date1 = c.getItem1().getCommitterIdent().getWhen();
            Date date2 = c.getItem2().getCommitterIdent().getWhen();
            long diff1 = minDate.getTime() - date1.getTime();
            if (diff1 > 0) { // date1 < minDate
                minDate = date1;
            }

            long diff2 = minDate.getTime() - date2.getTime();
            if (diff2 > 0) { // date2 < minDate
                minDate = date1;
            }
        }
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String strDate = dateFormat.format(minDate);
        return strDate;
    }

    /**
     * Gets the last date among the overlapping commits.
     * @return The last date.
     */
    public String getEndDate() {
        Date maxDate = new Date(0);

        for (Tuple<RevCommit> c : coVersions) {
            Date date1 = c.getItem1().getCommitterIdent().getWhen();
            Date date2 = c.getItem2().getCommitterIdent().getWhen();
            long diff1 = date1.getTime() - maxDate.getTime();

            if (diff1 > 0) { // date1 < minDate
                maxDate = date1;
            }

            long diff2 = date2.getTime() - maxDate.getTime();
            if (diff2 > 0) { // date2 < minDate
                maxDate = date2;
            }
        }
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String strDate = dateFormat.format(maxDate);
        return strDate;
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
