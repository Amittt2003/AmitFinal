package com.example.amitfinal.Models;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class InternetConnection {
    private final Context context;

    public InternetConnection(Context context){
        this.context = context;
    }

    //בודק האם למשתמש יש חיבור לאינטרנט ומחזיר true במידה וכן
    public boolean isConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);//wifi
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);//נתוני גלישה של הטלפון

        return (wifiConn != null && wifiConn.isConnectedOrConnecting()) || (mobileConn != null && mobileConn.isConnectedOrConnecting());
    }
}
