package com.android_batch_31.designdemo;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class DashBoard extends AppCompatActivity {

    Storage storage;

    @Optional
    @OnClick({R.id.linchangepassword,R.id.linforgotpassword,R.id.linshop,R.id.lincart,R.id.linwishlist,R.id.linlogout}) void OnClick(View v){
        int id = v.getId();
        Intent SwitchIntent=null;
        switch (id)
        {
            case R.id.linchangepassword:
                SwitchIntent = new Intent(this,ChangePassword.class);
                break;
          //  case R.id.linforgotpassword:
          //      SwitchIntent = new Intent(this,ForgotPassword.class);
          //      break;
            case R.id.linwishlist:
                SwitchIntent = new Intent(this,MyWishListContainer.class);
                break;
            case R.id.linshop:
                SwitchIntent = new Intent(this,CategoryContainer.class);
                break;
            case R.id.lincart:
                SwitchIntent = new Intent(this,CartContainer.class);
                break;
            case R.id.linlogout:
                storage.write("id",-1);  // This will open Login page when we click on Logout. Old id value which user has entered when previous
                // login will be deleted. Dashbooard screen will not open again.
                Toast.makeText(this,"Logout Successfull",Toast.LENGTH_LONG).show();
                SwitchIntent = new Intent(this,Login.class);
                break;
        }
        startActivity(SwitchIntent);
        if(id==R.id.linlogout)
            finish();  // when we press back button, when we are directed at login page from logout, we will be out of the app.
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        ButterKnife.bind(this);
        storage = new Storage(this);
        /*String username = this.getIntent().getExtras().getString("username");
        Toast.makeText(this,username,Toast.LENGTH_LONG).show();*/
    }
}
