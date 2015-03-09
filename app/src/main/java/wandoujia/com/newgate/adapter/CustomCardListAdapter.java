package wandoujia.com.newgate.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

import wandoujia.com.newgate.Intents;
import wandoujia.com.newgate.R;
import wandoujia.com.newgate.app.AppController;
import wandoujia.com.newgate.model.News;
import wandoujia.com.newgate.model.Tag;

public class CustomCardListAdapter extends RecyclerView.Adapter<CustomCardListAdapter.ViewHolder> {

    private static final int TYPE_NO_THUMB = 1;
    private static final int TYPE_THUMB = 2;

    private List<News> newses;
    private Context mContext;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomCardListAdapter(List<News> newses) {
        this.newses = newses;
    }

    private View.OnClickListener onNewsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Integer rowPosition = (Integer) v.getTag();
            Intents.openNewsDetail(mContext, newses.get(rowPosition));
        }
    };

    private View.OnClickListener onTagClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String tag = (String) v.getTag();
            Intents.openNewsTag(mContext, tag);
        }
    };
    @Override
    public int getItemViewType(int position){
        return TextUtils.isEmpty((newses.get(position).getThumbnail())) ? TYPE_NO_THUMB : TYPE_THUMB;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        if (viewType == TYPE_NO_THUMB) {
            final View view = LayoutInflater.from(mContext).inflate(R.layout.news_card_no_thumb, parent, false);
            return new ViewHolder(view);
        } else if (viewType == TYPE_THUMB) {
            final View view = LayoutInflater.from(mContext).inflate(R.layout.news_card_horizontal, parent, false);
            return new ViewHolder(view);
        }
        throw new RuntimeException("There is no type that matches the type " + viewType + " + make sure your using types    correctly");
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        if(viewHolder.thumbnail != null) {
            viewHolder.thumbnail.setImageDrawable(null);
        }

        News news = newses.get(position);

        viewHolder.title.setText(news.getTitle());
        viewHolder.title.setTag(Integer.valueOf(position));
        viewHolder.title.setOnClickListener(onNewsClickListener);

        ArrayList<Tag> tagsArry = news.getTags();

        viewHolder.tagsContainer.removeAllViews();
        for (int i = 0; i < tagsArry.size(); i++) {
            // additional layout is for attaching margin-right for dynamic text view
            RelativeLayout textViewLayout = (RelativeLayout) View.inflate(mContext, R.layout.tag_textview, null);
            TextView textView = (TextView) textViewLayout.findViewById(R.id.tag_textview);
            Tag tag = tagsArry.get(i);
            textView.setText(tag.getName());
            textViewLayout.setTag(tag.getName());
            textViewLayout.setOnClickListener(onTagClickListener);
            textViewLayout.setTag(tag.getName());
            viewHolder.tagsContainer.addView(textViewLayout);
        }
        if(viewHolder.thumbnail != null){
            viewHolder.thumbnail.setImageUrl(news.getThumbnail(), imageLoader);

            viewHolder.thumbnail.setTag(Integer.valueOf(position));
            viewHolder.thumbnail.setOnClickListener(onNewsClickListener);
        }

        if(viewHolder.content != null){
            viewHolder.content.setText(news.getContent());

            viewHolder.content.setTag(Integer.valueOf(position));
            viewHolder.content.setOnClickListener(onNewsClickListener);
        }
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return newses == null ? 0 : newses.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public NetworkImageView thumbnail;
        public TextView content;
        LinearLayout tagsContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            thumbnail = (NetworkImageView) itemView.findViewById(R.id.thumbnail);
            tagsContainer = (LinearLayout)itemView.findViewById(R.id.tags);
            content = (TextView) itemView.findViewById(R.id.content);
        }

    }

}