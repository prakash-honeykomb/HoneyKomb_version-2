package com.honeykomb.honeykomb.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.honeykomb.honeykomb.BuildConfig;
import com.honeykomb.honeykomb.R;
import com.honeykomb.honeykomb.customised.BadgeDrawerArrowDrawable;
import com.honeykomb.honeykomb.dao.AppUser;
import com.honeykomb.honeykomb.database.DataBaseHelper;
import com.honeykomb.honeykomb.network.IPostResponse;
import com.honeykomb.honeykomb.utils.Constants;
import com.honeykomb.honeykomb.utils.Util;


public abstract class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, IPostResponse, View.OnClickListener {

    public static final String TAG = Constants.APP_NAME;
    public Toolbar toolbar;
    public LinearLayout lnr_content;
    public FloatingActionButton fab;
    public View v;
    public LayoutInflater inflater;
    public DrawerLayout drawer;
    public ImageView closeIMV, ownerIMV, toolbarCalenderIMV, toolbarListViewIMV, toolbarAddIMV;
    public TextView titleTV, saveEventTV;
    public LinearLayout navNotificationLL, navListLL, navTermsLL, navPrivacyPolicyLL;
    public BadgeDrawerArrowDrawable badgeDrawable;
    public ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDB(this);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        lnr_content = findViewById(R.id.lnr_content);
        drawer = findViewById(R.id.drawer_layout);
        fab = findViewById(R.id.fab);
        titleTV = findViewById(R.id.tittle_TV);
        setSupportActionBar(toolbar);
        initViews();
        inflater = getLayoutInflater();
        fab.setVisibility(View.GONE);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @SuppressLint("SetTextI18n")
    private void initViews() {

        String versionName = BuildConfig.VERSION_NAME;

        closeIMV = findViewById(R.id.close_IMV);
        ownerIMV = findViewById(R.id.owner_IMV);
        saveEventTV = findViewById(R.id.toolbar_save_TV);
        toolbarCalenderIMV = findViewById(R.id.toolbar_calender_IMV);
        toolbarListViewIMV = findViewById(R.id.toolbar_list_view_IMV);
        toolbarAddIMV = findViewById(R.id.toolbar_add_IMV);
        TextView ownerNameTV = findViewById(R.id.owner_name_TV);
        TextView ownerPhoneNumberTV = findViewById(R.id.owner_phone_number_TV);

        TextView versionTV = findViewById(R.id.version_TV);
        TextView notificationMenuTV = findViewById(R.id.notification_menu_TV);
        TextView listMenuTV = findViewById(R.id.list_menu_TV);
        TextView termsOfUseMenuTV = findViewById(R.id.terms_of_use_menu_TV);
        TextView privacyMenuTV = findViewById(R.id.privacy_menu_TV);
        navNotificationLL = findViewById(R.id.nav_notification);
        navListLL = findViewById(R.id.nav_list);
        navTermsLL = findViewById(R.id.nav_terms);
        navPrivacyPolicyLL = findViewById(R.id.nav_privacy_policy);

        // setType face font type for respective TextView
        ownerNameTV.setTypeface(Util.setTextViewTypeFace(BaseActivity.this, "DroidSans-Bold.ttf"));
        ownerPhoneNumberTV.setTypeface(Util.setTextViewTypeFace(BaseActivity.this, "DroidSans.ttf"));
        versionTV.setTypeface(Util.setTextViewTypeFace(BaseActivity.this, "DroidSans.ttf"));
        notificationMenuTV.setTypeface(Util.setTextViewTypeFace(BaseActivity.this, "DroidSans-Bold.ttf"));
        listMenuTV.setTypeface(Util.setTextViewTypeFace(BaseActivity.this, "DroidSans-Bold.ttf"));
        termsOfUseMenuTV.setTypeface(Util.setTextViewTypeFace(BaseActivity.this, "DroidSans-Bold.ttf"));
        privacyMenuTV.setTypeface(Util.setTextViewTypeFace(BaseActivity.this, "DroidSans-Bold.ttf"));
        titleTV.setTypeface(Util.setTextViewTypeFace(BaseActivity.this, "DroidSans-Bold.ttf"));
        saveEventTV.setTypeface(Util.setTextViewTypeFace(BaseActivity.this, "DroidSans.ttf"));
        // set onclick listener
        closeIMV.setOnClickListener(this);
        ownerIMV.setOnClickListener(this);
        navNotificationLL.setOnClickListener(this);
        navListLL.setOnClickListener(this);
        navTermsLL.setOnClickListener(this);
        navPrivacyPolicyLL.setOnClickListener(this);
        toolbarAddIMV.setOnClickListener(this);
        // set Text for version number
        versionTV.setText(getResources().getString(R.string.app_name) + " " + versionName);
        AppUser appUser = Util._db.getAppUser();
        ownerNameTV.setText(appUser.DisplayName);
        ownerPhoneNumberTV.setText(appUser.PhoneNumber);

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public abstract void setContentLayout(int layout);

    @Override
    public void onClick(View v) {
        if (v == closeIMV) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
        } else if (v == navNotificationLL) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(BaseActivity.this, NotificationsActivity.class);
                startActivity(intent);
            }
        } else if (v == navListLL) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
        } else if (v == navTermsLL) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(BaseActivity.this, TermsOfUseActivity.class);
                startActivity(intent);
            }
        } else if (v == navPrivacyPolicyLL) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(BaseActivity.this, PrivacyPolicy.class);
                startActivity(intent);
            }
        } else if (v == ownerIMV) {
            Log.e(TAG, "onClick = ownerIMV");
        }
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
//            Util.CreateCMSAppFolder(getApplicationContext());
        } catch (Exception e) {
            Log.e(TAG, " Exception = " + e.getMessage());
        }
    }
}
