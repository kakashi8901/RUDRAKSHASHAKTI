package com.rudraksha.rudrakshashakti.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.rudraksha.rudrakshashakti.Common.MainActivity;
import com.rudraksha.rudrakshashakti.Common.SplashScreen;
import com.rudraksha.rudrakshashakti.Utilities.GenericKeyEvent;
import com.rudraksha.rudrakshashakti.Utilities.GenericTextWatcher;
import com.rudraksha.rudrakshashakti.Utilities.KeyboardUtil;
import com.rudraksha.rudrakshashakti.Utilities.MyProgressDialog;
import com.rudraksha.rudrakshashakti.Utilities.Utilities;
import com.rudraksha.rudrakshashakti.Utilities.VolleySingleton;
import com.rudraksha.rudrakshashakti.databinding.ActivityOtpBinding;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class OtpActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    FirebaseAuth mAuth;
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    CountDownTimer cTimer;
    String etOtp;
    private ActivityOtpBinding binding;
    private String phno;
    private String Uid;
    private int flag = 1;
    private MyProgressDialog myProgressDialog;
    private PhoneAuthCredential credential;
    FirebaseFirestore database;
    String uid,expertMainService="",underReview="false";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseFirestore.getInstance();
        phno = getIntent().getExtras().getString("phone");
        binding.phonenumber.setText("+91-" + phno);
        mAuth = FirebaseAuth.getInstance();
        setListeners();
        setOTPListeners();
        initFireBaseCallbacks();
        sendOTP();
    }

    private void setListeners() {
        binding.edit.setOnClickListener(this);
        binding.backotp.setOnClickListener(this);
        binding.resend.setOnClickListener(this);
        binding.otpnext.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,PhoneAuthentication.class));
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v == binding.edit || v == binding.backotp) {
            startActivity(new Intent(this,PhoneAuthentication.class));
            finish();
        } else if (v == binding.resend) {
            if (binding.resend.getText().equals("Resend")) {
                sendOTP();
            }
        } else if (v == binding.otpnext) {
            if (binding.otpnext.getAlpha() == 1) {
                if (flag == 1) {
                    myProgressDialog.showDialog(this);
                    firebaseOTPCheck();
                } else if (flag == 2) {
                    Utilities.makeToast("OTP could'nt be sent. Please try again!", OtpActivity.this);
                }
            }
        }
    }

    private void sendOTP() {
        myProgressDialog = new MyProgressDialog();
        myProgressDialog.showDialog(this);
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91" + phno)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .setForceResendingToken(mResendToken)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void firebaseOTPCheck() {
        credential = PhoneAuthProvider.getCredential(mVerificationId, etOtp);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        Uid = user.getUid();
                        checkUnderReviewOrNot();
                    } else {
                        myProgressDialog.dismissDialog();
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Utilities.makeToast("Invalid credentials!", OtpActivity.this);
                        }
                    }
                });
    }



    private void setOTPListeners() {
        binding.otp1.addTextChangedListener(new GenericTextWatcher(binding.otp2, binding.otp1, binding.otp1));
        binding.otp2.addTextChangedListener(new GenericTextWatcher(binding.otp3, binding.otp1, binding.otp2));
        binding.otp3.addTextChangedListener(new GenericTextWatcher(binding.otp4, binding.otp2, binding.otp3));
        binding.otp4.addTextChangedListener(new GenericTextWatcher(binding.otp5, binding.otp3, binding.otp3));
        binding.otp5.addTextChangedListener(new GenericTextWatcher(binding.otp6, binding.otp4, binding.otp5));
        binding.otp6.addTextChangedListener(new GenericTextWatcher(binding.otp6, binding.otp5, binding.otp6));
        binding.otp1.addTextChangedListener(this);
        binding.otp2.addTextChangedListener(this);
        binding.otp3.addTextChangedListener(this);
        binding.otp4.addTextChangedListener(this);
        binding.otp5.addTextChangedListener(this);
        binding.otp6.addTextChangedListener(this);
        binding.otp1.setOnKeyListener(new GenericKeyEvent(binding.otp1, null, binding.otp2));
        binding.otp2.setOnKeyListener(new GenericKeyEvent(binding.otp2, binding.otp1, binding.otp3));
        binding.otp3.setOnKeyListener(new GenericKeyEvent(binding.otp3, binding.otp2, binding.otp4));
        binding.otp4.setOnKeyListener(new GenericKeyEvent(binding.otp4, binding.otp3, binding.otp5));
        binding.otp5.setOnKeyListener(new GenericKeyEvent(binding.otp5, binding.otp4, binding.otp6));
        binding.otp6.setOnKeyListener(new GenericKeyEvent(binding.otp6, binding.otp5, null));
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        etOtp = binding.otp1.getText().toString() + binding.otp2.getText().toString() +
                binding.otp3.getText().toString() + binding.otp4.getText().toString() +
                binding.otp5.getText().toString() + binding.otp6.getText().toString();

    }


    void initFireBaseCallbacks() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                myProgressDialog.dismissDialog();
                binding.otpnext.performClick();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                flag = 2;
                myProgressDialog.dismissDialog();
                Log.d("TAG", "onVerificationFailed: "+e);
                Utilities.makeToast("OTP couldn't be sent. Please try again!", OtpActivity.this);
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                myProgressDialog.dismissDialog();
                flag = 1;
                Utilities.makeToast("OTP Sent", OtpActivity.this);
                mVerificationId = verificationId;
                mResendToken = token;
                changeResend();
            }

            private void changeResend() {
                cTimer = new CountDownTimer(60000, 1000) {
                    //when timer is started
                    public void onTick(long millisUntilFinished) {
                        binding.didnt.setText("Resend code in ");
                        long i = millisUntilFinished / 1000;
                        binding.resend.setText((i >= 0 && i <= 9 ? "0:0" : "0:") + i);
                    }

                    //when timer is finished
                    public void onFinish() {
                        binding.resend.setText("Resend");
                        binding.didnt.setText("Didn't receive? ");
                    }
                };
                cTimer.start();
            }
        };
    }

    /**
     * Cancelling the timer
     */
    void cancelTimer() {
        if (cTimer != null)
            cTimer.cancel();
    }

    /**
     * Cancelling the timer when app is closed to avoid memory leak
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTimer();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        KeyboardUtil keyboardUtil = new KeyboardUtil(this, ev);
        keyboardUtil.touchEvent();
        return super.dispatchTouchEvent(ev);
    }

    public void checkUnderReviewOrNot() {
        List<String> services = new ArrayList<>();
        services.add("Astrology");
        services.add("Numerology");
        services.add("Vastu Shastra");
        services.add("Lal Kitab");
        services.add("Tarot Card");
        for (int i = 0; i < services.size(); i++) {
            int finalI = i;
            database.collection(services.get(i)).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        if (snapshot.getId().equals(uid)) {
                            expertMainService = services.get(finalI);
                        }
                    }

                    if (expertMainService.equals("")) {
                        Intent detailsIntent = new Intent(getApplicationContext(), DetailsPage.class);
                        detailsIntent.putExtra("fromSplashScreen", "true");
                        startActivity(detailsIntent);
                        finish();
                    }else {
                        database.collection(expertMainService).document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                underReview = documentSnapshot.getString("underReview");
                                if (underReview.equals("true")){
                                    SplashScreen.encrypt.putString("details_filled", "true");
                                    Intent intent = new Intent(getApplicationContext(), UnderReview.class);
                                    startActivity(intent);
                                    finish();
                                }else if (underReview.equals("false")){
                                    SplashScreen.encrypt.putString("details_filled", "true");
                                    SplashScreen.encrypt.putString("under_review", "false");
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Utilities.makeToast(e.getMessage(), getApplicationContext());
                }
            });
        }
    }
}