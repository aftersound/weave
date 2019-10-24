package io.aftersound.weave.couchbase;

public class Repository {

    private String id;
    private GetControl getControl;
    private ViewQueryControl viewQueryControl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public GetControl getGetControl() {
        return getControl;
    }

    public void setGetControl(GetControl getControl) {
        this.getControl = getControl;
    }

    public ViewQueryControl getViewQueryControl() {
        return viewQueryControl;
    }

    public void setViewQueryControl(ViewQueryControl viewQueryControl) {
        this.viewQueryControl = viewQueryControl;
    }

}
