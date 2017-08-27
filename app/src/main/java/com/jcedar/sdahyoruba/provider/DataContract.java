package com.jcedar.sdahyoruba.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

import java.util.List;

/**
 * Created by Onyecar on 2/26/2015.
 */
public class DataContract
{
    //authority of data provider
    public static final String CONTENT_AUTHORITY = "com.jcedar.sdahyoruba.provider";

    //authority of base URI
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //Paths

    public static final String PATH_HYMNS = "hymns";
    public static final String PATH_FAVORITE_HYMNS = "favhymns";
    public static final String PATH_SEARCH = "search";


    private static final String CALLER_IS_SYNCADAPTER = "caller_is_sync_adapter";
    public static final String PATH_SEARCH_INDEX = "search_index";



    public static class Hymns implements HymnsColumns, BaseColumns, SyncColumns{
        /** Content URI for  hymns table */
        public static final Uri CONTENT_URI  =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_HYMNS).build();

        /** The mime type of a single item */
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE
                        + "/vnd.com.jcedar.sdahyoruba.provider.hymns";

        /** The mime type of a single item */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE
                        + "/vnd.com.jcedar.sdahyoruba.provider.hymns";

        public static Uri buildHymnUri(long hymnId){
            return CONTENT_URI.buildUpon().appendPath(Long.toString(hymnId)).build();
        }


        public static final String[] PROJECTION_ALL = {
                _ID, SONG_ID, SONG_NAME, SONG_TEXT, ENGLISH_VERSION,
                UPDATED,
        };
        public static final String[] PROJECTION = {
                _ID, SONG_ID, SONG_NAME, ENGLISH_VERSION
        };

        /** The default sort order for queries containing hymn */
        public static final String SORT_ORDER_DEFAULT = SONG_ID +" ASC";

        public static Uri buildSearchUri(String query) {
            return CONTENT_URI.buildUpon().appendPath(PATH_SEARCH).appendPath(query).build();
        }

        public static boolean isSearchUri(Uri uri) {
            List<String> pathSegments = uri.getPathSegments();
            return pathSegments.size() >= 2 && PATH_SEARCH.equals(pathSegments.get(1));
        }

        public static String getSearchQuery(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

     public static class FavoriteHymns implements HymnsColumns, BaseColumns, SyncColumns{
        /** Content URI for  favorite hymns table */
        public static final Uri CONTENT_URI  =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE_HYMNS).build();

        /** The mime type of a single item */
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE
                        + "/vnd.com.jcedar.sdahyoruba.provider.favhymns";

        /** The mime type of a single item */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE
                        + "/vnd.com.jcedar.sdahyoruba.provider.favhymns";

        public static Uri buildFavoriteHymnUri(long hymnId){
            return CONTENT_URI.buildUpon().appendPath(Long.toString(hymnId)).build();
        }


        public static final String[] PROJECTION_ALL = {
                _ID, SONG_ID, SONG_NAME, SONG_TEXT, ENGLISH_VERSION,
                UPDATED,

    };

        /** The default sort order for queries containing students */
        public static final String SORT_ORDER_DEFAULT = SONG_ID +" ASC";

        public static Uri buildSearchUri(String query) {
            return CONTENT_URI.buildUpon().appendPath(PATH_SEARCH).appendPath(query).build();
        }

        public static boolean isSearchUri(Uri uri) {
            List<String> pathSegments = uri.getPathSegments();
            return pathSegments.size() >= 2 && PATH_SEARCH.equals(pathSegments.get(1));
        }

        public static String getSearchQuery(Uri uri) {
            return uri.getLastPathSegment();
        }
    }




    public interface SyncColumns{
        String UPDATED = "updated";
    }

    interface HymnsColumns {
        String SONG_ID = "id";
        String SONG_NAME = "name";
        String ENGLISH_VERSION = "english";
        String SONG_TEXT = "text";

    }


    interface HymnSearchColumns {
        String SEARCH_HYMN_ID = "student_id";
        String CONTENT = "content";
    }

    public static Uri addCallerIsSyncAdapterParameter(Uri uri) {
        return uri.buildUpon().appendQueryParameter(
                DataContract.CALLER_IS_SYNCADAPTER, "true").build();
    }

    public static boolean hasCallerIsSyncAdapterParameter(Uri uri) {
        return TextUtils.equals("true",
                uri.getQueryParameter(DataContract.CALLER_IS_SYNCADAPTER));
    }
}
