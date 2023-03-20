package com.ksacp2022t3.aladarbi;

import static android.widget.Toast.*;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.SphericalUtil;
import com.ksacp2022t3.aladarbi.models.Reservation;
import com.ksacp2022t3.aladarbi.models.Review;
import com.ksacp2022t3.aladarbi.models.Ride;
import com.ksacp2022t3.aladarbi.models.Trip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReservationDetailsActivity extends AppCompatActivity {

    TextView txt_car_model,txt_car_number,txt_car_max_passengers,
            txt_trip_type,txt_trip_date,txt_trip_distance,
             txt_reserved_seats,
            txt_free_seats,txt_price,txt_status,txt_ride_date;
    EditText txt_comment;
    LinearLayoutCompat layout_rate;
    RatingBar rating;
    AppCompatButton btn_view_on_map,btn_send_reservation,btn_cancel_reservation,btn_pay,btn_send_review;
    ImageView btn_back;
    String trip_id;

    Date ride_date;

    Trip trip;
    Ride ride;
    String ride_id;


    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_details);
        txt_car_model = findViewById(R.id.txt_car_model);
        txt_car_number = findViewById(R.id.txt_car_number);
        txt_car_max_passengers = findViewById(R.id.txt_car_max_passengers);
        txt_trip_type = findViewById(R.id.txt_trip_type);
        txt_trip_date = findViewById(R.id.txt_trip_date);
        btn_cancel_reservation = findViewById(R.id.btn_cancel_reservation);
        txt_trip_distance = findViewById(R.id.txt_trip_distance);
        txt_reserved_seats = findViewById(R.id.txt_reserved_seats);
        txt_free_seats = findViewById(R.id.txt_free_seats);
        txt_price = findViewById(R.id.txt_price);
        txt_status = findViewById(R.id.txt_status);
        btn_view_on_map = findViewById(R.id.btn_view_on_map);
        btn_send_reservation = findViewById(R.id.btn_send_reservation);
        txt_ride_date = findViewById(R.id.txt_ride_date);
        btn_back = findViewById(R.id.btn_back);
        btn_pay = findViewById(R.id.btn_pay);
        btn_send_review = findViewById(R.id.btn_send_review);
        txt_comment = findViewById(R.id.txt_comment);
        layout_rate = findViewById(R.id.layout_rate);
        rating = findViewById(R.id.rating);









        trip_id=getIntent().getStringExtra("trip_id");

          ride_date= (Date) getIntent().getSerializableExtra("ride_date");
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        txt_ride_date.setText(simpleDateFormat.format(ride_date));
        ride_id=trip_id+"_"+simpleDateFormat.format(ride_date);



        firestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();


        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("تحميل المعلومات");
        progressDialog.setMessage("الرجاء الانتظار");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);


        load_trip();



        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_cancel_reservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel_reservation();
            }
        });


        btn_send_reservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                progressDialog.setMessage("إرسال حجز");
                if(ride==null)
                {

                    ride=new Ride();
                    ride.setTrip_id(trip.getId());
                    ride.setDriver_id(trip.getDriver_id());
                    ride.setFree_seats(trip.getMax_passengers_count());
                    ride.setReserved_seats(0);
                    ride.setRide_date(ride_date);
                    ride.setPrice(trip.getTotal_price());
                    ride.setStatus("متاح");
                    ride.setDriver_name(trip.getDriver_name());

                    ride.setId(ride_id);
                    List<String> clients=new ArrayList<>();
                    ride.setClients(clients);

                    firestore.collection("rides")
                            .document(ride_id).set(ride)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    send_reservation();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    makeText(ReservationDetailsActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();

                                }
                            });

                }
                else
                    send_reservation();
            }
        });


        btn_send_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String comment=txt_comment.getText().toString();

                if(comment.isEmpty())
                {
                    txt_comment.setError("لا يمكن ارسال حقل فارغ");
                    return;
                }

                Review review=new Review();
                SharedPreferences sharedPreferences=ReservationDetailsActivity.this.getSharedPreferences("aladarbi",MODE_PRIVATE);
                String user_name=sharedPreferences.getString("user_name","");
                review.setName(user_name);
                review.setComment(comment);
                review.setRate(rating.getRating());

                progressDialog.setTitle("ارسال التقييم");
                progressDialog.show();
                firestore.collection("rides")
                        .document(ride_id)
                        .collection("reviews")
                        .add(review)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                progressDialog.dismiss();
                                makeText(ReservationDetailsActivity.this,"تم إرسال التقييم بنجاح" , LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                makeText(ReservationDetailsActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                            }
                        });
            }
        });








    }

    void load_trip(){
        progressDialog.setTitle("تحميل المعلومات");
        progressDialog.show();
        firestore.collection("trips")
                .document(trip_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        trip=documentSnapshot.toObject(Trip.class);
                        txt_car_model.setText(trip.getCar_model());
                        txt_car_number.setText(trip.getCar_number());
                        txt_car_max_passengers.setText(trip.getMax_passengers_count()+" مقاعد");
                        txt_trip_type.setText(trip.getTrip_type());
                        if(trip.getTrip_date()==null)
                        {
                            txt_trip_date.setText("يوميا");
                        }
                        else{
                            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
                            txt_trip_date.setText(simpleDateFormat.format(trip.getTrip_date()));
                        }
                        txt_trip_distance.setText(distance_label(trip.getTotal_distance()));

                        LatLng t_start=new LatLng(trip.getStart_point().getLatitude(),
                                trip.getStart_point().getLongitude());
 
                        LatLng t_end=new LatLng(trip.getEnd_point().getLatitude(),
                                trip.getEnd_point().getLongitude());

                      
                        btn_view_on_map.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent intent = new Intent(ReservationDetailsActivity.this,ViewTripPathActivity. class);
                                Bundle points=new Bundle();
                                points.putParcelable("t_start",t_start);
                                points.putParcelable("t_end",t_end);
                                intent.putExtra("points",points);
                                startActivity(intent);

                            }
                        });


                        firestore.collection("rides")
                                .document(ride_id)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        progressDialog.dismiss();
                                        if(documentSnapshot.exists())
                                        {
                                            ride=documentSnapshot.toObject(Ride.class);
                                            if(ride.getFree_seats()==0) {
                                                btn_send_reservation.setVisibility(View.GONE);
                                            }

                                            txt_status.setText(ride.getStatus());
                                            txt_reserved_seats.setText(ride.getReserved_seats()+" ");
                                            txt_free_seats.setText(ride.getFree_seats()+" ");
                                            txt_price.setText(ride.getPrice()+" ر.س");



                                            firestore.collection("rides")
                                                    .document(ride.getId())
                                                    .collection("reservations")
                                                    .document(firebaseAuth.getUid())
                                                    .get()
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                                                            if(documentSnapshot.exists())
                                                            {
                                                                btn_send_reservation.setVisibility(View.GONE);
                                                                if(ride.getStatus().equals("منتهية")) {
                                                                    layout_rate.setVisibility(View.VISIBLE);
                                                                    btn_cancel_reservation.setVisibility(View.GONE);
                                                                    btn_pay.setVisibility(View.GONE);

                                                                }
                                                                else {
                                                                    Reservation reservation = documentSnapshot.toObject(Reservation.class);
                                                                    if (reservation.isPaid()) {
                                                                        txt_status.setText("مدفوع");
                                                                        txt_status.setTextColor(Color.RED);
                                                                        btn_cancel_reservation.setVisibility(View.GONE);
                                                                        btn_pay.setVisibility(View.GONE);
                                                                    } else {
                                                                        btn_cancel_reservation.setVisibility(View.VISIBLE);
                                                                        btn_pay.setVisibility(View.VISIBLE);
                                                                        btn_pay.setOnClickListener(new View.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(View view) {
                                                                                Intent intent = new Intent(ReservationDetailsActivity.this,PayReservationActivity. class);
                                                                                intent.putExtra("ride_id",ride_id);
                                                                                intent.putExtra("reservation_id",reservation.getId());
                                                                                startActivity(intent);

                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            }
                                                            else{
                                                                if(ride.getStatus().equals("منتهية"))
                                                                {
                                                                    btn_send_reservation.setVisibility(View.GONE);
                                                                }
                                                                else {
                                                                    btn_send_reservation.setVisibility(View.VISIBLE);
                                                                }
                                                                btn_cancel_reservation.setVisibility(View.GONE);
                                                                btn_pay.setVisibility(View.GONE);
                                                            }
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            progressDialog.dismiss();
                                                            makeText(ReservationDetailsActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                                                            finish();

                                                        }
                                                    });

                                        }
                                        else
                                        {
                                            txt_status.setText("متاح");
                                            txt_reserved_seats.setText("0");
                                            txt_free_seats.setText(trip.getMax_passengers_count()+" ");
                                            txt_price.setText(trip.getTotal_price()+" ر.س");
                                            btn_send_reservation.setVisibility(View.VISIBLE);
                                            btn_cancel_reservation.setVisibility(View.GONE);
                                            btn_pay.setVisibility(View.GONE);

                                        }


                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        makeText(ReservationDetailsActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                                        finish();
                                    }
                                });





                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        makeText(ReservationDetailsActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                        finish();
                    }
                });
    }

    void  cancel_reservation(){
        progressDialog.show();
        progressDialog.setMessage("الغاء الحجز");
        firestore.collection("rides")
                .document(ride.getId())
                .collection("reservations")
                .document(firebaseAuth.getUid()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        ride.setReserved_seats(ride.getReserved_seats()-1);
                        ride.setFree_seats(ride.getFree_seats()+1);
                        ride.setPrice(ride.getReserved_seats()==0?trip.getTotal_price():trip.getTotal_price()/ride.getReserved_seats());
                        if(ride.getReserved_seats()==0) {
                            firestore.collection("rides")
                                    .document(ride.getId())
                                    .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                            makeText(ReservationDetailsActivity.this, "تم إلغاء الحجز بنجاح", LENGTH_LONG).show();
                                            load_trip();
                                        }
                                    });
                        }
                        else {
                            firestore.collection("rides")
                                    .document(ride.getId())
                                    .update(
                                            "free_seats", ride.getFree_seats(),
                                            "reserved_seats", ride.getReserved_seats(),
                                            "price", ride.getPrice(),
                                            "clients", FieldValue.arrayRemove(firebaseAuth.getUid())

                                    )
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                            makeText(ReservationDetailsActivity.this, "تم إلغاء الحجز بنجاح", LENGTH_LONG).show();
                                            load_trip();
                                        }
                                    });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        makeText(ReservationDetailsActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();

                    }
                });
    }

    void send_reservation(){
        Reservation reservation=new Reservation();
        reservation.setReservation_date(ride_date);
        reservation.setTrip_id(trip_id);
        reservation.setDriver_id(trip.getDriver_id());
        reservation.setClient_id(firebaseAuth.getUid());
        SharedPreferences sharedPreferences=ReservationDetailsActivity.this.getSharedPreferences("aladarbi",MODE_PRIVATE);
        String user_name=sharedPreferences.getString("user_name","");
        reservation.setClient_name(user_name);
        reservation.setId(firebaseAuth.getUid());

        firestore.collection("rides")
                .document(ride.getId())
                .collection("reservations")
                .document(firebaseAuth.getUid()).set(reservation)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        ride.setReserved_seats(ride.getReserved_seats()+1);
                        ride.setFree_seats(ride.getFree_seats()-1);
                        ride.setPrice(ride.getReserved_seats()==0?trip.getTotal_price():trip.getTotal_price()/ride.getReserved_seats());


                        firestore.collection("rides")
                                .document(ride.getId())
                                .update(
                                        "free_seats",ride.getFree_seats(),
                                        "reserved_seats",ride.getReserved_seats(),
                                        "price",ride.getPrice(),
                                        "clients",FieldValue.arrayUnion(firebaseAuth.getUid())

                                )
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        load_trip();

                                        makeText(ReservationDetailsActivity.this,"تم إضافة الحجز بنجاح" , LENGTH_LONG).show();
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        makeText(ReservationDetailsActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();

                    }
                });
    }




    private String distance_label(double distance) {
        if (distance < 1000)
            return distance + " متر";
        else
            return (num_round(distance / 1000) + " كم");
    }



    private double num_round(double num) {
        return Math.round(num * 100.0) / 100.0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        load_trip();
    }
}