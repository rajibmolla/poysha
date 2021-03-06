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
        import android.widget.LinearLayout;
        import android.widget.TextView;

        import com.android.volley.Request;
        import com.android.volley.Response;
        import com.android.volley.VolleyError;
        import com.android.volley.toolbox.JsonObjectRequest;

        import org.json.JSONException;
        import org.json.JSONObject;

public class Transaction extends AppCompatActivity {


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

        setContentView(R.layout.transaction);

        session = new SessionHandler(getApplicationContext());
        displayLoader();
        LoadTrx(1);
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
        pDialog = new ProgressDialog(Transaction.this);
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

    private TextView TrxID;
    private TextView Type;
    private TextView Amount;
    private TextView Status;
    private LinearLayout TXX;

    private int cPage;


    private void LoadTrx(int Page)
    {
        cPage = Page;
        JSONObject request = new JSONObject();
        try {
            //Populate the request parameters
            request.put(KeyPages,    cPage);
            request.put(KeyUserID,   session.GetUserID());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String login_url = "http://app.poyshabd.com/Transaction.php";
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, login_url, request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        pDialog.dismiss();
                        try {
                            //Check if user got logged in successfully
                            if (response.getInt(KEY_STATUS) == 786) {
                              //  Test.setText(response.toString());
                                int T = response.getInt("total")+1;
                                int C = 1;
                                for(int I = 1; I < T; I++ ){
                                    String Cto = Integer.valueOf(C).toString();
                                    String TID = "SL" + C + "Trx";
                                    String TY = "SL" + C + "Type";
                                    String AM = "SL" + C + "Amount";
                                    String ST = "SL" + C + "Status";
                                    String LI = "TRX" + C ;

                                    TXX = findViewById(getResources().getIdentifier(LI, "id", getPackageName()));
                                    TXX.setVisibility(View.VISIBLE);

                                    TrxID = (TextView)findViewById(getResources().getIdentifier(TID, "id", getPackageName()));
                                    TrxID.setText(response.getJSONObject("TrxData").getJSONObject(Cto).getString("trx_id"));

                                    Type = (TextView)findViewById(getResources().getIdentifier(TY, "id", getPackageName()));
                                    Type.setText(response.getJSONObject("TrxData").getJSONObject(Cto).getString("type"));
                                    Amount = (TextView)findViewById(getResources().getIdentifier(AM, "id", getPackageName()));
                                    Amount.setText(response.getJSONObject("TrxData").getJSONObject(Cto).getString("amount"));
                                    Status = (TextView)findViewById(getResources().getIdentifier(ST, "id", getPackageName()));
                                    Status.setText(response.getJSONObject("TrxData").getJSONObject(Cto).getString("status"));
                                    C++;
                                }
                                /*Button Prev = findViewById(R.id.Prev);
                                Button Next = findViewById(R.id.Next);
                                Prev.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        displayLoader();
                                        LoadTrx(cPage - 1);
                                    }
                                });
                                Next.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        displayLoader();
                                        LoadTrx(cPage + 1);
                                    }
                                });*/

                            }else{
                                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(Transaction.this);
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
                        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(Transaction.this);
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
    // For Go back
    private static long back_pressed;
    @Override
    public void onBackPressed()
    {
        Intent i = new Intent(getApplicationContext(), Dashboard.class);
        startActivity(i);
        finish();
    }
}
