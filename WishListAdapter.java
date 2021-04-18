package com.android_batch_31.designdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WishListAdapter extends RecyclerView.Adapter {
    private Context ctx;
    private ArrayList<WishList> MyWishList = new ArrayList<>();

    public WishListAdapter(Context ctx, ArrayList<WishList> myWishList)
    {
        this.ctx = ctx;
        MyWishList = myWishList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View WishListRow = LayoutInflater.from(ctx).inflate(R.layout.wishlist_row,null);
        MyWidgetContainer container = new MyWidgetContainer(WishListRow);
        return container;
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        WishList CurrentWishList = MyWishList.get(position);
        MyWidgetContainer container = (MyWidgetContainer)holder;
        container.lblwishlisttitle.setText(CurrentWishList.getTitle());
        container.lblwishlistprice.setText(CurrentWishList.getPrice());
        //container.imgwishlist.setImageURI();
        container.btnmovetocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ctx,"Item Added into cart",Toast.LENGTH_LONG).show();
            }
        });

        container.btndeletefromwishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ctx,"Item removed from Wishlist",Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    public int getItemCount()
    {
        return MyWishList.size();
    }
    class MyWidgetContainer extends RecyclerView.ViewHolder
    {
        public TextView lblwishlisttitle,lblwishlistprice;
        public ImageView imgwishlist,btnmovetocart,btndeletefromwishlist;

        public MyWidgetContainer(@NonNull View WishListRow)
        {
            super(WishListRow);
            lblwishlisttitle = WishListRow.findViewById(R.id.lblwishlisttitle);
            lblwishlistprice = WishListRow.findViewById(R.id.lblwishlistprice);
            imgwishlist = WishListRow.findViewById(R.id.imgwishlist);
            btnmovetocart = WishListRow.findViewById(R.id.btnmovetocart);
            btndeletefromwishlist = WishListRow.findViewById(R.id.btndeletefromwishlist);
        }
    }
}
