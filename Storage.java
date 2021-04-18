package com.android_batch_31.designdemo;

import android.content.Context;
import android.content.SharedPreferences;

public class Storage {
    public static final int INTEGER = 1;
    public static final int FLOAT = 2;
    public static final int STRING = 3;
    public static final int LONG = 4;
    public static final int BOOLEAN = 5;
    //instances variables
    private Context ctx;
    private SharedPreferences pref;
    private SharedPreferences.Editor DataWriter; //used to write data into sharedpreferences file
    public Storage(Context ctx){
        this.ctx = ctx;
        pref = ctx.getSharedPreferences("easylearn",Context.MODE_PRIVATE);
        DataWriter = pref.edit();
    }
    // function overloading

    public void write(String key,int value){
        DataWriter.putInt(key,value);
        DataWriter.commit();
    }
    public void write(String key,long value){
        DataWriter.putLong(key,value);
        DataWriter.commit();
    }
    public void write(String key,float value){
        DataWriter.putFloat(key,value);
        DataWriter.commit();
    }
    public void write(String key,boolean value){
        DataWriter.putBoolean(key,value);
        DataWriter.commit();
    }
    public void write(String key,String value){
        DataWriter.putString(key,value);
        DataWriter.commit();
    }

    public Object read(String key,int DataType){
        Object temp = null;
        if(DataType==INTEGER)
        {
            temp = pref.getInt(key,0);
        }
        else  if(DataType==FLOAT)
        {
            temp = pref.getFloat(key,0.0f);
        }
        else  if(DataType==STRING)
        {
            temp = pref.getString(key,"");
        }
        else  if(DataType==BOOLEAN)
        {
            temp = pref.getBoolean(key,false);
        }
        else  if(DataType==LONG)
        {
            temp = pref.getLong(key,0);
        }
        return  temp;
    }

}
