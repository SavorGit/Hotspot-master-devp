/*
 * Copyright (C) 2010 mAPPn.Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.common.api.utils;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import com.common.api.codec.binary.Base64;

import android.text.TextUtils;



/**
 * This class provides some security utility method. 
 * 
 * @author andrew.wang
 * @date    2010-9-19
 * @since   Version 0.4.0
 */
public class SecurityUtil {

    private static final byte[] SECRET_KEY_NORMAL;
    private static final byte[] SECRET_KEY_HTTP;
    private static final byte[] SECRET_KEY_HTTP_CHARGE;
    public static final byte[] SECRET_KEY_HTTP_CHARGE_ALIPAY;
    
    public  static final String KEY_HTTP_CHARGE_ALIPAY = "h9sEVED84X81u9ev";
    
    static {
        SECRET_KEY_NORMAL =  DigestUtils.md5(DigestUtils.md5("7U727ALEWH8".getBytes()));
        SECRET_KEY_HTTP = "sdk_mappn_201008".getBytes();
        SECRET_KEY_HTTP_CHARGE = "MAPPN-ANDY-XIAN-".getBytes();
        SECRET_KEY_HTTP_CHARGE_ALIPAY = KEY_HTTP_CHARGE_ALIPAY.getBytes();
    }
    
    public static String decrypt(String encValue) {
        if (TextUtils.isEmpty(encValue))
            return "";

        byte[] bytes = Base64.decodeBase64(AppUtils.getUTF8Bytes(encValue));
        if (bytes == null)
            return "";

        bytes = new Crypter().decrypt(bytes, SECRET_KEY_NORMAL);
        if (bytes == null)
            return "";

        return AppUtils.getUTF8String(bytes);
    }
    
    public static String encrypt(String value) {
        if (value == null)
            return null;

        byte[] bytes = AppUtils.getUTF8Bytes(value);
        bytes = new Crypter().encrypt(bytes, SECRET_KEY_NORMAL);
        bytes = Base64.encodeBase64(bytes);

        return AppUtils.getUTF8String(bytes);
    }
    
    public static String encryptPassword(String targetText, String publicKey) {
        byte[] key = DigestUtils.md5(AppUtils.getUTF8Bytes(publicKey));
        swapBytes(key);
        reverseBits(key);
        byte[] enc = new Crypter().encrypt(AppUtils.getUTF8Bytes(targetText), key);
        return AppUtils.getUTF8String(Base64.encodeBase64(enc));
    }
    
    public static byte[] encryptHttpBody(String body) {
        return Base64.encodeBase64(
        		new Crypter().encrypt(AppUtils.getUTF8Bytes(body), SECRET_KEY_HTTP));
    }
    
	public static byte[] encryptHttpChargeBody(String body) {
		return new Crypter().encrypt(AppUtils.getUTF8Bytes(body), SECRET_KEY_HTTP_CHARGE);
	}
	
	public static byte[] encryptHttpChargePalipayBody(String body) {
		return Base64.encodeBase64(new Crypter().encrypt(AppUtils.getUTF8Bytes(body), SECRET_KEY_HTTP_CHARGE_ALIPAY));
	}
    
    /**
     * 
     * @param entity
     * @return
     */
    public static byte[] decryptHttpEntity(HttpEntity entity) {
        byte[] buffer = null;
        try {
            buffer = EntityUtils.toByteArray(entity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (buffer != null) {
            return new Crypter().decrypt(buffer, SECRET_KEY_HTTP);
        }
        return buffer;
    }
    
    private static void swapBytes(byte[] b) {
        for (int i = 0; i < b.length; i += 2) {
            byte tmp = b[i];
            b[i] = b[i + 1];
            b[i + 1] = tmp;
        }
    }

    private static void reverseBits(byte[] b) {
        for (int i = 0; i < b.length; i++)
            b[i] ^= 0xFF;
    }

}
