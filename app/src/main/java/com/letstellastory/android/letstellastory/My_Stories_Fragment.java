package com.letstellastory.android.letstellastory;

import android.app.ProgressDialog;
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

import com.letstellastory.android.letstellastory.Common.Common;
import com.letstellastory.android.letstellastory.Holder.QBUsersHolder;
import com.letstellastory.android.letstellastory.adapter.StoryDialogAdapters;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.BaseService;
import com.quickblox.auth.session.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dozie on 2017-07-08.
 */

public class My_Stories_Fragment extends Fragment {
    GridView gridview;
    String story, genre, user, password;

    @Override
    public void onResume() {
        super.onResume();
        loadStoryDialogs();


    }

    @Override
    public void onStart() {
        super.onStart();
        //createSessionForStory();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_invited__stories_, container, false);

        /*GridView gridview = (GridView)view.findViewById(gridview);
        List<ItemObject> sList = getListItemData();
        CustomAdapter customAdapter = new CustomAdapter(getActivity(), sList);
        gridview.setAdapter(customAdapter);*/
        //RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        //recyclerView.setHasFixedSize(true);

        //_sGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        //recyclerView.setLayoutManager(_sGridLayoutManager);



        //StoryRecycler rcAdapter = new StoryRecycler(getActivity(), sList);
        //recyclerView.setAdapter(rcAdapter);

        //createSessionForStory();

        //gridview = (GridView)view.findViewById(R.id.gridview);
        story = theStories.story;
        genre = theStories.genre;
        user = theStories.user;
        password = theStories.password;

        DBHelper mystories = new DBHelper(getActivity());
        if(story != null && genre != null) {
            mystories.insertData_my_stories(story, genre);
        }

        gridview = (GridView)view.findViewById(R.id.gridview);
        List<ItemObject> sList = getListItemData();
        CustomAdapter customAdapter = new CustomAdapter(getActivity(), sList);
        gridview.setAdapter(customAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                createSessionForStory();

                QBChatDialog qbChatDialog = (QBChatDialog) gridview.getAdapter().getItem(position);
                Intent intent = new Intent(getActivity(), Story.class);
                intent.putExtra("story", story);
                intent.putExtra(Common.DIALOG_EXTRA, qbChatDialog);
                intent.putExtra("genre", genre);
                intent.putExtra("user", user);
                intent.putExtra("password", password);
                startActivity(intent);
            }
        });

            loadStoryDialogs();
            //createSessionForStory();


        return view;


    }


    private List<ItemObject> getListItemData() {
        //Intent intent = getIntent();
        //story = intent.getExtras().getString("title");
        //genre = intent.getExtras().getString("genre");
        List<ItemObject> listViewItems = new ArrayList<ItemObject>();
        DBHelper helper = new DBHelper(this.getContext());
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = helper.getMyStoriesInformations(db);
        String id,mystory,mygenre;

        while(cursor.moveToNext()){
            id = cursor.getString(cursor.getColumnIndex(helper.COL_ID));
            mystory = cursor.getString(cursor.getColumnIndex(helper.COL_TITLE));
            mygenre = cursor.getString(cursor.getColumnIndex(helper.COL_GENRE));
            listViewItems.add(new ItemObject(mystory, mygenre));
            //Product product = new Product(id,mystory,mygenre);

        }



        //listViewItems.add(new ItemObject(story, genre));
        /*listViewItems.add(new ItemObject("Pride and Prejudice", "Horror"));
        listViewItems.add(new ItemObject("One Hundred Years of Solitude", "Romance"));
        listViewItems.add(new ItemObject("The Book Thief", "Romance"));
        listViewItems.add(new ItemObject("The Hunger Games", "Horror"));
        listViewItems.add(new ItemObject("The Hitchhiker's Guide to the Galaxy", "Drama"));
        listViewItems.add(new ItemObject("The Theory Of Everything", "Romance"));
        listViewItems.add(new ItemObject("story", "genre"));
        listViewItems.add(new ItemObject("Pride and Prejudice", "Horror"));
        listViewItems.add(new ItemObject("One Hundred Years of Solitude", "Romance"));
        listViewItems.add(new ItemObject("The Book Thief", "Romance"));
        listViewItems.add(new ItemObject("The Hunger Games", "Horror"));
        listViewItems.add(new ItemObject("The Hitchhiker's Guide to the Galaxy", "Drama"));
        listViewItems.add(new ItemObject("The Theory Of Everything", "Romance"));*/





        return listViewItems;
    }

    private void loadStoryDialogs() {

        QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
        requestBuilder.setLimit(100);

        QBRestChatService.getChatDialogs(null, requestBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatDialog>>() {
            @Override
            public void onSuccess(ArrayList<QBChatDialog> qbChatDialogs, Bundle bundle) {

                Log.d("CREATION","Inside of my stories fragment");
                /*List<ItemObject> sList = getListItemData();
                CustomAdapter customAdapter = new CustomAdapter(getActivity(), sList);
                gridview.setAdapter(customAdapter);
                customAdapter.notifyDataSetChanged();*/
                StoryDialogAdapters adapter = new StoryDialogAdapters(getActivity(), qbChatDialogs);
                gridview.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("ERROR", e.getMessage());
            }
        });


    }


    private void createSessionForStory(){
        final ProgressDialog mDialog = new ProgressDialog(getActivity());
        mDialog.setMessage("Please wait...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        String user = theStories.user;
        String password = theStories.password;

        QBUsers.getUsers(null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                QBUsersHolder.getInstance().putUsers(qbUsers);
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });


        final QBUser qbUser = new QBUser(user, password);
        Log.d("CREATION", "in story fragment password is " + password);
        QBAuth.createSession(qbUser).performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {
                qbUser.setId(qbSession.getUserId());
                try {
                    qbUser.setPassword(BaseService.getBaseService().getToken());
                } catch (BaseServiceException e) {
                    e.printStackTrace();
                }

                QBChatService.getInstance().login(qbUser, new QBEntityCallback() {
                    @Override
                    public void onSuccess(Object o, Bundle bundle) {
                        mDialog.dismiss();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Log.e("ERROR",""+e.getMessage());
                    }
                });
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }
}
