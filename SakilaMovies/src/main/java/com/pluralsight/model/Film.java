package com.pluralsight.model;

public class Film {
    private int filmID;
    private String title;
    private String description;
    private int releaseYear;
    private int length;

    public Film(int filmID, String title, int releaseYear, int length, String description) {
        this.filmID = filmID;
        this.title = title;
        this.description = description;
        this.releaseYear = releaseYear;
        this.length = length;
    }

    public int getFilmID() {
        return filmID;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public int getLength() {
        return length;
    }
}
