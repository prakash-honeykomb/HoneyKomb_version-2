package com.honeykomb.honeykomb.dao;

public class ActivityDetailsDao {
    private String activityID;
    private String activityTitle;
    private String activityOwner;
    private String blockCalendar;
    private String activityStatus;
    private String canEdit;
    private String startTime;
    private String endTime;
    private String displayName;
    private String quickbloxGroupID;
    private String quickbloxRoomJID;
    private String startDate = "";
    private String endDate = "";
    private int unreadCount;
    private String status;
    private String userActivityStatus;
    private String eventType;
    private String reminder;
    private String address;
    private String snoozeDateNTime;
    private int invitiesToActivity;
    private String activityDateID;
    private String eventLocation;

    public String getSnoozeDateNTime() {
        return snoozeDateNTime;
    }

    public void setSnoozeDateNTime(String snoozeDateNTime) {
        this.snoozeDateNTime = snoozeDateNTime;
    }

    public int getInvitiesToActivity() {
        return invitiesToActivity;
    }

    public void setInvitiesToActivity(int invitiesToActivity) {
        this.invitiesToActivity = invitiesToActivity;
    }

    public String getActivityDateID() {
        return activityDateID;
    }

    public void setActivityDateID(String activityDateID) {
        this.activityDateID = activityDateID;
    }

    public String getReminder() {
        return reminder;
    }

    public void setReminder(String reminder) {
        this.reminder = reminder;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getActivityID() {
        return activityID;
    }

    public void setActivityID(String activityID) {
        this.activityID = activityID;
    }

    public String getActivityTitle() {
        return activityTitle;
    }

    public void setActivityTitle(String activityTitle) {
        this.activityTitle = activityTitle;
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

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserActivityStatus() {
        return userActivityStatus;
    }

    public void setUserActivityStatus(String userActivityStatus) {
        this.userActivityStatus = userActivityStatus;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
}
