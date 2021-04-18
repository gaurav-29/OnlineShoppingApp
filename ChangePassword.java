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

public class ChangePassword extends AppCompatActivity {

    Context ctx = this;
    @BindView(R.id.txtcurrentpassword) TextInputEditText txtcurrentpassword;
    @BindView(R.id.txtnewpassword) TextInputEditText txtnewpassword;
    @BindView(R.id.txtconfirmpassword) TextInputEditText txtconfirmpassword;
    Storage storage;
    private String currentpassword,newpassword,confirmpassword;


    @Optional
    @OnClick(R.id.btnchangepassword) void OnClick(View v){

        boolean isValid = ValidateInput();
        if(isValid==true)
        {
            //Call web service using Volley Library
            SendRequest();
        }
    }

    private void SendRequest() {

        String WebServiceUrl = Common.getWebServiceUrl() + "change_password.php?id=" + storage.read("id",Storage.INTEGER).toString();

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
                    // [{"error":"no"},{"success":"yes"},{"message":"Password changed"}]
                    //here first JSON object(index 0) is {"error":"no"}, second JSON object(index 1) is {"success":"yes"} and third JSON object(index 2) is {"message":"Password Changed"}.
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
                        if(success.equals("yes")==true) //password changed successfully
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
                //input: currentpassword, newpassword, confirmpassword (required).
                KeyValuePair.put("currentpassword",currentpassword);
                KeyValuePair.put("newpassword",newpassword);
                KeyValuePair.put("confirmpassword",confirmpassword);
                return KeyValuePair;
            }
        };

        request.setRetryPolicy(Common.getRetryPolicy());

        //addToRequestQueue() actually send request on server
        AppController.getInstance().addToRequestQueue(request);
    }
    private boolean ValidateInput() {

        currentpassword = txtcurrentpassword.getText().toString().trim();
        newpassword = txtnewpassword.getText().toString().trim();
        confirmpassword = txtconfirmpassword.getText().toString().trim();

        boolean isValid = true;
        if(currentpassword.length()==0) //current password is blank
        {
            isValid = false;
            txtcurrentpassword.setError("Current Password is required");
        }
        if(newpassword.length()==0)
        {
            isValid = false;
            txtnewpassword.setError("New password is required");
        }
        if(confirmpassword.length()==0)
        {
            isValid = false;
            txtconfirmpassword.setError("Confirm Password is required");
        }
        if(newpassword.equals(confirmpassword)==false)
        {
            isValid = false;
            Toast.makeText(ctx,"New Password and Confirm Password must be same",Toast.LENGTH_LONG).show();
        }

        return isValid;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        getSupportActionBar().hide(); //it hides actions bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);
        storage = new Storage(ctx);
    }
}
