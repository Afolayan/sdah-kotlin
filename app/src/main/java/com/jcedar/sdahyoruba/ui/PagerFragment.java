package com.jcedar.sdahyoruba.ui;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.jcedar.sdahyoruba.R;
import com.jcedar.sdahyoruba.provider.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PagerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PagerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PagerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = PagerFragment.class.getSimpleName();

    ViewPager viewPager;

    int hymnSize;
    private SearchView searchView;
    private ViewPagerAdapter pagerAdapter;

    private long mHymnId;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    FragmentActivity fragmentActivity;
    private int position;

    FloatingActionMenu menuRed;
    FloatingActionButton fab1, fab2, fab3;
    private List<FloatingActionMenu> menus = new ArrayList<>();
    FabListener fabListener;

    public static PagerFragment newInstance(long hymnId) {
        PagerFragment fragment = new PagerFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, hymnId);

        fragment.setArguments(args);
        return fragment;
    }

    public PagerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mHymnId = getArguments().getLong(ARG_PARAM1);

            Log.e(TAG, "from fav " + mHymnId);

        }
        /*if( savedInstanceState != null){
            final int lastPosition = savedInstanceState.getInt("POSITION");
            Log.e(TAG, "last position  = " + lastPosition);
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    viewPager.setCurrentItem(lastPosition, true);
                }
            });

        }*/setRetainInstance(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_pager, container, false);
        hymnSize = new DatabaseHelper(getActivity()).getHymnsSize();

        viewPager = (ViewPager) view.findViewById(R.id.viewpager);

        pagerAdapter = new ViewPagerAdapter(fragmentActivity.getSupportFragmentManager(), hymnSize);

        viewPager.setAdapter(pagerAdapter);

        if (getArguments() != null) {
            mHymnId = getArguments().getLong(ARG_PARAM1);

            int id = Integer.parseInt( mHymnId+"");
            viewPager.setCurrentItem(id - 1);


        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        menuRed = (FloatingActionMenu) view.findViewById(R.id.menu_red);

        fab1 = (FloatingActionButton) view.findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) view.findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) view.findViewById(R.id.fab3);

       // fab1.setEnabled(false); //to disable fab
        menuRed.setClosedOnTouchOutside(true); //close when outside layout is touched
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        menus.add(menuRed);
        fab1.setOnClickListener(clickListener);
        fab2.setOnClickListener(clickListener);
        fab3.setOnClickListener(clickListener);

        int delay = 400;
        for (final FloatingActionMenu menu : menus) {
            Handler mUiHandler = new Handler();
            mUiHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    menu.showMenuButton(true);
                }
            }, delay);
            delay += 150;
        }

        menuRed.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menuRed.isOpened()) {
                    //Toast.makeText(getActivity(), menuRed.getMenuButtonLabelText(), Toast.LENGTH_SHORT).show();
                }

                menuRed.toggle(true);
            }
        });
    }


    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fab1: //share
                    fabListener.onHymnSharedFab(viewPager.getCurrentItem()+1);
                    menuRed.close(true);
                    break;
                case R.id.fab2: //favorites
                    /**
                     * insert selected row into favorites table
                     */
                    fabListener.onHymnLikedFab(viewPager.getCurrentItem()+1);
                    menuRed.close(true);
                    break;
                case R.id.fab3: //dialpad
                    /**
                     * display dialpad to go to number
                     */
                    startNumberFragment();
                    menuRed.close(true);
                    break;
            }
        }
    };

    private void startNumberFragment() {
        NumberDialogFragment ndf = new NumberDialogFragment();
        ndf.show(getFragmentManager(), "Number dialog");
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
            fragmentActivity =  (FragmentActivity) activity;
            fabListener =  (FabListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setPosition(int position) {
        this.position = position;
    }
    public int getPosition() {
        return this.position;
    }


    public interface FabListener{
        void onHymnSharedFab(int position);
        void onHymnLikedFab(int position);

    }
    public interface OnFragmentInteractionListener {

         void onFragmentInteraction(Uri uri);
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private final int mSize;

        public ViewPagerAdapter(FragmentManager fm, int size) {
            super(fm);
            mSize = size;
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {

            setPosition(position);
            /**  */
            return HymnsFragment.newInstance(position+1);

        }

        @Override
        public int getCount() {
            return mSize;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "SDAH Yoruba - "+(position + 1);
        }
    }

    /*@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e(TAG, "position in saveInstance == " + getPosition());
        outState.putInt("POSITION", getPosition());


    }*/
}
