package com.jcedar.sdahyoruba.helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AppSettings {
    public static final String PROVIDER_AUTHORITY = "com.jcedar.sdahyoruba.provider";
    public static DateFormat spriteDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
    public static DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
    public static DateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.ENGLISH);
    public static DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
    public static DateFormat serverDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);

    public static final String SERVER_URL = "http://www.mobile.jcedar.com.ng/sdahyoruba/";
    public static final String SERVER_IMAGE_URL = SERVER_URL+"uploads/";

    public static String getDateOneMonthAgo(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        Date oneMonthAgo = calendar.getTime();
        return  AppSettings.serverDateFormat.format(oneMonthAgo);
    }


//    public static final String GCM_SERVER_URL = "";
    public static final String GCM_SENDER_ID = "1048218450634"; //Gotten when the google api is created on the server
    public static final String API_KEY = "AIzaSyAXrashiObJD6OgNT7g1PZSa8F68UaMblk";


}

