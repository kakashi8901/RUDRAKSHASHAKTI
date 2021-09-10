package com.rudraksha.rudrakshashakti.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.rudraksha.rudrakshashakti.Common.MainActivity;
import com.rudraksha.rudrakshashakti.R;
import com.rudraksha.rudrakshashakti.databinding.ActivityManageOtpBinding;

import java.util.concurrent.TimeUnit;

public class ManageOtp extends AppCompatActivity {

    private static final String TAG = "ManageOtp";

    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_SUCCESS = 5;
    private static final int STATE_SIGNIN_FAILED = 6;

    private FirebaseAuth mAuth;
    private Button resendOtp;

    private String mVerificationId;
    String phoneNumber;
    private String code;
    ImageButton verifyOtp;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    // handles callbacks for sending otp
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onVerificationCompleted(PhoneAuthCredential credential) {
                    // This callback will be invoked in two situations:
                    // 1 - Instant verification. In some cases the phone number can be instantly
                    //     verified without needing to send or enter a verification code.
                    // 2 - Auto-retrieval. On some devices Google Play services can automatically
                    //     detect the incoming verification SMS and perform verification without
                    //     user action.
                    Log.d(TAG, "onVerificationCompleted:" + credential);
                    updateUI(STATE_VERIFY_SUCCESS);

                    signInWithPhoneAuthCredential(credential);
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    // This callback is invoked in an invalid request for verification is made,
                    // for instance if the the phone number format is not valid.
                    Log.w(TAG, "onVerificationFailed", e);
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    updateUI(STATE_VERIFY_FAILED);

                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        // Invalid request
                    } else if (e instanceof FirebaseTooManyRequestsException) {
                        // The SMS quota for the project has been exceeded
                    }

                    // Show a message and update the UI
                }

                @Override
                public void onCodeSent(@NonNull String verificationId,
                                       @NonNull PhoneAuthProvider.ForceResendingToken token) {
                    // The SMS verification code has been sent to the provided phone number, we
                    // now need to ask the user to enter the code and then construct a credential
                    // by combining the code with a verification ID.
                    Log.d(TAG, "onCodeSent:" + verificationId);

                    // Save verification ID and resending token so we can use them later
                    mVerificationId = verificationId;
                    mResendToken = token;
                    // Update UI
                    updateUI(STATE_CODE_SENT);
                }
            };
    private ActivityManageOtpBinding OtpBinding;

    @Override
    protected void onStart() {
        super.onStart();
        updateUI(STATE_INITIALIZED);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_otp);

        mAuth = FirebaseAuth.getInstance();
        phoneNumber = getIntent().getStringExtra("mobile");

        sendOtp(phoneNumber);

        OtpBinding = ActivityManageOtpBinding.inflate(getLayoutInflater());

        View view = OtpBinding.getRoot();
        //Setting content view to Root view
        setContentView(view);

        code = OtpBinding.enterOtp.getText().toString();
        verifyOtp = OtpBinding.verifyOtpButton;
        resendOtp = OtpBinding.resendButton;

        verifyOtpOnClick();
        resendOtpOnClick();
    }

    private void resendOtpOnClick() {
        resendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendVerificationCode(phoneNumber, mResendToken);
            }
        });
    }

    /**
     * Sending otp
     *
     * @param phoneNumber
     */
    private void sendOtp(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    /**
     * Verifying otp
     */
    private void verifyOtpOnClick() {
        verifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                code = OtpBinding.enterOtp.getText().toString();
                if (code.isEmpty())
                    Toast.makeText(getApplicationContext(), "Blank Field can not be processed", Toast.LENGTH_LONG).show();
                else if (code.length() != 6)
                    Toast.makeText(getApplicationContext(), "Invalid OTP", Toast.LENGTH_LONG).show();
                else {
                    Toast.makeText(ManageOtp.this, "Clicked", Toast.LENGTH_SHORT).show();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });
    }

    /**
     * Signing in user after validating otp
     *
     * @param credential
     */
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();

                            Intent intent = new Intent(ManageOtp.this, MainActivity.class);
                            intent.putExtra("uid", user.getUid());
                            startActivity(intent);
                            finish();

                            // Update UI
                            updateUI(STATE_SIGNIN_SUCCESS);

                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            // Update UI
                            updateUI(STATE_SIGNIN_FAILED);
                            Toast.makeText(getApplicationContext(), "SignIn Code Error", Toast.LENGTH_LONG).show();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    /**
     * For resending otp
     *
     * @param phoneNumber
     * @param token       resend token
     */
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(token)     // ForceResendingToken from callbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    /**
     * For enabling and disabling Resend BUTTON
     *
     * @param uiState
     */
    private void updateUI(int uiState) {
        switch (uiState) {

            case STATE_INITIALIZED:
            case STATE_VERIFY_SUCCESS:
                disableViews(resendOtp);
                break;
            case STATE_CODE_SENT:
            case STATE_VERIFY_FAILED:
            case STATE_SIGNIN_FAILED:
                enableViews(resendOtp);
                break;
            case STATE_SIGNIN_SUCCESS:
                break;
        }
    }

    /**
     * For enabling views
     *
     * @param views Each View
     */
    private void enableViews(View... views) {
        for (View v : views) {
            v.setEnabled(true);
        }
    }

    /**
     * For disabling views
     *
     * @param views Each View
     */
    private void disableViews(View... views) {
        for (View v : views) {
            v.setEnabled(false);
        }
    }

}