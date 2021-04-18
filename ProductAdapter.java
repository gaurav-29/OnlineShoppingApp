package com.android_batch_31.designdemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter {

    private Context ctx;
    private ArrayList<Product> ProductList;
    String ImageUrl;
    ImageLoader loader;
    Storage storage;
    RelativeLayout relproduct;

    public ProductAdapter(Context ctx, ArrayList<Product> productList) {
        this.ctx = ctx;
        ProductList = productList;
        this.ImageUrl = Common.getBaseImageUrl() + "product/";
        loader = AppController.getInstance().getImageLoader();
        storage = new Storage(ctx);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View ProductRow = LayoutInflater.from(ctx).inflate(R.layout.product_row, null);
        MyWidgetContainer container = new MyWidgetContainer(ProductRow);
        return container;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {  // when this method runs first time, value of position will be 0,
        // after that it will increases as per the repeatation of this method.
        MyWidgetContainer container = (MyWidgetContainer) holder;
        Product CurrentProduct = ProductList.get(position);
        container.lblproducttitle.setText(CurrentProduct.getTitle());
        container.lblproductprice.setText("Rs " + CurrentProduct.getPrice());
        container.imgproductphoto.setImageUrl(ImageUrl + CurrentProduct.getPhoto(), loader);

        container.btnaddtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(ctx,"Added to cart",Toast.LENGTH_LONG).show();
                SendRequest(CurrentProduct,"addtocart.php");	//we have passed CurrentProduct & WebServiceName as argument.So after
                // this, it will be received as argument at this method definition below.
            }
        });

        container.btnaddtowishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(ctx,"Added to wishlist",Toast.LENGTH_LONG).show();
               SendRequest(CurrentProduct,"addtowishlist.php");	//we have passed CurrentProduct & WebServiceName as argument.So after
                                                        // this, it will be received as argument at this method definition below.
            }
        });

        container.relproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent SwitchIntent = new Intent(ctx, ProductDetail.class);
                SwitchIntent.putExtra("productid",CurrentProduct.getId());
                ctx.startActivity(SwitchIntent);
            }
        });
    }

    private void SendRequest(Product CurrentProduct, String WebServiceName) {    // CurrentProduct & WebServiceName arguments are received here from above(method declaration);

        String productid = CurrentProduct.getId();
        String WebServiceUrl = Common.getWebServiceUrl() + WebServiceName + "?productid=" +
                productid + "&customerid=" + storage.read("id",Storage.INTEGER).toString();	//here id(customerid) in storage class is id generated by system at the time of login.It is stored in customer table under id field in database.
        JsonArrayRequest request = new JsonArrayRequest(WebServiceUrl, new Response.Listener<JSONArray>() {
            @Override			//  we used JsonArrayRequest class so don't need getParams() method.
            public void onResponse(JSONArray response) {
                //[{"error":"no error"},{"message":"product is already added into wishlist"}]
                log.d(response.toString());
                try
                {
                    String error = response.getJSONObject(0).getString("error");
                    if(error.equals("no error")==false)
                        Toast.makeText(ctx,error,Toast.LENGTH_LONG).show();
                    else
                    {
                        Toast.makeText(ctx,response.getJSONObject(1).getString("message"),
                                Toast.LENGTH_LONG).show();
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
                        SendRequest(CurrentProduct, WebServiceName);
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

    @Override
    public int getItemCount() {
        return ProductList.size();
    }

    class MyWidgetContainer extends RecyclerView.ViewHolder
    {
        public TextView lblproducttitle, lblproductprice;
        public ImageView btnaddtocart, btnaddtowishlist;
        public NetworkImageView imgproductphoto;
        public RelativeLayout relproduct;
        // One argument parameterised constructor.
        public MyWidgetContainer(@NonNull View itemView) {
            super(itemView);

            lblproducttitle = itemView.findViewById(R.id.lblproducttitle);
            lblproductprice = itemView.findViewById(R.id.lblproductprice);
            btnaddtocart = itemView.findViewById(R.id.btnaddtocart);
            btnaddtowishlist = itemView.findViewById(R.id.btnaddtowishlist);
            imgproductphoto = itemView.findViewById(R.id.imgproductphoto);
            relproduct = itemView.findViewById(R.id.relproduct);
        }
    }
}