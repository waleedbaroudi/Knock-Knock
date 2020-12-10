package com.huaweicontest.knockknock.model;

import android.view.View;

import com.huaweicontest.knockknock.R;

public class AccountControlKeyListener implements View.OnClickListener {

    AccountHandler handler;

    public AccountControlKeyListener(AccountHandler handler) {
        this.handler = handler;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                handler.signIn();
                break;
            //add other cases
        }


    }
}
