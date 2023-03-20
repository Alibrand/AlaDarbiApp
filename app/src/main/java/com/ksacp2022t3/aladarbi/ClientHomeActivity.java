package com.ksacp2022t3.aladarbi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.ksacp2022t3.aladarbi.adapters.NotificationsListAdapter;
import com.ksacp2022t3.aladarbi.models.Notification;

import java.util.List;

public class ClientHomeActivity extends AppCompatActivity {

    TextView txt_user_name,txt_clear_all;
    AppCompatButton btn_logout,btn_search,btn_my_reservations,btn_account,btn_chatbot;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;

    RecyclerView recycler_notifications;
    LinearLayoutCompat layout_notifications;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_home);
        txt_user_name = findViewById(R.id.txt_user_name);
        btn_logout = findViewById(R.id.btn_logout);
        btn_search = findViewById(R.id.btn_search);
        btn_my_reservations = findViewById(R.id.btn_my_reservations);
        btn_account = findViewById(R.id.btn_account);
        btn_chatbot = findViewById(R.id.btn_chatbot);
        recycler_notifications = findViewById(R.id.recycler_notifications);
        layout_notifications = findViewById(R.id.layout_notifications);
        txt_clear_all = findViewById(R.id.txt_clear_all);
        
        







        firebaseAuth= FirebaseAuth.getInstance();
        firestore= FirebaseFirestore.getInstance();

        SharedPreferences sharedPreferences=this.getSharedPreferences("aladarbi",MODE_PRIVATE);
        String user_name=sharedPreferences.getString("user_name","");
        txt_user_name.setText("أهلا " + user_name);





        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                Intent intent = new Intent(ClientHomeActivity.this,MainActivity. class);
                startActivity(intent);
                finish();
            }
        });

        btn_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ClientHomeActivity.this,UpdateAccountActivity. class);
                startActivity(intent);
            }
        });

        btn_my_reservations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ClientHomeActivity.this,MyReservationsActivity. class);
                startActivity(intent);
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ClientHomeActivity.this,SearchActivity. class);
                startActivity(intent);

            }
        });
        btn_chatbot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ClientHomeActivity.this,ChatBotActivity. class);
                startActivity(intent);
            }
        });

        txt_clear_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firestore.collection("notifications")
                        .whereEqualTo("status","unseen")
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                List<Notification> notifications=queryDocumentSnapshots.toObjects(Notification.class);
                                WriteBatch batch=firestore.batch();
                                for(Notification n:notifications)
                                {
                                    batch.update(firestore.collection("notifications")
                                            .document(n.getId()),"status","seen");


                                }

                                batch.commit()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                check_new_notifications();
                                            }
                                        });
                            }
                        });
            }
        });

    }
    void check_new_notifications(){
        firestore.collection("notifications")
                .whereArrayContains("to",firebaseAuth.getUid())
                .whereEqualTo("status","unseen")
                .orderBy("created_at", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Notification> notifications=queryDocumentSnapshots.toObjects(Notification.class);
                        if(notifications.size()>0) {
                            layout_notifications.setVisibility(View.VISIBLE);
                            NotificationsListAdapter adapter = new NotificationsListAdapter(notifications, ClientHomeActivity.this);
                            recycler_notifications.setAdapter(adapter);
                        }
                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        check_new_notifications();
    }
}