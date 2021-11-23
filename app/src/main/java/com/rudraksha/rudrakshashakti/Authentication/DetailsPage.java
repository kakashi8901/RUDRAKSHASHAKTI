package com.rudraksha.rudrakshashakti.Authentication;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rudraksha.rudrakshashakti.Common.MainActivity;
import com.rudraksha.rudrakshashakti.Common.ReconnectPage;
//import com.rudraksha.rudrakshashakti.Common.SplashScreen;
import com.rudraksha.rudrakshashakti.Common.SplashScreen;
import com.rudraksha.rudrakshashakti.Pojo.ExpertDetails;
import com.rudraksha.rudrakshashakti.Pojo.ExpertCourseDetails;
import com.rudraksha.rudrakshashakti.R;
import com.rudraksha.rudrakshashakti.Utilities.InternetConnection;
import com.rudraksha.rudrakshashakti.Utilities.MyProgressDialog;
import com.rudraksha.rudrakshashakti.Utilities.Utilities;
import com.rudraksha.rudrakshashakti.databinding.ActivityDetailsPageBinding;
import com.sinaseyfi.advancedcardview.AdvancedCardView;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;


public class DetailsPage extends AppCompatActivity implements View.OnClickListener{

    ActivityDetailsPageBinding aBinding;

    FirebaseFirestore database;
    private Uri imageUri;
    private StorageReference storageReference;

    private MyProgressDialog myProgressDialog;

    FirebaseAuth mAuth;

    String name,dateOfBirth,state,city,Profile_Pic_Uri,uid,gender,fathersName,EmailId,WhatsappNo,UpiNo,mainExperty,experience,remarks,price,referral,availableForCourses,courseMode,DurationOfCourse,sessions,BasicCoursePrice,AdvanceCoursePrice,expertNote,ExpertMainCourse;
    List<String> otherExperties = new ArrayList<String>();
    List<String> coursesList = new ArrayList<String>();
    List<String> languages = new ArrayList<String>();
    List<String> pujans = new ArrayList<String>();
    boolean allSelected = false,agreed=false,allLanSelected=false,doesAllPuja=false,callSelected = false;
    private MyProgressDialog progressDialog;

    TextView txtPoojans;
    CheckBox poojans;

    LinearLayout courseLayout;

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

        courseLayout =(LinearLayout)findViewById(R.id.coursesLayoutBox);
        courseLayout.setVisibility(View.GONE);

        //set states array list
        setStates();
        setGender();
        getPujas();
        setMainExperties();
        setCourseMode();
    }

    private void getPujas() {
        myProgressDialog = new MyProgressDialog();
        myProgressDialog.showDialog(this);

        database.collection("Poojas")
                .orderBy("id", Query.Direction.ASCENDING)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                pujans.clear();
                for (DocumentSnapshot snapshot : queryDocumentSnapshots){
                    pujans.add(snapshot.getString("uid"));

                }
                myProgressDialog.dismissDialog();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Utilities.makeToast(e.getMessage(),getApplicationContext());
                myProgressDialog.dismissDialog();
            }
        });
    }
    private void openPrivacyPolicy() {
        Dialog dialog = new Dialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.privacy_policy_dialog,null);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        AdvancedCardView done = view.findViewById(R.id.agreeBtn);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                agreed=true;
            }
        });
        dialog.setContentView(view);
        dialog.show();
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
        final String[] genders = new String[]{"Male" , "Female"};
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
        aBinding.call.setOnClickListener(this);
        aBinding.castrology.setOnClickListener(this);
        aBinding.cnumerology.setOnClickListener(this);
        aBinding.ctarotCard.setOnClickListener(this);
        aBinding.cvastuShastra.setOnClickListener(this);
        aBinding.clalKitab.setOnClickListener(this);
        aBinding.cmobileNumerology.setOnClickListener(this);
        aBinding.allLang.setOnClickListener(this);
        aBinding.English.setOnClickListener(this);
        aBinding.Hindi.setOnClickListener(this);
        aBinding.bengali.setOnClickListener(this);
        aBinding.kannada.setOnClickListener(this);
        aBinding.malayalam.setOnClickListener(this);
        aBinding.pujans.setOnClickListener(this);
        aBinding.coursesCheckbox.setOnClickListener(this);
        aBinding.courseMode.setOnClickListener(this);
        aBinding.inputDuration.setOnClickListener(this);
        aBinding.inputSessions.setOnClickListener(this);
        aBinding.inputBasicCoursesPrice.setOnClickListener(this);
        aBinding.inputAdvanceCoursesPrice.setOnClickListener(this);
        aBinding.expertNote.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        txtPoojans = findViewById(R.id.txtPoojans);
        poojans = findViewById(R.id.pujans);
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
        }else if(view == aBinding.courseMode){
            selectCourseMode();
        }
        else if(view == aBinding.all){
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

                txtPoojans.setVisibility(View.VISIBLE);
                poojans.setVisibility(View.VISIBLE);
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

                txtPoojans.setVisibility(View.GONE);
                poojans.setVisibility(View.GONE);
            }
            otherExperties.clear();
            getOtherExperties("all","add");
        }else if(view == aBinding.astrology){
            if (((CheckBox) view).isChecked()) {
                getOtherExperties("Astrology","add");

                txtPoojans.setVisibility(View.VISIBLE);
                poojans.setVisibility(View.VISIBLE);
            }else{
                getOtherExperties("Astrology","remove");

                txtPoojans.setVisibility(View.GONE);
                poojans.setVisibility(View.GONE);
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
        }else if(view == aBinding.coursesCheckbox) {
            aBinding.coursesCheckbox.setClickable(true);
            if (((CheckBox) view).isChecked()){
                availableForCourses = "yes";
                ShowCourseLayout();
            }
            else if(!((CheckBox) view).isChecked()){
                availableForCourses = "no";
                HideCourseLayout();
            }

        }else if(view == aBinding.call){
                aBinding.call.setClickable(true);
                    if (((CheckBox) view).isChecked()) {
                        callSelected = true;
                        aBinding.castrology.setChecked(true);
                        aBinding.cnumerology.setChecked(true);
                        aBinding.cvastuShastra.setChecked(true);
                        aBinding.ctarotCard.setChecked(true);
                        aBinding.clalKitab.setChecked(true);
                        aBinding.cmobileNumerology.setChecked(true);

                        aBinding.castrology.setClickable(false);
                        aBinding.cnumerology.setClickable(false);
                        aBinding.cvastuShastra.setClickable(false);
                        aBinding.ctarotCard.setClickable(false);
                        aBinding.clalKitab.setClickable(false);
                        aBinding.cmobileNumerology.setClickable(false);

                    } else {
                        callSelected = false;
                        aBinding.castrology.setChecked(false);
                        aBinding.cnumerology.setChecked(false);
                        aBinding.cvastuShastra.setChecked(false);
                        aBinding.ctarotCard.setChecked(false);
                        aBinding.clalKitab.setChecked(false);
                        aBinding.castrology.setClickable(true);
                        aBinding.cnumerology.setClickable(true);
                        aBinding.cvastuShastra.setClickable(true);
                        aBinding.ctarotCard.setClickable(true);
                        aBinding.clalKitab.setClickable(true);
                        aBinding.cmobileNumerology.setClickable(true);

                    }

                    coursesList.clear();
                    getCourses("all","add");

                }
        else if(view == aBinding.castrology){
            if (((CheckBox) view).isChecked()) {
                getCourses("Astrology","add");
            }else{
                getCourses("Astrology","remove");
            }
        }else if(view == aBinding.cnumerology){
            if (((CheckBox) view).isChecked()) {
                getCourses("Numerology","add");
            }else{
                getCourses("Numerology","remove");
            }
        }else if(view == aBinding.cvastuShastra){
            if (((CheckBox) view).isChecked()) {
                getCourses("Vastu Shastra","add");
            }else{
                getCourses("Vastu Shastra","remove");
            }
        }else if(view == aBinding.clalKitab){
            if (((CheckBox) view).isChecked()) {
                getCourses("Lal Kitab","add");
            }else{
                getCourses("Lal Kitab","remove");
            }
        }else if(view == aBinding.ctarotCard){
            if (((CheckBox) view).isChecked()) {
                getCourses("Tarot Card","add");
            }else{
                getCourses("Tarot Card","remove");
            }
        }else if(view == aBinding.cmobileNumerology){
            if (((CheckBox) view).isChecked()) {
                getCourses("Mobile Numerology","add");
            }else{
                getCourses("Mobile Numerology","remove");
            }
        }
        else if(view == aBinding.allLang){
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
        }else if(view == aBinding.pujans){
            if (((CheckBox) view).isChecked()) {
                doesAllPuja = true;
            }else{
                doesAllPuja =false;
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
        if (agreed) {
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
        }else {
            openPrivacyPolicy();
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
                            uploadCoursesinFirestore();
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
        courseMode = aBinding.courseMode.getText().toString();
        DurationOfCourse = aBinding.inputDuration.getText().toString();
        sessions = aBinding.inputSessions.getText().toString();
        BasicCoursePrice = aBinding.inputBasicCoursesPrice.getText().toString();
        AdvanceCoursePrice = aBinding.inputAdvanceCoursesPrice.getText().toString();
        expertNote = aBinding.expertNote.getText().toString();



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

    private void getCourses(String item, String what) {
        if (item.equals("all")){

            if(callSelected){
                coursesList.add("Astrology");
                coursesList.add("Vastu Shastra");
                coursesList.add("Numerology");
                coursesList.add("Lal Kitab");
                coursesList.add("Tarot Cards");
                coursesList.add("Mobile numerology");

            }else if(!callSelected){
                coursesList.remove("Astrology");
                coursesList.remove("Vastu Shastra");
                coursesList.remove("Numerology");
                coursesList.remove("Lal Kitab");
                coursesList.remove("Tarot Cards");
                coursesList.remove("Mobile numerology");
            }
        }else{
            if (what.equals("add")){
                coursesList.add(item);
            }else if(what.equals("remove")){
                coursesList.remove(item);
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
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()- 568025136000L);
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
        expertDetails.setRating("No Rating");
        expertDetails.setMainService(mainExperty);
        expertDetails.setExperience(experience);
        expertDetails.setGender(gender);
        expertDetails.setUpiNo(UpiNo);
        expertDetails.setProfilePic(Profile_Pic_Uri);
        expertDetails.setUid(uid);
        expertDetails.setPrice(price);
        if(!doesAllPuja){
            pujans.clear();
        }
        expertDetails.setPoojas(pujans);
        expertDetails.setReferral(referral);
        expertDetails.setUnderReview("true");
        expertDetails.setMyCode(name.split(" ")[0]+String.valueOf(no));
        database = FirebaseFirestore.getInstance();
        database.collection(mainExperty).document(uid).set(expertDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                if(doesAllPuja){
                    for (int i = 0 ; i< pujans.size(); i++) {
                        database.collection("Poojas").document(pujans.get(i)).update("experts", FieldValue.arrayUnion(mainExperty+"$"+uid)).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                myProgressDialog.dismissDialog();
                                SendToHomeActivity();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                Utilities.makeToast("Cant add try again !",getApplicationContext());

                            }
                        });
                    }
                }else{
                    myProgressDialog.dismissDialog();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                myProgressDialog.dismissDialog();
            }
        });
    }

    public void uploadCoursesinFirestore(){
        Random rand = new Random();
        int no = rand.nextInt(100000);
        ExpertCourseDetails expertCourseDetails = new ExpertCourseDetails();
        expertCourseDetails.setavailableForCourses(availableForCourses);
        expertCourseDetails.setCourses(coursesList);
        expertCourseDetails.setCourseMode(courseMode);
        expertCourseDetails.setDurationOfCourse(DurationOfCourse);
        expertCourseDetails.setsessions(sessions);
        expertCourseDetails.setBasicCoursePrice(BasicCoursePrice);
        expertCourseDetails.setAdvanceCoursePrice(AdvanceCoursePrice);
        expertCourseDetails.setexpertNote(expertNote);


        database = FirebaseFirestore.getInstance();
        if(aBinding.castrology.isChecked()){
            {
        database.collection("Astrology Courses").document(uid).set(expertCourseDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                myProgressDialog.dismissDialog();
                SendToHomeActivity();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Utilities.makeToast("Cant add try again !",getApplicationContext());
                myProgressDialog.dismissDialog();
            }
        });

    }}

        if(aBinding.cnumerology.isChecked()){
            {
                database.collection("Numerology Courses").document(uid).set(expertCourseDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        myProgressDialog.dismissDialog();
                        SendToHomeActivity();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Utilities.makeToast("Cant add try again !",getApplicationContext());
                        myProgressDialog.dismissDialog();
                    }
                });

            }}

        if(aBinding.ctarotCard.isChecked()){
            {
                database.collection("Tarot Card Courses").document(uid).set(expertCourseDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        myProgressDialog.dismissDialog();
                        SendToHomeActivity();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Utilities.makeToast("Cant add try again !",getApplicationContext());
                        myProgressDialog.dismissDialog();
                    }
                });

            }}

        if(aBinding.clalKitab.isChecked()){
            {
                database.collection("Lal Kitab Courses").document(uid).set(expertCourseDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        myProgressDialog.dismissDialog();
                        SendToHomeActivity();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Utilities.makeToast("Cant add try again !",getApplicationContext());
                        myProgressDialog.dismissDialog();
                    }
                });

            }}

        if(aBinding.cvastuShastra.isChecked()){
            {
                database.collection("Vastu Shastra Courses").document(uid).set(expertCourseDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        myProgressDialog.dismissDialog();
                        SendToHomeActivity();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Utilities.makeToast("Cant add try again !",getApplicationContext());
                        myProgressDialog.dismissDialog();
                    }
                });

            }}

        if(aBinding.cmobileNumerology.isChecked()){
            {
                database.collection("Mobile Numerology Courses").document(uid).set(expertCourseDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        myProgressDialog.dismissDialog();
                        SendToHomeActivity();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Utilities.makeToast("Cant add try again !",getApplicationContext());
                        myProgressDialog.dismissDialog();
                    }
                });

            }}

    }



    public void ShowCourseLayout(){
        courseLayout =(LinearLayout)findViewById(R.id.coursesLayoutBox);
        courseLayout.setVisibility(View.VISIBLE);

    }

    public void HideCourseLayout(){
        courseLayout =(LinearLayout)findViewById(R.id.coursesLayoutBox);
        courseLayout.setVisibility(View.GONE);

    }


    private void setCourseMode() {
        aBinding.courseMode.setThreshold(0);
        final String[] modes = new String[]{"online" , "offline"};
        ArrayAdapter<String> mode = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, modes);
        aBinding.courseMode.setAdapter(mode);
    }

    private void selectCourseMode(){aBinding.courseMode.showDropDown();}


    /**
     * send user to main home page*/
    private void SendToHomeActivity() {
        Intent intent = new Intent(getApplicationContext(), SelectTimeslots.class);
        startActivity(intent);
    }





}