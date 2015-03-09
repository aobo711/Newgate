package wandoujia.com.newgate;

import android.content.Context;
import android.content.Intent;

import wandoujia.com.newgate.activity.DetailActivity;
import wandoujia.com.newgate.activity.TagActivity;
import wandoujia.com.newgate.model.News;

/**
 * Created by jintian on 15/2/2.
 */
public class Intents {

    public static final String EXTRA_TAG = "tag";
    public static final String EXTRA_NEWS = "news";
    public static final String EXTRA_ACTION = "phoenix.intent.extra.ACTION";

    public static final String ACTION_WEBVIEW = "phoenix.intent.action.WEBVIEW";

    public static void openNewsTag(Context context, String tag){
        Intent intent = new Intent(context, TagActivity.class);
        intent.putExtra(EXTRA_TAG, tag);
        context.startActivity(intent);
    }

    public static void openNewsDetail(Context context, News news){
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(EXTRA_NEWS, news);
        context.startActivity(intent);
    }
}
