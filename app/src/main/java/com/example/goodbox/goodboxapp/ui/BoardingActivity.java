package com.example.goodbox.goodboxapp.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.goodbox.goodboxapp.R;
import com.example.goodbox.goodboxapp.service.SyncUtils;

public class BoardingActivity extends AppCompatActivity implements View.OnClickListener{

    //UI Variables
    private Button btnEnter;
    private EditText urlEditTxv;

    //Others
    Context context;
    SharedPreferences mPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boarding);
        context = this;

        urlEditTxv = (EditText) findViewById(R.id.editText);
        btnEnter = (Button) findViewById(R.id.button);
        btnEnter.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.button:

                String url = urlEditTxv.getText().toString();

                if(url != null && !TextUtils.isEmpty(url)) {

                    mPreference = getSharedPreferences(SyncUtils.PREF_NAME,MODE_PRIVATE);
                    SharedPreferences.Editor editor = mPreference.edit();
                    editor.putString(SyncUtils.KEY_SERVER_URL,url);
                    editor.commit();

                    SyncUtils.CreateSyncAccount(getApplicationContext());
                    Intent mainIntent = new Intent(context,MainActivity.class);
                    startActivity(mainIntent);
                    finish();

                }else {

                    Toast.makeText(context,"Please Enter Server Address to start !!",Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }
}
