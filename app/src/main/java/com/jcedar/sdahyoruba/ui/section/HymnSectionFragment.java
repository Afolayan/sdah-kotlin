package com.jcedar.sdahyoruba.ui.section;


import android.app.Activity;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jcedar.sdahyoruba.R;
import com.jcedar.sdahyoruba.adapter.RecyclerCursorAdapter;
import com.jcedar.sdahyoruba.helper.FontChangeCrawler;
import com.jcedar.sdahyoruba.provider.DataContract;

public class HymnSectionFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>{


    private static final int NORMAL_LOADER_ID = 1;
    public static final String ARG_LIMIT_1 = "LIMIT1";
    public static final String ARG_LIMIT_2 = "LIMIT2";
    private View rootView;
    private String context, limit1, limit2;
    private RecyclerView recyclerView;
    private RecyclerCursorAdapter resultsCursorAdapter;
    private TextView tvError;
    private Listener mCallback;

    public HymnSectionFragment() {
        // Required empty public constructor
    }

    /*public static HymnSectionFragment newInstance() {
        HymnSection
        Fragment fragment = new HymnSectionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }*/


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);

        FontChangeCrawler fontChanger =
                new FontChangeCrawler(getActivity().getAssets(), "fonts/proxima-nova-regular.ttf");
        fontChanger.replaceFonts((ViewGroup) this.getView());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            limit1 = getArguments().getString(ARG_LIMIT_1, "0");
            limit2 = getArguments().getString(ARG_LIMIT_2, "100");
            Log.e("HymnSectionFrag1", "limit1 == "+limit1 +" \nlimit2=="+limit2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /*if( rootView != null ){
            if(  rootView.getParent() != null ){
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        ((ViewGroup) rootView.getParent()).removeView(rootView);
                    }
                });

            }
            return rootView;
        }*/
        rootView = inflater.inflate(R.layout.fragment_favorites, container, false);
        tvError = (TextView) rootView.findViewById(R.id.tvErrorMag);

        recyclerView = (RecyclerView) rootView.findViewById( R.id.recyclerview );
        resultsCursorAdapter = new RecyclerCursorAdapter( getActivity(), HymnSectionFragment.this );

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);

        recyclerView.setAdapter(resultsCursorAdapter);

        resultsCursorAdapter.setOnItemClickListener(new RecyclerCursorAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(Cursor data) {

                long Id = data.getLong(
                        data.getColumnIndex(DataContract.Hymns.SONG_ID));

                 mCallback.onHymnSelected(Id);


            }
        });

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = DataContract.Hymns.CONTENT_URI;

        String[] projection = DataContract.Hymns.PROJECTION;
        //Log.e("HymnSectionFrag", "oncreateloader limit1 == "+limit1 +" \nlimit2=="+limit2);

        //String sortOrder = DataContract.Hymns._ID + " ASC LIMIT 0,100"; //1-100
        //String sortOrder = DataContract.Hymns._ID + " ASC LIMIT 100,100"; //101-200
        //String sortOrder = DataContract.Hymns._ID + " ASC LIMIT 500, 121";
        String sortOrder = DataContract.Hymns._ID + " ASC LIMIT 0,100";

        return new CursorLoader(
                getActivity(),
                uri,
                projection,
                null,    // selection
                null,   // arguments
                sortOrder );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        resultsCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    public interface Listener {
        void onHymnSelected(long hymnId);
        void onFragmentDetached(Fragment fragment);
        void onFragmentAttached(Fragment fragment);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof Listener) {
            mCallback = (Listener) activity;
            mCallback.onFragmentAttached(this);
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement fragments listener");
        }
        activity.getContentResolver().registerContentObserver(
              DataContract.Hymns.CONTENT_URI, true, mObserver);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (getActivity() instanceof Listener) {
            ((Listener) getActivity()).onFragmentDetached(this);
        }
        getActivity().getContentResolver().unregisterContentObserver(mObserver);
    }
    private final ContentObserver mObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            if (!isAdded()) {
                return;
            }
            getLoaderManager().restartLoader(NORMAL_LOADER_ID, null, HymnSectionFragment.this);
        }
    };
}