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

    /**
     * animates login activity views back in after coming back from profile activity
     */
    private void animateIn() {
        welcomeLabel.animate().translationX(0).setDuration(LOGIN_ANIM_DURATION).setInterpolator(new DecelerateInterpolator());
        signInButton.animate().translationX(0).setDuration(LOGIN_ANIM_DURATION).setInterpolator(new DecelerateInterpolator());
    }

    /**
     * animates login activity views out and goes to profile activity
     */
    private void animateOut() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        welcomeLabel.animate().translationX(-metrics.widthPixels).setDuration(LOGIN_ANIM_DURATION).setInterpolator(new AccelerateInterpolator());
        signInButton.animate().translationX(metrics.widthPixels).setDuration(LOGIN_ANIM_DURATION).setInterpolator(new AccelerateInterpolator())
                .withEndAction(() -> startActivity(new Intent(LoginActivity.this, ProfileActivity.class)));
    }

    /**
     * hides the progress bar and animates the views out
     */
    @Override
    public void onSignedIn() {
        loginProgress.setVisibility(View.GONE);
        animateOut();
    }

    /**
     * toasts that authorization is needed and starts authorization process
     *
     * @param signInIntent the intent that initiates authorization
     */
    @Override
    public void onAuthorizationNeeded(Intent signInIntent) {
        Toast.makeText(this, getString(R.string.taost_authorization_needed), Toast.LENGTH_SHORT).show();
        startActivityForResult(signInIntent, LOGIN_REQUEST_CODE);
    }

    /**
     * toasts failure code, hides progress bar, and enables the sign in button back.
     *
     * @param failureCode the code with which the silent sign in failed
     */
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
            //authorization process ended, attempt sign in.
            handler.authorizedSignIn(data);
        }
    }
}