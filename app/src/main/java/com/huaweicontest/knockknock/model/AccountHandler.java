package com.huaweicontest.knockknock.model;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService;

public class AccountHandler {

    AccountControlListener listener;
    Context context;

    //Account Kit Fields
    HuaweiIdAuthService service;
    HuaweiIdAuthParams params;

    public AccountHandler(AccountControlListener listener, Context context) {
        this.listener = listener;
        this.context = context;
        this.params = new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setIdToken().setAccessToken().createParams();
        this.service = HuaweiIdAuthManager.getService(context, params);
    }

    public void signIn() {
        Task<AuthHuaweiId> authTask = service.silentSignIn();
        authTask.addOnSuccessListener(authID -> {
            Toast.makeText(context, "Signed in!", Toast.LENGTH_SHORT).show();
            listener.onSignedIn(authID);
        });

        authTask.addOnFailureListener(e -> {
            Toast.makeText(context, "Authorization Needed", Toast.LENGTH_SHORT).show();
            listener.onAuthorizationNeeded(service);
        });
    }

    public void authenticateAndSignIn(Intent data) {
        Task<AuthHuaweiId> authTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data);
        if (authTask.isSuccessful())
            listener.onSignedIn(authTask.getResult());
        else {
            int failureCode = ((ApiException) authTask.getException()).getStatusCode();
            listener.onSignInFailed(failureCode);
        }
    }

    public void revokeAuth() {

    }

    public void signOut() {

    }

    public interface AccountControlListener {
        void onSignedIn(AuthHuaweiId userID);

        void onSignInFailed(int failureCode);

        void onAuthorizationNeeded(HuaweiIdAuthService service);
    }

}
