package org.coursera.mutibo;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.coursera.mutibo.data.MutiboUserResult;

import java.util.ArrayList;
import java.util.Collection;


public class LeaderboardActivity extends FragmentActivity
{
    public static final String PLAYER_ARG = "player";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        mLeaderboardPagerAdapter = new LeaderboardPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mLeaderboardPagerAdapter);

        mViewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position)
                    {
                        // When swiping between pages, select the corresponding tab.
                        getActionBar().setSelectedNavigationItem(position);
                    }
                });


        // action bar
       final ActionBar actionBar = getActionBar();

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ActionBar.TabListener tabListener = new ActionBar.TabListener()
        {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft)
            {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft)
            {
            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft)
            {
            }
        };

        actionBar.addTab(actionBar.newTab()
                            .setText(R.string.leaderboard_tab_top)
                            .setTabListener(tabListener)
        );

        actionBar.addTab(actionBar.newTab()
                        .setText(R.string.leaderboard_tab_you)
                        .setTabListener(tabListener)
        );

        // check for parameters
        Bundle bundle = getIntent().getExtras();

        if (bundle != null && bundle.containsKey(PLAYER_ARG))
        {
            mViewPager.setCurrentItem(1);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Pager
    //

    public class LeaderboardPagerAdapter extends FragmentPagerAdapter
    {
        public LeaderboardPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int i)
        {
            Fragment fragment = new LeaderboardFragment();

            Bundle args = new Bundle();
            args.putInt(LeaderboardFragment.LEADERBOARD_ARG_TYPE, i + 1);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount()
        {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            if (position == 0)
                return getString(R.string.leaderboard_tab_top);
            else
                return getString(R.string.leaderboard_tab_you);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // ListFragment
    //


    // member variables
    LeaderboardPagerAdapter     mLeaderboardPagerAdapter;
    ViewPager                   mViewPager;


}
