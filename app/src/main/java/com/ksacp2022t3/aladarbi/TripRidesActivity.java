package com.ksacp2022t3.aladarbi;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ksacp2022t3.aladarbi.adapters.RidesListAdapter;
import com.ksacp2022t3.aladarbi.models.Ride;

import java.util.List;

public class TripRidesActivity extends AppCompatActivity {

    ImageView btn_back;
    RecyclerView recycler_rides;

    ProgressDialog progressDialog;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_rides);
        btn_back = findViewById(R.id.btn_back);
        recycler_rides = findViewById(R.id.recycler_rides);


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        firestore=FirebaseFirestore.getInstance();

        progressDialog=new ProgressDialog(this);
                progressDialog.setTitle("تحميل");
                progressDialog.setMessage("الرجاء الانتظار");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);

        String trip_id=getIntent().getStringExtra("trip_id");

        Log.d("tripp",trip_id);

        progressDialog.show();
        firestore.collection("rides")
                .whereEqualTo("trip_id",trip_id)
                .orderBy("ride_date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        progressDialog.dismiss();
                        if(queryDocumentSnapshots.getDocuments().size()==0)
                        {
                            makeText(TripRidesActivity.this,
                                    "لا توجد حجوزات لهذه الرحلة بعد", LENGTH_LONG).show();
                        }
                        else
                        {
                            List<Ride> rideList=queryDocumentSnapshots.toObjects(Ride.class);
                            RidesListAdapter adapter=new RidesListAdapter(rideList,TripRidesActivity.this);
                            recycler_rides.setAdapter(adapter);
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                         makeText(TripRidesActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                         finish();
                    }
                });




    }
}