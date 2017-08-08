package com.savor.savorphone.presenter;

import android.content.Context;

import com.savor.savorphone.core.Session;
import com.savor.savorphone.interfaces.IBaseView;
import com.savor.savorphone.interfaces.IEditTextView;

/**
 * Created by hezd on 2017/2/7.
 */

public class EditPicPresenter extends BasePresenter {
    private final IEditTextView mEditTextView;
    private EditState mCurrentState = EditState.NORMAL;

    public enum EditState{
        /**初始状态*/
        NORMAL,
        /**预编辑状态*/
        PREEDIT,
        /**编辑文字状态*/
        EDITTEXT,
    }

    public EditPicPresenter(Context context, IBaseView viewCallBack, IEditTextView editTextView) {
        super(context, viewCallBack);
        this.mEditTextView = editTextView;
    }

    public void setState(EditState state) {
        mCurrentState = state;
    }

    @Override
    public void onCreate() {
        mSession = Session.get(mContext);
    }

    public EditState getState() {
        return mCurrentState;
    }

    public void updateUI(EditState state) {
        mCurrentState = state;
        switch (state) {
            case NORMAL:
                mEditTextView.showNormalUi();
                break;
            case PREEDIT:
                mEditTextView.showPreEditUI();
                break;
            case EDITTEXT:
                mEditTextView.showEditTextUI();
                break;
        }
    }

    @Override
    public void onDestroy() {
        if(mViewCallBack!=null) {
            mViewCallBack = null;
        }
    }
}
