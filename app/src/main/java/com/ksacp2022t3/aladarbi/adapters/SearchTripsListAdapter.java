package com.ksacp2022t3.aladarbi.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.maps.android.SphericalUtil;
import com.ksacp2022t3.aladarbi.R;
import com.ksacp2022t3.aladarbi.TripDetailsActivity;
import com.ksacp2022t3.aladarbi.models.Trip;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SearchTripsListAdapter extends RecyclerView.Adapter<SearchTripItem> {
    List<Trip> tripList;
    Context context;
    LatLng start_point,finish_point;
    Date ride_date;

    public SearchTripsListAdapter(List<Trip> tripList, Context context, LatLng start_point,LatLng finish_point,Date ride_date) {
        this.tripList = tripList;
        this.context = context;
        this.start_point=start_point;
        this.finish_point=finish_point;
        this.ride_date=ride_date;
    }
    @NonNull
    @Override
    public SearchTripItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_trip,parent,false);

        return new SearchTripItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchTripItem holder, int position) {
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

            LatLng t_start=new LatLng(trip.getStart_point().getLatitude(),
                trip.getStart_point().getLongitude());

            double dis= SphericalUtil.computeDistanceBetween(start_point,t_start);

            holder.txt_trip_start_distance.setText(distance_label(dis));

            holder.btn_view_details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, TripDetailsActivity. class);
                    intent.putExtra("trip_id",trip.getId());
                    Bundle points=new Bundle();
                    points.putParcelable("start_point",start_point);
                    points.putParcelable("finish_point",finish_point);
                    intent.putExtra("ride_date",ride_date);
                    intent.putExtra("points",points);
                    context.startActivity(intent);
                }
            });






    }

    @Override
    public int getItemCount() {
        return tripList.size();
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

}
class SearchTripItem extends RecyclerView.ViewHolder{
    TextView txt_car_model,txt_car_number,txt_trip_date,txt_trip_type,txt_trip_start_distance;
    AppCompatButton btn_view_details;
    LinearLayoutCompat layout_date;

    public SearchTripItem(@NonNull View itemView) {
        super(itemView);
        txt_car_model=itemView.findViewById(R.id.txt_car_model);
        txt_car_number=itemView.findViewById(R.id.txt_car_number);
        txt_trip_date=itemView.findViewById(R.id.txt_trip_date);
        txt_trip_type=itemView.findViewById(R.id.txt_trip_type);
        btn_view_details=itemView.findViewById(R.id.btn_view_details);
        txt_trip_start_distance=itemView.findViewById(R.id.txt_trip_start_distance);
        layout_date=itemView.findViewById(R.id.layout_date);
    }

}
