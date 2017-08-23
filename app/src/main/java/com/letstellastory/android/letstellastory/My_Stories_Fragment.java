package com.letstellastory.android.letstellastory;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.letstellastory.android.letstellastory.Common.Common;
import com.letstellastory.android.letstellastory.Holder.QBChatDialogHolder;
import com.letstellastory.android.letstellastory.Holder.QBUnreadMessageHolder;
import com.letstellastory.android.letstellastory.Holder.QBUsersHolder;
import com.letstellastory.android.letstellastory.adapter.StoryDialogAdapters;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.BaseService;
import com.quickblox.auth.session.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.listeners.QBSystemMessageListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by dozie on 2017-07-08.
 */

public class My_Stories_Fragment extends Fragment implements QBSystemMessageListener, QBChatDialogMessageListener{
    GridView gridview;
    String story, genre, user, password;
    //boolean hasposted = false;
    //boolean haspassed = false;
    Integer currentUser;
    String dialogID;

    @Override
    public void onResume() {
        super.onResume();
        Log.d("LOADSTORY", "onresume");
        Log.d("LOADSTORY", "password: " + password);
        createSessionForStory();
        loadStoryDialogs();

    }


    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("LOADSTORY", "onstart");
        Log.d("LOADSTORY", "password: " + password);
        //createSessionForStory();
        loadStoryDialogs();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_invited__stories_, container, false);
        story = theStories.story;
        genre = theStories.genre;
        user = theStories.user;
        password = theStories.password;

        createSessionForStory();

        gridview = (GridView)view.findViewById(R.id.gridview);
        registerForContextMenu(gridview);



        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView storyShow,genreShow;
                storyShow = (TextView) view.findViewById(R.id.storyView);
                genreShow = (TextView) view.findViewById(R.id.genreView);
                ImageView image_unread;
                QBChatDialog qbChatDialog = (QBChatDialog) gridview.getAdapter().getItem(position);

                Log.d("DIALOGTYPE", qbChatDialog.getType().toString());
                dialogID = qbChatDialog.getDialogId();


                Intent intent = new Intent(getActivity(), Story.class);
                intent.putExtra("story", storyShow.getText());
                intent.putExtra(Common.DIALOG_EXTRA, qbChatDialog);
                intent.putExtra("position", position);
                intent.putExtra("dialogID", dialogID);
                //intent.putExtra("hasposted", hasposted);
                //intent.putExtra("haspassed", haspassed);
                intent.putExtra("genre", genreShow.getText());
                intent.putExtra("user", user);
                intent.putExtra("password", password);
                intent.putExtra("currentUser", currentUser.toString());

                getActivity().startActivity(intent);

            }
        });

        loadStoryDialogs();


        return view;
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.story_dialog_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();


        switch(item.getItemId()){
            case R.id.context_delete_dialog:

                deleteDialog(info.position);

                break;
        }
        return true;
    }

    private void deleteDialog(int index) {
        final QBChatDialog chatDialog = (QBChatDialog) gridview.getAdapter().getItem(index);
        Integer adminID = chatDialog.getUserId();



        Log.d("CREATION", "admin user: " + adminID);
        Log.d("CREATION", "current user: " + currentUser);

            QBRestChatService.deleteDialog(chatDialog.getDialogId(), false)
                    .performAsync(new QBEntityCallback<Void>() {
                        @Override
                        public void onSuccess(Void aVoid, Bundle bundle) {
                                QBChatDialogHolder.getInstance().removeDialog(chatDialog.getDialogId());
                                StoryDialogAdapters adapter = new StoryDialogAdapters(getActivity().getBaseContext(), QBChatDialogHolder.getInstance().getAllGroupChatDialogs());
                                gridview.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }


                        @Override
                        public void onError(QBResponseException e) {

                        }
                    });



    }


    private void loadStoryDialogs() {

        final ProgressDialog mDialog = new ProgressDialog(getActivity());
        mDialog.setMessage("Loading stories");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();


        QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
        requestBuilder.setLimit(100);

        QBRestChatService.getChatDialogs(null, requestBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatDialog>>() {
            @Override
            public void onSuccess(final ArrayList<QBChatDialog> qbChatDialogs, Bundle bundle) {
                mDialog.dismiss();
                QBChatDialogHolder.getInstance().putDialogs(qbChatDialogs);

                Set<String> setIds = new HashSet<String>();
                for(final QBChatDialog chatDialog: qbChatDialogs){
                    setIds.add(chatDialog.getDialogId());

                    QBRestChatService.getTotalUnreadMessagesCount(setIds, QBUnreadMessageHolder.getInstance().getBundle())
                            .performAsync(new QBEntityCallback<Integer>() {
                                @Override
                                public void onSuccess(Integer integer, Bundle bundle) {
                                        QBUnreadMessageHolder.getInstance().setBundle(bundle);
                                        StoryDialogAdapters adapter = new StoryDialogAdapters(getActivity().getBaseContext(), QBChatDialogHolder.getInstance().getAllGroupChatDialogs());
                                        gridview.setAdapter(adapter);
                                        adapter.notifyDataSetChanged();
                                    }


                                @Override
                                public void onError(QBResponseException e) {

                                }
                            });
                }
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("ERROR", e.getMessage());
            }
        });


    }


    private void createSessionForStory(){


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

        QBAuth.createSession(qbUser).performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {

                qbUser.setId(qbSession.getUserId());
                currentUser = qbSession.getUserId();
                try {
                    qbUser.setPassword(BaseService.getBaseService().getToken());
                } catch (BaseServiceException e) {
                    e.printStackTrace();
                }



                QBChatService.getInstance().login(qbUser, new QBEntityCallback() {
                    @Override
                    public void onSuccess(Object o, Bundle bundle) {


                        QBSystemMessagesManager qbSystemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();
                        qbSystemMessagesManager.addSystemMessageListener(My_Stories_Fragment.this);

                        /*QBIncomingMessagesManager qbIncomingMessagesManager = QBChatService.getInstance().getIncomingMessagesManager();
                        qbIncomingMessagesManager.addDialogMessageListener(My_Stories_Fragment.this);*/
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

    @Override
    public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {
        //Log.d("CREATION", "Enters processMessage");

        loadStoryDialogs();
    }

    @Override
    public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {

    }

    @Override
    public void processMessage(QBChatMessage qbChatMessage) {


        QBRestChatService.getChatDialogById(qbChatMessage.getBody()).performAsync(new QBEntityCallback<QBChatDialog>() {

            @Override
        public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                    QBChatDialogHolder.getInstance().putDialog(qbChatDialog);
                    ArrayList<QBChatDialog> adapterSource = QBChatDialogHolder.getInstance().getAllGroupChatDialogs();
                    StoryDialogAdapters adapters = new StoryDialogAdapters(getActivity().getBaseContext(), adapterSource);
                    gridview.setAdapter(adapters);
                    adapters.notifyDataSetChanged();
                }


        @Override
        public void onError(QBResponseException e) {

        }
    });
    }

    @Override
    public void processError(QBChatException e, QBChatMessage qbChatMessage) {
        Log.e("ERROR", ""+e.getMessage());
    }


}
