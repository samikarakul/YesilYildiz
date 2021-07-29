package com.example.mobilvizeprojesi;

public class MovieUserPoint {

    private String userEmail;
    private String movieId;
    private int moviePoint;

    public MovieUserPoint() {
    }

    public MovieUserPoint(String userEmail, String movieId, int moviePoint) {
        this.userEmail = userEmail;
        this.movieId = movieId;
        this.moviePoint = moviePoint;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public int getMoviePoint() {
        return moviePoint;
    }

    public void setMoviePoint(int moviePoint) {
        this.moviePoint = moviePoint;
    }
}
