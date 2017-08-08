package com.common.api.utils;

import android.app.Activity;
import android.content.Intent;

/**
 * 支付工具类
 * 
 * @author liaiguo
 * 
 */
public class PayUtils {

	public static void launchAlipay(Activity activity, String info, int requestCode) {
		Intent intent = new Intent();
		// intent.setPackage(getPackageName());
		intent.setPackage("com.lashou.groupurchasing");
		intent.setAction("com.alipay.mobilepay.android");
		intent.putExtra("order_info", info);
		activity.startActivityForResult(intent, requestCode);
	}

}
