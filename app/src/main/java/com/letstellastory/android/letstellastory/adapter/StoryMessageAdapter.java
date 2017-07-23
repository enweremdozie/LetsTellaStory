package com.letstellastory.android.letstellastory.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.letstellastory.android.letstellastory.R;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBChatMessage;

import java.util.ArrayList;

/**
 * Created by dozie on 2017-07-16.
 */

public class StoryMessageAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<QBChatMessage> qbChatMessages;

    public StoryMessageAdapter(Context context, ArrayList<QBChatMessage> qbChatMessages) {
        this.context = context;
        this.qbChatMessages = qbChatMessages;
    }

    @Override
    public int getCount() {
        return qbChatMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return qbChatMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(qbChatMessages.get(position).getSenderId().equals(QBChatService.getInstance().getUser().getId())){
                view = inflater.inflate(R.layout.list_send_story, null);
                TextView textView = (TextView)view.findViewById(R.id.story_content);
                textView.setText(qbChatMessages.get(position).getBody());
            }
            else{
                view = inflater.inflate(R.layout.list_send_story,null);
                TextView textView = (TextView)view.findViewById(R.id.story_content);
                textView.setText(qbChatMessages.get(position).getBody());
                //TextView txtName = (TextView)view.findViewById(R.id.story_user);
                //txtName.setText(QBUsersHolder.getInstance().getUserById(qbChatMessages.get(position).getSenderId()).getFullName());

            }
        }


        return view;
    }
}
