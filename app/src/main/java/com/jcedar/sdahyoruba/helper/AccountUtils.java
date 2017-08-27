package com.jcedar.sdahyoruba.helper;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.jcedar.sdahyoruba.R;
import com.jcedar.sdahyoruba.provider.DataContract;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;


public class AccountUtils {
    private static final String TAG = AccountUtils.class.toString();

    private static final String PREF_CHOSEN_ACCOUNT = "chosen_account";
    private static final String PREF_AUTH_TOKEN = "auth_token";
    private static final String PREF_FIRST_RUN = "firstRun";
    public static final String PREF_FULLNAME = "fullName";
    public static final String PREF_ROLE = "roleName";
    private static final String PREF_EMAIL = "email";
    public static final String PROPERTY_REG_ID = "registration_id";
    public static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String PREF_SERVER_ID = "server_id";
    private static final String PREF_FONT_SIZE = "fonts";


    public static String getChosenAccountName(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_CHOSEN_ACCOUNT, null);
    }

    public static void setAuthToken(final Context context, final String authToken) {
        Log.i(TAG, "Auth token of length "
                + (TextUtils.isEmpty(authToken) ? 0 : authToken.length()));
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_AUTH_TOKEN, authToken).apply();
        Log.d(TAG, "Auth Token: " + authToken);
    }

    public static String getFullName(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_FULLNAME, null);
    }

    public static String getRole(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_ROLE, null);
    }
    //  create get and set for ParticipantType and MarketParticipantId

    public static void setFirstRun(final boolean isFirst, final Context context){

        Log.d(TAG, "Set first run to" + Boolean.toString(isFirst));
        //SharedPreferences sp = context.getSharedPreferences(PREF_ACCOUNT, Context.MODE_PRIVATE);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_FIRST_RUN, isFirst).apply();
    }

    public static boolean isFirstRun(final Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return  sp.getBoolean(PREF_FIRST_RUN, true);
    }

    public static Account getChosenAccount(final Context context) {
        String account = getChosenAccountName(context);
        if (account != null) {
            return new Account(account, context.getString(R.string.account_type));
        } else {
            return null;
        }
    }


    // If syncing return true
    public static boolean signOut(Context context){

        //Remove reg_id from server( push userId)
        //delete content provider
        //set all shared preference to null or delete

        //AccountUtils.setLoggedAccountName(context, getChosenAccountName(context));
        Account account = AccountUtils.getChosenAccount(context);
        if (account == null) return false;

        boolean syncActive = ContentResolver.isSyncActive(
                account, DataContract.CONTENT_AUTHORITY);
        boolean syncPending = ContentResolver.isSyncPending(
                account, DataContract.CONTENT_AUTHORITY);

        //Cancel sync
        boolean syncing = syncActive || syncPending;
        if(syncing){
            ContentResolver.cancelSync(account, DataContract.CONTENT_AUTHORITY);
            return true;
        }

        //Delete all server registration
        // 1a. GCM


        // 1b. Stop sync
        ContentResolver.setIsSyncable(account, DataContract.CONTENT_AUTHORITY, 0);

        // 2a. Invalidate token
        invalidateAuthToken(context);

        // 2b. Remove Account
       // SessionManager.removeAccount(account, context);

        // 3. Delete prefs data
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        //SharedPreferences preferences = context.getSharedPreferences(PREF_ACCOUNT, Context.MODE_PRIVATE);
        preferences.edit().clear().apply();

        // 4. Delete local data
        context.getContentResolver().delete(DataContract.BASE_CONTENT_URI, null, null);
        return false;

    }



    private static void invalidateAuthToken(Context context) {
        //SessionManager.invalidateAuthToken(context, getAuthToken(context));
        setAuthToken(context, null);
    }

   public static String getId(final Context context) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            return sp.getString(PREF_SERVER_ID, "");
        }



    public static void setUserEmail(final Context context, final String email){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_EMAIL, email).apply();
    }

    public static String getUserEmail(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_EMAIL, null);
    }

    private static final String PREF_PHONE_NUMBER_1 = "phone_number1";

    public static void setPhoneNumber(final Context context, final String phoneNumber){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_PHONE_NUMBER_1, phoneNumber).apply();
    }

    public static String getPhoneNumber(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_PHONE_NUMBER_1, "");
    }
    public static int getFontSettings(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.parseInt(sp.getString(PREF_FONT_SIZE, "22"));
    }

    public static String getRegistrationId(Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        Log.d(TAG, "registration id == "+registrationId);

        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = UIUtils.getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }















    public byte[] setPicture(String fullPhotoUrl) {
        byte[] picByte= null;
        Bitmap bb=null;
        try {
            bb = new LoadProfileImage().execute(fullPhotoUrl).get();
            picByte = getBytes(bb);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return picByte;
    }
    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }


    public static class LoadProfileImage extends AsyncTask<String, String, Bitmap> {
        // ImageView downloadedImage;
        Bitmap photoBitmap;

        public LoadProfileImage() {
            //this.downloadedImage = image;
        }
        @Override
        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap icon = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                icon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage() + "Hello world");
                e.printStackTrace();
            }
            return icon;
        }

        protected void onPostExecute(Bitmap result) {
            //downloadedImage.setImageBitmap(result);
            photoBitmap = result;
        }

    }


    public static boolean hasImage(Cursor cursor){
        String imagePresent = cursor.getString(
                cursor.getColumnIndex(DataContract.Hymns.SONG_ID));
        if(imagePresent != null){
            Log.e(TAG, "image =="+imagePresent);
            if( imagePresent.equals("1"))
                return true;
        }
        return false;
    }

}
