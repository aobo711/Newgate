package wandoujia.com.newgate.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import wandoujia.com.newgate.R;
import wandoujia.com.newgate.activity.DetailActivity;
import wandoujia.com.newgate.activity.TagActivity;
import wandoujia.com.newgate.model.News;
import wandoujia.com.newgate.model.Tag;
import wandoujia.com.newgate.util.NewsDataServices;

public class CustomNewsListAdapter extends BaseAdapter {
    private Context mContext;
    private int layoutResourceId;
    private List<News> newsItems;
    public static final String EXTRA_NEWS = "news";
    public static final String EXTRA_TAG = "tag";

    public CustomNewsListAdapter(Context context, int layoutResourceId, List<News> newsItems) {
        this.mContext = context;
        this.layoutResourceId = layoutResourceId;
        this.newsItems = newsItems;
    }

    private View.OnClickListener onNewsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Integer rowPosition = (Integer) v.getTag();
            openNewsDetail(newsItems.get(rowPosition));
        }
    };
    private View.OnClickListener onTagClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String tag = (String) v.getTag();
            openNewsTag(tag);
        }
    };

    @Override
    public int getCount() {
        return newsItems.size();
    }

    @Override
    public Object getItem(int location) {
        return newsItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null){
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, null);
        }
        News news = newsItems.get(position);

        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView date = (TextView) convertView.findViewById(R.id.date);
        LinearLayout tagsContainer = (LinearLayout) convertView.findViewById(R.id.tags);
        TextView content = (TextView) convertView.findViewById(R.id.content);
        ImageView iconPlay = (ImageView) convertView.findViewById(R.id.play_icon);

        // ref:http://stackoverflow.com/questions/2160619/android-ellipsize-multiline-textview
        content.setMaxLines(2);

        title.setText(news.getTitle());

        if(TextUtils.isEmpty(news.getFirstVideo())){
            iconPlay.setVisibility(View.GONE);
        }else{
            iconPlay.setVisibility(View.VISIBLE);
        }

        date.setText(news.getDate());

        ArrayList<Tag> tagsArry = news.getTags();

        tagsContainer.removeAllViews();

        for (int i = 0; i < tagsArry.size(); i++) {
            // additional layout is for attaching margin-right for dynamic text view
            RelativeLayout textViewLayout = (RelativeLayout) View.inflate(mContext, R.layout.tag_textview, null);
            TextView textView = (TextView) textViewLayout.findViewById(R.id.tag_textview);
            Tag tag = (Tag) tagsArry.get(i);
            textView.setText(tag.getName());
            textViewLayout.setTag(tag.getName());
            textViewLayout.setOnClickListener(onTagClickListener);
            tagsContainer.addView(textViewLayout);
        }


        content.setText(news.getContent());

//        String thumbnailSrc = n.getThumbnail();
//        if(TextUtils.isEmpty(thumbnailSrc)){
//            thumbnail.setMaxHeight(0);
//            thumbnail.setMaxWidth(0);
//        }else{
//            new ImageDownloaderTask(thumbnail).execute(thumbnailSrc);
//        }

        title.setTag(Integer.valueOf(position));
        content.setTag(Integer.valueOf(position));

        title.setOnClickListener(onNewsClickListener);
        content.setOnClickListener(onNewsClickListener);

        return convertView;
    }


    private void openNewsTag(String tag){
        Intent intent = new Intent(mContext, TagActivity.class);
        intent.putExtra(EXTRA_TAG, tag);
        mContext.startActivity(intent);
        Log.d(NewsDataServices.class.getSimpleName(), "onCreate() Restoring previous state");
    }

    private void openNewsDetail(News news){
        Intent intent = new Intent(mContext, DetailActivity.class);
        intent.putExtra(EXTRA_NEWS, news);
        mContext.startActivity(intent);
        Log.d(NewsDataServices.class.getSimpleName(), "onCreate() Restoring previous state");
    }
}