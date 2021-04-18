package com.android_batch_31.designdemo;

import com.android.volley.DefaultRetryPolicy;

public class Common {

    public static String getBaseUrl()
    {
        return "http://192.168.43.166/android_batch_31/";
    }
    public static String getWebServiceUrl()
    {
        return getBaseUrl() + "ws/";
    }
    public static String getMessage()
    {
        return "please check \n " +
                "1) check WAMP server is running or not " +
                "\n 2) check both laptop/pc and mobile is in same network or not" +
                "\n 3) check path & spelling of the WebService " +
                "\n 4) please check & update ip address Common.java file";
    }
    public static String getBaseImageUrl() {
        return getBaseUrl() + "images/";
    }

    public static DefaultRetryPolicy getRetryPolicy()   // Consider like public int(here class DefaultRetryPolicy as datatype) getRetryPolicy() and
    // it will return int variable policy (here variable as object- new DefaultRetryPolicy(3000, 3, 1)).
    {
        return new DefaultRetryPolicy(3000, 3, 1);
    }
}
