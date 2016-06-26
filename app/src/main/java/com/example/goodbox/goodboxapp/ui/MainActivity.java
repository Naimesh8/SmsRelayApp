package com.example.goodbox.goodboxapp.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.goodbox.goodboxapp.R;
import com.example.goodbox.goodboxapp.adapter.SmsListAdapter;
import com.example.goodbox.goodboxapp.service.SyncUtils;
import com.example.goodbox.goodboxapp.model.Message;
import com.example.goodbox.goodboxapp.provider.MessageContract;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks,SmsListAdapter.onSMSListClickListener{

    //constants
    private static final int LOADER_ID_MESSAGES = 1;

    //UI Variables
    private RecyclerView mSmsListView;
    private LinearLayoutManager mRecyclerLinearLayoutManager;
    private TextView mPhoneNoTxv,mMsgBodyTxv;
    private ImageButton mYesBtn,mNoBtn;

    //Data variables

    //TODO = reduce scope of arraylist
    private ArrayList<Message> messageArrayList = new ArrayList<>();
    private SmsListAdapter smsListAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initControls();
    }

    private void initControls() {

        mSmsListView = (RecyclerView) findViewById(R.id.sms_list);
        mRecyclerLinearLayoutManager = new LinearLayoutManager(this);
        mSmsListView.setLayoutManager(mRecyclerLinearLayoutManager);

        mPhoneNoTxv = (TextView) findViewById(R.id.phone_number);
        mMsgBodyTxv = (TextView) findViewById(R.id.message_body);

        mYesBtn = (ImageButton) findViewById(R.id.btn_yes);
        mNoBtn = (ImageButton) findViewById(R.id.btn_no);

        if (smsListAdapter == null) {
            smsListAdapter = new SmsListAdapter(this);
        }

        mSmsListView.setAdapter(smsListAdapter);

        loadMessages();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    private void loadMessages() {

        Loader<Cursor> loaderMessages = getSupportLoaderManager().getLoader(LOADER_ID_MESSAGES);

        if (loaderMessages != null && !loaderMessages.isReset()) {
            getSupportLoaderManager().restartLoader(LOADER_ID_MESSAGES, null, this);
        } else {
            getSupportLoaderManager().initLoader(LOADER_ID_MESSAGES, null, this);
        }
    }


    @Override
    public Loader onCreateLoader(int id, Bundle args) {

        switch (id) {

            case LOADER_ID_MESSAGES:

                String[] projection = new String[]{
                        MessageContract.Message._ID,
                        MessageContract.Message.COLUMN_NAME_NUMBER,
                        MessageContract.Message.COLUMN_NAME_MESSAGE_BODY,
                        MessageContract.Message.COLUMN_NAME_TIMESTAMP,
                        MessageContract.Message.COLUMN_NAME_IS_SYNCED
                };


                String selection = MessageContract.Message.COLUMN_NAME_IS_SYNCED + " = ? ";
                String[] selectionArg = new String[]{"0"};

                CursorLoader cursorLoader = new CursorLoader(this, MessageContract.Message.CONTENT_URI, projection, selection, selectionArg, null);
                return cursorLoader;

        }

        return null;
    }


    @Override
    public void onLoadFinished(Loader loader, Object data) {

        //TODO = Update Adapter & populate UI
        switch (loader.getId()) {

            case LOADER_ID_MESSAGES:

                messageArrayList.clear();

                if (data != null) {

                    Cursor cursor = (Cursor) data;

                    if (cursor.getCount() > 0) {

                        for(int index = 0 ; index < cursor.getCount() ; index ++) {

                            cursor.moveToPosition(index);

                            int id = cursor.getInt(cursor.getColumnIndex(MessageContract.Message._ID));
                            String strPhnNo = cursor.getString(cursor.getColumnIndex(MessageContract.Message.COLUMN_NAME_NUMBER));
                            String strMsgBody = cursor.getString(cursor.getColumnIndex(MessageContract.Message.COLUMN_NAME_MESSAGE_BODY));
                            String strTimestamp = cursor.getString(cursor.getColumnIndex(MessageContract.Message.COLUMN_NAME_TIMESTAMP));
                            int isSyncF = cursor.getInt(cursor.getColumnIndex(MessageContract.Message.COLUMN_NAME_IS_SYNCED));

                            Message message = new Message(strPhnNo,strMsgBody,strTimestamp,isSyncF);
                            messageArrayList.add(message);

                        }
                    }
                }

                //Update UI
                smsListAdapter.initData(messageArrayList);

                break;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    @Override
    public void onYesButtonClick(int position) {

        //start sync with server

        //TODO = option 1) do need to delete from DB & start sync OR
               // option 2) sync start & on success response delete from DB

        if(messageArrayList != null) {
            Message msg = messageArrayList.get(position);
            if (msg != null) {
                SyncUtils.syncSMS(msg.getID());
            }
        }
    }

    @Override
    public void onNoButtonClick(int position) {

        if(messageArrayList != null) {

            Message msg = messageArrayList.get(position);
            if(msg != null) {

                int id = msg.getID();
                String strID = String.valueOf(id);

                String selection = MessageContract.Message._ID + " = ? ";
                String[] selectionArg = new String[]{strID};

                int result = getContentResolver().delete(
                                MessageContract.Message.CONTENT_URI,
                                selection,
                                selectionArg);

                if(result > 0) {
                    messageArrayList.remove(position);

                    if(smsListAdapter != null)
                        smsListAdapter.initData(messageArrayList);
                }
            }
        }
    }
}
