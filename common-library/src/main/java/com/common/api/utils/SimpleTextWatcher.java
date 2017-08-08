package com.common.api.utils;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * 简单的继承TextWatcher，减小上层不必要的代码量
 * Created by zhanghq on 2016/7/3.
 */
public abstract class SimpleTextWatcher implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
