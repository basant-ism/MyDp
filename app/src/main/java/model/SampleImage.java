package model;

public class SampleImage
{
    String imageUrl;
    String Dpid;
    String title;
    String uid;

    public SampleImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public SampleImage() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDpid() {
        return Dpid;
    }

    public void setDpid(String dpid) {
        Dpid = dpid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
