package com.rudraksha.rudrakshashakti.Authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import com.rudraksha.rudrakshashakti.Utilities.Utilities;
import com.rudraksha.rudrakshashakti.databinding.ActivitySelectTimeslotsBinding;
import com.rudraksha.rudrakshashakti.databinding.ActivityUnderReviewBinding;

import java.sql.Time;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class SelectTimeslots extends AppCompatActivity implements View.OnClickListener{

    ActivitySelectTimeslotsBinding binding;
    int hour,minutes;
    List<String> timeslots;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Incorporating View Binding
        binding= ActivitySelectTimeslotsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        //Setting content view to Root view
        setContentView(view);
        setListeners();
        uploadOnFirestore();
    }

    private void uploadOnFirestore() {
        if(binding.mondayStartTime.getText().equals("") ||binding.mondayEndTime.getText().equals("") ||binding.tuesdayStartTime.getText().equals("") ||binding.tuesdayEndTime.getText().equals("") ||binding.wednesdayStartTime.getText().equals("") ||binding.wednesdayEndTime.getText().equals("") ||binding.thursdayStartTime.getText().equals("") ||binding.thursdayEndTime.getText().equals("") ||binding.fridayStartTime.getText().equals("") ||binding.fridayEndTime.getText().equals("") ||binding.saturdayStartTime.getText().equals("") ||binding.saturdayEndTime.getText().equals("") ||binding.sundayStartTime.getText().equals("") ||binding.sundayEndTime.getText().equals("") ){
            Utilities.makeToast("Enter All fields",getApplicationContext());
        }else{
            timeslots.add("Monday$"+binding.mondayStartTime.getText()+"-"+binding.mondayEndTime.toString());
            timeslots.add("Tuesday$"+binding.tuesdayStartTime.getText()+"-"+binding.tuesdayEndTime.toString());
            timeslots.add("Wednesday$"+binding.wednesdayStartTime.getText()+"-"+binding.wednesdayEndTime.toString());
            timeslots.add("Thursday$"+binding.thursdayStartTime.getText()+"-"+binding.thursdayEndTime.toString());
            timeslots.add("Friday$"+binding.fridayStartTime.getText()+"-"+binding.fridayEndTime.toString());
            timeslots.add("Saturday$"+binding.saturdayStartTime.getText()+"-"+binding.saturdayEndTime.toString());
            timeslots.add("Sunday$"+binding.sundayStartTime.getText()+"-"+binding.sundayEndTime.toString());
        }
    }

    private void setListeners() {
        binding.mondayStartTime.setOnClickListener(this);
        binding.mondayEndTime.setOnClickListener(this);
        binding.mondayHoliday.setOnClickListener(this);

        binding.tuesdayStartTime.setOnClickListener(this);
        binding.tuesdayEndTime.setOnClickListener(this);
        binding.tuesdayHoliday.setOnClickListener(this);

        binding.wednesdayStartTime.setOnClickListener(this);
        binding.wednesdayEndTime.setOnClickListener(this);
        binding.wednesdayHoliday.setOnClickListener(this);

        binding.thursdayStartTime.setOnClickListener(this);
        binding.thursdayEndTime.setOnClickListener(this);
        binding.thursdayHoliday.setOnClickListener(this);

        binding.fridayStartTime.setOnClickListener(this);
        binding.fridayEndTime.setOnClickListener(this);
        binding.fridayHoliday.setOnClickListener(this);

        binding.saturdayStartTime.setOnClickListener(this);
        binding.saturdayEndTime.setOnClickListener(this);
        binding.saturdayHoliday.setOnClickListener(this);

        binding.sundayStartTime.setOnClickListener(this);
        binding.sundayEndTime.setOnClickListener(this);
        binding.sundayHoliday.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == binding.mondayStartTime){
           openTimePicker(binding.mondayStartTime);
        }else if (view == binding.mondayEndTime){
            openTimePicker(binding.mondayEndTime);
        }else if (view == binding.mondayHoliday){
        }else if (view == binding.tuesdayStartTime){
            openTimePicker(binding.tuesdayStartTime);
        }else if (view == binding.tuesdayEndTime){
            openTimePicker(binding.tuesdayEndTime);
        }else if (view == binding.tuesdayHoliday){
        }else if (view == binding.wednesdayStartTime){
            openTimePicker(binding.wednesdayStartTime);
        }else if (view == binding.wednesdayEndTime){
            openTimePicker(binding.wednesdayEndTime);
        }else if (view == binding.wednesdayHoliday){
        }else if (view == binding.thursdayStartTime){
            openTimePicker(binding.thursdayStartTime);
        }else if (view == binding.thursdayEndTime){
            openTimePicker(binding.thursdayEndTime);
        }else if (view == binding.thursdayHoliday){

        }else if (view == binding.fridayStartTime){
            openTimePicker(binding.fridayStartTime);
        }else if (view == binding.fridayEndTime){
            openTimePicker(binding.fridayEndTime);
        }else if (view == binding.fridayHoliday){

        }else if (view == binding.saturdayStartTime){
            openTimePicker(binding.saturdayStartTime);
        }else if (view == binding.saturdayEndTime){
            openTimePicker(binding.saturdayEndTime);
        }else if (view == binding.saturdayHoliday){

        }else if (view == binding.sundayStartTime){
            openTimePicker(binding.sundayStartTime);
        }else if (view == binding.sundayEndTime){
            openTimePicker(binding.sundayEndTime);
        }else if (view == binding.sundayHoliday){

        }

    }

    private void openTimePicker(EditText editText) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                hour = hourOfDay;
                minutes = minute;
                Time tme = new Time(hour,minute,0);
                @SuppressLint("SimpleDateFormat") Format format = new SimpleDateFormat("hh:mma");

                editText.setText(format.format(tme));
            }
        };

        TimePickerDialog timePickerDialog1 = new TimePickerDialog(this,onTimeSetListener,hour,minutes,false);
        timePickerDialog1.show();
    }


}