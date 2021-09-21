package com.rudraksha.rudrakshashakti.Authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.rudraksha.rudrakshashakti.databinding.ActivityDetailsPageBinding;
import com.rudraksha.rudrakshashakti.databinding.ActivityUnderReviewBinding;

public class UnderReview extends AppCompatActivity implements View.OnClickListener{

    ActivityUnderReviewBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Incorporating View Binding
        binding= ActivityUnderReviewBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        //Setting content view to Root view
        setContentView(view);
        setListeners();
    }

    private void setListeners() {
        binding.sendNow.setOnClickListener(this);
        binding.textView2.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
       if(view == binding.sendNow){
           openEmailId();
       }else if(view == binding.textView2){
           openEmailId();
       }
    }

    private void openEmailId() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]
                {"Aadishaktiadmin@rudraksha.org.in"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Verify My Documents For Account Creation RUDRASHAKTI");
        intent.putExtra(Intent.EXTRA_TEXT, "\n\n1. My Name: \n\n2. My Phone Number: \n\n3. My Aadhar Card Photo: \n\n4. My Pan Card Photo: \n \n");
        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent,"Choose an Email Client"));
    }
}