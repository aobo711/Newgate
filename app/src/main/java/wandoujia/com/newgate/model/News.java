package wandoujia.com.newgate.model;

import java.io.Serializable;
import java.util.ArrayList;

public class News implements Serializable {
    private String title, firstVideo, date, thumbnail;
    private String content;
    private int id;
    private ArrayList<Tag> tags;

    public News() {
    }

    public News(int id, String name, String firstVideo, String date, ArrayList<Tag> tags, String thumbnail) {
        this.id = id;
        this.title = name;
        this.firstVideo = firstVideo;
        this.date = date;
        this.tags = tags;
        this.thumbnail = thumbnail;
    }
    public int getId(){return id;}

    public void setId(int id){ this.id = id; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getFirstVideo() {
        return firstVideo;
    }

    public void setFirstVideo(String firstVideo) {
        this.firstVideo = firstVideo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<Tag> getTags() {
        return tags;
    }

    public void setTags(ArrayList<Tag> tags) {
        this.tags = tags;
    }

    public String getContent(){return content;}

    public void setContent(String content){ this.content = content; }

    public String getThumbnail(){return thumbnail;}

    public void setThumbnail(String thumbnail){ this.thumbnail = thumbnail; }

    @Override
    public boolean equals(Object other) {
        // Not strictly necessary, but often a good optimization
        if (this == other)
            return true;
        if (!(other instanceof News))
            return false;
        News otherA = (News) other;
        return id == otherA.id;
    }
    @Override
    public int hashCode() { return 0; }
}