package com.abhi.rapido.googlemapdemoapp.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class Util {

    public static boolean isNetworkConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        // should check null because in air plan mode it will be null
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;

    }
}
