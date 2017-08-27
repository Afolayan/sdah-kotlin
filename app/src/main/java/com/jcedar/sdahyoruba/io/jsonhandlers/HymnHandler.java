package com.jcedar.sdahyoruba.io.jsonhandlers;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.jcedar.sdahyoruba.helper.FileUtils;
import com.jcedar.sdahyoruba.helper.Lists;
import com.jcedar.sdahyoruba.io.model.Hymn;
import com.jcedar.sdahyoruba.provider.DataContract;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Afolayan on 29/05/2016.
 */
public class HymnHandler extends JSONHandler{
    private static final String TAG = HymnHandler.class.getSimpleName();
    private int hymnCount;
    public HymnHandler(Context context) {
        super(context);
    }

    @Override
    public ArrayList<ContentProviderOperation> parse(String json) throws IOException {
        if(json == null){
            return null;
        }
        Log.d(TAG, TextUtils.isEmpty(json) ? "Empty  Json" : json);

        final ArrayList<ContentProviderOperation> batch = Lists.newArrayList();

        Hymn[] currentHymn = Hymn.fromJson(json);
        hymnCount = currentHymn.length;


        for ( Hymn hymn: currentHymn) {
            try {
                Uri uri = DataContract.addCallerIsSyncAdapterParameter(
                        DataContract.Hymns.CONTENT_URI);
                ContentProviderOperation.Builder builder = ContentProviderOperation
                        .newInsert(uri)
                        .withValue(DataContract.Hymns.SONG_ID, hymn.getSongId())
                        .withValue(DataContract.Hymns.SONG_NAME, hymn.getSongTitle())
                        .withValue(DataContract.Hymns.SONG_TEXT, hymn.getSongText())
                        .withValue(DataContract.Hymns.ENGLISH_VERSION, hymn.getEnglishVersion())
                        .withValue(DataContract.Hymns.UPDATED, String.valueOf( System.currentTimeMillis()));

                Log.d(TAG, "Data from Json" + hymn.getSongTitle() );

                batch.add(builder.build());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return batch;
    }

     public ArrayList<ContentProviderOperation> parseHymns(String hymns) throws IOException {
        if(hymns == null){
            return null;
        }

        final ArrayList<ContentProviderOperation> batch = Lists.newArrayList();

        Hymn[] currentHymn = FileUtils.allHymnsSplitted(hymns);

         //save all hymns to an arraylist
         // for populating the hymn list
         ArrayList<Hymn> allHymns = FileUtils.saveHymnsToArray(currentHymn);
         FileUtils.setHymnList(mContext, allHymns);

         Log.e(TAG, "hymn size == "+currentHymn.length);

        hymnCount = currentHymn.length;


        for ( Hymn hymn: currentHymn) {
            try {
                Uri uri = DataContract.addCallerIsSyncAdapterParameter(
                        DataContract.Hymns.CONTENT_URI);
                ContentProviderOperation.Builder builder = ContentProviderOperation
                        .newInsert(uri)
                        .withValue(DataContract.Hymns.SONG_ID, hymn.getSongId())
                        .withValue(DataContract.Hymns.SONG_NAME, hymn.getSongTitle())
                        .withValue(DataContract.Hymns.SONG_TEXT, hymn.getSongText())
                        .withValue(DataContract.Hymns.ENGLISH_VERSION, hymn.getEnglishVersion())
                        .withValue(DataContract.Hymns.UPDATED, String.valueOf( System.currentTimeMillis()));

                Log.d(TAG, "Data from Json" + hymn.getSongTitle() );

                batch.add(builder.build());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return batch;
    }

    public int getHymnCount() {
        return hymnCount;
    }
}
