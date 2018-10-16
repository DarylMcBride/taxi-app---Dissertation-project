package com.example.owner.mapDemo;

public class BookingHistory {

    private double price;
    private String fromLocation, toLocation, usersName, driversName, usersPhone, timestamp;


    public BookingHistory(double price, String fromLocation, String toLocation, String usersName, String driversName, String usersPhone, String timestamp) {
        this.price = price;
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.usersName = usersName;
        this.driversName = driversName;
        this.usersPhone = usersPhone;
        this.timestamp = timestamp;


    }


    public void setPrice(double price) {
        this.price = price;
    }

    public void setFromLocation(String fromLocation) {
        this.fromLocation = fromLocation;
    }

    public void setToLocation(String toLocation) {
        this.toLocation = toLocation;
    }

    public void setUsersName(String usersName) {
        this.usersName = usersName;
    }

    public void setDriversName(String driversName) {
        this.driversName = driversName;
    }

    public void setUsersPhone(String usersPhone) {
        this.usersPhone = usersPhone;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public double getPrice() {
        return price;
    }

    public String getFromLocation() {
        return fromLocation;
    }

    public String getToLocation() {
        return toLocation;
    }

    public String getUsersName() {
        return usersName;
    }

    public String getDriversName() {
        return driversName;
    }

    public String getUsersPhone() {
        return usersPhone;
    }

    public String getTimestamp() {
        return timestamp;
    }


}
