package com.ksacp2022t3.aladarbi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ksacp2022t3.aladarbi.adapters.PassengersListAdapter;
import com.ksacp2022t3.aladarbi.adapters.ReviewsListAdapter;
import com.ksacp2022t3.aladarbi.models.Reservation;
import com.ksacp2022t3.aladarbi.models.Review;
import com.ksacp2022t3.aladarbi.models.Ride;

import java.text.SimpleDateFormat;
import java.util.List;

public class RideDetailsActivity extends AppCompatActivity {

    TextView txt_ride_date,txt_status,txt_reserved_seats,
            txt_free_seats,txt_price;
    AppCompatButton btn_set_done;
    RecyclerView recycler_passengers,recycler_reviews;
    ImageView btn_back;
    LinearLayoutCompat layout_reviews;

    FirebaseFirestore firestore;
    ProgressDialog progressDialog;
    String ride_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_details);
        txt_ride_date = findViewById(R.id.txt_ride_date);
        txt_status = findViewById(R.id.txt_status);
        txt_reserved_seats = findViewById(R.id.txt_reserved_seats);
        txt_free_seats = findViewById(R.id.txt_free_seats);
        txt_price = findViewById(R.id.txt_price);
        btn_set_done = findViewById(R.id.btn_set_done);
        recycler_passengers = findViewById(R.id.recycler_passengers);
        recycler_reviews = findViewById(R.id.recycler_reviews);
        btn_back = findViewById(R.id.btn_back);
        layout_reviews = findViewById(R.id.layout_reviews);


          ride_id=getIntent().getStringExtra("ride_id");

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

        load_ride_info();


        btn_set_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                progressDialog.setTitle("إنهاء الرحلة");
                firestore.collection("rides")
                        .document(ride_id)
                        .update("status","منتهية")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                load_ride_info();
                            }
                        });


            }
        });




    }

    private void load_ride_info() {
        progressDialog.show();
        progressDialog.setTitle("تحميل");
        firestore.collection("rides")
                .document(ride_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Ride ride=documentSnapshot.toObject(Ride.class);
                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
                        txt_ride_date.setText(simpleDateFormat.format(ride.getRide_date()));

                        txt_status.setText(ride.getStatus());
                        txt_reserved_seats.setText(String.valueOf(ride.getReserved_seats()));
                        txt_free_seats.setText(String.valueOf(ride.getFree_seats()));
                        txt_price.setText(ride.getPrice()+" ر.س");

                        firestore.collection("rides")
                                .document(ride_id)
                                .collection("reservations")
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                        List<Reservation> reservations=queryDocumentSnapshots.toObjects(Reservation.class);
                                        PassengersListAdapter adapter=new PassengersListAdapter(reservations,RideDetailsActivity.this);
                                        recycler_passengers.setAdapter(adapter);

                                        if(ride.getStatus().equals("منتهية"))
                                        {
                                            layout_reviews.setVisibility(View.VISIBLE);
                                            btn_set_done.setVisibility(View.GONE);
                                            firestore.collection("rides")
                                                    .document(ride_id)
                                                    .collection("reviews")
                                                    .orderBy("created_at", Query.Direction.DESCENDING)
                                                    .get()
                                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                            progressDialog.dismiss();
                                                            List<Review> reviewList=queryDocumentSnapshots.toObjects(Review.class);
                                                            ReviewsListAdapter reviews_adapter=new ReviewsListAdapter(reviewList,RideDetailsActivity.this);
                                                            recycler_reviews.setAdapter(reviews_adapter);

                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(RideDetailsActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                                                            finish();
                                                        }
                                                    });

                                        }
                                        else
                                            progressDialog.dismiss();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(RideDetailsActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RideDetailsActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }
}