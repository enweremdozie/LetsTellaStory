package com.letstellastory.android.letstellastory;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.letstellastory.android.letstellastory.Holder.QBUsersHolder;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.BaseService;
import com.quickblox.auth.session.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.utils.DialogUtils;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

public class StartAStory extends AppCompatActivity {
    TextView cancel;
    TextView okay;
    EditText storyName;
    String et;
    RadioGroup genre;
    RadioGroup length;
    RadioButton genreDisplay;
    RadioButton storyLength;
    CheckBox pass;
    String passState;
    TextView show;
    String user, password, currentUser;
    String lenOfStory = "25000";
    Integer userID;

    @Override
    protected void onRestart() {
        super.onRestart();
        createSessionForStory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        createSessionForStory();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_astory);
        cancel = (TextView) findViewById(R.id.cancel);
        okay = (TextView) findViewById(R.id.ok);

        genre = (RadioGroup) findViewById(R.id.radGroup);
        length = (RadioGroup) findViewById(R.id.length);
        //pass = (CheckBox) findViewById(R.id.PassaStart);

        Intent intent = getIntent();
        user = intent.getExtras().getString("user");
        password = intent.getExtras().getString("password");
        currentUser = intent.getExtras().getString("currentUser");
        Log.d("CURRENTUSER1", "current user in SAS: " + currentUser);


        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setTitle("START A STORY");
        centerTitle();

        length.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                View radioButton = length.findViewById(i);
                int index = radioGroup.indexOfChild(radioButton);
                Log.d("STORYLENGTH", "this is the length:" + index);

                StringBuilder sb = new StringBuilder();
                sb.append("");


                switch(index){
                    case 0:
                        sb.append("25000");
                        lenOfStory = sb.toString();
                        break;

                    case 1:
                        sb.append("50000");
                        lenOfStory = sb.toString();
                        break;

                    case 2:
                        sb.append("100000");
                        lenOfStory = sb.toString();
                        break;
                }
                Log.d("STORYLENGTH", "this is the length:" + lenOfStory);
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });




        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                storyName = (EditText) findViewById(R.id.storyName);
                et = storyName.getText().toString();
                int selectedId = genre.getCheckedRadioButtonId();
                int lengthId =  length.getCheckedRadioButtonId();

                genreDisplay = (RadioButton) findViewById(selectedId);
                storyLength = (RadioButton) findViewById(lengthId);



                if(et.length() > 0) {


                    AlertDialog.Builder builder = new AlertDialog.Builder(StartAStory.this);
                    builder.setTitle("Story snippet");
                    builder.setMessage("Story name:    " + et + "\n" +
                                        "Genre:    "  + genreDisplay.getText().toString() + "\n" +
                                          "Story length:    " + storyLength.getText().toString());// + "\n" +
                                           // "Pass a start: " + passState);



                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {


                                    Log.d("STORYLENGTH", "this is the length:" + lenOfStory);

                                    ArrayList<Integer> occupantIdsList = new ArrayList<Integer>();
                            QBChatDialog mdialog = DialogUtils.buildDialog(storyName.getText().toString() + "-" + genreDisplay.getText().toString() + "*" + userID.toString() + "&" + lenOfStory + "#" + "0", QBDialogType.GROUP, occupantIdsList);

                            QBRestChatService.createChatDialog(mdialog).performAsync(new QBEntityCallback<QBChatDialog>() {
                                @Override
                                public void onSuccess(QBChatDialog result, Bundle params) {

                                }

                                @Override
                                public void onError(QBResponseException responseException) {

                                }
                            });



                            Intent intent = new Intent(v.getContext(), theStories.class);
                            intent.putExtra("title", et);
                            intent.putExtra("genre", genreDisplay.getText());
                            intent.putExtra("user", user);
                            intent.putExtra("password", password);
                                    intent.putExtra("currentUser", currentUser);


                            Log.d("LOADSTORY", "password in Start a story: " + password);
                            startActivity(intent);
                            finish();
                            /*getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container, new Invited_Stories_Fragment())
                                    .commit();*/

                            //Intent intent = new Intent(v.getContext(), Story_Grid.class);                         //over here

                            //Toast.makeText(StartAStory.this, radioButton.getText(), Toast.LENGTH_LONG).show();
                            //createSessionForStory(et, genreDisplay.getText());

                            // You don't have to do anything here if you just want it dismissed when clicked
                        }
                    });

                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            // You don't have to do anything here if you just want it dismissed when clicked
                        }
                    });
                    builder.show();

                }

                else{

                    //Toast.makeText(StartAStory.this, "working", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(StartAStory.this);
                    builder.setTitle("");
                    builder.setMessage("Please name your story");


                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            // You don't have to do anything here if you just want it dismissed when clicked
                        }
                    });
                    builder.show();
                }
            }
        });
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
                userID = qbUser.getId();
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
