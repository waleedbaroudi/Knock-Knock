package com.huaweicontest.knockknock.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.huaweicontest.knockknock.R;
import com.huaweicontest.knockknock.model.AccountHandler;

public class MainActivity extends AppCompatActivity {

    Button signInButton;

    AccountHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signInButton = findViewById(R.id.sign_in_button);
        handler = new AccountHandler();
        signInButton.setOnClickListener(v -> handler.signIn());

    }
}