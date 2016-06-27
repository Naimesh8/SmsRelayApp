package com.example.goodbox.goodboxapp.service;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.example.goodbox.goodboxapp.provider.MessageContract;

/**
 * Created by PRAVEEN-PC on 26-06-2016.
 */
public class SyncUtils {

    private static final long SYNC_FREQUENCY = 60 * 60;
    private static final String CONTENT_AUTHORITY = MessageContract.CONTENT_AUTHORITY;
    private static final String PREF_SETUP_COMPLETE = "setup_complete";

    public static final String KEY_SERVER_URL = "key_server_url";
    public static final String PREF_NAME = "AppPrefData";
    public static final String KEY_SYNC_TYPE = "key_sync_type";
    public static final String KEY_SYNC_SMS_ID = "key_sync_sms_id";
    public static final String KEY_SYNC_SMS_PHONE_NO = "key_sync_sms_phone_no";
    public static final String KEY_SYNC_SMS_MSG = "key_sync_sms_body";
    public static final String KEY_SYNC_SMS_TIMESTAMP = "key_sync_sms_timestamp";


    public static final int SYNC_TYPE_REFRESH = 100;
    //public static final int SYNC_TYPE_SMS = 101;
    public static final int SYNC_TYPE_RECEIVED_SMS = 102;

    public static final String ACCOUNT_TYPE = "com.example.android.messagesyncadapter.account";
    //private Context mContext;

    @TargetApi(Build.VERSION_CODES.FROYO)
    public static void CreateSyncAccount(Context context) {
        boolean newAccount = false;
        boolean setupComplete = PreferenceManager
                .getDefaultSharedPreferences(context).getBoolean(PREF_SETUP_COMPLETE, false);

        Account account = AccountService.GetAccount(ACCOUNT_TYPE);
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        if (accountManager.addAccountExplicitly(account, null, null)) {
            ContentResolver.setIsSyncable(account, CONTENT_AUTHORITY, 1);
            ContentResolver.setSyncAutomatically(account, CONTENT_AUTHORITY, true);
            ContentResolver.addPeriodicSync(account, CONTENT_AUTHORITY, new Bundle(),SYNC_FREQUENCY);
            newAccount = true;
        }

        if (newAccount || !setupComplete) {
            TriggerRefresh();
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putBoolean(PREF_SETUP_COMPLETE, true).commit();
        }
    }

    public static void TriggerRefresh() {
        Bundle syncBundle = new Bundle();

        syncBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        syncBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        syncBundle.putInt(KEY_SYNC_TYPE,SYNC_TYPE_REFRESH);
        ContentResolver.requestSync(
                AccountService.GetAccount(ACCOUNT_TYPE),
                MessageContract.CONTENT_AUTHORITY,
                syncBundle);
    }

    public static void autoSyncSMS(Context ctx,String phnNo,String msgBody,String timestamp) {

        Bundle syncBundle = new Bundle();

        syncBundle.putBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_BACKOFF,true);
        syncBundle.putBoolean(ContentResolver.SYNC_EXTRAS_DO_NOT_RETRY, false);
        syncBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        syncBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        syncBundle.putInt(KEY_SYNC_TYPE,SYNC_TYPE_RECEIVED_SMS);
        syncBundle.putString(KEY_SYNC_SMS_PHONE_NO,phnNo);
        syncBundle.putString(KEY_SYNC_SMS_MSG,msgBody);
        syncBundle.putString(KEY_SYNC_SMS_TIMESTAMP,timestamp);

        AccountManager am = AccountManager.get(ctx);
        Account[] acts = am.getAccountsByType(ACCOUNT_TYPE);

        ContentResolver.requestSync(
                acts[0],
                MessageContract.CONTENT_AUTHORITY,
                syncBundle);
    }



    /*public static void syncSMS(Context ctx,int smsID) {

        Bundle syncBundle = new Bundle();

        syncBundle.putBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_BACKOFF,true);
        syncBundle.putBoolean(ContentResolver.SYNC_EXTRAS_DO_NOT_RETRY, false);
        syncBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        syncBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        syncBundle.putInt(KEY_SYNC_TYPE,SYNC_TYPE_SMS);
        syncBundle.putInt(KEY_SYNC_SMS_ID,smsID);

        AccountManager am = AccountManager.get(ctx);
        Account[] acts = am.getAccountsByType(ACCOUNT_TYPE);

        ContentResolver.requestSync(
                acts[0],
                MessageContract.CONTENT_AUTHORITY,
                syncBundle);
        }*/
}
