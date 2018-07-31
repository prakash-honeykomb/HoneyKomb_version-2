package com.honeykomb.honeykomb.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.honeykomb.honeykomb.R;
import com.honeykomb.honeykomb.utils.Constants;

public class PrivacyPolicy extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.terms_of_use);
        WebView webView = findViewById(R.id.web_view);
        // set up tool bar views
        titleTV.setText(getResources().getString(R.string.privacy_policy));
        toolbarCalenderIMV.setVisibility(View.GONE);
        toolbarListViewIMV.setVisibility(View.GONE);
        toolbarAddIMV.setVisibility(View.GONE);
        saveEventTV.setVisibility(View.GONE);

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
        webView.loadUrl(Constants.PRIVACY_URL);
    }

    @Override
    public void setContentLayout(int layout) {
        v = inflater.inflate(layout, null);
        lnr_content.addView(v);
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
