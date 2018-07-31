package com.honeykomb.honeykomb.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.honeykomb.honeykomb.async.AddActivityAsync;
import com.honeykomb.honeykomb.utils.UtilityHelper;

import java.util.concurrent.Executor;

/**
 * Created by Rajashekar.Nimmala on 7/19/2017.
 */

public class SendAllActivityToServer extends IntentService {

    private String TAG = SendAllActivityToServer.class.getSimpleName();

    public SendAllActivityToServer() {
        super("SendAllActivityToServer");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Log.i(TAG, "----onHandleIntent----");
            executeRequest();
        } catch (Exception e) {
            Log.i(TAG + "----Exception----", ":" + e.getMessage());
            this.stopSelf();
        }
        this.stopSelf();
    }

    private void executeRequest() {
        Executor threadPoolExecutor = UtilityHelper.getExecutor();
        new AddActivityAsync(getApplicationContext()).executeOnExecutor(threadPoolExecutor);
    }

}
