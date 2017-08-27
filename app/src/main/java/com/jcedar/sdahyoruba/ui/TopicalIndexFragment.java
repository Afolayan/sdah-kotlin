package com.jcedar.sdahyoruba.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jcedar.sdahyoruba.R;
import com.jcedar.sdahyoruba.adapter.ViewPagerAdapter;
import com.jcedar.sdahyoruba.app.SdahYoruba;
import com.jcedar.sdahyoruba.helper.FontChangeCrawler;
import com.jcedar.sdahyoruba.ui.section.HymnSectionFragment;
import com.jcedar.sdahyoruba.ui.section.HymnSectionFragment1;
import com.jcedar.sdahyoruba.ui.section.HymnSectionFragment2;
import com.jcedar.sdahyoruba.ui.section.HymnSectionFragment3;
import com.jcedar.sdahyoruba.ui.section.HymnSectionFragment4;
import com.jcedar.sdahyoruba.ui.section.HymnSectionFragment5;
import com.jcedar.sdahyoruba.ui.section.HymnSectionFragment6;


public class TopicalIndexFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_LIMIT_1 = HymnSectionFragment.ARG_LIMIT_1;
    private static final String ARG_LIMIT_2 = HymnSectionFragment.ARG_LIMIT_2;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private OnFragmentInteractionListener mListener;

    private Toolbar toolbar;

    public TopicalIndexFragment() {
        // Required empty public constructor
    }


    public static TopicalIndexFragment newInstance(String param1, String param2) {
        TopicalIndexFragment fragment = new TopicalIndexFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
        setHasOptionsMenu(false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FontChangeCrawler fontChanger =
                new FontChangeCrawler(getActivity().getAssets(),
                        "fonts/proxima-nova-regular.ttf");
        fontChanger.replaceFonts((ViewGroup) this.getView());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_hymns, container, false);

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        viewPager = (ViewPager) view.findViewById(R.id.pager);

        ViewPagerAdapter adapter = new ViewPagerAdapter
                (getActivity().getSupportFragmentManager(), getActivity());
        SdahYoruba.getInstance().trackScreenView("Topical Index page");
        setUpViewPager(adapter);

        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setTabTextColors(ContextCompat.getColorStateList(getActivity(), R.color.tab_selector));
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getActivity(), R.color.indicator));
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                getActivity().setTitle("Topical Index");
            }
        });
        return view;
    }

    public void setUpViewPager(ViewPagerAdapter adapter){
        adapter.addFragment(newFragment(0, 100), "1-100");
        //adapter.addFragment(newFragment(100, 100), "101-200");
        adapter.addFragment(new HymnSectionFragment1(), "101-200");
        adapter.addFragment(new HymnSectionFragment2(), "201-300");
        adapter.addFragment(new HymnSectionFragment3(), "301-400");
        adapter.addFragment(new HymnSectionFragment4(), "401-500");
        adapter.addFragment(new HymnSectionFragment5(), "501-600");
        adapter.addFragment(new HymnSectionFragment6(), "601-621");

        viewPager.setAdapter(adapter);

    }

    private HymnSectionFragment newFragment(int start, int limit){
        HymnSectionFragment a = new HymnSectionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LIMIT_1, start);
        args.putInt(ARG_LIMIT_2, limit);
        a.setArguments(args);

        return a;
    }
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            SdahYoruba.getInstance().trackException(new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener"));
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

}
