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
    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<DrawerOption> optionList;

    public CustomDrawerListAdapter(Activity activity, ArrayList<DrawerOption> optionList) {
        this.activity = activity;
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

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.drawer_list_row, null);

        ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView hint = (TextView) convertView.findViewById(R.id.hint);

        DrawerOption option = optionList.get(position);

        Resources resource = activity.getApplicationContext().getResources();

        icon.setBackground(resource.getDrawable(resource.getIdentifier(option.getIcon(), "drawable", activity.getPackageName())));
        title.setText(option.getTitle());
        hint.setText(option.getHint());

        return convertView;
    }

}