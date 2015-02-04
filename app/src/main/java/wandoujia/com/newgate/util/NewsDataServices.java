package wandoujia.com.newgate.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;

import wandoujia.com.newgate.R;
import wandoujia.com.newgate.adapter.CustomNewsListAdapter;
import wandoujia.com.newgate.app.AppController;
import wandoujia.com.newgate.model.News;
import wandoujia.com.newgate.model.Tag;

/**
 * Created by jintian on 15/1/17.
 */
public class NewsDataServices {

    public static final String REGRESH_FLAG_PULLDOWN = "pullDown";
    public static final String REGRESH_FLAG_SCROLL = "scroll";
    public static final String REGRESH_FLAG_INIT = "init";

    SharedPreferences sp;
    private static final String TAG = NewsDataServices.class.getSimpleName();
    private LinkedList<News> newsList;
    private SwipeRefreshLayout mRefreshLayout;
    private CustomNewsListAdapter adapter;
    private Context context;

    public NewsDataServices(){}

    public NewsDataServices(Context context, LinkedList<News> newsLis, SwipeRefreshLayout mRefreshLayout, CustomNewsListAdapter adapter){
        this.newsList = newsLis;
        this.mRefreshLayout = mRefreshLayout;
        this.adapter = adapter;
        this.context = context;
        this.sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public NewsDataServices(Context context, LinkedList<News> newsLis, CustomNewsListAdapter adapter){
        this.newsList = newsLis;
        this.adapter = adapter;
        this.context = context;
        this.sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Fetch news from server side.
     * @param refreshFlag has three options. "pullDown" for checking updates when the list has been pulled down
     *                    "init" for first time rendering;
     *                    and others for loading more news when pulling up.
     */
    public void requestHomeNews(final String refreshFlag){
        if(newsList == null){
            return;
        }
        handleRequest(refreshFlag, AppController.API_NEWS_PREFIX);
    }

    private void handleRequest(final String refreshFlag, String apiPrefix){
        String url;
        switch (refreshFlag){
            case REGRESH_FLAG_PULLDOWN:
                url = String.format("%s?start=0&max=10&refresh=1", apiPrefix);
                break;
            case REGRESH_FLAG_INIT:
                url = String.format("%s?start=0&max=10", apiPrefix);
                break;
            case REGRESH_FLAG_SCROLL:
                url = String.format("%s?start=%s&max=10", apiPrefix, newsList.size());
                break;
            default:
                url = String.format("%s?start=%s&max=10", apiPrefix, newsList.size());
                break;
        }

        url = appendNewsFilter(url);

        JsonArrayRequest newsReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    int currentListSize = newsList.size();

                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                News news = new News();
                                news.setId(obj.getInt("id"));
                                news.setTitle(obj.getString("title"));
                                news.setDate(obj.getString("created_at"));
                                news.setFirstVideo(obj.getString("first_video"));
                                news.setContent(obj.getString("content"));
                                news.setThumbnail(obj.getString("thumbnail"));

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
                        if(newsList.size() == currentListSize && refreshFlag.equals(REGRESH_FLAG_PULLDOWN)){
                            Toast.makeText(context, R.string.no_newer_news, Toast.LENGTH_SHORT).show();
                            return;
                        }else if(newsList.size() == currentListSize + 10){
                            newsList.subList(0, 10);
                            adapter.notifyDataSetChanged();
                        }else{
                            adapter.notifyDataSetChanged();
                        }

                        // no need to call refresh callback
                        if(refreshFlag != REGRESH_FLAG_INIT && mRefreshLayout != null){
                            mRefreshLayout.onFinishTemporaryDetach();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        AppController.getInstance().addToRequestQueue(newsReq);
    }

    public void requestTagNews(String tag, final String refreshFlag){
        try {
            tag = java.net.URLEncoder.encode(tag, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        handleRequest(refreshFlag, AppController.API_TAG_PREFIX + tag);
    }

    private String appendNewsFilter(String url){
        String filter = sp.getString(AppController.PREF_USER_SELECTED_FILTER, "");
        return url.indexOf("?") > 0 ? String.format("%s&filter=%s", url, filter)
                :String.format("%s?filter=%s", url, filter);
    }
}
