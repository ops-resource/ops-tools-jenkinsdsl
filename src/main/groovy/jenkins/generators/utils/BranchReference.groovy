package jenkins.generators.utils

public class BranchReference {
    private String Id
    private String Refspec

    String getId() {
        return this.Id
    }

    String getRefspec() {
        return this.Refspec
    }

    void setId(String id) {
        this.Id = id
    }

    void setRefspec(String refspec) {
        this.Refspec = refspec
    }
}
