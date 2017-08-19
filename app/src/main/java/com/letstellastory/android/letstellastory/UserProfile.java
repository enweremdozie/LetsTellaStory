package com.letstellastory.android.letstellastory;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.letstellastory.android.letstellastory.Common.Common;
import com.letstellastory.android.letstellastory.Holder.QBUsersHolder;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class UserProfile extends AppCompatActivity {

    EditText edtPassword, edtOldPassword, edtFullName, edtEmail, edtPhone;
    Button btnUpdate, btnCancel, deleteAccount;
    String user, password, currentUser;
    ImageView user_avatar;

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
        setContentView(R.layout.activity_user_profile);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        user = intent.getExtras().getString("user");
        password = intent.getExtras().getString("password");
        user_avatar = (ImageView) findViewById(R.id.user_avatar);
        currentUser = intent.getExtras().getString("currentUser");

        initViews();

        loadUserProfile();

        centerTitle();

        setTitle("EDIT MY PROFILE");

        user_avatar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
             Intent intent1 = new Intent();
             intent1.setType("image/*");
             intent1.setAction(Intent.ACTION_GET_CONTENT);
             startActivityForResult(Intent.createChooser(intent1,"Select Picture"), Common.SELECT_PICTURE);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = edtPassword.getText().toString();
                String oldPassword = edtOldPassword.getText().toString();
                String email = edtEmail.getText().toString();
                String phone = edtPhone.getText().toString();
                String fullName = edtFullName.getText().toString();

                QBUser user = new QBUser();
                user.setId((QBChatService.getInstance().getUser().getId()));
                if(!Common.isNullOrEmptyString(oldPassword));
                user.setOldPassword(oldPassword);
                if(!Common.isNullOrEmptyString(password));
                user.setPassword(password);
                if(!Common.isNullOrEmptyString(fullName));
                user.setFullName(fullName);
                if(!Common.isNullOrEmptyString(email));
                user.setEmail(email);
                if(!Common.isNullOrEmptyString(phone));
                user.setPhone(phone);

               final ProgressDialog mDialog = new ProgressDialog(UserProfile.this);

                mDialog.setMessage("Please wait...");
                mDialog.show();
                QBUsers.updateUser(user).performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {
                        Toast.makeText(UserProfile.this, "User: " + qbUser.getLogin() + " Updated", Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();

                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Toast.makeText(UserProfile.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

        //Log.d("CURRRENTUSER", "user in user profile " + currentUser);
        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
                builder.setTitle("Delete account");
                builder.setMessage("Are you sure you want to delete your account");


                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        QBUsers.deleteUser(Integer.parseInt(currentUser));

                        /*QBUsers.deleteUser(Integer.valueOf(currentUser), new QBEntityCallback<QBUser>() {
                            @Override
                            public void onSuccess(QBUser qbUser, Bundle bundle) {

                            }

                            @Override
                            public void onError(QBResponseException e) {

                            }
                        });*/

                        Intent intent = new Intent(UserProfile.this, MainActivity.class);
                        startActivity(intent);
                        // You don't have to do anything here if you just want it dismissed when clicked
                    }
                });
                builder.show();


            }
        });
    }

    private void loadUserProfile() {

        QBUsers.getUser(QBChatService.getInstance().getUser().getId())
                    .performAsync(new QBEntityCallback<QBUser>() {
                        @Override
                        public void onSuccess(QBUser qbUser, Bundle bundle) {
                            QBUsersHolder.getInstance().putUser(qbUser);
                            if(qbUser.getFileId() != null)
                            {
                                int profilePictureId = qbUser.getFileId();

                                QBContent.getFile(profilePictureId)
                                        .performAsync(new QBEntityCallback<QBFile>() {
                                            @Override
                                            public void onSuccess(QBFile qbFile, Bundle bundle) {
                                                String fileUrl = qbFile.getPublicUrl();
                                                Picasso.with(getBaseContext())
                                                        .load(fileUrl)
                                                        .into(user_avatar);
                                            }

                                            @Override
                                            public void onError(QBResponseException e) {

                                            }
                                        });
                            }
                        }

                        @Override
                        public void onError(QBResponseException e) {

                        }
                    });
        QBUser currentUser = QBChatService.getInstance().getUser();
        String fullName = currentUser.getFullName();
        String email = currentUser.getEmail();
        String phone = currentUser.getPhone();

        edtEmail.setText(email);
        edtFullName.setText(fullName);
        edtPhone.setText(phone);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if(requestCode == Common.SELECT_PICTURE){
                Uri selectedImageUri = data.getData();
                final ProgressDialog mDialog = new ProgressDialog(UserProfile.this);
                mDialog.setMessage("Please Wait...");
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.show();

                try {
                    InputStream in = getContentResolver().openInputStream(selectedImageUri);
                    final Bitmap bitmap = BitmapFactory.decodeStream(in);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG,100,bos);
                    File file = new File(Environment.getExternalStorageDirectory() + "/myimage.png");
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(bos.toByteArray());
                    fos.flush();
                    fos.close();


                    final int imageSizeKb = (int) file.length() / 1024;
                    if(imageSizeKb >= (1024*100)){
                        Toast.makeText(this, "Error image size", Toast.LENGTH_SHORT).show();
                    }

                    //QBContent.uploadFile(file,true,

                    QBContent.uploadFileTask(file, true, null)
                            .performAsync(new QBEntityCallback<QBFile>() {
                                @Override
                                public void onSuccess(QBFile qbFile, Bundle bundle) {
                                    QBUser user = new QBUser();
                                    user.setId(QBChatService.getInstance().getUser().getId());
                                    user.setFileId(Integer.parseInt(qbFile.getId().toString()));

                                    QBUsers.updateUser(user)
                                            .performAsync(new QBEntityCallback<QBUser>() {
                                                @Override
                                                public void onSuccess(QBUser qbUser, Bundle bundle) {
                                                    mDialog.dismiss();
                                                    user_avatar.setImageBitmap(bitmap);
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
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
           onBackPressed();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        btnCancel = (Button) findViewById(R.id.update_user_btn_cancel);
        btnUpdate = (Button) findViewById(R.id.update_user_btn_update);
        deleteAccount = (Button) findViewById(R.id.delete_account);

        edtEmail = (EditText) findViewById(R.id.update_edt_email);
        edtPhone = (EditText) findViewById(R.id.update_edt_phone);
        edtFullName = (EditText) findViewById(R.id.update_edt_full_name);
        edtPassword = (EditText) findViewById(R.id.update_edt_password);
        edtOldPassword = (EditText) findViewById(R.id.update_edt_old_password);

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
