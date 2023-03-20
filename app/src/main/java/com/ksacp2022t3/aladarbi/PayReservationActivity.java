package com.ksacp2022t3.aladarbi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ksacp2022t3.aladarbi.models.Notification;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PayReservationActivity extends AppCompatActivity {
    EditText txt_card_number,txt_expiry_date,txt_cvv;
    ImageView btn_back;
    AppCompatButton btn_pay;

    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_reservation);
        txt_card_number = findViewById(R.id.txt_card_number);
        txt_expiry_date = findViewById(R.id.txt_expiry_date);
        txt_cvv = findViewById(R.id.txt_cvv);
        btn_back = findViewById(R.id.btn_back);
        btn_pay = findViewById(R.id.btn_pay);


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        firestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
                progressDialog.setTitle("يتم الدفع");
                progressDialog.setMessage("الرجاء الانتظار");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);

        String ride_id=getIntent().getStringExtra("ride_id") ;
        String reservation_id=getIntent().getStringExtra("reservation_id") ;
        String driver_id=getIntent().getStringExtra("driver_id") ;
        Date  ride_date= (Date) getIntent().getSerializableExtra("ride_date");


        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number=txt_card_number.getText().toString();
                String expiry=txt_expiry_date.getText().toString();
                String cvv=txt_cvv.getText().toString();


                if(number.isEmpty())
                {
                    txt_card_number.setError("حقل فارغ");
                    return;
                }


                if(expiry.isEmpty())
                {
                    txt_expiry_date.setError("حقل فارغ");
                    return;
                }

                if(cvv.isEmpty())
                {
                    txt_cvv.setError("حقل فارغ");
                    return;
                }

                if(!isValidCardNo(number))
                {
                    txt_card_number.setError("تنسيق رقم البطاقة غير صالح");
                    return;
                }

                progressDialog.show();
                firestore.collection("rides")
                        .document(ride_id)
                        .collection("reservations")
                        .document(reservation_id)
                        .update("paid",true)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                                Toast.makeText(PayReservationActivity.this,"تم الدفع بنجاح" , Toast.LENGTH_LONG).show();
                                //sen notification
                                SharedPreferences sharedPreferences=PayReservationActivity.this.getSharedPreferences("aladarbi",MODE_PRIVATE);
                                String user_name=sharedPreferences.getString("user_name","");

                                Notification notification=new Notification();
                                notification.setType("New Reservation");
                                notification.setRide_id(ride_id);
                                List<String> to=new ArrayList<>();
                                to.add(driver_id);
                                notification.setTo(to);
                                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
                                String date=simpleDateFormat.format(ride_date);
                                notification.setContent("قام " + user_name+  " بدفع كلفة الرحلة ذات التاريخ "+date);
                                notification.setFrom(firebaseAuth.getUid());

                                DocumentReference doc=firestore.collection("notifications")
                                        .document();
                                notification.setId(doc.getId());
                                doc.set(notification);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                 Toast.makeText(PayReservationActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();

                            }
                        });


            }
        });
    }

    boolean isValidCardNo(String str)
    {

        ArrayList<String> listOfPattern=new ArrayList<String>();

        String ptVisa = "^4[0-9]{6,}$";
        listOfPattern.add(ptVisa);
        String ptMasterCard = "^5[1-5][0-9]{5,}$";
        listOfPattern.add(ptMasterCard);
        String ptAmeExp = "^3[47][0-9]{5,}$";
        listOfPattern.add(ptAmeExp);
        String ptDinClb = "^3(?:0[0-5]|[68][0-9])[0-9]{4,}$";
        listOfPattern.add(ptDinClb);
        String ptDiscover = "^6(?:011|5[0-9]{2})[0-9]{3,}$";
        listOfPattern.add(ptDiscover);
        String ptJcb = "^(?:2131|1800|35[0-9]{3})[0-9]{3,}$";
        listOfPattern.add(ptJcb);


        for (String pat:listOfPattern
             ) {
            // Compile the ReGex
            Pattern p = Pattern.compile(pat);
            Matcher m = p.matcher(str);
            if(m.matches()==true)
                return  true;
        }

        return false;
    }
}