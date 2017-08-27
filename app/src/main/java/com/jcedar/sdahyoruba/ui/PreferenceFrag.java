package com.jcedar.sdahyoruba.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.util.Log;

import com.jcedar.sdahyoruba.R;

public class PreferenceFrag extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        settings.registerOnSharedPreferenceChangeListener(mPrefChangeListener);

        PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        settings.unregisterOnSharedPreferenceChangeListener(mPrefChangeListener);

        PreferenceManager.getDefaultSharedPreferences(getActivity()).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);


        //changeFontSize();
    }

    private SharedPreferences.OnSharedPreferenceChangeListener mPrefChangeListener
            = new SharedPreferences.OnSharedPreferenceChangeListener() {

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());


            onNotificationChanged(key);
            //updatePrefSummary(key);
        }
    };

    private void onNotificationChanged(String key) {
        if(key.equals("notification")){
            SwitchPreference switchPreference = (SwitchPreference) findPreference(key);
            if(null == switchPreference)  return;


        }
    }


    private void updatePrefSummary(Preference pref) {
        if ( pref instanceof ListPreference){
            ListPreference listPref = (ListPreference) pref;
            pref.setSummary(listPref.getEntry());
            Log.e("Preference Tag", listPref.getEntry().toString());
        }
    }
    private void updatePrefSummary(String key) {
        Preference preference = findPreference(key);
        if(key.equals("fonts")){
            preference.setSummary(((ListPreference)preference).getEntry());
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updatePrefSummary(key);
    }
}