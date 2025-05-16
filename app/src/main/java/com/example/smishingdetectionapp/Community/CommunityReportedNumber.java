package com.example.smishingdetectionapp.Community;

public class CommunityReportedNumber {
    public String number;
    public int count;
    public String lastReportedDate;

    public CommunityReportedNumber(String number, int count, String lastReportedDate) {
        this.number = number;
        this.count = count;
        this.lastReportedDate = lastReportedDate;
    }
}