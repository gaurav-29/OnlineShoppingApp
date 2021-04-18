package com.android_batch_31.designdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

public class ForgotPassword extends AppCompatActivity {
    Context ctx = this;
    @BindView(R.id.txtregemail) TextInputEditText txtregemail;
    Storage storage;
    private String Regemail;


    @Optional
    @OnClick(R.id.btnrecoverpassword) void OnClick(View v)
    {
        boolean isValid = ValidateInput();
        if(isValid==true)
        {
            //Call web service using Volley Library
            SendRequest();
        }
    }

    private void SendRequest() {

        String WebServiceUrl = Common.getWebServiceUrl() + "forgot_password.php";

        StringRequest request = new StringRequest(StringRequest.Method.POST,
                WebServiceUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String res)
            {
                // This is call back method and will execute if server returns response. Response may contain error.
                log.d(res);   // response from server will be printed in logcat window.
                //convert response into json format
                try
                {
                    JSONArray response = new JSONArray(res);
                    // [{"error":"no"},{"success":"no"},{"message":"email not registered with us"}]
                    //here first JSON object(index 0) is {"error":"no"}, second JSON object(index 1) is {"success":"no"} and third JSON object(index 2) is {"message":"Password Changed"}.
                    String error = response.getJSONObject(0).getString("error");
                    if(error.equals("no")==false)
                    {
                        Toast.makeText(ctx,error,Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        String success = response.getJSONObject(1).getString("success");
                        String message = response.getJSONObject(2).getString("message");
                        Toast.makeText(ctx,message,Toast.LENGTH_LONG).show();
                        if(success.equals("yes")==true)
                        {
                            Intent SwitchIntent = new Intent(ctx,Login.class);
                            startActivity(SwitchIntent);
                            finish();
                        }
                    }
                }
                catch (JSONException e) {
                    log.e(e.getMessage(),ctx);   //If android will not be able to convert response into JSON format, then runtime exception error msg
                    // will be printed in logcat window as wel as Toast.(as per our log.e method in log.java class).
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                //this method is call back method which will execute if server do not respond according to our retry policy.
                AlertDialog.Builder b1 = new AlertDialog.Builder(ctx);
                b1.setTitle("Network error");
                b1.setMessage(Common.getMessage());
                b1.setIcon(R.mipmap.ic_launcher);
                b1.setPositiveButton("retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SendRequest();
                    }
                });

                b1.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        b1.create().dismiss();
                    }
                });

                b1.create().show();
            }
        })
        {
            // below method is used to give user input to server.
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> KeyValuePair = new HashMap<String,String>();
                //input: Regemail(required).
                KeyValuePair.put("email",Regemail);

                return KeyValuePair;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1)); //  when we click on Forgot Password
        // button in Login page in mobile, there are two password recovery emails comming. So problem was response from server of first request is
        // slow so as per our retry policy of 3 seconds, there is another request sent to server and emails of first & second request coming together.
        // So to overcome this problem, here in arguments initialTimeoutMs-10000, maxNumRetries-1 taken.

        //addToRequestQueue() actually send request on server
        AppController.getInstance().addToRequestQueue(request);

    }
    private boolean ValidateInput() {

        Regemail = txtregemail.getText().toString().trim();     // Text entered by user may be in numbers also, so we have to convert it in string by toString().

        boolean isValid = true;
        if(Regemail.length()==0) //current password is blank
        {
            isValid = false;
            txtregemail.setError("Registered Email is required");
        }

        return isValid;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        getSupportActionBar().hide(); //it hides actions bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);
        storage = new Storage(ctx);
    }
}
