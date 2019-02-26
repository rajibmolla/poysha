package aincoder.app.poyshabd;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class SendMoneySearch extends AppCompatActivity {


    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_USERNAME = "userid";
    private static final String KEY_EMPTY = "";
    private EditText etUserID;
    private String UserID;
    private ProgressDialog pDialog;
    private String login_url = "http://app.poyshabd.com/SendMoneySearch.php";
    private SessionHandler session;
    public static JSONObject DATA;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sendmoney);

        session = new SessionHandler(getApplicationContext());
        etUserID = findViewById(R.id.UserID);
        Button BtnSearch = findViewById(R.id.BtnSearch);
        BtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserID = etUserID.getText().toString().toLowerCase().trim();
                if (validateInputs()) {
                    SearchUser();
                }
            }
        });
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
        pDialog = new ProgressDialog(SendMoneySearch.this);
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
        return true;
    }
    private void SearchUser()
    {
        displayLoader();
        JSONObject request = new JSONObject();
        try {
            //Populate the request parameters
            request.put(KEY_USERNAME, UserID);
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
                                // Load the send amount page
                                DATA = response;
                                session.SendTo(response.getJSONObject("UserInfo").getString("mobile"));
                                Intent i = new Intent(getApplicationContext(), SendMoneyAmount.class);
                                startActivity(i);
                                finish();
                            }else{
                                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(SendMoneySearch.this);
                                dlgAlert.setMessage(response.getString(KEY_MESSAGE));
                                dlgAlert.setTitle("Oops!");
                                dlgAlert.setPositiveButton("Try Again",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
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
                        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(SendMoneySearch.this);
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
    private void LoadUserDetails() {
        Intent i = new Intent(getApplicationContext(), Dashboard.class);
        startActivity(i);
        finish();
    }
    public static JSONObject GetData()
    {
        return DATA;
    }

    // For exit the app
    private static long back_pressed;
    @Override
    public void onBackPressed()
    {
        Intent i = new Intent(getApplicationContext(), Dashboard.class);
        startActivity(i);
        finish();
    }
}
