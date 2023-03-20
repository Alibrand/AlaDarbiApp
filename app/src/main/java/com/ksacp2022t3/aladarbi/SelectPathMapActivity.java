package com.ksacp2022t3.aladarbi;

import static android.widget.Toast.*;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.GeoPoint;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.data.Geometry;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonLineStringStyle;
import com.ksacp2022t3.aladarbi.databinding.ActivitySelectPathMapBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectPathMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivitySelectPathMapBinding binding;
    private FusedLocationProviderClient fusedLocationProviderClient;
    AppCompatButton btn_get_path,btn_ok,btn_reset;
    List<Marker> trip_locations=new ArrayList<>();
    List<GeoPoint> route_points=new ArrayList<>();
    ProgressDialog progressDialog;
    GeoJsonLayer currentJsonLayer;
    double totaldistance;
    boolean path_drwan=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySelectPathMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        btn_get_path = findViewById(R.id.btn_get_path);
        btn_ok = findViewById(R.id.btn_ok);
        btn_reset = findViewById(R.id.btn_reset);
        

        progressDialog=new ProgressDialog(this);
                progressDialog.setTitle("حساب مسار الرحلة");
                progressDialog.setMessage("يرجى الانتظار");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        JSONObject object = new JSONObject();
        currentJsonLayer = new GeoJsonLayer(mMap, object);

        new AlertDialog.Builder(SelectPathMapActivity.this)
                .setPositiveButton("موافق",null)
                .setTitle("تلميحات")
                .setMessage("قم بتحديد موقع الانطلاق وموقع الوصول \n يمكنك تحديد نقاط إضافية لتحديد مسار الرحلة بشكل دقيق\n عند الانتهاء قم بالضغط على حساب المسار")
                .show();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(SelectPathMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 110);

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

        btn_get_path.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(trip_locations.size()<2)
                {
                    makeText(SelectPathMapActivity.this,"يجب أن تحدد نقطتين على الأقل" , LENGTH_LONG).show();
                    return;
                }
                else
                drawShortRoute();
            }
        });

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                path_drwan=false;
                if (currentJsonLayer.isLayerOnMap())
                    currentJsonLayer.removeLayerFromMap();
                mMap.clear();
                trip_locations.clear();

            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!path_drwan)
                    finish();
                else {
                    LatLng start_point=trip_locations.get(0).getPosition();
                    LatLng end_point=trip_locations.get(trip_locations.size()-1).getPosition();
                    Intent intent = new Intent();
                    intent.putExtra("distance", totaldistance);
                    intent.putExtra("start_point",start_point.latitude+","+start_point.longitude);
                    intent.putExtra("end_point",end_point.latitude+","+end_point.longitude);

                     Log.d("mapss",trip_locations.get(0).getPosition().toString());
                    setResult(110, intent);
                    finish();
                }
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                trip_locations.add(mMap.addMarker(new MarkerOptions().position(latLng)));
            }
        });

    }

    private void drawShortRoute() {
      //  progressDialog.setTitle("Finding Best Route");
      //  progressDialog.setMessage("Please Wait");
        progressDialog.show();



        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api.openrouteservice.org/v2/directions/driving-car/geojson";

        JSONObject postData = new JSONObject();
        JSONArray coordinates = new JSONArray();


        try {

            for (Marker marker:trip_locations
                 ) {
                JSONArray point = new JSONArray();
                point.put(marker.getPosition().longitude);
                point.put(marker.getPosition().latitude);
                coordinates.put(point);
            }
            postData.put("coordinates", coordinates);
        } catch (JSONException e) {
            e.printStackTrace();
        }
// Request a string response from the provided URL.
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, url, postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        progressDialog.dismiss();
                        if (currentJsonLayer.isLayerOnMap())
                            currentJsonLayer.removeLayerFromMap();
                        path_drwan=true;
                        currentJsonLayer = new GeoJsonLayer(mMap, response);
                        currentJsonLayer.addLayerToMap();
                        GeoJsonFeature feature = currentJsonLayer.getFeatures().iterator().next();

                        GeoJsonLineStringStyle lineStringStyle = new GeoJsonLineStringStyle();
                        lineStringStyle.setColor(Color.RED);
                        lineStringStyle.setWidth(35);
                        feature.setLineStringStyle(lineStringStyle);


                        if (feature.hasProperty("summary")) {
                            String summary = feature.getProperty("summary");
                            try {
                                JSONObject route_summary = new JSONObject(summary);
                                 totaldistance = route_summary.getDouble("distance");
                                double totalduration = route_summary.getDouble("duration");
                                makeText(SelectPathMapActivity.this,distance_label(totaldistance)+" "+duration_label(totalduration) , LENGTH_LONG).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }




                        CameraUpdate update;
                        update = CameraUpdateFactory.newLatLngBounds(feature.getBoundingBox(), 220);

                        mMap.animateCamera(update);




                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();

                // As of f605da3 the following should work
                try {
                    if (error.networkResponse != null) {

                        int statusCode = error.networkResponse.statusCode;
                        if (error.networkResponse.data != null) {

                            String body = new String(error.networkResponse.data, "UTF-8");
                            if (statusCode == 400) {


                                JSONObject obj = new JSONObject(body);
                                JSONObject error_obj = obj.getJSONObject("error");
                                String errorMsg = error_obj.getString("message");

                                // getting error msg message may be different according to your API
                                //Display this error msg to user
                                makeText(getApplicationContext(), errorMsg, LENGTH_SHORT).show();


                            }
                        }
                    }
                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                    Log.e("TAG", "UNKNOWN ERROR :" + e.getMessage());
                    makeText(getApplicationContext(), "Something went Wrong!", LENGTH_SHORT).show();
                }

            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "5b3ce3597851110001cf62488310917010d3436c8a6e67dc7d6286c6");
                params.put("Content-Type", "application/json");

                return params;
            }
        };

// Add the request to the RequestQueue.
        queue.add(stringRequest);
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 110) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                makeText(SelectPathMapActivity.this, "We could not determine your location", LENGTH_LONG).show();
            }

        }
    }
}