package com.ksacp2022t3.aladarbi;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.ksacp2022t3.aladarbi.databinding.ActivitySelectPathMapBinding;

public class SetLocationActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;

    private FusedLocationProviderClient fusedLocationProviderClient;
    AppCompatButton btn_ok;
    LatLng selected_point;
    String point_type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_location);
        btn_ok = findViewById(R.id.btn_ok);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        point_type=getIntent().getStringExtra("point_type");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                if(selected_point==null)
                    setResult(RESULT_CANCELED);
                else{
                    intent.putExtra("point_type",point_type);
                    intent.putExtra("lat",selected_point.latitude);
                    intent.putExtra("lng",selected_point.longitude);
                    setResult(RESULT_OK,intent);
                }

                finish();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);

        new AlertDialog.Builder(SetLocationActivity.this)
                .setPositiveButton("نعم",null)
                .setTitle("تلميحات")
                .setMessage("قم بتحديد الموقع ثم اضغط على موافق ")
                .show();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(SetLocationActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 110);

            return;
        }
        mMap.setMyLocationEnabled(true);


        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                CameraUpdate update;
                update = CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(), location.getLongitude()), 15);

                mMap.animateCamera(update);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                selected_point=latLng;
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng));
            }
        });
    }
}