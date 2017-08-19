package com.letstellastory.android.letstellastory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by dozie on 2017-07-09.
 */

public class CustomAdapter extends BaseAdapter{
    private LayoutInflater layoutinflater;
    private List<ItemObject> listStorage;
    private Context context;

    public CustomAdapter(Context context, List<ItemObject> customizedListView) {
        this.context = context;
        layoutinflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listStorage = customizedListView;
    }


    @Override
    public int getCount() {
        return listStorage.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder listViewHolder;
        if(convertView == null){
            listViewHolder = new ViewHolder();
            convertView = layoutinflater.inflate(R.layout.grid_view, parent, false);
            listViewHolder.story = (TextView)convertView.findViewById(R.id.storyView);
            listViewHolder.genre = (TextView)convertView.findViewById(R.id.genreView);

            convertView.setTag(listViewHolder);
        }
        else{
            listViewHolder = (ViewHolder)convertView.getTag();
        }
        listViewHolder.story.setText(listStorage.get(position).getName());
        listViewHolder.genre.setText(listStorage.get(position).getGenre());

        return convertView;
    }

    static class ViewHolder{
        ImageView screenShot;
        TextView story;
        TextView genre;
    }
}