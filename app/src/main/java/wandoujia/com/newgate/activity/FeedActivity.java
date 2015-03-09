package wandoujia.com.newgate.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import com.astuetz.PagerSlidingTabStrip;

import wandoujia.com.newgate.R;
import wandoujia.com.newgate.fragment.CradListFragment;
import wandoujia.com.newgate.fragment.NavigationDrawerFragment;

public class FeedActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private DrawerLayout mDrawerLayout;
    private NavigationDrawerFragment mDrawerListview;

    private MyPagerAdapter pagerAdapter;
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;

    private Toolbar mToolbar;

    public Toolbar getToolbar(){
        return mToolbar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);

        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(pagerAdapter);

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(pager);

        // drawer layout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerListview = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.drawer_listview);
        // render left drawer UI
        mDrawerListview.setUp(R.id.left_drawer, mDrawerLayout, mToolbar);

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {}

    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {"推荐", "视频", "阵型", "新手", "部落战" };

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            return CradListFragment.newInstance(position);
        }
    }
}
