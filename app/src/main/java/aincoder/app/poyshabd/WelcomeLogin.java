package aincoder.app.poyshabd;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import aincoder.app.poyshabd.FcmNotify.FCMHelperFunction;
import aincoder.app.poyshabd.FcmNotify.app.Config;
import aincoder.app.poyshabd.FcmNotify.util.NotificationUtils;


public class WelcomeLogin extends AppCompatActivity {

    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_USERNAME = "userid";
    private static final String KEY_PASSWORD = "password";
    private static final String KeyrBalance    = "rBalance";
    private static final String KEY_EMPTY = "";
    private EditText etUserID;
    private EditText etPassword;
    private String UserID;
    private String Password;
    private ProgressDialog pDialog;
    private static final String login_url = "http://app.poyshabd.com/login.php";
    private SessionHandler session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        session = new SessionHandler(getApplicationContext());
        if(session.isLoggedIn()){
            loadDashboard();
        }
            super.onCreate(savedInstanceState);
            setContentView(R.layout.welcomelogin);
            Button register = findViewById(R.id.btnResister);
            Button login = findViewById(R.id.btnLogin);
            etUserID = findViewById(R.id.UserID);
            etPassword = findViewById(R.id.Password);
            //Launch Registration screen when Register Button is clicked
            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(WelcomeLogin.this, ResisterProcess.class);
                    startActivity(i);
                    finish();
                }
            });
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Retrieve the data entered in the edit texts
                    UserID = etUserID.getText().toString().toLowerCase().trim();
                    Password = etPassword.getText().toString().trim();
                    if (validateInputs()) {
                        login();
                    }
                }
            });
            //Call the FCM Class
           // new FCMHelperFunction();
    }
    /**
     * Launch Dashboard Activity on Successful Login
     */
    private void loadDashboard() {
        Intent i = new Intent(getApplicationContext(), Dashboard.class);
        startActivity(i);
        finish();
    }
    /**
     * Display Progress bar while Logging in
     */
    private void displayLoader() {
        pDialog = new ProgressDialog(WelcomeLogin.this);
        pDialog.setMessage("Processing Your Request...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }
    private boolean validateInputs() {
        if(KEY_EMPTY.equals(UserID)){
            etUserID.setError("UserID or Phone Number Cannot be EMPTY");
            etUserID.requestFocus();
            return false;
        }
        if(KEY_EMPTY.equals(Password)){
            etPassword.setError("Password cannot be EMPTY");
            etPassword.requestFocus();
            return false;
        }
        return true;
    }
    // For exit the app
    private static long back_pressed;
    @Override
    public void onBackPressed()
    {
        if (back_pressed + 2000 > System.currentTimeMillis()) super.onBackPressed();
        else Toast.makeText(getBaseContext(), "Press Once Again To Exit!", Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }
    private void login() {
        displayLoader();
        JSONObject request = new JSONObject();
        try {
            //Populate the request parameters
            request.put(KEY_USERNAME, UserID);
            request.put(KEY_PASSWORD, Password);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, login_url, request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.dismiss();
                        try {
                            //Check if user got logged in successfully
                            if (response.getInt(KEY_STATUS) == 786) {
                                session.loginUser(UserID,response.getString(KEY_PASSWORD));
                                loadDashboard();
                            }else{
                                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(WelcomeLogin.this);
                                dlgAlert.setMessage(response.getString(KEY_MESSAGE));
                                dlgAlert.setTitle("Oops!");
                                dlgAlert.setPositiveButton("Try Again",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                //dismiss the dialog
                                                dialog.dismiss();
                                            }
                                        });
                                dlgAlert.setCancelable(false);
                                dlgAlert.create().show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
                        //Display error message whenever an error occurs
                        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(WelcomeLogin.this);
                        dlgAlert.setMessage("Something Was Wrong, Please Try Again.");
                        dlgAlert.setTitle("Oops!");
                        dlgAlert.setPositiveButton("Try Again",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //dismiss the dialog
                                        dialog.dismiss();
                                    }
                                });
                        dlgAlert.setCancelable(false);
                        dlgAlert.create().show();
                    }
                });
        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);
    }
}
