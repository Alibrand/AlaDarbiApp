package com.ksacp2022t3.aladarbi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.SphericalUtil;
import com.ksacp2022t3.aladarbi.adapters.SearchTripsListAdapter;
import com.ksacp2022t3.aladarbi.models.Trip;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

public class SearchResultsActivity extends AppCompatActivity {
    ImageView btn_back;
    RecyclerView recycler_trips;


    FirebaseFirestore firestore;
    ProgressDialog progressDialog;
    LatLng start_point,finish_point;
    Date ride_date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        recycler_trips = findViewById(R.id.recycler_trips);
        btn_back = findViewById(R.id.btn_back);



        Bundle points=getIntent().getBundleExtra("points");
        start_point=points.getParcelable("start_point");
        finish_point=points.getParcelable("finish_point");
        ride_date = (Date) getIntent().getSerializableExtra("trip_date");
        firestore=FirebaseFirestore.getInstance();


        progressDialog=new ProgressDialog(this);
                progressDialog.setTitle("البحث عن الرحلات");
                progressDialog.setMessage("الرجاء الانتظار");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        progressDialog.show();
        firestore.collection("trips")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Trip> tripList=queryDocumentSnapshots.toObjects(Trip.class);
                        tripList.sort(new Comparator<Trip>() {
                            @Override
                            public int compare(Trip t1, Trip t2) {
                                LatLng t1_start=new LatLng(t1.getStart_point().getLatitude(),
                                        t1.getStart_point().getLongitude());
                                LatLng t1_end=new LatLng(t1.getEnd_point().getLatitude(),
                                        t1.getEnd_point().getLongitude());

                                LatLng t2_start=new LatLng(t2.getStart_point().getLatitude(),
                                        t2.getStart_point().getLongitude());
                                LatLng t2_end=new LatLng(t2.getEnd_point().getLatitude(),
                                        t2.getEnd_point().getLongitude());



                                double ds1= SphericalUtil.computeDistanceBetween(start_point,t1_start);
                                double ds2=SphericalUtil.computeDistanceBetween(start_point,t2_start);

                                double de1= SphericalUtil.computeDistanceBetween(finish_point,t1_end);
                                double de2= SphericalUtil.computeDistanceBetween(finish_point,t2_end);

                                double x1=ds1-ds2;
                                double x2=de1-de2;

                                return (int)(ds1-ds2);
                            }
                        });

                        Calendar calendar=Calendar.getInstance();
                        calendar.setTime(ride_date);
                        calendar.add(Calendar.DAY_OF_MONTH,-1);

                        tripList.removeIf(new Predicate<Trip>() {
                            @Override
                            public boolean test(Trip trip) {
                                if(trip.getTrip_date()==null)
                                    return false;
                                else if(trip.getTrip_date().before(calendar.getTime()))
                                    return true;
                                else
                                    return  false;
                            }
                        });

                        SearchTripsListAdapter adapter=new SearchTripsListAdapter(tripList,SearchResultsActivity.this,start_point,finish_point, ride_date);
                        recycler_trips.setAdapter(adapter);

                        progressDialog.dismiss();



                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                         Toast.makeText(SearchResultsActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                    finish();
                    }
                });



    }
}