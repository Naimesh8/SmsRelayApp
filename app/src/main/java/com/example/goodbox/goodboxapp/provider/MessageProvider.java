package com.example.goodbox.goodboxapp.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by PRAVEEN-PC on 26-06-2016.
 */

/*
    ContentProvider implementation for DB operation
 */
public class MessageProvider extends ContentProvider{

    MessageDatabase mDatabaseHelper;

    /**
     * Content authority for this provider.
     */
    private static final String AUTHORITY = MessageContract.CONTENT_AUTHORITY;

    public static final int MESSAGES = 1;

    public static final int MESSAGES_ID = 2;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(AUTHORITY, "messages", MESSAGES);
        sUriMatcher.addURI(AUTHORITY, "messages/*", MESSAGES_ID);
    }

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new MessageDatabase(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MESSAGES:
                return MessageContract.Message.CONTENT_TYPE;
            case MESSAGES_ID:
                return MessageContract.Message.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }


    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MESSAGES:
                SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
                qb.setTables(MessageContract.Message.TABLE_NAME);
                Cursor c = qb.query(db, projection, selection, selectionArgs, null,
                        null, sortOrder);
                c.setNotificationUri(getContext().getContentResolver(), uri);
                return c;
            case MESSAGES_ID:
                throw new UnsupportedOperationException("Query not supported on URI: " + uri);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        assert db != null;
        final int match = sUriMatcher.match(uri);
        Uri result;
        switch (match) {
            case MESSAGES:
                long id = db.insertOrThrow(MessageContract.Message.TABLE_NAME, null, values);
                result = Uri.parse(MessageContract.Message.CONTENT_URI + "/" + id);
                break;
            case MESSAGES_ID:
                throw new UnsupportedOperationException("Insert not supported on URI: " + uri);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Send broadcast to registered ContentObservers, to refresh UI.
        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null, false);
        return result;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int count;
        switch (match) {
            case MESSAGES:
                count = performBulkInsert(db, values);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null, false);
        return count;
    }

    private int performBulkInsert(SQLiteDatabase db,ContentValues[] allValues) {

        int rowsAdded = 0;
        long rowId = 0;
        ContentValues values;
        try {
            db.beginTransaction();

            for (ContentValues initialValues : allValues) {
                values = initialValues == null ? new ContentValues() : new ContentValues(initialValues);

                rowId = db.insert(MessageContract.Message.TABLE_NAME, null, values);
                if (rowId > 0) {
                    rowsAdded++;
                }
            }

            db.setTransactionSuccessful();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            db.endTransaction();
        }

        return rowsAdded;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int count;
        switch (match) {
            case MESSAGES:
                count = db.delete(MessageContract.Message.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Send broadcast to registered ContentObservers, to refresh UI.
        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null, false);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int count;

        switch (match) {
            case MESSAGES:
                count = db.update(MessageContract.Message.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;

    }

    static class MessageDatabase extends SQLiteOpenHelper {

        // All Static variables
        private static final String TAG = MessageDatabase.class.getSimpleName();

        // Database Version
        private static final int DATABASE_VERSION = 1;

        // Database Name
        private static final String DATABASE_NAME = "MessageManager.db";

        // Contacts table name
        private static final String TABLE_MESSAGE = "message";

        private static final String CREATE_MESSAGE_TABLE = "CREATE TABLE " + MessageContract.Message.TABLE_NAME + "("
                + MessageContract.Message._ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + MessageContract.Message.COLUMN_NAME_NUMBER + " TEXT,"
                + MessageContract.Message.COLUMN_NAME_MESSAGE_BODY + " TEXT,"
                + MessageContract.Message.COLUMN_NAME_TIMESTAMP + " TEXT,"
                + MessageContract.Message.COLUMN_NAME_IS_SYNCED + " INTEGER" + ")";

        public MessageDatabase(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        // Creating Tables
        @Override
        public void onCreate(SQLiteDatabase db) {
            //Create DB table
            db.execSQL(CREATE_MESSAGE_TABLE);
        }

        // Upgrading database
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

    }
}
