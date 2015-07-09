package com.zxn.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by Administrator on 2015/7/9.
 */
public class BookProvider extends ContentProvider {

    // Logging helper tag. No significance to providers.
    private static final String TAG = "BookProvider";

    // 定义一个投影映射以便我们可以重命名列，这里均设置的相同
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
    private static final UriMatcher sUriMatcher;  // 定义一个URI匹配器，用于之后的URI匹配
    private static final int INCOMING_BOOK_COLLECTION_URI_INDICATOR = 1;
    private static final int INCOMING_SINGLE_BOOK_URI_INDICATOR = 2;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(BookProviderMetaData.AUTHORITY, "books", INCOMING_BOOK_COLLECTION_URI_INDICATOR);
        sUriMatcher.addURI(BookProviderMetaData.AUTHORITY, "books/#", INCOMING_SINGLE_BOOK_URI_INDICATOR);
    }

    // 定义一个DatabaseHelper以便我们能够方便的打开，创建，更新数据库文件
    // This class helps open, create, and upgrade the database file
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
        switch(sUriMatcher.match(uri)) {  // 匹配URI
            case INCOMING_BOOK_COLLECTION_URI_INDICATOR:  // 如果是图书集合
                qb.setTables(BookProviderMetaData.BookTableMetaData.TABLE_NAME);  // 设置查询表
                qb.setProjectionMap(sBooksProjectionMap);  // 设置查询的投影映射，官方说明是：投影映射将从调用方传入查询的列名称映射到数据库列名称。
                                                           // 这对于重命名列以及在进行联结时消除列名称歧义非常有用。
                break;
            case INCOMING_SINGLE_BOOK_URI_INDICATOR:  // 如果是单个图书
                qb.setTables(BookProviderMetaData.BookTableMetaData.TABLE_NAME);
                qb.setProjectionMap(sBooksProjectionMap);
                qb.appendWhere(BookProviderMetaData.BookTableMetaData._ID + "=" + uri.getPathSegments().get(1)); // 给一些条件到查询中的Where子句
                break;
            default:
                throw new IllegalArgumentException("Unknow URI" + uri); //  抛出参数异常
        }

        // if no sort order is specified use the default
        String orderBy;
        if(TextUtils.isEmpty(sortOrder)) {
            orderBy = BookProviderMetaData.BookTableMetaData.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }

        // Get the databases and run query
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);  //  进行查询操作，返回当前查询结果的cursor

        // example of getting a count
        int i = c.getCount();

        // Tell the cursor what uri to watch
        // so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch(sUriMatcher.match(uri)) {
            case INCOMING_BOOK_COLLECTION_URI_INDICATOR:
                return BookProviderMetaData.BookTableMetaData.CONTENT_TYPE;  // 返回图书集合URI的MIME类型
            case INCOMING_SINGLE_BOOK_URI_INDICATOR:
                return BookProviderMetaData.BookTableMetaData.CONTENT_ITEM_TYPE;  // 返回单个图书URI的MIME类型
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        // Validate the requested uri
        if(sUriMatcher.match(uri) != INCOMING_BOOK_COLLECTION_URI_INDICATOR) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // contentValues可以存储一个键值映射用于ContentResolver处理
        ContentValues values;
        if(contentValues != null) {
            values = new ContentValues(contentValues);
        } else {
            values = new ContentValues();
        }

        Long now = Long.valueOf(System.currentTimeMillis());

        // 插入操作要保证每个列必须存在
        // Make sure that the fields are all set
        if(values.containsKey(BookProviderMetaData.BookTableMetaData.CREATED_DATE) == false) {
            values.put(BookProviderMetaData.BookTableMetaData.CREATED_DATE, now);
        }
        if(values.containsKey(BookProviderMetaData.BookTableMetaData.MODIFIED_DATE) == false) {
            values.put(BookProviderMetaData.BookTableMetaData.MODIFIED_DATE, now);
        }
        if(values.containsKey(BookProviderMetaData.BookTableMetaData.BOOK_NAME) == false) {
            throw new SQLException("Failed to insert row because Book Name is needed " + uri);
        }
        if(values.containsKey(BookProviderMetaData.BookTableMetaData.BOOK_ISBN) == false) {
            values.put(BookProviderMetaData.BookTableMetaData.BOOK_ISBN, "Unknown ISBN");
        }
        if(values.containsKey(BookProviderMetaData.BookTableMetaData.BOOK_AUTHOR) == false) {
            values.put(BookProviderMetaData.BookTableMetaData.BOOK_AUTHOR, "Unknown Author");
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(BookProviderMetaData.BookTableMetaData.TABLE_NAME, BookProviderMetaData.BookTableMetaData.BOOK_NAME, values);
        if(rowId > 0) {
            Uri insertedBookUri = ContentUris.withAppendedId(BookProviderMetaData.BookTableMetaData.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(insertedBookUri, null);
            return insertedBookUri;
        }

        throw new SQLException("Failed to insert row into " + uri);

    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch(sUriMatcher.match(uri)) {
            case INCOMING_BOOK_COLLECTION_URI_INDICATOR:
                count = db.delete(BookProviderMetaData.BookTableMetaData.TABLE_NAME, where, whereArgs);
                break;
            case INCOMING_SINGLE_BOOK_URI_INDICATOR:
                String rowId = uri.getPathSegments().get(1);
                count = db.delete(BookProviderMetaData.BookTableMetaData.TABLE_NAME, BookProviderMetaData.BookTableMetaData._ID + "=" + rowId
                        + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch(sUriMatcher.match(uri)) {
            case INCOMING_BOOK_COLLECTION_URI_INDICATOR:
                count = db.update(BookProviderMetaData.BookTableMetaData.TABLE_NAME, values, where, whereArgs);
                break;
            case INCOMING_SINGLE_BOOK_URI_INDICATOR:
                String rowId = uri.getPathSegments().get(1);
                count = db.update(BookProviderMetaData.BookTableMetaData.TABLE_NAME, values, BookProviderMetaData.BookTableMetaData._ID + "=" + rowId
                        + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
