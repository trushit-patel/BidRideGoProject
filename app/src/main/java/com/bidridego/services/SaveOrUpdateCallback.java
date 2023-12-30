package com.bidridego.services;

public interface SaveOrUpdateCallback {
    void onSuccess();

    void onFailure(String errorMessage);
}
