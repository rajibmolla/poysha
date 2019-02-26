package aincoder.app.poyshabd;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import aincoder.app.poyshabd.FcmNotify.FCMHelperFunction;
import aincoder.app.poyshabd.FcmNotify.app.Config;
import aincoder.app.poyshabd.FcmNotify.util.NotificationUtils;

public class Dashboard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private SessionHandler session;
    private ProgressDialog pDialog;
    private static final String KEY_STATUS  = "status";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_EMPTY   = "";
    private TextView FullName;
    private TextView UserID;
    private TextView Balance;
    private ImageView ProfileImage;
    //FCM Variable Start
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private static final String TAG = "AiN FCM";
    //FCM Variable End
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        session = new SessionHandler(getApplicationContext());
        if (!session.isLoggedIn()){
            Intent i = new Intent(Dashboard.this, WelcomeLogin.class);
            startActivity(i);
            finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        // new FCMHelperFunction();
        FullName        = findViewById(R.id.FullName);
        UserID          = findViewById(R.id.UserID);
        Balance         = findViewById(R.id.Balance);
        ProfileImage    = findViewById(R.id.ProfileImage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        LinearLayout SendMoney      = findViewById(R.id.SendMoney);
        LinearLayout ReceiveMoney   = findViewById(R.id.ReceiveMoney);
        LinearLayout PayByQRCode    = findViewById(R.id.PayByQRCode);
        LinearLayout MakePayment    = findViewById(R.id.MakePayment);
        ImageView    Refresh        = findViewById(R.id.Refresh);
        ImageView    Transaction    = findViewById(R.id.Transaction);
        Transaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Dashboard.this, Transaction.class);
                startActivity(i);
                finish();
            }
        });
        SendMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Dashboard.this, SendMoneySearch.class);
                startActivity(i);
                finish();
            }
        });
        ReceiveMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Dashboard.this, ReceiveMoneySearch.class);
                startActivity(i);
                finish();
            }
        });
        Refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadProfile();
            }
        });
        LoadProfile();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        session.SetFCM(refreshedToken);
        // FCM Notify Start
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    String message = intent.getStringExtra("message");
                    Toast.makeText(getApplicationContext(),  message, Toast.LENGTH_LONG).show();
                }
            }
        };
        //displayFirebaseRegId();
        // FCM Notify End
       // new FCMHelperFunction();
        //Toast.makeText(getApplicationContext(), "Push notification: " + session.GetFCM(), Toast.LENGTH_LONG).show();


    }
    private void LoadProfile()
    {
        displayLoader();
        JSONObject request = new JSONObject();
        try {
            //Populate the request parameters
            request.put("UserID",   session.GetUserID());
            request.put("FCM",      session.GetFCM());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String login_url = "http://app.poyshabd.com/DashBoard.php";
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, login_url, request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.dismiss();
                        try {
                            //Check if user got logged in successfully
                            if (response.getInt(KEY_STATUS) == 786) {
                                FullName.setText(response.getString("FullName"));
                                UserID.setText(response.getString("UserID"));
                                Balance.setText(response.getString("Balance"));
                                Picasso.get().load("http://app.poyshabd.com/" + response.getString("ProfileImage")).into(ProfileImage);
                                pDialog.dismiss();
                            }else{
                                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(Dashboard.this);
                                dlgAlert.setMessage(response.getString(KEY_MESSAGE));
                                dlgAlert.setTitle("Oops!");
                                dlgAlert.setPositiveButton("Try Again",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                pDialog.dismiss();
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
                        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(Dashboard.this);
                        dlgAlert.setMessage("Something Is Wrong Fetching Your Profile Data, Please try again!");
                        dlgAlert.setTitle("Oops!");
                        dlgAlert.setPositiveButton("Try Again",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //dismiss the dialog
                                        LoadProfile();
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
    private void displayLoader() {
        pDialog = new ProgressDialog(Dashboard.this);
        pDialog.setMessage("Processing Your Request...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.Notification) {
            Intent i = new Intent(Dashboard.this, Notification.class);
            startActivity(i);
            finish();
            return true;
        }
        if (id == R.id.LogOut) {
            session.logoutUser();
            Intent i = new Intent(Dashboard.this, WelcomeLogin.class);
            startActivity(i);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.Transaction2) {
            Intent i = new Intent(Dashboard.this, Transaction.class);
            startActivity(i);
            finish();
        } else if (id == R.id.SendMoney2) {
            Intent i = new Intent(Dashboard.this, SendMoneySearch.class);
            startActivity(i);
            finish();
        } else if (id == R.id.ReceiveMoney2) {
            Intent i = new Intent(Dashboard.this, ReceiveMoneySearch.class);
            startActivity(i);
            finish();
        } else if (id == R.id.LogOut) {
            session.logoutUser();
            Intent i = new Intent(Dashboard.this, WelcomeLogin.class);
            startActivity(i);
            finish();
            return true;
        } else if (id == R.id.BugReport) {
            Toast.makeText(getApplicationContext(), "Coming Soon", Toast.LENGTH_LONG).show();
        } else if (id == R.id.ContactUs) {
            Toast.makeText(getApplicationContext(), "Coming Soon", Toast.LENGTH_LONG).show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    // FCM Notify Functions Start
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);
        Log.e(TAG, "FCM ID: " + regId);
        Toast.makeText(getApplicationContext(), "FCM ID:- " + regId, Toast.LENGTH_LONG).show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));
        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }
    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
    // FCm Notify Functions End
}
