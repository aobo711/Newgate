package wandoujia.com.newgate.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import wandoujia.com.newgate.R;
import wandoujia.com.newgate.model.News;
import wandoujia.com.newgate.model.Tag;

public class CustomNewsListAdapter extends BaseAdapter {
    private Context mContext;
    private int layoutResourceId;
    private List<News> newsItems;

    public CustomNewsListAdapter(Context context, int layoutResourceId, List<News> newsItems) {
        this.mContext = context;
        this.layoutResourceId = layoutResourceId;
        this.newsItems = newsItems;
    }

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
//        ImageView thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail);

        // ref:http://stackoverflow.com/questions/2160619/android-ellipsize-multiline-textview
        content.setMaxLines(2);

        // getting movie data for the row

        String titleText = news.getTitle();

        if(TextUtils.isEmpty(news.getFirstVideo())){
            title.setText(titleText);
        }else{
            final String img = String.format("<img src=\"%s\"/>", R.drawable.ic_play);
            final String html = titleText + img;
            title.setText(Html.fromHtml(html, new Html.ImageGetter() {
                @Override
                public Drawable getDrawable(final String source) {
                    Drawable d = mContext.getResources().getDrawable(Integer.parseInt(source));
                    d.setBounds(0, 0, 58, 58);
                    return d;
                }
            }, null));
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

        return convertView;
    }

}