package com.letstellastory.android.letstellastory.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.letstellastory.android.letstellastory.Holder.QBUnreadMessageHolder;
import com.letstellastory.android.letstellastory.R;
import com.quickblox.chat.model.QBChatDialog;

import java.util.ArrayList;

/**
 * Created by dozie on 2017-07-12.
 */

public class StoryDialogAdapters extends BaseAdapter{
    private Context context;
    private ArrayList<QBChatDialog> qbChatDialogs;
    int index = 0;



    public StoryDialogAdapters(Context context, ArrayList<QBChatDialog> qbChatDialogs) {
        this.context = context;
        this.qbChatDialogs = qbChatDialogs;
    }


    @Override
    public int getCount() {
        return qbChatDialogs.size();
    }

    @Override
    public Object getItem(int position) {
        return qbChatDialogs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.grid_view, null);
            TextView storyShow,genreShow;
            ImageView image_unread;

            storyShow = (TextView) view.findViewById(R.id.storyView);
            genreShow = (TextView) view.findViewById(R.id.genreView);
            image_unread = (ImageView) view.findViewById(R.id.image_unread);

            TextDrawable.IBuilder unreadBuilder = TextDrawable.builder().beginConfig()
                    .withBorder(4)
                    .endConfig()
                    .round();

            int unread_count = QBUnreadMessageHolder.getInstance().getBundle().getInt(qbChatDialogs.get(position).getDialogId());
            if(unread_count > 0){
                TextDrawable unread_drawable = unreadBuilder.build(""+unread_count, Color.RED);
                image_unread.setImageDrawable(unread_drawable);
            }
            /*if(position == 0) {
                storyShow.setText("position 1");
                genreShow.setText("genre 1");
            }

            if(position == 1){
                storyShow.setText("position 2");
                genreShow.setText("genre 2");
            }*/

            //for(int i = 0; i < qbChatDialogs.size(); i++){

                String name,dialstory,dialgenre;
                int pos;
                if(position == index) {
                    name = qbChatDialogs.get(position).getName();
                    pos = name.lastIndexOf("-");
                    dialstory = name.substring(0, pos);
                    dialgenre = name.substring((pos + 1), (name.length()));
                    storyShow.setText(dialstory);
                    genreShow.setText(dialgenre);
                    index++;
               // }
            }
        }
        return view;
    }
}
