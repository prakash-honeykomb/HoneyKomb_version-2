package com.honeykomb.honeykomb.network;

/**
 * Created by Rajashekar.Nimmala on 5/4/2017.
 */

public class WebURLs {


    private static String APP_BASE_URL = "https://9d8tiqxrpe.execute-api.ap-south-1.amazonaws.com/beta/";// TODO BETA
    //public static String APP_BASE_URL = "https://ndqm25xmdl.execute-api.ap-south-1.amazonaws.com/prod/";// TODO PROD
    //public static String APP_BASE_URL = "https://6jkuw7maz2.execute-api.us-east-1.amazonaws.com/Dev/";TODO DEV

    //BETA 24-03-2017
    public static final String AWS_IdentityPoolID = "us-east-1:452c84ed-9020-42e3-874e-f3ef62850bca";
    public static final String REST_ACTION_VERIFY_USER = APP_BASE_URL + "users/verify";
    public static final String REST_ACTION_CREATE_USER = APP_BASE_URL + "users";
    public static final String REST_ACTION_USER_ACTIVITY = APP_BASE_URL + "activity";
    public static final String REST_ACTION_ADD_UPDATE_ACTIVITY = APP_BASE_URL + "addactivity";
    public static final String REST_ACTION_CONTACT_LIST = APP_BASE_URL + "users/contactlist";
    public static final String REST_ACTION_USER_NOTIFICATIONS = APP_BASE_URL + "users/notifications";
    public static final String REST_ACTION_ACKNOWLEDGE_SERVER = APP_BASE_URL + "acknowledgement";
    public static final String REST_ACTION_PENDING_INVITATIONS = APP_BASE_URL + "users/invitations";
    public static final String REST_ACTION_UPDATE_USER = APP_BASE_URL + "users/update";
    public static final String REST_ACTION_USER_GET_ACTIVITY = APP_BASE_URL + "users/getactivity";
    public static final String REST_ACTION_USERS_RSVP = APP_BASE_URL + "users/rsvp";
    public static final String REST_ACTION_NOTIFICATION_MOVE = APP_BASE_URL + "users/notificationmove";
    public static final String BROADCAST_USERS_NEW_CONTACT_REGISTERED = APP_BASE_URL + "users/newcontactreg";
    public static final String OVERDUE_ACT = APP_BASE_URL + "users/overdueact";
    public static final String REST_ACTION_READ_N_DELIVER =  APP_BASE_URL + "activity/getactuserinfo";

    public static final String UPDATE_USER_NAME = APP_BASE_URL + "users/update";
    public static final String Service_Name_Verify_User = "VERIFYUSER";
    public static final String Service_Name_Create_User = "CREATEUSER";
    public static final String Service_Name_Activity = "ACTIVITY";
    public static final String Service_Name_GET_ALL_Activity = "USERSACTIVITY";
    public static final String Service_Name_Notifications = "NOTIFICATIONS";
    public static final String Service_Name_Contact_List = "CONTACTLIST";
    public static final String Service_Name_Pending_Invitations = "PENDINGINVITATIONS";
    public static final String Service_Name_Pending_Activity = "PENDINGACTIVITY";
    public static final String Service_Name_Update_User = "UPDATEUSER";
    public static final String SCHEME_RED = "Red";
    public static final String REST_ACTION_NEW_USER_REG = APP_BASE_URL + "web/nonverifyusersreg";


}
