package wandoujia.com.newgate.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.LinkedList;

import wandoujia.com.newgate.R;
import wandoujia.com.newgate.adapter.CustomNewsListAdapter;
import wandoujia.com.newgate.app.AppController;
import wandoujia.com.newgate.fragment.NavigationDrawerFragment;
import wandoujia.com.newgate.model.News;
import wandoujia.com.newgate.util.EndlessScrollListener;
import wandoujia.com.newgate.util.NewsDataServices;

public class HomeActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener,NavigationDrawerFragment.NavigationDrawerCallbacks {

    public static final String EXTRA_NEWS = "news";
    private String currentNewFilter;

    SharedPreferences sp;

    private LinkedList<News> newsList = new LinkedList<News>();
    private ListView mListView;
    private Menu mMenu;
    private CustomNewsListAdapter mAdapter;
    private NewsDataServices newsDataServices;
    private DrawerLayout mDrawerLayout;
    private SwipeRefreshLayout mRefreshLayout;
    private NavigationDrawerFragment mDrawerListview;

    private EndlessScrollListener mEndlessScrollListener = new EndlessScrollListener() {
        @Override
        public void onLoadMore(int page, int totalItemsCount) {
            newsDataServices.requestHomeNews(NewsDataServices.REGRESH_FLAG_SCROLL);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);

        // get filter user selected
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        String defaultFilterId = getResources().getResourceName(R.id.th_level_0);
        defaultFilterId = defaultFilterId.split("/")[1];
        currentNewFilter = sp.getString(AppController.PREF_USER_SELECTED_FILTER, defaultFilterId);

        // pull down to refresh
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mRefreshLayout.setOnRefreshListener(this);
        setLoadingAppearance();

        // 1. fetching data from api
        // 2. binding on click event listener for list item
        mListView = (ListView) findViewById(R.id.home_news_listview);
        mAdapter = new CustomNewsListAdapter(this, R.layout.list_row, newsList);
        mListView.setAdapter(mAdapter);

        // endless scrolling for loading more data
        mListView.setOnScrollListener(mEndlessScrollListener);

        // initialization list view
        newsDataServices = new NewsDataServices(this, newsList, mRefreshLayout, mAdapter);
        newsDataServices.requestHomeNews(NewsDataServices.REGRESH_FLAG_INIT);

        // drawer layout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerListview = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.drawer_listview);
        // render left drawer UI
        mDrawerListview.setUp(R.id.left_drawer, mDrawerLayout, mToolbar);

    }


    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
            mRefreshLayout.setRefreshing(false);
            newsDataServices.requestHomeNews(NewsDataServices.REGRESH_FLAG_PULLDOWN);
            }
        }, 1000);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String itemResourceName = getResources().getResourceName(item.getItemId());
        if(!TextUtils.equals(item.getTitle(), getResources().getString(R.string.action_filter)) &&
                !TextUtils.equals(currentNewFilter, itemResourceName.split("/")[1])){
            item.setChecked(true);
            currentNewFilter = itemResourceName.split("/")[1];
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(AppController.PREF_USER_SELECTED_FILTER, currentNewFilter);
            editor.commit();
            resetFilterMenuState(mMenu);
            newsList.clear();
            mAdapter.notifyDataSetChanged();
            newsDataServices.requestHomeNews(NewsDataServices.REGRESH_FLAG_INIT);
        }

        return super.onOptionsItemSelected(item);
    }

    private void resetFilterMenuState(Menu menu){
        MenuItem filterMenu = (MenuItem) menu.findItem(R.id.drawer_indicator);
        if(!TextUtils.equals(currentNewFilter, "th_level_0")){
            filterMenu.setIcon(getResources().getDrawable(R.drawable.ic_action_filter_selected));
        }else {
            filterMenu.setIcon(getResources().getDrawable(R.drawable.ic_action_filter));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.mMenu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        resetFilterMenuState(menu);

        MenuItem menuItem = (MenuItem) menu.findItem(getResources().getIdentifier(currentNewFilter, "id", this.getPackageName()));
        menuItem.setChecked(true);

        return true;
    }
    private void setLoadingAppearance() {
        mRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {}
}
