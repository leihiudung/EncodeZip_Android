package com.bvt.encodezip.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.List;

public class PreferenceUtil {

    final static String TOKEN_PREFERENCE = "TOKEN_PREFERENCE";

    private static SharedPreferences.Editor editor;
    private static SharedPreferences sharedPreferences;
    private static Context mContext;


    public static String getTokenPreference() {
        return TOKEN_PREFERENCE;
    }

    /**
     * Create SharedPreference and SharedPreferecne Editor for Context
     *
     * @param context
     */
    private static void createSharedPreferenceEditor(Context context) {
        try {
            if (context != null) {
                mContext = context;
            } else {

                return;
            }
            sharedPreferences = context.getSharedPreferences(TOKEN_PREFERENCE, Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /**
     * Put String in SharedPreference Editor
     *
     * @param context
     * @param key
     * @param value
     */
    public static void putPrefString(Context context, String key, String value) {
        try {
            createSharedPreferenceEditor(context);
            editor.putString(key, value);
            editor.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static String getString(Context context, String key) {

        if (context == null) {
            return "";
        }
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(TOKEN_PREFERENCE, Context.MODE_PRIVATE);
        }

        String tokenStr = sharedPreferences.getString(key, null);


        return tokenStr;
    }

    public static boolean putPreFileDownloaded(Context context, String fileName, String path) {
        try {
            createSharedPreferenceEditor(context);
            String filesCombStr = sharedPreferences.getString(FileUtils.DWONLOADED_FILE_LIST, "");
            StringBuffer filesCombStrBuff = new StringBuffer(filesCombStr);

            filesCombStrBuff.append(fileName + ",");
            editor.putString(FileUtils.DWONLOADED_FILE_LIST, filesCombStrBuff.toString());
            editor.putString(fileName, path);
            editor.commit();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static String getPreFileDownloaded(Context context, String preKey) {
        if (context == null) {
            return "";
        }
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(preKey, Context.MODE_PRIVATE);
        }
        String filesName = sharedPreferences.getString(preKey, null);
        return filesName;
    }
}

