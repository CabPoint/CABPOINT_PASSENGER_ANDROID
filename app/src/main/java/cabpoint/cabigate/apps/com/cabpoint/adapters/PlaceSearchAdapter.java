package cabpoint.cabigate.apps.com.cabpoint.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

import cabpoint.cabigate.apps.com.cabpoint.R;
import cabpoint.cabigate.apps.com.cabpoint.models.AddressDao;


/**
 * Created by Danish on 11/06/18.
 */

public class PlaceSearchAdapter extends BaseAdapter {

    private ArrayList<AddressDao> mList;
    private LayoutInflater mLayoutInflater;

    public PlaceSearchAdapter(Context context, ArrayList<AddressDao> list) {;
        mList  = list;
        mLayoutInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;

        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.item_place_search, viewGroup,false);
            viewHolder = new ViewHolder();
            viewHolder.txtTitle = (TextView) view.findViewById(R.id.item_search_title);
            viewHolder.txtSubTitle = (TextView) view.findViewById(R.id.item_search_sub);
            viewHolder.pins = (ImageView) view.findViewById(R.id.pin);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.txtTitle.setText(mList.get(i).getPrimaryText());
        viewHolder.txtSubTitle.setText(mList.get(i).getSecondaryText());

            viewHolder.pins.setImageResource(R.mipmap.suggest_pin);


        return view;
    }

    private class ViewHolder {
        public TextView txtTitle;
        public TextView txtSubTitle;
        public ImageView pins;

    }
}