package com.ksacp2022t3.aladarbi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;
import java.util.Date;

public class SearchActivity extends AppCompatActivity {

    AppCompatButton btn_set_start,btn_set_finish,btn_search;
    LatLng start_point,finish_point;
    DatePicker dp_trip_date;
    ImageView btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        btn_set_start = findViewById(R.id.btn_set_start);
        btn_set_finish = findViewById(R.id.btn_set_finish);
        btn_search = findViewById(R.id.btn_search);
        dp_trip_date = findViewById(R.id.dp_trip_date);
        btn_back = findViewById(R.id.btn_back);


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });




        btn_set_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SearchActivity.this,SetLocationActivity.class);
                intent.putExtra("point_type","start");
                startActivityForResult(intent,100);
            }
        });

        btn_set_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SearchActivity.this,SetLocationActivity.class);
                intent.putExtra("point_type","finish");
                startActivityForResult(intent,110);
            }
        });


        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(start_point ==null || finish_point==null)
                {
                    Toast.makeText(SearchActivity.this,"يجب تحديد نقاط الانطلاق و الوصول" , Toast.LENGTH_LONG).show();
                return;
                }



                Calendar calendar=Calendar.getInstance();
                calendar.set(dp_trip_date.getYear(),dp_trip_date.getMonth(),dp_trip_date.getDayOfMonth());

                Bundle points=new Bundle();
                points.putParcelable("start_point",start_point);
                points.putParcelable("finish_point",finish_point);

                Intent intent=new Intent(SearchActivity.this,SearchResultsActivity.class);
                intent.putExtra("points",points);
                intent.putExtra("trip_date",calendar.getTime());
                startActivity(intent);

            }
        });







    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK)
        {
            double lat=data.getDoubleExtra("lat",0.0);
            double lng=data.getDoubleExtra("lng",0.0);
            if(data.getStringExtra("point_type").equals("start"))
            {
                start_point=new LatLng(lat,lng);
                btn_set_start.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_check_circle_24,0,0,0);
            }
            else
            {
                finish_point=new LatLng(lat,lng);
                btn_set_finish.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_check_circle_24,0,0,0);

            }
        }
    }
}