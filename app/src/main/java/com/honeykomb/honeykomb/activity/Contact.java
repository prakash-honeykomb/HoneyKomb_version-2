package com.honeykomb.honeykomb.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.honeykomb.honeykomb.R;
import com.honeykomb.honeykomb.adapters.SimpleCursorRecyclerAdapter;
import com.honeykomb.honeykomb.dao.SelectedContactObject;
import com.honeykomb.honeykomb.utils.Util;
import com.honeykomb.honeykomb.utils.UtilityHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Contact extends AppCompatActivity {

    private SimpleCursorRecyclerAdapter adapter;
    private EditText edtSearch;
    private ListView contactList = null;
    private ArrayList<SelectedContactObject> selectedContactObjects = new ArrayList<>();
    private ArrayList<SelectedContactObject> selectedgroupObjects = new ArrayList<>();
    private String from = "";
    private String TAG = Contact.class.getSimpleName();
    //    private Tracker mTracker;
    private Cursor cursor,cursor1,cursorForGroupMembers;
    Toolbar toolbar;
    TextView ibDoneContacts,txtViewSelectedUsers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_contacts);
//        AnalyticsApplication application = (AnalyticsApplication) getApplication();
//        mTracker = application.getDefaultTracker();
        edtSearch = findViewById(R.id.input_search);

        contactList = findViewById(R.id.contacts_RCV);
        getSupportActionBar().hide();
        txtViewSelectedUsers =    findViewById(R.id.tv_all_contacts);

        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            if (getSupportActionBar() != null) {
                toolbar.setNavigationIcon(R.mipmap.backarrow);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
            }
        }
        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle.containsKey("from"))
                from = bundle.getString("from");
            Log.e("from", "from = " + from);

            if (bundle.containsKey("selectedContactObjects")) {
                selectedContactObjects = bundle.getParcelableArrayList("selectedContactObjects");
            }
        }
        if (from.equalsIgnoreCase("addButton") || from.equalsIgnoreCase("EventDetials")) {
            cursor = UtilityHelper.getCursor(Contact.this, selectedContactObjects);
            adapter = new SimpleCursorRecyclerAdapter(Contact.this, cursor, adapterItemClick/*, selectedContactObjects*/);
            contactList.setAdapter(adapter);
        } else {
            cursor1 = Util._db.getCursorContactNewGroup();
            cursor = getCursor();
            adapter = new SimpleCursorRecyclerAdapter(Contact.this, cursor,cursor1, adapterItemClick/*, selectedContactObjects*/);
            contactList.setAdapter(adapter);
        }

        edtSearch.clearFocus();

//        mUserContactsSelectAll.setVisibility(View.GONE);
        ColorDrawable dividerColor1 = new ColorDrawable(getApplicationContext().getResources().getColor(R.color.gray_holo_light));
        contactList.setDivider(dividerColor1);
        contactList.setDividerHeight(1);

        edtSearch.setCursorVisible(true);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2,
                                      int arg3) {
                String text = edtSearch.getText().toString().toLowerCase(Locale.getDefault());

                if (adapter != null) {
                  /*  if (from.equalsIgnoreCase("addButton") || from.equalsIgnoreCase("EventDetials")) {
                        cursor = UtilityHelper.getCursorWithFilter(Contact.this, "%" + text + "%", selectedContactObjects);
                        adapter = new SimpleCursorRecyclerAdapter(Contact.this, cursor, adapterItemClick, selectedContactObjects);
                        contactList.setAdapter(adapter);
                    } else {
//                        cursor = UtilityHelper.getCursor(Contact.this, "%" + text + "%");*/
                    if (selectedContactObjects != null) {
                        cursor = UtilityHelper.getCursorWithFilter(Contact.this, "%" + text + "%", selectedContactObjects);
                        cursor1 = UtilityHelper.getCursorWithFilterGroup(Contact.this, "%" + text + "%", selectedgroupObjects);

                        adapter = new SimpleCursorRecyclerAdapter(Contact.this, cursor,cursor1, adapterItemClick/*, selectedContactObjects*/);
                        contactList.setAdapter(adapter);
                    }
//                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {

            }

            @Override
            public void afterTextChanged(Editable arg0) {

            }
        });


          ibDoneContacts = findViewById(R.id.toolbar_save_TV);
        ibDoneContacts.setVisibility(View.GONE);

        ibDoneContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hide_keyboard_from(Contact.this, edtSearch);
                getSelectedContacts();
            }
        });
       // TextView tvDoneContacts

        /*mUserContactsSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    mUserContactsSelectAll.setText("Deselect all");
                } else {
                    mUserContactsSelectAll.setText("Select all");
                }
                if (isUserContactsSelected && adapter != null) {
//                    adapter.onSelectAllClicked(checked);
                }
            }
        });*/
    }


    private void getSelectedContacts() {
        ArrayList<String> contactNo = new ArrayList<>();
        ArrayList<String> contactName = new ArrayList<>();
        ArrayList<String> contactHkID = new ArrayList<>();
        ArrayList<String> contacthkUUID = new ArrayList<>();
        ArrayList<String> contactinvitationStatus = new ArrayList<>();
        ArrayList<String> contactQBUserID = new ArrayList<>();

        List<SelectedContactObject> noRepeat = new ArrayList<>(selectedContactObjects);

        for (SelectedContactObject bean : noRepeat) {
             if (bean.isSelected()) {
                contactNo.add(bean.getNumber());
                contactName.add(bean.getName());
                contactHkID.add(bean.getHkID());
                contacthkUUID.add(bean.getHkUUID());
                contactinvitationStatus.add("Pending");
                contactQBUserID.add(" ");
             }
        }

        Log.i(TAG, "contactNo..." + contactNo.toString());

        if (from.equalsIgnoreCase("EventDetials")) {
            Intent intent = new Intent(Contact.this, SelectedListOfInvitees.class);
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("contactNo", contactNo);
            bundle.putStringArrayList("contactName", contactName);
            bundle.putStringArrayList("contacthkID", contactHkID);
            bundle.putStringArrayList("contacthkUUID", contacthkUUID);
            bundle.putStringArrayList("invitationStatus", contactinvitationStatus);
            bundle.putStringArrayList("contactQBUserID", contactQBUserID);
            intent.putExtras(bundle);
            setResult(Activity.RESULT_OK, intent);
            finish();
        } else {
            Intent intent = new Intent(Contact.this, ContactSelected.class);
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("contactNo", contactNo);
            bundle.putStringArrayList("contactName", contactName);
            bundle.putStringArrayList("contacthkID", contactHkID);
            bundle.putStringArrayList("contacthkUUID", contacthkUUID);
            bundle.putStringArrayList("invitationStatus", contactinvitationStatus);
            bundle.putStringArrayList("contactQBUserID", contactQBUserID);
            intent.putExtras(bundle);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    public static void hide_keyboard_from(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "Setting screen name: " + TAG);
//        mTracker.setScreenName("Image~" + TAG);
//        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (cursor != null) {
            cursor.close();
        }
        super.onDestroy();
    }

    public Cursor getCursor() {
        cursor = Util._db.getCursorContactNew();
        //cursor.a
        return cursor;
    }

    private SimpleCursorRecyclerAdapter.OnItemCheckListener adapterItemClick = new SimpleCursorRecyclerAdapter.OnItemCheckListener() {
        @Override
        public void onItemCheck(SelectedContactObject item) {
            Log.e("onItemCheck", " Size = " + selectedContactObjects.size());
            Log.e("onItemCheck", " = " + item.getNumber());
            if (item.getQuickBloxID() == null || item.getQuickBloxID().isEmpty()) {
                Log.e("onItemCheck", " Single user" );

                if (!selectedContactObjects.contains(item)) {
                selectedContactObjects.add(item);
            }
            if (!selectedgroupObjects.contains(item)) {
                selectedgroupObjects.add(item);
            }

        } else {
                inviteGroup(item.getQuickBloxID(),"onItemCheck");
                Log.e("onItemCheck", " Group = id:  "+item.getQuickBloxID() );
            }

            if (selectedContactObjects.size() >= 1)
                ibDoneContacts.setVisibility(View.VISIBLE);
            Log.e("onItemCheck", " Size 2 = " + selectedgroupObjects);
            Log.e("onItemCheck", " Size 2 = " + selectedContactObjects.size());
            txtViewSelectedUsers.setText( selectedContactObjects.size()+ " Users Selected");
        }

        public void onItemUncheck(SelectedContactObject item) {
            Log.e("onItemUncheck", " Size = " + selectedContactObjects.size());
            Log.e("onItemUncheck", " = " + item.getNumber());
            if (item.getQuickBloxID() == null || item.getQuickBloxID().isEmpty()) {
                if (selectedContactObjects.contains(item)) {
                    selectedContactObjects.remove(item);
                }
                if (selectedgroupObjects.contains(item)) {
                    selectedgroupObjects.remove(item);
                }
            }else {
                inviteGroup(item.getQuickBloxID(),"onItemUncheck");
                Log.e("onItemCheck", " Group = id:  "+item.getQuickBloxID() );
            }
            txtViewSelectedUsers.setText( selectedContactObjects.size()+ " Users Selected");

            if(selectedContactObjects.size()>=1)
                ibDoneContacts.setVisibility(View.VISIBLE);
            else
                ibDoneContacts.setVisibility(View.GONE);

            Log.e("onItemUncheck", " Size 2 = " + selectedContactObjects.size());
        }
    };



    public  void inviteGroup(String groupID, String action) {
        cursorForGroupMembers = Util._db.getCursorGroupUsers(groupID);

       //  ArrayList<ContactsObject> values= new ArrayList<>();
        //selectedContactObjects =  new ArrayList<>();

//        while(c.moveToNext()) {
//            groupUsersList.add(new ContactObject(cursor.getString(cursor.getColumnIndex("contactNo")),cursor.getString(cursor.getColumnIndex("contactNo")), cursor.getString(cursor.getColumnIndex("_id"))));
//        }
        if (cursorForGroupMembers != null) {
            if (cursorForGroupMembers.getCount() != 0) {
                while (cursorForGroupMembers.moveToNext()) {
                   // groupUsersList.add(new ContactObject(cursor.getString(cursor.getColumnIndex("contactNo")),cursor.getString(cursor.getColumnIndex("contactNo")), cursor.getString(cursor.getColumnIndex("_id"))));

                    SelectedContactObject user = new SelectedContactObject(Parcel.obtain());
                  //  String groupId = cursorForGroupMembers.getString(cursorForGroupMembers.getColumnIndex("groupID"));
                    String contactNo = cursorForGroupMembers.getString(cursorForGroupMembers.getColumnIndex("contactNo"));
                    Log.e("onItemCheck", " group user----"+contactNo );
                    user.setName("Test");
                    user.setNumber(contactNo);
                    user.setSelected(true);
                    user.setHkID("123");
                    user.setHkUUID("123");
                   // user.setHkID(groupId);
                    if(action.equalsIgnoreCase("onItemCheck")) {
                        if (!selectedContactObjects.contains(user)) {
                            selectedContactObjects.add(user);
                        }
                    }
                    else{
                    if (selectedContactObjects.contains(user)) {
                        selectedContactObjects.remove(user);
                    }}
                   // selectedContactObjects.add(user);
                }
            }
            cursorForGroupMembers.close();
        }


        //     groupsRCVAdapter.notifyDataSetChanged();



    }
}
