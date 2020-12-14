package com.huaweicontest.knockknock.model;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService;

public class AccountHandler {
    private static final String TAG = "AccountHandler";

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

    public void silentSignIn() {
        Task<AuthHuaweiId> authTask = service.silentSignIn();
        authTask.addOnSuccessListener(authID -> {
            listener.onSignedIn(authID);
        });

        authTask.addOnFailureListener(e -> {
            listener.onAuthorizationNeeded(service.getSignInIntent());
        });
    }

    public void authorizedSignIn(Intent data) {
        Task<AuthHuaweiId> authTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data);
        if (authTask.isSuccessful())
            listener.onSignedIn(authTask.getResult());
        else {
            int failureCode = ((ApiException) authTask.getException()).getStatusCode();
            listener.onSignInFailed(failureCode);
        }
    }

    public void revokeAuth() {
        Task<Void> revokeAuthTask = service.cancelAuthorization();
        revokeAuthTask.addOnCompleteListener(task -> {
            if (task.isSuccessful())
                listener.onSignedOut(true);
            else {
                if (task.getException() instanceof ApiException) {
                    int code = ((ApiException) task.getException()).getStatusCode();
                    listener.onAuthRevocationFailed(code);
                    listener.onSignedOut(false);
                }
            }
        });
    }

    public void signOut() {
        Task<Void> signOutTask = service.signOut();
        signOutTask.addOnCompleteListener(vd -> listener.onSignedOut(false));
    }

    public interface AccountControlListener {
        /*
        methods are made default so that some of them are not unnecessarily implemented in the
        implementing classes (e.g. onSignedIn in ProfileActivity)
         */
        default void onSignedIn(AuthHuaweiId userID) {
            Log.w(TAG, "onSignedIn: this method is not yet implemented", new IllegalStateException());
        }


        default void onSignInFailed(int failureCode) {
            Log.w(TAG, "onSignInFailed: this method is not yet implemented", new IllegalStateException());
        }

        default void onAuthorizationNeeded(Intent signInIntent) {
            Log.w(TAG, "onAuthorizationNeeded: this method is not yet implemented", new IllegalStateException());
        }

        default void onSignedOut(boolean isAuthRevoked) {
            Log.w(TAG, "onSignedOut: this method is not yet implemented", new IllegalStateException());
        }

        default void onAuthRevocationFailed(int failureCode) {
            Log.w(TAG, "onAuthRevocationFailed: this method is not yet implemented", new IllegalStateException());
        }
    }

}
