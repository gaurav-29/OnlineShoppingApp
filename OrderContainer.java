package com.android_batch_31.designdemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderContainer extends AppCompatActivity {

    @BindView(R.id.recorder) RecyclerView recorder;
    Context ctx = this;
    ArrayList<Myorders> OrderList = new ArrayList<>();
    String customerid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_container);
        ButterKnife.bind(this);
        Storage storage = new Storage(ctx);
        customerid = storage.read("id",Storage.INTEGER).toString();
        SendRequest();
    }

    private void SendRequest() {

        String WebServiceUrl = Common.getWebServiceUrl() + "myorders.php";

        StringRequest request = new StringRequest(StringRequest.Method.POST, WebServiceUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String res) {  // Mostly response from server will be in JSON format. It may not be in JSON format.
                log.d(res);

                try {
                    JSONArray response = new JSONArray(res);  // Converts res into JSON Array, bcoz the response we get in Json format is actually in String.
                    // From this line onwards, code depends on output of webservice.

                    // [{"error":"no error"},
                    // {"count":1},
                    // {"billid":"4","billdate":"2020-12-05","amount":"120000","paymentmode":"1","fullname":"Gaurav Rathod","mobile":"1234567890","address1":"Hilldrive","city":"Bhavnagar","pincode":"364002"}]
                    String error = response.getJSONObject(0).getString("error");
                    if(error.equals("no error")==false)
                    {
                        Toast.makeText(ctx,error,Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        int count = response.getJSONObject(1).getInt("count");
                        if(count==0)
                        {
                            Toast.makeText(ctx,"No product found",Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            String billid,billdate,amount;
                            for(int i=2;i<count+2;i++)
                            {
                                billid = response.getJSONObject(i).getString("billid");  //we have received id as String here bcoz the Json output of id is in " " (double quote means String).
                                billdate = response.getJSONObject(i).getString("billdate");
                                amount = response.getJSONObject(i).getString("amount");
                                Myorders orders = new Myorders(billid,billdate,amount);
                                OrderList.add(orders);
                            }
                            MyordersAdapter adapter = new MyordersAdapter(ctx,OrderList);
                            recorder.setItemAnimator(new DefaultItemAnimator());
                            recorder.setLayoutManager(new GridLayoutManager(ctx,1));
                            recorder.setAdapter(adapter);  // This will call onCreateViewHolder() method once and after that will call onBindViewHolder() method
                            // of ProductAdapter class as many item counts in Product.
                        }
                    }
                } catch (JSONException e) {
                    log.e(e.getMessage(),ctx);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
                Map<String,String> KeyValuePair = new HashMap<String, String>();
                KeyValuePair.put("customerid",customerid);
                return KeyValuePair;
            }
        };
        request.setRetryPolicy(Common.getRetryPolicy());
        AppController.getInstance().addToRequestQueue(request);     // this method sends request on server.

    }

}

