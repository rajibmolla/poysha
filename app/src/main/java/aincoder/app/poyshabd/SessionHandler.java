package aincoder.app.poyshabd;


import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

/**
 * Created by Abhi on 20 Jan 2018 020.
 */

public class SessionHandler {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_USERNAME = "UserID";
    private static final String KEY_EXPIRES = "expires";
    private static final String KEY_PASSWORD = "Password";
    private static final String KeyrBalance     = "rBalance";
    private static final String KEY_EMPTY = "";
    private Context mContext;
    private SharedPreferences.Editor mEditor;
    private SharedPreferences mPreferences;



    public SessionHandler(Context mContext) {
        this.mContext = mContext;
        mPreferences = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.mEditor = mPreferences.edit();
    }

    /**
     * Logs in the user by saving user details and setting session
     *
     * @param UserID
     * @param Password
     */
    public void loginUser(String UserID, String Password) {
        mEditor.putString(KEY_USERNAME, UserID);
        mEditor.putString(KEY_PASSWORD, Password);
        Date date = new Date();
        //Set user session for next 7 days
        long millis = date.getTime() + (7 * 24 * 60 * 60 * 1000);
        mEditor.putLong(KEY_EXPIRES, millis);
        mEditor.commit();
    }
    private static final String KeySendTo = "ToUserID";
    public void SendTo(String UserID) {
        mEditor.putString(KeySendTo, UserID);
        Date date = new Date();
        mEditor.commit();
    }

    public String GetToUserID() {
        return mPreferences.getString(KeySendTo,null);
    }
    public String GetUserID() {
        return mPreferences.getString(KEY_USERNAME,null);
    }


    /**
     * Checks whether user is logged in
     *
     * @return
     */
    public boolean isLoggedIn() {
        Date currentDate = new Date();

        long millis = mPreferences.getLong(KEY_EXPIRES, 0);

        /* If shared preferences does not have a value
         then user is not logged in
         */
        if (millis == 0) {
            return false;
        }
        Date expiryDate = new Date(millis);

        /* Check if session is expired by comparing
        current date and Session expiry date
        */
        return currentDate.before(expiryDate);
    }

    /**
     * Fetches and returns user details
     *
     * @return user details
     */
    public User getUserDetails() {
        //Check if user is logged in first
        if (!isLoggedIn()) {
            return null;
        }
        User user = new User();
        user.setUserID(mPreferences.getString(KEY_USERNAME, KEY_EMPTY));
        user.setPassword(mPreferences.getString(KEY_PASSWORD, KEY_EMPTY));
        user.setSessionExpiryDate(new Date(mPreferences.getLong(KEY_EXPIRES, 0)));

        return user;
    }

    /**
     * Logs out user by clearing the session
     */
    public void logoutUser(){
        mEditor.clear();
        mEditor.commit();
    }

    private static final String KeyTrxID = "TrxID";
    public void SetNotifyTrxID(String UserID) {
        mEditor.putString(KeyTrxID, UserID);
        Date date = new Date();
        mEditor.commit();
    }
    public String GetNotifyTrxID() {
        return mPreferences.getString(KeyTrxID,null);
    }

    private static final String KeyFCM = "FCM";
    public void SetFCM(String UserID) {
        mEditor.putString(KeyFCM, UserID);
        Date date = new Date();
        mEditor.commit();
    }
    public String GetFCM() {
        return mPreferences.getString(KeyFCM,"Failed");
    }

}
