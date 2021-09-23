package com.rudraksha.rudrakshashakti.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.rudraksha.rudrakshashakti.Adapters.PoojaListAdapter;
import com.rudraksha.rudrakshashakti.Pojo.PoojaList;
import com.rudraksha.rudrakshashakti.Utilities.MyProgressDialog;
import com.rudraksha.rudrakshashakti.Utilities.Utilities;
import com.rudraksha.rudrakshashakti.databinding.ActivitySetPoojasBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SetPoojas extends AppCompatActivity implements View.OnClickListener{

    ActivitySetPoojasBinding binding;
    FirebaseFirestore database;
    private MyProgressDialog myProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Incorporating View Binding
        binding= ActivitySetPoojasBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        //Setting content view to Root view
        setContentView(view);
        setListeners();
        getDataFromFirestore();
    }

    private void getDataFromFirestore() {
        myProgressDialog = new MyProgressDialog();
        myProgressDialog.showDialog(this);
        List<String> poojas = new ArrayList<String>();
        final ArrayList<PoojaList> poojaLists = new ArrayList<>();
        final PoojaListAdapter poojaListAdapter = new PoojaListAdapter(getApplicationContext(),poojaLists,poojas);
        database =FirebaseFirestore.getInstance();
        database.collection("Poojas")
                .orderBy("id", Query.Direction.ASCENDING)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                poojaLists.clear();
                PoojaList poojaList = new PoojaList();
                poojaList.setPoojaId("Select All");
                for (DocumentSnapshot snapshot : queryDocumentSnapshots){
                    poojaList.setPoojaId(snapshot.getString("poojaName"));
                    poojaLists.add(poojaList);
                }
                poojaListAdapter.notifyDataSetChanged();
                myProgressDialog.dismissDialog();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Utilities.makeToast(e.getMessage(),getApplicationContext());
                myProgressDialog.dismissDialog();
            }
        });
        binding.poojas.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));
        binding.poojas.setAdapter(poojaListAdapter);
    }

    private void setListeners() {
    }

    @Override
    public void onClick(View view) {

    }
}