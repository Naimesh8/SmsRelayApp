package com.example.goodbox.goodboxapp.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by PRAVEEN-PC on 26-06-2016.
 */

/*
    Contract class for perform DB operation via content provider
 */
public class MessageContract {

    private MessageContract() {

    }

    public static final String CONTENT_AUTHORITY = "com.example.goodbox.messagesyncadapter";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final String PATH_MESSAGES = "messages";

    public static class Message implements BaseColumns {

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.messagesyncadapter.messages";

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.messagesyncadapter.message";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MESSAGES).build();

        public static final String TABLE_NAME = "message";

        public static final String COLUMN_NAME_NUMBER = "number";

        public static final String COLUMN_NAME_MESSAGE_BODY = "message_body";

        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";

        public static final String COLUMN_NAME_IS_SYNCED = "is_synced";

    }
}