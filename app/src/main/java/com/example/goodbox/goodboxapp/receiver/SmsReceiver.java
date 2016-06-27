package com.example.goodbox.goodboxapp.receiver;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.ArrayList;

import com.example.goodbox.goodboxapp.model.Message;
import com.example.goodbox.goodboxapp.provider.MessageContract;
import com.example.goodbox.goodboxapp.service.SyncUtils;


/**
 * Created by PRAVEEN-PC on 25-06-2016.
 */
/*
    Broadcast Receiver for incoming sms broadcast
 */
public class SmsReceiver extends BroadcastReceiver {
    private String TAG = SmsReceiver.class.getSimpleName();

    public SmsReceiver() {

    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;

        String str = "";

        if (bundle != null) {
            // Retrieve the SMS Messages received
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];

            String format = (String) bundle.get("format");
            int currentapiVersion = android.os.Build.VERSION.SDK_INT;

            if(msgs.length > 0 ) {

                ArrayList<ContentValues> contentValuesList = new ArrayList<ContentValues>();

                // For every SMS message received
                for (int i=0; i < msgs.length; i++) {

                    if (currentapiVersion < android.os.Build.VERSION_CODES.M){
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    } else{
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i],format);
                    }

                    Long tsLong = System.currentTimeMillis()/1000;
                    String strTimestamp = tsLong.toString();

                    Log.d(" ","appdebugtest RECEIVER address = "+msgs[i].getOriginatingAddress()
                                            +" Msg-body = "+msgs[i].getMessageBody().toString()
                                            +" Timestamp = "+strTimestamp
                                            );

                    ContentValues value = new ContentValues();
                    value.put(MessageContract.Message.COLUMN_NAME_NUMBER,msgs[i].getOriginatingAddress());
                    value.put(MessageContract.Message.COLUMN_NAME_MESSAGE_BODY,msgs[i].getMessageBody().toString());
                    value.put(MessageContract.Message.COLUMN_NAME_TIMESTAMP,strTimestamp);
                    value.put(MessageContract.Message.COLUMN_NAME_IS_SYNCED,0);

                    contentValuesList.add(value);

                    SyncUtils.autoSyncSMS(context,msgs[i].getOriginatingAddress(),msgs[i].getMessageBody().toString(),strTimestamp);
                }

                final ContentValues[] contentValuesBulk = contentValuesList.toArray(new ContentValues[0]);

                //Insert into DB
                context.getContentResolver().bulkInsert(MessageContract.Message.CONTENT_URI,contentValuesBulk);

            }
        }
    }
}