package wandoujia.com.newgate.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import wandoujia.com.newgate.R;
import wandoujia.com.newgate.model.News;
import wandoujia.com.newgate.model.Tag;

public class CustomNewsListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<News> newsItems;

    public CustomNewsListAdapter(Activity activity, List<News> newsItems) {
        this.activity = activity;
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

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row, null);

        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView date = (TextView) convertView.findViewById(R.id.date);
        TextView tags = (TextView) convertView.findViewById(R.id.tags);

        // getting movie data for the row
        News n = newsItems.get(position);
        String titleText = n.getTitle();

        if(TextUtils.isEmpty(n.getFirstVideo())){
            title.setText(titleText);
        }else{
            final String img = String.format("<img src=\"%s\"/>", R.drawable.ic_play);
            final String html = titleText + img;
            title.setText(Html.fromHtml(html, new Html.ImageGetter() {
                @Override
                public Drawable getDrawable(final String source) {
                    Drawable d = null;
                    try {
                        d = activity.getResources().getDrawable(Integer.parseInt(source));
                        d.setBounds(0, 0, 72, 72);
                    } catch (Resources.NotFoundException e) {
                        Log.e("log_tag", "Image not found. Check the ID.", e);
                    } catch (NumberFormatException e) {
                        Log.e("log_tag", "Source string not a valid resource ID.", e);
                    }

                    return d;
                }
            }, null));
        }

        date.setText(n.getDate());

        ArrayList<Tag> tagsArry = n.getTags();

        String tagsStr = new String();

        for (int i = 0; i < tagsArry.size(); i++) {
            Tag tag = (Tag) tagsArry.get(i);
            tagsStr += String.format("<font color=\"%s\">#%s</font>", tag.getColor(), tag.getName());
        }
        tags.setText(Html.fromHtml(tagsStr));

        return convertView;
    }

}