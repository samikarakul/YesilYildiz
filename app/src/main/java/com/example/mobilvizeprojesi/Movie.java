package com.example.mobilvizeprojesi;

import java.io.Serializable;

public class Movie implements Serializable {

    private String movieTitle;
    private String movieGenre;
    private String moviePhoto;
    private int moviePoint;

    public Movie() {
    }

    public Movie(String movieTitle, String movieGenre, String moviePhoto, int moviePoint) {
        this.movieTitle = movieTitle;
        this.movieGenre = movieGenre;
        this.moviePhoto = moviePhoto;
        this.moviePoint = moviePoint;
    }

    public int getMoviePoint() {
        return moviePoint;
    }

    public void setMoviePoint(int moviePoint) {
        this.moviePoint = moviePoint;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getMovieGenre() {
        return movieGenre;
    }

    public void setMovieGenre(String movieGenre) {
        this.movieGenre = movieGenre;
    }

    public String getMoviePhoto() {
        return moviePhoto;
    }

    public void setMoviePhoto(String moviePhoto) {
        this.moviePhoto = moviePhoto;
    }
}
