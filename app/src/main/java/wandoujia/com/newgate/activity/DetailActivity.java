package wandoujia.com.newgate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;

import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;
import com.hannesdorfmann.swipeback.transformer.SlideSwipeBackTransformer;

import wandoujia.com.newgate.R;
import wandoujia.com.newgate.app.AppController;
import wandoujia.com.newgate.model.News;

public class DetailActivity extends ActionBarActivity {
    private WebView webView;

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
                .setContentView(R.layout.news_detail)
                .setSwipeBackView(R.layout.custom_swipe_back);

        Intent intent = getIntent();
        if(intent == null){
            finish();
        }

        final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        webView = (WebView) findViewById(R.id.news_webView);
        webView.getSettings().setJavaScriptEnabled(true);

        News news =(News) intent.getExtras().getSerializable(HomeActivity.EXTRA_NEWS);
        webView.loadUrl(AppController.WEB_URL_NEWS_PREFIX + news.getId());

    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.swipeback_stack_to_front,
                R.anim.swipeback_stack_right_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.destroy();
        webView = null;
    }


}
