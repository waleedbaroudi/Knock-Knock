package com.huaweicontest.knockknock.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import static com.huaweicontest.knockknock.model.Constant.*;

import com.huawei.hms.support.hwid.result.AuthHuaweiId;
import com.huaweicontest.knockknock.R;
import com.huaweicontest.knockknock.model.AccountHandler;

public class LoginActivity extends AppCompatActivity implements AccountHandler.AccountControlListener {
    //Views
    Button signInButton;
    TextView welcomeLabel;
    ProgressBar loginProgress;
    //Account Control
    AccountHandler handler;

    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPreferences = getSharedPreferences(APP_SHARED_PREFS, MODE_PRIVATE);

        signInButton = findViewById(R.id.sign_in_button);
        welcomeLabel = findViewById(R.id.welcome_label);
        loginProgress = findViewById(R.id.sign_in_progress);

        signInButton.setOnClickListener(v -> {
            loginProgress.setVisibility(View.VISIBLE);
            signInButton.setEnabled(false);
            handler.silentSignIn();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*
        assigning handler here to make sure the service context and control listener are
        are set to this activity when coming back from profile activity after successful sign out
         */
        handler = AccountHandler.getInstance(this, this);
        signInButton.setEnabled(true);
    }

    @Override
    public void onSignedIn(AuthHuaweiId userID) {
        loginProgress.setVisibility(View.GONE);
        startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
    }

    @Override
    public void onAuthorizationNeeded(Intent signInIntent) {
        Toast.makeText(this, getString(R.string.taost_authorization_needed), Toast.LENGTH_SHORT).show();
        startActivityForResult(signInIntent, LOGIN_REQUEST_CODE);
    }

    @Override
    public void onSignInFailed(int failureCode) {
        Toast.makeText(this, "Failure code: " + failureCode, Toast.LENGTH_SHORT).show();
        signInButton.setEnabled(true);
        loginProgress.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQUEST_CODE) {
            handler.authorizedSignIn(data);
        }
    }
}