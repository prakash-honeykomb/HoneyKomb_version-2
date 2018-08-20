package com.honeykomb.honeykomb.dao;

public class GroupObject {
//    public GroupObject(String groupName, String groupMembers) {
//        this.groupName = groupName;
//        this.groupMembers = groupMembers;
//
//    }

    String groupName;

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public GroupObject(String groupName, String groupID, String groupMembers) {
        this.groupName = groupName;
        this.groupID = groupID;
        this.groupMembers = groupMembers;
    }

    String groupID;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(String groupMembers) {
        this.groupMembers = groupMembers;
    }

    String groupMembers;

}
