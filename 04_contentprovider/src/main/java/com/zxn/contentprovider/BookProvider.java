package com.zxn.contentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by Administrator on 2015/7/9.
 */
public class BookProvider extends ContentProvider {

    // Logging helper tag. No significance to providers.
    private static final String TAG = "BookProvider";

    // Projection maps are similar to "as" construct in an sql statement where by you can rename the Columns;
    private static HashMap<String, String> sBooksProjectionMap;
    static {
        sBooksProjectionMap = new HashMap<String, String>();
        sBooksProjectionMap.put(BookProviderMetaData.BookTableMetaData._ID, BookProviderMetaData.BookTableMetaData._ID);

        // name, isbn, author
        sBooksProjectionMap.put(BookProviderMetaData.BookTableMetaData.BOOK_NAME, BookProviderMetaData.BookTableMetaData.BOOK_NAME);
        sBooksProjectionMap.put(BookProviderMetaData.BookTableMetaData.BOOK_ISBN, BookProviderMetaData.BookTableMetaData.BOOK_ISBN);
        sBooksProjectionMap.put(BookProviderMetaData.BookTableMetaData.BOOK_AUTHOR, BookProviderMetaData.BookTableMetaData.BOOK_AUTHOR);

        // created date, modified date
        sBooksProjectionMap.put(BookProviderMetaData.BookTableMetaData.CREATED_DATE, BookProviderMetaData.BookTableMetaData.CREATED_DATE);
        sBooksProjectionMap.put(BookProviderMetaData.BookTableMetaData.MODIFIED_DATE, BookProviderMetaData.BookTableMetaData.MODIFIED_DATE);
    }

    // Provide a mechanism to identify
    // all the incoming uri patterns
    private static final UriMatcher sUriMatcher;
    private static final int INCOMING_BOOK_COLLECTION_URI_INDICATOR = 1;
    private static final int INCOMING_SINGLE_BOOK_URI_INDICATOR = 2;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(BookProviderMetaData.AUTHORITY, "books", INCOMING_BOOK_COLLECTION_URI_INDICATOR);
        sUriMatcher.addURI(BookProviderMetaData.AUTHORITY, "books/#", INCOMING_SINGLE_BOOK_URI_INDICATOR);
    }

    // This class helps open, create, and ipgriade the database file
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, BookProviderMetaData.DATABASE_NAME, null, BookProviderMetaData.DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            Log.d(TAG, "inner oncreate called");
            sqLiteDatabase.execSQL("CREATE TABLE " + BookProviderMetaData.BookTableMetaData.TABLE_NAME + " ("
                    + BookProviderMetaData.BookTableMetaData._ID + " INTEGER PRIMARY KEY,"
                    + BookProviderMetaData.BookTableMetaData.BOOK_NAME + " TEXT,"
                    + BookProviderMetaData.BookTableMetaData.BOOK_ISBN + " TEXT,"
                    + BookProviderMetaData.BookTableMetaData.BOOK_AUTHOR + " TEXT,"
                    + BookProviderMetaData.BookTableMetaData.CREATED_DATE + " INTEGER,"
                    + BookProviderMetaData.BookTableMetaData.MODIFIED_DATE + " INTEGER"
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
            Log.d(TAG, "inner onupgrade called");
            Log.w(TAG, "Upgrading database from version" + i + " to " + i2 + ", which will destroy all old data");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BookProviderMetaData.BookTableMetaData.TABLE_NAME);
            onCreate(sqLiteDatabase);
        }
    }

    private DatabaseHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        Log.d(TAG, "main onCreate called");
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch(sUriMatcher.match(uri)) {
            case INCOMING_BOOK_COLLECTION_URI_INDICATOR:
                qb.setTables(BookProviderMetaData.BookTableMetaData.TABLE_NAME);
                qb.setProjectionMap(sBooksProjectionMap);
                break;
            case
        }
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
