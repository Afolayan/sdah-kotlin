package com.jcedar.sdahyoruba.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.jcedar.sdahyoruba.helper.SelectionBuilder;

import java.util.Arrays;

public class DataProvider extends ContentProvider {

    private static final String TAG = DataProvider.class.getName();
    DatabaseHelper mOpenHelper = null;
    private static UriMatcher sUriMatcher = buildUriMatcher();

    private static final int HYMN_ID = 101;
    private static final int HYMN_LIST = 102;
    private static final int HYMNS_SEARCH = 103;

    private static final int FAVORITE_HYMNS_ID = 201;
    private static final int FAVORITE_HYMNS_LIST = 202;

    private static final int SEARCH_INDEX = 1701;


    public DataProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        Log.v(TAG, "delete(uri=" + uri + ", values=" + Arrays.toString(selectionArgs) + ")");
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        // TODO: Handle signOut
        if (uri == DataContract.BASE_CONTENT_URI) {
            // Handle whole database deletes (e.g. when signing out)
            deleteDatabase();
            notifyChange(uri, false);
            return 1;
        }


        int match = sUriMatcher.match(uri);
        if (match == HYMN_LIST || match == HYMN_ID) {
            DatabaseHelper.rebuildDashbaord(db);
            return 1;
        }

        final SelectionBuilder builder = buildSelection(uri, match);
        int retVal = builder.where(selection, selectionArgs).delete(db);
        notifyChange(uri, !DataContract.hasCallerIsSyncAdapterParameter(uri));
        return retVal;
    }

    @Override
    public String getType(Uri uri) {

        switch (sUriMatcher.match(uri)) {
            case HYMN_LIST:
                 return DataContract.Hymns.CONTENT_TYPE;
            case HYMN_ID:
                return DataContract.Hymns.CONTENT_ITEM_TYPE;

            case FAVORITE_HYMNS_LIST:
                return DataContract.FavoriteHymns.CONTENT_TYPE;
            case FAVORITE_HYMNS_ID:
                return DataContract.FavoriteHymns.CONTENT_ITEM_TYPE;


            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.v(TAG, "insert(uri=" + uri + ")");
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        long id;
        boolean syncToNetwork = DataContract.hasCallerIsSyncAdapterParameter(uri);
        switch (match) {
            case HYMN_LIST: {
                id = db.insertOrThrow(DatabaseHelper.Tables.HYMNS, null, values);
                notifyChange(uri, syncToNetwork);
                return getUriForId(id, uri);

            }case FAVORITE_HYMNS_LIST: {
                id = db.insertOrThrow(DatabaseHelper.Tables.FAVORITE_HYMNS, null, values);
                notifyChange(uri, syncToNetwork);
                return getUriForId(id, uri);
            }



            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Override
    public boolean onCreate() {

        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Log.v(TAG, "query(uri=" + uri + ", proj=" + Arrays.toString(projection) + ")");
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        // The bulk of the setting is done inside DataProvider.buildSelection()
        final SelectionBuilder builder = buildSelection(uri, match);
        switch (match) {
            case HYMN_LIST:
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = DataContract.Hymns.SORT_ORDER_DEFAULT;
                }
                break;
            case FAVORITE_HYMNS_LIST:
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = DataContract.FavoriteHymns.SORT_ORDER_DEFAULT;
                }
                break;


            default:
                break;
        }
        return builder.where(selection, selectionArgs).query(db, projection, sortOrder);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.i(TAG, "update(uri=" + uri + ", values=" + values.toString() + ")");
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "uri and match in update : " + uri + "match" + match);
        if (match == SEARCH_INDEX) {
            // update the search index
            Log.d(TAG, "calling updateSearchIndex ");
            DatabaseHelper.updateSearchIndex(db);
            return 1;
        }

        final SelectionBuilder builder = buildSelection(uri, match);
        int retVal = builder.where(selection, selectionArgs).update(db, values);
        boolean syncToNetwork = !DataContract.hasCallerIsSyncAdapterParameter(uri);
        notifyChange(uri, syncToNetwork);
        return retVal;
    }

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DataContract.CONTENT_AUTHORITY;


        matcher.addURI(authority, DataContract.PATH_HYMNS, HYMN_LIST);
        matcher.addURI(authority, DataContract.PATH_HYMNS + "/#", HYMN_ID);

        matcher.addURI(authority, "students/search/*", HYMNS_SEARCH);
        matcher.addURI(authority, DataContract.PATH_SEARCH_INDEX, SEARCH_INDEX);

        matcher.addURI(authority, DataContract.PATH_FAVORITE_HYMNS, FAVORITE_HYMNS_LIST);
        matcher.addURI(authority, DataContract.PATH_FAVORITE_HYMNS + "/#", FAVORITE_HYMNS_ID);

        return matcher;
    }

    private static Uri getUriForId(long id, Uri uri) {
        if (id > 0) {
            Uri itemUri = ContentUris.withAppendedId(uri, id);
            return itemUri;
        }
        //something went wrong
        throw new SQLException("Problem inserting into uri: " + uri);
    }

    private void notifyChange(Uri uri, boolean syncToNetwork) {
        Context context = getContext();
        if( context != null) {
            ContentResolver resolver = context.getContentResolver();
            resolver.notifyChange(uri, null, syncToNetwork);
        }

        // Widgets can't register content observers so we refresh widgets separately.
        // context.sendBroadcast(ScheduleWidgetProvider.getRefreshBroadcastIntent(context, false));
    }

    private SelectionBuilder buildSelection(Uri uri, int match) {

        Log.d(TAG, "uri and match:" + uri + "match" + match);
        final SelectionBuilder builder = new SelectionBuilder();
        switch (match) {
            case HYMN_LIST: {
                return builder.table(DatabaseHelper.Tables.HYMNS);
            }
            case HYMN_ID: {
                final String id = uri.getLastPathSegment();
                return builder.table(DatabaseHelper.Tables.HYMNS)
                        .where(DataContract.Hymns._ID + "=?", id);
            }
            case HYMNS_SEARCH: {
                final String query = DataContract.Hymns.getSearchQuery(uri);
                return builder.table(DatabaseHelper.Tables.HYMNS_SEARCH_JOIN)
                        .mapToTable(DataContract.Hymns.SONG_NAME, DatabaseHelper.Tables.HYMNS)
                        .mapToTable(DataContract.Hymns._ID, DatabaseHelper.Tables.HYMNS)
                        .mapToTable(DataContract.HymnSearchColumns.CONTENT, DatabaseHelper.Tables.HYMNS_SEARCH)
                        .where(DataContract.HymnSearchColumns.CONTENT + " MATCH ?", query);
            }

            case FAVORITE_HYMNS_LIST: {
                return builder.table(DatabaseHelper.Tables.FAVORITE_HYMNS);
            }
            case FAVORITE_HYMNS_ID: {
                final String id = uri.getLastPathSegment();
                return builder.table(DatabaseHelper.Tables.HYMNS)
                        .where(DataContract.Hymns.SONG_ID + "=?", id);
            }

            default: {
                throw new UnsupportedOperationException("Unknown uri for " + match + ": " + uri);
            }
        }
    }


    private void deleteDatabase() {
        // TODO: wait for content provider operations to finish, then tear down
        mOpenHelper.close();
        Context context = getContext();
        DatabaseHelper.deleteDatabase(context);
        mOpenHelper = new DatabaseHelper(getContext());
    }

}
