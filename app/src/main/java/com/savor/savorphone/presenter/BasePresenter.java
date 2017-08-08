package com.savor.savorphone.presenter;

import android.content.Context;

import com.savor.savorphone.core.Session;
import com.savor.savorphone.interfaces.IBaseView;

/**
 * Created by hezd on 2016/11/7.
 *
 * BasePresent主导器基类，类似于BaseActivity在此类中定义一些通用操作。
 * 模拟生命周期方法oncreate，onDestroy，做相关初始化和资源释放工作。
 */

public abstract class BasePresenter<V extends IBaseView> {
    protected Context mContext;
    protected V mViewCallBack;
    protected Session mSession;

    public BasePresenter(Context context, V viewCallBack) {
        this.mContext = context;
        this.mViewCallBack = viewCallBack;
        mSession = Session.get(mContext);
        initModel();
        onCreate();
    }

    public void initModel() {}
    public abstract void onCreate();
    public abstract void onDestroy();
}
