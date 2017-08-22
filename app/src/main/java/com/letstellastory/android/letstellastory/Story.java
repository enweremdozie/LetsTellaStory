package com.letstellastory.android.letstellastory;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.letstellastory.android.letstellastory.Common.Common;
import com.letstellastory.android.letstellastory.Holder.QBStoryMessageHolder;
import com.letstellastory.android.letstellastory.Holder.QBUsersHolder;
import com.letstellastory.android.letstellastory.adapter.StoryMessageAdapter;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.QBSession;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.request.QBDialogRequestBuilder;
import com.quickblox.chat.request.QBMessageGetBuilder;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.messages.QBPushNotifications;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBEvent;
import com.quickblox.messages.model.QBNotificationType;
import com.quickblox.messages.model.QBPushType;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smackx.muc.DiscussionHistory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Story extends AppCompatActivity implements QBChatDialogMessageListener{
    //TextView pass = (TextView) findViewById(R.id.passStory);
    TextView pass, post;
    //DBHelper db;
    EditText storyED;
    String ActTitle, genre, story, user, password, dialogID, storyEdit, nameText, genreText, whoIsNext, currentUser, storyText, storyActText;
    String genreFormat;
    long storyTime;
    StoryMessageAdapter adapter;
    int position, passed, namepos, genrepos, storyLength, storyActLength, userStoryLength, wordsLeft;
    long pagesLeft;
    int contextMenuIndexClicked = -1;
    boolean isEditMode = false;
    QBChatMessage editMessage;
    Menu item;

    QBChatDialog qbChatDialog;
    ListView lstStoryMessages;
    boolean hasposted = false;
    boolean haspassed = false;
    TextInputLayout textInputLayout;
    TextView textCount;
    TextView textMax;
    Integer userID;
    String whoIsNextUserID;
    public static boolean canPassState = true;



    QBUser qbuser = new QBUser();


    @Override
    protected void onStart() {
        super.onStart();
        retrieveStories();

    }

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
    protected void onResume() {
        super.onResume();
        QBChatService.getInstance().setReconnectionAllowed(true);

        createSessionForStory();

        if(canPassState == false ) {
            pass.setVisibility(View.GONE);
            post.setVisibility(View.GONE);
            textInputLayout.setVisibility(View.GONE);
            textCount.setVisibility(View.GONE);
            textMax.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        QBChatService.getInstance().setReconnectionAllowed(true);

        createSessionForStory();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        //db = new DBHelper(this);

        QBChatService.getInstance().setReconnectionAllowed(true);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


        lstStoryMessages = (ListView) findViewById(R.id.storyList);
        post = (TextView) findViewById(R.id.postStory);
        pass = (TextView) findViewById(R.id.passStory);
        storyED = (EditText) findViewById(R.id.storyEdit);
        textCount = (TextView) findViewById(R.id.current_amount);
        textMax = (TextView) findViewById(R.id.textMax);
        textInputLayout = (TextInputLayout) findViewById(R.id.editLayout);


        qbChatDialog = (QBChatDialog)getIntent().getSerializableExtra(Common.DIALOG_EXTRA);

        if(qbChatDialog.getType().toString().equals("GROUP")) {
            registerForContextMenu(lstStoryMessages);
        }
        canPassState = true;

        if(qbChatDialog.getType().toString().equals("PUBLIC_GROUP")) {
            canPassState = true;
        }


        storyED.addTextChangedListener(mTextEditorWatcher);

        Intent intent = getIntent();
        story = intent.getExtras().getString("story");
        ActTitle = intent.getExtras().getString("story");
        genre = intent.getExtras().getString("genre");
        user = intent.getExtras().getString("user");
        password = intent.getExtras().getString("password");
        hasposted = intent.getExtras().getBoolean("hasposted");
        haspassed = intent.getExtras().getBoolean("haspassed");
        position = intent.getExtras().getInt("position");
        dialogID = intent.getExtras().getString("dialogID");
        currentUser = intent.getExtras().getString("currentUser");
        qbuser = new QBUser(user, password);


        setTitle(ActTitle);
        centerTitle();


        createSessionForStory();
        initStoryDialogs();
        QBSettings.getInstance().setEnablePushNotification(true);


        storyED.setFocusable(true);

            post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveStory();

            }
            });


            pass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        addUser();
                }
            });
    }

    private void saveStory() {
        storyEdit = storyED.getText().toString();
        userStoryLength = spaceCount(storyEdit);

        if (!storyEdit.equals(null) && storyED.getText().toString().trim().length() > 0) {


            QBChatMessage storyMessage = new QBChatMessage();
            storyMessage.setBody(storyEdit);
            storyMessage.setSenderId(QBChatService.getInstance().getUser().getId());
            storyMessage.setSaveToHistory(true);

            try {
                qbChatDialog.sendMessage(storyMessage);
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }

            QBStoryMessageHolder.getInstance().putStory(qbChatDialog.getDialogId(), storyMessage);
            ArrayList<QBChatMessage> stories = QBStoryMessageHolder.getInstance().getStoryMessageByDialogId(qbChatDialog.getDialogId());
            adapter = new StoryMessageAdapter(getBaseContext(), stories);
            lstStoryMessages.setAdapter(adapter);
            adapter.notifyDataSetChanged();


            if (qbChatDialog.getType().toString().equals("GROUP")) {
                changeDialogName();
                pass.setVisibility(View.VISIBLE);
                post.setVisibility(View.INVISIBLE);
                textInputLayout.setVisibility(View.INVISIBLE);
                textCount.setVisibility(View.INVISIBLE);
                textMax.setVisibility(View.INVISIBLE);
                Toast.makeText(Story.this, "Posting", Toast.LENGTH_SHORT).show();
                storyED.setText("");
            }


            else if (qbChatDialog.getType().toString().equals("PUBLIC_GROUP")) {
                pass.setVisibility(View.GONE);
                canPassState = true;



                if (currentUser.equals("31009125")) {
                    addAllUsers();
                }


                storyED.setText("");
            }

            retrieveStories();

        }

        else {
            Toast.makeText(Story.this, "\"What happens next\" cannot be empty", Toast.LENGTH_LONG).show();
        }
            /*QBMessageUpdateBuilder messageUpdateBuilder = new QBMessageUpdateBuilder();
            messageUpdateBuilder.updateText(storyEdit).markDelivered().markRead();

            QBRestChatService.updateMessage(storyMessage.getId(), qbChatDialog.getDialogId(), messageUpdateBuilder)
                    .performAsync(new QBEntityCallback<Void>() {
                        @Override
                        public void onSuccess(Void aVoid, Bundle bundle) {
                            if(qbChatDialog.getType().toString().equals("GROUP")) {
                                changeDialogName();
                                pass.setVisibility(View.VISIBLE);
                                post.setVisibility(View.INVISIBLE);
                                textInputLayout.setVisibility(View.INVISIBLE);
                                textCount.setVisibility(View.INVISIBLE);
                                textMax.setVisibility(View.INVISIBLE);
                                Toast.makeText(Story.this, "Posting", Toast.LENGTH_SHORT).show();

                                retrieveStories();
                                initStoryDialogs();


                                storyED.setText("");
                                //storyED.setFocusable(false);
                            }

                            else if(qbChatDialog.getType().toString().equals("PUBLIC_GROUP")){
                                pass.setVisibility(View.GONE);
                                canPassState = true;
                                /*post.setVisibility(View.VISIBLE);
                                textInputLayout.setVisibility(View.VISIBLE);
                                textCount.setVisibility(View.VISIBLE);
                                textMax.setVisibility(View.VISIBLE);
                                if(currentUser.equals("31009125")) {
                                    addAllUsers();
                                }

                                retrieveStories();
                                initStoryDialogs();

                                storyED.setText("");
                                //storyED.setFocusable(false);
                            }
                        }

                        @Override
                        public void onError(QBResponseException e) {
                            Toast.makeText(Story.this, "User error, please log out and log back in", Toast.LENGTH_LONG).show();

                            //Toast.makeText(getBaseContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            // }
        }

        else {
            Toast.makeText(Story.this, "\"What happens next\" cannot be empty", Toast.LENGTH_LONG).show();
        }*/




    }

    private void addAllUsers() {
        List<QBUser> qbusers = new ArrayList<>();
        qbusers = QBUsersHolder.getInstance().getAllUsers();
        StringifyArrayList<Integer> userId = new StringifyArrayList<Integer>();



        Integer user = 31009125;
        for (int i = 0; i < 1; i++) {
            userId.add(user);
        }

        QBEvent event = new QBEvent();
        event.setUserIds(userId);
        event.setEnvironment(QBEnvironment.DEVELOPMENT);
        event.setNotificationType(QBNotificationType.PUSH);
        event.setPushType(QBPushType.GCM);
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("data.message", "You have been invited to a new local story");
        data.put("data.type", "push notification");


        event.setMessage(data);

        QBPushNotifications.createEvent(event).performAsync(new QBEntityCallback<QBEvent>() {
            @Override
            public void onSuccess(QBEvent qbEvent, Bundle args) {
                // sent
            }

            @Override
            public void onError(QBResponseException errors) {

            }
        });

    }





    private void changeDialogName() {
        StringBuilder sb = new StringBuilder();
        sb.append("");
        sb.append(Integer.parseInt(storyActText) + userStoryLength);
        storyActText = sb.toString();



        qbChatDialog.setName(nameText + "-" + genreText + "*" + user + "&" + storyText + "#" + storyActText);

        wordsLeft = Integer.parseInt(storyText) - Integer.parseInt(storyActText) + userStoryLength;
        pagesLeft = Math.round(wordsLeft/500);

        QBDialogRequestBuilder requestBuilder = new QBDialogRequestBuilder();
        QBRestChatService.updateGroupChatDialog(qbChatDialog, requestBuilder)
                .performAsync(new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                        Log.d("DIALOGNAME", "Dialog name was changed");
                        //userStoryLength = spaceCount(); //storyEdit.length() - storyEdit.replaceAll(" ", "").length();
                    }

                    @Override
                    public void onError(QBResponseException e) {

                    }
                });

        sendPushNotification();
    }

    /*private void addPassedToDB() {
        DBHelper helper = new DBHelper(Story.this);
        helper.insertPassedStory(position);
    }*/

    private void addPostedToDB(String dialogID) {
        DBHelper helper = new DBHelper(Story.this);
        helper.insertPostedStory(dialogID);
    }

    private void addUser(){
        Intent intent = new Intent(Story.this, ListUsersActivity.class);
        intent.putExtra(Common.UPDATE_DIALOG_EXTRA,qbChatDialog);
        intent.putExtra(Common.UPDATE_MODE,Common.UPDATE_ADD_MODE);
        intent.putExtra("dialogID", dialogID);
        intent.putExtra("user", user);
        intent.putExtra("password", password);
        intent.putExtra("storyEdit", storyEdit);
        intent.putExtra("position", position);
        startActivity(intent);

    }


    private void retrieveStories() {
        QBMessageGetBuilder messageGetBuilder = new QBMessageGetBuilder();
        messageGetBuilder.setLimit(100000);

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

        namepos = qbChatDialog.getName().toString().lastIndexOf("-");
        genrepos = qbChatDialog.getName().toString().lastIndexOf("*");
        storyLength = qbChatDialog.getName().toString().lastIndexOf("&");
        storyActLength = qbChatDialog.getName().toString().lastIndexOf("#");
        nameText = qbChatDialog.getName().substring(0, namepos);
        genreText = qbChatDialog.getName().substring(namepos + 1, genrepos);
        whoIsNext = qbChatDialog.getName().substring(genrepos + 1, storyLength);
        storyText = qbChatDialog.getName().substring(storyLength + 1, storyActLength);
        storyActText = qbChatDialog.getName().substring(storyActLength + 1, qbChatDialog.getName().length());

        Log.d("STORYTEXT", "Story: " + storyText + " StoryAct: " + storyActText);

        int storyAct = Integer.parseInt(storyActText);
        int storyFull = Integer.parseInt(storyText);
        wordsLeft = storyFull - storyAct;
        double roundedupPagesLeft = wordsLeft / 500;
        pagesLeft = Math.round(roundedupPagesLeft);

        Log.d("STORYTEXT", "StoryAct: " + storyAct + " " + storyFull);

        if(storyAct >= storyFull){
            pass.setVisibility(View.GONE);
            post.setVisibility(View.GONE);
            textInputLayout.setVisibility(View.GONE);
            textCount.setVisibility(View.GONE);
            textMax.setVisibility(View.GONE);

            AlertDialog.Builder builder = new AlertDialog.Builder(Story.this);
            builder.setTitle("About story");
            builder.setMessage("This story has ended");


            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    // You don't have to do anything here if you just want it dismissed when clicked
                }
            });
            builder.show();
        }



            else if(whoIsNext.equals(user) && qbChatDialog.getType().toString().equals("GROUP")){
                pass.setVisibility(View.VISIBLE);
                post.setVisibility(View.INVISIBLE);
                textInputLayout.setVisibility(View.INVISIBLE);
                textCount.setVisibility(View.INVISIBLE);
                textMax.setVisibility(View.INVISIBLE);
            }

        else if(!currentUser.equals(whoIsNext) && qbChatDialog.getType().toString().equals("GROUP")) {
            pass.setVisibility(View.GONE);
            post.setVisibility(View.GONE);
            textInputLayout.setVisibility(View.GONE);
            textCount.setVisibility(View.GONE);
            textMax.setVisibility(View.GONE);
        }

        else if(currentUser.equals(whoIsNext) && qbChatDialog.getType().toString().equals("GROUP")){
            pass.setVisibility(View.GONE);
            post.setVisibility(View.VISIBLE);
            textInputLayout.setVisibility(View.VISIBLE);
            textCount.setVisibility(View.VISIBLE);
            textMax.setVisibility(View.VISIBLE);
        }

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
                Log.e("ERROR", ""+e.getMessage());
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
                Log.e("ERRORIN", "ERROR IN LOADING MESSAGE");
            }
        });




    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        if (Integer.parseInt(currentUser) != qbChatDialog.getUserId()) {
            menu.getItem(4).setVisible(false);
        }

        if(Integer.parseInt(storyActText) >= Integer.parseInt(storyText)){
            menu.getItem(4).setEnabled(false);
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.story_menu, menu);

        item = menu;
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(Story.this, theStories.class);
            intent.putExtra("title", story);
            intent.putExtra("genre", genre);
            intent.putExtra("user", user);
            intent.putExtra("password", password);
            startActivity(intent);
            finish();
        }

        if(item.getItemId() == R.id.profile){
            showUserProfile();
        }

        else if(item.getItemId() == R.id.start){
            Bundle args = new Bundle();
            args.putString("user", user);
            args.putString("password", password);


            DialogFragment dialog = new CreateDialogFragment();
            dialog.setArguments(args);
            dialog.show(getFragmentManager(), "CreateDialogFragment.tag");
        }

        else if(item.getItemId() == R.id.join){
            Intent intent = new Intent(Story.this, theStories.class);
            intent.putExtra("title", story);
            intent.putExtra("genre", genre);
            intent.putExtra("user", user);
            intent.putExtra("password", password);
            startActivity(intent);
            finish();
        }

        else if(item.getItemId() == R.id.about){
          openStoryDialog();
        }

        else if(item.getItemId() == R.id.endStory){
            endTheStory();
        }


        else if(item.getItemId() == R.id.menu_sign_out){
            logOut();
        }


        return super.onOptionsItemSelected(item);
    }


    private void endTheStory() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Story.this);
        builder.setTitle(nameText);
        builder.setMessage("Are you sure you want to end this story");
        // "Pass a start: " + passState);



        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                item.getItem(4).setEnabled(false);
                qbChatDialog.setName(nameText + "-" + genreText + "*" + user + "&" + storyText + "#" + (Integer.parseInt(storyText)));

                QBDialogRequestBuilder requestBuilder = new QBDialogRequestBuilder();
                QBRestChatService.updateGroupChatDialog(qbChatDialog, requestBuilder)
                        .performAsync(new QBEntityCallback<QBChatDialog>() {
                            @Override
                            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                                Log.d("DIALOGEND", "Dialog ended");
                                pass.setVisibility(View.GONE);
                                post.setVisibility(View.GONE);
                                textInputLayout.setVisibility(View.GONE);
                                textCount.setVisibility(View.GONE);
                                textMax.setVisibility(View.GONE);
                                Toast.makeText(Story.this, "Story ended successfully", Toast.LENGTH_SHORT).show();

                                //userStoryLength = spaceCount(); //storyEdit.length() - storyEdit.replaceAll(" ", "").length();
                            }

                            @Override
                            public void onError(QBResponseException e) {

                            }
                        });

            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        builder.show();

    }


    private void openStoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Story.this);
        builder.setTitle(nameText);
        builder.setMessage("Genre:     " + genreText + "\n" +
                "Pages left:     "  + pagesLeft + "\n" +
                "Words left:     "  + wordsLeft);


        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        builder.show();

    }


    private void showUserProfile() {
        Intent intent = new Intent(Story.this, UserProfile.class);
        intent.putExtra("user", user);
        intent.putExtra("password", password);
        startActivity(intent);
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
                        Toast.makeText(Story.this, "Signed out", Toast.LENGTH_SHORT).show();
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


        switch(item.getItemId()) {
            /*case R.id.chat_message_update_mesage:
                updateMessage();
                break;*/
            case R.id.chat_message_information:
                infoMessage();
                break;
            case R.id.chat_message_delete_message:

                deleteMessage();
                /*}

                else
                    Toast.makeText(this, "Only the story creator can delete", Toast.LENGTH_LONG).show();*/
                break;

        }

        return true;
    }


    private void infoMessage() {
        QBChatMessage storyMessage = new QBChatMessage();

        AlertDialog.Builder builder = new AlertDialog.Builder(Story.this);
        builder.setTitle("About writer");
        QBChatMessage userMsg = QBStoryMessageHolder.getInstance().getStoryMessageByDialogId(qbChatDialog.getDialogId())
                .get(contextMenuIndexClicked);


        builder.setMessage("Username: " + QBUsersHolder.getInstance().getUserById(userMsg.getSenderId()).getLogin());


        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        builder.show();
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


    private void centerTitle() {
        ArrayList<View> textViews = new ArrayList<>();

        getWindow().getDecorView().findViewsWithText(textViews, getTitle(), View.FIND_VIEWS_WITH_TEXT);

        if(textViews.size() > 0) {
            AppCompatTextView appCompatTextView = null;
            if(textViews.size() == 1) {
                appCompatTextView = (AppCompatTextView) textViews.get(0);
            } else {
                for(View v : textViews) {
                    if(v.getParent() instanceof Toolbar) {
                        appCompatTextView = (AppCompatTextView) v;
                        break;
                    }
                }
            }

            if(appCompatTextView != null) {
                ViewGroup.LayoutParams params = appCompatTextView.getLayoutParams();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                appCompatTextView.setLayoutParams(params);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    appCompatTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
            }
        }
    }


    private void createSessionForStory(){
        final QBUser qbUser = new QBUser(user, password);
        QBAuth.createSession(qbUser).performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {

            }

            @Override
            public void onError(QBResponseException e) {

            }
        });



    }


    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {


            StringBuilder sb = new StringBuilder(s.length());
            sb.append(s);
            String textLen = String.valueOf(sb.length());
            textCount.setText(textLen);
        }

        public void afterTextChanged(Editable s) {
        }
    };


    private void checkDialogName() {
        QBDialogRequestBuilder requestBuilder = new QBDialogRequestBuilder();
        QBRestChatService.updateGroupChatDialog(qbChatDialog, requestBuilder)
                .performAsync(new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {

                    }

                    @Override
                    public void onError(QBResponseException e) {

                    }
                });

    }


    private void sendPushNotification() {
        StringifyArrayList<Integer> userIds = new StringifyArrayList<Integer>();
        List<Integer> occupantsId = qbChatDialog.getOccupants();
        List<QBUser> usersinGroup = QBUsersHolder.getInstance().getUserByIds(occupantsId);



        for (int i = 0; i < usersinGroup.size(); i++) {

            if (!usersinGroup.get(i).getId().equals(Integer.valueOf(currentUser))) {
                userIds.add(usersinGroup.get(i).getId());
            }
        }


        if (usersinGroup.size() > 1) {
            QBEvent event = new QBEvent();
            event.setUserIds(userIds);
            event.setEnvironment(QBEnvironment.DEVELOPMENT);
            event.setNotificationType(QBNotificationType.PUSH);
            event.setPushType(QBPushType.GCM);
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("data.message", user + ": " + storyED.getText());

            data.put("data.type", "push notification");


            event.setMessage(data);
            //event.setName(user);

            QBPushNotifications.createEvent(event).performAsync(new QBEntityCallback<QBEvent>() {
                @Override
                public void onSuccess(QBEvent qbEvent, Bundle args) {
                    // sent
                }

                @Override
                public void onError(QBResponseException errors) {

                }
            });



        }

    }


    public int spaceCount(String word){
        String data[];
        int k=0;
        data=word.split("");
        for(int i=0;i<data.length;i++){
            if(data[i].equals(" ")){
                k++;
            }
        }
        return k + 1;
    }


    public void nostories(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Story.this);
        builder.setTitle("No local stories");
        builder.setMessage("As there are not enough people signed up to \"Lets tell a story\" there are currently no local stories to join but please feel free to start your own story, sorry for the inconvenience.");


        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                // You don't have to do anything here if you just want it dismissed when clicked
            }
        });
        builder.show();
    }

}

