package wandoujia.com.newgate.util;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import wandoujia.com.newgate.Intents;
import wandoujia.com.newgate.activity.TagActivity;


public class WebViewPlugin {
    private final Activity activity;
    private final WebView webView;

    private final String EXTRA_TAG = "tag";

    public WebViewPlugin(Activity activity, WebView webView) {
        this.activity = activity;
        this.webView = webView;
    }

    /**
     *
     * @param tag tag list to open
     */
    @JavascriptInterface
    public void openNewsListByTag(String tag) {
        if(TextUtils.isEmpty(tag)){
            return;
        }
        Intent intent = new Intent(activity, TagActivity.class);
        intent.putExtra(Intents.EXTRA_TAG, tag);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intents.EXTRA_ACTION, Intents.ACTION_WEBVIEW);

        activity.startActivity(intent);
    }
}