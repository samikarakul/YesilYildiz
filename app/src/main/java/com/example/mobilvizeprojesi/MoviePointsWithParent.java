package com.example.mobilvizeprojesi;

public class MoviePointsWithParent {

    private String parentId;
    private Movie movie;
    private MovieUserPoint movieUserPoint;

    public MoviePointsWithParent() {
    }

    public MoviePointsWithParent(String parentId, Movie movie, MovieUserPoint movieUserPoint) {
        this.parentId = parentId;
        this.movie = movie;
        this.movieUserPoint = movieUserPoint;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public MovieUserPoint getMovieUserPoint() {
        return movieUserPoint;
    }

    public void setMovieUserPoint(MovieUserPoint movieUserPoint) {
        this.movieUserPoint = movieUserPoint;
    }
}