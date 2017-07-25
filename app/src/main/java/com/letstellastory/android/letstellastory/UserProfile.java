package com.letstellastory.android.letstellastory;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.letstellastory.android.letstellastory.Common.Common;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

public class UserProfile extends AppCompatActivity {

    EditText edtPassword, edtOldPassword, edtFullName, edtEmail, edtPhone;
    Button btnUpdate, btnCancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        initViews();

        loadUserProfile();

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

    }

    private void loadUserProfile() {
        QBUser currentUser = QBChatService.getInstance().getUser();
        String fullName = currentUser.getFullName();
        String email = currentUser.getEmail();
        String phone = currentUser.getPhone();

        edtEmail.setText(email);
        edtFullName.setText(fullName);
        edtPhone.setText(phone);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
           onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        btnCancel = (Button) findViewById(R.id.update_user_btn_cancel);
        btnUpdate = (Button) findViewById(R.id.update_user_btn_update);

        edtEmail = (EditText) findViewById(R.id.update_edt_email);
        edtPhone = (EditText) findViewById(R.id.update_edt_phone);
        edtFullName = (EditText) findViewById(R.id.update_edt_full_name);
        edtPassword = (EditText) findViewById(R.id.update_edt_password);
        edtOldPassword = (EditText) findViewById(R.id.update_edt_old_password);


    }
}
