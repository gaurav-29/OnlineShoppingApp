package com.android_batch_31.designdemo;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import ss.com.bannerslider.Slider;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProductDetail extends AppCompatActivity {

    String productid;
    Context ctx = this;
    @BindView(R.id.lblproducttitle3) TextView lblproducttitle3;
    @BindView(R.id.lblproductprice3) TextView lblproductprice3;
    @BindView(R.id.lblproductweight3) TextView lblproductweight3;
    @BindView(R.id.lblproductsize3) TextView lblproductsize3;
    @BindView(R.id.lblproductdetail3) TextView lblproductdetail3;
    @BindView(R.id.banner_slider1) Slider slider;
    ArrayList<String> ImageList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        ButterKnife.bind(this);
        productid = this.getIntent().getExtras().getString("productid");
        Slider.init(new PicassoImageLoadingService(this));
        SendRequest();
    }

    private void SendRequest() {
        String WebServiceURL = Common.getWebServiceUrl() + "product.php";
        StringRequest request =
                new StringRequest(StringRequest.Method.POST, WebServiceURL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {
                        log.d(res);
                        try
                        {
                            JSONArray response = new JSONArray(res);
                            String error = response.getJSONObject(0).getString("error");
                            if(error.equals("no")==false)
                                Toast.makeText(ctx,error,Toast.LENGTH_LONG).show();
                            else
                            {
                                int count = response.getJSONObject(1).getInt("count");
                                if(count==0)
                                    Toast.makeText(ctx,"no product found",Toast.LENGTH_LONG).show();
                                else
                                {
                                    //{"id":"1","categoryid":"1","title":"shirt","price":"1200","stock":"97",
                                    // "size":"large, extra large, medium","weight":"120","photo":"shirt.jpg",
                                    // "detail":"cotton shirt for office work","islive":"1","isdeleted":"0","created_at":"0000-00-00"}]
                                    JSONObject object = response.getJSONObject(2);
                                    lblproducttitle3.setText(object.getString("title"));
                                    lblproductdetail3.setText(object.getString("detail"));
                                    lblproductprice3.setText(object.getString("price"));
                                    lblproductweight3.setText(object.getString("weight"));
                                    lblproductsize3.setText(object.getString("size"));
                                    LoadImagesFromServer();

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
                        Map<String, String> KeyValuePair = new HashMap<>();
                        KeyValuePair.put("productid", productid);
                        return KeyValuePair;
                    }
                };
        request.setRetryPolicy(Common.getRetryPolicy());
        AppController.getInstance().addToRequestQueue(request);

    }

    private void LoadImagesFromServer() {

        String WebServiceURL = Common.getWebServiceUrl() + "slider.php";
        StringRequest request =
                new StringRequest(StringRequest.Method.POST, WebServiceURL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {
                        log.d(res);
                        try
                        {
                            JSONArray response = new JSONArray(res);
                            String error = response.getJSONObject(0).getString("error");
                            if(error.equals("no")==false)
                                Toast.makeText(ctx,error,Toast.LENGTH_LONG).show();
                            else
                            {
                                int count = response.getJSONObject(1).getInt("count");
                                if(count==0)
                                    Toast.makeText(ctx,"no slider found",Toast.LENGTH_LONG).show();
                                else
                                {
                                    for(int i=2;i<count+2;i++)
                                    {
                                        ImageList.add(response.getJSONObject(i).getString("name"));
                                    }
                                    MainSliderAdapter adapter = new MainSliderAdapter(ImageList);
                                    slider.setAdapter(adapter);
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
                        AlertDialog.Builder b1 = new AlertDialog.Builder(ctx);
                        b1.setTitle("Network error");
                        b1.setMessage(Common.getMessage());
                        b1.setIcon(R.mipmap.ic_launcher);
                        b1.setPositiveButton("retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                LoadImagesFromServer();
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
                        Map<String, String> KeyValuePair = new HashMap<>();
                        KeyValuePair.put("productid", productid);
                        return KeyValuePair;
                    }
                };
        request.setRetryPolicy(Common.getRetryPolicy());
        AppController.getInstance().addToRequestQueue(request);
    }
}

