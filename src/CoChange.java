import org.eclipse.jgit.lib.ObjectId;

import java.util.List;

public class CoChange {
    private String file1;
    private String file2;
    private List<ObjectId> coVersions;

    public CoChange(String file1, String file2, List<ObjectId> coVersions) {
        this.file1 = file1;
        this.file2 = file2;
        this.coVersions = coVersions;
    }

    public String getFile1() {
        return file1;
    }

    public String getFile2() {
        return file2;
    }

    public List<ObjectId> getCoVersions() {
        return coVersions;
    }

    public String toString() {
        return "co-change: " + file1 + " and " + file2 + "(overlap: " + coVersions.size() + ")";
    }
}
