package com.letstellastory.android.letstellastory;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.letstellastory.android.letstellastory.Common.Common;
import com.letstellastory.android.letstellastory.Holder.QBStoryMessageHolder;
import com.letstellastory.android.letstellastory.adapter.StoryMessageAdapter;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.BaseService;
import com.quickblox.auth.session.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.request.QBMessageGetBuilder;
import com.quickblox.chat.request.QBMessageUpdateBuilder;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smackx.muc.DiscussionHistory;

import java.util.ArrayList;

//Not needed

public class Story extends AppCompatActivity implements QBChatDialogMessageListener{
    TextView pass, post;
    DBHelper db;
    EditText storyED;
    String ActTitle, genre, story, storyTeller;
    long storyTime;
    StoryMessageAdapter adapter;

    int contextMenuIndexClicked = -1;
    boolean isEditMode = false;
    QBChatMessage editMessage;

    QBChatDialog qbChatDialog;
    ListView lstStoryMessages;



    @Override
    protected void onDestroy() {
        super.onDestroy();
        qbChatDialog.removeMessageListrener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        qbChatDialog.removeMessageListrener(this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        //db = new DBHelper(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        lstStoryMessages = (ListView) findViewById(R.id.storyList);
        post = (TextView) findViewById(R.id.postStory);
        pass = (TextView) findViewById(R.id.passStory);
        storyED = (EditText) findViewById(R.id.storyEdit);
        registerForContextMenu(lstStoryMessages);
        //show = (TextView) findViewById(R.id.showAll);
        Intent intent = getIntent();
        ActTitle = intent.getExtras().getString("story");
        genre = intent.getExtras().getString("genre");
        //Toast.makeText(Story.this, genre, Toast.LENGTH_LONG).show();
        setTitle(ActTitle);

        //AddData();
        //createSessionForStory();


        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            if(storyED.getText() != null && storyED.getText().toString().trim().length() > 0){
                post.setVisibility(View.INVISIBLE);
                pass.setVisibility(View.VISIBLE);
                Toast.makeText(Story.this, "posting", Toast.LENGTH_LONG).show();

                if(!isEditMode) {
                    QBChatMessage storyMessage = new QBChatMessage();
                    storyMessage.setBody(storyED.getText().toString());
                    storyMessage.setSenderId(QBChatService.getInstance().getUser().getId());
                    storyMessage.setSaveToHistory(true);

                    try {
                        qbChatDialog.sendMessage(storyMessage);
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }

                /*QBStoryMessageHolder.getInstance().putStory(qbChatDialog.getDialogId(), storyMessage);
                ArrayList<QBChatMessage> messages = QBStoryMessageHolder.getInstance().getStoryMessageByDialogId(qbChatDialog.getDialogId());
                adapter = new StoryMessageAdapter(getBaseContext(), messages);
                lstStoryMessages.setAdapter(adapter);
                adapter.notifyDataSetChanged();*/
                /*if(qbChatDialog.getType() == QBDialogType.PRIVATE){

                }*/

                    storyED.setText("");
                    storyED.setFocusable(true);
                }

                else{
                    final ProgressDialog updateDialog = new ProgressDialog(Story.this);
                    updateDialog.setMessage("Please wait...");
                    updateDialog.show();

                    QBMessageUpdateBuilder messageUpdateBuilder = new QBMessageUpdateBuilder();
                    messageUpdateBuilder.updateText(storyED.getText().toString()).markDelivered().markRead();

                    QBRestChatService.updateMessage(editMessage.getId(), qbChatDialog.getDialogId(), messageUpdateBuilder)
                            .performAsync(new QBEntityCallback<Void>() {
                                @Override
                                public void onSuccess(Void aVoid, Bundle bundle) {
                                    retrieveStories();
                                    isEditMode = false;
                                    updateDialog.dismiss();

                                    storyED.setText("");
                                    storyED.setFocusable(true);
                                }

                                @Override
                                public void onError(QBResponseException e) {
                                    Toast.makeText(getBaseContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
          else {
                Toast.makeText(Story.this, "\"What happens next\" cannot be empty", Toast.LENGTH_LONG).show();
            }

            }
        });

        pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUser();
            }
        });




        initStoryDialogs();

        retrieveStories();
        //loadChatDialogs();

    }

    private void addUser(){
        Intent intent = new Intent(Story.this, ListUsersActivity.class);
        intent.putExtra(Common.UPDATE_DIALOG_EXTRA,qbChatDialog);
        intent.putExtra(Common.UPDATE_MODE,Common.UPDATE_ADD_MODE);
        startActivity(intent);

    }

    private void retrieveStories() {
        QBMessageGetBuilder messageGetBuilder = new QBMessageGetBuilder();
        messageGetBuilder.setLimit(700);

        if(qbChatDialog != null){
            QBRestChatService.getDialogMessages(qbChatDialog, messageGetBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatMessage>>() {
                @Override
                public void onSuccess(ArrayList<QBChatMessage> qbChatMessages, Bundle bundle) {
                    QBStoryMessageHolder.getInstance().putStories(qbChatDialog.getDialogId(), qbChatMessages);
                    adapter = new StoryMessageAdapter(getBaseContext(), qbChatMessages);
                    lstStoryMessages.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onError(QBResponseException e) {

                }
            });
        }

    }

    private void initStoryDialogs() {
        qbChatDialog = (QBChatDialog)getIntent().getSerializableExtra(Common.DIALOG_EXTRA);
        qbChatDialog.initForChat(QBChatService.getInstance());

        QBIncomingMessagesManager incomingMessage = QBChatService.getInstance().getIncomingMessagesManager();
        incomingMessage.addDialogMessageListener(new QBChatDialogMessageListener() {
            @Override
            public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {

            }

            @Override
            public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {

            }
        });

        if(qbChatDialog.getType() == QBDialogType.PUBLIC_GROUP || qbChatDialog.getType() == QBDialogType.GROUP){
            DiscussionHistory discussionHistory = new DiscussionHistory();
            discussionHistory.setMaxStanzas(0);                                                 //number of messages to show from history
            qbChatDialog.join(discussionHistory, new QBEntityCallback() {
                @Override
                public void onSuccess(Object o, Bundle bundle) {

                }

                @Override
                public void onError(QBResponseException e) {
                Log.d("ERROR", ""+e.getMessage());
                }
            });

        }

        qbChatDialog.addMessageListener(new QBChatDialogMessageListener() {
            @Override
            public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {
                QBStoryMessageHolder.getInstance().putStory(qbChatMessage.getDialogId(),qbChatMessage);
                ArrayList<QBChatMessage> messages = QBStoryMessageHolder.getInstance().getStoryMessageByDialogId(qbChatMessage.getDialogId());
                adapter = new StoryMessageAdapter(getBaseContext(), messages);
                lstStoryMessages.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {
                Log.e("ERROR", e.getMessage());
            }
        });
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.story_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.profile){

        }

        else if(item.getItemId() == R.id.start){
            DialogFragment dialog = new CreateDialogFragment();
            dialog.show(getFragmentManager(), "CreateDialogFragment.tag");
            //Toast.makeText(this, "start", Toast.LENGTH_LONG).show();
            Log.d("CREATION", "creating in drama");
        }

        else if(item.getItemId() == R.id.join){

        }

        else if(item.getItemId() == R.id.my_stories){
            Intent intent = new Intent(Story.this,theStories.class);
            startActivity(intent);
        }
        else if(item.getItemId() == R.id.menu_sign_out){
            logOut();
        }
        return super.onOptionsItemSelected(item);
    }


    private void createSessionForStory(){
       final ProgressDialog mDialog = new ProgressDialog(Story.this);
       mDialog.setMessage("Please wait...");
       mDialog.setCanceledOnTouchOutside(false);
       mDialog.show();

       String user,password;
       user = getIntent().getStringExtra("user");
       password = getIntent().getStringExtra("password");


        Log.d("CREATION", "in story password is " + password);

        final QBUser qbUser = new QBUser(user, password);
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

    @Override
    public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {

    }

    @Override
    public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {
    Log.e("ERROR",""+e.getMessage());
    }


    private void logOut() {
        QBUsers.signOut().performAsync(new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                QBChatService.getInstance().logout(new QBEntityCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid, Bundle bundle) {
                        Toast.makeText(Story.this, "Logged out", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Story.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(QBResponseException e) {

                    }
                });
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        contextMenuIndexClicked = info.position;

        switch(item.getItemId()){
            case R.id.chat_message_update_mesage:
                updateMessage();
                break;
            case R.id.chat_message_delete_mesage:
                deleteMessage();
                break;
        }
        return true;
    }

    private void deleteMessage() {
        final ProgressDialog deleteDialog = new ProgressDialog(Story.this);
        deleteDialog.setMessage("Please wait...");
        deleteDialog.show();

        editMessage = QBStoryMessageHolder.getInstance().getStoryMessageByDialogId(qbChatDialog.getDialogId())
                .get(contextMenuIndexClicked);

        QBRestChatService.deleteMessage(editMessage.getId(), false).performAsync(new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                retrieveStories();
                deleteDialog.dismiss();
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }

    private void updateMessage() {


        editMessage = QBStoryMessageHolder.getInstance().getStoryMessageByDialogId(qbChatDialog.getDialogId())
                .get(contextMenuIndexClicked);
        storyED.setText(editMessage.getBody());
        isEditMode = true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.chat_message_context_menu, menu);
    }
}

