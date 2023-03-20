package com.ksacp2022t3.aladarbi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ksacp2022t3.aladarbi.adapters.ReservationsListAdapter;
import com.ksacp2022t3.aladarbi.models.Ride;

import java.util.List;

public class MyReservationsActivity extends AppCompatActivity {
    ImageView btn_back;
    RecyclerView recycler_reservations;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reservations);
        btn_back = findViewById(R.id.btn_back);
        recycler_reservations = findViewById(R.id.recycler_reservations);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        firestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
                progressDialog.setTitle("تحميل");
                progressDialog.setMessage("الرجاء الانتظار");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);

        progressDialog.show();

        firestore.collection("rides")
                .whereArrayContains("clients",firebaseAuth.getUid())
                .orderBy("ride_date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        progressDialog.dismiss();
                        List<Ride> rideList=queryDocumentSnapshots.toObjects(Ride.class);
                        ReservationsListAdapter adapter=new ReservationsListAdapter(rideList,MyReservationsActivity.this);
                        recycler_reservations.setAdapter(adapter);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                         Toast.makeText(MyReservationsActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                         finish();

                    }
                });



    }
}