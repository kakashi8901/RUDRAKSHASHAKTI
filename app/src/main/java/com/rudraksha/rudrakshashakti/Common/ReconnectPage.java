package com.rudraksha.rudrakshashakti.Common;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.rudraksha.rudrakshashakti.Utilities.InternetConnection;
import com.rudraksha.rudrakshashakti.Utilities.MyProgressDialog;
import com.rudraksha.rudrakshashakti.Utilities.Utilities;
import com.rudraksha.rudrakshashakti.databinding.ActivityReconnectPageBinding;

public class ReconnectPage extends AppCompatActivity implements View.OnClickListener {

    private MyProgressDialog myProgressDialog;
    //    ProgressDialog progressDialog = new ProgressDialog();
    private ActivityReconnectPageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_reconnect_page);

        binding = ActivityReconnectPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        myProgressDialog = new MyProgressDialog();

        binding.reconnectBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if (view == binding.reconnectBtn) {
            myProgressDialog.showDialog(ReconnectPage.this);
            if (InternetConnection.checkConnection(this)) {
                myProgressDialog.dismissDialog();
                finish();
            } else {
                Utilities.makeToast("Not connected to the internet", this);
                myProgressDialog.dismissDialog();
            }

        }
    }
}