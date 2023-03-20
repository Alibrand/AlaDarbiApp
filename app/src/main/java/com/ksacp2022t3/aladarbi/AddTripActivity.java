package com.ksacp2022t3.aladarbi;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.ksacp2022t3.aladarbi.models.Trip;

import java.util.Calendar;
import java.util.Date;

public class AddTripActivity extends AppCompatActivity {

    AppCompatButton btn_select_path,btn_save;
    TextView txt_total_distance,txt_total_price;
    GeoPoint start_point,end_point;
    EditText txt_car_model,txt_car_number,txt_passengers;
    RadioGroup group_type;
    TimePicker tp_time;
    DatePicker dp_trip_date;
    LinearLayoutCompat layout_date;
    ImageView btn_back;
    RadioButton rd_daily,rd_once;
    double total_distance;
    double total_price;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);
        btn_select_path = findViewById(R.id.btn_select_path);
        txt_total_distance = findViewById(R.id.txt_total_distance);
        txt_total_price = findViewById(R.id.txt_total_price);
        txt_car_model = findViewById(R.id.txt_car_model);
        txt_car_number = findViewById(R.id.txt_car_number);
        txt_passengers = findViewById(R.id.txt_passengers);
        group_type = findViewById(R.id.group_type);
        tp_time = findViewById(R.id.tp_start_time);
        dp_trip_date = findViewById(R.id.dp_trip_date);
        layout_date = findViewById(R.id.layout_date);
        btn_back = findViewById(R.id.btn_back);
        rd_daily = findViewById(R.id.rd_daily);
        rd_once = findViewById(R.id.rd_once);
        btn_save = findViewById(R.id.btn_save);


        firestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();

        progressDialog=new ProgressDialog(this);
                progressDialog.setTitle("حفظ البيانات");
                progressDialog.setMessage("الرجاء الانتظار");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);






        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        rd_daily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout_date.setVisibility(View.GONE);
            }
        });
        rd_once.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout_date.setVisibility(View.VISIBLE);
            }
        });







        btn_select_path.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddTripActivity.this,SelectPathMapActivity. class);
                startActivityForResult(intent,110);
            }
        });


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_txt_car_model =txt_car_model.getText().toString();
                String str_txt_car_number =txt_car_number.getText().toString();
                String str_txt_passengers =txt_passengers.getText().toString();
                RadioButton selected=findViewById(group_type.getCheckedRadioButtonId());
                String trip_type=selected.getText().toString();
                if(str_txt_car_model.isEmpty())
                {
                     txt_car_model.setError("الحقل مطلوب");
                     return;
                }
                if(str_txt_car_number.isEmpty())
                {
                    txt_car_number.setError("الحقل مطلوب");
                     return;
                }

                if(str_txt_passengers.isEmpty())
                {
                     txt_passengers.setError("الحقل مطلوب");
                     return;
                }

                if(start_point==null)
                {
                    makeText(AddTripActivity.this,"يجب أن تحدد مسار الرحلة" , LENGTH_LONG).show();
                return;
                }

                Trip trip=new Trip();
                trip.setTrip_type(trip_type);
                trip.setCar_model(str_txt_car_model);
                trip.setCar_number(str_txt_car_number);
                trip.setMax_passengers_count(Integer.parseInt(str_txt_passengers));
                trip.setDeparture_hours(tp_time.getHour());
                trip.setDeparture_minutes(tp_time.getMinute());
                trip.setDriver_id(firebaseAuth.getUid());
                trip.setTotal_distance(total_distance);
                trip.setTotal_price(total_price);
                trip.setStart_point(start_point);
                trip.setEnd_point(end_point);
                SharedPreferences sharedPreferences=AddTripActivity.this.getSharedPreferences("aladarbi",MODE_PRIVATE);
                String user_name=sharedPreferences.getString("user_name","");
                trip.setDriver_name(user_name);




                if(rd_once.isChecked())
                {
                    Calendar calendar=Calendar.getInstance();
                    calendar.set(dp_trip_date.getYear(),dp_trip_date.getMonth(),dp_trip_date.getDayOfMonth());
                    trip.setTrip_date(calendar.getTime());
                }


                DocumentReference doc=firestore.collection("trips")
                        .document();
                trip.setId(doc.getId());

                progressDialog.show();
                doc.set(trip)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                                finish();
                                makeText(AddTripActivity.this,"تمت إضافة الرحلة بنجاح" , LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                 makeText(AddTripActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                            }
                        });



            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==110)
        {
             total_distance=data.getDoubleExtra("distance",0);
            txt_total_distance.setText(distance_label(total_distance));
            total_price=num_round(3*total_distance/1000);
            txt_total_price.setText(total_price+" ر.س");
            String[] start_coords=data.getStringExtra("start_point").split(",");
            String[] end_coords=data.getStringExtra("end_point").split(",");
            start_point=new GeoPoint(Double.parseDouble(start_coords[0]),Double.parseDouble(start_coords[1]));
            end_point=new GeoPoint(Double.parseDouble(end_coords[0]),Double.parseDouble(end_coords[1]));



        }
    }

    private String distance_label(double distance) {
        if (distance < 1000)
            return distance + " متر";
        else
            return (num_round(distance / 1000) + " كم");
    }

    private String duration_label(double duration) {
        if (duration < 60)
            return duration + " ثانية";
        else if (duration < 3600)
            return Math.round(duration / 60) + " دقيقة";
        else if (duration < 86400)
            return Math.round(duration / 3600) + " ساعة";
        else
            return Math.round(duration / 86400) + " أيام";
    }

    private double num_round(double num) {
        return Math.round(num * 100.0) / 100.0;
    }


}