package com.example.firebase;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.Date;

public class SessionHandler {

    private static final String PREF_NAME = "UserSession";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EXPIRES = "expires";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_EMPTY = "";
    private Context mContext;
    private SharedPreferences.Editor mEditor;
    private SharedPreferences mPreferences;

    public SessionHandler(Context mContext){
        this.mContext = mContext;
        mPreferences = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.mEditor = mPreferences.edit();
    }

    public void loginUser(String username, String fullname){
        mEditor.putString(KEY_USERNAME, username);
        mEditor.putString(KEY_FULL_NAME, fullname);
        Date date = new Date();

        long millis = date.getTime() + (7*24*60*60*1000);
        mEditor.putLong(KEY_EXPIRES, millis);
        mEditor.commit();
    }

    public boolean isLoggedIn(){
        Date currentDate = new Date();

        long millis = mPreferences.getLong(KEY_EXPIRES, 0);

        if(millis == 0){
            return false;
        }
        Date expiryDate = new Date(millis);

        return currentDate.before(expiryDate);
    }

    public User getUserDetails(){
        if(!isLoggedIn()){
            return  null;
        }
        User user = new User();
        user.setUsername(mPreferences.getString(KEY_USERNAME, KEY_EMPTY));
        user.setFullName(mPreferences.getString(KEY_FULL_NAME,KEY_EMPTY));
        user.setSessionExpiryDate(new Date(mPreferences.getLong(KEY_EXPIRES, 0)));

        return user;
    }

    public void logoutUser(){
        mEditor.clear();
        mEditor.commit();
    }
}
