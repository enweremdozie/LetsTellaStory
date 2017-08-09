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
import com.quickblox.auth.session.BaseService;
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
import com.quickblox.chat.request.QBMessageUpdateBuilder;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.BaseServiceException;
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
    String ActTitle, genre, story, user, password, dialogID, storyEdit, nameText, genreText, whoIsNext, currentUser;
    long storyTime;
    StoryMessageAdapter adapter;
    int position, passed, namepos, genrepos;
    int contextMenuIndexClicked = -1;
    boolean isEditMode = false;
    QBChatMessage editMessage;

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
    //ListUsersActivity list = new ListUsersActivity();


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
        /*retrieveStories();
        //createSessionForStory();
        initStoryDialogs();*/

        if(canPassState == false) {

            //canPassState = false;
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
        //createSessionForStory();
        /*QBUsers.signIn(qbuser).performAsync( new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser user, Bundle args) {
                // success
            }

            @Override
            public void onError(QBResponseException error) {
                // error
            }
        });*/
        //initStoryDialogs();

        //retrieveStories();
        //createSessionForStory();
        /*DBHelper helper = new DBHelper(Story.this);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = helper.getPassedStoryInfo(db);
        boolean state = false;
        String dialog;
        if(cursor.getCount() == 0){
            state = false;
        }

        else if (cursor.getCount() > 0){
            while(cursor.moveToNext() && state != true){
                dialog = cursor.getString(1);

                if(dialogID.equals(dialog)){
                    state = true;
                }

                else{
                    state = false;
                }
            }
        }

        if(state == true){
            pass.setVisibility(View.GONE);
            post.setVisibility(View.GONE);
            textInputLayout.setVisibility(View.GONE);
            textCount.setVisibility(View.GONE);
            textMax.setVisibility(View.GONE);

        }*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        //db = new DBHelper(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


        //Log.d("CREATION" , "DIALOG ID in story: " + qbChatDialog.getDialogId());
        lstStoryMessages = (ListView) findViewById(R.id.storyList);
        post = (TextView) findViewById(R.id.postStory);
        pass = (TextView) findViewById(R.id.passStory);
        storyED = (EditText) findViewById(R.id.storyEdit);
        textCount = (TextView) findViewById(R.id.current_amount);
        textMax = (TextView) findViewById(R.id.textMax);
        textInputLayout = (TextInputLayout) findViewById(R.id.editLayout);
        registerForContextMenu(lstStoryMessages);
        canPassState = true;




        storyED.addTextChangedListener(mTextEditorWatcher);
        //show = (TextView) findViewById(R.id.showAll);

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
        Log.d("CURRENTUSER1" , "current user in story" + currentUser);
        qbuser = new QBUser(user, password);

        //createSessionForStory();
        //Log.d("CREATION", "position in Story " + position);
        setTitle(ActTitle);
        centerTitle();
        //AddData();
        //createSessionForStory();
        /*if(hasposted == true){
            post.setVisibility(View.INVISIBLE);
            pass.setVisibility(View.VISIBLE);
        }

        if(haspassed == true){
            pass.setVisibility(View.GONE);
            post.setVisibility(View.GONE);
            textInputLayout.setVisibility(View.GONE);
            textCount.setVisibility(View.GONE);
            textMax.setVisibility(View.GONE);
        }*/


            /*if(userID != whoIsNextUserID) {
                pass.setVisibility(View.GONE);
                post.setVisibility(View.GONE);
                textInputLayout.setVisibility(View.GONE);
                textCount.setVisibility(View.GONE);
                textMax.setVisibility(View.GONE);
            }

            else if(userID == whoIsNextUserID){
                pass.setVisibility(View.GONE);
                post.setVisibility(View.VISIBLE);
                textInputLayout.setVisibility(View.VISIBLE);
                textCount.setVisibility(View.VISIBLE);
                textMax.setVisibility(View.VISIBLE);
            }*/
        //createSessionForStory();
        initStoryDialogs();
            retrieveStories();

        storyED.setFocusable(true);
            post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Log.d("ENTERSHERE", "enters once");



                    //Log.d("CHANGEPOST", "QB dialog id " + qbChatDialog.getDialogId());
                storyEdit = storyED.getText().toString();
                //addPostedToDB(dialogID);
                    if (!storyEdit.equals(null) && storyED.getText().toString().trim().length() > 0) {

                        /*if (!isEditMode) {
                            QBChatMessage storyMessage = new QBChatMessage();
                            storyMessage.setBody(storyED.getText().toString());
                            storyMessage.setSenderId(QBChatService.getInstance().getUser().getId());
                            storyMessage.setSaveToHistory(true);

                            try {
                                qbChatDialog.sendMessage(storyMessage);
                            }
                            catch (SmackException.NotConnectedException e) {
                                e.printStackTrace();
                            }

                            storyED.setText("");
                            storyED.setFocusable(true);
                        }

                        else {*/
                            //if (!isEditMode) {
                            QBChatMessage storyMessage = new QBChatMessage();
                            storyMessage.setBody(storyEdit);
                            storyMessage.setSenderId(QBChatService.getInstance().getUser().getId());
                            storyMessage.setSaveToHistory(true);

                            try {
                                qbChatDialog.sendMessage(storyMessage);
                            }
                            catch (SmackException.NotConnectedException e) {
                                e.printStackTrace();
                            }

                            /*storyED.setText("");
                            storyED.setFocusable(true);*/

                            /*final ProgressDialog updateDialog = new ProgressDialog(Story.this);
                            updateDialog.setMessage("Please wait...");
                            updateDialog.show();*/

                            QBMessageUpdateBuilder messageUpdateBuilder = new QBMessageUpdateBuilder();
                            messageUpdateBuilder.updateText(storyEdit).markDelivered().markRead();

                        Log.d("MESSAGEERROR", storyMessage.getId() + "   " + qbChatDialog.getDialogId());

                            QBRestChatService.updateMessage(storyMessage.getId(), qbChatDialog.getDialogId(), messageUpdateBuilder)
                                    .performAsync(new QBEntityCallback<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid, Bundle bundle) {
                                            changeDialogName();
                                            pass.setVisibility(View.VISIBLE);
                                            post.setVisibility(View.INVISIBLE);
                                            textInputLayout.setVisibility(View.INVISIBLE);
                                            textCount.setVisibility(View.INVISIBLE);
                                            textMax.setVisibility(View.INVISIBLE);
                                            Toast.makeText(Story.this, "posting", Toast.LENGTH_SHORT).show();

                                            retrieveStories();
                                            initStoryDialogs();


                                            //isEditMode = false;
                                            //updateDialog.dismiss();

                                            storyED.setText("");
                                            storyED.setFocusable(false);
                                        }

                                        @Override
                                        public void onError(QBResponseException e) {
                                            Toast.makeText(getBaseContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                       // }
                    }

                    else {
                        Toast.makeText(Story.this, "\"What happens next\" cannot be empty", Toast.LENGTH_LONG).show();
                    }

                    //push notification

                    QBSettings.getInstance().setEnablePushNotification(true);
                   /* BroadcastReceiver pushBroadcastReceiver = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            String message = intent.getStringExtra("message");
                            String from = intent.getStringExtra("from");
                            Log.i("CREATION", "Receiving message: " + message + ", from " + from);
                        }
                    };

                    LocalBroadcastManager.getInstance(getBaseContext()).registerReceiver(pushBroadcastReceiver,
                            new IntentFilter("new-push-event"));*/





            }
            });


            pass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(canPassState == false){
                        Toast.makeText(Story.this, "sorry it is not your turn", Toast.LENGTH_LONG).show();

                    }

                    else {
                        addUser();
                    }
                }
            });







    }

    private void changeDialogName() {
        qbChatDialog.setName(nameText + "-" + genreText + "*" + user);
        QBDialogRequestBuilder requestBuilder = new QBDialogRequestBuilder();
        QBRestChatService.updateGroupChatDialog(qbChatDialog, requestBuilder)
                .performAsync(new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                        Log.d("DIALOGNAME", "Dialog name was changed");
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
        //Log.d("CREATION", "position in Story " + pos);
        intent.putExtra("position", position);
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

        namepos = qbChatDialog.getName().toString().lastIndexOf("-");
        genrepos = qbChatDialog.getName().toString().lastIndexOf("*");
        nameText = qbChatDialog.getName().substring(0, namepos);
        genreText = qbChatDialog.getName().substring(namepos + 1, genrepos);
        whoIsNext = qbChatDialog.getName().substring(genrepos + 1, qbChatDialog.getName().length());
        //whoIsNextUserID = Integer.valueOf(whoIsNext).toString();

        //userID = Integer.valueOf(currentUser);

        Log.d("USERID2", "dialog ID: " + whoIsNextUserID);

            if(whoIsNext.equals(user)){
                pass.setVisibility(View.VISIBLE);
                post.setVisibility(View.INVISIBLE);
                textInputLayout.setVisibility(View.INVISIBLE);
                textCount.setVisibility(View.INVISIBLE);
                textMax.setVisibility(View.INVISIBLE);
            }

        else if(!currentUser.equals(whoIsNext)) {
            pass.setVisibility(View.GONE);
            post.setVisibility(View.GONE);
            textInputLayout.setVisibility(View.GONE);
            textCount.setVisibility(View.GONE);
            textMax.setVisibility(View.GONE);
        }

        else if(currentUser.equals(whoIsNext)){
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
                //Log.e("ERRORIN", messages.get(0).toString());
                adapter = new StoryMessageAdapter(getBaseContext(), messages);
                lstStoryMessages.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {
                Log.e("ERRORIN", "ERROR IN LOADING MESSAGE");
            }
        });

        //Log.d("USERID1", "userID is " + userID);
        //Log.d("USERID1", "who is next is " + whoIsNextUserID);


    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.story_menu, menu);
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
            /*args.putString("story", story);
            args.putString("genre", genre);*/

            DialogFragment dialog = new CreateDialogFragment();
            dialog.setArguments(args);
            dialog.show(getFragmentManager(), "CreateDialogFragment.tag");
        }

        else if(item.getItemId() == R.id.join){

        }

        /*else if(item.getItemId() == R.id.my_stories){
            Intent intent = new Intent(Story.this,theStories.class);
            startActivity(intent);
        }*/
        else if(item.getItemId() == R.id.menu_sign_out){
            logOut();
        }
        return super.onOptionsItemSelected(item);
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
                //setSenderId(QBChatService.getInstance().getUser().getId());
        AlertDialog.Builder builder = new AlertDialog.Builder(Story.this);
        builder.setTitle("About writer");
        QBChatMessage userMsg = QBStoryMessageHolder.getInstance().getStoryMessageByDialogId(qbChatDialog.getDialogId())
                .get(contextMenuIndexClicked);

        //builder.setMessage("User name: " + userMsg.getSenderId());

        builder.setMessage("Username: " + QBUsersHolder.getInstance().getUserById(userMsg.getSenderId()).getLogin());


        builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
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
                //qbChatDialog.getUserId()
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
        //Log.d("CREATION", "in story fragment password is " + password);
        QBAuth.createSession(qbUser).performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {
                qbUser.setId(qbSession.getUserId());
                Log.d("USERID1", "userID session " + qbSession.getUserId());

                //userID = qbUser.getId();
                //Log.d("USERID1", "userID init " + userID);
                try {
                    qbUser.setPassword(BaseService.getBaseService().getToken());
                } catch (BaseServiceException e) {
                    e.printStackTrace();
                }

                QBChatService.getInstance().login(qbUser, new QBEntityCallback() {
                    @Override
                    public void onSuccess(Object o, Bundle bundle) {
                        //mDialog.dismiss();
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
        /*qbChatDialog = (QBChatDialog)getIntent().getSerializableExtra(Common.DIALOG_EXTRA);
        qbChatDialog.initForChat(QBChatService.getInstance());*/


    }

    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //This sets a textview to the current length
            //storyED.setText(String.valueOf(s.length()));
           // Log.d("EDITTEXTLENGTH", "Text length: " + s.length());

            StringBuilder sb = new StringBuilder(s.length());
            sb.append(s);
            String textLen = String.valueOf(sb.length());
            textCount.setText(textLen);
        }

        public void afterTextChanged(Editable s) {
        }
    };



    private void checkDialogName() {
        //qbChatDialog.setName(name + "-" + genre + "*" + qbuser.getId().toString());
        QBDialogRequestBuilder requestBuilder = new QBDialogRequestBuilder();
        QBRestChatService.updateGroupChatDialog(qbChatDialog, requestBuilder)
                .performAsync(new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                        //Log.d("DIALOGNAME", "Dialog name was changed");
                        //finish();
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

        /*for (int i = 0; i < occupantsId.size(); i++) {

                //userIds.add(usersinGroup.get(i).getId());
                Log.d("OCCUPANTSID", "occupants id: " + occupantsId.get(i).toString());

        }*/

        for (int i = 0; i < usersinGroup.size(); i++) {

            if (!usersinGroup.get(i).getId().equals(Integer.valueOf(currentUser))) {
                userIds.add(usersinGroup.get(i).getId());
                //Log.d("PUSHNOT1", userIds.get(i).toString());
            }
        }


        //userIds.add(qbuser.getId());
        if (usersinGroup.size() > 1) {
            QBEvent event = new QBEvent();
            event.setUserIds(userIds);
            event.setEnvironment(QBEnvironment.DEVELOPMENT);
            event.setNotificationType(QBNotificationType.PUSH);
            event.setPushType(QBPushType.GCM);
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("data.message", user + ": " + storyED.getText());
            //data.put("data.from", user);

            data.put("data.type", "push notification");
            //

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

            //changeDialogName();

        }

    }

}

