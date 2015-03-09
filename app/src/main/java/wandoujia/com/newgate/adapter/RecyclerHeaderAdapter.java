package wandoujia.com.newgate.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eowise.recyclerview.stickyheaders.StickyHeadersAdapter;

import java.util.List;

import wandoujia.com.newgate.R;
import wandoujia.com.newgate.model.News;

public class RecyclerHeaderAdapter implements StickyHeadersAdapter <RecyclerHeaderAdapter.ViewHolder> {

    private List<News> newses;
    private Context c;

    public RecyclerHeaderAdapter(List<News> newses) {
        this.newses = newses;
    }
    @Override
    public RecyclerHeaderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_row_header, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder headerViewHolder, int position) {
        headerViewHolder.date.setText(newses.get(position).getDate());
    }

    @Override
    public long getHeaderId(int position) {
        return Long.parseLong(newses.get(position).getDate().replaceAll("年|月|日", ""));
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        public ViewHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.header_date);
        }
    }
}
