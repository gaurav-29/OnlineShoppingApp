package com.android_batch_31.designdemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter {
    private Context ctx;
    private ArrayList<Cart> CartList;
    Storage storage;

    public CartAdapter(Context ctx, ArrayList<Cart> CartList) {
        this.ctx = ctx;
        this.CartList = CartList;
        storage = new Storage(this.ctx);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View CartRow = LayoutInflater.from(ctx).inflate(R.layout.cart_row,null);
        MyWidgetContainer container = new MyWidgetContainer(CartRow);
        return container;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Cart CurrentCart = CartList.get(position);
        MyWidgetContainer container = (MyWidgetContainer)holder;
        container.lblcarttitle.setText(CurrentCart.getTitle());
        container.lblcartprice.setText("Rs" + CurrentCart.getPrice());
        container.lblcartquantity.setText("" + CurrentCart.getQuantity());
        container.iblcartsubtotal.setText((CurrentCart.getQuantity() * CurrentCart.getPrice()) + "");
        //container.imgcartphoto.setImageURI();
        container.btnmovetowishllist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String cartid = CurrentCart.getId();  // here we taken datatype of cartid String, bcoz getId() method always returns String.
                String productid = CurrentCart.getProductid();
                String customerid = storage.read("id",Storage.INTEGER).toString();
                //int customerid = Integer.parseInt(storage.read("id",Storage.INTEGER).toString());
                //In above line, why we converted int id(which we got from read method) into toString then into int(Integer.parseInt)?
                //bcoz read() method always returns object(here id). so minimum we have to convert it into toString, then it becomes variable(here id). and then we can convert it into int.
                String WebServiceUrl = Common.getWebServiceUrl() + "movetowishlist.php?cartid=" + cartid +
                        "&customerid=" + customerid + "&productid=" + productid;  // Java's rule that variable name should not be in " ".
                SendRequest(WebServiceUrl, position);
            }
        });

        container.btndeletefromcart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String cartid = CurrentCart.getId();
                String WebServiceUrl = Common.getWebServiceUrl() + "deletefromcart.php?cartid=" + cartid;
                SendRequest(WebServiceUrl, position);
            }
        });
    }

    private void SendRequest(String webServiceUrl, int position) { //we can use one common SendRequest() method for calling above two webservices bcoz output of webservices is same.

        StringRequest request = new StringRequest(StringRequest.Method.GET, webServiceUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String res) {

                try {
                    JSONArray response = new JSONArray(res);  // Converts res into JSON Array, bcoz the response we get in Json format is actually in String.
                    // From this line onwards, code depends on output of webservice.

                    // [{"error":"no error"},
                    // {"count":2},
                    // {"cartid":"7","productid":"3","title":"iphone 11","price":"99000","photo":"iphone.jpg","quantity":"2"},
                    // {"cartid":"5","productid":"4","title":"Nexus one","price":"21000","photo":"nexus.jpg","quantity":"1"}]
                    String error = response.getJSONObject(0).getString("error");
                    if(error.equals("no error")==false)
                    {
                        Toast.makeText(ctx,error,Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        CartList.remove(position);   // This will remove the item from ArrayList (here CartList).
                        // Bcoz we need position here, we have to pass it as argument in SendRequest() method.
                        // We can not use cartid instead of position as argument, bcoz remove method needs position((press ctl+q on remove to see the detail).
                        notifyDataSetChanged();      // This will remove item from RecyclerView.
                                                     // Also item is removed from server by calling webservice.
                                                     // So as per above, if we need to delete item, we have to delete it from above 3 places.
                        Toast.makeText(ctx, response.getJSONObject(1).getString("message"),Toast.LENGTH_LONG).show();
                    }
                }
                catch (JSONException e) {
                    log.e(e.getMessage(),ctx);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) { // if we do not get the response from the server as per our set retry policy.
                AlertDialog.Builder b1 = new AlertDialog.Builder(ctx);
                b1.setTitle("Network error");
                b1.setMessage(Common.getMessage());
                b1.setIcon(R.mipmap.ic_launcher);
                b1.setPositiveButton("retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SendRequest(webServiceUrl, position);
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
        AppController.getInstance().addToRequestQueue(request);
    }

    @Override
    public int getItemCount() {
        return CartList.size();
    }


    class MyWidgetContainer extends RecyclerView.ViewHolder
    {
        public TextView lblcarttitle,lblcartprice,lblcartquantity,iblcartsubtotal;
        public ImageView imgcartphoto;
        public Button btndeletefromcart,btnmovetowishllist;

        public MyWidgetContainer(@NonNull View CartRow)
        {
            super(CartRow);
            lblcarttitle = CartRow.findViewById(R.id.lblcarttitle);
            lblcartprice = CartRow.findViewById(R.id.lblcartprice);
            lblcartquantity = CartRow.findViewById(R.id.lblcartquantity);
            iblcartsubtotal = CartRow.findViewById(R.id.iblcartsubtotal);
            imgcartphoto = CartRow.findViewById(R.id.imgcartphoto);
            btndeletefromcart = CartRow.findViewById(R.id.btndeletefromcart);
            btnmovetowishllist = CartRow.findViewById(R.id.btnmovetowishllist);
        }
    }
}
