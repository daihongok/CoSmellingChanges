package Model;

import Model.FilePair;
import org.eclipse.jgit.revwalk.RevCommit;
import utility.Tuple;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CoChange extends FilePair {

    private ArrayList<Tuple<RevCommit>> coVersions;

    public CoChange(String file1, String file2, ArrayList<Tuple<RevCommit>> coVersions) {
        super(file1,file2);
        this.coVersions = coVersions;
    }

    public String toString() {
        return "CoCh: " + file1.getName() + " and " + file2.getName() + ". Overlap: \r\n"
                // Add the commit pairs the account to the co-change
                + coVersions.stream()
                            .map(tuple -> "("+tuple.getItem1()+", "+tuple.getItem2()+")\r\n")
                            .reduce("",(curr,next) -> curr + next);
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
        return getSpecificDate(minDate, TARGETDATE.MIN);
    }

    /**
     * Gets the last date among the overlapping commits.
     * @return The last date.
     */
    public String getEndDate() {
        Date maxDate = new Date(0);
        return getSpecificDate(maxDate, TARGETDATE.MAX);
    }

    private String getSpecificDate(Date comparison, TARGETDATE targetdate){
        for (Tuple<RevCommit> c : coVersions) {
            // date1: date of commit of file A
            // date2: date of commit of file B, which fuzzy overlaps with commit of A
            Date date1 = c.getItem1().getCommitterIdent().getWhen();
            Date date2 = c.getItem2().getCommitterIdent().getWhen();
            // Every loop iteration, compare date1 and date2 to the running aggregate: comparison
            long diff1 = 0;
            long diff2 = 0;

            switch (targetdate){
                case MAX:
                    diff1 = date1.getTime() - comparison.getTime();
                    diff2 = date2.getTime() - comparison.getTime();
                    break;
                case MIN:
                    diff1 = comparison.getTime() - date1.getTime();
                    diff2 = comparison.getTime() - date2.getTime();
                    break;
            }

            if (diff1 > 0) { // date1 < minDate
                comparison = date1;
            }

            if (diff2 > 0) { // date2 < minDate
                comparison = date2;
            }
        }
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String strDate = dateFormat.format(comparison);
        return strDate;
    }

    private enum TARGETDATE {
        MIN,
        MAX
    }


}
