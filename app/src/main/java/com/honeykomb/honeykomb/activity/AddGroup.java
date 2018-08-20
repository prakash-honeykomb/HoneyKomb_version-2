package com.honeykomb.honeykomb.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import android.widget.Toast;

import com.honeykomb.honeykomb.R;
import com.honeykomb.honeykomb.adapters.ContactsAdapter;
import com.honeykomb.honeykomb.dao.SelectedContactObject;
import com.honeykomb.honeykomb.dao.ServiceContactObject;
import com.honeykomb.honeykomb.utils.Constants;
import com.honeykomb.honeykomb.utils.Util;
import com.honeykomb.honeykomb.utils.UtilityHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddGroup extends AppCompatActivity {

    private ContactsAdapter adapter;
    private EditText edtSearch;
    private ListView contactList = null;
    private ArrayList<SelectedContactObject> selectedContactObjects = new ArrayList<>();
    private ArrayList<SelectedContactObject> selectedContactObjectsNew = new ArrayList<>();

    private String from = "";
    private String TAG = Contact.class.getSimpleName();
    //    private Tracker mTracker;
    Toolbar toolbar;
    private Cursor cursor;
    TextView tvSelectedContacts;
    Bundle bundle;
    Intent intent;
    ListView selectedUsersRV;
    TextView ibDoneContacts;
    //public static DataBaseHelper _db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        getSupportActionBar().hide();

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
        ///_db= new DataBaseHelper(AddGroup.this);
//        AnalyticsApplication application = (AnalyticsApplication) getApplication();
//        mTracker = application.getDefaultTracker();
         edtSearch = findViewById(R.id.input_search);
         tvSelectedContacts=findViewById(R.id.tv_selected_contacts);
         contactList = findViewById(R.id.contacts_RCV);
         selectedUsersRV= findViewById(R.id.groups_RCV);
          intent = getIntent();
        if (getIntent().getExtras() != null) {
             bundle = getIntent().getExtras();
            if (bundle.containsKey("from"))
                from = bundle.getString("from");
            Log.e("from", "from = " + from);

            if (bundle.containsKey("selectedContactObjects")) {
                selectedContactObjects = bundle.getParcelableArrayList("selectedContactObjects");
            }
        }
        if (from.equalsIgnoreCase("FAVGroupDetails") || getIntent().getExtras().get(Constants.CREATE_OR_UPDATE_GROUP).toString().equalsIgnoreCase(Constants.UPDATE_GROUP)) {
            cursor = UtilityHelper.getCursor(AddGroup.this, selectedContactObjects);
            adapter = new ContactsAdapter(AddGroup.this, cursor, adapterItemClick/*, selectedContactObjects*/);
            contactList.setAdapter(adapter);
            //selectedUsersRV.setAdapter(adapter);

        } else {
            cursor = getCursor();
            adapter = new ContactsAdapter(AddGroup.this, cursor, adapterItemClick/*, selectedContactObjects*/);
            contactList.setAdapter(adapter);
            selectedUsersRV.setAdapter(adapter);

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
                        cursor = UtilityHelper.getCursorWithFilter(AddGroup.this, "%" + text + "%", selectedContactObjects);
                        adapter = new ContactsAdapter(AddGroup.this, cursor, adapterItemClick/*, selectedContactObjects*/);
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
                hide_keyboard_from(AddGroup.this, edtSearch);
                getSelectedContacts();
            }
        });



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

     /*   if (from.equalsIgnoreCase("EventDetials")) {
            Intent intent = new Intent(AddGroup.this, SelectedListOfInvitees.class);
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
            Intent intent = new Intent(AddGroup.this, ContactSelected.class);
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
        }*/

        ServiceContactObject deviceContact = new ServiceContactObject();
        deviceContact.contactName=getIntent().getExtras().get("groupname").toString()!=null?getIntent().getExtras().get("groupname").toString():"test";
        deviceContact.contactNo=selectedContactObjects.size()+"";
        deviceContact.image=" ";

        if(getIntent().getExtras().get(Constants.CREATE_OR_UPDATE_GROUP).toString().equalsIgnoreCase( Constants.CREATE_GROUP)) {
            Long groupID = Util._db.addOrUpdateGroup(deviceContact);
            if (groupID != -1) {
                for (int i = 0; i < selectedContactObjects.size(); i++) {
                    deviceContact.contactNo = selectedContactObjects.get(i).getNumber();
                    Util._db.addOrUpdateGroupDetails(deviceContact, groupID+"");
                }
                Toast.makeText(AddGroup.this, "Group created successfully...", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(AddGroup.this, "Please try again...", Toast.LENGTH_LONG).show();

            }
        } else {
           // if (groupID != -1) {
                for (int i = 0; i < selectedContactObjectsNew.size(); i++) {
                    deviceContact.contactNo = selectedContactObjectsNew.get(i).getNumber().toString();
                    Util._db.addOrUpdateGroupDetails(deviceContact,  getIntent().getExtras().get(Constants.GROUP_ID).toString());
                }
                Toast.makeText(AddGroup.this, "Group updated successfully...", Toast.LENGTH_LONG).show();
           // }
        }
        // new FAVGroup().prepareMovieData();
        if(Constants.CREATE_GROUP.equalsIgnoreCase(getIntent().getExtras().get(Constants.CREATE_OR_UPDATE_GROUP).toString())) {
            startActivity(new Intent(AddGroup.this, FAVGroup.class));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

           // this.finish();
        } else {

            Intent gotoGroupDetails = new Intent(AddGroup.this,FAVGroupDetails.class);
            Bundle bundle = new Bundle();
            bundle.putString(Constants.GROUP_ID, getIntent().getExtras().get(Constants.GROUP_ID).toString());
            bundle.putBoolean("isOwner", true);

            bundle.putString("groupName",  getIntent().getExtras().get("groupname").toString());
            bundle.putString("hK_UUID", "");
            bundle.putString("QuickbloxGroupID", " ");
            gotoGroupDetails.putExtras(bundle);
            startActivity(gotoGroupDetails);
            //finish();
           // startActivity(new Intent(AddGroup.this, FAVGroup.class));
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);

           // this.finish();
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

        if(Constants.CREATE_GROUP.equalsIgnoreCase(getIntent().getExtras().get(Constants.CREATE_OR_UPDATE_GROUP).toString())) {
            startActivity(new Intent(AddGroup.this, FAVGroup.class));
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);

        } else {
            Intent gotoGroupDetails = new Intent(AddGroup.this,FAVGroupDetails.class);
            Bundle bundle = new Bundle();
            bundle.putString(Constants.GROUP_ID, getIntent().getExtras().get(Constants.GROUP_ID).toString());
            bundle.putBoolean("isOwner", true);

            bundle.putString("groupName",  getIntent().getExtras().get("groupname").toString());
            bundle.putString("hK_UUID", "");
            bundle.putString("QuickbloxGroupID", " ");
            gotoGroupDetails.putExtras(bundle);
            startActivity(gotoGroupDetails);
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);

        }

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
        return cursor;
    }


    private ContactsAdapter.OnItemCheckListener adapterItemClick = new ContactsAdapter.OnItemCheckListener() {
        @Override
        public void onItemCheck(SelectedContactObject item) {
            Log.e("onItemCheck", " Size = " + selectedContactObjects.size());
            Log.e("onItemCheck", " = " + item.getNumber());
            if (!selectedContactObjects.contains(item)) {
                selectedContactObjects.add(item);
               // selectedContactObjectsNew.add(item);
                tvSelectedContacts.setText(selectedContactObjectsNew.size()+" Selected");
            }
            if (!selectedContactObjectsNew.contains(item))
                selectedContactObjectsNew.add(item);
            if(selectedContactObjectsNew.size()>=1)
                ibDoneContacts.setVisibility(View.VISIBLE);

            Log.e("onItemCheck", " Size new = " + selectedContactObjectsNew.size());

            Log.e("onItemCheck", " Size 2 = " + selectedContactObjects.size());
        }

        @Override
        public void onItemUncheck(SelectedContactObject item) {
            Log.e("onItemUncheck", " Size = " + selectedContactObjects.size());
            Log.e("onItemUncheck", " = " + item.getNumber());
            if (selectedContactObjects.contains(item)) {

                selectedContactObjectsNew.remove(item);
                tvSelectedContacts.setText(selectedContactObjectsNew.size()+" Selected");

            }
            if (selectedContactObjectsNew.contains(item))
                selectedContactObjectsNew.remove(item);
            if(selectedContactObjectsNew.size()>=1)
                ibDoneContacts.setVisibility(View.VISIBLE);
            else
                ibDoneContacts.setVisibility(View.GONE);
            Log.e("onItemUncheck", " Size new = " + selectedContactObjectsNew.size());

            Log.e("onItemUncheck", " Size 2 = " + selectedContactObjects.size());
        }
    };


}
