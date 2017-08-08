package com.common.api.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * <p>
 * Title: 本地存储SharedPreferences
 * </p>
 * <p>
 * Created: 11-06-11
 * 
 * @author Lizhiwo_wo
 * @version 1.0
 */
public class LocalStorageUtils {

    protected static SharedPreferences settings;
    protected static SharedPreferences.Editor editor;

    public LocalStorageUtils(Context connext, String name) {
        settings = connext.getSharedPreferences(name, Context.MODE_WORLD_READABLE+Context.MODE_WORLD_WRITEABLE);
        editor = settings.edit();
    }

    /**
     * Load the value referred to the configuration given the key
     * 
     * @param key
     *            the String formatted key representing the value to be loaded
     * @return String String formatted vlaue related to the give key
     */
    protected String loadKey(String key) {
        return settings.getString(key, null);
    }

    /**
     * Save the loaded twin key-value using the android context package SharedPreferences.Editor
     * instance
     * 
     * @param key
     *            the key to be saved
     * @param value
     *            the value related to the key String formatted
     */
    protected void saveKey(String key, String value) {
    	
        editor.putString(key, value);
        editor.commit();
    }

    public void removeKey(String key) {
        editor.remove(key);
        editor.commit();
    }

    public boolean loadBooleanKey(String key, boolean defaultValue) {
        String v = loadKey(key);
        boolean bv;
        if (v == null) {
            bv = defaultValue;
        } else {
            if (v.equals("TRUE")) {
                bv = true;
            } else {
                bv = false;
            }
        }
        return bv;
    }

    public void saveBooleanKey(String key, boolean value) {
        String v;
        if (value) {
            v = "TRUE";
        } else {
            v = "FALSE";
        }
        saveKey(key, v);
    }

    public int loadIntKey(String key, int defaultValue) {
        String v = loadKey(key);
        int iv;
        if (v == null) {
            iv = defaultValue;
        } else {
            try {
                iv = Integer.parseInt(v);
            } catch (Exception e) {
                iv = defaultValue;
            }
        }
        return iv;
    }

    public void saveIntKey(String key, int value) {
        String v = String.valueOf(value);
        saveKey(key, v);
    }

    public long loadLongKey(String key, long defaultValue) {
        String v = loadKey(key);
        long iv;
        if (v == null) {
            iv = defaultValue;
        } else {
            try {
                iv = Long.parseLong(v);
            } catch (Exception e) {
                iv = defaultValue;
            }
        }
        return iv;
    }

    public void saveLongKey(String key, long value) {
        String v = String.valueOf(value);
        saveKey(key, v);
    }

    public String loadStringKey(String key, String defaultValue) {
        String v = loadKey(key);
        if (v == null) {
            v = defaultValue;
        }
        return v;
    }

    public void saveStringKey(String key, String value) {
        saveKey(key, value);
    }
}
