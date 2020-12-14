package com.huaweicontest.knockknock.model;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService;

/**
 * Singleton class that handles Huawei Account Kit operations
 */
public class AccountHandler {
    private static final String TAG = "AccountHandler";

    private static AccountHandler instance;
    static AccountControlListener listener;

    //Account Kit Fields
    private static HuaweiIdAuthService service;
    private static HuaweiIdAuthParams params;

    private AuthHuaweiId currentUserAccount;

    /**
     * @param listener a listener that handles UI responses to Account events
     * @param context  the context in which the HuaweiIdAuthService will be created
     */
    private AccountHandler(AccountControlListener listener, Context context) {
        AccountHandler.listener = listener;
        params = new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setIdToken().setAccessToken().createParams();
        service = HuaweiIdAuthManager.getService(context, params);
    }


    public static synchronized AccountHandler getInstance(AccountControlListener listener, Context context) {
        if (instance == null) {
            instance = new AccountHandler(listener, context);
            return instance;
        }
        AccountHandler.listener = listener;
        service = HuaweiIdAuthManager.getService(context, params);
        return instance;
    }

    /**
     * Handles the silent sign in process in case the app is already authorized
     */
    public void silentSignIn() {
        Task<AuthHuaweiId> authTask = service.silentSignIn();
        authTask.addOnSuccessListener(authID -> {
            currentUserAccount = authID;
            listener.onSignedIn();
        });

        authTask.addOnFailureListener(e -> listener.onAuthorizationNeeded(service.getSignInIntent()));
    }

    /**
     * handles sign in and authorization when silent sign in fails
     *
     * @param data intent that contains the result of the authorization
     */
    public void authorizedSignIn(Intent data) {
        Task<AuthHuaweiId> authTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data);
        if (authTask.isSuccessful()) {
            AuthHuaweiId id = authTask.getResult();
            listener.onSignedIn();
            currentUserAccount = id;
        } else {
            int failureCode = ((ApiException) authTask.getException()).getStatusCode();
            listener.onSignInFailed(failureCode);
        }
    }

    /**
     * cancels authorization and logs the user out.
     * if the cancellation fails, the user is logged out and revocation failure is indicated
     */
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

    /**
     * signs the user out without revoking authorization
     */
    public void signOut() {
        Task<Void> signOutTask = service.signOut();
        signOutTask.addOnCompleteListener(vd -> listener.onSignedOut(false));
    }

    /**
     * returns the current user
     *
     * @return the user in the result of the last task for AuthHuaweiId
     */
    public AuthHuaweiId getCurrentUserAccount() {
        return currentUserAccount;
    }

    public interface AccountControlListener {
        /*
        methods are made default so that some of them are not unnecessarily implemented in the
        implementing classes (e.g. onSignedIn in ProfileActivity)
         */
        default void onSignedIn() {
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
