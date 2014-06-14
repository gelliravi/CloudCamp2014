package com.example.chartbuster.app;

public class Movie {

    public String id;
    public String name;
    public String url;
    public int rating;
    public int seats;
    public int price;


    @Override
    public String toString() {
        return "[ "+id+" "+name+" "+url+" "+rating+" "+seats+" "+price+" ]";
    }
}
