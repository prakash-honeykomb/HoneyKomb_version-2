package com.honeykomb.honeykomb.adapters;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Parcel;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.honeykomb.honeykomb.R;
import com.honeykomb.honeykomb.activity.FAVGroupDetails;
import com.honeykomb.honeykomb.dao.ContactObject;
import com.honeykomb.honeykomb.dao.SelectedContactObject;
import com.honeykomb.honeykomb.utils.Util;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GroupUsersAdapter extends BaseAdapter implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    public boolean dataChangedInviteeAdapter = false;
    private Context mContext;
   // private ArrayList<SelectedContactObject> arrayList;
    List<ContactObject> groupUsersList;
  //  private String activityID;
   // private boolean isOwner;
    private String hkID;
    private String contactNo;
    private String contactName;
    private String TAG = GroupUsersAdapter.class.getSimpleName();
  //  private String disableAdd;
  TextView txtView;

    public GroupUsersAdapter(Activity context, String activityID, boolean owner, String mHK_UUID, String isCompleted, String authenticationKey, String disableAdd) {

        this.mContext = context;
     //   this.activityID = activityID;
      //  this.isOwner = owner;
      //  this.HK_UUID = mHK_UUID;
      //  this.isCompleted = isCompleted;
      //  this.qbGroupID = Util._db.getQBGroupIDBasedOnActivityID(activityID);
     //   this.disableAdd = disableAdd;
       // if (qbGroupID != null) {
        //    Log.i(TAG, "qbGroupID = " + qbGroupID);
      ///  }

        //this.arrayList = Util._db.getActivityUsers(activityID);
        //Log.e("arrayList", " = = = = " + this.arrayList.size());
    }

    public GroupUsersAdapter(Context context, List<ContactObject> groupUsersList) {
        this.mContext = context;
        this.groupUsersList = groupUsersList;
        txtView = (TextView) ((Activity)mContext).findViewById(R.id.memberListTV);


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
        return groupUsersList.size();
    }

    @Override
    public ContactObject getItem(int position) {
        return groupUsersList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View view, ViewGroup parent) {
     //   ContactObject groupObject= groupUsersList.get(position);
        final ContactObject contactObject = groupUsersList.get(position);

        final GroupUsersAdapter.ViewHolder holder;
        if (view == null) {
//            contactObject = getItem(position);
            holder = new GroupUsersAdapter.ViewHolder();
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            view = vi.inflate(R.layout.group_users_adapter, null);
            holder.name = view.findViewById(R.id.contactname2);
            holder.number = view.findViewById(R.id.contactno2);
           holder.image = view.findViewById(R.id.polygonImage);
            holder.hkID = view.findViewById(R.id.tv_hkid2);
           //holder.rsvpCount = view.findViewById(R.id.tv_rsvp_count2);
           // holder.rsvpText = view.findViewById(R.id.tv_rsvp2);
           // holder.ll_rsvp = view.findViewById(R.id.ll_rsvp2);
          //  holder.tv_Read = view.findViewById(R.id.tv_readOn2);
           // holder.tv_deliveredTimeValue = view.findViewById(R.id.tv_readOnTime2);
          //  holder.user_activity_status_IMV = view.findViewById(R.id.user_activity_status_IMV);//user_activity_status
          //  holder.iv_invitation_status = view.findViewById(R.id.invitation_status_IV2);//invitation_status_IV

//            if (!holder.name.getText().toString().equalsIgnoreCase("You")) {
            holder.swipeLayout = view.findViewById(R.id.swipeInvitee);


            view.setTag(holder);


        } else {
            holder = (GroupUsersAdapter.ViewHolder) view.getTag();
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
      //  holder.rsvpCount.setText(contactObject.getRSVPCount());
        holder.number.setText(contactObject.getNumber());
        holder.hkID.setText(contactObject.getHkID());
      //  holder.image.setBorder(true);
       // holder.image.addBorderResource(5, R.color.white);
       // holder.image.setCornerRadius(5);
       // holder.image.setVertices(6);
       // holder.image.setRotationAngle(60);
        holder.swipeLayout.setSwipeEnabled(true);
       //  if (contactObject.getInvitationStatus().equalsIgnoreCase("No")) {
            holder.swipeLayout.setLeftSwipeEnabled(true);
            holder.swipeLayout.setRightSwipeEnabled(true);
      //  } else
//            if (contactObject.getInvitationStatus().equalsIgnoreCase("Yes")) {
//            holder.swipeLayout.setLeftSwipeEnabled(false);
//            holder.swipeLayout.setRightSwipeEnabled(true);
//        } else if (contactObject.getInvitationStatus().equalsIgnoreCase("Pending")) {
//            holder.swipeLayout.setLeftSwipeEnabled(false);
//            holder.swipeLayout.setRightSwipeEnabled(true);
//        } else {
            holder.swipeLayout.setSwipeEnabled(true);
       // }

        holder.swipeLayout.setTag(contactObject);
        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, view.findViewById(R.id.ll_delete_invitee));
        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, view.findViewById(R.id.ll_contact_reinvite));
        holder.swipeLayout.setId(position);
        holder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                ContactObject obj = (ContactObject) layout.getTag();
                if (layout.getDragEdge() == SwipeLayout.DragEdge.Left) {
                    if(!dataChangedInviteeAdapter)
                    holder.swipeLayout.setSwipeEnabled(false);


                  //  String hK_UUID = obj.getHkID();


                    //arrayList = Util._db.getActivityUsers(activityID);
//
                    // TODO adding user to existing group.
                 /*   if (qbGroupID != null && !qbGroupID.equalsIgnoreCase("0") && qbGroupID.length() > 2) {
                        String UniqueID = UUID.randomUUID().toString();
                        Util._db.saveUser(UniqueID, Integer.parseInt(qbGroupID), obj.getHkID(), "ADD", "NO");
//                        UtilityHelper.addUserToGroup(UniqueID, mContext, Integer.parseInt(qbGroupID), obj.getHkID());
                        if (Util._db.getActionTypeBasedOnActivityID(activityID)) {
                            Intent i = new Intent(Intent.ACTION_SYNC, null, mContext, SendAllActivityToServer.class);
                            mContext.startService(i);
                        }
                    }*/
                    Log.i("FAVGroup: ", "onActivityResult clled: if "  );
                    Toast.makeText(mContext,"Swipe left",Toast.LENGTH_LONG).show();

                    dataChangedInviteeAdapter = true;
                    holder.swipeLayout.close();


                } else if (layout.getDragEdge() == SwipeLayout.DragEdge.Right) {

                    Log.i("FAVGroup: ", "onActivityResult clled: else "  );
                    String number = obj.getNumber();
                  //  if (dataChangedInvitee || groupUsersAdapter.dataChangedInviteeAdapter) {



                    Util._db.updateGroupSelectedListOfUsers(number);
                    // Intent intent = new Intent(mContext, FAVGroup.class);
                    // setResult(Activity.RESULT_OK, intent);
                    // }
                    //  Toast.makeText(mContext,"Swipe right",Toast.LENGTH_LONG).show();
                    holder.swipeLayout.close();
                    SelectedContactObject user = new SelectedContactObject(Parcel.obtain());
                    String groupId = obj.getHkID();
                    String contactNo = obj.getNumber();
                    user.setNumber(contactNo);
                    user.setHkID(groupId);
                   // selectedContactObjects.add(user);

                    FAVGroupDetails.selectedContactObjects.remove(user);

                    groupUsersList.remove(obj);
                    txtView.setText(groupUsersList.size()+" Users");
                    //FAVGroupDetails.grupSizeTV.setText(groupUsersList.size());
                    dataChangedInviteeAdapter = true;
                }
                notifyDataSetChanged();
                notifyDataSetInvalidated();
            }
        });




       /* if (contactObject.getHkUUID() != null && contactObject.getHkUUID().equalsIgnoreCase(Util._db.getActivityOwnerIDBasedOnActivityID(activityID))) {
            holder.image.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.owner_user));
        } else {
            holder.image.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.hk_user));
        }

        if (contactObject.getDeliveredTime() != null && !HK_UUID.equals(contactObject.getHkUUID()) && contactObject.getDeliveredTime().trim().length() > 0) {
            String date = UtilityHelper.getUIDislayDateTime(contactObject.getDeliveredTime());
            holder.tv_deliveredTimeValue.setTag(date);
        }*/
        return view;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        groupUsersList.clear();
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
       protected ImageView image;
        private TextView hkID;
       // private TextView rsvpText;
       // private TextView rsvpCount;
       // private LinearLayout ll_rsvp;
       // private TextView tv_Read;
      //  private TextView tv_deliveredTimeValue;
       // private ImageView user_activity_status_IMV;
       // private ImageView iv_invitation_status;
        protected SwipeLayout swipeLayout;
    }

}

