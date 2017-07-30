package com.letstellastory.android.letstellastory.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.letstellastory.android.letstellastory.Holder.QBUsersHolder;
import com.letstellastory.android.letstellastory.R;
import com.quickblox.chat.QBChatService;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by dozie on 2017-07-13.
 */

public class ListUsersAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<QBUser> qbUserArrayList;
    //Adapter adapter = new Adapter(getBaseContext());

    public ListUsersAdapter(Context context, ArrayList<QBUser> qbUserArrayList) {
        this.context = context;
        this.qbUserArrayList = qbUserArrayList;
    }


    @Override
    public int getCount() {
        return qbUserArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return qbUserArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final int pos = position;


        if(convertView == null);
        {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.list_user_layout, parent, false);
            TextView textView = (TextView)view.findViewById(R.id.list_user_name);
            final ImageView imageView = (ImageView)view.findViewById(R.id.image_user);
            //CheckBox checkBox = (CheckBox)view.findViewById(R.id.check_box);

            textView.setText(qbUserArrayList.get(position).getLogin());
            //imageView.setIm
            //imageView.setImageResource(qbUserArrayList.get(position).getFileId());
            QBUsers.getUser(QBChatService.getInstance().getUser().getId())
                    .performAsync(new QBEntityCallback<QBUser>() {
                        @Override
                        public void onSuccess(QBUser qbUser, Bundle bundle) {
                            QBUsersHolder.getInstance().putUser(qbUser);
                            if (qbUserArrayList.get(pos).getFileId() != null) {
                                int profilePictureId = qbUserArrayList.get(pos).getFileId();

                                QBContent.getFile(profilePictureId)
                                        .performAsync(new QBEntityCallback<QBFile>() {
                                            @Override
                                            public void onSuccess(QBFile qbFile, Bundle bundle) {
                                                String fileUrl = qbFile.getPublicUrl();
                                                Picasso.with(context)
                                                        .load(fileUrl)
                                                        .into(imageView);
                                            }

                                            @Override
                                            public void onError(QBResponseException e) {

                                            }
                                        });
                            }
                        }

                        @Override
                        public void onError(QBResponseException e) {

                        }
                    });
        }
        return view;
    }


}
