package com.honeykomb.honeykomb.listeners;

public interface DialogListener {

    void onDialogResultSuccess(Object result, Boolean text);

    void onDialogResultSuccess(Object result, Boolean check, Boolean text);

    void onDialogResultFailed();
}
