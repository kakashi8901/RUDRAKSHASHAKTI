package com.rudraksha.rudrakshashakti.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.rudraksha.rudrakshashakti.R;
import com.rudraksha.rudrakshashakti.databinding.ActivityChooseAuthenticationBinding;

import java.util.Arrays;

public class ChooseAuthentication extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "Message";

    ActivityChooseAuthenticationBinding aBinding;

    private FirebaseAuth mAuth;

    private GoogleSignInClient mGoogleSignInClient;

    private CallbackManager facebookCallbackManager;
    private LoginButton loginButton;

//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null) {
//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            startActivity(intent);
//        }
//
////        updateUI(currentUser);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_choose_authentication);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //Incorporating View Binding
        aBinding = ActivityChooseAuthenticationBinding.inflate(getLayoutInflater());
        View view = aBinding.getRoot();
        //Setting content view to Root view
        setContentView(view);

        setListeners();

    }

    private void setListeners() {
        aBinding.button.setOnClickListener(this);
        aBinding.loginButton.setOnClickListener(this);
        aBinding.phoneAuth.setOnClickListener(this);
    }

    private void phoneAuthenticationHandler() {
                Intent intent = new Intent(getApplicationContext(), PhoneAuthentication.class);
                startActivity(intent);
                finish();
    }

    /**
     * Called when application is loaded for Facebook Authentication
     * onClick is also handled by it
     */
    private void facebookAuthenticationHandler() {
        facebookCallbackManager = CallbackManager.Factory.create();

//        loginButton = activityChooseAuthenticationBinding.loginButton;
//        loginButton.setPermissions(Arrays.asList(EMAIL));
//        loginButton.setPermissions("email", "public_profile");
        // If you are using in a fragment, call loginButton.setFragment(this);


        // Callback registration
        LoginManager.getInstance().registerCallback(facebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.d(TAG, "onError: " + exception.getMessage());
            }
        });
        LoginManager.getInstance().logInWithReadPermissions(ChooseAuthentication.this,
                Arrays.asList("public_profile", "email"));
    }

    /**
     * Handles Facebook signing in
     *
     * @param token generated  when Callback is registered
     */
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            Intent intent = new Intent(getApplicationContext(), PhoneAuthentication.class);
                            intent.putExtra("uid", user.getUid());
                            mAuth.signOut();
                            startActivity(intent);
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(ChooseAuthentication.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }
                    }
                });
    }

    /**
     * Called when application is loaded
     */
    private void googleAuthentication() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        //setting onClickListener for testing google authentication
        googleSignIn();
    }

    /**
     * Called when user clicks on google sign in button
     */
    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**
         * For Google
         */
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        } else {
            /**
             * For Facebook
             */
            // Pass the activity result back to the Facebook SDK
            facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    /**
     * Firebase Authenticates using id token for Google Sign in
     * @param idToken generated by google sign in
     */
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            Intent intent = new Intent(getApplicationContext(), PhoneAuthentication.class);
                            intent.putExtra("uid", user.getUid());
                            mAuth.signOut();
                            startActivity(intent);
                            Log.d(TAG, "onComplete: " + user.getUid());
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(ChooseAuthentication.this, "SignIn Failed", Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        if (view == aBinding.button) {
            googleAuthentication();
        } else if (view == aBinding.loginButton) {
            facebookAuthenticationHandler();
        } else if (view == aBinding.phoneAuth) {
            phoneAuthenticationHandler();
        }
    }
}