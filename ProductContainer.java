package com.android_batch_31.designdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
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

public class ProductContainer extends AppCompatActivity {

    @BindView(R.id.recproduct) RecyclerView recproduct;
    Context ctx = this;
    String categoryid;
    ArrayList<Product> ProductList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_container);
        ButterKnife.bind(this);
        categoryid = this.getIntent().getExtras().getString("categoryid"); // we have received categoryid as string, bcoz getId() method in putExtra in CategoryAdapter returns String.
        SendRequest();
    }

    private void SendRequest() {

        String WebServiceUrl = Common.getWebServiceUrl() + "product.php";

        StringRequest request = new StringRequest(StringRequest.Method.POST, WebServiceUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String res) {  // Mostly response from server will be in JSON format. It may not be in JSON format.
                log.d(res);

                try {
                    JSONArray response = new JSONArray(res);  // Converts res into JSON Array, bcoz the response we get in Json format is actually in String.
                    // From this line onwards, code depends on output of webservice.

                    // [{"error":"no"},
                    // {"count":2},
                    // {"id":"1","title":"atomic habits","price":"500","photo":"habits.jpg"},
                    // {"id":"2","title":"rich dad poor dad","price":"1000","photo":"dad.jpg"}]
                    String error = response.getJSONObject(0).getString("error");
                    if(error.equals("no")==false)
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
                            String id,title,photo,price;
                            for(int i=2;i<count+2;i++)
                            {
                                id = response.getJSONObject(i).getString("id");  //we have received id as String here bcoz the Json output of id is in " " (double quote means String).
                                title = response.getJSONObject(i).getString("title");
                                photo = response.getJSONObject(i).getString("photo");
                                price = response.getJSONObject(i).getString("price");
                                Product p = new Product(id,title,photo,price);
                                ProductList.add(p);
                            }
                            ProductAdapter adapter = new ProductAdapter(ctx,ProductList);
                            recproduct.setItemAnimator(new DefaultItemAnimator());
                            recproduct.setLayoutManager(new GridLayoutManager(ctx,1));
                            recproduct.setAdapter(adapter);  // This will call onCreateViewHolder() method once and after that will call onBindViewHolder() method
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
                KeyValuePair.put("categoryid",categoryid);
                return KeyValuePair;
            }
        };
        request.setRetryPolicy(Common.getRetryPolicy());
        AppController.getInstance().addToRequestQueue(request);     // this method sends request on server.

    }
}
