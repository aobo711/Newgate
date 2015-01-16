package wandoujia.com.newgate.model;

import java.util.ArrayList;

public class News {
    private String title, firstVideo, date;
    private int id;
    private ArrayList<Tag> tags;

    public News() {
    }

    public News(int id, String name, String firstVideo, String date, ArrayList<Tag> tags) {
        this.id = id;
        this.title = name;
        this.firstVideo = firstVideo;
        this.date = date;
        this.tags = tags;
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