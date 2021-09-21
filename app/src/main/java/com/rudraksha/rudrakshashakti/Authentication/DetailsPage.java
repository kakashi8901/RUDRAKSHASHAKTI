package com.rudraksha.rudrakshashakti.Authentication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rudraksha.rudrakshashakti.Common.MainActivity;
import com.rudraksha.rudrakshashakti.Common.ReconnectPage;
//import com.rudraksha.rudrakshashakti.Common.SplashScreen;
import com.rudraksha.rudrakshashakti.Pojo.ExpertDetails;
import com.rudraksha.rudrakshashakti.Utilities.InternetConnection;
import com.rudraksha.rudrakshashakti.Utilities.MyProgressDialog;
import com.rudraksha.rudrakshashakti.Utilities.Utilities;
import com.rudraksha.rudrakshashakti.databinding.ActivityDetailsPageBinding;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class DetailsPage extends AppCompatActivity implements View.OnClickListener{

    ActivityDetailsPageBinding aBinding;

    FirebaseFirestore database;
    private Uri imageUri;
    private StorageReference storageReference;

    private MyProgressDialog myProgressDialog;

    FirebaseAuth mAuth;

    String name,dateOfBirth,state,city,Profile_Pic_Uri,uid,gender,fathersName,EmailId,WhatsappNo,UpiNo,mainExperty,experience,remarks,price,referral;
    List<String> otherExperties = new ArrayList<String>();
    List<String> languages = new ArrayList<String>();
    boolean allSelected = false,allLanSelected=false;
    private MyProgressDialog progressDialog;

    @Override
    protected void onStart() {
        super.onStart();
        reconnect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Auth and storage
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        database = FirebaseFirestore.getInstance();
        uid = mAuth.getUid();

        //Incorporating View Binding
        aBinding = ActivityDetailsPageBinding.inflate(getLayoutInflater());
        View view = aBinding.getRoot();
        //Setting content view to Root view
        setContentView(view);
        setListeners();


        //set states array list
        setStates();
        setGender();
        setMainExperties();
    }

    private void setMainExperties() {
        List<String> popupItems = new ArrayList<String>();
        popupItems.add("Astrology");
        popupItems.add("Numerology");
        popupItems.add("Vastu Shastra");
        popupItems.add("Lal Kitab");
        popupItems.add("Tarot Card");
        aBinding.mainExperty.setItems(popupItems);
    }


    /**Reconnects and also checks internet connection*/
    public void reconnect() {
        progressDialog = new MyProgressDialog();
        progressDialog.showDialog(this);
        if (InternetConnection.checkConnection(this)) {
            progressDialog.dismissDialog();

        } else {
            progressDialog.dismissDialog();
            Intent intent = new Intent(this, ReconnectPage.class);
            startActivity(intent);
        }
    }


    /**
     * set Gender Array adapter*/
    private void setGender() {
        aBinding.gender.setThreshold(0);
        final String[] genders = new String[]{"Male" , "Female" , "Other"};
        ArrayAdapter<String> gender = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, genders);
        aBinding.gender.setAdapter(gender);
    }



    /**
     * set State Array adapter*/
    private void setStates() {
        aBinding.inputStateOfBirth.setThreshold(1);
        final String[] states = new String[]{"Andhra Pradesh","Arunachal Pradesh","Assam","Bihar","Chhattisgarh","Goa","Gujarat","Haryana","Himachal Pradesh","Jammu and Kashmir","Jharkhand","Karnataka","Kerala","Madhya Pradesh","Maharashtra","Manipur","Meghalaya","Mizoram","Nagaland","Odisha","Punjab","Rajasthan","Sikkim","Tamil Nadu","Telangana","Tripura","Uttarakhand","Uttar Pradesh","West Bengal","Andaman and Nicobar Islands","Chandigarh","Dadra and Nagar Haveli","Daman and Diu","Delhi","Lakshadweep","Puducherry"};
        ArrayAdapter<String> state = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, states);
        aBinding.inputStateOfBirth.setAdapter(state);
    }


    private void setListeners() {
        aBinding.nextBtn.setOnClickListener(this);
        aBinding.backBtn.setOnClickListener(this);
        aBinding.chooseImage.setOnClickListener(this);
        aBinding.inputDateOfBirth.setOnClickListener(this);
        aBinding.inputStateOfBirth.setOnClickListener(this);
        aBinding.gender.setOnClickListener(this);
        aBinding.all.setOnClickListener(this);
        aBinding.astrology.setOnClickListener(this);
        aBinding.numerology.setOnClickListener(this);
        aBinding.tarotCard.setOnClickListener(this);
        aBinding.vastuShastra.setOnClickListener(this);
        aBinding.lalKitab.setOnClickListener(this);
        aBinding.allLang.setOnClickListener(this);
        aBinding.English.setOnClickListener(this);
        aBinding.Hindi.setOnClickListener(this);
        aBinding.bengali.setOnClickListener(this);
        aBinding.kannada.setOnClickListener(this);
        aBinding.malayalam.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == aBinding.nextBtn) {
            next();
        } else if (view == aBinding.backBtn) {
            back();
        } else if (view == aBinding.chooseImage) {
            choosePicture();
        }else if (view == aBinding.inputDateOfBirth){
            selectDate();
        }else if(view == aBinding.inputStateOfBirth){
            selectState();
        }else if(view == aBinding.gender){
            selectGender();
        }else if(view == aBinding.all){
                aBinding.all.setClickable(true);
                if (((CheckBox) view).isChecked()) {
                    allSelected = true;
                    aBinding.astrology.setChecked(true);
                    aBinding.numerology.setChecked(true);
                    aBinding.vastuShastra.setChecked(true);
                    aBinding.tarotCard.setChecked(true);
                    aBinding.lalKitab.setChecked(true);
                    aBinding.astrology.setClickable(false);
                    aBinding.numerology.setClickable(false);
                    aBinding.vastuShastra.setClickable(false);
                    aBinding.tarotCard.setClickable(false);
                    aBinding.lalKitab.setClickable(false);
                } else {
                    allSelected = false;
                    aBinding.astrology.setChecked(false);
                    aBinding.numerology.setChecked(false);
                    aBinding.vastuShastra.setChecked(false);
                    aBinding.tarotCard.setChecked(false);
                    aBinding.lalKitab.setChecked(false);

                    aBinding.astrology.setClickable(true);
                    aBinding.numerology.setClickable(true);
                    aBinding.vastuShastra.setClickable(true);
                    aBinding.tarotCard.setClickable(true);
                    aBinding.lalKitab.setClickable(true);
                }
            otherExperties.clear();
            getOtherExperties("all","add");
        }else if(view == aBinding.astrology){
            if (((CheckBox) view).isChecked()) {
                getOtherExperties("Astrology","add");
            }else{
                getOtherExperties("Astrology","remove");
            }
        }else if(view == aBinding.numerology){
            if (((CheckBox) view).isChecked()) {
                getOtherExperties("Numerology","add");
            }else{
                getOtherExperties("Numerology","remove");
            }
        }else if(view == aBinding.vastuShastra){
            if (((CheckBox) view).isChecked()) {
                getOtherExperties("Vastu Shastra","add");
            }else{
                getOtherExperties("Vastu Shastra","remove");
            }
        }else if(view == aBinding.lalKitab){
            if (((CheckBox) view).isChecked()) {
                getOtherExperties("Lal Kitab","add");
            }else{
                getOtherExperties("Lal Kitab","remove");
            }
        }else if(view == aBinding.tarotCard){
            if (((CheckBox) view).isChecked()) {
                getOtherExperties("Tarot Card","add");
            }else{
                getOtherExperties("Tarot Card","remove");
            }
        }else if(view == aBinding.allLang){
            aBinding.allLang.setClickable(true);
            if (((CheckBox) view).isChecked()) {
                allLanSelected = true;
                aBinding.English.setChecked(true);
                aBinding.bengali.setChecked(true);
                aBinding.Hindi.setChecked(true);
                aBinding.kannada.setChecked(true);
                aBinding.malayalam.setChecked(true);


                aBinding.English.setClickable(false);
                aBinding.bengali.setClickable(false);
                aBinding.Hindi.setClickable(false);
                aBinding.kannada.setClickable(false);
                aBinding.malayalam.setClickable(false);
            } else {
                allLanSelected = false;
                aBinding.English.setChecked(false);
                aBinding.bengali.setChecked(false);
                aBinding.Hindi.setChecked(false);
                aBinding.kannada.setChecked(false);
                aBinding.malayalam.setChecked(false);


                aBinding.English.setClickable(true);
                aBinding.bengali.setClickable(true);
                aBinding.Hindi.setClickable(true);
                aBinding.kannada.setClickable(true);
                aBinding.malayalam.setClickable(true);
            }
            languages.clear();
            getLanguages("all","add");
        }else if(view == aBinding.English){
            if (((CheckBox) view).isChecked()) {
               getLanguages("English","add");
            }else{
               getLanguages("English","remove");
            }
        }else if(view == aBinding.Hindi){
            if (((CheckBox) view).isChecked()) {
               getLanguages("Hindi","add");
            }else{
               getLanguages("Hindi","remove");
            }
        }else if(view == aBinding.bengali){
            if (((CheckBox) view).isChecked()) {
               getLanguages("Bengali","add");
            }else{
               getLanguages("Bengali","remove");
            }
        }else if(view == aBinding.kannada){
            if (((CheckBox) view).isChecked()) {
               getLanguages("Kannada","add");
            }else{
               getLanguages("Kannada","remove");
            }
        }else if(view == aBinding.malayalam){
            if (((CheckBox) view).isChecked()) {
               getLanguages("Malayalam","add");
            }else{
               getLanguages("Malayalam","remove");
            }
        }
    }

    private void getLanguages(String item, String what) {
        if (item.equals("all")){

            if(allLanSelected){
                languages.add("English");
                languages.add("Bengali");
                languages.add("Hindi");
                languages.add("Kannada");
                languages.add("Malayalam");
            }else if(!allLanSelected){
                languages.remove("English");
                languages.remove("Bengali");
                languages.remove("Hindi");
                languages.remove("Kannada");
                languages.remove("Malayalam");
            }
        }else{
            if (what.equals("add")){
                languages.add(item);
            }else if(what.equals("remove")){
                languages.remove(item);
            }
        }
    }


    /**
     * It will save all users details and upload it to firestore*/
    private void next() {
        getDetails();
        if (name.equals("") || dateOfBirth.equals("") || gender.equals("") || state.equals("") || city.equals("") || fathersName.equals("") || EmailId.equals("") || WhatsappNo.equals("") || UpiNo.equals("") || mainExperty.equals("") || experience.equals("") || remarks.equals("") || languages.isEmpty() || price.equals("")) {
            Utilities.makeToast("Enter all Required details", getApplicationContext());
        }else{
            for( int i=0;i<otherExperties.size();i++){
                if(otherExperties.get(i).equals(mainExperty)){
                    otherExperties.remove(i);
                }
            }
            getOtherExperties(mainExperty,"add");
            compressAndUploadDetails();
        }
    }

    /**
     * works as a back function */
    private void back() {
        Intent intent = getIntent();
        boolean fromSplashScreen = intent.getExtras().getBoolean("fromSplashScreen");
        if(fromSplashScreen){
            this.finish();
        }else{
            mAuth.signOut();
            this.finish();
        }
    }



    /**
     * It opens the images in gallery and allows to set profile image
     * */
    private void choosePicture(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }


    /**
     * On activity result it sets Profile image from gallery
     * */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri=data.getData();
            aBinding.profilePic.setImageURI(imageUri);
        }
    }


    /**
     * It will upload the image to firebase storage and then it gets the storage url and calls the function to set users details in firestore
     * */
    private void compressAndUploadDetails(){
        myProgressDialog = new MyProgressDialog();
        if(imageUri != null){
           uploadImage();
        }else{
            Utilities.makeToast("Please Select a profile image", this);
        }
    }


    /**
     * Uploads the image to firebase after reducing the size*/
    private void uploadImage() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] final_profile_image = baos.toByteArray();
            myProgressDialog.showDialog(this);
            storageReference.child("ExpertsProfilePhoto/").child(uid+"_profile_pic").putBytes(final_profile_image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.child("ExpertsProfilePhoto/").child(uid+"_profile_pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Profile_Pic_Uri = uri.toString();
                            Utilities.makeToast("Upload image sucessfull!!", DetailsPage.this);
                            UploadInFirestore();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Utilities.makeToast("Sorry image can't be uploaded try again!!", DetailsPage.this);
                        }
                    });
                }
            });
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    /**
     * Get all the input values by users and store it in string*/
    private void getDetails(){
         name = aBinding.inputName.getText().toString();
         dateOfBirth = aBinding.inputDateOfBirth.getText().toString();
         gender = aBinding.gender.getText().toString();
         state = aBinding.inputStateOfBirth.getText().toString();
         city = aBinding.inputCityOfBirth.getText().toString();
         fathersName = aBinding.inputFathersName.getText().toString();
         EmailId = aBinding.inputEmailId.getText().toString();
         WhatsappNo = aBinding.inputWhatsappNumber.getText().toString();
         UpiNo = aBinding.inputUPINumber.getText().toString();
         mainExperty = aBinding.mainExperty.getText().toString();
         experience = aBinding.inputExperience.getText().toString();
         remarks = aBinding.remarks.getText().toString();
         price = aBinding.inputPrice.getText().toString();
         referral = aBinding.inputReferral.getText().toString();

    }

    private void getOtherExperties(String item, String what) {
        if (item.equals("all")){

            if(allSelected){
                otherExperties.add("Astrology");
                otherExperties.add("Vastu Shastra");
                otherExperties.add("Numerology");
                otherExperties.add("Lal Kitab");
                otherExperties.add("Tarot Cards");
            }else if(!allSelected){
                otherExperties.remove("Astrology");
                otherExperties.remove("Vastu Shastra");
                otherExperties.remove("Numerology");
                otherExperties.remove("Lal Kitab");
                otherExperties.remove("Tarot Cards");
            }
        }else{
             if (what.equals("add")){
                 otherExperties.add(item);
             }else if(what.equals("remove")){
                 otherExperties.remove(item);
             }
        }
    }


    /**
     * opens a date selector popup dialog*/
    private void selectDate(){
        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(DetailsPage.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                String date = dayOfMonth+" / "+month+" / "+year;
                aBinding.inputDateOfBirth.setText(date);
            }
        }, year, month, day);
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();
    }

    /**
     * opens a state selector menu list*/
    private void selectState(){
        aBinding.inputStateOfBirth.showDropDown();
    }

    /**
     * opens a gender selector menu list*/
    private void selectGender(){aBinding.gender.showDropDown();}


    /**
     * Posts users details in firestore*/
    public void UploadInFirestore() {
        Random rand = new Random();
        int no = rand.nextInt(100000);
        ExpertDetails expertDetails = new ExpertDetails();
        expertDetails.setServices(otherExperties);
        expertDetails.setLanguages(languages);
        expertDetails.setBackground(remarks);
        expertDetails.setCity(city);
        expertDetails.setState(state);
        expertDetails.setName(name);
        expertDetails.setFathersName(fathersName);
        expertDetails.setEmailId(EmailId);
        expertDetails.setWhatsappNo(WhatsappNo);
        expertDetails.setDateOfBirth(dateOfBirth);
        expertDetails.setRating("3");
        expertDetails.setMainService(mainExperty);
        expertDetails.setExperience(experience);
        expertDetails.setGender(gender);
        expertDetails.setUpiNo(UpiNo);
        expertDetails.setProfilePic(Profile_Pic_Uri);
        expertDetails.setUid(uid);
        expertDetails.setPrice(price);
        expertDetails.setReferral(referral);
        expertDetails.setUnderReview("true");
        expertDetails.setMyCode(name.split(" ")[0]+String.valueOf(no));
        database = FirebaseFirestore.getInstance();
        database.collection(mainExperty).document(uid).set(expertDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                myProgressDialog.dismissDialog();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                myProgressDialog.dismissDialog();
            }
        });
    }




    /**
     * send user to main home page*/
    private void SendToHomeActivity() {
//        SplashScreen.encrypt.putString("details_filled", "true");
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }



}