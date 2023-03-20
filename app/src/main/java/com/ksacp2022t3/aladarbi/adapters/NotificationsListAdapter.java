package com.ksacp2022t3.aladarbi.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.maps.android.SphericalUtil;
import com.ksacp2022t3.aladarbi.R;
import com.ksacp2022t3.aladarbi.RideDetailsActivity;
import com.ksacp2022t3.aladarbi.TripDetailsActivity;
import com.ksacp2022t3.aladarbi.models.Notification;
import com.ksacp2022t3.aladarbi.models.Trip;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NotificationsListAdapter extends RecyclerView.Adapter<NotificationItem> {
    List<Notification> notifications;
    Context context;
    FirebaseFirestore firestore;


    public NotificationsListAdapter(List<Notification> notificationList, Context context ) {
        this.notifications=notificationList;
        this.context = context;
        firestore=FirebaseFirestore.getInstance();

    }
    @NonNull
    @Override
    public NotificationItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification,parent,false);

        return new NotificationItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationItem holder, int position) {
            Notification notification=notifications.get(position);

            holder.txt_content.setText(notification.getContent());
            if(notification.getType().equals("Normal"))
            {
                holder.btn_view_details.setVisibility(View.GONE);

            }
            else
            {
                holder.btn_view_details.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, RideDetailsActivity. class);
                        intent.putExtra("ride_id",notification.getRide_id());
                        context.startActivity(intent);
                    }
                });
            }

            holder.btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    firestore.collection("notifications")
                            .document(notification.getId())
                            .update("status","seen")
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    notifications.remove(notification);
                                    NotificationsListAdapter.this.notifyDataSetChanged();
                                }
                            });
                }
            });






    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }





}
class NotificationItem extends RecyclerView.ViewHolder{
    TextView txt_content;
    AppCompatButton btn_view_details,btn_delete;


    public NotificationItem(@NonNull View itemView) {
        super(itemView);
        txt_content=itemView.findViewById(R.id.txt_content);
        btn_view_details=itemView.findViewById(R.id.btn_view_details);
        btn_delete=itemView.findViewById(R.id.btn_delete);

    }

}
