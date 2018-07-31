package com.honeykomb.honeykomb.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.honeykomb.honeykomb.R;
import com.honeykomb.honeykomb.utils.Constants;

public class DemoActivity extends BaseActivity {

    private RelativeLayout oneRL, twoRL, threeRL, fourRL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity);
        oneRL = findViewById(R.id.demo_one_RL);
        twoRL = findViewById(R.id.demo_two_RL);
        threeRL = findViewById(R.id.demo_three_RL);
        fourRL = findViewById(R.id.demo_four_RL);

    }

    public void demoOne(View v) {
        oneRL.setVisibility(View.GONE);
        twoRL.setVisibility(View.VISIBLE);
        threeRL.setVisibility(View.GONE);
        fourRL.setVisibility(View.GONE);
    }

    public void demoTwo(View v) {
        oneRL.setVisibility(View.GONE);
        twoRL.setVisibility(View.GONE);
        threeRL.setVisibility(View.VISIBLE);
        fourRL.setVisibility(View.GONE);
    }

    public void demoThree(View v) {
        oneRL.setVisibility(View.GONE);
        twoRL.setVisibility(View.GONE);
        threeRL.setVisibility(View.GONE);
        fourRL.setVisibility(View.VISIBLE);
    }

    public void demoFour(View v) {
        SharedPreferences spRaj = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editorRaj = spRaj.edit();
        editorRaj.putBoolean(Constants.DEMO_SHOWN, true);
        editorRaj.apply();
        Intent intent = new Intent(DemoActivity.this, MainScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void setContentLayout(int layout) {

    }

    @Override
    public void doRequest(String url) {

    }

    @Override
    public void parseJsonResponse(String response, String requestType) {

    }

    @Override
    public String getValues() {
        return null;
    }
}
