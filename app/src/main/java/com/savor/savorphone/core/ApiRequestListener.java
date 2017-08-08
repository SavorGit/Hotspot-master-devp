package com.savor.savorphone.core;


public interface ApiRequestListener {
    /**
     * The CALLBACK for success aMarket API HTTP response
     */
    void onSuccess(AppApi.Action method, Object obj);

    /**
     * The CALLBACK for failure aMarket API HTTP response
     */
    void onError(AppApi.Action method, Object obj);


    void onNetworkFailed(AppApi.Action method);
}
