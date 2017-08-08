package com.savor.savorphone.interfaces;

/**
 * Created by luminita on 2016/12/12.
 */

public abstract class AsyncCallBack {


    /**
     * 开启异步任务前的操作
     */
    public void onPreExecute() {
    }

    /**
     * 执行异步任重中的操作
     */
    public abstract void doInBackground();

    /**
     * 执行异步任务后的操作
     */
    public abstract void onPostExecute();

}
