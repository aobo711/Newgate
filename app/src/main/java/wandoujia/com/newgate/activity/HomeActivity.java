package wandoujia.com.newgate.activity;

import android.app.ActionBar;
import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;

import wandoujia.com.newgate.R;
import wandoujia.com.newgate.adapter.CustomNewsListAdapter;
import wandoujia.com.newgate.model.News;
import wandoujia.com.newgate.model.Tag;
import wandoujia.com.newgate.util.EndlessScrollListener;

public class HomeActivity extends ListActivity {
    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final String REGRESH_FLAG_PULLDOWN = "pullDown";
    private static final String REGRESH_FLAG_SCROLL = "scroll";
    private static final String REGRESH_FLAG_INIT = "init";

    private static String url = "http://100.64.82.6/v1/news/";

    private LinkedList<News> newsList = new LinkedList<News>();
    private ListView listView;
    private CustomNewsListAdapter adapter;
    private PullToRefreshListView pullToRefreshView;
    private ActionBar actionBar;
    private int actionBarHeight;
    RelativeLayout ptrWrapper;

    private OnRefreshListener2 onRefreshListener2 = new OnRefreshListener2<ListView>() {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase<ListView> listViewPullToRefreshBase) {
            new GetDataTask().execute("pullDown");
        }
        @Override
        public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        actionBar = getActionBar();
        actionBarHeight = actionBar.getHeight();
        ptrWrapper =  (RelativeLayout) findViewById(R.id.ptr_wrapper);

        // set something up for pull to refresh
        pullToRefreshView = (PullToRefreshListView) findViewById(R.id.pull_to_refresh_listview);
        pullToRefreshView.setOnRefreshListener(onRefreshListener2);
        pullToRefreshView.setPullToRefreshOverScrollEnabled(false);
        pullToRefreshView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

        adapter = new CustomNewsListAdapter(this, newsList);

        // bind endless scroll listener
        listView = pullToRefreshView.getRefreshableView();
        listView.setOnScrollListener(new EndlessScrollListener() {

            private int mLastFirstVisibleItem;

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);

                if (mLastFirstVisibleItem < firstVisibleItem) {
                    if (actionBar.isShowing()) {
                        actionBar.hide();
                        ptrWrapper.setPadding(0, 0, 0, 0);
                    }
                }

                if (mLastFirstVisibleItem > firstVisibleItem) {
                    if (!actionBar.isShowing()) {
                        actionBar.show();
                        ptrWrapper.setPadding(0, 0, 0, 0);
                    }
                }
                mLastFirstVisibleItem = firstVisibleItem;
            }

            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                new GetDataTask().execute("scroll");
            }
        });

        pullToRefreshView.setAdapter(adapter);

        // first time fetching news
        requestData(REGRESH_FLAG_INIT);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Fetch news from server side.
     * @param refreshFlag has three options. "pullDown" for checking updates when the list has been pulled down
     *                    "init" for first time rendering;
     *                    and others for loading more news when pulling up.
     */
    private void requestData(final String refreshFlag){
        String listingUrl;
        switch (refreshFlag){
            case REGRESH_FLAG_PULLDOWN:
                listingUrl = String.format("%s?start=0&max=10&refresh=1", url);
            case REGRESH_FLAG_INIT:
                listingUrl = String.format("%s?start=0&max=10", url);
                break;
            case REGRESH_FLAG_SCROLL:
                listingUrl = String.format("%s?start=%s&max=10", url, newsList.size());
                break;
            default:
                listingUrl = String.format("%s?start=%s&max=10", url, newsList.size());
                break;
        }
        JsonArrayRequest newsReq = new JsonArrayRequest(listingUrl,
            new Response.Listener<JSONArray>() {

                int currentListSize = newsList.size();

                @Override
                public void onResponse(JSONArray response) {
                Log.d(TAG, response.toString());
                for (int i = 0; i < response.length(); i++) {
                    try {

                        JSONObject obj = response.getJSONObject(i);
                        News news = new News();
                        news.setId(obj.getInt("id"));
                        news.setTitle(obj.getString("title"));
                        news.setDate(obj.getString("created_at"));
                        news.setFirstVideo(obj.getString("first_video"));

                        JSONArray tagsArry = obj.getJSONArray("tags");
                        ArrayList<Tag> tags = new ArrayList<Tag>();
                        for (int j = 0; j < tagsArry.length(); j++) {
                            Tag tag =  new Tag();
                            JSONObject jOjb = (JSONObject) tagsArry.get(j);
                            tag.setId(jOjb.getInt("id"));
                            tag.setName(jOjb.getString("name"));
                            tag.setColor(jOjb.getString("color"));

                            tags.add(tag);
                        }
                        news.setTags(tags);
                        if (!refreshFlag.equals(REGRESH_FLAG_PULLDOWN)){
                            newsList.add(news);
                        }else{
                            if(!newsList.contains(news)){
                                newsList.addFirst(news);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                if(newsList.size() == currentListSize + 10){
                    newsList.subList(0, 10);
                }
                adapter.notifyDataSetChanged();

                // no need to call refresh callback
                if(refreshFlag != REGRESH_FLAG_INIT){
                    pullToRefreshView.onRefreshComplete();
                }
                }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(newsReq);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private class GetDataTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return new String(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            requestData(result);
            super.onPostExecute(result);
        }
    }
}
