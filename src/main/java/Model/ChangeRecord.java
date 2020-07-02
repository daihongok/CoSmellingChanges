package Model;

import java.util.Objects;

public class ChangeRecord {
    private String name;
    private String aPackage;
    private String version;


    public ChangeRecord(String name, String aPackage, String version) {
        this.name = name;
        this.aPackage = aPackage;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public String getPackage() {
        return aPackage;
    }

    public String getVersion() {
        return version;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChangeRecord that = (ChangeRecord) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(aPackage, that.aPackage) &&
                Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, aPackage, version);
    }
}
