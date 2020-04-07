package smells;

import java.util.ArrayList;

public class Smell {

    private ArrayList<String> affectedComponents;

    public Smell(ArrayList<String> affectedFiles) {
        this.affectedComponents = affectedFiles;
    }

    public ArrayList<String> getAffectedComponents() {
        return affectedComponents;
    }

    public void setAffectedComponents(ArrayList<String> affectedComponents) {
        this.affectedComponents = affectedComponents;
    }
}
