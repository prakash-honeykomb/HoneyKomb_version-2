package com.honeykomb.honeykomb.dao;

public class RSVPCount {
    private int noCount = 0;
    private int notRespondedCount = 0;
    private int RSVPCount = 0;
    private int yesCount = 0;
    private int totalCount = 0;
    private int userRSVPCount = 0;
    private int totalRSVPCount = 0;
    private int totalNoOfInvitees = 0;

    public int getTotalRSVPCount() {
        return totalRSVPCount;
    }

    public void setTotalRSVPCount(int totalRSVPCount) {
        this.totalRSVPCount = totalRSVPCount;
    }

    public int getTotalNoOfInvitees() {
        return totalNoOfInvitees;
    }

    public void setTotalNoOfInvitees(int totalNoOfInvitees) {
        this.totalNoOfInvitees = totalNoOfInvitees;
    }

    public int getNoCount() {
        return noCount;
    }

    public void setNoCount(int noCount) {
        this.noCount = noCount;
    }

    public int getNotRespondedCount() {
        return notRespondedCount;
    }

    public void setNotRespondedCount(int notRespondedCount) {
        this.notRespondedCount = notRespondedCount;
    }

    public int getRSVPCount() {
        return RSVPCount;
    }

    public void setRSVPCount(int RSVPCount) {

        this.RSVPCount = RSVPCount;
    }

    public int getYesCount() {
        return yesCount;
    }

    public void setYesCount(int yesCount) {

        this.yesCount = yesCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getUserRSVPCount() {
        return userRSVPCount;
    }

    public void setUserRSVPCount(int userRSVPCount) {
        this.userRSVPCount = userRSVPCount;
    }
}

