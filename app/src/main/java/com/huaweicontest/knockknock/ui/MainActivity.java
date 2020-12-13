package com.huaweicontest.knockknock.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class MainActivity extends AppCompatActivity implements AccountHandler.AccountControlListener {
    //Views
    Button signInButton;
    FloatingActionButton signOutButton;
    TextView nameLabel;
    CircleImageView userImage;
    //Account Control
    AccountHandler handler;
    private static final int LOGIN_REQUEST_CODE = 1003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signInButton = findViewById(R.id.sign_in_button);
        signOutButton = findViewById(R.id.signout_button);
        nameLabel = findViewById(R.id.name_label);
        userImage = findViewById(R.id.user_image);

        handler = new AccountHandler(this, this);
        signInButton.setOnClickListener(v -> {
            handler.signIn();
        });
    }

    private void showSignedInViews() {
        userImage.setVisibility(View.VISIBLE);
        nameLabel.setVisibility(View.VISIBLE);
        signOutButton.setVisibility(View.VISIBLE);
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