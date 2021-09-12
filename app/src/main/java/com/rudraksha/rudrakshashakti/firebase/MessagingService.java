package com.rudraksha.rudrakshashakti.firebase;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rudraksha.rudrakshashakti.Utilities.Constants;
import com.rudraksha.rudrakshashakti.VideoCall.IncomingCallInvitation;

import org.jetbrains.annotations.NotNull;

public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull @NotNull String token) {
        super.onNewToken(token);
        Log.d("FCM", "onNewToken: "+token);
    }

    @Override
    public void onMessageReceived(@NonNull @NotNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String type = remoteMessage.getData().get(Constants.REMOTE_MSG_TYPE);
        if(type != null){
            if(type.equals(Constants.REMOTE_MSG_INVITATION)){
                Intent intent = new Intent(getApplicationContext(), IncomingCallInvitation.class);
                intent.putExtra(
                        Constants.REMOTE_MSG_MEETING_TYPE,
                        remoteMessage.getData().get(Constants.REMOTE_MSG_MEETING_TYPE)
                );
                intent.putExtra(
                        Constants.NAME,
                        remoteMessage.getData().get(Constants.NAME)
                );
                intent.putExtra(
                        Constants.TIME_SLOT,
                        remoteMessage.getData().get(Constants.TIME_SLOT)
                );
                intent.putExtra(
                        Constants.IMAGE,
                        remoteMessage.getData().get(Constants.IMAGE)
                );
                intent.putExtra(
                        Constants.REMOTE_MSG_INVITERS_TOKEN,
                        remoteMessage.getData().get(Constants.REMOTE_MSG_INVITERS_TOKEN)
                );
                intent.putExtra(
                        Constants.REMOTE_MSG_INVITERS_TOKEN,
                        remoteMessage.getData().get(Constants.REMOTE_MSG_INVITERS_TOKEN)
                );
                intent.putExtra(
                        Constants.REMOTE_MSG_MEETING_ROOM,
                        remoteMessage.getData().get(Constants.REMOTE_MSG_MEETING_ROOM)
                );
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }else if(type.equals(Constants.REMOTE_MSG_INVITATION_RESPONSE)){
                Intent intent = new Intent(Constants.REMOTE_MSG_INVITATION_RESPONSE);
                intent.putExtra(
                        Constants.REMOTE_MSG_INVITATION_RESPONSE,
                        remoteMessage.getData().get(Constants.REMOTE_MSG_INVITATION_RESPONSE)
                );
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }
        }
    }
}
