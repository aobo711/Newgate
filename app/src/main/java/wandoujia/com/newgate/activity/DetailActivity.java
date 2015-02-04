package wandoujia.com.newgate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;
import com.hannesdorfmann.swipeback.transformer.SlideSwipeBackTransformer;

import wandoujia.com.newgate.R;
import wandoujia.com.newgate.app.AppController;
import wandoujia.com.newgate.model.News;
import wandoujia.com.newgate.util.WebViewPlugin;

public class DetailActivity extends ActionBarActivity {
    private WebView webView;
    private static final String PLUGIN_WEBVIEW = "webview";
    private ActionBar mActionbar;
    private News mNews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SwipeBack.attach(this, Position.LEFT)
                .setDrawOverlay(true)
                .setSwipeBackTransformer(new SlideSwipeBackTransformer(){
                    @Override
                    public void onSwiping(SwipeBack swipeBack, float openRatio, int pixelOffset) {
                        super.onSwiping(swipeBack, openRatio, pixelOffset);
                    }

                })
                .setContentView(R.layout.activity_news_detail)
                .setSwipeBackView(R.layout.custom_swipe_back);

        Intent intent = getIntent();
        if(intent == null){
            finish();
        }

        final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mActionbar = getSupportActionBar();
        mActionbar.setDisplayHomeAsUpEnabled(true);
        mActionbar.setDisplayShowTitleEnabled(false);

        webView = (WebView) findViewById(R.id.news_webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new myWebViewClient());
        String userAgent = webView.getSettings().getUserAgentString();
        webView.getSettings().setUserAgentString(userAgent.replace("Mobile Safari", "NewGate App"));

        WebViewPlugin webViewPlugin = new WebViewPlugin(this, webView);
        webView.addJavascriptInterface(webViewPlugin, PLUGIN_WEBVIEW);

        mNews =(News) intent.getExtras().getSerializable(HomeActivity.EXTRA_NEWS);
        webView.loadUrl(AppController.WEB_URL_NEWS_PREFIX + mNews.getId());

    }

    /**
     * rewrite webview behaviors
     */
    private class myWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }


    }

    public void updateActionBar() {
        if (webView == null) {
            return;
        }
        mActionbar.setTitle(webView.getTitle());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_item_share:
                openShareIntent();
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    private void openShareIntent(){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = AppController.WEB_URL_NEWS_PREFIX + Integer.toString(mNews.getId());
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mNews.getTitle());
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_to)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.destroy();
        webView = null;
    }


}
