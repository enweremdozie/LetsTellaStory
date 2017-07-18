package com.letstellastory.android.letstellastory;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import com.quickblox.chat.request.QBMessageGetBuilder;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.SmackException;

import java.util.ArrayList;

//Not needed

public class Story extends AppCompatActivity {
    TextView pass, post;
    DBHelper db;
    EditText storyED;
    String ActTitle, genre, story, storyTeller;
    long storyTime;
    StoryMessageAdapter adapter;

    QBChatDialog qbChatDialog;
    ListView lstStoryMessages;



    @Override
    protected void onResume() {
        super.onResume();
        //loadChatDialogs();
        //initChatDialogs();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        //db = new DBHelper(this);

        lstStoryMessages = (ListView) findViewById(R.id.storyList);
        post = (TextView) findViewById(R.id.postStory);
        pass = (TextView) findViewById(R.id.passStory);
        storyED = (EditText) findViewById(R.id.storyEdit);
        //show = (TextView) findViewById(R.id.showAll);
        Intent intent = getIntent();
        ActTitle = intent.getExtras().getString("story");
        genre = intent.getExtras().getString("genre");
        //Toast.makeText(Story.this, genre, Toast.LENGTH_LONG).show();
        //setTitle(ActTitle);

        //AddData();
        //createSessionForStory();


        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            if(storyED.getText() != null && storyED.getText().toString().trim().length() > 0){
                post.setVisibility(View.INVISIBLE);
                pass.setVisibility(View.VISIBLE);
                Toast.makeText(Story.this, "posting", Toast.LENGTH_LONG).show();

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


                storyED.setText("");
                storyED.setFocusable(true);

            }
          else {
                Toast.makeText(Story.this, "\"What happens next\" cannot be empty", Toast.LENGTH_LONG).show();
            }

            }
        });

        pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Story.this, ListUsersActivity.class);
                startActivity(intent);
            }
        });




        initStoryDialogs();

        retrieveStories();
        //loadChatDialogs();

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

        if(item.getItemId() == R.id.start){
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
}

