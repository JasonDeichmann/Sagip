package com.example.leebet_pc.saggip;

public class CrimeReportModel {

        private String date;
        private String location;
        private String details;

        public CrimeReportModel(String date, String location, String details){
            this.date = date;
            this.location = location;
            this.details = details;
        }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
