package com.example.goodbox.goodboxapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.goodbox.goodboxapp.R;
import com.example.goodbox.goodboxapp.model.Message;

import java.util.List;

/**
 * Created by PRAVEEN-PC on 26-06-2016.
 */
public class SmsListAdapter extends RecyclerView.Adapter<SmsListAdapter.SmsViewHolder> {

    private List<Message> mMessageList;
    private onSMSListClickListener listener;

    public interface onSMSListClickListener {

        //void onYesButtonClick(int position);

        void onDeleteButtonClick(int position);
    }

    public SmsListAdapter(onSMSListClickListener listener){
        this.listener = listener;
    }

    public SmsListAdapter(List<Message> msgList,onSMSListClickListener listener) {
        this.listener = listener;
        initData(msgList);
    }

    public void initData(List<Message> msgList) {
        Log.d(" ","appdebugtest adapter initData");
        this.mMessageList = msgList;
        this.notifyDataSetChanged();
    }

    @Override
    public SmsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sms_list_item, parent, false);

        return new SmsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SmsViewHolder holder, int position) {

        Log.d(" ","appdebugtest adapter onBindViewHolder = Position = "+position);
        Message message = mMessageList.get(position);

        if(message != null) {

            Log.d(" ","appdebugtest adapter onBindViewHolder = Position = "+position+" message != null");
            holder.phoneTxv.setText(message.getPhoneNumber());
            holder.msgTxv.setText(message.getMsg());

        } else {

            Log.d(" ","appdebugtest adapter onBindViewHolder = Position = "+" message == null");
            holder.phoneTxv.setText(" ");
            holder.msgTxv.setText(" ");
        }

    }

    @Override
    public int getItemCount() {
        if(mMessageList != null) {

            Log.d(" ","appdebugtest getItemCout = "+mMessageList.size());
            return mMessageList.size();
        }
        else {
            Log.d(" ","appdebugtest getItemCout = Zero");
            return 0;
        }

    }

    public class SmsViewHolder extends RecyclerView.ViewHolder {
        public TextView phoneTxv, msgTxv;
        public ImageButton yesBtn,noBtn;

        public SmsViewHolder(View view) {
            super(view);

            phoneTxv = (TextView) view.findViewById(R.id.phone_number);
            msgTxv = (TextView) view.findViewById(R.id.message_body);

           // yesBtn = (ImageButton) view.findViewById(R.id.btn_yes);

            /*yesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null)
                        listener.onYesButtonClick(getLayoutPosition());
                }
            });*/

            noBtn = (ImageButton) view.findViewById(R.id.btn_no);
            noBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null)
                        listener.onDeleteButtonClick(getLayoutPosition());
                }
            });

        }
    }
}



