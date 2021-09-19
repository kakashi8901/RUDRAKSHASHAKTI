package com.rudraksha.rudrakshashakti.VideoCall;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.rudraksha.rudrakshashakti.Utilities.Constants;
import com.rudraksha.rudrakshashakti.Utilities.Utilities;
import com.rudraksha.rudrakshashakti.databinding.ActivityOutgoingCallInvitationBinding;
import com.rudraksha.rudrakshashakti.network.ApiClient;
import com.rudraksha.rudrakshashakti.network.ApiService;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutgoingCallInvitation extends AppCompatActivity implements View.OnClickListener {
    ActivityOutgoingCallInvitationBinding binding;
    private String invitersToken = null;
    String name,timeSlot,expertToken,expertImage,userImage,userName;
    String meetingType =null;
    String meetingRoom = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);binding = ActivityOutgoingCallInvitationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        //Setting content view to Root view
        setContentView(view);
        setListner();
        Intent intent = getIntent();
        meetingType = intent.getExtras().getString("MeetingType");
        name = intent.getExtras().getString("ExpertName");
        timeSlot = intent.getExtras().getString("Timeslot");
        expertToken = intent.getExtras().getString("ExpertToken");
        expertImage = intent.getExtras().getString("ExpertImage");
        userImage = intent.getExtras().getString("UserImage");
        userName = intent.getExtras().getString("UserName");
        initialize();


    }


    @SuppressLint("SetTextI18n")
    private void initialize() {
        if(meetingType != null){
            if (meetingType.equals("VideoOnly")){
                binding.call.setText("Video Calling...");
            }else if (meetingType.equals("AudioOnly")){
                binding.call.setText("Audio Calling...");
            }
        }
        binding.expertName.setText(userName);
        Picasso.with(getApplicationContext())
                .load(userImage)
                .into(binding.expertImage);
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<InstanceIdResult> task) {
                invitersToken = task.getResult().getToken();
                if(meetingType != null && name != null && timeSlot != null && expertToken != null){
                    initiateMeeting(meetingType,expertToken);
                }
            }
        });
    }

    private void initiateMeeting(String meetingType, String receiverToken){
        try {
            JSONArray tokens = new JSONArray();
            tokens.put(receiverToken);

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION);
            data.put(Constants.REMOTE_MSG_MEETING_TYPE, meetingType);
            data.put(Constants.NAME, name);
            data.put(Constants.TIME_SLOT,timeSlot);
            data.put(Constants.IMAGE,expertImage);
            data.put(Constants.REMOTE_MSG_INVITERS_TOKEN,invitersToken);

            meetingRoom = UUID.randomUUID().toString().substring(0,8);
            data.put(Constants.REMOTE_MSG_MEETING_ROOM,meetingRoom);
            body.put(Constants.REMOTE_MSG_DATA,data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);

            sendRemoteMessage(body.toString(), Constants.REMOTE_MSG_INVITATION);
        }catch (Exception exception){
            Utilities.makeToast(exception.getMessage(),getApplicationContext());
        }
    }


    private void sendRemoteMessage(String remoteMessageBody,String type){
        ApiClient.getClient().create(ApiService.class).sendRemoteMessage(
                Constants.getRemoteMsgHeaders(), remoteMessageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                  if (response.isSuccessful()){
                        if(type.equals(Constants.REMOTE_MSG_INVITATION)){
                            Utilities.makeToast("Started Calling",getApplicationContext());
                        }else if (type.equals(Constants.REMOTE_MSG_INVITATION_RESPONSE)){
                            Utilities.makeToast("Cant Call", getApplicationContext());
                            finish();
                        }
                  }else{
                      Utilities.makeToast(response.message(),getApplicationContext());
                      finish();
                  }
            }

            @Override
            public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                Utilities.makeToast(t.getMessage(),getApplicationContext());
                finish();
            }
        });
    }

    private void cancelInvitation(String receiverToken ){
        try {
            JSONArray tokens = new JSONArray();
            tokens.put(receiverToken);

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constants.REMOTE_MSG_TYPE,Constants.REMOTE_MSG_INVITATION_RESPONSE);
            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE,Constants.REMOTE_MSG_INVITATION_CANCELLED);

            body.put(Constants.REMOTE_MSG_DATA,data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS,tokens);

            sendRemoteMessage(body.toString(),Constants.REMOTE_MSG_INVITATION_RESPONSE);
        }catch (Exception exception){
            Utilities.makeToast(exception.getMessage(), getApplicationContext());
            finish();
        }
    }

    private final BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
             String type = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE);
             if (type != null){
                 if(type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)){
                     try {
                         URL serverURL = new URL("https://meet.jit.si");
                         JitsiMeetConferenceOptions.Builder builder= new JitsiMeetConferenceOptions.Builder();
                         builder.setServerURL(serverURL);
                         builder.setWelcomePageEnabled(false);
                         builder.setRoom(meetingRoom);
                         if (meetingType.equals("AudioOnly")){
                             builder.setVideoMuted(true);
                         }
                         JitsiMeetActivity.launch(OutgoingCallInvitation.this, builder.build());
                         finish();
                     }catch (Exception exception){
                         Utilities.makeToast(exception.getMessage(), getApplicationContext());
                         finish();
                     }
                 }else if(type.equals(Constants.REMOTE_MSG_INVITATION_REJECTED)){
                     Utilities.makeToast("Call Rejected", getApplicationContext());
                     finish();
                 }
             }
        }
    };

    private void setListner() {
        binding.endCall.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
       if( view == binding.endCall){
           if(meetingType != null && name != null && timeSlot != null && expertToken != null){
               cancelInvitation(expertToken);
           }
       }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                invitationResponseReceiver,
                new IntentFilter(Constants.REMOTE_MSG_INVITATION_RESPONSE)
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(
                invitationResponseReceiver
        );
    }
}