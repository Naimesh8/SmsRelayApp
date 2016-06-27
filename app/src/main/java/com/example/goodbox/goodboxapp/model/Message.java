package com.example.goodbox.goodboxapp.model;

/**
 * Created by PRAVEEN-PC on 25-06-2016.
 */
public class Message {

    //property variables
    private String _msg;
    private String _number;
    private String _timestamp;
    private int _isSynced;
    private int _id;


    // constructor
    public Message(String number, String msg, String timestamp, int isSynced, int id) {

        this._number = number;
        this._msg = msg;
        this._timestamp = timestamp;
        this._isSynced = isSynced;
        this._id = id;
    }

    //getting msg
    public String getMsg() {
        return this._msg;
    }

    //setting msg
    public void setMsg(String name) {
        this._msg = name;
    }

    // getting phone number
    public String getPhoneNumber() {
        return this._number;
    }

    // setting phone number
    public void setPhoneNumber(String phone_number) {
        this._number = phone_number;
    }

    // getting timestamp
    public String getTimestamp() {
        return this._timestamp;
    }

    // setting timestamp
    public void setTimestamp(String timestamp) {
        this._timestamp = timestamp;
    }

    //getting syncStatus
    public int getSyncStatus() {
        return this._isSynced;
    }

    //setting syncStatus
    public void setSyncStatus(int syncStatus) {
        this._isSynced = syncStatus;
    }

    //getting rowID
    public int getID(){
        return _id;
    }
}

