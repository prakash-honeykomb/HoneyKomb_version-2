package com.honeykomb.honeykomb.listeners;

public interface LocationInterface {
    void onTextLocationSelect(String address);

    void onGeoLocationSelect(String name, String address, String lattitude, String longitude);
}
