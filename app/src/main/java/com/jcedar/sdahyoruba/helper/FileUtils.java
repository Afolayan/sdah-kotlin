package com.jcedar.sdahyoruba.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.jcedar.sdahyoruba.R;
import com.jcedar.sdahyoruba.io.model.Hymn;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Afolayan Oluwaseyi on 19/09/2016.
 */
public class FileUtils {

    private static final String PREF_HYMN_LIST = "hymn_list";

    /**
     * @param context
     * @return read String from file
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static String readFromFile(Context context)
            throws  IOException {

        InputStream is = context.getResources().openRawResource(R.raw.all_hymns);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, "UTF-8"));

        StringBuilder sub = new StringBuilder();
        String mLine = reader.readLine();
        int hymnId = 0;
        while (mLine != null ){
            sub.append(mLine).append("\n");
            mLine = reader.readLine();
        } reader.close();

        return sub.toString();
    }

    /**
     * @return String array of individual strings
     */
    public static String[] splitHymns(String fromFile)  {
        String[] hymns = fromFile.split("..fileend..\\n");

        System.out.println("hymns count == "+hymns.length);

        /**
         * Creates new file from the original txt file
         */
        for (int i = 0; i < hymns.length; i++) {

            System.out.println(hymns[i].split("\n")[0]); //prints hymn number
            System.out.println(hymns[i].split("\n")[1]); //prints hymn title
            System.out.println(hymns[i].split("\n")[2]); //prints hymns English text
        }
        return hymns;
    }

    /**
     * @param singleHymn of individual hymn
     * @return Hymn object
     * array[0]= hymn number
     * array[1]= hymn title
     * array[2]= hymn english alternative
     */
    public static Hymn splittedHymn(String singleHymn){

        String[] split =  singleHymn.split("\\n");
        Hymn hymn = new Hymn();
        hymn.setSongId(split[0]);
        hymn.setSongTitle(split[1]);
        hymn.setEnglishVersion(split[2]);

        String song = singleHymn.replace(split[0], "")
                .replace(split[1], "")
                .replace(split[2], "");
        hymn.setSongText( song.trim() );
        //hymn.setSongText( singleHymn.substring( singleHymn.indexOf("\\n\\n"), singleHymn.length() ) );

        return hymn;
    }


    public static Hymn[] allHymnsSplitted(String readFromFile){
        String[] strings = splitHymns(readFromFile);
        Hymn[] hymns = new Hymn[strings.length];

        for (int i = 0; i < strings.length; i++) {
            hymns[i] = splittedHymn(strings[i]);
        }

        return hymns;
    }

    public static String[] splitHymnStanza(String song){
        return song.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
    }

    public static String removeBrackets(String string){
        return string.replaceAll("[\\[\\](){}]", "").trim();
    }

    public static ArrayList<Hymn> saveHymnsToArray(Hymn[] hymns){
        ArrayList<Hymn> allHymns = new ArrayList<>();
        for (Hymn hymn: hymns) {
            allHymns.add(hymn);
        }
        return allHymns;
    }

    public static void setHymnList(final Context context, final ArrayList<Hymn> allHymns){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        //sp.edit().putStringSet(PREF_HYMN_LIST, allHymns).apply();

        SharedPreferences.Editor mEdit = sp.edit();
        mEdit.putInt(PREF_HYMN_LIST+"_size_", allHymns.size());

        for (int i= 0; i< allHymns.size(); i++) {
            mEdit.putString(PREF_HYMN_LIST+"_id_"+i, allHymns.get(i).getSongId());
            mEdit.putString(PREF_HYMN_LIST+"_title_"+i, allHymns.get(i).getSongTitle());
            mEdit.putString(PREF_HYMN_LIST+"_engv_"+i, allHymns.get(i).getEnglishVersion());
        }
        mEdit.apply();
    }

    public static ArrayList<Hymn> getHymnList(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        //return sp.getStringSet(PREF_HYMN_LIST, "");
        ArrayList<Hymn> savedHymns = new ArrayList<>();

        int size = sp.getInt(PREF_HYMN_LIST+"_size_", 0);

        for( int i = 0; i < size; i++){
            Hymn hymn = new Hymn( sp.getString(PREF_HYMN_LIST+"_id_"+i, ""),
                            sp.getString(PREF_HYMN_LIST+"_title_"+i, ""),
                            sp.getString(PREF_HYMN_LIST+"_engv_"+i, ""));

            savedHymns.add(hymn);
        }

        return savedHymns;
    }
}
