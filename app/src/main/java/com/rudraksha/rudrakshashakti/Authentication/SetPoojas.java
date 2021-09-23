package com.rudraksha.rudrakshashakti.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.rudraksha.rudrakshashakti.Adapters.PoojaListAdapter;
import com.rudraksha.rudrakshashakti.Adapters.poojaListner;
import com.rudraksha.rudrakshashakti.Pojo.AddPoojas;
import com.rudraksha.rudrakshashakti.Pojo.PoojaList;
import com.rudraksha.rudrakshashakti.Utilities.MyProgressDialog;
import com.rudraksha.rudrakshashakti.Utilities.Utilities;
import com.rudraksha.rudrakshashakti.databinding.ActivitySetPoojasBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetPoojas extends AppCompatActivity implements poojaListner,View.OnClickListener{

    ActivitySetPoojasBinding binding;
    FirebaseFirestore database;
    private MyProgressDialog myProgressDialog;
    List<String> poojaSelected = new ArrayList<>();
    String expertMainService="",uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();
        //Incorporating View Binding
        binding= ActivitySetPoojasBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        //Setting content view to Root view
        setContentView(view);
        setListner();
        getDataFromFirestore();
    }

    private void setListner() {
        binding.backBtn.setOnClickListener(this);
        binding.nextBtn.setOnClickListener(this);
    }

    private void getDataFromFirestore() {
        myProgressDialog = new MyProgressDialog();
        myProgressDialog.showDialog(this);
        final ArrayList<PoojaList> poojaLists = new ArrayList<>();
        final PoojaListAdapter poojaListAdapter = new PoojaListAdapter(getApplicationContext(),poojaLists,this);
        database =FirebaseFirestore.getInstance();
        List<String> services = new ArrayList<>();
        services.add("Astrology");
        services.add("Numerology");
        services.add("Vastu Shastra");
        services.add("Lal Kitab");
        services.add("Tarot Card");
        for (int i =0;i<services.size();i++){
            int finalI = i;
            database.collection(services.get(i)).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots){
                        if (snapshot.getId().equals(uid)){
                            expertMainService = services.get(finalI);
                            Utilities.makeToast(expertMainService,getApplicationContext());
                        }
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Utilities.makeToast(e.getMessage(),getApplicationContext());
                    myProgressDialog.dismissDialog();
                }
            });
        }
        database.collection("Poojas")
                .orderBy("id", Query.Direction.ASCENDING)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                poojaLists.clear();
                for (DocumentSnapshot snapshot : queryDocumentSnapshots){
                    PoojaList poojaList = new PoojaList();
                    poojaList.setPoojaId(snapshot.getString("uid"));
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

    @Override
    public void onPoojaItemChange(ArrayList<String> poojaList) {
        poojaSelected=poojaList;
    }

    @Override
    public void onClick(View view) {
        if (view == binding.backBtn){
            finish();
        }else if(view == binding.nextBtn){
            setInFirestore();
        }
    }

    private void setInFirestore() {
        myProgressDialog = new MyProgressDialog();
        myProgressDialog.showDialog(this);
        AddPoojas addPoojas = new AddPoojas();
        addPoojas.setPoojas(poojaSelected);
        database =FirebaseFirestore.getInstance();
        Map<String,Object> poojas = new HashMap<>();
        poojas.put("poojas",poojaSelected);
        database.collection(expertMainService).document(uid).update(poojas).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                for (int i = 0 ; i< poojaSelected.size(); i++) {
                    database.collection("Poojas").document(poojaSelected.get(i)).update("experts",FieldValue.arrayUnion(expertMainService+"$"+uid+"123")).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Utilities.makeToast("Cant add try again !",getApplicationContext());
                            myProgressDialog.dismissDialog();
                        }
                    });
                }
                myProgressDialog.dismissDialog();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Utilities.makeToast("Cant add try again !",getApplicationContext());
                myProgressDialog.dismissDialog();
            }
        });
    }
}