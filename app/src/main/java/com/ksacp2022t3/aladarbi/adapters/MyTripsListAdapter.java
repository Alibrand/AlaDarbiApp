package com.ksacp2022t3.aladarbi.adapters;

import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ksacp2022t3.aladarbi.R;
import com.ksacp2022t3.aladarbi.TripDetailsActivity;
import com.ksacp2022t3.aladarbi.TripRidesActivity;
import com.ksacp2022t3.aladarbi.models.Notification;
import com.ksacp2022t3.aladarbi.models.Ride;
import com.ksacp2022t3.aladarbi.models.Trip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MyTripsListAdapter extends RecyclerView.Adapter<MyTripItem> {
    List<Trip> tripList;
    Context context;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;

    public MyTripsListAdapter(List<Trip> tripList, Context context) {
        this.tripList = tripList;
        this.context = context;
        firestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(context);
                progressDialog.setTitle("يتم الان حذف الرحلة");
                progressDialog.setMessage("الرجاء الانتظار");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
    }

    @NonNull
    @Override
    public MyTripItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_trip,parent,false);

        return new MyTripItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyTripItem holder, int position) {
            Trip trip=tripList.get(position);
            holder.txt_car_model.setText(trip.getCar_model());
            holder.txt_car_number.setText(trip.getCar_number());
            holder.txt_trip_type.setText(trip.getTrip_type());
            if(trip.getTrip_type().equals("يومياً"))
                holder.layout_date.setVisibility(View.GONE);
            else
            {
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
                holder.txt_trip_date.setText(simpleDateFormat.format(trip.getTrip_date()));
            }

            holder.btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progressDialog.show();
                    firestore.collection("trips")
                            .document(trip.getId())
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    List<String> to=new ArrayList<>();

                                    tripList.remove(trip);
                                    MyTripsListAdapter.this.notifyDataSetChanged();
                                    firestore.collection("rides")
                                            .whereEqualTo("trip_id",trip.getId())
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                                    progressDialog.dismiss();
                                                    List<Ride> rideList=queryDocumentSnapshots.toObjects(Ride.class);
                                                    for(Ride r:rideList)
                                                    {
                                                        to.addAll(r.getClients());
                                                        firestore.collection("rides")
                                                                .document(r.getId())
                                                                .delete();
                                                    }
                                                }
                                            });

                                    //notification


                                    Notification notification=new Notification();
                                    notification.setType("Normal");

                                    notification.setTo(to);
                                    notification.setContent( "السائق  " + trip.getDriver_name() +" قام بحذف رحلته....راجع حجوزاتك");
                                    notification.setFrom(trip.getDriver_id());

                                    DocumentReference doc=firestore.collection("notifications")
                                            .document();
                                    notification.setId(doc.getId());
                                    doc.set(notification);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                     Toast.makeText(context,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                                }
                            });
                }
            });

            holder.btn_view_requests.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, TripRidesActivity. class);
                    intent.putExtra("trip_id",trip.getId());
                    context.startActivity(intent);
                }
            });
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }
}
class MyTripItem extends RecyclerView.ViewHolder{
    TextView txt_car_model,txt_car_number,txt_trip_date,txt_trip_type;
    AppCompatButton btn_view_requests,btn_delete;
    LinearLayoutCompat layout_date;

    public MyTripItem(@NonNull View itemView) {
        super(itemView);
        txt_car_model=itemView.findViewById(R.id.txt_car_model);
        txt_car_number=itemView.findViewById(R.id.txt_car_number);
        txt_trip_date=itemView.findViewById(R.id.txt_trip_date);
        txt_trip_type=itemView.findViewById(R.id.txt_trip_type);
        btn_view_requests=itemView.findViewById(R.id.btn_view_requests);
        btn_delete=itemView.findViewById(R.id.btn_delete);
        layout_date=itemView.findViewById(R.id.layout_date);
    }
}
