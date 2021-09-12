package com.rudraksha.rudrakshashakti.VideoCall;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.rudraksha.rudrakshashakti.Utilities.Constants;
import com.rudraksha.rudrakshashakti.Utilities.Utilities;
import com.rudraksha.rudrakshashakti.databinding.ActivityIncomingCallInvitationBinding;
import com.rudraksha.rudrakshashakti.network.ApiClient;
import com.rudraksha.rudrakshashakti.network.ApiService;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IncomingCallInvitation extends AppCompatActivity implements View.OnClickListener {

    ActivityIncomingCallInvitationBinding binding;
    String expertImage,name;
    private String meetingType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIncomingCallInvitationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        //Setting content view to Root view
        setContentView(view);
        setListner();
        initialize();
    }

    @SuppressLint("SetTextI18n")
    private void initialize() {
        Intent intent = getIntent();
        name = intent.getExtras().getString(Constants.NAME);
        expertImage = intent.getExtras().getString(Constants.IMAGE);
        meetingType = intent.getExtras().getString(Constants.REMOTE_MSG_MEETING_TYPE);

        if (meetingType != null){
            if (meetingType.equals("AudioOnly")){
                binding.call.setText("Incoming Audio Call From..");
            }else if (meetingType.equals("VideoOnly")){
                binding.call.setText("Incoming Video Call From..");
            }
        }
        binding.expertName.setText(name);
        Picasso.with(getApplicationContext())
                .load(expertImage)
                .into(binding.expertImage);
    }

    private void setListner() {
        binding.acceptCall.setOnClickListener(this);
        binding.declineCall.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if ( view == binding.acceptCall ){
            sendInvitationResponse(
                    Constants.REMOTE_MSG_INVITATION_ACCEPTED,
                    getIntent().getStringExtra(Constants.REMOTE_MSG_INVITERS_TOKEN)
            );
        }else if(view == binding.declineCall){
            sendInvitationResponse(
                    Constants.REMOTE_MSG_INVITATION_REJECTED,
                    getIntent().getStringExtra(Constants.REMOTE_MSG_INVITERS_TOKEN)
            );
        }
    }

    private void sendInvitationResponse(String type, String receiverToken ){
      try {
          JSONArray tokens = new JSONArray();
          tokens.put(receiverToken);

          JSONObject body = new JSONObject();
          JSONObject data = new JSONObject();

          data.put(Constants.REMOTE_MSG_TYPE,Constants.REMOTE_MSG_INVITATION_RESPONSE);
          data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE,type);

          body.put(Constants.REMOTE_MSG_DATA,data);
          body.put(Constants.REMOTE_MSG_REGISTRATION_IDS,tokens);

          sendRemoteMessage(body.toString(),type);
      }catch (Exception exception){
        Utilities.makeToast(exception.getMessage(), getApplicationContext());
        finish();
      }
    }
    private void sendRemoteMessage(String remoteMessageBody,String type){
        ApiClient.getClient().create(ApiService.class).sendRemoteMessage(
                Constants.getRemoteMsgHeaders(), remoteMessageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                if (response.isSuccessful()){
                    if (type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)){
                        try {
                            URL serverURL = new URL("https://meet.jit.si");
                            JitsiMeetConferenceOptions.Builder builder= new JitsiMeetConferenceOptions.Builder();
                            builder.setServerURL(serverURL);
                            builder.setWelcomePageEnabled(false);
                            builder.setRoom(getIntent().getStringExtra(Constants.REMOTE_MSG_MEETING_ROOM));
                            if (meetingType.equals("AudioOnly")){
                                builder.setVideoMuted(true);
                            }
                            JitsiMeetActivity.launch(IncomingCallInvitation.this, builder.build());
                            finish();
                        }catch (Exception exception){
                            Utilities.makeToast(exception.getMessage(), getApplicationContext());
                            finish();
                        }
                    }else{
                        Utilities.makeToast("Call Rejected", getApplicationContext());
                        finish();
                    }
                }else{
                    Utilities.makeToast(response.message(), getApplicationContext());
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



    private final BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE);
            if (type != null){
                if(type.equals(Constants.REMOTE_MSG_INVITATION_CANCELLED)){
                    Utilities.makeToast("Call Cancelled", getApplicationContext());
                    finish();
                }
            }
        }
    };

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