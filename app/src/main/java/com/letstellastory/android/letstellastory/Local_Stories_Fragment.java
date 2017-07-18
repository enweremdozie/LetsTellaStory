package com.letstellastory.android.letstellastory;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dozie on 2017-07-08.
 */

public class Local_Stories_Fragment extends Fragment {

    GridView gridview;
    String story, genre;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_invited__stories_, container, false);

        story = story;
        genre = genre;
        DBHelper mystories = new DBHelper(getActivity());
        if (story != null && genre != null) {
            mystories.insertData_local_stories(story, genre);
        }

        gridview = (GridView) view.findViewById(R.id.gridview);
        /*List<ItemObject> sList = getListItemData();
        CustomAdapter customAdapter = new CustomAdapter(getActivity(), sList);
        gridview.setAdapter(customAdapter);*/
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), Story.class);
                intent.putExtra("story", story);
                intent.putExtra("genre", genre);
                startActivity(intent);
            }
        });
        if (story != null) {
            loadStoryDialogs();
            //createSessionForStory();
        }
        return view;
    }


    private List<ItemObject> getListItemData() {
        List<ItemObject> listViewItems = new ArrayList<ItemObject>();
        DBHelper helper = new DBHelper(this.getContext());
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = helper.getLocalStoriesInformations(db);
        String id,mystory,mygenre;

        while(cursor.moveToNext()){
            id = cursor.getString(cursor.getColumnIndex(helper.COL_ID));
            mystory = cursor.getString(cursor.getColumnIndex(helper.COL_TITLE));
            mygenre = cursor.getString(cursor.getColumnIndex(helper.COL_GENRE));
            listViewItems.add(new ItemObject(mystory, mygenre));
            //Product product = new Product(id,mystory,mygenre);

        }

        return listViewItems;
    }

    private void loadStoryDialogs() {

        QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
        requestBuilder.setLimit(100);

        QBRestChatService.getChatDialogs(null, requestBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatDialog>>() {
            @Override
            public void onSuccess(ArrayList<QBChatDialog> qbChatDialogs, Bundle bundle) {

                //Log.d("CREATION","Inside of local stories fragment");
                List<ItemObject> sList = getListItemData();
                CustomAdapter customAdapter = new CustomAdapter(getActivity(), sList);
                gridview.setAdapter(customAdapter);
                customAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("ERROR", e.getMessage());
            }
        });


    }


}
