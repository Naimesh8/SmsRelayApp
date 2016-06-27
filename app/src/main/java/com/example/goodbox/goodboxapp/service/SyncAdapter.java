package com.example.goodbox.goodboxapp.service;

import android.accounts.Account;
import android.annotation.TargetApi;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

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

    private String SERVER_URL=null;

    private static final int NET_CONNECT_TIMEOUT_MILLIS = 15000;  // 15 seconds

    private static final int NET_READ_TIMEOUT_MILLIS = 10000;  // 10 seconds

    private final ContentResolver mContentResolver;
    private Context mContext;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
        mContext = context;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
        mContext = context;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {

        try {

            int sync_type = extras.getInt(SyncUtils.KEY_SYNC_TYPE);

            if(SERVER_URL != null && !TextUtils.isEmpty(SERVER_URL)) {

            } else {

                SharedPreferences mPreference = mContext.getSharedPreferences(SyncUtils.PREF_NAME,mContext.MODE_PRIVATE);
                String url = mPreference.getString(SyncUtils.KEY_SERVER_URL,"");

                SERVER_URL = url;
            }

            switch(sync_type) {

                case SyncUtils.SYNC_TYPE_REFRESH:

                    //Use this For Auto Sync Feature

                    break;

                case SyncUtils.SYNC_TYPE_RECEIVED_SMS:

                    String phnNo = extras.getString(SyncUtils.KEY_SYNC_SMS_PHONE_NO);
                    String msgBody = extras.getString(SyncUtils.KEY_SYNC_SMS_MSG);
                    String timestamp = extras.getString(SyncUtils.KEY_SYNC_SMS_TIMESTAMP);

                    sendPostRequest(phnNo,msgBody,timestamp,syncResult);

                    break;
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void sendPostRequest(String phnNo,String msgBody,String timestamp,SyncResult syncResult) {

        if(SERVER_URL == null)
            return;

        HttpURLConnection conn = null;

        try {

            URL url = new URL(SERVER_URL);

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
