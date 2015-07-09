package com.zxn.contentprovider;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * Created by Administrator on 2015/7/9.
 */
public class ProviderTester extends BaseTester {

    private static String tag = "Provider Tester";
    ProviderTester(Context ctx, IReportBack target) {
        super(ctx, target);
    }

    /**
     * 添加图书
     */
    public void addBook() {
        Log.d(tag, "Adding a book");
        ContentValues contentValues = new ContentValues();
        contentValues.put(BookProviderMetaData.BookTableMetaData.BOOK_NAME, "book1");
        contentValues.put(BookProviderMetaData.BookTableMetaData.BOOK_ISBN, "isbn-1");
        contentValues.put(BookProviderMetaData.BookTableMetaData.BOOK_AUTHOR, "author-1");

        ContentResolver contentResolver = this.mContext.getContentResolver();
        Uri uri = BookProviderMetaData.BookTableMetaData.CONTENT_URI;
        Log.d(tag, "book insert uri:" + uri);
        Uri insertedUri = contentResolver.insert(uri, contentValues);
        Log.d(tag, "inserted uri:" + insertedUri);
        this.reportString("Inserted Uri:" + insertedUri);
    }

    /**
     * 删除图书
     */
    public void removeBook() {
        int i = getCount();
        ContentResolver contentResolver = this.mContext.getContentResolver();
        Uri uri = BookProviderMetaData.BookTableMetaData.CONTENT_URI;
        Uri delUri = Uri.withAppendedPath(uri, Integer.toString(i));
        reportString("Del Uri:" + delUri);
        contentResolver.delete(delUri, null, null);
        this.reportString("NewCount:" + getCount());
    }

    /**
     * 显示所有图书
     */
    public void showBooks() {
        Uri uri = BookProviderMetaData.BookTableMetaData.CONTENT_URI;
        Activity a = (Activity)this.mContext;
        Cursor cursor = a.managedQuery(uri,
                null,  // projection
                null,  // selection string
                null,  // selection args array of strings
                null); // sort order
        int iname = cursor.getColumnIndex(BookProviderMetaData.BookTableMetaData.BOOK_NAME);
        int iisbn = cursor.getColumnIndex(BookProviderMetaData.BookTableMetaData.BOOK_ISBN);
        int iauthor = cursor.getColumnIndex(BookProviderMetaData.BookTableMetaData.BOOK_AUTHOR);

        // Report your indexes
        reportString("name,isbn,author:" + iname + iisbn + iauthor);

        // walk through the rows based on indexes
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            // gather values
            String id = cursor.getString(1);
            String name = cursor.getString(iname);
            String isbn = cursor.getString(iisbn);
            String author = cursor.getString(iauthor);

            // Report or log the row
            StringBuffer cbuf = new StringBuffer(id);
            cbuf.append(",").append(name);
            cbuf.append(",").append(isbn);
            cbuf.append(",").append(author);
            reportString(cbuf.toString());
        }

        // Report how many rows have been read
        int numberOfRecords = cursor.getCount();
        reportString("Num of Records:" + numberOfRecords);

        // close the cursor
        // ideally this should be done in a finally block
        cursor.close();
    }

    /**
     * 获得个数
     * @return
     */
    private int getCount() {
        Uri uri = BookProviderMetaData.BookTableMetaData.CONTENT_URI;
        Activity activity = (Activity)this.mContext;
        Cursor cursor = activity.managedQuery(uri,
                null,  // projection
                null,  // selection string
                null,  // selection args array of strings
                null); // sort order
        int numberOfRecords = cursor.getCount();
        cursor.close();
        return numberOfRecords;
    }

    private void report(int stringid) {
        this.mReportTo.reportBack(tag, this.mContext.getString(stringid));
    }
    private void reportString(String s) {
        this.mReportTo.reportBack(tag, s);
    }
    private void reportString(String s, int stringid) {
        this.mReportTo.reportBack(tag, s);
        report(stringid);
    }

}
