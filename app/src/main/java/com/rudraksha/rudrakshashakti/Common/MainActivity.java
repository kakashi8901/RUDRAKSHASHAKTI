package com.rudraksha.rudrakshashakti.Common;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rudraksha.rudrakshashakti.Authentication.ChooseAuthentication;
import com.rudraksha.rudrakshashakti.R;
import com.rudraksha.rudrakshashakti.Utilities.Utilities;
import com.rudraksha.rudrakshashakti.VideoCall.OutgoingCallInvitation;
import com.rudraksha.rudrakshashakti.databinding.ActivityDetailsPageBinding;
import com.rudraksha.rudrakshashakti.databinding.ActivityMainBinding;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    ActivityMainBinding binding;
    FirebaseAuth mAuth;
    String callUserToken = "cXbSkffvQvSJh0YShOmkgz:APA91bF4kXX_MR-fa3d-bkOpHNWuEpb_J7C5-V6xaRTVFx8lWOuvwUC0VernPKUbRpyMMqBfMfkfv1lVUlbrZsRgIr7UU87qCkvn2SS5ICHXbAXRr3gbV2RwC_PKWa7KXnfO621BYKG_",
            callUserName="sdgs",
            callUserTimeslot="sgg",
            callUserImage="hgf";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        //Setting content view to Root view
        setContentView(view);

        mAuth = FirebaseAuth.getInstance();
        setListeners();
        setToken();
        setUpcomingPreviousCalls();
    }

    private void setUpcomingPreviousCalls() {
    }

    private void setListeners() {
        binding.audioCall.setOnClickListener(this);
        binding.videoCall.setOnClickListener(this);
        binding.logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
       if (view == binding.logout){
          logout();
       }else if(view == binding.audioCall){
           openAudioMeeting("AudioOnly");
       }else if (view == binding.videoCall){
           openAudioMeeting("VideoOnly");
       }
    }

    private void logout() {
        Utilities.makeToast("Logging Out ...",getApplicationContext());
        FirebaseFirestore database = FirebaseFirestore.getInstance();
//        DocumentReference documentReference = database.collection("users").document(mAuth.getUid());
//        HashMap<String , Object> updates = new HashMap<>();
//        updates.put("FCMToken", FieldValue.delete());
//        documentReference.update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void unused) {
//                mAuth.signOut();
//                Intent intent = new Intent(getApplicationContext(), ChooseAuthentication.class);
//                startActivity(intent);
//                finish();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull @NotNull Exception e) {
//                Utilities.makeToast("Sorry failed to logout! Try Again",getApplicationContext());
//            }
//        });
    }



    private void setToken() {
//        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
//            if(task.isSuccessful() && task.getResult() != null){
//                sendFCMTokenTODatabase(task.getResult().getToken());
//            }
//        });
    }
    private void sendFCMTokenTODatabase(String token){
//        FirebaseFirestore database = FirebaseFirestore.getInstance();
//        DocumentReference documentReference = database.collection("users").document(FirebaseAuth.getInstance().getUid());
//        documentReference.update("FCMToken",token)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull @NotNull Exception e) {
//                Utilities.makeToast("Token Not Successfully",getApplicationContext());
//            }
//        });
    }


    private void openAudioMeeting(String type) {
        if (callUserToken == null && callUserToken.trim().isEmpty()) {
            Utilities.makeToast(callUserName + " is not available for meeting", getApplicationContext());
        } else {
            Intent intent = new Intent(this, OutgoingCallInvitation.class);
            intent.putExtra("MeetingType", type);
            intent.putExtra("ExpertName", callUserName);
            intent.putExtra("Timeslot", callUserTimeslot);
            intent.putExtra("ExpertToken", callUserToken);
            intent.putExtra("ExpertImage", callUserImage);
            startActivity(intent);
        }
    }

}