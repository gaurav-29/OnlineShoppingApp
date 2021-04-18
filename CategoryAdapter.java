package com.android_batch_31.designdemo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter {

    private Context ctx;   // We cannot initialize ctx=this, bcoz this class is not extending AppCompatActivity.
    private ArrayList<Category> CategoryList;
    private ImageLoader imageLoader;

    public CategoryAdapter(Context ctx, ArrayList<Category> categoryList) { // CategoryList from CategoryContainer will be received here in Constructor.
        this.ctx = ctx;  // We cannot initialize ctx=this, bcoz this class is not extending AppCompatActivity.
        CategoryList = categoryList;
        imageLoader = AppController.getInstance().getImageLoader();
    }

    @NonNull
    @Override
    // Below method will be called only once.
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View CategoryRow = LayoutInflater.from(ctx).inflate(R.layout.category_row,null);
        MyWidgetContainer container = new MyWidgetContainer(CategoryRow);
        return container;
    }

    @Override
    // Below method will be called equals to item counts in Category. (It works as one type of loop).
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        Category CurrentCategory = CategoryList.get(position);
        MyWidgetContainer container = (MyWidgetContainer) holder;
        container.lblcategorytitle.setText(CurrentCategory.getTitle());
        String CategoryImageUrl = Common.getBaseImageUrl() + "category/" + CurrentCategory.getPhoto();
        container.imgcategory.setImageUrl(CategoryImageUrl,imageLoader);
        container.relcategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent SwitchIntent = new Intent(ctx, ProductContainer.class);
                SwitchIntent.putExtra("categoryid", CurrentCategory.getId());  // getId() method returns String so in ProductContainer, we will receive this Intent(categoryid) as String.
                ctx.startActivity(SwitchIntent);  // startActivity() is method of AppCompatActivity class. This class does not extends AppCompatActivity class. So we have used ctx.startActivity()
            }
        });
    }

    @Override
    public int getItemCount() {
        return CategoryList.size();
    }

    class MyWidgetContainer extends RecyclerView.ViewHolder
    {
        public TextView lblcategorytitle;
        public NetworkImageView imgcategory;
        public RelativeLayout relcategory;
        public MyWidgetContainer(@NonNull View CategoryRow)
        {
            super(CategoryRow);
            lblcategorytitle = CategoryRow.findViewById(R.id.lblcategorytitle); // we can't use findViewById directly bcoz CategoryAdapter class not extending AppCompatActivity class.
            imgcategory = CategoryRow.findViewById(R.id.imgcategory);
            relcategory = CategoryRow.findViewById(R.id.relcategory);
        }
    }
}
