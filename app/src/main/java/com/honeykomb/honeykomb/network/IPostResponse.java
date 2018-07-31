package com.honeykomb.honeykomb.network;

/**
 * Created by laxmanamurthy on 10/1/2016.
 */

public interface IPostResponse {

    public void doRequest(String url);
    public void parseJsonResponse(String response, String requestType);
    public String getValues();
}
