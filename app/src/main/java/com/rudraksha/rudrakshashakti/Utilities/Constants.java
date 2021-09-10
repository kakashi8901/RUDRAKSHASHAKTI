package com.rudraksha.rudrakshashakti.Utilities;

import java.util.HashMap;

public class Constants {

    public static final String NAME = "Name";
    public static final String TIME_SLOT = "Time_Slot";
    public static final String IMAGE= "Image";

    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";

    public static final String REMOTE_MSG_TYPE = "type";
    public static final String REMOTE_MSG_INVITATION = "invitation";
    public static final String REMOTE_MSG_MEETING_TYPE= "meetingType";
    public static final String REMOTE_MSG_INVITERS_TOKEN = "invitersToken";
    public static final String REMOTE_MSG_DATA= "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS= "registration_ids";

    public static final String REMOTE_MSG_INVITATION_RESPONSE="invitationResponse";

    public static final String REMOTE_MSG_INVITATION_ACCEPTED="accepted";
    public static final String REMOTE_MSG_INVITATION_REJECTED="rejected";

    public static final String REMOTE_MSG_INVITATION_CANCELLED="cancelled";

    public static final String REMOTE_MSG_MEETING_ROOM = "meetingRoom";
    public static HashMap<String, String> getRemoteMsgHeaders(){
        HashMap<String, String> headers = new HashMap<>();
        headers.put(
                Constants.REMOTE_MSG_AUTHORIZATION,
                "key=AAAAlrlDaqU:APA91bEFOWQ2DQo7NIKwmlxBIX9UypXFIGtCZjDeHjyoWK6fFJMcNgaX19z-kfGbJTDY3_NWz6_6B_3YN2MGpzyU5qaueXRSAPsAOg7Ck1WpqP1W8YNGcVazCch0ZmvLxHbax1cv1thh"
        );
        headers.put(Constants.REMOTE_MSG_CONTENT_TYPE,"application/json");
        return headers;
    }
}
