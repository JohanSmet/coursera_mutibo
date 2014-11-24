package org.coursera.mutibo;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebViewFragment;
import android.widget.TextView;


public class AboutActivity extends FragmentActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        mAboutPagerAdapter = new AboutPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAboutPagerAdapter);

        mViewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position)
                    {
                        if (getActionBar() != null)
                            getActionBar().setSelectedNavigationItem(position);
                    }
                });


        // action bar
        final ActionBar actionBar = getActionBar();

        if (actionBar != null)
        {
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
                            .setText(R.string.about_tab_howto)
                            .setTabListener(tabListener)
            );

            actionBar.addTab(actionBar.newTab()
                            .setText(R.string.about_tab_support)
                            .setTabListener(tabListener)
            );

            actionBar.addTab(actionBar.newTab()
                            .setText(R.string.about_tab_credits)
                            .setTabListener(tabListener)
            );
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Pager
    //

    public class AboutPagerAdapter extends FragmentPagerAdapter
    {
        public AboutPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int i)
        {
            Fragment fragment = null;

            switch (i)
            {
                case 0 :
                    fragment = TextViewFragment.newInstance(R.string.about_contents_howto);
                    break;

                case 1 :
                    fragment = SupportFragment.newInstance();
                    break;

                case 2 :
                    fragment = TextViewFragment.newInstance(R.string.about_contents_credits);
                    break;
            }

            return fragment;
        }

        @Override
        public int getCount()
        {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            switch (position)
            {
                case 0:
                    return getString(R.string.about_tab_howto);
                case 1:
                    return getString(R.string.about_tab_support);
                default:
                    return getString(R.string.about_tab_credits);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // fragments
    //

    public static class TextViewFragment extends Fragment
    {
        private static String ARG_TEXT_RESOURCE = "text";

        public static TextViewFragment newInstance(int textResource)
        {
            TextViewFragment fragment = new TextViewFragment();

            Bundle args = new Bundle();
            args.putInt(ARG_TEXT_RESOURCE, textResource);
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View view = inflater.inflate(R.layout.fragment_about_textview, container, false);

            if (getArguments() != null)
                ((TextView) view.findViewById(R.id.txtAboutText)).setText(getArguments().getInt(ARG_TEXT_RESOURCE));

            return view;
        }
    }

    public static class SupportFragment extends Fragment
    {
        public static SupportFragment newInstance()
        {
            SupportFragment fragment = new SupportFragment();
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View view = inflater.inflate(R.layout.fragment_about_support, container, false);

            view.findViewById(R.id.btnRemoveAccount).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                }
            });

            return view;
        }
    }

    // member variables
    AboutPagerAdapter   mAboutPagerAdapter;
    ViewPager           mViewPager;
}
