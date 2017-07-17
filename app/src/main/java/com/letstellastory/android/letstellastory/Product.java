package com.letstellastory.android.letstellastory;

/**
 * Created by dozie on 2017-07-15.
 */

public class Product {

    private String id,story,genre;

    public Product(String id, String story, String genre) {
        this.setId(id);
        this.setStory(story);
        this.setGenre(genre);

    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}
