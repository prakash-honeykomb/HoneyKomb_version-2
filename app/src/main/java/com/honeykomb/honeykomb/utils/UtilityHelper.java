package com.honeykomb.honeykomb.utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.honeykomb.honeykomb.R;
import com.honeykomb.honeykomb.dao.SelectedContactObject;
import com.honeykomb.honeykomb.listeners.AppListeners;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Rajashekar on 22/06/18.
 */
public class UtilityHelper {

    private static final String TAG = UtilityHelper.class.getName();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static ProgressDialog progressDialog;
    private static Activity mContext;
    private static AlertDialog filterAlert = null;
    private static Dialog dialog;
    private static SharedPreferences prefs;
    private AppListeners.SingleDialogListener mCallback;

    public static boolean checkPlayServices(Context mContext) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, (Activity) mContext, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                // finish();
            }
            return false;
        }
        return true;
    }

    public static void setStringPreferences(Context context, String key, Object value) {
        SharedPreferences setting = context.getSharedPreferences(Constants.APP_PREFERENCE_NAME, 0);
        SharedPreferences.Editor editor = setting.edit();
        if (value instanceof String)
            editor.putString(key, (String) value);
        else if (value instanceof Boolean)
            editor.putBoolean(key, (Boolean) value);
        editor.apply();
    }

    public static boolean haveNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        ConnectivityManager cm;
        // boolean haveConnectedToInternet = false;

        try {
            cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo[] netInfo = cm.getAllNetworkInfo();
                if (netInfo != null) {
                    for (NetworkInfo ni : netInfo) {
                        if (ni != null && ni.getTypeName().equalsIgnoreCase("WIFI"))
                            if (ni.isConnected())
                                haveConnectedWifi = true;
                        if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                            if (ni.isConnected())
                                haveConnectedMobile = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }


        return haveConnectedWifi || haveConnectedMobile;
        // return haveConnectedToInternet;

    }

    public static Object getStringPreferences(Context context, String key) {

        SharedPreferences setting = context.getSharedPreferences(Constants.APP_PREFERENCE_NAME, 0);
        setting.getClass();

        if (key.equals("FirstTimeLogin"))
            return setting.getBoolean(key, true);
        else
            return setting.getString(key, "");

    }

    public static String getGCMRegistrationId(Context context) {
        SharedPreferences prfs = context.getSharedPreferences("GCM_Registration", MODE_PRIVATE);
        String registrationId = prfs.getString("GCM_Registration_Id", "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        return registrationId;
    }

    public static ListView onTop(ListView listView) {

        listView.setSelection(0);
//        listView.getTop();

        return listView;
    }

//    public static QBUser getQBUser(Context context) {
//   /* prefs = context.getSharedPreferences(PREFERENCES, 0);
//    String qbUserID = prefs.getString("qbUserID", "");
//    String digitsSessionID = prefs.getString("digitsSessionID", "");
//    String qbUserEmailID = prefs.getString("qbUserEmailID", "");
//    String qbUserLoginID = prefs.getString("qbUserLoginID", "");
//    String qbUserNewPassword = prefs.getString("qbUserNewPassword", "");
//    String qbUserTwitterDigitsId = prefs.getString("qbUserTwitterDigitsId", "");
//    final QBUser user = new QBUser();
//    user.setId(Integer.parseInt(qbUserID));
//    user.setPassword(qbUserNewPassword);
//    user.setEmail(qbUserEmailID);
//    user.setTwitterDigitsId(qbUserTwitterDigitsId);
//    user.setLogin(qbUserLoginID);*/
//        db_Helper = new DataBaseHelper(context.getApplicationContext());
//        DataBaseHelper.getInstance(context.getApplicationContext());
//        QBUser qbUser = db_Helper.getQBUserDetails();
//        qbUser.setId(qbUser.getId());
//        qbUser.setPassword(qbUser.getPassword());
//        qbUser.setEmail(qbUser.getEmail());
//        qbUser.setTwitterDigitsId(qbUser.getTwitterDigitsId());
//        qbUser.setLogin(qbUser.getLogin());
//        return qbUser;
//    }

    public static String returnStartEndDate(String date) {
        String modifiedDate = date;

        SimpleDateFormat simpleDateFormatCreated = new SimpleDateFormat("EEE, MMM dd, yyyy | hh:mm aaa", Locale.getDefault());
        if (modifiedDate != null) {
            try {
                Date newDate = simpleDateFormatCreated.parse(modifiedDate);

                simpleDateFormatCreated = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String finalDate = simpleDateFormatCreated.format(newDate);
                modifiedDate = finalDate;

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return modifiedDate;
    }

    public static String returnStartEndTime(String time) {
        String modifiedDate = time;

        SimpleDateFormat simpleDateFormatCreated = new SimpleDateFormat("EEE, MMM dd, yyyy | hh:mm aaa", Locale.getDefault());
        if (modifiedDate != null) {
            try {
                Date newDate = simpleDateFormatCreated.parse(modifiedDate);

                simpleDateFormatCreated = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String finalDate = simpleDateFormatCreated.format(newDate);
                modifiedDate = finalDate;

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return modifiedDate;
    }

    public static String returnDate(String date) {

        String modifiedDate = date;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date1 = simpleDateFormat.parse(date);
            modifiedDate = simpleDateFormat1.format(date1);
        } catch (Exception e) {
            Log.i(TAG, "modifiedDate = " + e);
        }


//        Log.i(TAG, "modifiedDate VALUE = " + modifiedDate.toString());
//        // SimpleDateFormat simpleDateFormatCreated = new SimpleDateFormat("yyyy-MM-dd hh:mm aaa");
//        SimpleDateFormat simpleDateFormatCreated = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        if (modifiedDate != null) {
//            try {
//                Date newDate = simpleDateFormatCreated.parse(modifiedDate);
//
//                simpleDateFormatCreated = new SimpleDateFormat("yyyy-MM-dd hh:mm");
//                String finalDate = simpleDateFormatCreated.format(newDate);
//                modifiedDate = finalDate;
//
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }

        return modifiedDate;
    }

    public static String getTime(String mDate) {
        String result = "";
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a",Locale.getDefault());
//        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("hh:mm aaa", Locale.getDefault());
        String[] dateTime = mDate.split(" ");
        String[] mTime = dateTime[1].split(":");
        Calendar now = Calendar.getInstance();
        try {
            now.set(Calendar.HOUR_OF_DAY, Integer.valueOf(mTime[0]));
            now.set(Calendar.MINUTE, Integer.valueOf(mTime[1]));
            now.set(Calendar.SECOND, Integer.valueOf(mTime[2]));

            String hour = (now.get(Calendar.HOUR) < 10 ? "0" : "") + now.get(Calendar.HOUR);
            if (hour.equals("00"))
                hour = "12";
            String min = (now.get(Calendar.MINUTE) < 10 ? "0" : "") + now.get(Calendar.MINUTE);
            String am_pm = now.get(Calendar.AM_PM) > 0 ? "PM" : "AM";
            result = hour + ":" + min + " " + am_pm;
        } catch (Exception e) {
            Log.i(TAG, "Error : " + e);
        }

//        try {
//            Date time = simpleDateFormat.parse(mDate);
//            result = dateFormat.format(time);
//        } catch (Exception e) {
//            Log.i(TAG, "time" + e);
//        }
        return result;
    }

    public static String getUIDate(String mData) {
        String result = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        try {
            Date date = simpleDateFormat.parse(mData);
            result = simpleDateFormat1.format(date);
        } catch (Exception e) {
            Log.i(TAG, "date" + e);
        }
        return result;
    }

    public static String getUIDislayDateTime(String mData) {
        String result = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("MMM dd,yyyy hh:mm a", Locale.getDefault());
        simpleDateFormat1.setTimeZone(TimeZone.getDefault());
        try {
            Date date = simpleDateFormat.parse(mData);

            result = simpleDateFormat1.format(date);
        } catch (Exception e) {
            Log.i(TAG, "date" + e);
        }
        return result;
    }

    public static String getDate(String mData) {
        String result = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date = simpleDateFormat.parse(mData);
            result = simpleDateFormat1.format(date);
        } catch (Exception e) {
            Log.i(TAG, "date" + e);
        }
        return result;
    }


    public static String getServerDate(String mData) {
        String result = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        try {
            Date date = simpleDateFormat.parse(mData);
            result = simpleDateFormat1.format(date);
        } catch (Exception e) {
            Log.i(TAG, "date" + e);
        }
        return result;
    }


    public static String getChatDisplayDate(String mData) {
        String result = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("MMM dd,yyyy", Locale.getDefault());
        try {
            Date date = simpleDateFormat.parse(mData);
            result = simpleDateFormat1.format(date);
        } catch (Exception e) {
            Log.i(TAG, "date" + e);
        }
        return result;
    }

   /* public static String convertToDateTime(String date, String time) {
        String result = "";
        String mDate = date;
        String am_pm = "am";
        int time_value = 0;
        try {
            result = new Date().toString();

            Calendar c = Calendar.getInstance();
            String[] dateArray = date.split("-");
            String[] timeArray = time.replace(" ", ":").split(":");
            if (timeArray[2].equalsIgnoreCase("PM")) {
                time_value = 1;
            }
//          am_pm = timeArray[2].replaceAll(".","");

            c.set(Calendar.YEAR, Integer.valueOf(dateArray[0]));
            c.set(Calendar.MONTH, Integer.valueOf(dateArray[1]) - 1);
            c.set(Calendar.DATE, Integer.valueOf(dateArray[2]));
            c.set(Calendar.HOUR_OF_DAY, Integer.valueOf(timeArray[0]));
            c.set(Calendar.MINUTE, Integer.valueOf(timeArray[1]));
            c.set(Calendar.AM_PM, time_value);
            //       SimpleDateFormat simpleDateFormatDateTime = new SimpleDateFormat("yyyy-MM-dd hh:mm a",Locale.getDefault());
            //     simpleDateFormatDateTime.setTimeZone(TimeZone.getDefault());
//          Date localDate = simpleDateFormatDateTime.parse(new String(mDate+" "+mTime));

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

            result = simpleDateFormat.format(c.getTime());
        } catch (Exception e) {
            Log.i(TAG, "localdate = " + e);
        }
        return result;
    }*/

    public static String convertToDateTime(String date, String time) {
        String result = "";
        String mDate = date;
        String am_pm = "a";
        try {

            result = new Date().toString();

//            Log.i(TAG, "Time Value = " + time);
//
//            SimpleDateFormat inputFormat = new SimpleDateFormat("hh:mm aaa",Locale.getDefault());
//            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm:ss",Locale.getDefault());
//            Date date2 = null;
//            String str = null;
//            try {
//                date2 = inputFormat.parse(time/*.replace(".","")*/);
//                Log.i(TAG, "Date2 Value = " + date2.toString());
//                str = outputFormat.format(date2);
//                Log.i(TAG, "Str Value = " + str);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            Calendar c = Calendar.getInstance();
            String[] dateArray = date.split("-");
            String[] timeArray = time.replace(" ", ":").split(":");

//            am_pm = timeArray[2].replaceAll(".", "");
            if (timeArray[2].contains(".")) {
                am_pm = timeArray[2].replaceAll(".", "");
            } else {
                am_pm = timeArray[2];
            }

            c.set(Calendar.YEAR, Integer.valueOf(dateArray[0]));
            c.set(Calendar.MONTH, Integer.valueOf(dateArray[1]) - 1);
            c.set(Calendar.DATE, Integer.valueOf(dateArray[2]));
//            c.set(Calendar.DAY_OF_MONTH, Integer.valueOf(dateArray[2]));
            c.set(Calendar.HOUR, Integer.valueOf(timeArray[0]) > 11 ? 0 : Integer.valueOf(timeArray[0]));
            c.set(Calendar.MINUTE, Integer.valueOf(timeArray[1]));
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            c.set(Calendar.AM_PM, Integer.valueOf(am_pm.contains("a") || am_pm.contains("A") ? "0" : "1"));
            //       SimpleDateFormat simpleDateFormatDateTime = new SimpleDateFormat("yyyy-MM-dd hh:mm a",Locale.getDefault());
            //     simpleDateFormatDateTime.setTimeZone(TimeZone.getDefault());
//          Date localDate = simpleDateFormatDateTime.parse(new String(mDate+" "+mTime));

            // time is changed to AM and added 12 hours to it, when user creates and event or task in between 12-1 PM irrespective of date.
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

            result = simpleDateFormat.format(c.getTime());

        } catch (Exception e) {
            Log.i(TAG, "localdate = " + e);
        }
        return result;
    }


    public static String returnTime(String time) {

        String modifiedTime = time;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("hh:mm aaa", Locale.getDefault());
        try {
            Date time1 = simpleDateFormat.parse(time);
            modifiedTime = simpleDateFormat1.format(time1);
        } catch (Exception e) {
            Log.i(TAG, "modifiedDate = " + e);
        }

        return modifiedTime;

//        String modifiedTime = time;
//        Log.i(TAG, "modifiedTime VALUE = " + modifiedTime.toString());
//        //SimpleDateFormat simpleDateFormatCreated = new SimpleDateFormat("yyyy-MM-dd hh:mm aaa");
//        SimpleDateFormat simpleDateFormatCreated = new SimpleDateFormat("hh:mm aaa");
//        if (modifiedTime != null || modifiedTime.length() > 2) {
//            try {
//                Date newDate = simpleDateFormatCreated.parse(modifiedTime);
//
//                simpleDateFormatCreated = new SimpleDateFormat("hh:mm aaa");
//                String finalTime = simpleDateFormatCreated.format(newDate);
//                modifiedTime = finalTime;
//
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }
//
//        return modifiedTime;
    }

    @SuppressWarnings("deprecation")
    public static String setTextColorForNotification(TextView nav, Context mContext, String colorScheme) {
        if (Constants.SCHEME_BLUE.equalsIgnoreCase(colorScheme)) {
            nav.setTextColor(mContext.getResources().getColor(R.color.BLUE));
            return "#019CDC";
        } else if (Constants.SCHEME_BROWN.equalsIgnoreCase(colorScheme)) {
            nav.setTextColor(mContext.getResources().getColor(R.color.BROWN));
            return "#6666CC";
        } else if (Constants.SCHEME_GREEN.equalsIgnoreCase(colorScheme)) {
            nav.setTextColor(mContext.getResources().getColor(R.color.GREEN));
            return "#62BB47";
        } else if (Constants.SCHEME_ORANGE.equalsIgnoreCase(colorScheme)) {
            nav.setTextColor(mContext.getResources().getColor(R.color.ORANGE));
            return "#F78320";
        } else if (Constants.SCHEME_PURPLE.equalsIgnoreCase(colorScheme)) {
            nav.setTextColor(mContext.getResources().getColor(R.color.PURPLE));
            return "#963D97";
        } else if (Constants.SCHEME_RED.equalsIgnoreCase(colorScheme)) {
            nav.setTextColor(mContext.getResources().getColor(R.color.RED));
            return "#E03A3E";
        } else if (Constants.SCHEME_YELLOW.equalsIgnoreCase(colorScheme)) {
            nav.setTextColor(mContext.getResources().getColor(R.color.YELLOW));
            return "#FCB827";
        } else {
            nav.setTextColor(mContext.getResources().getColor(R.color.action_bar_color));
            return "#333333";
        }
    }

    public static String getDifferenceBtwTime(Date dateTime) {

        try {
            Calendar calendar = Calendar
                    .getInstance(/* TimeZone.getTimeZone("UTC"), */ Locale.getDefault());
            int timezoneDiff = calendar.getTimeZone().getRawOffset();
            calendar.setTimeInMillis(dateTime.getTime() + timezoneDiff);

            Date dateTime1 = new Date(calendar.getTimeInMillis());

            Date date = new Date(); // Current date

            long timeDifferenceMilliseconds = date.getTime() - dateTime1.getTime();
            long diffSeconds = timeDifferenceMilliseconds / 1000;
            long diffMinutes = timeDifferenceMilliseconds / (60 * 1000);
            long diffHours = timeDifferenceMilliseconds / (60 * 60 * 1000);
            long diffDays = timeDifferenceMilliseconds / (60 * 60 * 1000 * 24);
            long diffWeeks = timeDifferenceMilliseconds / (60 * 60 * 1000 * 24 * 7);
            long diffMonths = (long) (timeDifferenceMilliseconds / (60 * 60 * 1000 * 24 * 30.41666666));
            long diffYears = timeDifferenceMilliseconds / (1000 * 60 * 60 * 24 * 365);

            if (diffSeconds < 59) {
                return "few sec(s) ago";
            } else if (diffHours < 1) {
                return diffMinutes + " minute(s) ago";
            } else if (diffDays < 1) {
                return diffHours + " hour(s) ago";
            } else {
                return new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(dateTime1).toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void showDialogTwoButton(Activity activity, String tittle, String message, String buttonOneText, String buttonTwoText,
                                           final ButtonNavigation leftButton,
                                           final ButtonNavigation rightButton,
                                           final AppListeners.SingleDialogListener buttonListener) {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
        final FrameLayout frameView = new FrameLayout(activity);
        builder.setView(frameView);

        final android.app.AlertDialog alertDialog = builder.create();
        LayoutInflater inflater = alertDialog.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.alert_dialog, frameView);
        alertDialog.show();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);

        Button leftBTN = (Button) dialoglayout.findViewById(R.id.project_cancelbtn);
        Button rightBTN = (Button) dialoglayout.findViewById(R.id.project_okbtn);

        leftBTN.setText(buttonOneText);
        rightBTN.setText(buttonTwoText);

        TextView titleTV = (TextView) dialoglayout.findViewById(R.id.projectdialogtitle);
        titleTV.setTypeface(UtilityHelper.getFontTypeForApp(activity.getApplicationContext()), Typeface.BOLD);
        TextView messageTV = (TextView) dialoglayout.findViewById(R.id.projectdialog_message);
        messageTV.setTypeface(UtilityHelper.getFontTypeForApp(activity.getApplicationContext()), Typeface.BOLD);
        titleTV.setText(tittle);

        messageTV.setText(message);

        leftBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                buttonListener.onSingleButton(leftButton);
            }
        });

        rightBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                buttonListener.onSingleButton(rightButton);
            }
        });
    }

    public static Typeface getFontTypeForApp(Context context) {

        return Typeface.createFromAsset(context.getResources().getAssets(),
                "font/Calibri.otf");
    }

    public static void showProgressDialog(Activity activity) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("Loading...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    public static void dismissProgressDialog(Activity activity) {
        if (progressDialog != null && progressDialog.isShowing() && activity != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public static int daysBetweenDates(Date endDate, Date startDate) {
        int result = 1;
        Calendar c1 = Calendar.getInstance();
        c1.setTime(endDate);
        c1.set(Calendar.HOUR_OF_DAY, 0);
        c1.set(Calendar.MINUTE, 0);
        c1.set(Calendar.SECOND, 0);
        c1.set(Calendar.MILLISECOND, 0);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(startDate);
        c2.set(Calendar.HOUR_OF_DAY, 0);
        c2.set(Calendar.MINUTE, 0);
        c2.set(Calendar.SECOND, 0);
        c2.set(Calendar.MILLISECOND, 0);
        int diff = (int) (c1.getTimeInMillis() - c2.getTimeInMillis());
        if (diff < 0)
            return result;
        if (diff != 0)
            result = diff / (24 * 60 * 60 * 1000) + 1;
        return result;
    }

    public static long daysBetweenDates1(Date endDate, Date startDate) {
        //in milliseconds
        Date d1 = startDate;
        Date d2 = endDate;
        DateTime dt1 = null;
        DateTime dt2 = null;

        try {

            dt1 = new DateTime(d1);
            dt2 = new DateTime(d2);

            Log.e(TAG, "days = = = " + Days.daysBetween(dt1.toLocalDateTime(), dt2.toLocalDateTime()).getDays() + " days, ");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Days.daysBetween(dt1.toLocalDateTime(), dt2.toLocalDateTime()).getDays();
    }

    public static int daysBetweenDatesNew(Date endDate, Date startDate) {
        return (int) ((startDate.getTime() - endDate.getTime()) / (1000 * 60 * 60 * 24) + 1);
    }


    public static void refreshView(Context activity) {
        Intent broadcastInt = new Intent();
        broadcastInt.setAction(Constants.REFRESH_VIEW);
        LocalBroadcastManager.getInstance(activity).sendBroadcast(broadcastInt);
    }

    /*public static void addAPPLozicUserToGroup(Context context, final AppLozicAddOrRemoveUser appLozicAddOrRemoveUser) {
        ApplozicChannelAddMemberTask.ChannelAddMemberListener channelAddMemberListener = new ApplozicChannelAddMemberTask.ChannelAddMemberListener() {
            @Override
            public void onSuccess(String response, Context context) {
                Log.i(TAG, "ApplozicChannelMember Add Response:" + response);
                Util._db.updateAppLozicID(appLozicAddOrRemoveUser.UniqueID);
            }

            @Override
            public void onFailure(String response, Exception e, Context context) {
                Log.i(TAG, "ApplozicChannelMember Add Response:" + response);
            }
        };

        ApplozicChannelAddMemberTask applozicChannelAddMemberTask = new ApplozicChannelAddMemberTask(context, Integer.parseInt(appLozicAddOrRemoveUser.AppLoxicGroupID), appLozicAddOrRemoveUser.HK_ID, channelAddMemberListener);//pass channel key and userId whom you want to add to channel
        applozicChannelAddMemberTask.execute((Void) null);
    }

    public static void removeUserFromGroup(Context context, final AppLozicAddOrRemoveUser appLozicAddOrRemoveUser) {
        ApplozicChannelRemoveMemberTask.ChannelRemoveMemberListener channelRemoveMemberListener = new ApplozicChannelRemoveMemberTask.ChannelRemoveMemberListener() {
            @Override
            public void onSuccess(String response, Context context) {
                Log.i(TAG, "deleteUserFromGroup onSuccess response = " + response);
                Util._db.updateAppLozicID(appLozicAddOrRemoveUser.UniqueID);
            }

            @Override
            public void onFailure(String response, Exception e, Context context) {
                Log.i(TAG, "deleteUserFromGroup onFailure response = " + response);
            }
        };

        ApplozicChannelRemoveMemberTask applozicChannelRemoveMemberTask = new ApplozicChannelRemoveMemberTask(context, Integer.parseInt(appLozicAddOrRemoveUser.AppLoxicGroupID), appLozicAddOrRemoveUser.HK_ID, channelRemoveMemberListener);//pass channelKey and userId whom you want to remove from channel
        applozicChannelRemoveMemberTask.execute((Void) null);
    }*/


    /*public static void deleteChatGroup(final Context context, int channelKey) {
        DeleteChatGroup deleteChatGroup = new DeleteChatGroup(context, channelKey);
        deleteChatGroup.execute();
    }*/

    /*static class DeleteChatGroup extends AsyncTask<Void, Void, ApiResponse> {
        private Context context;
        private int channelKey;

        public DeleteChatGroup(Context context, int channelKey) {
            this.context = context;
            this.channelKey = channelKey;
        }

        protected ApiResponse doInBackground(Void... params) {
            ChannelClientService channelClientService = ChannelClientService.getInstance(context);
            ApiResponse response = channelClientService.deleteChannel(channelKey);
            Log.i(TAG, "deleteChannel response = " + response);
            return response;
        }

        @Override
        protected void onPostExecute(ApiResponse s) {
            super.onPostExecute(s);

            if (s != null && s.getStatus().equalsIgnoreCase("success") && s.getResponse().toString().equalsIgnoreCase("success")) {

                Channel channel = ChannelDatabaseService.getInstance(context).getChannelByChannelKey(channelKey);
                ApplozicChannelDeleteTask.TaskListener channelDeleteTask = new ApplozicChannelDeleteTask.TaskListener() {
                    @Override
                    public void onSuccess(String response) {
                        Log.i(TAG, "Channel deleted response:" + response);

                        Executor threadPoolExecutor = UtilityHelper.getExecutor();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                            new AddActivityAsync(context).executeOnExecutor(threadPoolExecutor);
                        else
                            new AddActivityAsync(context).execute("");
                    }

                    @Override
                    public void onFailure(String response, Exception exception) {
                        Log.i(TAG, "Channel deleted response:" + response);
                    }

                    @Override
                    public void onCompletion() {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                };
                ApplozicChannelDeleteTask applozicChannelDeleteTask = new ApplozicChannelDeleteTask(context, channelDeleteTask, channel);
                applozicChannelDeleteTask.execute((Void) null);
            }
        }
    }*/

    public static Executor getExecutor() {
        int corePoolSize = 60;
        int maximumPoolSize = 80;
        int keepAliveTime = 10;

        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);
    }

    /*public static void sendMessageInChatGroup(Context context, String activityID, int channelKey, String updatedMsg) {
//        DataBaseHelper db_Helper = new DataBaseHelper(context);
        AppUser appUser = Util._db.getAppUser();
        MobiComUserPreference userPreferences = MobiComUserPreference.getInstance(context);
        Message message = new Message();
        message.setGroupId(channelKey);
        message.setRead(Boolean.TRUE);
        message.setStoreOnDevice(Boolean.TRUE);
        message.setCreatedAtTime(System.currentTimeMillis() + userPreferences.getDeviceTimeOffset());
        message.setSendToDevice(Boolean.FALSE);
        message.setType(Message.MessageType.MT_OUTBOX.getValue());
        message.setMessage(appUser.DisplayName + " has updated " + updatedMsg); //Message to send
        message.setDeviceKeyString(userPreferences.getDeviceKeyString());
        message.setSource(Message.Source.MT_MOBILE_APP.getValue());
        //Method for sending a message
        new MobiComConversationService(context).sendMessage(message);


    }*/

    public static boolean checkConnectivity(Context context) {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo == null) {
            Log.i(TAG, "checkConnectivity not connected");
            return false;
        } else {
            Log.i(TAG, "checkConnectivity netInfo else = " + netInfo.toString());
            return true;
        }
    }

    public static String getUserPhoneNumber(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        return prefs.getString(Constants.SHARED_PREFF_PHONE_NUMBER, "");
    }

    public enum ButtonNavigation {
        HOME_SCREEN, SETTINGS, TRY_AGAIN, LOGIN_SCREEN, SAME_SCREEN, WALLET_LOAD, EXIT_APP, ADD_ACCOUNT, SIGNOUT, USER_SETTINGS
    }

    /*public static void addUserToGroup(final String UniqueID, Context context, final Integer channelKey, final String userId) {
        ApplozicChannelAddMemberTask.ChannelAddMemberListener channelAddMemberListener = new ApplozicChannelAddMemberTask.ChannelAddMemberListener() {
            @Override
            public void onSuccess(String response, Context context) {
                Log.i(TAG, "ApplozicChannelMember Add Response:" + response);
                Util._db.addUserToGroupUH(UniqueID);

            }

            @Override
            public void onFailure(String response, Exception e, Context context) {
                Log.i(TAG, "ApplozicChannelMember Add Response:" + response);
            }
        };

        ApplozicChannelAddMemberTask applozicChannelAddMemberTask = new ApplozicChannelAddMemberTask(context, channelKey, userId, channelAddMemberListener);//pass channel key and userId whom you want to add to channel
        applozicChannelAddMemberTask.execute((Void) null);
    }

    public static void deleteUserFromGroup(final String UniqueID, Context context, final Integer channelKey, final String userId) {
        ApplozicChannelRemoveMemberTask.ChannelRemoveMemberListener channelRemoveMemberListener = new ApplozicChannelRemoveMemberTask.ChannelRemoveMemberListener() {
            @Override
            public void onSuccess(String response, Context context) {
                Log.i(TAG, "deleteUserFromGroup onSuccess response = " + response);
                Util._db.addUserToGroupUH(UniqueID);
            }

            @Override
            public void onFailure(String response, Exception e, Context context) {
                Log.i(TAG, "deleteUserFromGroup onFailure response = " + response);
            }
        };

        ApplozicChannelRemoveMemberTask applozicChannelRemoveMemberTask = new ApplozicChannelRemoveMemberTask(context, channelKey, userId, channelRemoveMemberListener);//pass channelKey and userId whom you want to remove from channel
        applozicChannelRemoveMemberTask.execute((Void) null);
    }

    public static void updateChatDialogTittle(Context context, final AppLozicAddOrRemoveUser appLozicAddOrRemoveUser) {
        ApplozicChannelNameUpdateTask.ChannelNameUpdateListener channelNameUpdateListener = new ApplozicChannelNameUpdateTask.ChannelNameUpdateListener() {
            @Override
            public void onSuccess(String response, Context context) {
                Log.i(TAG, "updateChatDialogTittle Name update:" + response);
                Util._db.updateChatDialogTittleUH(appLozicAddOrRemoveUser.UniqueID);
            }

            @Override
            public void onFailure(String response, Exception e, Context context) {
                Log.i(TAG, "ApplozicChannel Name update: " + response);
            }
        };

        ApplozicChannelNameUpdateTask channelNameUpdateTask = new ApplozicChannelNameUpdateTask(context, Integer.parseInt(appLozicAddOrRemoveUser.AppLoxicGroupID), appLozicAddOrRemoveUser.HK_ID, channelNameUpdateListener);//pass context ,channelKey,chnanel new name
        channelNameUpdateTask.execute((Void) null);
    }*/

    public static Cursor getCursor(Context context, String strItemCode) {
        Cursor cursor = Util._db.getCursorUH(strItemCode);
        return cursor;
    }

    public static Cursor getCursorWithFilter(Context context, String strItemCode, ArrayList<SelectedContactObject> selectedContactObjects) {
        ArrayList<String> numberList = new ArrayList<String>();
        for (SelectedContactObject selectedContactObject : selectedContactObjects) {
            numberList.add("'" + selectedContactObject.getNumber() + "'");
        }
        String inClause = numberList.toString();

        inClause = inClause.replace("[", "(");
        inClause = inClause.replace("]", ")");
        Log.e("inClause", " = " + inClause);

        Cursor cursor = Util._db.getCursorWithFilterUH(inClause, strItemCode);
        return cursor;
    }

    public static Cursor getCursor(Context context, ArrayList<SelectedContactObject> selectedContactObjects) {
        ArrayList<String> numberList = new ArrayList<String>();
        for (SelectedContactObject selectedContactObject : selectedContactObjects) {
            numberList.add("'" + selectedContactObject.getNumber() + "'");
        }
        String inClause = numberList.toString();

        inClause = inClause.replace("[", "(");
        inClause = inClause.replace("]", ")");
        Log.e("inClause", " = " + inClause);
        Cursor cursor = Util._db.getCursor(inClause);
        return cursor;
    }

    public static Integer getUserContactsCount(Context context) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER;
        Cursor cursor1 = cr.query(uri, new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.Contacts._ID},
                selection, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        return cursor1.getCount();
    }

}
