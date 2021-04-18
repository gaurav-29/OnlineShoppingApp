package com.android_batch_31.designdemo;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    // FirebaseMessagingService is a base class for receiving messages from Firebase Cloud Messaging.
    // It also provides functionality to automatically display notifications.

    Storage storage;

    public void onNewToken(String RegID){

        // Called when a new token for the default Firebase project is generated.
        // This is invoked after app install when a token is first generated, and again if the token changes.

        storage = new Storage(getApplicationContext());
        log.d("Refreshed RegID: " + RegID);
        storage.write("regid", RegID);
        log.d("RegID from Storage: " + storage.read("regid", Storage.STRING).toString());
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        // Called when a message is received.
        // This is also called when a notification message is received while the app is in the foreground.
        // The notification parameters can be retrieved with RemoteMessage#getNotification().

        log.d("remoteMessage: " + remoteMessage.getFrom());
        if(remoteMessage.getData().size()>0)
        {
            log.d("Message data payload: " + remoteMessage.getData());
            String data = remoteMessage.getData().get("data");
            log.d("data: " + data);

            // Below data in {} is called object. In below object there are key:value pairs.
            // {"image":null,
            // "is_background":false,
            // "payload":[],
            // "title":"First message1",    // key:value pair
            // "message":"First message content1",
            // "timestamp":"2021-02-05 1:41:17"}

            try
            {   // Below code is to do JSON Parsing to get title & message in logcat window.
                JSONObject object = new JSONObject(data);
                String title,message;
                title = object.getString("title");
                message = object.getString("message");
                log.d(title);
                log.d(message);

                //Below code is to send Notification to user.
                Context ctx = getApplicationContext();
                Intent intent = new Intent(ctx,DashBoard.class);
                PendingIntent PIntent = PendingIntent.getActivity(ctx,100,intent,PendingIntent.FLAG_UPDATE_CURRENT); // FLAG_UPDATE_CURRENT will replace old notification with new notification

                MyNotificationManager.addNotification(title,message,ctx,PIntent,true,true);

            } catch (JSONException e)
            {
                e.printStackTrace();
            }

        }
    }
}
