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


}
