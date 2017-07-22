package com.letstellastory.android.letstellastory;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.letstellastory.android.letstellastory.Common.Common;
import com.letstellastory.android.letstellastory.Holder.QBUsersHolder;
import com.letstellastory.android.letstellastory.adapter.ListUsersAdapter;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.request.QBDialogRequestBuilder;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.SmackException;

import java.util.ArrayList;
import java.util.List;

public class ListUsersActivity extends AppCompatActivity {

    ListView lstUsers;
    Button btnPass;


    String mode ="";
    QBChatDialog qbChatDialog;
    ArrayList<QBUser> userAdd = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users);

        mode = getIntent().getStringExtra(Common.UPDATE_MODE);
        qbChatDialog = (QBChatDialog) getIntent().getSerializableExtra(Common.UPDATE_DIALOG_EXTRA);



        lstUsers = (ListView)findViewById(R.id.lstUsers);
        lstUsers.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        btnPass = (Button)findViewById(R.id.btn_pass_story);

        btnPass.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                int countChoice = lstUsers.getCount();

                if(lstUsers.getCheckedItemPositions().size() == 1){
                    passStory(lstUsers.getCheckedItemPositions());
                    //loadListAvailableUser();                      stopped here

                }
                else if (lstUsers.getCheckedItemPositions().size() > 1){
                    Toast.makeText(ListUsersActivity.this, "Select only one friend", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(ListUsersActivity.this, "Please Select a friend", Toast.LENGTH_SHORT).show();
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

                            QBDialogRequestBuilder requestBuilder = new QBDialogRequestBuilder();
                            requestBuilder.addUsers(userAdd.get(0));
                        }
                    }

                    @Override
                    public void onError(QBResponseException e) {

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

        QBChatDialog dialog = new QBChatDialog();
        //dialog.setName(Common.createChatDialogName(occupantIdsList));
        dialog.setType(QBDialogType.GROUP);
        //dialog.setOccupantsIds(occupantIdsList);

        QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                mDialog.dismiss();
                Toast.makeText(getBaseContext(),"Pass successful", Toast.LENGTH_SHORT).show();

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




                finish();
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("ERROR", e.getMessage());
            }
        });
    }

    //Right here for adding users
    private void retrieveAllUsers() {

        QBUsers.getUsers(null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {

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
}
