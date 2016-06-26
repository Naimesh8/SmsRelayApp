package com.example.goodbox.goodboxapp.service;

import android.accounts.Account;
import android.annotation.TargetApi;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import com.example.goodbox.goodboxapp.provider.MessageContract;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by PRAVEEN-PC on 26-06-2016.
 */
class SyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String TAG = "SyncAdapter";

    //TODO = get value from user
    private static final String FEED_URL = "http://android-developers.blogspot.com/atom.xml";

    private static final int NET_CONNECT_TIMEOUT_MILLIS = 15000;  // 15 seconds

    private static final int NET_READ_TIMEOUT_MILLIS = 10000;  // 10 seconds

    private final ContentResolver mContentResolver;

    //TODO = think about this already defined in contract class
    private static final String[] PROJECTION = new String[] {
            MessageContract.Message._ID,
            MessageContract.Message.COLUMN_NAME_NUMBER,
            MessageContract.Message.COLUMN_NAME_MESSAGE_BODY,
            MessageContract.Message.COLUMN_NAME_TIMESTAMP,
            MessageContract.Message.COLUMN_NAME_IS_SYNCED};

    // Constants representing column positions from PROJECTION.
    public static final int COLUMN_ID = 0;
    public static final int COLUMN_NUMBER = 1;
    public static final int COLUMN_MESSAGE_BODY = 2;
    public static final int COLUMN_TIMESTAMP = 3;
    public static final int COLUMN_IS_SYNCED = 4;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {

        try {

            int sync_type = extras.getInt(SyncUtils.KEY_SYNC_TYPE);

            switch(sync_type) {

                case SyncUtils.SYNC_TYPE_REFRESH:

                    //Use this For Auto Sync Feature

                    break;

                case SyncUtils.SYNC_TYPE_SMS:

                    int smsID = extras.getInt(SyncUtils.KEY_SYNC_SMS_ID);
                    syncSMS(smsID,syncResult);

                    break;
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void syncSMS(int smsID,SyncResult syncResult) {

        String[] projection = new String[]{
                MessageContract.Message._ID,
                MessageContract.Message.COLUMN_NAME_NUMBER,
                MessageContract.Message.COLUMN_NAME_MESSAGE_BODY,
                MessageContract.Message.COLUMN_NAME_TIMESTAMP,
                MessageContract.Message.COLUMN_NAME_IS_SYNCED
        };

        String selection = MessageContract.Message._ID + " = ? ";
        String[] selectionArg = new String[]{String.valueOf(smsID)};

        Cursor cursor = mContentResolver.query(MessageContract.Message.CONTENT_URI, projection, selection, selectionArg, null);

        if(cursor != null) {

            cursor.moveToPosition(0);

            String strPhnNo = cursor.getString(cursor.getColumnIndex(MessageContract.Message.COLUMN_NAME_NUMBER));
            String strMsgBody = cursor.getString(cursor.getColumnIndex(MessageContract.Message.COLUMN_NAME_MESSAGE_BODY));
            String strTimestamp = cursor.getString(cursor.getColumnIndex(MessageContract.Message.COLUMN_NAME_TIMESTAMP));

            sendPostRequest(String.valueOf(smsID),strPhnNo,strMsgBody,strTimestamp,syncResult);

        }
    }


    private void sendPostRequest(String id,String phnNo,String msgBody,String timestamp,SyncResult syncResult) {

        HttpURLConnection conn = null;

        try {

            //TODO = make logic to pass URL here

            URL url = new URL("https://www.googleapis.com/youtube/v3/playlistItems?part=snippet"
                    + "&key=AIzaSyAhONZJpMCBqCfQjFUj21cR2klf6JWbVSo"
                    + "&access_token=");

            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setConnectTimeout(NET_CONNECT_TIMEOUT_MILLIS);
            conn.setReadTimeout(NET_READ_TIMEOUT_MILLIS);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json");

            // build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("message-from-a-number",phnNo);
            jsonObject.accumulate("message-body-contains-text",msgBody);
            jsonObject.accumulate("message-timestamp",timestamp);

            // convert JSONObject to JSON to String
            String json = "";
            json = jsonObject.toString();

            OutputStream os = conn.getOutputStream();
            os.write(json.getBytes());
            os.flush();

            int HttpResult = conn.getResponseCode();

            if (HttpResult == HttpURLConnection.HTTP_OK) {

                String result = "";
                InputStream inputStream = conn.getInputStream();

                BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
                String line = "";

                while((line = bufferedReader.readLine()) != null)
                    result += line;

                inputStream.close();

                //Update DB i.e. SMS synced Flag

                //TODO = think on this update sync flag or delete sms from DB ??
                String selection = MessageContract.Message._ID + " = ? ";
                String[] selectionArg = new String[]{String.valueOf(id)};

                ContentValues value = new ContentValues();
                value.put(MessageContract.Message.COLUMN_NAME_IS_SYNCED,1);

                mContentResolver.update(MessageContract.Message.CONTENT_URI,value,selection,selectionArg);
            }

        } catch (JSONException e) {

            e.printStackTrace();

        } catch (MalformedURLException e) {

            e.printStackTrace();
            syncResult.stats.numParseExceptions++;

        } catch (IOException e) {

            e.printStackTrace();
            syncResult.stats.numIoExceptions++;

        } catch (Exception e) {

            e.printStackTrace();
            syncResult.stats.numIoExceptions++;

        } finally {

            if(conn != null)
                conn.disconnect();

        }
    }
}
