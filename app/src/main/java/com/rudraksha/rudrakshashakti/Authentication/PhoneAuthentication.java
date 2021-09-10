package com.rudraksha.rudrakshashakti.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.rudraksha.rudrakshashakti.Utilities.Utilities;
import com.rudraksha.rudrakshashakti.databinding.ActivityPhoneAuthenticationBinding;

public class PhoneAuthentication extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Message";
    private ActivityPhoneAuthenticationBinding binding;
    private FirebaseAuth mAuth;

    EditText phoneNumber;
    Button b1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_phone_authentication);

        mAuth = FirebaseAuth.getInstance();

        binding = ActivityPhoneAuthenticationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        //Setting content view to Root view
        setContentView(view);

        setListeners();
    }

    private void setListeners() {
        binding.sendOtp.setOnClickListener(this);
        binding.backBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == binding.sendOtp) {
            next();
        } else if (view == binding.backBtn) {
            back();
        }
    }

    private void next() {
//        Utilities.makeToast("Clicked", getApplicationContext());
        phoneNumber = binding.editTextPhone;

        Intent intent = new Intent(PhoneAuthentication.this, OtpActivity.class);
        intent.putExtra("phone", phoneNumber.getText().toString());
        startActivity(intent);
        finish();
    }

    private void back() {
        startActivity(new Intent(this,ChooseAuthentication.class));
        Utilities.makeToast("Please Sign In", getApplicationContext());
        this.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,ChooseAuthentication.class));
        Utilities.makeToast("Please Sign In", getApplicationContext());
        this.finish();
    }
}