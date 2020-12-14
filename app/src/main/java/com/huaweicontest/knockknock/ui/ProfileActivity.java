package com.huaweicontest.knockknock.ui;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;
import com.huaweicontest.knockknock.R;
import com.huaweicontest.knockknock.model.AccountHandler;
import com.huaweicontest.knockknock.model.Constant;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

import static com.huaweicontest.knockknock.model.Constant.SHOWCASE_SHOWN_BOOL;

public class ProfileActivity extends AppCompatActivity implements AccountHandler.AccountControlListener {

    FloatingActionButton signOutButton;
    TextView nameLabel;
    CircleImageView userImage;
    SharedPreferences sharedPreferences;

    AccountHandler handler;
    AuthHuaweiId userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        sharedPreferences = getSharedPreferences(Constant.APP_SHARED_PREFS, MODE_PRIVATE);
        //initialize handler and get current user
        handler = AccountHandler.getInstance(this, this);
        userID = handler.getCurrentUserAccount();

        setupUIElements();

        //show sign out button instructions if it's the first log in
        if (!sharedPreferences.getBoolean(SHOWCASE_SHOWN_BOOL, false))
            displayShowCaseView();
    }


    @Override
    public void onBackPressed() {
        showSignOutDialog(false);
    }

    private void setupUIElements() {
        // initialize elements
        signOutButton = findViewById(R.id.signout_button);
        nameLabel = findViewById(R.id.name_label);
        userImage = findViewById(R.id.user_image);
        //set user's name
        nameLabel.setText(userID.getDisplayName());
        //set click to show sign out dialog without auth revocation message
        signOutButton.setOnClickListener(v -> showSignOutDialog(false));
        //set long click to show sign out dialog with auth revocation message
        signOutButton.setOnLongClickListener(v -> {
            showSignOutDialog(true);
            return true;
        });
        //get user profile picture and, if available, show it.
        Uri userImageUri = userID.getAvatarUri();
        if (!TextUtils.isEmpty(userImageUri.toString())) {
            Glide.with(this).load(userImageUri.toString()).into(userImage);
        }
    }

    private void showSignOutDialog(boolean revoke) {
        String message = revoke ? getString(R.string.signout_dialog_message_revoke)
                : getString(R.string.signout_dialog_message);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.DialogTheme);
        dialogBuilder.setTitle(getString(R.string.signout_dialog_title))
                .setMessage(message)
                .setPositiveButton(R.string.signout_dialog_positive, (dialog, which) -> {
                    if (revoke)
                        handler.revokeAuth();
                    else
                        handler.signOut();
                })
                .setNegativeButton(R.string.signout_dialog_negative, null)
                .create().show();
    }

    private void displayShowCaseView() {
        MaterialTapTargetPrompt.Builder showCaseBuilder = new MaterialTapTargetPrompt.Builder(this);
        showCaseBuilder.setTarget(signOutButton)
                .setPrimaryText(R.string.showcase_primary)
                .setSecondaryText(R.string.showcase_secondary)
                .setBackButtonDismissEnabled(true)
                .setBackgroundColour(getResources().getColor(R.color.custom_orange))
                .setPromptStateChangeListener((prompt, state) -> {
                    if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED || state == MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED)
                        sharedPreferences.edit().putBoolean(SHOWCASE_SHOWN_BOOL, true).apply();
                }).show();
    }

    @Override
    public void onSignedOut(boolean isAuthRevoked) {
        if (isAuthRevoked)
            Toast.makeText(getApplicationContext(), getString(R.string.toast_authorization_revoked), Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onAuthRevocationFailed(int failureCode) {
        Toast.makeText(this, "Revocation failed with code: " + failureCode, Toast.LENGTH_SHORT).show();
    }
}