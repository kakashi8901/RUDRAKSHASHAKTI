package com.rudraksha.rudrakshashakti.Common;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rudraksha.rudrakshashakti.Adapters.PreviousCallsAdapter;
import com.rudraksha.rudrakshashakti.Adapters.UpcomingCallsAdapter;
import com.rudraksha.rudrakshashakti.Authentication.ChooseAuthentication;
import com.rudraksha.rudrakshashakti.Pojo.PreviousCalls;
import com.rudraksha.rudrakshashakti.Pojo.UpcommingCalls;
import com.rudraksha.rudrakshashakti.R;
import com.rudraksha.rudrakshashakti.Utilities.InternetConnection;
import com.rudraksha.rudrakshashakti.Utilities.MyProgressDialog;
import com.rudraksha.rudrakshashakti.Utilities.Utilities;
import com.rudraksha.rudrakshashakti.VideoCall.OutgoingCallInvitation;
import com.rudraksha.rudrakshashakti.databinding.ActivityDetailsPageBinding;
import com.rudraksha.rudrakshashakti.databinding.ActivityMainBinding;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    ActivityMainBinding binding;
    private String URL = "https://us-central1-cosmic-solutions-7388c.cloudfunctions.net/expertMainScreenData";
    FirebaseAuth mAuth;
    String uid;
    private String mainService=null;
    private MyProgressDialog myProgressDialog;
    String callUserToken ,
            callUserName,
            callUserTimeslot,
            callUserImage,callExpertImage,callExpertName;


    JSONObject userCallDetails;

    @Override
    protected void onStart() {
        super.onStart();
        reconnect();
    }
    /**Reconnects and also checks internet connection*/
    public void reconnect() {
        if (!InternetConnection.checkConnection(this)) {
            Intent intent = new Intent(this, ReconnectPage.class);
            startActivity(intent);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        //Setting content view to Root view
        setContentView(view);

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();
        setListeners();
        getUpcomingAndPreviousCalls();
    }

    /**
     * Get Upcomming Calls from firestore*/
    public void getUpcomingAndPreviousCalls() {
        Map<String, String> data = new HashMap<>();
        data.put("uid",uid);

        RequestQueue queue = Volley.newRequestQueue(this);

        myProgressDialog = new MyProgressDialog();
        myProgressDialog.showDialog(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL,
                new JSONObject(data),
                new Response.Listener<JSONObject>() {
                boolean expertExist = false;
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean callExist = false;
                            expertExist = response.getBoolean("expertExist");
                            if (expertExist){
                                callExpertName= response.getString("expertName");
                                callExpertImage = response.getString("expertProfilePic");
                                Picasso.with(getApplicationContext())
                                        .load(response.getString("expertProfilePic"))
                                        .into(binding.expertProfilePic);
                                binding.expertName.setText(response.getString("expertName"));
                                binding.expertLocation.setText(response.getString("expertState")+" "+response.getString("expertCity"));
                                binding.expertRating.setText(response.getString("expertRating"));
                                mainService = response.getString("mainService");
                                callExist = response.getBoolean("callExist");
                                Utilities.makeToast(String.valueOf(callExist),getApplicationContext());
                                if(callExist){
                                    userCallDetails = response.getJSONObject("calldata");
                                    callUserImage = userCallDetails.getString("userProfilePic");
                                    Toast.makeText(MainActivity.this, callUserName, Toast.LENGTH_SHORT).show();
                                    callUserName = userCallDetails.getString("userName");
                                    callUserTimeslot = userCallDetails.getString("timeSlot");
                                    String userUid = userCallDetails.getString("userUid");
                                    FirebaseFirestore database = FirebaseFirestore.getInstance();
                                    database.collection("users").document(userUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            callUserToken = documentSnapshot.getString("FCMToken");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull @NotNull Exception e) {
                                            Utilities.makeToast("User is not Registered",getApplicationContext());
                                        }
                                    });
                                    binding.callUserName.setText(callUserName);
                                    binding.callUserTimeslot.setText(callUserTimeslot);
                                    Picasso.with(getApplicationContext())
                                            .load(callUserImage)
                                            .into(binding.callUserImage);
                                    binding.noCalls.setVisibility(View.GONE);
                                    binding.callingCard.setVisibility(View.VISIBLE);
                                }
                                if (mainService != null){
                                    setToken();
                                }
                                addUpcomingCallsToRecyclerView(response);
                                addPreviousCallsToRecyclerView(response);
                                myProgressDialog.dismissDialog();
                            }else{
                               Utilities.makeToast("Sorry you are not found in database!!",getApplicationContext());
                                myProgressDialog.dismissDialog();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                myProgressDialog.dismissDialog();
                Utilities.makeToast("Network is slow",getApplicationContext());
                getUpcomingAndPreviousCalls();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }
    /***
     * IT fetches and add previous call
     */
    public void addPreviousCallsToRecyclerView(JSONObject response) {
        ArrayList<PreviousCalls> previousCallsArrayList = new ArrayList<>();
        try {
            JSONArray jsonArray = response.getJSONArray("previousCalls");

            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject previousCall = jsonArray.getJSONObject(i);


                PreviousCalls previousCalls = new PreviousCalls();
                previousCalls.setUserProfilePic(previousCall.getString("userProfilePic"));
                previousCalls.setUserName(previousCall.getString("userName"));
                previousCalls.setCallDate(previousCall.getString("date"));
                previousCalls.setCallDuration(previousCall.getString("duration"));
                previousCalls.setCallTime(previousCall.getString("timeSlot"));
                previousCalls.setService(previousCall.getString("service"));
                previousCalls.setCallId(previousCall.getString("uid"));

                previousCallsArrayList.add(previousCalls);

            }
            if (previousCallsArrayList.isEmpty()) {
                binding.noPreviousCalls.setVisibility(View.VISIBLE);
            }
            final PreviousCallsAdapter previousCallsAdapter = new PreviousCallsAdapter(getApplication(),previousCallsArrayList);
            binding.previousCalls.setLayoutManager(new GridLayoutManager(getApplicationContext(),1));
            binding.previousCalls.setAdapter(previousCallsAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /***
     * IT fetches and add upcoming call
     */
    public void addUpcomingCallsToRecyclerView(JSONObject response) {
        ArrayList<UpcommingCalls> upcommingCallsArrayList = new ArrayList<>();
        try {
            JSONArray jsonArray = response.getJSONArray("upcomingCalls");
            for (int i = 0; i < jsonArray.length(); i++){

                JSONObject upcomingCall = jsonArray.getJSONObject(i);

                UpcommingCalls upcommingCalls = new UpcommingCalls();

                upcommingCalls.setUserProfilePic(upcomingCall.getString("userProfilePic"));
                upcommingCalls.setUserName(upcomingCall.getString("userName"));
                upcommingCalls.setCallDate(upcomingCall.getString("date"));
                upcommingCalls.setCallDuration(upcomingCall.getString("duration"));
                upcommingCalls.setCallTime(upcomingCall.getString("timeSlot"));
                upcommingCalls.setService(upcomingCall.getString("service"));

                upcommingCallsArrayList.add(upcommingCalls);

            }
            if (upcommingCallsArrayList.isEmpty()) {
               binding.noUpcomingCalls.setVisibility(View.VISIBLE);
            }

            final UpcomingCallsAdapter UpcomingCallsadapter = new UpcomingCallsAdapter(getApplication(),upcommingCallsArrayList);
            binding.upcomingCalls.setLayoutManager(new GridLayoutManager(getApplicationContext(),1));
            binding.upcomingCalls.setAdapter(UpcomingCallsadapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        if (mainService != null){
        DocumentReference documentReference = database.collection(mainService).document(mAuth.getUid());
        HashMap<String , Object> updates = new HashMap<>();
        updates.put("FCMToken", FieldValue.delete());
        documentReference.update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                mAuth.signOut();
                Intent intent = new Intent(getApplicationContext(), ChooseAuthentication.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Utilities.makeToast("Sorry failed to logout! Try Again",getApplicationContext());
            }
        });

        }
    }



    private void setToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if(task.isSuccessful() && task.getResult() != null){
                sendFCMTokenTODatabase(task.getResult().getToken());
            }
        });
    }
    private void sendFCMTokenTODatabase(String token){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(mainService).document(FirebaseAuth.getInstance().getUid());
        documentReference.update("FCMToken",token)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Utilities.makeToast("Token Not Successfully",getApplicationContext());
            }
        });
    }


    private void openAudioMeeting(String type) {
        if (callUserToken == null && callUserToken.trim().isEmpty()) {
            Utilities.makeToast(callUserName + " is not available for meeting", getApplicationContext());
        } else {
            Intent intent = new Intent(this, OutgoingCallInvitation.class);
            intent.putExtra("MeetingType", type);
            intent.putExtra("ExpertName", callExpertName);
            intent.putExtra("UserName", callUserName);
            intent.putExtra("Timeslot", callUserTimeslot);
            intent.putExtra("ExpertToken", callUserToken);
            intent.putExtra("ExpertImage",callExpertImage );
            intent.putExtra("UserImage", callUserImage);
            startActivity(intent);
        }
    }

}