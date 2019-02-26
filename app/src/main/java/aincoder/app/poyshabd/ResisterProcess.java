package aincoder.app.poyshabd;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;


public class ResisterProcess extends AppCompatActivity {

    private ProgressDialog pDialog;
    private static final String KEY_EMPTY = "";
    private String Gender = "";
    private EditText etfName;
    private EditText etlName;
    private EditText etPhone;
    private EditText etNID;
    private EditText etPassword;
    private Boolean etMale;
    private EditText etFemale;
    private String FirstName,LastName,NID,Phone,Password;
    private String register_url = "http://app.poyshabd.com/register.php";
    private SessionHandler session;

    //Sending variable name (Will be used on server side)
    private static final String KeyFirstName    = "fname";
    private static final String KeyLastName     = "lname";
    private static final String KeyNID          = "nid";
    private static final String KeyPassword     = "password";
    private static final String KeyPhone        = "mobile";
    private static final String KeyGender       = "gender";

    // Server response variable name
    private static final String KEY_STATUS      = "status"; // Status Code
    private static final String KEY_MESSAGE     = "message"; // Response Message
    private static final String KeyUserID       = "userid"; // Unique User Id (Not Insert ID)
    private static final String KEY_EXPIRES     = "expires";
    private static final String IsLogged        = "IsLogged";
    private static final String IsTrue          = "Yes";


    // Only Used for identify the request action on server
    private static final String KeyAction       = "action";
    private static final String KeyDo           = "resister";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reg_screen);
        session = new SessionHandler(getApplicationContext());

        etfName     = findViewById(R.id.fName);
        etlName     = findViewById(R.id.lName);
        etPhone     = findViewById(R.id.Phone);
        etNID       = findViewById(R.id.NID);
        etPassword  = findViewById(R.id.Password);
        Button register = findViewById(R.id.btnResister);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etfName     = findViewById(R.id.fName);
                etlName     = findViewById(R.id.lName);
                etPhone     = findViewById(R.id.Phone);
                etNID       = findViewById(R.id.NID);
                etPassword  = findViewById(R.id.Password);

                FirstName   = etfName.getText().toString().toLowerCase().trim();
                LastName    = etlName.getText().toString().toLowerCase().trim();
                Phone       = etPhone.getText().toString().toLowerCase().trim();
                NID         = etNID.getText().toString().toLowerCase().trim();
                Password    = etPassword.getText().toString().trim();
                displayLoader();
                if (validateInputs()){
                    AddNewUser();
                }
            }
        });

        //Back to the login screen
        Button login = findViewById(R.id.btnLogin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ResisterProcess.this, WelcomeLogin.class);
                startActivity(i);
                finish();
            }
        });
    }
    private boolean validateInputs() {
        if(KEY_EMPTY.equals(FirstName)){
            etfName.setError("First Name Cannot Be Empty");
            etfName.requestFocus();
            pDialog.dismiss();
            return false;
        }
        if(KEY_EMPTY.equals(LastName)){
            etlName.setError("First Name Cannot Be Empty");
            etlName.requestFocus();
            pDialog.dismiss();
            return false;
        }
        if(KEY_EMPTY.equals(Phone)){
            etPhone.setError("Phone Number Cannot Be Empty");
            etPhone.requestFocus();
            pDialog.dismiss();
            return false;
        }
        if(KEY_EMPTY.equals(NID)){
            etNID.setError("NID Cannot Be Empty");
            etNID.requestFocus();
            pDialog.dismiss();
            return false;
        }
        if(KEY_EMPTY.equals(Password)){
            etNID.setError("Password Cannot Be Empty");
            etNID.requestFocus();
            pDialog.dismiss();
            return false;
        }
        //initiate a check box
        //check current state of a check box (true or false)
        CheckBox etMale = (CheckBox) findViewById(R.id.Male);
        Boolean ismale = etMale.isChecked();
        CheckBox etfemale = (CheckBox) findViewById(R.id.Female);
        Boolean isfemale  = etfemale.isChecked();
        if(!isfemale && !ismale){
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
            dlgAlert.setMessage("Please add your Gender.");
            dlgAlert.setTitle("oops,There was an Error");
            dlgAlert.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //dismiss the dialog
                            dialog.dismiss();
                            pDialog.dismiss();
                        }
                    });
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();
            return false;
        }
        if(isfemale && ismale){
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
            dlgAlert.setMessage("Please add your Valid Gender.");
            dlgAlert.setTitle("Oops,There was an Error");
            dlgAlert.setPositiveButton("Okay",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //dismiss the dialog
                            dialog.dismiss();
                            pDialog.dismiss();
                        }
                    });
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();
            return false;
        }
        if (ismale){
            Gender =  "Male";
        }else if(isfemale){
            Gender = "Female";
        }else{
            Gender = "";
        }
        return true;
    }
    private void displayLoader() {
        pDialog = new ProgressDialog(ResisterProcess.this);
        pDialog.setMessage("Processing Your Request...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ResisterProcess.this, WelcomeLogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    private void AddNewUser()
    {
        JSONObject request = new JSONObject();
        try {
            //Populate the request parameters
            request.put(KeyFirstName, FirstName);
            request.put(KeyLastName, LastName);
            request.put(KeyNID, NID);
            request.put(KeyPhone, Phone);
            request.put(KeyPassword, Password);
            request.put(KeyGender, Gender);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, register_url, request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.dismiss();
                        try {
                            //Check if user got registered successfully
                            if (response.getInt(KEY_STATUS) == 1) {
                                //Set the user session And Show Congratulation message
                                session.loginUser(response.getString(KeyUserID) , response.getString(KeyPassword));
                                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(ResisterProcess.this);
                                dlgAlert.setMessage(response.getString(KEY_MESSAGE));
                                dlgAlert.setTitle("Congratulation!");
                                dlgAlert.setPositiveButton("Go To Dashboard",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                //dismiss the dialog
                                                dialog.dismiss();
                                                loadDashboard();
                                            }
                                        });
                                dlgAlert.setCancelable(true);
                                dlgAlert.create().show();
                            }else{
                                //Display error message whenever an error occurs
                                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(ResisterProcess.this);
                                dlgAlert.setMessage(response.getString(KEY_MESSAGE));
                                dlgAlert.setTitle("Oops,There was an Error!");
                                dlgAlert.setPositiveButton("Try Again",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                //dismiss the dialog
                                                dialog.dismiss();
                                            }
                                        });
                                dlgAlert.setCancelable(true);
                                dlgAlert.create().show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Display error message whenever an error occurs
                        pDialog.dismiss();
                        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(ResisterProcess.this);
                        dlgAlert.setMessage(error.toString() );
                        dlgAlert.setTitle("Oops,There was an Error!");
                        dlgAlert.setPositiveButton("Try Again",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //dismiss the dialog
                                        dialog.dismiss();
                                    }
                                });
                        dlgAlert.setCancelable(true);
                        dlgAlert.create().show();
                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);
    }
    /**
     * Launch Dashboard Activity on Successful Sign Up
     */
    private void loadDashboard() {
        Intent i = new Intent(getApplicationContext(), Dashboard.class);
        startActivity(i);
        finish();
    }
}
