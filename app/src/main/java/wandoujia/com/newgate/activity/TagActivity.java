package wandoujia.com.newgate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.LinkedList;

import wandoujia.com.newgate.R;
import wandoujia.com.newgate.adapter.CustomNewsListAdapter;
import wandoujia.com.newgate.model.News;
import wandoujia.com.newgate.listener.EndlessScrollListener;
import wandoujia.com.newgate.util.NewsDataServices;

public class TagActivity extends ActionBarActivity {
    private String EXTRA_TAG = "tag";
    private ActionBar mActionbar;
    private ListView mListView;
    private String tag;
    private LinkedList<News> newsList = new LinkedList<News>();
    private CustomNewsListAdapter mAdapter;
    private NewsDataServices newsDataServices;

    private EndlessScrollListener mEndlessScrollListener = new EndlessScrollListener() {
        @Override
        public void onLoadMore(int page, int totalItemsCount) {
            newsDataServices.requestTagNews(tag, NewsDataServices.REGRESH_FLAG_SCROLL);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tag);

        Intent intent = getIntent();
        if(intent == null){
            finish();
        }

        final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mActionbar = getSupportActionBar();
        mActionbar.setDisplayHomeAsUpEnabled(true);

        tag = intent.getExtras().getString(EXTRA_TAG);
        mActionbar.setTitle("#" + tag);

        mListView = (ListView) findViewById(R.id.tag_listview);
        mAdapter = new CustomNewsListAdapter(this, R.layout.list_row, newsList);
        mListView.setAdapter(mAdapter);

        mListView.setOnScrollListener(mEndlessScrollListener);

        // initialization list view
        newsDataServices = new NewsDataServices(this, newsList , mAdapter);
        newsDataServices.requestTagNews(tag, NewsDataServices.REGRESH_FLAG_INIT);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return (super.onOptionsItemSelected(menuItem));
    }

}
