package com.android_batch_31.designdemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoryContainer extends AppCompatActivity {

    @BindView(R.id.reccategory) RecyclerView reccategory;
    Context ctx = this;
    ArrayList<Category> CategoryList = new ArrayList<>();  //we have made object CategoryList here bcoz we need to dump values in it.

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_container);

        ButterKnife.bind(this);
        SendRequest();
    }

    private void SendRequest() {

        String WebServiceUrl = Common.getWebServiceUrl() + "category.php";

        // We should not use JsonArrayRequest class or any other class instead of StringRequest class
        // to send the request as far as possible. Below we used just for our knowlwdge.

        JsonArrayRequest request = new JsonArrayRequest(WebServiceUrl, new Response.Listener<JSONArray>() {
            @Override
            // If server returns response this method will run.
            public void onResponse(JSONArray response) {
                log.d(response.toString());   // response will be in JSONArray type object, so we have to convert it in toString() to print in logcat.
                try
                {
                    // [{"error":"no"},{"count":10},{"id":"1","title":"BOOKS","photo":"books.jpg"},{"id":"2","title":"MOBILE","photo":"mobile.jpg"}]
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
                            Toast.makeText(ctx,"No category found",Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            String id,title,photo;
                            for(int i=2;i<count+2;i++)
                            {
                                id = response.getJSONObject(i).getString("id");
                                title = response.getJSONObject(i).getString("title");
                                photo = response.getJSONObject(i).getString("photo");
                                Category cat = new Category(id,title,photo);    // from here id, title, photo will be get and set in Category.java
                                CategoryList.add(cat);
                            }
                            CategoryAdapter adapter = new CategoryAdapter(ctx,CategoryList);  // From here, values of ctx & CategoryList will go to Constructor of CategoryAdapter.java
                            reccategory.setItemAnimator(new DefaultItemAnimator());
                            reccategory.setLayoutManager(new GridLayoutManager(ctx,2));
                            reccategory.setAdapter(adapter);  // This will call onCreateViewHolder() method once and after that will call onBindViewHolder() method
                            // of CategoryAdapter class as many item counts in Categoty.
                        }
                    }
                }
                catch (JSONException e)
                {
                    log.e(e.getMessage(),ctx);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            //this method only runs if server not responds as per our retrypolicy.
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
        });

        request.setRetryPolicy(Common.getRetryPolicy());
        AppController.getInstance().addToRequestQueue(request); //this will actually send request on server
    }
}
