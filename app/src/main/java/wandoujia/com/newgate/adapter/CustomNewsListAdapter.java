package wandoujia.com.newgate.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

import wandoujia.com.newgate.R;
import wandoujia.com.newgate.activity.DetailActivity;
import wandoujia.com.newgate.activity.TagActivity;
import wandoujia.com.newgate.app.AppController;
import wandoujia.com.newgate.model.News;
import wandoujia.com.newgate.model.Tag;

public class CustomNewsListAdapter extends BaseAdapter {
    private Context mContext;
    private int layoutResourceId;
    private List<News> newsItems;
    public static final String EXTRA_NEWS = "news";
    public static final String EXTRA_TAG = "tag";
    private LayoutInflater inflater;

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

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
        ViewHolder holder;

        if( convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(layoutResourceId, null);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.tagsContainer = (LinearLayout) convertView.findViewById(R.id.tags);
            holder.thumbnail = (NetworkImageView) convertView.findViewById(R.id.thumbnail);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        if (imageLoader == null) {
            imageLoader = AppController.getInstance().getImageLoader();
        }

        News news = newsItems.get(position);

        holder.title.setText(news.getTitle());

        holder.date.setText(news.getDate());

        ArrayList<Tag> tagsArry = news.getTags();

        holder.tagsContainer.removeAllViews();

        for (int i = 0; i < tagsArry.size(); i++) {
            // additional layout is for attaching margin-right for dynamic text view
            RelativeLayout textViewLayout = (RelativeLayout) View.inflate(mContext, R.layout.tag_textview, null);
            TextView textView = (TextView) textViewLayout.findViewById(R.id.tag_textview);
            Tag tag = (Tag) tagsArry.get(i);
            textView.setText(tag.getName());
            textViewLayout.setTag(tag.getName());
            textViewLayout.setOnClickListener(onTagClickListener);
            holder.tagsContainer.addView(textViewLayout);
        }


//        content.setText(news.getContent());

        String thumbnailSrc = news.getThumbnail();
        if(TextUtils.isEmpty(thumbnailSrc)){
//            holder.thumbnail.setVisibility(View.INVISIBLE);
//            holder.thumbnail.setBackground(((Activity) mContext).getDrawable(R.drawable.ic_play));
        }else{
            holder.thumbnail.setImageUrl(thumbnailSrc, imageLoader);
        }

        holder.title.setTag(Integer.valueOf(position));
        holder.title.setOnClickListener(onNewsClickListener);

        return convertView;
    }

    class ViewHolder{
        TextView title;
        TextView date;
        LinearLayout tagsContainer;
        NetworkImageView thumbnail;
    }


    private void openNewsTag(String tag){
        Intent intent = new Intent(mContext, TagActivity.class);
        intent.putExtra(EXTRA_TAG, tag);
        mContext.startActivity(intent);
    }

    private void openNewsDetail(News news){
        Intent intent = new Intent(mContext, DetailActivity.class);
        intent.putExtra(EXTRA_NEWS, news);
        mContext.startActivity(intent);
    }
}