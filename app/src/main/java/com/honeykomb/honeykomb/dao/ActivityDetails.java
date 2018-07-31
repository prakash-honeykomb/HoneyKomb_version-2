package com.honeykomb.honeykomb.dao;

import java.io.Serializable;

public class ActivityDetails implements Serializable {

    private String activityID;
    private String activityTittle;
    private String activityOwner;
    private String blockCalendar;
    private String activityStatus;
    private String canEdit;
    private String startTime;
    private String endTime;
    private String displayName;
    private String quickbloxGroupID;
    private String quickbloxRoomJID;
    private String invitationStatus;
    private String activityNotes;
    private String countRSVP;
    private String activityRSVP;
    private String startDate;
    private String endDate;
    private int unReadCount;
    private String status;
    private String address;
    private String reminder;
    private String notes;
    private String activityDateID;
    private int inviteesToActivity;
    private String GroupID;


    public int getInviteesToActivity() {
        return inviteesToActivity;
    }

    public void setInviteesToActivity(int inviteesToActivity) {
        this.inviteesToActivity = inviteesToActivity;
    }

    public String getGroupID() {
        return GroupID;
    }

    public void setGroupID(String GroupID) {
        this.GroupID = GroupID;
    }


    public String getActivityDateID() {
        return activityDateID;
    }

    public void setActivityDateID(String activityDateID) {
        this.activityDateID = activityDateID;
    }


    public String getInvitationStatus() {
        return invitationStatus;
    }

    public void setInvitationStatus(String invitationStatus) {
        this.invitationStatus = invitationStatus;
    }

    public String getActivityNotes() {
        return activityNotes;
    }

    public void setActivityNotes(String activityNotes) {
        this.activityNotes = activityNotes;
    }

    public String getCountRSVP() {
        return countRSVP;
    }

    public void setCountRSVP(String countRSVP) {
        this.countRSVP = countRSVP;
    }

    public String getActivityRSVP() {
        return activityRSVP;
    }

    public void setActivityRSVP(String activityRSVP) {
        this.activityRSVP = activityRSVP;
    }

    public int getUnReadCount() {
        return unReadCount;
    }

    public void setUnReadCount(int unReadCount) {
        this.unReadCount = unReadCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }


    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getReminder() {
        return reminder;
    }

    public void setReminder(String reminder) {
        this.reminder = reminder;
    }

    public String getActivityID() {
        return activityID;
    }

    public void setActivityID(String activityID) {
        this.activityID = activityID;
    }

    public String getActivityTittle() {
        return activityTittle;
    }

    public void setActivityTittle(String activityTittle) {
        this.activityTittle = activityTittle;
    }

    public String getActivityOwner() {
        return activityOwner;
    }

    public void setActivityOwner(String activityOwner) {
        this.activityOwner = activityOwner;
    }

    public String getBlockCalendar() {
        return blockCalendar;
    }

    public void setBlockCalendar(String blockCalendar) {
        this.blockCalendar = blockCalendar;
    }

    public String getActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(String activityStatus) {
        this.activityStatus = activityStatus;
    }

    public String getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(String canEdit) {
        this.canEdit = canEdit;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getQuickbloxGroupID() {
        return quickbloxGroupID;
    }

    public void setQuickbloxGroupID(String quickbloxGroupID) {
        this.quickbloxGroupID = quickbloxGroupID;
    }

    public String getQuickbloxRoomJID() {
        return quickbloxRoomJID;
    }

    public void setQuickbloxRoomJID(String quickbloxRoomJID) {
        this.quickbloxRoomJID = quickbloxRoomJID;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

   /* @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }*/
}

