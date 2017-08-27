package com.jcedar.sdahyoruba.provider;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.jcedar.sdahyoruba.io.model.Hymn;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper
{

    private static final String DATABASE_NAME = "sdahyoruba.db";
    private static final int DATABASE_VERSION = 103;
    private static String TAG = DatabaseHelper.class.getName();
    private final Context mContext;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_HYMN_TABLE);
        db.execSQL(SQL_CREATE_FAVORITE_HYMN_TABLE);
        db.execSQL(SQL_CREATE_STUDENTS_SEARCH_TABLE);

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.e(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + Tables.HYMNS_SEARCH);
        onCreate(db);
    }

//    public static void updateMonthlySummary(SQLiteDatabase db) {
//        db.execSQL("DELETE FROM " + Tables.MONTLY_SUMMARY);
//        //db.execSQL(SQL_UPDATE_SEARCH_TABLE);
//
//    }

    final static String SQL_CREATE_HYMN_TABLE = "CREATE TABLE "
            + Tables.HYMNS + "("
            + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + DataContract.Hymns.SONG_ID + " VARCHAR , "
            + DataContract.Hymns.SONG_NAME + " VARCHAR , "
            + DataContract.Hymns.SONG_TEXT + " VARCHAR , "
            + DataContract.Hymns.ENGLISH_VERSION + " VARCHAR , "
            + DataContract.Hymns.UPDATED + " LONG DEFAULT 0 )" ;



    public final static String SQL_CREATE_FAVORITE_HYMN_TABLE = "CREATE TABLE "
            + Tables.FAVORITE_HYMNS + "("
            + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DataContract.FavoriteHymns.SONG_ID + " VARCHAR , "
            + DataContract.FavoriteHymns.SONG_NAME + " VARCHAR , "
            + DataContract.FavoriteHymns.SONG_TEXT + " VARCHAR , "
            + DataContract.FavoriteHymns.ENGLISH_VERSION + " VARCHAR , "
            + DataContract.FavoriteHymns.UPDATED + " LONG DEFAULT 0 )" ;

    final static String SQL_CREATE_STUDENTS_SEARCH_TABLE = "CREATE VIRTUAL TABLE "
            + Tables.HYMNS_SEARCH + " USING fts3("
            + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DataContract.HymnSearchColumns.CONTENT + " TEXT NOT NULL, "
            + DataContract.HymnSearchColumns.SEARCH_HYMN_ID + " VARCHAR NOT NULL,"
            + "tokenize=simple)";

    final static String SQL_UPDATE_SEARCH_TABLE = "INSERT INTO " + Tables.HYMNS_SEARCH
            + "(" + DataContract.HymnSearchColumns.SEARCH_HYMN_ID + ","
            + DataContract.HymnSearchColumns.CONTENT + ")"

            + " SELECT " + DataContract.Hymns._ID + ", ("
            + DataContract.Hymns.ENGLISH_VERSION + "||'; '||"
            + DataContract.Hymns.SONG_NAME + "||'; '||"
            + DataContract.Hymns.SONG_TEXT + ")"

            + " FROM " + Tables.HYMNS;
    public static void deleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }


    private void upgradeDb(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
    }

    public static void updateSearchIndex(SQLiteDatabase db) {
        db.execSQL("DELETE FROM " + Tables.HYMNS_SEARCH);
        db.execSQL(SQL_UPDATE_SEARCH_TABLE);
        Log.d(TAG, "Search table updating");
    }

    interface Tables {
        String HYMNS = "hymns";
        String HYMNS_SEARCH = "hymns_search";
        String FAVORITE_HYMNS = "favorite_hymns";

        String HYMNS_SEARCH_JOIN = Tables.HYMNS
                + " INNER JOIN "+ Tables.HYMNS_SEARCH+" ON "
                + Tables.HYMNS+"."+ DataContract.Hymns._ID +"="
                + Tables.HYMNS_SEARCH+"."+ DataContract.HymnSearchColumns.SEARCH_HYMN_ID;


    }

    public static void rebuildDashbaord(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + Tables.HYMNS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.HYMNS_SEARCH);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.FAVORITE_HYMNS);

        /*db.execSQL(SQL_CREATE_HYMN_TABLE);
        db.execSQL(SQL_CREATE_FAVORITE_HYMN_TABLE);
        db.execSQL(SQL_CREATE_STUDENTS_SEARCH_TABLE);*/

    }

    public Cursor getSearch(String query){
        Cursor c = mContext.getContentResolver().query(
                DataContract.Hymns.CONTENT_URI,
                DataContract.Hymns.PROJECTION_ALL,
                DataContract.Hymns.SONG_NAME +" LIKE ? ",
                new String[]{query},
                DataContract.Hymns._ID +" ASC"
                );

        return c;
    }


    public int getHymnsSize(){
        Cursor c = mContext.getContentResolver().query(
                DataContract.Hymns.CONTENT_URI,
                DataContract.Hymns.PROJECTION_ALL, null, null, null);
        if( null != c ){
            int size = c.getCount();
            c.close();
            return size;
        }

        return 0;
    }

    public String[] getItemAtPosition(int position){
        Cursor c = mContext.getContentResolver().query(
                DataContract.Hymns.CONTENT_URI,
                DataContract.Hymns.PROJECTION_ALL, DataContract.Hymns._ID+"=?",
                new String[]{position+""}, null);
        String[] data = new String[4];
        if( c.moveToFirst() ){
            data[0] = c.getString( c.getColumnIndex(DataContract.Hymns.SONG_ID));
            data[1] = c.getString( c.getColumnIndex(DataContract.Hymns.SONG_NAME));
            data[2] = c.getString( c.getColumnIndex(DataContract.Hymns.SONG_TEXT));
            data[3] = c.getString( c.getColumnIndex(DataContract.Hymns.ENGLISH_VERSION));

            c.close();
            return data;
        }

        return data;
    }

    public ArrayList<Hymn> getHymns( String query ){

        ArrayList<Hymn> hymns = new ArrayList<>();
        String selection = DataContract.Hymns.SONG_NAME + " LIKE '%" +query + "%'";
        String[] projection = DataContract.Hymns.PROJECTION_ALL;
        String sortOrder = DataContract.Hymns.SONG_NAME + " ASC";

        Cursor c = mContext.getContentResolver().query(
                DataContract.Hymns.CONTENT_URI, projection, selection,null, sortOrder);
        if( c == null ) return null;
        if( c.moveToFirst() ){
            do{
                Hymn hymn = new Hymn();
                hymn.setSongId(c.getString(c.getColumnIndex(DataContract.Hymns.SONG_ID)));
                hymn.setSongTitle(c.getString(c.getColumnIndex(DataContract.Hymns.SONG_NAME)));
                hymn.setSongText(c.getString(c.getColumnIndex(DataContract.Hymns.SONG_TEXT)));
                hymn.setEnglishVersion(c.getString(c.getColumnIndex(DataContract.Hymns.ENGLISH_VERSION)));

                hymns.add( hymn );
            } while ( c.moveToNext() ) ;

            c.close();
        }

        return hymns;
    }
    public ArrayList<Hymn> getHymns(){

        ArrayList<Hymn> hymns = new ArrayList<>();
        String[] projection = DataContract.Hymns.PROJECTION_ALL;
        String sortOrder = DataContract.Hymns._ID + " ASC";

        Cursor c = mContext.getContentResolver().query(
                DataContract.Hymns.CONTENT_URI, projection, null,null, sortOrder);
        if( c == null ) return null;
        if( c.moveToFirst() ){
            do{
                Hymn hymn = new Hymn();
                hymn.setId(c.getString(c.getColumnIndex(DataContract.Hymns._ID)));
                hymn.setSongId(c.getString(c.getColumnIndex(DataContract.Hymns.SONG_ID)));
                hymn.setSongTitle(c.getString(c.getColumnIndex(DataContract.Hymns.SONG_NAME)));
                hymn.setSongText(c.getString(c.getColumnIndex(DataContract.Hymns.SONG_TEXT)));
                hymn.setEnglishVersion(c.getString(c.getColumnIndex(DataContract.Hymns.ENGLISH_VERSION)));

                hymns.add( hymn );
            } while ( c.moveToNext() ) ;

            c.close();
        }

        return hymns;
    }
    public Cursor getHymnsCursor( String query ){
        String selection = DataContract.Hymns.SONG_NAME + " LIKE '%" +query + "%' OR "
                +DataContract.Hymns.SONG_TEXT + " LIKE '%" +query+ "%'";
        String[] projection = DataContract.Hymns.PROJECTION_ALL;
        String sortOrder = DataContract.Hymns._ID + " ASC";


        return mContext.getContentResolver().query(
                DataContract.Hymns.CONTENT_URI, projection, selection,null, sortOrder);
    }

    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "mesage" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(SQLException sqlEx){
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }


    }

}
