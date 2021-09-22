package com.rudraksha.rudrakshashakti.Authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.rudraksha.rudrakshashakti.databinding.ActivitySelectTimeslotsBinding;
import com.rudraksha.rudrakshashakti.databinding.ActivityUnderReviewBinding;

public class SelectTimeslots extends AppCompatActivity implements View.OnClickListener{

    ActivitySelectTimeslotsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Incorporating View Binding
        binding= ActivitySelectTimeslotsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        //Setting content view to Root view
        setContentView(view);
        setListeners();
    }

    private void setListeners() {
    }

    @Override
    public void onClick(View view) {

    }
}