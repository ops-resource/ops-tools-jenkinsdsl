package jenkins.generators.utils

public class Project {

    private String Area;
    private String Description;
    private String Name;
    private List<BranchReference> Refspecs;

    String getArea() {
        return this.Area
    }

    String getDescription() {
        return this.Description
    }

    String getName() {
        return this.Name
    }

    List<BranchReference> getRefspecs() {
        return this.Refspecs
    }

    void setArea(String area) {
        this.Area = area
    }

    void setDescription(String description) {
        this.Description = description
    }

    void setName(String name) {
        this.Name = name
    }

    void setRefspecs(List<BranchReference> refspecs) {
        this.Refspecs = refspecs
    }
}
