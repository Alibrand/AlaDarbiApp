package com.ksacp2022t3.aladarbi.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ksacp2022t3.aladarbi.R;
import com.ksacp2022t3.aladarbi.TripRidesActivity;
import com.ksacp2022t3.aladarbi.models.Account;
import com.ksacp2022t3.aladarbi.models.Reservation;
import com.ksacp2022t3.aladarbi.models.Trip;

import java.text.SimpleDateFormat;
import java.util.List;

public class PassengersListAdapter extends RecyclerView.Adapter<PassengerItem> {
    List<Reservation> reservations;
    Context context;
    FirebaseFirestore firestore;



    public PassengersListAdapter(List<Reservation> reservations, Context context) {
        this.reservations = reservations;
        this.context = context;
        firestore=FirebaseFirestore.getInstance();

    }

    @NonNull
    @Override
    public PassengerItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_passenger,parent,false);

        return new PassengerItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PassengerItem holder, int position) {
            Reservation reservation=reservations.get(position);
            holder.txt_name.setText(reservation.getClient_name());
            holder.txt_status.setText(reservation.isPaid()?"مدفوع":"غير مدفوع");
            firestore.collection("accounts")
                    .document(reservation.getClient_id())
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
        return reservations.size();
    }
}
class PassengerItem extends RecyclerView.ViewHolder{
    TextView txt_name,txt_status;
    AppCompatButton btn_call;


    public PassengerItem(@NonNull View itemView) {
        super(itemView);
        txt_name=itemView.findViewById(R.id.txt_name);
        txt_status=itemView.findViewById(R.id.txt_status);
        btn_call=itemView.findViewById(R.id.btn_call);

    }
}
