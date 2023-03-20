package com.ksacp2022t3.aladarbi;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonLineStringStyle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ViewTripPathActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ProgressDialog progressDialog;
    GeoJsonLayer currentJsonLayer;
    LatLng t_start,t_end;
    AppCompatButton btn_ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_trip_path);
        btn_ok = findViewById(R.id.btn_ok);


        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        progressDialog=new ProgressDialog(this);
                progressDialog.setTitle("تحميل معلومات الرحلة");
                progressDialog.setMessage("الرجاء الانتظار");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        JSONObject object = new JSONObject();
         currentJsonLayer = new GeoJsonLayer(mMap, object);

        Bundle points=getIntent().getBundleExtra("points");

            t_start = points.getParcelable("t_start");
            t_end = points.getParcelable("t_end");
            if(points.containsKey("start_point")) {
                LatLng start_point = points.getParcelable("start_point");
                LatLng finish_point = points.getParcelable("finish_point");
                mMap.addMarker(new MarkerOptions().title("نقطة انطلاقك").position(start_point));

                mMap.addMarker(new MarkerOptions().title("نقطة وصولك").position(finish_point));
            }

        int height = 140;
        int width = 125;
        BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.start_pin);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        mMap.addMarker(new MarkerOptions().title("نقطة انطلاقك").position(t_start)
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
        );
         bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.finish_pin);
         b = bitmapdraw.getBitmap();
         smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        mMap.addMarker(new MarkerOptions().title("نقطة وصولك").position(t_end)
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));



        drawShortRoute();









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


                JSONArray point = new JSONArray();
                point.put(t_start.longitude);
                point.put(t_start.latitude);
                coordinates.put(point);
            JSONArray point1 = new JSONArray();
            point1.put(t_end.longitude);
            point1.put(t_end.latitude);
            coordinates.put(point1);

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

                        currentJsonLayer = new GeoJsonLayer(mMap, response);
                        currentJsonLayer.addLayerToMap();
                        GeoJsonFeature feature = currentJsonLayer.getFeatures().iterator().next();

                        GeoJsonLineStringStyle lineStringStyle = new GeoJsonLineStringStyle();
                        lineStringStyle.setColor(Color.RED);
                        lineStringStyle.setWidth(35);
                        feature.setLineStringStyle(lineStringStyle);








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
}