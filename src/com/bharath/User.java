package com.bharath;

public class User {
    public int id;
    public String name;
    public String date;
    public String address;
    public String country;
    public String mobile;
    public String email;
    public String city;
    public String zipcode;

    public User(int id, String name, String date, String address, String city, String zipcode, String country, String mobile, String email) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.address = address;
        this.country = country;
        this.mobile = mobile;
        this.email = email;
    }
}
