package com.rudraksha.rudrakshashakti.Common;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.rudraksha.rudrakshashakti.Authentication.ChooseAuthentication;
import com.rudraksha.rudrakshashakti.Authentication.DetailsPage;
import com.rudraksha.rudrakshashakti.Authentication.UnderReview;
import com.rudraksha.rudrakshashakti.R;
import com.rudraksha.rudrakshashakti.Utilities.EncryptedSharedPref;
import com.rudraksha.rudrakshashakti.Utilities.InternetConnection;
import com.rudraksha.rudrakshashakti.Utilities.MyProgressDialog;
import com.rudraksha.rudrakshashakti.Utilities.Utilities;
import com.rudraksha.rudrakshashakti.Utilities.VolleySingleton;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SplashScreen extends AppCompatActivity {


    public static EncryptedSharedPref encrypt;
    FirebaseFirestore database;
    private MyProgressDialog progressDialog;
    String uid,expertMainService="",underReview="false";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        database = FirebaseFirestore.getInstance();


        encrypt = new EncryptedSharedPref(this, "details_filled");
        encrypt = new EncryptedSharedPref(this, "under_review");

        String e = encrypt.getString("details_filled");
        String f = encrypt.getString("details_filled");

        if (e == null) {
            encrypt.putString("details_filled", "false");
        }
        if (f == null){
            encrypt.putString("under_review", "false");
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        uid =  mAuth.getUid();;
        if (user != null) {
            //User is Logged in
            if (encrypt.getString("details_filled").equals("false") || encrypt.getString("under_review").equals("false")) {
                reconnect();
                checkUnderReviewOrNot();
            }else {
                //User has filled Details page
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent details = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(details);
                        finish();
                    }
                },1200);
            }

        } else {
            //No User is Logged in
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent chooseAuth = new Intent(getApplicationContext(), ChooseAuthentication.class);
                    startActivity(chooseAuth);
                    finish();
                }
            },1200);
        }
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
                                    encrypt.putString("details_filled", "true");
                                    Intent intent = new Intent(getApplicationContext(), UnderReview.class);
                                    startActivity(intent);
                                    finish();
                                }else if (underReview.equals("false")){
                                    encrypt.putString("details_filled", "true");
                                    encrypt.putString("under_review", "false");
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

}