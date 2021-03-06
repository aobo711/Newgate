package wandoujia.com.newgate.app;


import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

import wandoujia.com.newgate.R;
import wandoujia.com.newgate.util.LruBitmapCache;

public class AppController extends Application {

    public static String API_PREFIX = "http://play.wandoujia.com/v1/";
    public static String WEB_PREFIX = "http://play.wandoujia.com/";
//    public static String API_PREFIX = "http://100.64.74.5/v1/";
//    public static String WEB_PREFIX = "http://100.64.74.5/";

    public static String API_FEEDBACK = API_PREFIX + "feedback/";
    public static String API_NEWS_PREFIX = API_PREFIX + "news/";
    public static String API_TAG_PREFIX =  API_PREFIX + "tags/";
    public static String WEB_URL_NEWS_PREFIX = WEB_PREFIX + "news/";

    public static String PREF_USER_SELECTED_FILTER = "news_filter";

    private ImageLoader mImageLoader;

    /**
     * Log or request TAG
     */
    public static final String TAG = "VolleyPatterns";

    /**
     * Global request queue for Volley
     */
    private RequestQueue mRequestQueue;

    /**
     * A singleton instance of the application class for easy access in other places
     */
    private static AppController sInstance;


    public synchronized Tracker getTracker() {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        analytics.getLogger()
                .setLogLevel(Logger.LogLevel.VERBOSE);
        Tracker t = analytics.newTracker(R.xml.global_tracker);
        return t;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // initialize the singleton
        sInstance = this;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            int cacheSize = 4 * 1024 * 1024;
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache(cacheSize));
        }
        return this.mImageLoader;
    }
    /**
     * @return ApplicationController singleton instance
     */
    public static synchronized AppController getInstance() {
        return sInstance;
    }

    /**
     * @return The Volley Request queue, the queue will be created if it is null
     */
    public RequestQueue getRequestQueue() {
        // lazy initialize the request queue, the queue instance will be
        // created when it is accessed for the first time
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    /**
     * Adds the specified request to the global queue, if tag is specified
     * then it is used else Default TAG is used.
     *
     * @param req
     * @param tag
     */
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);

        VolleyLog.d("Adding request to queue: %s", req.getUrl());

        getRequestQueue().add(req);
    }

    /**
     * Adds the specified request to the global queue using the Default TAG.
     *
     * @param req
     */
    public <T> void addToRequestQueue(Request<T> req) {
        // set the default tag if tag is empty
        req.setTag(TAG);

        getRequestQueue().add(req);
    }

    /**
     * Cancels all pending requests by the specified TAG, it is important
     * to specify a TAG so that the pending/ongoing requests can be cancelled.
     *
     * @param tag
     */
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}

