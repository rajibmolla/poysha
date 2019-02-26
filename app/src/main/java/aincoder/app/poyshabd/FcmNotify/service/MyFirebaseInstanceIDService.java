package aincoder.app.poyshabd.FcmNotify.service;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;

import aincoder.app.poyshabd.FcmNotify.app.Config;
import aincoder.app.poyshabd.MySingleton;
import aincoder.app.poyshabd.SessionHandler;
import aincoder.app.poyshabd.WelcomeLogin;

import static android.accounts.AccountManager.KEY_PASSWORD;


/**
 * Created by Ravi Tamada on 08/08/16.
 * www.androidhive.info
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "FCMIS";
    private static final String KEY_STATUS = "status";
    private static final String KeyUserID = "userid";
    private static final String KeyToken = "fcm_token";
    private static final String KEY_EMPTY = "";
    private static final String login_url = "http://app.poyshabd.com/AddFCM.php";
    private SessionHandler session;

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        Toast.makeText(getApplicationContext(), "FCM Token:- " + refreshedToken, Toast.LENGTH_LONG).show();


        // Saving reg id to shared preferences
        storeRegIdInPref(refreshedToken);
        session.SetFCM(refreshedToken);
        // sending reg id to your server
        sendRegistrationToServer(refreshedToken);

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        Log.e(TAG, "sendRegistrationToServer: " + token);
        session = new SessionHandler(getApplicationContext());
        JSONObject request = new JSONObject();
        try {
            //Populate the request parameters
            request.put(KeyUserID, session.GetUserID());
            request.put(KeyToken, token);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, login_url, request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Check if user got logged in successfully
                            if (response.getInt(KEY_STATUS) == 786) {

                            }else{

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);
    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.commit();
        session.SetFCM(token);
    }
}

