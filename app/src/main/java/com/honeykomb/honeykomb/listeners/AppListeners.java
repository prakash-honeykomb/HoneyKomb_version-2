package com.honeykomb.honeykomb.listeners;

import android.os.Bundle;
import android.os.Parcelable;

import com.honeykomb.honeykomb.utils.UtilityHelper;

public class AppListeners {
    public interface getReminderListener extends Parcelable {
        void OnGetReminderListener(String reminderTime);
    }

    public interface getSelectedContacts extends Parcelable {
        void onContactsSelectedListener(Bundle selectedContactObjects);

    }

    public interface SingleDialogListener {
        void onSingleButton(UtilityHelper.ButtonNavigation value);
    }

    public interface MyListener {
        void onCallBack();
    }

    public interface OnItemClickListener {
        public void onClick(Object contactObject);
    }

    public interface RefreshMainScreen {
        public void onRefresh(Object object);
    }
}