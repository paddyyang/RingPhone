package com.pyzed.ringphone;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by ygd on 2016/12/10.
 */

public class SettingsUtil {

    private final static String pre_name = "ring_phone_service";

    private static void storeSettings(Context context, String keyname, String value){


        SharedPreferences sp =context.getSharedPreferences(pre_name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString(keyname, value);
        editor.commit();
    }

    private static void storeSettings(Context context, String keyname, Boolean value){


        SharedPreferences sp =context.getSharedPreferences(pre_name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putBoolean(keyname, value);
        editor.commit();
    }

    private static void storeSettings(Context context, String keyname, Integer value){


        SharedPreferences sp =context.getSharedPreferences(pre_name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putInt(keyname, value);
        editor.commit();
    }


    private static String getSettingsString(Context context, String keyname){


        SharedPreferences sp =context.getSharedPreferences(pre_name, Context.MODE_PRIVATE);
        return sp.getString(keyname, null);
    }

    private static Boolean getSettingsBoolean(Context context, String keyname){


        SharedPreferences sp =context.getSharedPreferences(pre_name, Context.MODE_PRIVATE);
        return sp.getBoolean(keyname, false);
    }

    private static Integer getSettingsInt(Context context, String keyname){


        SharedPreferences sp =context.getSharedPreferences(pre_name, Context.MODE_PRIVATE);
        return sp.getInt(keyname, -1);
    }


    public static boolean getAutoStartSetting(Context context) {

        SharedPreferences sp =context.getSharedPreferences(pre_name, Context.MODE_PRIVATE);
        boolean isChecked  = sp.getBoolean("auto_start", false);
        return isChecked;
    }

    public static void storeAutoStartSettings(Context context, Boolean value) {
        storeSettings(context, "auto_start", value);
    }

    public static int getVolumeSetting(Context context) {

        return getSettingsInt(context, "volume");
    }
    public static void storeVolumeSettings(Context context, Integer value) {

        storeSettings(context, "volume", value);
    }


    public static int getMaxNum(Context context) {

        SharedPreferences sp =context.getSharedPreferences(pre_name, Context.MODE_PRIVATE);
        Integer num  = sp.getInt("max_num", 0);
        return num;
    }
    public static void storeMaxNum(Context context, Integer value) {

        storeSettings(context, "max_num", value);
    }

    public static ArrayList<String> getListNumber(Context context) {

        ArrayList<String> listPhoneNumber =new ArrayList<String>();

        int num = getMaxNum(context);
        if(num > 0) {
            for(int i = 0; i < num; i++){
                String keyname = "phone" + i;
                String phone_number = getSettingsString(context,keyname);
                listPhoneNumber.add(i, phone_number);

                Log.v("RingPhone", "get phone number: " + keyname + ": " + phone_number);
            }
        }
        Log.v("RingPhone", "get max =  " + getMaxNum(context));

        return  listPhoneNumber;
    }

    public static void storeListNumber(Context context, ArrayList<String> listNumber){

        if(listNumber == null) return;
        int num = listNumber.size();
        if(num < 1) {
            storeMaxNum(context, 0);
            return;
        } else {
            storeMaxNum(context, num);

            for(int i = 0; i < num; i++){
                String keyname = "phone" + i;
                String phone_number = listNumber.get(i);
                storeSettings(context, keyname, phone_number);

                Log.v("RingPhone", "store phone number: " + keyname + ": " + phone_number);
            }

        }
        Log.v("RingPhone", "store max =  " + getMaxNum(context));

    }

    public static boolean findPhoneNumber(Context context, String phone){

        ArrayList<String> listNumber = getListNumber(context);
        Boolean b =  (listNumber.indexOf(phone) != -1);
        Log.v("RingPhone", "findPhoneNumber: " + b);

        return b;
    }

    public static boolean getEveryOneSetting(Context context) {

        SharedPreferences sp =context.getSharedPreferences(pre_name, Context.MODE_PRIVATE);
        boolean isChecked  = sp.getBoolean("for_every_one", true);
        return isChecked;
    }

    public static void storeEveryOneSetting(Context context, Boolean value) {
        storeSettings(context, "for_every_one", value);
    }

}
