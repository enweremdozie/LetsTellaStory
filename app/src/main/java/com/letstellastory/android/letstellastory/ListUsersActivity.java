package com.letstellastory.android.letstellastory;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.letstellastory.android.letstellastory.Common.Common;
import com.letstellastory.android.letstellastory.Holder.QBUsersHolder;
import com.letstellastory.android.letstellastory.adapter.ListUsersAdapter;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.request.QBDialogRequestBuilder;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ListUsersActivity extends AppCompatActivity {

    ListView lstUsers;
    Button btnPass;
    Story story = new Story();
    int hasclicked, namepos, genrepos;
    //public boolean clicked = false;
    boolean passstate = false;
    QBUser currentUser;


    String mode = "";
    String dialogID, storyEdit, whoIsNext;
    QBChatDialog qbChatDialog;
    List<QBUser> userAdd = new ArrayList<>();
    TextView passedButton;
    String user, password, name, genre, nameText, genreText, storyText, storyActText;
    QBUser qbuser = new QBUser();
    boolean userInStory;
    int posClicked, storyLength, storyActLength;

    @Override
    protected void onRestart() {
        super.onRestart();
        QBChatService.getInstance().setReconnectionAllowed(true);
        createSessionForStory();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users);

        QBChatService.getInstance().setReconnectionAllowed(true);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        setTitle("USERS");
        centerTitle();

        mode = getIntent().getStringExtra(Common.UPDATE_MODE);
        qbChatDialog = (QBChatDialog) getIntent().getSerializableExtra(Common.UPDATE_DIALOG_EXTRA);

        namepos = qbChatDialog.getName().toString().lastIndexOf("-");
        genrepos = qbChatDialog.getName().toString().lastIndexOf("*");
        storyLength = qbChatDialog.getName().toString().lastIndexOf("&");
        storyActLength = qbChatDialog.getName().toString().lastIndexOf("#");
        nameText = qbChatDialog.getName().substring(0, namepos);
        genreText = qbChatDialog.getName().substring(namepos + 1, genrepos);
        whoIsNext = qbChatDialog.getName().substring(genrepos + 1, storyLength);
        storyText = qbChatDialog.getName().substring(storyLength + 1, storyActLength);
        storyActText = qbChatDialog.getName().substring(storyActLength + 1, qbChatDialog.getName().length());

        Log.d("WHOISNEXT", "who is next in LUA: " + whoIsNext);


        Intent intent = getIntent();
        dialogID = intent.getExtras().getString("dialogID");
        user = intent.getExtras().getString("user");
        password = intent.getExtras().getString("password");
        storyEdit = intent.getExtras().getString("storyEdit");

        Log.d("DIALOGID", "Dialog ID in LUA: " + dialogID);
        lstUsers = (ListView) findViewById(R.id.lstUsers);

        listviewclicked();

        qbuser = (QBUser) lstUsers.getItemAtPosition(posClicked);//user being passed to

        btnPass = (Button) findViewById(R.id.btn_pass_story);

        btnPass.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //addPassedToDB(dialogID);
                qbuser = (QBUser) lstUsers.getItemAtPosition(posClicked);//user being passed to

                /*if(!whoIsNext.equals("1")){
                    Toast.makeText(story, "sorry it is not your turn", Toast.LENGTH_SHORT).show();
                }
                else */



                if (userInStory == true) {
                    sendPushNotification();
                }

                else if (userInStory == false) {
                    //hasclicked = listviewclicked();

                    if (mode.equals(Common.UPDATE_ADD_MODE) && qbChatDialog != null && passstate) {

                        if (userAdd.size() > 0) {
                            QBDialogRequestBuilder requestBuilder = new QBDialogRequestBuilder();

                            int cntChoice = lstUsers.getCount();
                            //SparseBooleanArray checkItemPositions = lstUsers.getCheckedItemPositions();
                            //for(int i = 0; i < cntChoice; i++){
                            //if(checkItemPositions.get(i))
                            //{


                            requestBuilder.addUsers(qbuser);
                            Log.d("PASSEDTO", "being passed to: " + qbuser.getLogin());
                            Log.d("PASSEDTO", "hasclicked position: " + hasclicked);

                            // }
                            // }

                            QBRestChatService.updateGroupChatDialog(qbChatDialog, requestBuilder)
                                    .performAsync(new QBEntityCallback<QBChatDialog>() {
                                        @Override
                                        public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {

                                            Toast.makeText(ListUsersActivity.this, "PASS successful", Toast.LENGTH_SHORT).show();
                                            sendToRecepient();
                                        }

                                        @Override
                                        public void onError(QBResponseException e) {

                                        }
                                    });
                        }
                    }

                    else if (mode.equals(Common.UPDATE_REMOVE_MODE) && qbChatDialog != null) {
                        if (userAdd.size() > 0) {
                            QBDialogRequestBuilder requestBuilder = new QBDialogRequestBuilder();
                            int cntChoice = lstUsers.getCount();
                            SparseBooleanArray checkItemPositions = lstUsers.getCheckedItemPositions();

                            for (int i = 0; i < cntChoice; i++) {
                                if (checkItemPositions.get(i)) {
                                    QBUser user = (QBUser) lstUsers.getItemAtPosition(i);
                                    requestBuilder.removeUsers(user);
                                }
                            }

                            QBRestChatService.updateGroupChatDialog(qbChatDialog, requestBuilder)
                                    .performAsync(new QBEntityCallback<QBChatDialog>() {
                                        @Override
                                        public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                                            Toast.makeText(ListUsersActivity.this, "REMOVAL successful", Toast.LENGTH_SHORT).show();
                                            //finish();
                                        }

                                        @Override
                                        public void onError(QBResponseException e) {

                                        }
                                    });
                        }
                    }

                    else {
                        Toast.makeText(ListUsersActivity.this, "Please make a selection", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        if (mode == null && qbChatDialog == null) {
            //retrieveAllUsers();
        } else {
            if (mode.equals(Common.UPDATE_ADD_MODE)) {
                retrieveAllUsers();

                //loadListAvailableUser();
            } else if (mode.equals(Common.UPDATE_REMOVE_MODE)) {
                loadListUserInGroup();
            }
        }
    }


    private void loadListUserInGroup() {
        QBRestChatService.getChatDialogById(qbChatDialog.getDialogId())
                .performAsync(new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                        List<Integer> occupantsId = qbChatDialog.getOccupants();
                        List<QBUser> listUserAlreadyInGroup = QBUsersHolder.getInstance().getUserByIds(occupantsId);
                        ArrayList<QBUser> users = new ArrayList<QBUser>();
                        users.addAll(listUserAlreadyInGroup);

                        ListUsersAdapter adapter = new ListUsersAdapter(getBaseContext(), users);
                        lstUsers.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        userAdd = users;
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Toast.makeText(ListUsersActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadListAvailableUser() {
        QBRestChatService.getChatDialogById(qbChatDialog.getDialogId())
                .performAsync(new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                        ArrayList<QBUser> listUsers = QBUsersHolder.getInstance().getAllUsers();
                        List<Integer> occupantsId = qbChatDialog.getOccupants();
                        List<QBUser> listUserAlreadyInChatGroup = QBUsersHolder.getInstance().getUserByIds(occupantsId);

                        for (QBUser user : listUserAlreadyInChatGroup) {
                            listUsers.remove(user);
                        }
                        if (listUsers.size() > 0) {
                            ListUsersAdapter adapter = new ListUsersAdapter(getBaseContext(), listUsers);
                            lstUsers.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            userAdd = listUsers;
                            // QBDialogRequestBuilder requestBuilder = new QBDialogRequestBuilder();
                            // requestBuilder.addUsers(userAdd.get(0));
                        }
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Toast.makeText(ListUsersActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void passStory(int checkedItemPositions) {


        int countChoice = lstUsers.getCount();
        //ArrayList<Integer> occupantIdsList = new ArrayList<>();

        for (int i = 0; i < countChoice; i++) {
            if (checkedItemPositions >= 0) {
                QBUser user = (QBUser) lstUsers.getItemAtPosition(i);
                //occupantIdsList.add(user.getId());

            }
        }


    }

    //Right here for adding users
    private void retrieveAllUsers() {

        QBUsers.getUsers(null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            //Log.d("CREATION", "this is the users fileID " + user.getFileId());

            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                Log.d("CREATION", "inside retrieve");

                QBUsersHolder.getInstance().putUsers(qbUsers);
                ArrayList<QBUser> qbUserWithoutCurrent = new ArrayList<QBUser>();

                for (QBUser user : qbUsers) {
                    if (!user.getLogin().equals(QBChatService.getInstance().getUser().getLogin()))
                        qbUserWithoutCurrent.add(user);
                    Log.d("CREATION", "this is the user " + user.toString());
                }

                for (int i = 0; i < qbUserWithoutCurrent.size(); i++) {
                    if (qbUserWithoutCurrent.get(i) == currentUser) {
                        qbUserWithoutCurrent.remove(i);
                    }
                }

                ListUsersAdapter adapter = new ListUsersAdapter(getBaseContext(), qbUserWithoutCurrent);
                lstUsers.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                userAdd = qbUserWithoutCurrent;
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("ERROR", e.getMessage());
            }
        });
    }

    private void addPassedToDB(String dialogID) {
        DBHelper helper = new DBHelper(ListUsersActivity.this);
        helper.insertPassedStory(dialogID);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            finish();
        }
        return super.onOptionsItemSelected(item);
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

    public void listviewclicked() {
        final int[] pos = new int[1];
        final boolean[] check = {false};
        lstUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long id) {
                passstate = true;
                for (int i = 0; i < lstUsers.getChildCount(); i++) {
                    if (position == i) {
                        posClicked = position;
                        userInStory = isInStory();
                        lstUsers.getChildAt(i).setBackgroundColor(Color.LTGRAY);
                    }

                    else {
                        check[0] = false;
                        lstUsers.getChildAt(i).setBackgroundColor(Color.WHITE);
                    }
                }

            }
        });
    }

    public void sendToRecepient() {

        final ProgressDialog mDialog = new ProgressDialog(ListUsersActivity.this);
        mDialog.setMessage("Please wait...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        QBRestChatService.getChatDialogById(dialogID).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                mDialog.dismiss();
                QBSystemMessagesManager qbSystemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();
                QBChatMessage qbChatMessage = new QBChatMessage();
                qbChatMessage.setBody(qbChatDialog.getDialogId());

                qbChatMessage.setRecipientId(qbuser.getId());

                Log.d("RECEPIENT", "the recepient: " + qbuser.getId());

                try {
                    qbSystemMessagesManager.sendSystemMessage(qbChatMessage);
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("ERROR", e.getMessage());
            }
        });

        sendPushNotification();


    }

    private void sendPushNotification() {
        StringifyArrayList<Integer> userId = new StringifyArrayList<Integer>();


        userId.add(qbuser.getId());
        QBEvent event = new QBEvent();
        event.setUserIds(userId);
        event.setEnvironment(QBEnvironment.DEVELOPMENT);
        event.setNotificationType(QBNotificationType.PUSH);
        event.setPushType(QBPushType.GCM);
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("data.message", "A story has been passed to you");
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

        changeDialogName();

    }

    private void changeDialogName() {

        qbChatDialog.setName(nameText + "-" + genreText + "*" + qbuser.getId().toString() + "&" + storyText + "#" + storyActText);

        QBDialogRequestBuilder requestBuilder = new QBDialogRequestBuilder();
        QBRestChatService.updateGroupChatDialog(qbChatDialog, requestBuilder)
                .performAsync(new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                        Log.d("DIALOGNAME", "Dialog name was changed");


                        Story story = new Story();
                        story.canPassState = false;
                        finish();
                    }

                    @Override
                    public void onError(QBResponseException e) {

                    }
                });


        mode = getIntent().getStringExtra(Common.UPDATE_MODE);
        qbChatDialog = (QBChatDialog) getIntent().getSerializableExtra(Common.UPDATE_DIALOG_EXTRA);

        namepos = qbChatDialog.getName().toString().lastIndexOf("-");
        genrepos = qbChatDialog.getName().toString().lastIndexOf("*");
        storyLength = qbChatDialog.getName().toString().lastIndexOf("&");
        storyActLength = qbChatDialog.getName().toString().lastIndexOf("#");
        nameText = qbChatDialog.getName().substring(0, namepos);
        genreText = qbChatDialog.getName().substring(namepos + 1, genrepos);
        whoIsNext = qbChatDialog.getName().substring(genrepos + 1, storyLength);
        storyText = qbChatDialog.getName().substring(storyLength + 1, storyActLength);
        storyActText = qbChatDialog.getName().substring(storyActLength + 1, qbChatDialog.getName().length());


        //Log.d("WHOISNEXT", "who is next in LUA on click: " + whoIsNext);

    }

    public boolean isInStory() {
        boolean state = false;
        List<Integer> occupants;

        //occupants = qbChatDialog.getOccupants();
        qbuser = (QBUser) lstUsers.getItemAtPosition(posClicked);//user being passed to

        List<Integer> occupantsId = qbChatDialog.getOccupants();
        List<QBUser> listUserAlreadyInGroup = QBUsersHolder.getInstance().getUserByIds(occupantsId);
        ArrayList<QBUser> users = new ArrayList<QBUser>();
        users.addAll(listUserAlreadyInGroup);

        for (int i = 0; i < users.size(); i++) {
            Log.d("CHECKID", "any qbuserID " + users.get(i).getId().toString());
        }


        Log.d("CHECKID", "qbuserID " + qbuser.getId().toString());


        for (int i = 0; i < users.size(); i++) {
            if (state == false) {
                if (qbuser.getId().equals(users.get(i).getId())) {
                    state = true;
                } else {
                    state = false;
                }
            }
        }
        return state;
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

}