package com.ksacp2022t3.aladarbi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ksacp2022t3.aladarbi.adapters.MyTripsListAdapter;
import com.ksacp2022t3.aladarbi.models.Trip;

import java.util.ArrayList;
import java.util.List;

public class MyTripsActivity extends AppCompatActivity {

    FloatingActionButton btn_add_trip;
    RecyclerView recycler_trips;
    ImageView btn_back;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trips);
        btn_add_trip = findViewById(R.id.btn_add_trip);
        btn_back = findViewById(R.id.btn_back);
        recycler_trips = findViewById(R.id.recycler_trips);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        firebaseAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();

        progressDialog=new ProgressDialog(this);
                progressDialog.setTitle("يتم تحميل الرحلات");
                progressDialog.setMessage("برجى الانتظار");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);






         load_my_trips();




        btn_add_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyTripsActivity.this,AddTripActivity. class);
                startActivity(intent);
            }
        });

    }

    private void load_my_trips() {
        progressDialog.show();
        String uid= firebaseAuth.getUid();
        firestore.collection("trips")
                .whereEqualTo("driver_id",uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        progressDialog.dismiss();
                        List<Trip> tripList=new ArrayList<>();
                        for(DocumentSnapshot doc:queryDocumentSnapshots.getDocuments())
                        {
                            Trip trip=doc.toObject(Trip.class);
                            tripList.add(trip);
                        }

                        MyTripsListAdapter adapter=new MyTripsListAdapter(tripList,MyTripsActivity.this);
                        recycler_trips.setAdapter(adapter);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(MyTripsActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();

                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        load_my_trips();
    }
}