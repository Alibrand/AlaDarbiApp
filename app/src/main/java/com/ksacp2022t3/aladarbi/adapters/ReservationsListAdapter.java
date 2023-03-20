package com.ksacp2022t3.aladarbi.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ksacp2022t3.aladarbi.R;
import com.ksacp2022t3.aladarbi.ReservationDetailsActivity;
import com.ksacp2022t3.aladarbi.RideDetailsActivity;
import com.ksacp2022t3.aladarbi.TripDetailsActivity;
import com.ksacp2022t3.aladarbi.models.Account;
import com.ksacp2022t3.aladarbi.models.Ride;

import java.text.SimpleDateFormat;
import java.util.List;

public class ReservationsListAdapter extends RecyclerView.Adapter<ReservationItem> {
    List<Ride> rideList;
    Context context;
    FirebaseFirestore firestore;

    public ReservationsListAdapter(List<Ride> rideList, Context context) {
        this.rideList = rideList;
        this.context = context;
        firestore=FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ReservationItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reservation,parent,false);

        return new ReservationItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationItem holder, int position) {
        Ride ride=rideList.get(position);
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");

        holder.txt_date.setText(simpleDateFormat.format(ride.getRide_date()));
        holder.txt_price.setText(String.valueOf(ride.getPrice()));
        holder.txt_driver_name.setText(ride.getDriver_name());

        holder.txt_status.setText(ride.getStatus());

        holder.btn_view_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ReservationDetailsActivity. class);
                intent.putExtra("trip_id",ride.getTrip_id());
                intent.putExtra("ride_date",ride.getRide_date());
                context.startActivity(intent);

            }
        });

        firestore.collection("accounts")
                .document(ride.getDriver_id())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Account account=documentSnapshot.toObject(Account.class);
                        holder.btn_call.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:"+account.getPhone()));
                                context.startActivity(intent);
                            }
                        });
                    }
                });
    }

    @Override
    public int getItemCount() {
        return rideList.size();
    }
}

class ReservationItem extends RecyclerView.ViewHolder
{
    TextView txt_date,txt_driver_name,txt_status,txt_price;
    AppCompatButton btn_view_details,btn_call;
    public ReservationItem(@NonNull View itemView) {
        super(itemView);
        txt_date=itemView.findViewById(R.id.txt_date);
        txt_driver_name=itemView.findViewById(R.id.txt_driver_name);
        btn_view_details=itemView.findViewById(R.id.btn_view_details);
        txt_status=itemView.findViewById(R.id.txt_status);
        txt_price=itemView.findViewById(R.id.txt_price);
        btn_call=itemView.findViewById(R.id.btn_call);
    }
}
