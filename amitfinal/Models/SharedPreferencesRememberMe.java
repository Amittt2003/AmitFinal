package com.example.amitfinal.Models;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesRememberMe {

    //A SharedPreferences object points to a file containing key-value pairs and provides simple methods to read and write them
    //ניתן למצוא את תיקיית RememberMe.xml תחת Device File Explorer ולאחר מכן, לגשת אל: data/data/com.example.amitfinale/shared_prefs/RememberMe.xml.

    //המשתנה sharedPref הוא final משום שיוצרים את התיקייה של SharedPreferences ם: RememberMe.xml פעם אחת!
    // תיקייה זו אחראית על שמירת ה key - rememberMe וה Value של הKey. לאחר מכן, עובדים רק על תיקייה זו, ולא יוצרים תיקייה חדשה!. כגון, שינוי הValue של ה Key
    private final SharedPreferences sharedPref;

    private final String key = "rememberMe";

    // יצירת האובייקט של הSharedPreferences בשם: RememberMe. כלומר, יצירת תייקיה תחת SharedPreferences בשם: RememberMe.xml
    public SharedPreferencesRememberMe(Context context){
        this.sharedPref = context.getSharedPreferences("RememberMe", Context.MODE_PRIVATE);
    }

    //שינוי הערכים(key,value) שנמצאים בתיקיית RememberMe.xml
    public void edit(boolean value){
        SharedPreferences.Editor editor = this.sharedPref.edit();
        editor.putBoolean(this.key, value);
        editor.apply();
    }

    //בודק האם קיים ה key - rememberMe בתוך התיקייה RememberMe.xml
    public boolean contains(){
        return this.sharedPref.contains(this.key);
    }

    //מחזיר את ערך ה - Value של ה Key - rememberMe
    public boolean getBoolean(){
        return this.sharedPref.getBoolean(this.key, false);
    }

}
