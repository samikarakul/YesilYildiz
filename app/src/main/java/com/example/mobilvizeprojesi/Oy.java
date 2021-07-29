package com.example.mobilvizeprojesi;


import java.util.Date;

public class Oy {

    private Date oyTarihi;
    private String movieTitle;
    private String puan;

    public Oy() {
    }

    public Oy(Date oyTarihi, String movieTitle, String puan) {
        this.oyTarihi = oyTarihi;
        this.movieTitle = movieTitle;
        this.puan = puan;
    }

    public Date getOyTarihi() {
        return oyTarihi;
    }

    public void setOyTarihi(Date oyTarihi) {
        this.oyTarihi = oyTarihi;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getPuan() {
        return puan;
    }

    public void setPuan(String puan) {
        this.puan = puan;
    }
}
