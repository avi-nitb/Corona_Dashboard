package com.coronadashboard.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefOperation {


    public static String SHARED_PREF_NAME = "RS_PREF";

    public static String getSharedPrefString(String preffConstant, Context context) {
        String stringValue = "";
        SharedPreferences sp = context.getSharedPreferences(
                SHARED_PREF_NAME, 0);
        stringValue = sp.getString(preffConstant, "");
        return stringValue;
    }

    public static void setSharedPrefString(String preffConstant,
                                           String stringValue, Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SHARED_PREF_NAME, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(preffConstant, stringValue);
        editor.commit();
    }

    public static int getSharedPrefInteger(String preffConstant, Context context) {
        int intValue = 0;
        SharedPreferences sp = context.getSharedPreferences(
                SHARED_PREF_NAME, 0);
        intValue = sp.getInt(preffConstant, 0);
        return intValue;
    }

    public static void setSharedPrefInteger(String preffConstant, int value, Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SHARED_PREF_NAME, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(preffConstant, value);
        editor.commit();
    }

    public static float getSharedPrefFloat(String preffConstant, Context context) {
        float floatValue = 0;
        SharedPreferences sp = context.getSharedPreferences(
                preffConstant, 0);
        floatValue = sp.getFloat(preffConstant, 0);
        return floatValue;
    }

    public static void setSharedPrefFloat(String preffConstant, float floatValue, Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                preffConstant, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putFloat(preffConstant, floatValue);
        editor.commit();
    }

}
