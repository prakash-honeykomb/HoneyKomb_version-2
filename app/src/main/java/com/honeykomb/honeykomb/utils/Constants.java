package com.honeykomb.honeykomb.utils;

import android.os.Build;

public class Constants {
    public static final String TWITTER_KEY = "YYYWkw6h65LuoHGDJmpuO2cv4";
    public static final String TWITTER_SECRET = "R83NXArisWfZzMdDglWtH34HoPXHc1uqOEa9e23tUdHGmFK108";
    public static final String APP_NAME = "HoneyKomb";
    public static final String LONG_URL = "http:web.honeykomb.in/myapp/Login.jsp?";
    public static final String TERMS_URL = "http://honeykomb.in/policy.html";
    public static final String PRIVACY_URL = "http://honeykomb.in/policy.html";
    public static final String DEVICE_MODEL = Build.MODEL;
    public static final String DEVICE_TOKEN = "devicetoken";
    public static final String OS_TYPE = "Android";
    public static final int OS_VERSION = Build.VERSION.SDK_INT;
    public static final String AUTH_KEY = "authenticationKey";
    public static final int HANDLER_MESSAGE_CODE_USER_NOT_EXISTS = 1502;
    public static final String HK_UUID = "hK_UUID";
    public static final String DISPLAY_NAME = "displayName";
    public static final String HK_ID = "hK_ID";
    public static final String STATUS_MESSAGE = "message";
    public static final String PHOTO_PATH = "photoPath";
    public static final String REG_ID = "regId";
    public static final String SELECTED_OBJECT_KEY = "selectedContactObjects";
    public static final String CONTACT_NO_OBJECT_KEY = "contactNo";
    public static final String CONTACT_NAME_OBJECT_KEY = "contactName";
    public static final String CONTACT_HKID_OBJECT_KEY = "contacthkID";
    public static final String CONTACT_HK_UUID_OBJECT_KEY = "contacthkUUID";
    public static final String ACTIVITY_ID = "activityID";
    public static final String GROUP_ID = "groupID";
    public static final String ACTIVITY_DATE_ID = "activityDateID";
    public static final String ACTIVITY_OWNER = "activityOwner";
    public static int GROUP_INVITEES_SIZE = 1;
    public static String APP_PREFERENCE_NAME = "HoneyKomb_Shared_Preferences";
    public static String LAUNCH_APP_FOR_FIRST_TIME = "appLaunchedFirstTime";
    public static final String USER_STATUS = "UserStatus";
    public static final int ALARM_REQUEST_CODE = 500;
    public static final String SCHEME_BLUE = "Blue";
    public static final String SCHEME_GREEN = "Green";
    public static final String SCHEME_YELLOW = "Yellow";
    public static final String SCHEME_PURPLE = "Purple";
    public static final String SCHEME_BROWN = "Brown";
    public static final String SCHEME_ORANGE = "Orange";
    public static final String SCHEME_RED = "Red";
    public static final String REFRESH_VIEW = "refreshViews";
    public static final String SHARED_PREFF_DIGITS_SESSION_ID = "digitsSessionID";
    public static final String SHARED_PREFF_PHONE_NUMBER = "phoneNumber";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String DEMO_SHOWN = "demo_shown";
    // API response code
    public static final int HANDLER_MESSAGE_CODE_OK = 200;
    public static final int HANDLER_MESSAGE_UNAUTHORIZED = 401;
    public static final int HANDLER_MESSAGE_CODE_USER_ALREADY_EXISTS = 1513;
    public static final int HANDLER_MESSAGE_CODE_INSUFFICIENT = 1514;

    // service name for acknowledgment request
    public static final String Service_Name_Verify_User = "VERIFYUSER";
    public static final String Service_Name_Activity = "ACTIVITY";
    public static final String Service_Name_GET_ALL_Activity = "USERSACTIVITY";
    public static final String Service_Name_Notifications = "NOTIFICATIONS";
    public static final String Service_Name_Contact_List = "CONTACTLIST";

    public static final String UPDATE_GROUP = "updateGroup";

    public static final String CREATE_GROUP = "createGroup";

    public static final String CREATE_OR_UPDATE_GROUP = "createUpdateGroup";
}
