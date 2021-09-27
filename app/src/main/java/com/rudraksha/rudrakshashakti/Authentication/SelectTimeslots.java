package com.rudraksha.rudrakshashakti.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.rudraksha.rudrakshashakti.Common.MainActivity;
import com.rudraksha.rudrakshashakti.Common.SplashScreen;
import com.rudraksha.rudrakshashakti.R;
import com.rudraksha.rudrakshashakti.Utilities.MyProgressDialog;
import com.rudraksha.rudrakshashakti.Utilities.Utilities;
import com.rudraksha.rudrakshashakti.databinding.ActivitySelectTimeslotsBinding;

import org.jetbrains.annotations.NotNull;

import java.sql.Time;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class SelectTimeslots extends AppCompatActivity implements View.OnClickListener{

    ActivitySelectTimeslotsBinding binding;
    int hour,minutes;
    String defaultColor,uid,expertMainService="";
    List<String> timeslots = new ArrayList<>();
    FirebaseFirestore database;
    private MyProgressDialog myProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Incorporating View Binding
        binding= ActivitySelectTimeslotsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        uid = mAuth.getUid();
        defaultColor = binding.mondayStartTime.getTextColors().toString();
        //Setting content view to Root view
        setContentView(view);
        setListeners();
    }

    private void uploadOnFirestore() {
        if(binding.mondayStartTime.getText().toString().equals("") ||binding.mondayEndTime.getText().toString().equals("") ||binding.tuesdayStartTime.getText().toString().equals("") ||binding.tuesdayEndTime.getText().toString().equals("") ||binding.wednesdayStartTime.getText().toString().equals("") ||binding.wednesdayEndTime.getText().toString().equals("") ||binding.thursdayStartTime.getText().toString().equals("") ||binding.thursdayEndTime.getText().toString().equals("") ||binding.fridayStartTime.getText().toString().equals("") ||binding.fridayEndTime.getText().toString().equals("") ||binding.saturdayStartTime.getText().toString().equals("") ||binding.saturdayEndTime.getText().toString().equals("") ||binding.sundayStartTime.getText().toString().equals("") ||binding.sundayEndTime.getText().toString().equals("") ){
            Utilities.makeToast("Enter All fields",getApplicationContext());
        }else{
            myProgressDialog = new MyProgressDialog();
            myProgressDialog.showDialog(this);
            timeslots.add("Monday$"+binding.mondayStartTime.getText()+"-"+binding.mondayEndTime.getText());
            timeslots.add("Tuesday$"+binding.tuesdayStartTime.getText()+"-"+binding.tuesdayEndTime.getText());
            timeslots.add("Wednesday$"+binding.wednesdayStartTime.getText()+"-"+binding.wednesdayEndTime.getText());
            timeslots.add("Thursday$"+binding.thursdayStartTime.getText()+"-"+binding.thursdayEndTime.getText());
            timeslots.add("Friday$"+binding.fridayStartTime.getText()+"-"+binding.fridayEndTime.getText());
            timeslots.add("Saturday$"+binding.saturdayStartTime.getText()+"-"+binding.saturdayEndTime.getText());
            timeslots.add("Sunday$"+binding.sundayStartTime.getText()+"-"+binding.sundayEndTime.getText());


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

                        if (!expertMainService.equals("")){
                            database.collection(expertMainService).document(uid).update("timings",timeslots).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Utilities.makeToast("Added",getApplicationContext());
                                    myProgressDialog.dismissDialog();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    Utilities.makeToast("NotAdded",getApplicationContext());
                                    myProgressDialog.dismissDialog();
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

        binding.nextBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == binding.mondayStartTime){
           openTimePicker(binding.mondayStartTime);
        }else if (view == binding.mondayEndTime){
            openTimePicker(binding.mondayEndTime);
        }else if (view == binding.mondayHoliday){
            if (((CheckBox) view).isChecked()) {
                disableEditText(binding.mondayStartTime,binding.mondayEndTime,"disable");
            }else{
                disableEditText(binding.mondayStartTime,binding.mondayEndTime,"enable");
            }
        }else if (view == binding.tuesdayStartTime){
            openTimePicker(binding.tuesdayStartTime);
        }else if (view == binding.tuesdayEndTime){
            openTimePicker(binding.tuesdayEndTime);
        }else if (view == binding.tuesdayHoliday){
            if (((CheckBox) view).isChecked()) {
                disableEditText(binding.tuesdayStartTime,binding.tuesdayEndTime,"disable");
            }else{
                disableEditText(binding.tuesdayStartTime,binding.tuesdayEndTime,"enable");
            }
        }else if (view == binding.wednesdayStartTime){
            openTimePicker(binding.wednesdayStartTime);
        }else if (view == binding.wednesdayEndTime){
            openTimePicker(binding.wednesdayEndTime);
        }else if (view == binding.wednesdayHoliday){ if (((CheckBox) view).isChecked()) {
            disableEditText(binding.wednesdayStartTime,binding.wednesdayEndTime,"disable");
        }else{
            disableEditText(binding.wednesdayStartTime,binding.wednesdayEndTime,"enable");
        }

        }else if (view == binding.thursdayStartTime){
            openTimePicker(binding.thursdayStartTime);
        }else if (view == binding.thursdayEndTime){
            openTimePicker(binding.thursdayEndTime);
        }else if (view == binding.thursdayHoliday){
            if (((CheckBox) view).isChecked()) {
                disableEditText(binding.thursdayStartTime,binding.thursdayEndTime,"disable");
            }else{
                disableEditText(binding.thursdayStartTime,binding.thursdayEndTime,"enable");
            }

        }else if (view == binding.fridayStartTime){
            openTimePicker(binding.fridayStartTime);
        }else if (view == binding.fridayEndTime){
            openTimePicker(binding.fridayEndTime);
        }else if (view == binding.fridayHoliday){
            if (((CheckBox) view).isChecked()) {
                disableEditText(binding.fridayStartTime,binding.fridayEndTime,"disable");
            }else{
                disableEditText(binding.fridayStartTime,binding.fridayEndTime,"enable");
            }
        }else if (view == binding.saturdayStartTime){
            openTimePicker(binding.saturdayStartTime);
        }else if (view == binding.saturdayEndTime){
            openTimePicker(binding.saturdayEndTime);
        }else if (view == binding.saturdayHoliday){
            if (((CheckBox) view).isChecked()) {
                disableEditText(binding.saturdayStartTime,binding.saturdayEndTime,"disable");
            }else{
                disableEditText(binding.saturdayStartTime,binding.saturdayEndTime,"enable");
            }
        }else if (view == binding.sundayStartTime){
            openTimePicker(binding.sundayStartTime);
        }else if (view == binding.sundayEndTime){
            openTimePicker(binding.sundayEndTime);
        }else if (view == binding.sundayHoliday){
            if (((CheckBox) view).isChecked()) {
                disableEditText(binding.sundayStartTime,binding.sundayEndTime,"disable");
            }else{
                disableEditText(binding.sundayStartTime,binding.sundayEndTime,"enable");
            }
        }else if(view == binding.nextBtn){
            uploadOnFirestore();
        }

    }

    private void disableEditText(EditText StartTime, EditText EndTime, String what) {
        if (what.equals("disable")){
            StartTime.setText("Holiday");
            EndTime.setText("Holiday");
            StartTime.setClickable(false);
            EndTime.setClickable(false);
            StartTime.setHintTextColor(Color.parseColor("#A6A6A6"));
            EndTime.setHintTextColor(Color.parseColor("#A6A6A6"));
            StartTime.setTextColor(Color.parseColor("#A6A6A6"));
            EndTime.setTextColor(Color.parseColor("#A6A6A6"));
            StartTime.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.text_input_disabled));
            EndTime.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.text_input_disabled));
        }else if(what.equals("enable")){
            StartTime.setText("");
            EndTime.setText("");
            StartTime.setClickable(true);
            EndTime.setClickable(true);
            StartTime.setHintTextColor(Color.parseColor("#43DDE6"));
            EndTime.setHintTextColor(Color.parseColor("#43DDE6"));
            StartTime.setTextColor(Color.parseColor("#43DDE6"));
            EndTime.setTextColor(Color.parseColor("#43DDE6"));
            StartTime.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.text_input_box));
            EndTime.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.text_input_box));
        }

    }

    private void openTimePicker(EditText editText) {
        if(editText.getHintTextColors().toString().equals(defaultColor)){
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
        }else{
            Utilities.makeToast("Holiday",getApplicationContext());
        }

    }


}