package com.ksacp2022t3.aladarbi.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.ksacp2022t3.aladarbi.R;
import com.ksacp2022t3.aladarbi.RideDetailsActivity;
import com.ksacp2022t3.aladarbi.models.Ride;

import java.text.SimpleDateFormat;
import java.util.List;

public class RidesListAdapter extends RecyclerView.Adapter<RideItem> {
    List<Ride> rideList;
    Context context;

    public RidesListAdapter(List<Ride> rideList, Context context) {
        this.rideList = rideList;
        this.context = context;
    }

    @NonNull
    @Override
    public RideItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ride,parent,false);

        return new RideItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideItem holder, int position) {
        Ride ride=rideList.get(position);
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");

        holder.txt_date.setText(simpleDateFormat.format(ride.getRide_date()));
        holder.txt_reserved_seats.setText(String.valueOf(ride.getReserved_seats()));

        holder.txt_status.setText(ride.getStatus());

        holder.btn_view_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, RideDetailsActivity. class);
                intent.putExtra("ride_id",ride.getId());
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return rideList.size();
    }
}

class RideItem extends RecyclerView.ViewHolder
{
    TextView txt_date,txt_reserved_seats,txt_status;
    AppCompatButton btn_view_details;
    public RideItem(@NonNull View itemView) {
        super(itemView);
        txt_date=itemView.findViewById(R.id.txt_date);
        txt_reserved_seats=itemView.findViewById(R.id.txt_reserved_seats);
        btn_view_details=itemView.findViewById(R.id.btn_view_details);
        txt_status=itemView.findViewById(R.id.txt_status);
    }
}
