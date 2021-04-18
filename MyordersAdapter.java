package com.android_batch_31.designdemo;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyordersAdapter extends RecyclerView.Adapter {

    private Context ctx;   // We cannot initialize ctx=this, bcoz this class is not extending AppCompatActivity.
    private ArrayList<Myorders> OrderList;
    public MyordersAdapter(Context ctx, ArrayList<Myorders> orderList) {
        this.ctx=ctx;
        OrderList=orderList;
        // I am here on 14.12.20(3.30pm). Now Show 3 TextViews billid, billdate, amount in myorders_row.xml, which is created.
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
