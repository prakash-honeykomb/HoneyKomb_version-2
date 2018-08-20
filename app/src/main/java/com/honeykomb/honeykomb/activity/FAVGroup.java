package com.honeykomb.honeykomb.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.honeykomb.honeykomb.R;
import com.honeykomb.honeykomb.RecycllerView.RecyclerTouchListener;
import com.honeykomb.honeykomb.adapters.GroupsRCVAdapter;
import com.honeykomb.honeykomb.dao.GroupObject;
import com.honeykomb.honeykomb.utils.Constants;
import com.honeykomb.honeykomb.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class FAVGroup extends AppCompatActivity  implements View.OnClickListener{
    public ImageView closeIMV, ownerIMV, toolbarCalenderIMV, toolbarListViewIMV, toolbarAddIMV;
    Button createGroupBTN;
    Toolbar toolbar;
    List<GroupObject> groupList= new ArrayList<>();
    GroupsRCVAdapter groupsRCVAdapter;
    RecyclerView recyclerView;
    private Cursor cursor,cursor1;
    private static final int CONTACTS_FROM_LIST = 1121;
    private static final int CONTACTS = 112;
     LinearLayout linearLayoutNoGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favgroup);
        getSupportActionBar().hide();
        toolbarAddIMV = findViewById(R.id.toolbar_add_IMV);
        createGroupBTN= findViewById(R.id.create_group_BTN);
        toolbarListViewIMV=findViewById(R.id.toolbar_list_view_IMV);
        linearLayoutNoGroup = findViewById(R.id.noGroupsLL);
        recyclerView= (RecyclerView)findViewById(R.id.groups_RCV);
        groupsRCVAdapter= new GroupsRCVAdapter(FAVGroup.this,groupList);
        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
      // recyclerView.addItemDecoration(new Mydivider(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(groupsRCVAdapter);
        prepareMovieData();
     //   int val=1;
        if(groupList.size()>=1)
        {
           linearLayoutNoGroup.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

        } else
            {
                linearLayoutNoGroup.setVisibility(View.VISIBLE);

                recyclerView.setVisibility(View.GONE);
            }
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                GroupObject groupObject = groupList.get(position);
              //  Toast.makeText(getApplicationContext(), groupObject.getGroupID() + " is selected!", Toast.LENGTH_SHORT).show();
            Intent gotoGroupDetails = new Intent(FAVGroup.this,FAVGroupDetails.class);
                Bundle bundle = new Bundle();
                bundle.putString(Constants.GROUP_ID, groupObject.getGroupID());
                bundle.putBoolean("isOwner", true);
                bundle.putString("groupName",  groupObject.getGroupName());
                bundle.putString("hK_UUID", "");
                bundle.putString("QuickbloxGroupID", " ");
                gotoGroupDetails.putExtras(bundle);
              //  startActivityForResult(gotoGroupDetails, CONTACTS_FROM_LIST);
              //  overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                //overridePendingTransition(R.anim.slide_right,R.anim.fab_slide_in_from_left);
               // finish();

                 startActivity(gotoGroupDetails);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        })  );

        toolbarAddIMV.setOnClickListener(this);
        toolbarListViewIMV.setOnClickListener(this);
        createGroupBTN.setOnClickListener(this);
        toolbarListViewIMV.setVisibility(View.GONE);
        //toolbar = findViewById(R.id.toolbar);
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
        toolbarAddIMV.setVisibility(View.VISIBLE);

    }

    @Override
    public void onClick(View view) {
if(view==toolbarAddIMV)
{

    showInputDialog();

}
else if(view==createGroupBTN)
{
    //startActivity(new Intent(FAVGroup.this,AddGroup.class));
    showInputDialog();
}
    }

    public  void prepareMovieData() {
        Cursor c = getCursor();

        while(c.moveToNext()) {
            cursor1 = Util._db.getCursorGroupUsers(cursor.getString(cursor.getColumnIndex("_id")));
            groupList.add(new GroupObject(cursor.getString(cursor.getColumnIndex("groupName")),cursor.getString(cursor.getColumnIndex("_id")), cursor1.getCount()+" members."));
        }
        c.close();

        groupsRCVAdapter.notifyDataSetChanged();
    }


    protected void showInputDialog() {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(FAVGroup.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FAVGroup.this,android.R.style.Theme_Material_Light_Dialog_Alert );
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(editText.getText().toString().trim().isEmpty() || editText.getText().toString().trim()==null ) {

                            editText.setError("Name Can't be empty.");
                            Toast.makeText(FAVGroup.this,"Name Can't be empty.",Toast.LENGTH_LONG).show();

                        } else {
                            Intent i = new Intent(FAVGroup.this, AddGroup.class);
                            i.putExtra("groupname", editText.getText());
                            i.putExtra(Constants.CREATE_OR_UPDATE_GROUP, Constants.CREATE_GROUP);

                            startActivity(i);
                        //    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);

                            // FAVGroup.this.finish();

                            // startActivity(new Intent(FAVGroup.this, AddGroup.class));
                        }
                       // resultText.setText("Hello, " + editText.getText());
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();

        alert.show();
    }

    public Cursor getCursor() {
        cursor = Util._db.getCursorContactNewGroup();
        return cursor;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case (CONTACTS): {
                if (resultCode == Activity.RESULT_OK) {
                    Log.i("FAVGroup: ", "onActivityResult clled:"  );
              /*      ArrayList<String> contactNo = data.getStringArrayListExtra("contactNo");
                  //  ArrayList<String> contactHkUUID = data.getStringArrayListExtra("contacthkUUID");
               //     Log.i(TAG, "contactHkUUIDVALUE Invitee :  " + contactHkUUID.toString());
                    Log.i(TAG, "SELECTEDCONTACT Invitee :" + contactNo.toString());
                    Log.i(TAG, "contactNo Size Invitee :" + contactNo.size());

                    for (int i = 0; i < contactNo.size(); i++) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("QuickBloxID", "");
                        ContactObject cb = Util._db.getUserFromContactNumber(*//*db,*//* contactNo.get(i));
                        if (cb != null) {
                            contentValues.put("hK_UUID", cb.getHkUUID());
                            contentValues.put("QuickBloxID", cb.getQuickBlockID());
                            contentValues.put("QBUserID", cb.getQBUserID());
                        }
                     //   contentValues.put("createdBy", hK_UUID);
                    //    contentValues.put("modifiedBy", hK_UUID);
                     //   contentValues.put("activityUserID", UUID.randomUUID().toString());
                      //  contentValues.put("activityID", activityID);
                        contentValues.put("phoneNumber", contactNo.get(i));
                     //   contentValues.put("countRSVP", "0");
                      //  contentValues.put("invitationStatus", "Pending");
                      //  contentValues.put("userActivityStatus", "Active");
                       // contentValues.put("ActionType", "ADD");

                        Util._db.insertOrUpdateSLOfINV(contentValues);
                        dataChangedInvitee = true;
                        if (Util._db.getActionTypeBasedOnActivityID(activityID)) {
                            Util._db.updateActivitySelectedListOfInvitees(activityID);

                        }
                        qbGroupID = Util._db.getQBGroupIDBasedOnActivityID(activityID);
                        if (qbGroupID != null && qbGroupID.trim().length() > 2 && cb != null && cb.getHkID() != null && !cb.getHkID().equalsIgnoreCase("")) {
                            String UniqueID = UUID.randomUUID().toString();
                            Util._db.saveUserWithDBasParams(UniqueID, Integer.parseInt(qbGroupID), cb.getHkID(), "ADD", "NO");
//                            UtilityHelper.addUserToGroup(UniqueID, SelectedListOfInvitees.this, Integer.parseInt(qbGroupID), cb.getHkID());
                        }
                    }
                    selectedInviteesAdapter = new SelectedInviteesAdapter(SelectedListOfInvitees.this*//*getApplicationContext()*//*, activityID, isOwner, hK_UUID, isCompleted, authenticationKey, disableAdd);
                    ll_selected_contacts.setAdapter(selectedInviteesAdapter);*/

                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(FAVGroup.this,MainScreen.class));
       // finish();
        super.onBackPressed();
    }
}
