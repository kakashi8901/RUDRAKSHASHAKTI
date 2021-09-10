package com.rudraksha.rudrakshashakti.Utilities;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.functions.FirebaseFunctions;
import com.rudraksha.rudrakshashakti.Common.ReconnectPage;

public class Utilities {
    public static FirebaseFunctions mFunctions;
    public static MyProgressDialog progressDialog;
    public static Toast toast;

    public static void makeToast(String s, Context context) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context, s, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void reconnect(Context context) {
//        progressDialog.showDialog(context);
        if (InternetConnection.checkConnection(context)) {
//            progressDialog.dismissDialog();
            Intent intent = new Intent(context, ReconnectPage.class);
            context.startActivity(intent);
        } else {
//            progressDialog.dismissDialog();
        }
    }
}
