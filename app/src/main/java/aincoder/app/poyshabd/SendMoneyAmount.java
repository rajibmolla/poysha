package aincoder.app.poyshabd;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import aincoder.app.poyshabd.Dashboard;

public class SendMoneyAmount extends AppCompatActivity {

    private static final String KEY_STATUS      = "status";
    private static final String KEY_MESSAGE     = "message";
    private static final String KEY_USERNAME    = "userid";
    private static final String KEY_EMPTY       = "";
    private static final String KeyAmount       = "amount";
    private static final String KeyNote         = "note";
    private static final String KeyToUser       = "touserid";
    private static final String KeySenderID     = "userid";
    private static final String KeyrBalance     = "rBalance";
    private static final String KeyTrxPin       = "Pin";
    private EditText                            etAmount;
    public  String                              Amount;
    private EditText                            etNote;
    public  String                              Note;
    private ProgressDialog                      pDialog;
    private SessionHandler                      session;
    public  String                              ToUserID;
    public  String                              SenderID;
    private String                              TransactionPin = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        session = new SessionHandler(getApplicationContext());
        displayLoader();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sendmoneyamount);
        LoadUserData();
        etAmount = findViewById(R.id.Amount);
        etNote   = findViewById(R.id.Note);
        SenderID = session.GetUserID();
        ToUserID = session.GetToUserID();
        session.SendTo(null);
        Button BtnSend = findViewById(R.id.BtnSend);
        BtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SendMoneyAmount.this);
                builder.setTitle("What's Your Pin Number?");
                // Set up the input
                final EditText input = new EditText(SendMoneyAmount.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);
                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TransactionPin  = input.getText().toString();
                        Amount          = etAmount.getText().toString().toLowerCase().trim();
                        Note            = etNote.getText().toString().trim();
                        if (validateInputs()) {
                            SendMoney();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
        ImageView GoBack = findViewById(R.id.GoBack);
        GoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SendMoneySearch.class);
                startActivity(i);
                finish();
            }
        });
    }
    private void LoadUserData()
    {
        JSONObject request = new JSONObject();
        try {
            //Populate the request parameters
            request.put("UserID", session.GetToUserID());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String userDetails = "http://app.poyshabd.com/DashBoard.php";
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, userDetails, request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.dismiss();
                        try {
                            //Check if user got logged in successfully
                            if (response.getInt(KEY_STATUS) == 786) {
                                TextView fName = findViewById(R.id.FullName);
                                fName.setText(capitalize(response.getString("FullName")));
                                TextView SUserID = findViewById(R.id.SUserID);
                                SUserID.setText(response.getString("UserID"));
                                ImageView imageView= findViewById(R.id.profile_image);
                                Picasso.get().load("http://app.poyshabd.com/" + response.getString("ProfileImage")).into(imageView);
                            }else{
                                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(SendMoneyAmount.this);
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
                        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(SendMoneyAmount.this);
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
    public void SendMoney()
    {
        displayLoader();
        JSONObject request = new JSONObject();
        try {
            request.put(KeyAmount,  Amount);
            request.put(KeyNote,    Note);
            request.put(KeyToUser,  ToUserID);
            request.put(KeySenderID,SenderID);
            request.put(KeyTrxPin,  TransactionPin);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String login_url = "http://app.poyshabd.com/SendMoneyAmount.php";
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, login_url, request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.dismiss();
                        try {
                            //Check if user got logged in successfully
                            if (response.getInt(KEY_STATUS) == 786) {
                                // Load the send amount page
                                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(SendMoneyAmount.this);
                                dlgAlert.setMessage(response.getString(KEY_MESSAGE));
                                dlgAlert.setTitle("Okay");
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
                            }else{
                                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(SendMoneyAmount.this);
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
                        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(SendMoneyAmount.this);
                        dlgAlert.setMessage(error.toString());
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
    private void displayLoader() {
        pDialog = new ProgressDialog(SendMoneyAmount.this);
        pDialog.setMessage("Processing Your Request...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }
    private boolean validateInputs() {
        if(KEY_EMPTY.equals(etAmount)){
            etAmount.setError("Sending Amount Cannot be EMPTY");
            etAmount.requestFocus();
            return false;
        }
        return true;
    }
    public static String capitalize(String input) {
        if (input == null || input.length() <= 0) {
            return input;
        }
        char[] chars = new char[1];
        input.getChars(0, 1, chars, 0);
        if (Character.isUpperCase(chars[0])) {
            return input;
        } else {
            StringBuilder buffer = new StringBuilder(input.length());
            buffer.append(Character.toUpperCase(chars[0]));
            buffer.append(input.toCharArray(), 1, input.length()-1);
            return buffer.toString();
        }
    }
    // For exit the app
    private static long back_pressed;
    @Override
    public void onBackPressed()
    {
        Intent i = new Intent(getApplicationContext(), SendMoneySearch.class);
        startActivity(i);
        finish();
    }

}
