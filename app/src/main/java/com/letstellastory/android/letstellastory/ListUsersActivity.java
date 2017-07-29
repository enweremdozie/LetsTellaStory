package com.letstellastory.android.letstellastory;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.letstellastory.android.letstellastory.Common.Common;
import com.letstellastory.android.letstellastory.Holder.QBUsersHolder;
import com.letstellastory.android.letstellastory.adapter.ListUsersAdapter;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.BaseService;
import com.quickblox.auth.session.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.request.QBDialogRequestBuilder;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.SmackException;

import java.util.ArrayList;
import java.util.List;

public class ListUsersActivity extends AppCompatActivity {

    ListView lstUsers;
    Button btnPass;
    Story story = new Story();
    //public boolean clicked = false;


    String mode = "";
    String dialogID;
    QBChatDialog qbChatDialog;
    List<QBUser> userAdd = new ArrayList<>();
    TextView passedButton;
    String user, password;

    @Override
    protected void onRestart() {
        super.onRestart();
        createSessionForStory();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mode = getIntent().getStringExtra(Common.UPDATE_MODE);
        qbChatDialog = (QBChatDialog) getIntent().getSerializableExtra(Common.UPDATE_DIALOG_EXTRA);


        Intent intent = getIntent();
        dialogID = intent.getExtras().getString("dialogID");
        user = intent.getExtras().getString("user");
        password = intent.getExtras().getString("password");

        Log.d("DIALOGID", "Dialog ID in LUA: " + dialogID);
        lstUsers = (ListView)findViewById(R.id.lstUsers);
        lstUsers.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        btnPass = (Button)findViewById(R.id.btn_pass_story);

        btnPass.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                addPassedToDB(dialogID);
                //passedButton.setVisibility(View.GONE);
                //story.isPassed();
                //passedButton.setVisibility(View.GONE);
                //story.pass.setVisibility(View.GONE);
                if(mode == null) {
                    int countChoice = lstUsers.getCount();

                    if (lstUsers.getCheckedItemPositions().size() == 1) {
                        passStory(lstUsers.getCheckedItemPositions());
                        //loadListAvailableUser();                      stopped here

                    } else if (lstUsers.getCheckedItemPositions().size() > 1) {
                        Toast.makeText(ListUsersActivity.this, "Select only one friend", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ListUsersActivity.this, "Please make a selection", Toast.LENGTH_SHORT).show();
                    }
                }

                else if(mode.equals(Common.UPDATE_ADD_MODE) && qbChatDialog != null){
                    if(userAdd.size() > 0){
                        QBDialogRequestBuilder requestBuilder = new QBDialogRequestBuilder();

                        int cntChoice = lstUsers.getCount();
                        SparseBooleanArray checkItemPositions = lstUsers.getCheckedItemPositions();
                        for(int i = 0; i < cntChoice; i++){
                            if(checkItemPositions.get(i))
                            {
                                QBUser user = (QBUser)lstUsers.getItemAtPosition(i);
                                requestBuilder.addUsers(user);
                            }
                        }

                        QBRestChatService.updateGroupChatDialog(qbChatDialog, requestBuilder)
                                .performAsync(new QBEntityCallback<QBChatDialog>() {
                                    @Override
                                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                                        passStory(lstUsers.getCheckedItemPositions());
                                        Toast.makeText(ListUsersActivity.this, "PASS successful", Toast.LENGTH_SHORT).show();

                                    }

                                    @Override
                                    public void onError(QBResponseException e) {

                                    }
                                });
                    }
                }

                else if(mode.equals(Common.UPDATE_REMOVE_MODE) && qbChatDialog != null){
                    if(userAdd.size() > 0){
                        QBDialogRequestBuilder requestBuilder = new QBDialogRequestBuilder();
                        int cntChoice = lstUsers.getCount();
                        SparseBooleanArray checkItemPositions = lstUsers.getCheckedItemPositions();

                        for(int i = 0; i < cntChoice; i++){
                            if(checkItemPositions.get(i))
                            {
                                QBUser user = (QBUser)lstUsers.getItemAtPosition(i);
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
            }
        });

        if(mode == null && qbChatDialog == null){
            retrieveAllUsers();
        }
        else{
            if(mode.equals(Common.UPDATE_ADD_MODE)){
                loadListAvailableUser();
            }
            else if(mode.equals(Common.UPDATE_REMOVE_MODE)){
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
                        Toast.makeText(ListUsersActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadListAvailableUser() {
        QBRestChatService.getChatDialogById(qbChatDialog.getDialogId())
                .performAsync(new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                        ArrayList<QBUser>  listUsers = QBUsersHolder.getInstance().getAllUsers();
                        List<Integer> occupantsId = qbChatDialog.getOccupants();
                        List<QBUser> listUserAlreadyInChatGroup = QBUsersHolder.getInstance().getUserByIds(occupantsId);

                        for(QBUser user: listUserAlreadyInChatGroup){
                            listUsers.remove(user);
                        }
                        if(listUsers.size() > 0){
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
                        Toast.makeText(ListUsersActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void passStory(SparseBooleanArray checkedItemPositions) {

        final ProgressDialog mDialog = new ProgressDialog(ListUsersActivity.this);
        mDialog.setMessage("Please wait...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        int countChoice = lstUsers.getCount();
        //ArrayList<Integer> occupantIdsList = new ArrayList<>();

        for(int i = 0; i < countChoice; i++){
            if(checkedItemPositions.get(i)){
                QBUser user = (QBUser) lstUsers.getItemAtPosition(i);
                //occupantIdsList.add(user.getId());

            }

        }

        QBRestChatService.getChatDialogById(dialogID).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                Toast.makeText(getBaseContext(),"Pass successful", Toast.LENGTH_SHORT).show();
                mDialog.dismiss();
                QBSystemMessagesManager qbSystemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();
                QBChatMessage qbChatMessage = new QBChatMessage();
                qbChatMessage.setBody((qbChatDialog.getDialogId()));

                for(int i = 0; i < qbChatDialog.getOccupants().size(); i++) {
                    qbChatMessage.setRecipientId(qbChatDialog.getOccupants().get(i));
                    try {
                        qbSystemMessagesManager.sendSystemMessage(qbChatMessage);
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("ERROR", e.getMessage());
            }
        });

        finish();
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

                for(QBUser user : qbUsers){
                    if(!user.getLogin().equals(QBChatService.getInstance().getUser().getLogin()));
                    qbUserWithoutCurrent.add(user);
                    Log.d("CREATION", "this is the user " + user.toString());
                }

                ListUsersAdapter adapter= new ListUsersAdapter(getBaseContext(), qbUserWithoutCurrent);
                lstUsers.setAdapter(adapter);
                adapter.notifyDataSetChanged();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
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
    }
}