package com.huaweicontest.knockknock.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.huawei.hms.support.hwid.result.AuthHuaweiId;
import com.huaweicontest.knockknock.R;
import com.huaweicontest.knockknock.model.AccountHandler;

import static com.huaweicontest.knockknock.model.Constant.APP_SHARED_PREFS;
import static com.huaweicontest.knockknock.model.Constant.LOGIN_ANIM_DURATION;
import static com.huaweicontest.knockknock.model.Constant.LOGIN_REQUEST_CODE;

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
        animateIn();
        signInButton.setEnabled(true);
    }

    private void animateIn() { // animates login activity views back in
        welcomeLabel.animate().translationX(0).setDuration(LOGIN_ANIM_DURATION).setInterpolator(new DecelerateInterpolator());
        signInButton.animate().translationX(0).setDuration(LOGIN_ANIM_DURATION).setInterpolator(new DecelerateInterpolator());
    }

    private void animateOut() { // animates login activity views out and goes to profile activity
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        welcomeLabel.animate().translationX(-metrics.widthPixels).setDuration(LOGIN_ANIM_DURATION).setInterpolator(new AccelerateInterpolator());
        signInButton.animate().translationX(metrics.widthPixels).setDuration(LOGIN_ANIM_DURATION).setInterpolator(new AccelerateInterpolator())
                .withEndAction(() -> startActivity(new Intent(LoginActivity.this, ProfileActivity.class)));
    }

    @Override
    public void onSignedIn(AuthHuaweiId userID) {
        loginProgress.setVisibility(View.GONE);
        animateOut();
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