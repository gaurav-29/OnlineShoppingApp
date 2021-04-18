package com.android_batch_31.designdemo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;
public class Checkout extends AppCompatActivity {

    @BindView(R.id.txtfullname) EditText txtfullname;
    @BindView(R.id.txtaddressline1) EditText txtaddressline1;
    @BindView(R.id.txtaddressline2) EditText txtaddressline2;
    @BindView(R.id.txtpincode) EditText txtpincode;
    @BindView(R.id.txtcity) EditText txtcity;
    @BindView(R.id.txtremarks) EditText txtremarks;
    @BindView(R.id.txtmobile2) EditText txtmobile2;
    @BindView(R.id.lincheckout) LinearLayout lincheckout;
    private Context ctx = this;
    Storage storage;
    Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        storage = new Storage(ctx);
        ButterKnife.bind(this);
        snackbar = Snackbar.make(lincheckout,"",Snackbar.LENGTH_INDEFINITE);
    }
    @Optional
    @OnClick(R.id.btnplaceorder) void onClick(View v){
        log.d("button clicked event");
        SendRequest();
    }
    private void SendRequest()
    {
        if(isValidInput()==true)
        {
            String WebServiceUrl = Common.getWebServiceUrl() + "checkout.php";
            StringRequest request = new StringRequest(StringRequest.Method.POST, WebServiceUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String res) {
                    try
                    {
                        JSONArray response = new JSONArray(res);
                        //[{"error":"no error"},{"success":"no"},{"message":"cart is empty"}]
                        //[{"error":"no error"},{"success":"no"},{"message":"following items are out of stock iphone 11 Nexus one "}]
                        //[{"error":"no error"},{"success":"yes"},{"message":"order placed successfully orderid = 3"}]
                        String error = response.getJSONObject(0).getString("error");
                        if(error.equals("no error")==false) //error
                        {
                            Toast.makeText(ctx,error,Toast.LENGTH_LONG).show();  // This will print the "input is missing" message as per ReturnError method in this webservice and code for this is defined in connection.php webservice.
                        }
                        else //no error
                        {
                            String success = response.getJSONObject(1).getString("success");
                            String message = response.getJSONObject(2).getString("message");
                            Toast.makeText(ctx,message,Toast.LENGTH_LONG).show();
                            Intent SwitchIntent;
                            if(success.equals("no")==true)
                                SwitchIntent = new Intent(ctx,CartContainer.class);
                            else //order placed successfully
                                SwitchIntent = new Intent(ctx,CategoryContainer.class);
                        }
                    }
                    catch (JSONException e) {
                        log.e(e.getMessage(),ctx);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //timeout
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
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> keyvaluepair = new HashMap<>();
                    keyvaluepair.put("customerid",storage.read("id",Storage.INTEGER).toString());
                    keyvaluepair.put("fullname",txtfullname.getText().toString());
                    keyvaluepair.put("mobile",txtmobile2.getText().toString());
                    keyvaluepair.put("address1",txtaddressline1.getText().toString());
                    keyvaluepair.put("address2",txtaddressline2.getText().toString());
                    keyvaluepair.put("city",txtcity.getText().toString());
                    keyvaluepair.put("pincode",txtpincode.getText().toString());
                    keyvaluepair.put("remarks",txtremarks.getText().toString());
                    return keyvaluepair;
                }
            };
            request.setRetryPolicy(Common.getRetryPolicy());
            AppController.getInstance().addToRequestQueue(request); //actaully web service call
        }

    }
    private boolean isValidInput()
    {
        boolean isvalid = true;
        if(txtfullname.getText().toString().trim().length()==0)
        {
            txtfullname.setError("FullName can not be blank");
            isvalid=false;
        }
        if(txtaddressline1.getText().toString().trim().length()==0)
        {
            txtaddressline1.setError("Address Line 1 can not blank");
            isvalid=false;
        }
        if(txtaddressline2.getText().toString().trim().length()==0)
        {
            txtaddressline2.setError("Address Line 2 can not blank");
            isvalid=false;
        }
        if(txtmobile2.getText().toString().trim().length()<10)
        {
            txtmobile2.setError("Mobile No can not blank");
            isvalid=false;
        }
        if(txtcity.getText().toString().trim().length()==0)
        {
            txtcity.setError("City can not blank");
            isvalid=false;
        }
        if(txtpincode.getText().toString().trim().length()==0)
        {
            txtpincode.setError("Pincode can not blank");
            isvalid=false;
        }
        return isvalid;
    }
}
