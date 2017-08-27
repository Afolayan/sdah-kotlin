package com.jcedar.sdahyoruba.helper;

/*
import com.jcedar.sdahyoruba.provider.DataContract;
import com.jcedar.sdahyoruba.util.PrefUtils;
*/

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.util.Log;

import com.jcedar.sdahyoruba.io.jsonhandlers.HymnHandler;
import com.jcedar.sdahyoruba.provider.DataContract;

import java.io.IOException;
import java.util.ArrayList;

public class AppHelper  {

    private static final String TAG = AppHelper.class.getSimpleName();

    public static Context context;

    public AppHelper( ) {

    }

    public static void pullAndSaveAllHymnData(final Context context){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String response =  ServiceHandler.makeServiceCall
                            (AppSettings.SERVER_URL +"get_hymns.php", ServiceHandler.GET);
                    if(response == null){
                        return;
                    }
                    Log.e(TAG, "response " + response);

                    ArrayList<ContentProviderOperation> operations =
                            new HymnHandler(context).parse(response);
                    if (operations.size() > 0) {
                        ContentResolver resolver = context.getContentResolver();
                        resolver.applyBatch(DataContract.CONTENT_AUTHORITY, operations);
                    }
                }catch (IOException | OperationApplicationException | RemoteException e) {
                    e.printStackTrace();
                }

            }
        }).start();


    }

    public static void loadHymnDataFromFile(final Context context){

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        /**
                         * 1. Load file, parse file reference to FileUtils
                         * 2. Get the title and number
                         * 3. Save respective value into db
                         */
                        String response =  FileUtils.readFromFile(context);

                        ArrayList<ContentProviderOperation> operations =
                                new HymnHandler(context).parseHymns(response);
                        if (operations.size() > 0) {
                            ContentResolver resolver = context.getContentResolver();
                            resolver.applyBatch(DataContract.CONTENT_AUTHORITY, operations);
                        }
                    }catch (IOException | OperationApplicationException | RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }).start();


        }


}