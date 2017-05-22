package com.lychee.soft.ha;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.astuetz.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

import items.Team;
import tools.FontChangeCrawler;
import tools.Helpers;

public class dashboard_activity extends AppCompatActivity {

    private Menu menu;
    private NewsFragment mNewsFragment;
    private List<Team> teams;
    private PlayersFragment mPlayersFragment;
    private MatchesFragment mMatchesFragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private FragmentPagerAdapter adapterViewPager;
    private Context context;
    private ProgressBar bar;
    private ViewPager vpPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String teamEngName = "Hapoel Ashkelon";
        context = this;

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (Helpers.isOnline(context)) {
                        MatchesLoad matchesLoad = new MatchesLoad(context);
                        matchesLoad.syncResults(teamEngName);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }});


        t.start(); // spawn thread

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        setContentView(R.layout.activity_dash_board);
        FontChangeCrawler fontChangeCrawler = new FontChangeCrawler(getAssets(),"fonts/assistantsemibold.ttf");
        fontChangeCrawler.replaceFonts((ViewGroup)findViewById(R.id.DashboardLayout));
        initToolbar();
        fragmentManager = getSupportFragmentManager();
        initPager();
    }

    public void initPager(){
        vpPager = (ViewPager) findViewById(R.id.vppager);
        vpPager.setOffscreenPageLimit(3);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager(),this);
        vpPager.setAdapter(adapterViewPager);
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(vpPager);
        tabs.setBackgroundColor(R.color.white);
        int pixels = (int)(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 17, getResources().getDisplayMetrics()));
        tabs.setTextSize(pixels);
        tabs.setTextColorResource(R.color.offwhite);
        tabs.setIndicatorColorResource(R.color.aqua);
        tabs.setIndicatorHeight(10);
        tabs.setTypeface(FontChangeCrawler.boldFont, Typeface.NORMAL);
    }

    public void initToolbar()
    {
        try {
            Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(myToolbar);
         //   if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
         //     myToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.overflow,null));

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
        ProgressBar pb = (ProgressBar)findViewById(R.id.progressBar);
        if (pb != null)
            pb.setVisibility(View.GONE);

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getFragmentManager().popBackStack();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        this.menu = menu;
        // changing the font of menu items
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/assistantsemibold.ttf");  //  THIS
        SpannableStringBuilder title = new SpannableStringBuilder(getString(R.string.news));
        title.setSpan(face, 0, title.length(), 0);
        menu.add(Menu.NONE, R.id.news_menu, 0, title);

        title = new SpannableStringBuilder(getString(R.string.matches));
        title.setSpan(face, 0, title.length(), 0);
        menu.add(Menu.NONE, R.id.matches_menu, 0, title);

        title = new SpannableStringBuilder(getString(R.string.squad));
        title.setSpan(face, 0, title.length(), 0);
        menu.add(Menu.NONE, R.id.squad_menu, 0, title);

       // getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onCreateOptionsMenu(menu);
        switch (item.getItemId()) {
            case R.id.squad_menu:
                vpPager.setCurrentItem(0,true);
                return true;
            case R.id.news_menu:
                vpPager.setCurrentItem(2,true);
                return true;
            case R.id.matches_menu:
                vpPager.setCurrentItem(1,true);
                return true;
            default:
                return false;
        }

    }
    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 3;
        public List<Fragment> mFragments;
        private Context mContext;

        public MyPagerAdapter(FragmentManager fragmentManager,Context context) {
            super(fragmentManager);
            mContext = context;
            mFragments = new ArrayList<Fragment>();
            mFragments.add(new PlayersFragment());
            mFragments.add(new MatchesFragment());
            mFragments.add(new RootNewsFragment());
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (position >= getCount()) {
                FragmentManager manager = ((Fragment) object).getFragmentManager();
                FragmentTransaction trans = manager.beginTransaction();
                trans.remove((Fragment) object);
                trans.commit();
            }

        }
/*
        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }
*/
        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            if (position < mFragments.size()) {
                Log.d("AdapterGetItem","returning fragment " + mFragments.get(position).toString());
                return mFragments.get(position);
            }
            else
                return mFragments.get(position);
            //    return null;

        }


        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return mContext.getResources().getString(R.string.squad);
                case 1:
                    return mContext.getResources().getString(R.string.matches);
                case 2:
                    return mContext.getResources().getString(R.string.news);
                default:
                    return "";
            }
        }
    }
}

