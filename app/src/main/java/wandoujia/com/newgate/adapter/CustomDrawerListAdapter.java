package wandoujia.com.newgate.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import wandoujia.com.newgate.R;
import wandoujia.com.newgate.model.DrawerOption;

public class CustomDrawerListAdapter extends BaseAdapter {
    private Context mContext;
    private int layoutResourceId;
    private ArrayList<DrawerOption> optionList;

    public CustomDrawerListAdapter(Context context, int layoutResourceId, ArrayList<DrawerOption> optionList) {
        this.mContext = context;
        this.layoutResourceId = layoutResourceId;
        this.optionList = optionList;
    }

    @Override
    public int getCount() {
        return optionList.size();
    }

    @Override
    public Object getItem(int location) {
        return optionList.get(location);
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

        ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
        TextView title = (TextView) convertView.findViewById(R.id.title);

        DrawerOption option = optionList.get(position);

        Resources resource = mContext.getApplicationContext().getResources();

        icon.setBackground(resource.getDrawable(resource.getIdentifier(option.getIcon(), "drawable", mContext.getPackageName())));
        title.setText(option.getTitle());

        return convertView;
    }

}