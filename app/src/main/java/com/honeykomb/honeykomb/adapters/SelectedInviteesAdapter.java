package com.honeykomb.honeykomb.adapters;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.honeykomb.honeykomb.R;
import com.honeykomb.honeykomb.dao.SelectedContactObject;
import com.honeykomb.honeykomb.service.SendAllActivityToServer;
import com.honeykomb.honeykomb.utils.Util;
import com.honeykomb.honeykomb.utils.UtilityHelper;

import net.grobas.view.PolygonImageView;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

public class SelectedInviteesAdapter extends BaseAdapter implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    public boolean dataChangedInviteeAdapter = false;
    private Activity mContext;
    private ArrayList<SelectedContactObject> arrayList;
    private String activityID;
    private boolean isOwner;
    private String HK_UUID;
    private String isCompleted;
    private String qbGroupID;
    private String TAG = SelectedInviteesAdapter.class.getSimpleName();
    private String disableAdd;


    public SelectedInviteesAdapter(Activity context, String activityID, boolean owner, String mHK_UUID, String isCompleted, String authenticationKey, String disableAdd) {

        this.mContext = context;
        this.activityID = activityID;
        this.isOwner = owner;
        this.HK_UUID = mHK_UUID;
        this.isCompleted = isCompleted;
        this.qbGroupID = Util._db.getQBGroupIDBasedOnActivityID(activityID);
        this.disableAdd = disableAdd;
        if (qbGroupID != null) {
            Log.i(TAG, "qbGroupID = " + qbGroupID);
        }

        this.arrayList = Util._db.getActivityUsers(activityID);
        Log.e("arrayList", " = = = = " + this.arrayList.size());
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public SelectedContactObject getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View view, ViewGroup parent) {

        final SelectedContactObject contactObject = arrayList.get(position);

        final ViewHolder holder;
        if (view == null) {
//            contactObject = getItem(position);
            holder = new ViewHolder();
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            view = vi.inflate(R.layout.selected_invitees_adapter, null);
            holder.name = view.findViewById(R.id.contactname2);
            holder.number = view.findViewById(R.id.contactno2);
            holder.image = view.findViewById(R.id.polygonImage);
            holder.hkID = view.findViewById(R.id.tv_hkid2);
            holder.rsvpCount = view.findViewById(R.id.tv_rsvp_count2);
            holder.rsvpText = view.findViewById(R.id.tv_rsvp2);
            holder.ll_rsvp = view.findViewById(R.id.ll_rsvp2);
            holder.tv_Read = view.findViewById(R.id.tv_readOn2);
            holder.tv_deliveredTimeValue = view.findViewById(R.id.tv_readOnTime2);
            holder.user_activity_status_IMV = view.findViewById(R.id.user_activity_status_IMV);//user_activity_status
            holder.iv_invitation_status = view.findViewById(R.id.invitation_status_IV2);//invitation_status_IV

//            if (!holder.name.getText().toString().equalsIgnoreCase("You")) {
            holder.swipeLayout = view.findViewById(R.id.swipeInvitee);


            view.setTag(holder);


        } else {
            holder = (ViewHolder) view.getTag();
        }
        String deviceDisplayName = getContactName(mContext, contactObject.getNumber());
        String phoneNumberWOPlusNine = contactObject.getNumber().replace("+91", "");
        String contactName = getContactName(mContext, phoneNumberWOPlusNine);
        if (contactName != null && contactName.trim().length() > 1) {
            deviceDisplayName = getContactName(mContext, phoneNumberWOPlusNine);
        }
        if (deviceDisplayName != null && !deviceDisplayName.trim().equalsIgnoreCase("")) {
            holder.name.setText(deviceDisplayName);
            holder.name.setTextColor(Color.BLACK);
        } else {
            holder.name.setText("~" + contactObject.getName());
            holder.name.setTextColor(Color.LTGRAY);
        }
        holder.rsvpCount.setText(contactObject.getRSVPCount());
        holder.number.setText(contactObject.getNumber());
        holder.hkID.setText(contactObject.getHkID());
        holder.image.setBorder(true);
        holder.image.addBorderResource(5, R.color.white);
        holder.image.setCornerRadius(5);
        holder.image.setVertices(6);
        holder.image.setRotationAngle(60);
        holder.swipeLayout.setSwipeEnabled(true);
        if (HK_UUID.equals(contactObject.getHkUUID())) {
            holder.name.setText("You");
            holder.name.setTextColor(Color.BLACK);
            if (isOwner) {
                holder.swipeLayout.setLeftSwipeEnabled(false);
                holder.swipeLayout.setRightSwipeEnabled(false);
            }
        } else if (contactObject.getInvitationStatus().equalsIgnoreCase("No")) {
            holder.swipeLayout.setLeftSwipeEnabled(true);
            holder.swipeLayout.setRightSwipeEnabled(true);
        } else if (contactObject.getInvitationStatus().equalsIgnoreCase("Yes")) {
            holder.swipeLayout.setLeftSwipeEnabled(false);
            holder.swipeLayout.setRightSwipeEnabled(true);
        } else if (contactObject.getInvitationStatus().equalsIgnoreCase("Pending")) {
            holder.swipeLayout.setLeftSwipeEnabled(false);
            holder.swipeLayout.setRightSwipeEnabled(true);
        } else {
            holder.swipeLayout.setSwipeEnabled(true);
        }

        holder.swipeLayout.setTag(contactObject);
        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, view.findViewById(R.id.ll_delete_invitee));
        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, view.findViewById(R.id.ll_contact_reinvite));
        holder.swipeLayout.setId(position);
        holder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                SelectedContactObject obj = (SelectedContactObject) layout.getTag();
                if (layout.getDragEdge() == SwipeLayout.DragEdge.Left) {
                    holder.swipeLayout.setSwipeEnabled(false);

                    String hK_UUID = obj.getHkUUID();
                    String number = obj.getNumber();

                    if (obj.getInvitationStatus().equals("No")) {
                        Util._db.isTrueSelectedListOfInviteesAdapter("ActionType", "REINVITE", "invitationStatus", "Pending", "userActivityStatus", "Active", activityID, hK_UUID);
                        Util._db.updateTableFromSLIADPTER("ActionType", "REINVITE", "invitationStatus", "Pending", "userActivityStatus", "Active", activityID, number);
                        if (Util._db.getActionTypeBasedOnActivityID(activityID)) {
                            Util._db.updateActivitySelectedListOfInvitees(activityID);
                        }
                    } else {
                        Util._db.isTrueSelectedListOfInviteesAdapter("ActionType", "ADD", "invitationStatus", "Pending", "userActivityStatus", "Active", activityID, hK_UUID);
                        Util._db.updateTableFromSLIADPTER("ActionType", "ADD", "invitationStatus", "Pending", "userActivityStatus", "Active", activityID, number);
                        if (Util._db.getActionTypeBasedOnActivityID(activityID)) {
                            Util._db.updateActivitySelectedListOfInvitees(activityID);
                        }
                    }
                    arrayList = Util._db.getActivityUsers(activityID);

                    // TODO adding user to existing group.
                    if (qbGroupID != null && !qbGroupID.equalsIgnoreCase("0") && qbGroupID.length() > 2) {
                        String UniqueID = UUID.randomUUID().toString();
                        Util._db.saveUser(UniqueID, Integer.parseInt(qbGroupID), obj.getHkID(), "ADD", "NO");
//                        UtilityHelper.addUserToGroup(UniqueID, mContext, Integer.parseInt(qbGroupID), obj.getHkID());
                        if (Util._db.getActionTypeBasedOnActivityID(activityID)) {
                            Intent i = new Intent(Intent.ACTION_SYNC, null, mContext, SendAllActivityToServer.class);
                            mContext.startService(i);
                        }
                    }

                    dataChangedInviteeAdapter = true;
                    holder.swipeLayout.close();


                } else if (layout.getDragEdge() == SwipeLayout.DragEdge.Right) {
                    String hK_UUID = obj.getHkUUID();
                    String number = obj.getNumber();

                    if (obj.getInvitationStatus().equals("No")) {
                        Util._db.isTrueSelectedListOfInviteesAdapterNew("ActionType", "REMOVE", "userActivityStatus", "inActive", "countRSVP", " ", "invitationStatus", " ", activityID, hK_UUID);
                        Util._db.updateTableFromSLIADPTERNew("ActionType", "REMOVE", "userActivityStatus", "inActive", "countRSVP", " ", "invitationStatus", " ", activityID, number);
                        if (Util._db.getActionTypeBasedOnActivityID(activityID)) {
                            Util._db.updateActivitySelectedListOfInvitees(activityID);
                        }
                    } else {
                        Util._db.isTrueSelectedListOfInviteesAdapterNew("ActionType", "REMOVE", "userActivityStatus", "inActive", "countRSVP", " ", activityID, hK_UUID);
                        Util._db.updateTableFromSLIADPTERNew("ActionType", "REMOVE", "userActivityStatus", "inActive", "countRSVP", " ", activityID, number);
                        if (Util._db.getActionTypeBasedOnActivityID(activityID)) {
                            Util._db.updateActivitySelectedListOfInvitees(activityID);
                        }
                    }

                    holder.swipeLayout.close();

                    if (qbGroupID != null && !qbGroupID.equalsIgnoreCase("0") && qbGroupID.length() > 2) {
                        if (obj.getHkID() != null) {
                            String UniqueID = UUID.randomUUID().toString();
                            Util._db.saveUser(UniqueID, Integer.parseInt(qbGroupID), obj.getHkID(), "REMOVE", "NO");
//                            UtilityHelper.deleteUserFromGroup(UniqueID, mContext, Integer.parseInt(qbGroupID), obj.getHkID());
                            if (Util._db.getActionTypeBasedOnActivityID(activityID)) {
                                Intent i = new Intent(Intent.ACTION_SYNC, null, mContext, SendAllActivityToServer.class);
                                mContext.startService(i);
                            }
                        }
                    }

                    arrayList.remove(obj);
                    dataChangedInviteeAdapter = true;
                }
                notifyDataSetChanged();
                notifyDataSetInvalidated();
            }
        });


        Log.i(TAG, "contactObject.getRSVPCount() VALUE = " + contactObject.getRSVPCount());
        if (contactObject.getRSVPCount() == null || contactObject.getRSVPCount().equalsIgnoreCase("0") || contactObject.getRSVPCount().equalsIgnoreCase(" ")) {
            holder.rsvpText.setVisibility(View.GONE);
            holder.rsvpCount.setVisibility(View.GONE);
        } else {
            holder.rsvpText.setVisibility(View.VISIBLE);
            holder.rsvpCount.setVisibility(View.VISIBLE);
            holder.rsvpText.setText(mContext.getResources().getString(R.string.rsvp));
            holder.rsvpCount.setText(contactObject.getRSVPCount());
        }
        if (contactObject.getDeliveredTime() != null && !HK_UUID.equals(contactObject.getHkUUID()) && contactObject.getDeliveredTime().trim().length() > 0) {
            holder.tv_Read.setText(mContext.getResources().getString(R.string.read));
            String date = UtilityHelper.getUIDislayDateTime(contactObject.getDeliveredTime());
            holder.tv_deliveredTimeValue.setText(date);
            holder.tv_Read.setVisibility(View.VISIBLE);
            holder.tv_deliveredTimeValue.setVisibility(View.VISIBLE);
        } else {
            holder.tv_Read.setVisibility(View.GONE);
            holder.tv_deliveredTimeValue.setVisibility(View.GONE);
        }

        String invitationStatus = contactObject.getInvitationStatus();
        if (invitationStatus != null) {
            if (invitationStatus.equalsIgnoreCase("Yes")) {
                holder.iv_invitation_status.setImageResource(R.mipmap.accepted);
            } else if (invitationStatus.equalsIgnoreCase("Pending")) {
                holder.iv_invitation_status.setImageResource(R.mipmap.notresponded);
            } else if (invitationStatus.equalsIgnoreCase("No")) {
                holder.iv_invitation_status.setImageResource(R.mipmap.denied);
            }
        }
        if (contactObject.getUserActivityStatus() != null && contactObject.getUserActivityStatus().equalsIgnoreCase("Completed")) {
            holder.user_activity_status_IMV.setVisibility(View.VISIBLE);
            holder.rsvpText.setVisibility(View.GONE);
            holder.rsvpCount.setVisibility(View.GONE);
        }

        if (!isOwner)
            holder.swipeLayout.setSwipeEnabled(false);
        if (isCompleted.equalsIgnoreCase("Completed"))
            holder.swipeLayout.setSwipeEnabled(false);
        if (disableAdd.equalsIgnoreCase("Yes"))
            holder.swipeLayout.setSwipeEnabled(false);
        if (Util._db.getStatusByActivityID(activityID).equalsIgnoreCase("inActive"))
            holder.swipeLayout.setSwipeEnabled(false);
        if (contactObject.getHkUUID() != null && contactObject.getHkUUID().equalsIgnoreCase(Util._db.getActivityOwnerIDBasedOnActivityID(activityID))) {
            holder.image.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.owner_user));
        } else {
            holder.image.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.hk_user));
        }

        if (contactObject.getDeliveredTime() != null && !HK_UUID.equals(contactObject.getHkUUID()) && contactObject.getDeliveredTime().trim().length() > 0) {
            String date = UtilityHelper.getUIDislayDateTime(contactObject.getDeliveredTime());
            holder.tv_deliveredTimeValue.setTag(date);
        }
        return view;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        arrayList.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    }

    @Override
    public void onClick(View view) {

    }

    public Bitmap getByteContactPhoto(String contactId) {

        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(contactId));
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
//        Cursor cursor = mContext.getContentResolver().query(photoUri,
//                new String[]{Contacts.Photo.DATA15}, null, null, null);
//        ArrayList<ArrayList<String>> contacts = db_Helper.getFinalContactList();
        ArrayList<ArrayList<String>> contacts = Util._db.getFinalContactList();

        //       if (contacts.size() == 0) {
        //           return null;
        //       }
        if (contacts.size() > 0) {
//                byte[] data = cursor.getBlob(0);
            byte[] data = contacts.get(0).get(5).getBytes();
            if (data != null) {
                return BitmapFactory.decodeStream(new ByteArrayInputStream(data));
            }
        }
        return null;
    }


    static class ViewHolder {
        protected TextView name;
        protected TextView number;
        protected PolygonImageView image;
        private TextView hkID;
        private TextView rsvpText;
        private TextView rsvpCount;
        private LinearLayout ll_rsvp;
        private TextView tv_Read;
        private TextView tv_deliveredTimeValue;
        private ImageView user_activity_status_IMV;
        private ImageView iv_invitation_status;
        protected SwipeLayout swipeLayout;
    }

}

