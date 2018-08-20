package com.honeykomb.honeykomb.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcel;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.honeykomb.honeykomb.R;
import com.honeykomb.honeykomb.adapters.GroupUsersAdapter;
import com.honeykomb.honeykomb.dao.ContactObject;
import com.honeykomb.honeykomb.dao.ContactsObject;
import com.honeykomb.honeykomb.dao.SelectedContactObject;
import com.honeykomb.honeykomb.database.DataBaseHelper;
import com.honeykomb.honeykomb.utils.Constants;
import com.honeykomb.honeykomb.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class FAVGroupDetails extends AppCompatActivity {
    private CollapsingToolbarLayout collapsingToolbarLayout = null;
    Toolbar toolbar;
    TextView toolbarTitle;
    private String TAG = FAVGroupDetails.class.getSimpleName();
    private String groupID = "";
    private String groupName= "HoneyKomb";
    private String authenticationKey;
    //private List<SelectedContactObject> selectedContactObjects = null;
    List<ContactObject> groupUsersList= new ArrayList<>();
    public static ArrayList<SelectedContactObject> selectedContactObjects = null;

    private boolean dataChangedInvitee = false;
    private static final int CONTACTS = 112;
    private ListView ll_selected_contacts;
    private GroupUsersAdapter groupUsersAdapter;
    private Cursor cursor;
    private ImageView addUsersIMV;
  public  static TextView groupNameTV,grupSizeTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  initDB(this);

        if (getIntent() != null) {
            if (getIntent().getExtras() != null) {

                groupID = getIntent().getExtras().getString(Constants.GROUP_ID);
                groupName= getIntent().getExtras().getString("groupName");


                SharedPreferences sp = getSharedPreferences("HONEY_PREFS", Activity.MODE_PRIVATE);
                authenticationKey = sp.getString("authenticationKey", "");
            }
        }
        setContentView(R.layout.activity_favgroup_details);
        getSupportActionBar().hide();
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        toolbar = findViewById(R.id.toolbar);
        toolbarTitle= (TextView)findViewById(R.id.tittle_TV);
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
        dynamicToolbarColor();

       toolbarTextAppernce();
       changeToolBarText();

        ll_selected_contacts = findViewById(R.id.ll_selected_contacts);
        groupNameTV=findViewById(R.id.groupNameTV);
        grupSizeTV=findViewById(R.id.memberListTV);
          addUsersIMV=findViewById(R.id.toolbar_add_IMV);
          addUsersIMV.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                   selectedContactObjects = Util._db.getGroupUsersINArrayList(groupID);
                //  selectedContactObjects = Util._db.getActivityUsers(activityID);
                  Intent i = new Intent(FAVGroupDetails.this, AddGroup.class);
                  i.putExtra("groupname", groupName);
                  i.putExtra(Constants.GROUP_ID, groupID);
                  i.putParcelableArrayListExtra("selectedContactObjects", selectedContactObjects);
                  i.putExtra("from", "FAVGroupDetails");

                  i.putExtra(Constants.CREATE_OR_UPDATE_GROUP, Constants.UPDATE_GROUP);



                  startActivity(i);
                  //overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);

                  //  FAVGroupDetails.this.finish();

              }
          });
      // selectedContactObjects = Util._db.getGroupUsersINArrayList(groupID);

       // groupUsersAdapter = new GroupUsersAdapter(FAVGroupDetails.this/*getApplicationContext()*/, activityID, true, "", "", authenticationKey, "");
        groupUsersAdapter = new GroupUsersAdapter(FAVGroupDetails.this/*getApplicationContext()*/, groupUsersList);

        ll_selected_contacts.setAdapter(groupUsersAdapter);
        ColorDrawable dividerColor = new ColorDrawable(getApplicationContext().getResources().getColor(R.color.color_username_rectangle));
        ll_selected_contacts.setDivider(dividerColor);
        ll_selected_contacts.setDividerHeight(1);

        prepareMovieData(groupID);
        groupNameTV.setText(groupName);
        grupSizeTV.setText(groupUsersList.size()+" Users");
    }

private  void  changeToolBarText()
{
    AppBarLayout appbar = (AppBarLayout)findViewById(R.id.appbar);
    appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
        boolean isVisible = true;
        int scrollRange = -1;
        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            if (scrollRange == -1) {
                scrollRange = appBarLayout.getTotalScrollRange();
            }
            if (scrollRange + verticalOffset == 0) {
                toolbarTitle.setText(groupName);

                isVisible = true;
            } else if(isVisible) {

                toolbarTitle.setText(" ");
                isVisible = false;
            } else {
                toolbarTitle.setText(" ");
            }
        }
    });
}


    private void dynamicToolbarColor() {

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.mipmap.groups_no);
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {

             collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.white));
                collapsingToolbarLayout.setStatusBarScrimColor(getResources().getColor(R.color.white));
              collapsingToolbarLayout.setBackgroundColor(getResources().getColor(R.color.white));
            }
        });
    }


    private void toolbarTextAppernce() {
         collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.collapsedappbar);
       collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.expandedappbar);
    }

    public void initDB(Context ctx) {
        try {
            if (Util._db == null) {
                Util._db = new DataBaseHelper(ctx);
                Util._db.open();
            } else if (!Util._db.isOpen()) {
                Util._db.open();
            }
            Util.BackupDatabase();
        } catch (Exception e) {
            Log.i(TAG, "initDB Exception = " + e.getMessage());
        }
    }

    public  void prepareMovieData(String groupID) {
        Cursor c = getCursor(groupID);
        ArrayList<ContactsObject> values= new ArrayList<>();
selectedContactObjects =  new ArrayList<>();

//        while(c.moveToNext()) {
//            groupUsersList.add(new ContactObject(cursor.getString(cursor.getColumnIndex("contactNo")),cursor.getString(cursor.getColumnIndex("contactNo")), cursor.getString(cursor.getColumnIndex("_id"))));
//        }
        if (cursor != null) {
            if (cursor.getCount() != 0) {
                while (cursor.moveToNext()) {
                    groupUsersList.add(new ContactObject(cursor.getString(cursor.getColumnIndex("contactNo")),cursor.getString(cursor.getColumnIndex("contactNo")), cursor.getString(cursor.getColumnIndex("_id"))));

                    SelectedContactObject user = new SelectedContactObject(Parcel.obtain());
                    String groupId = cursor.getString(cursor.getColumnIndex("groupID"));
                    String contactNo = cursor.getString(cursor.getColumnIndex("contactNo"));

                    user.setNumber(contactNo);
                    user.setHkID(groupId);
                    selectedContactObjects.add(user);
                }
            }
            cursor.close();
        }
        c.close();

   //     groupsRCVAdapter.notifyDataSetChanged();


      /*  ArrayList<ContactsObject> values= new ArrayList<>();
        // ContactsObject user= new ContactsObject(Parcel.obtain());
        String query = "SELECT * FROM user_group_details WHERE groupID = '" + groupID + "'";
        Cursor cursor = db.rawQuery(query, null);



        if (cursor != null) {
            if (cursor.getCount() != 0) {
                while (cursor.moveToNext()) {
                    ContactsObject user = new ContactsObject(Parcel.obtain());
                    String groupId = cursor.getString(cursor.getColumnIndex("groupID"));
                    String contactNo = cursor.getString(cursor.getColumnIndex("contactNo"));

                    user.setContactNo(contactNo);
                    user.setHkID(groupId);
                    values.add(user);
                }
            }
            cursor.close();
        }*/
    }
    public Cursor getCursor(String groupID) {
        cursor = Util._db.getCursorGroupUsers(groupID);
        return cursor;
    }

//    @Override
//    public void finish() {
//        groupUsersAdapter = (GroupUsersAdapter) ll_selected_contacts.getAdapter();
//        if (dataChangedInvitee || groupUsersAdapter.dataChangedInviteeAdapter) {
//            Util._db.updateGroupSelectedListOfUsers(groupID);
//            Intent intent = new Intent(FAVGroupDetails.this, FAVGroup.class);
//            setResult(Activity.RESULT_OK, intent);
//        }
//        super.finish();
//    }
 @Override
public void onBackPressed() {
   // startActivity(new Intent(FAVGroupDetails.this,FAVGroup.class));
    Intent i= new Intent(FAVGroupDetails.this,FAVGroup.class);
     startActivity(i);
  overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
   // finish();
  //  super.onBackPressed();
}
}
