package com.jianghongkui.volumemanager.other;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.jianghongkui.volumemanager.model.Column;
import com.jianghongkui.volumemanager.util.MLog;
import com.jianghongkui.volumemanager.util.VolumeDBHelper;

/**
 * Created by jianghongkui on 2016/9/21.
 */

public class PackageVolumeProvider extends ContentProvider {

    private final static String TAG = "PackageVolumeProvider";

    public final static Uri CONTENT_URI = Uri.parse("content://" + Column.AUTOR + "/" + Column.TABLE);

    private static final int ALLROWS = 1;
    private static final int SINGLE_ROW = 2;

    private final String KEY_ID = Column._id;


    // UriMatcher类主要用来匹配Uri
    private static final UriMatcher uriMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);

    static {
        // 注册向外部程序提供的Uri
        uriMatcher.addURI(Column.AUTOR, Column.TABLE, ALLROWS);
        uriMatcher.addURI(Column.AUTOR, Column.TABLE + "/#", SINGLE_ROW);
    }

    private VolumeDBHelper helper;

    private Context context;


    @Override
    public boolean onCreate() {
        context = getContext();
        helper = new VolumeDBHelper(context);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = helper.getReadableDatabase();
        String groupBy = null;
        String having = null;
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(Column.TABLE);
        switch (uriMatcher.match(uri)) {
            case SINGLE_ROW:
                //String rowID = uri.getPathSegments().get(1);
                //builder.appendWhere(KEY_ID + "" + rowID);
            default:
                break;
        }
        Cursor cursor = builder.query(database, projection, selection, selectionArgs, groupBy, having, sortOrder);//database.query(Column.TABLE, projection,selection, selectionArgs, groupBy, having, sortOrder);
        MLog.d(TAG, "query frem database ,this cursor is " + cursor);
        return cursor;
    }


    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase database = helper.getReadableDatabase();
        //想要通过传入一个空的Contentvalue对象的方式向数据库中添加一个空行，
        //必须使用nullColumnHack参数来指定可以设置为null的列名
        String nullColumnHack = null;

        long id = database.insert(Column.TABLE, null, values);
        MLog.d(TAG, "insert into database ,it will be insert into " + id);
        if (id > 1) {
            Uri insertedID = ContentUris.withAppendedId(CONTENT_URI, id);
            context.getContentResolver().notifyChange(insertedID, null);
            return insertedID;
        } else
            return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = helper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case SINGLE_ROW:
                String rowID = uri.getPathSegments().get(1);
                selection = KEY_ID + "=" + rowID + (!TextUtils.isEmpty(selection) ? "AND (" + selection + ')' : "");
            default:
                break;
        }
        //想要返回删除的项的数量，必现指定一条where子句。删除所有的行并返回一个值，同时传入1。
        if (selection == null)
            selection = "1";
        int deleteCount = database.delete(Column.TABLE, selection, selectionArgs);
        MLog.d(TAG, "delete from database,the changed count is " + deleteCount);
        context.getContentResolver().notifyChange(uri, null);
        return deleteCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase database = helper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case SINGLE_ROW:
                String rowID = uri.getPathSegments().get(1);
                selection = KEY_ID + "=" + rowID + (!TextUtils.isEmpty(selection) ? "AND (" + selection + ')' : "");
            default:
                break;
        }
        int updateCount = database.update(Column.TABLE, values, selection, selectionArgs);
        MLog.d(TAG, "update database ,the changed count is" + updateCount);
        context.getContentResolver().notifyChange(uri, null);
        return updateCount;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case ALLROWS:
                return "vnd.android.cursor.dir/vnd.jianghongkui.volumemanager";
            case SINGLE_ROW:
                return "vnd.android.cursor.item/vnd.jianghongkui.volumemanager";
            default:
                throw new IllegalArgumentException("Unsupported URI:" + uri);
        }
    }
}
