package com.android_batch_31.designdemo;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Switch;
import android.widget.TextView;
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

public class Login extends AppCompatActivity {
    Context ctx = this;
    @BindView(R.id.txtemail2) TextInputEditText txtemail2;
    @BindView(R.id.txtpassword2) TextInputEditText txtpassword2;
    @BindView(R.id.txtforgotpassword2) TextView txtforgotpassword2;
    Storage storage;
    String Email,Password;

    @Optional
    @OnClick({R.id.btnlogin,R.id.txtforgotpassword2}) void OnClick(View v){

        int id = v.getId();
        switch(id)
        {
           case R.id.btnlogin:
                boolean isValid = ValidateInput();
                if(isValid==true)
                {
                    SendRequest();
                }
            break;

                case R.id.txtforgotpassword2:
                Intent SwitchIntent = new Intent(ctx,ForgotPassword.class);
                startActivity(SwitchIntent);
                finish();
                break;

        }
    }

    private void SendRequest() {

        String WebServiceUrl = Common.getWebServiceUrl() + "login.php";

        StringRequest request = new StringRequest(StringRequest.Method.POST, WebServiceUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String res) {
                //this method will run if server return  response
                log.d(res);
                try {
                    JSONArray response = new JSONArray(res);
                    // [{"error":"input missing"}]
                    // [{"error":"no"},{"success":"no"},{"message":"invalid login attampt"}]
                    // [{"error":"no"},{"success":"yes"},{"message":"login successfull"},{"id":"4"}]
                    String error = response.getJSONObject(0).getString("error");
                    if(error.equals("no")==false) //some error occured on server
                    {
                        Toast.makeText(ctx,error,Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        String success = response.getJSONObject(1).getString("success");
                        String message = response.getJSONObject(2).getString("message");
                        Toast.makeText(ctx,message,Toast.LENGTH_LONG).show();
                        if(success.equals("yes")==true) //login successfull
                        {
                            String id = response.getJSONObject(3).getString("id");   // This id is received from customer table which is generated under id column
                            // of customer table after successfull registration. & it will be in " " (double quote, means as a String) as response. So we have to convert it in Integer before storing(writing) it in Storage class.
                            storage.write("id",Integer.parseInt(id));  // This will overwrite on id= -1, which was written in Storage class in Register.java
                            Intent SwitchIntent = new Intent(ctx,DashBoard.class);
                            startActivity(SwitchIntent);
                            finish();
                        }
                    }
                }
                catch (JSONException e) {
                    log.e(e.getMessage(),ctx);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //this method only runs in case of timeout as per retrypolicy
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
            //When we want to send Data or Parameters to server through "StringRequest.Method.POST" method, we have to override below method.
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> keyvaluePair = new HashMap<String,String>();
                keyvaluePair.put("email",Email);
                keyvaluePair.put("password",Password);
                return keyvaluePair;
            }
        };

        request.setRetryPolicy(Common.getRetryPolicy());   // or request.setRetryPolicy(new DefaultRetryPolicy(3000, 3, 1));

        AppController.getInstance().addToRequestQueue(request); //this is call to webservice
    }

    private boolean ValidateInput() {
        boolean isValid = true;
        Email = txtemail2.getText().toString().trim();
        Password = txtpassword2.getText().toString().trim();
        if(Email.length()==0)
        {
            txtemail2.setError("email is required");
            isValid = false;
        }
        if(Password.length()==0)
        {
            txtpassword2.setError("password is required");
            isValid = false;
        }
        return isValid;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide(); //it hides actions bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //it hides notification bar
        ButterKnife.bind(this);
        storage = new Storage(ctx);
    }
}
