package com.common.api.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.util.Log;
import android.view.KeyEvent;

public class ShowProgress {
    private static ProgressDialog dialog;
	/**
     * Show Warning to user.
     */
    public static void ShowProgressOn(Context context, String strTitle, String strMessage) {
        dialog = new ProgressDialog(context);
        dialog.setTitle(strTitle);
        dialog.setMessage(strMessage);
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				switch(keyCode){
				case KeyEvent.KEYCODE_SEARCH:
					Log.d("test", "search key was pressed");
					return true;
				}
				return false;
			}
		});
        dialog.show();
    }

    public static void ShowProgressOff() {
        if (dialog != null)
            dialog.dismiss();
    }
   
}
