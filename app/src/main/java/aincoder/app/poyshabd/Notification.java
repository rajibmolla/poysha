package aincoder.app.poyshabd;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import aincoder.app.poyshabd.RequestMoneyApprove;



public class Notification extends AppCompatActivity {

    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_USERNAME = "UserID";
    private static final String KEY_EMPTY = "";
    private EditText etUserID;

    private ProgressDialog pDialog;
    private SessionHandler session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.notification);

        session = new SessionHandler(getApplicationContext());
        displayLoader();
        LoadTrx();
        ImageView GoBack = findViewById(R.id.GoBack);
        GoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Dashboard.class);
                startActivity(i);
                finish();
            }
        });
    }
    private void displayLoader() {
        pDialog = new ProgressDialog(Notification.this);
        pDialog.setMessage("Processing Your Request...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }
    private static final String KeyStart    = "Start";
    private static final String KeyEnd      = "End";
    private static final String KeyUserID   = "UserID";
    private static final String KeyPages   = "Pages";
    private TextView Test;
    private int dStart;

    private TextView NotyTx;
    private LinearLayout NotyLIN;



    private void LoadTrx()
    {
        JSONObject request = new JSONObject();
        try {
            //Populate the request parameters
            request.put(KeyUserID,   session.GetUserID());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String login_url = "http://app.poyshabd.com/Notification.php";
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, login_url, request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        pDialog.dismiss();
                        try {
                            //Check if user got logged in successfully
                            if (response.getInt(KEY_STATUS) == 786) {
                                //  Test.setText(response.toString());
                                int C = 1;

                                for(int I = 1; I < 11; I++ ){
                                    String Cto = Integer.valueOf(C).toString();

                                    String LI = "Noty" + C ;
                                    String Notytxx = "NotyTX" + C ;
                                    NotyTx = findViewById(getResources().getIdentifier(Notytxx, "id", getPackageName()));
                                    NotyTx.setText(response.getJSONObject("Notify").getJSONObject(Cto).getString("Text"));

                                    NotyLIN = findViewById(getResources().getIdentifier(LI, "id", getPackageName()));
                                    NotyLIN.setVisibility(View.VISIBLE);
                                    try {
                                        final String TRXID = response.getJSONObject("Notify").getJSONObject(Cto).getString("TrxID");
                                        if (response.getJSONObject("Notify").getJSONObject(Cto).getString("Action").equals("RequestMoney")){
                                            NotyTx.setTextColor(getResources().getColor(R.color.red));
                                            //Setting up on click event function on image button.
                                            NotyLIN.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    session.SetNotifyTrxID(TRXID);
                                                    LoadRequest();
                                                }
                                            });
                                        }else if (response.getJSONObject("Notify").getJSONObject(Cto).getString("Action").equals("None")){
                                            NotyTx.setTextColor(getResources().getColor(R.color.white));
                                            //Setting up on click event function on image button.
                                            NotyLIN.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    session.SetNotifyTrxID(TRXID);
                                                    LoadRequest();
                                                }
                                            });
                                        }else{
                                            NotyTx.setTextColor(getResources().getColor(R.color.white));
                                        }

                                    } catch (JSONException e) {
                                       // e.printStackTrace();
                                    }


                                    C++;
                                }
                            }else{
                                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(Notification.this);
                                dlgAlert.setMessage(response.getString(KEY_MESSAGE));
                                dlgAlert.setTitle("Oops!");
                                dlgAlert.setPositiveButton("Go To Dashboard",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                Intent i = new Intent(getApplicationContext(), Dashboard.class);
                                                startActivity(i);
                                                finish();
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
                        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(Notification.this);
                        dlgAlert.setMessage("Something Was Wrong, Please Try Again.");
                        dlgAlert.setTitle("Oops!");
                        dlgAlert.setPositiveButton("Try Again",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //dismiss the dialog
                                        dialog.dismiss();
                                        LoadTrx();
                                    }
                                });
                        dlgAlert.setCancelable(false);
                        dlgAlert.create().show();
                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);
    }
    // For Go back
    private static long back_pressed;
    @Override
    public void onBackPressed()
    {
        Intent i = new Intent(getApplicationContext(), Dashboard.class);
        startActivity(i);
        finish();
    }

    private void LoadRequest(){
        Intent i = new Intent(getApplicationContext(), RequestMoneyApprove.class);
        startActivity(i);
        finish();
    }
}
