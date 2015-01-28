package wandoujia.com.newgate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.LinkedList;

import wandoujia.com.newgate.R;
import wandoujia.com.newgate.adapter.CustomNewsListAdapter;
import wandoujia.com.newgate.fragment.NavigationDrawerFragment;
import wandoujia.com.newgate.model.News;
import wandoujia.com.newgate.util.EndlessScrollListener;
import wandoujia.com.newgate.util.HomeDataServices;

public class HomeActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener,NavigationDrawerFragment.NavigationDrawerCallbacks {

    public static final String EXTRA_NEWS = "news";

    private LinkedList<News> newsList = new LinkedList<News>();
    private ListView mListView;
    private CustomNewsListAdapter mAdapter;
    private HomeDataServices homeDataServices;
    private DrawerLayout mDrawerLayout;
    private SwipeRefreshLayout mRefreshLayout;
    private NavigationDrawerFragment mDrawerListview;

    private EndlessScrollListener mEndlessScrollListener = new EndlessScrollListener() {
        @Override
        public void onLoadMore(int page, int totalItemsCount) {
            homeDataServices.requestNews(HomeDataServices.REGRESH_FLAG_SCROLL);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // pull down to refresh
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mRefreshLayout.setOnRefreshListener(this);
        setLoadingAppearance();

        // 1. fetching data from api
        // 2. binding on click event listener for list item
        mListView = (ListView) findViewById(R.id.pull_to_refresh_listview);
        mAdapter = new CustomNewsListAdapter(this, R.layout.list_row, newsList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            openNewsDetail(newsList.get(position));
            }

        });

        // endless scrolling for loading more data
        mListView.setOnScrollListener(mEndlessScrollListener);

        // initialization list view
        homeDataServices = new HomeDataServices(this, newsList, mRefreshLayout, mAdapter);
        homeDataServices.requestNews(HomeDataServices.REGRESH_FLAG_INIT);

        // drawer layout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerListview = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.drawer_listview);
        // render left drawer UI
        mDrawerListview.setUp(R.id.left_drawer, mDrawerLayout, mToolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void openNewsDetail(News news){
        Intent intent = new Intent(HomeActivity.this, DetailActivity.class);
        intent.putExtra(EXTRA_NEWS, news);
        startActivity(intent);
        Log.d(HomeDataServices.class.getSimpleName(), "onCreate() Restoring previous state");
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                mRefreshLayout.setRefreshing(false);
                homeDataServices.requestNews(HomeDataServices.REGRESH_FLAG_PULLDOWN);
            }
        }, 1000);
    }

    private void setLoadingAppearance() {
        mRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

    }
}
