package com.honeykomb.honeykomb.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.honeykomb.honeykomb.R;
import com.honeykomb.honeykomb.adapters.ContactsSelectedAdapter;
import com.honeykomb.honeykomb.dao.SelectedContactObject;
import com.honeykomb.honeykomb.utils.Constants;

import java.util.ArrayList;

public class ContactSelected extends Activity implements View.OnClickListener {

    private static final String TAG = "ContactSelected";
    private static final int CONTACTS = 112;
    private ListView ll_selected_contacts;
    private ArrayList<String> contactNo = new ArrayList<>();
    private ArrayList<String> contactHkID = new ArrayList<>();
    private ContactsSelectedAdapter contactsSelectedAdapter;
    private ArrayList<SelectedContactObject> selectedContactObjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selected_invitees);
        ImageView done_IMV = findViewById(R.id.done_IMV);
        ImageView iv_add_invbitees = findViewById(R.id.iv_select_invitees);
        ll_selected_contacts = findViewById(R.id.ll_selected_contacts);

        iv_add_invbitees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedContactObjects != null && selectedContactObjects.size() > 0) {
                    Intent intentInvitee = new Intent(getApplicationContext(), Contact.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("from", "addButton");
                    bundle.putParcelableArrayList(Constants.SELECTED_OBJECT_KEY, selectedContactObjects);
                    intentInvitee.putExtras(bundle);
                    startActivityForResult(intentInvitee, CONTACTS);
                } else {
                    Intent intentInvitee = new Intent(getApplicationContext(), Contact.class);
                    startActivityForResult(intentInvitee, CONTACTS);
                }
            }
        });

        done_IMV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // if selected contacts list size is not zero, bundle will carry selected No of values to called activity onActivityResult method.
                if (selectedContactObjects.size() > 0) {
                    Intent intent = new Intent(ContactSelected.this, AddEventActivity.class);
                    Log.i(TAG, "contactNo size =  " + contactNo.size());
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList(Constants.SELECTED_OBJECT_KEY, selectedContactObjects);
                    intent.putExtras(bundle);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } else {
                    // if selected contacts list size is zero, bundle will carry zero values to called activity onActivityResult method.
                    Intent intent = new Intent(ContactSelected.this, AddEventActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList(Constants.SELECTED_OBJECT_KEY, selectedContactObjects);
                    intent.putExtras(bundle);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });
        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            selectedContactObjects = new ArrayList<>();
            selectedContactObjects = bundle.getParcelableArrayList(Constants.SELECTED_OBJECT_KEY);
            if (selectedContactObjects != null && selectedContactObjects.size() != 0) {
                Log.i(TAG, "selectedContactObjects Create 2 = " + selectedContactObjects.size());
                contactsSelectedAdapter = new ContactsSelectedAdapter(ContactSelected.this, selectedContactObjects);
                ll_selected_contacts.setAdapter(contactsSelectedAdapter);
                ColorDrawable dividerColor = new ColorDrawable(getApplicationContext().getResources().getColor(R.color.color_username_rectangle));
                ll_selected_contacts.setDivider(dividerColor);
                ll_selected_contacts.setDividerHeight(1);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (selectedContactObjects.size() > 0) {
            Intent intent = new Intent(ContactSelected.this, AddEventActivity.class);
            Log.i(TAG, "contactNo size =  " + contactNo.size());
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(Constants.SELECTED_OBJECT_KEY, selectedContactObjects);
            intent.putExtras(bundle);
            setResult(Activity.RESULT_OK, intent);
            finish();
        } else {
            Intent intent = new Intent(ContactSelected.this, AddEventActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(Constants.SELECTED_OBJECT_KEY, selectedContactObjects);
            intent.putExtras(bundle);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case (CONTACTS): {
                if (resultCode == Activity.RESULT_OK) {

                    contactNo = data.getStringArrayListExtra("contactNo");
                    ArrayList<String> contactName = data.getStringArrayListExtra("contactName");
                    if (data.getStringArrayListExtra("contacthkID") != null || data.getStringArrayListExtra("contacthkID").equals(" ")) {
                        contactHkID = data.getStringArrayListExtra("contacthkID");
                    }
                    ArrayList<String> contactHkUUID = data.getStringArrayListExtra("contacthkUUID");
                    Log.i(TAG, "contactHkUUIDVALUE :  " + contactHkUUID.toString());
                    Log.i(TAG, "SELECTEDCONTACT :" + contactNo.toString());
                    Log.i(TAG, "contactNo Size :" + contactNo.size());

                    if (selectedContactObjects != null && selectedContactObjects.size() != 0) {
                        for (int i = 0; i < contactNo.size(); i++) {
                            SelectedContactObject sb = new SelectedContactObject(Parcel.obtain());
                            sb.setName(contactName.get(i));
                            sb.setNumber(contactNo.get(i));
                            if (contactHkID.get(i) != null || !contactHkID.get(i).equals("")) {
                                sb.setHkID(contactHkID.get(i));
                            }
                            sb.setHkUUID(contactHkUUID.get(i));
                            selectedContactObjects.add(sb);
                            ArrayList<SelectedContactObject> ltc2 = new ArrayList<>();// unique
                            for (SelectedContactObject element : selectedContactObjects) {
                                if (!ltc2.contains(element)) {
                                    System.out.println(element);
                                    ltc2.add(element);
                                }
                            }
                            contactsSelectedAdapter = new ContactsSelectedAdapter(ContactSelected.this, ltc2);
                            ll_selected_contacts.setAdapter(contactsSelectedAdapter);
                            ColorDrawable dividerColor = new ColorDrawable(getApplicationContext().getResources().getColor(R.color.color_username_rectangle));
                            ll_selected_contacts.setDivider(dividerColor);
                            ll_selected_contacts.setDividerHeight(1);

                        }
                    } else {
                        for (int i = 0; i < contactNo.size(); i++) {
                            SelectedContactObject sb = new SelectedContactObject(Parcel.obtain());
                            sb.setName(contactName.get(i));
                            sb.setNumber(contactNo.get(i));
                            if (contactHkID.get(i) != null || !contactHkID.get(i).equals("")) {
                                sb.setHkID(contactHkID.get(i));
                            }
                            sb.setHkUUID(contactHkUUID.get(i));
                            selectedContactObjects.add(sb);
                            ArrayList<SelectedContactObject> ltc2 = new ArrayList<>();// unique
                            for (SelectedContactObject element : selectedContactObjects) {
                                if (!ltc2.contains(element)) {
                                    System.out.println(element);
                                    ltc2.add(element);
                                }
                            }
                            contactsSelectedAdapter = new ContactsSelectedAdapter(ContactSelected.this, ltc2);
                            ll_selected_contacts.setAdapter(contactsSelectedAdapter);
                            ColorDrawable dividerColor = new ColorDrawable(getApplicationContext().getResources().getColor(R.color.color_username_rectangle));
                            ll_selected_contacts.setDivider(dividerColor);
                            ll_selected_contacts.setDividerHeight(1);

                        }
                    }
                    if (selectedContactObjects != null) {
                        Log.i(TAG, "selectedContactObjects On Result = " + selectedContactObjects.size());
                    }
                }
                break;
            }
            default:
                break;
        }
    }

}
