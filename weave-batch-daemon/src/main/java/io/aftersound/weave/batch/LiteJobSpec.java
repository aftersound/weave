package io.aftersound.weave.batch;

public class LiteJobSpec {

    private final String type;
    private final String id;
    private final String classification;

    private String path;

    public LiteJobSpec(String type, String id, String classification) {
        this.type = type;
        this.id = id;
        this.classification = classification;
    }

    public LiteJobSpec setPath(String path) {
        this.path = path;
        return this;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getClassification() {
        return classification;
    }

    public boolean isSimple() {
        return "SIMPLE".equals(classification);
    }

    public boolean isLeaderFollowers() {
        return "LEADER-FOLLOWERS".equals(classification);
    }

    public String getPath() {
        return path;
    }
}
