package com.savor.savorphone.push;

import android.content.Context;

/**
 * Created by hezd on 2016/12/13.
 */

public interface IPushHandler<T> {
    void initPush(Context context);
    void handlePushMessage(Context context, T t);
}
