package com.honeykomb.honeykomb.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
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
import android.widget.Toast;

import com.honeykomb.honeykomb.BuildConfig;
import com.honeykomb.honeykomb.R;
import com.honeykomb.honeykomb.customised.BadgeDrawerArrowDrawable;
import com.honeykomb.honeykomb.dao.AppUser;
import com.honeykomb.honeykomb.database.DataBaseHelper;
import com.honeykomb.honeykomb.network.IPostResponse;
import com.honeykomb.honeykomb.utils.Constants;
import com.honeykomb.honeykomb.utils.RealPathUtils;
import com.honeykomb.honeykomb.utils.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


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
    public LinearLayout navNotificationLL, navListLL, navTermsLL, navPrivacyPolicyLL,navFavGroupLL;
    public BadgeDrawerArrowDrawable badgeDrawable;
    public ActionBarDrawerToggle toggle;

    Bitmap myBitmap;
    Uri picUri;

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    AppUser appUser;
    private final static int ALL_PERMISSIONS_RESULT = 107;


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
        TextView favGroupMenuTV = findViewById(R.id.fav_group_menu_TV);
        TextView privacyMenuTV = findViewById(R.id.privacy_menu_TV);
        navNotificationLL = findViewById(R.id.nav_notification);
        navListLL = findViewById(R.id.nav_list);
        navTermsLL = findViewById(R.id.nav_terms);
        navPrivacyPolicyLL = findViewById(R.id.nav_privacy_policy);
        navFavGroupLL =findViewById(R.id.nav_group);
        // setType face font type for respe;ctive TextView
        ownerNameTV.setTypeface(Util.setTextViewTypeFace(BaseActivity.this, "DroidSans-Bold.ttf"));
        ownerPhoneNumberTV.setTypeface(Util.setTextViewTypeFace(BaseActivity.this, "DroidSans.ttf"));
        versionTV.setTypeface(Util.setTextViewTypeFace(BaseActivity.this, "DroidSans.ttf"));
        notificationMenuTV.setTypeface(Util.setTextViewTypeFace(BaseActivity.this, "DroidSans-Bold.ttf"));
        listMenuTV.setTypeface(Util.setTextViewTypeFace(BaseActivity.this, "DroidSans-Bold.ttf"));
        favGroupMenuTV.setTypeface(Util.setTextViewTypeFace(BaseActivity.this,"DroidSans-Bold.ttf"));
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
        navFavGroupLL.setOnClickListener(this);
        toolbarAddIMV.setOnClickListener(this);
        // set Text for version number
        versionTV.setText(getResources().getString(R.string.app_name) + " " + versionName);
          appUser = Util._db.getAppUser();
        ownerNameTV.setText(appUser.DisplayName);
        ownerPhoneNumberTV.setText(appUser.PhoneNumber);

        Bitmap bitmap;
        File imgFile = new  File(appUser.PhotoPath);

        if(imgFile.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            //ImageView myImage = (ImageView) findViewById(R.id.imageviewTest);
            ownerIMV.setImageBitmap(myBitmap);
           // myImage.setImageBitmap(myBitmap);

        }

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
                startActivity(new Intent(BaseActivity.this,FAVGroup.class));
                //overridePendingTransition(R.anim.slide_right,0);
                //overridePendingTransition(0,0);
            }
        } else if (v == navFavGroupLL) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
                startActivity(new Intent(BaseActivity.this,FAVGroup.class));
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
           //  Toast.makeText(getApplicationContext(),"hai"+appUser.HK_UUID,Toast.LENGTH_SHORT).show();
           // Log.e(TAG, "onClick = ownerIMV");
            startActivityForResult(getPickImageChooserIntent(), 200);

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

    //TODO: added on 18 aug 2018
    public Intent getPickImageChooserIntent() {

        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        // the main intent is the last in the list (fucking android) so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");
         // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }
    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;
    }
    private static Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {

        ExifInterface ei = new ExifInterface(selectedImage.getPath());
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }
    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width=0;
        int height=0;
        try {


        width = image.getWidth();
         height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        }catch (Exception e)
        {
            Toast.makeText(BaseActivity.this,"Please Select Image",Toast.LENGTH_SHORT).show();

        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }


    /**
     * Get the URI of the selected image from {@link #getPickImageChooserIntent()}.<br/>
     * Will return the correct URI for camera and gallery image.
     *
     * @param data the returned data of the activity result
     */
    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }


        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("pic_uri", picUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        picUri = savedInstanceState.getParcelable("pic_uri");
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
//                for (String perms : permissionsToRequest) {
//                    if (hasPermission(perms)) {
//
//                    } else {
//
//                        permissionsRejected.add(perms);
//                    }
//                }

//                if (permissionsRejected.size() > 0) {
//
//
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
//                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
//                                    new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//
//                                                //Log.d("API123", "permisionrejected " + permissionsRejected.size());
//
//                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
//                                            }
//                                        }
//                                    });
//                            return;
//                        }
//                    }
//
//                }
                Util.requestPermission(BaseActivity.this);
                break;
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK)
            return;

        Bitmap bitmap;
        if (resultCode == Activity.RESULT_OK) {

            //  ImageView imageView = (ImageView) findViewById(R.id.imageView);

            if (getPickImageResultUri(data) != null) {
                picUri = getPickImageResultUri(data);

                try {
                    myBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), picUri);
                  //    myBitmap = rotateImageIfRequired(myBitmap, picUri);
                    try {
                        myBitmap = getResizedBitmap(myBitmap, 500);

                    } catch (Exception E)
                    {
                        Toast.makeText(BaseActivity.this,"File type not supported",Toast.LENGTH_SHORT).show();

                        return;
                    }

                    //  CircleImageView croppedImageView = (CircleImageView) findViewById(R.id.img_profile);
//                    croppedImageView.setImageBitmap(myBitmap);
                    ownerIMV.setImageBitmap(myBitmap);
                 //   Util._db.updateOwnerDetails(appUser.HK_UUID,picUri.getPath());
                    Util._db.updateOwnerDetails(appUser.HK_UUID,RealPathUtils.getRealPath(BaseActivity.this,picUri));

                     //  BitmapDrawable background = new BitmapDrawable(getResources(), myBitmap);
                    // eventImageIMV.setBackground(background);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            } else {


                bitmap = (Bitmap) data.getExtras().get("data");
               // picUri = getPickImageResultUri(data);

                myBitmap = bitmap;
              //  RealPathUtils.getRealPath(BaseActivity.this,picUri);

                ImageView croppedImageView = (ImageView) findViewById(R.id.bannerImage);
                if (croppedImageView != null) {
                    // croppedImageView.setImageBitmap(myBitmap);
                    //  BitmapDrawable background = new BitmapDrawable(getResources(), myBitmap);
                    //eventImageIMV.setBackground(background);
                    ownerIMV.setImageBitmap(myBitmap);
                    Util._db.updateOwnerDetails(appUser.HK_UUID,RealPathUtils.getRealPath(BaseActivity.this,picUri));

                }
                //BitmapDrawable background = new BitmapDrawable(getResources(), myBitmap);
                //  eventImageIMV.setBackground(background);

                ownerIMV.setImageBitmap(myBitmap);
                Util._db.updateOwnerDetails(appUser.HK_UUID, RealPathUtils.getRealPath(BaseActivity.this,picUri));

            }

        }
    }


}
