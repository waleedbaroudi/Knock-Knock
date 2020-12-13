package com.huaweicontest.knockknock.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService;
import com.huaweicontest.knockknock.R;
import com.huaweicontest.knockknock.model.AccountHandler;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class MainActivity extends AppCompatActivity implements AccountHandler.AccountControlListener {
    //Views
    Button signInButton;
    FloatingActionButton signOutButton;
    TextView nameLabel;
    CircleImageView userImage;
    //Account Control
    AccountHandler handler;
    private static final int LOGIN_REQUEST_CODE = 1003;

    SharedPreferences sharedPreferences;
    private static final String SHOW_CASE_SHOWN_BOOL = "SHOWCASE_SHOWN"; //todo: move constants to a constants class

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences(IntroActivity.APP_SHARED_PREFS, MODE_PRIVATE);

        signInButton = findViewById(R.id.sign_in_button);
        signOutButton = findViewById(R.id.signout_button);
        nameLabel = findViewById(R.id.name_label);
        userImage = findViewById(R.id.user_image);

        handler = new AccountHandler(this, this);
        signInButton.setOnClickListener(v -> handler.signIn());
    }

    private void showSignedInViews() {
        userImage.setVisibility(View.VISIBLE);
        nameLabel.setVisibility(View.VISIBLE);
        signOutButton.setVisibility(View.VISIBLE);
        if (!sharedPreferences.getBoolean(SHOW_CASE_SHOWN_BOOL, false))
            displayShowCaseView();
    }

    private void displayShowCaseView() {
        MaterialTapTargetPrompt.Builder showCaseBuilder = new MaterialTapTargetPrompt.Builder(this);
        showCaseBuilder.setTarget(signOutButton)
                .setPrimaryText("Sign Out")
                .setSecondaryText("Press this button to log out, press and hold to sign out and revoke authorization")
                .setBackButtonDismissEnabled(true)
                .setBackgroundColour(getResources().getColor(R.color.custom_orange))
                .setPromptStateChangeListener((prompt, state) -> {
                    if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED || state == MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED)
                        sharedPreferences.edit().putBoolean(SHOW_CASE_SHOWN_BOOL, true).apply();
                }).show();
    }

    @Override
    public void onSignedIn(AuthHuaweiId userID) {
        nameLabel.setText(userID.getDisplayName());
        Uri userImageUri = userID.getAvatarUri();
        if (!TextUtils.isEmpty(userImageUri.toString())) {
            Glide.with(this).load(userImageUri.toString()).into(userImage);
        }
        showSignedInViews();
    }

    @Override
    public void onAuthorizationNeeded(HuaweiIdAuthService service) {
        startActivityForResult(service.getSignInIntent(), LOGIN_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQUEST_CODE) {
            handler.authenticateAndSignIn(data);
        }
    }
}