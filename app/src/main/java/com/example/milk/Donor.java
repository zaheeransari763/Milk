package com.example.milk;

public class Donor {
    public String Address, City, Age, Contact, District, Email, Fullname , image;

    public Donor() {
    }

    public Donor(String address, String city, String age, String contact, String district, String email, String fullname, String image) {
        this.Address = address;
        this.City = city;
        this.Age = age;
        this.Contact = contact;
        this.District = district;
        this.Email = email;
        this.Fullname = fullname;
        this.image = image;
    }

    public String getAddresss() {
        return Address;
    }

    public void setAddresss(String address) {
        Address = address;
    }

    public String getCityy() {
        return City;
    }

    public void setCityy(String city) {
        City = city;
    }

    public String getAgee() {
        return Age;
    }

    public void setAgee(String age) {
        Age = age;
    }

    public String getContactt() {
        return Contact;
    }

    public void setContactt(String contact) {
        Contact = contact;
    }

    public String getDistrictt() {
        return District;
    }

    public void setDistrictt(String district) {
        District = district;
    }

    public String getEmaill() {
        return Email;
    }

    public void setEmaill(String email) {
        Email = email;
    }

    public String getFullnamee() {
        return Fullname;
    }

    public void setFullnamee(String fullname) {
        Fullname = fullname;
    }

    public String getImagee() {
        return image;
    }

    public void setImagee(String image) {
        this.image = image;
    }
}
