package com.application.soil.soils;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ImageView weatherIcon;
    TextView weatherStatus, tempTv, pressureTv, humidityTv, tmpMinTv, tmpMaxTv, windSpeedTv, windDegTv, soilInfo;
    WeatherModel weatherModel;
    RequestQueue requestQueue;
    double lat = 20.5;
    double longi = 78.6;
    String cityName = "";
    String stateName = "";
    ProgressDialog progressDialog;
    int statePosition;
    int cityPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading");
        progressDialog.show();

        stateName = getIntent().getExtras().get("stateName").toString();
        cityName = getIntent().getExtras().get("cityName").toString();
        final String stateNameEnglish = getIntent().getExtras().getString("stateNameEnglish");
        final String cityNameEnglish = getIntent().getExtras().getString("cityNameEnglish");
        final String jsonData = getResources().getString(R.string.states_JSON);

        weatherIcon = findViewById(R.id.weatherIcon);
        weatherStatus = findViewById(R.id.weatherStatus);
        tempTv = findViewById(R.id.tempTv);
        pressureTv = findViewById(R.id.pressureTv);
        humidityTv = findViewById(R.id.humidityTv);
        tmpMinTv = findViewById(R.id.tmpMinTv);
        tmpMaxTv = findViewById(R.id.tmpMaxTv);
        windSpeedTv  = findViewById(R.id.windSpeedTv);
        windDegTv = findViewById(R.id.windDegTv);
        soilInfo = findViewById(R.id.soilInfo);

        requestQueue = Volley.newRequestQueue(this);

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.GET, Values.STATES_ENG, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray statesArray = response.getJSONArray("states");
                            for (int i = 0; i < statesArray.length(); ++i) {
                                JSONObject stateObject = statesArray.getJSONObject(i);
                                if (TextUtils.equals(stateObject.getString("sname"), stateNameEnglish)) {
                                    JSONArray cityArray = stateObject.getJSONArray("cities");
                                    for (int j = 0; j < cityArray.length(); ++j) {
                                        if(TextUtils.equals(cityArray.getString(j), cityNameEnglish)) {
                                            statePosition = i;
                                            cityPosition = j;
                                            return;
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        requestQueue.add(jsonObjectRequest1);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, jsonData, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray statesArray = response.getJSONArray("states");
                            JSONObject stateObject = statesArray.getJSONObject(statePosition);
                            stateName = stateObject.getString("sname");
                            cityName = stateObject.getJSONArray("cities").getString(cityPosition);
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MapsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonObjectRequest);


        try {
            getWeatherInfo(stateNameEnglish, cityNameEnglish);
            List<Address> addressList = geocoder.getFromLocationName(cityNameEnglish+stateNameEnglish+",India", 1);
            if(addressList.size()>0) {
                lat = addressList.get(0).getLatitude();
                longi = addressList.get(0).getLongitude();
            }
        } catch (IOException e) {
            Toast.makeText(this, "Error in weather", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        soilInfo.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        progressDialog.show();
                        Intent intent = new Intent(MapsActivity.this, SoilInfoActivity.class);
                        //intent.putStringArrayListExtra("soilTypes", soilTypes);
                        intent.putExtra("stateName", stateName);
                        intent.putExtra("cityName", cityName);
                        intent.putExtra("stateNameEnglish", stateNameEnglish);
                        intent.putExtra("cityNameEnglish", cityNameEnglish);
                        startActivity(intent);
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(progressDialog!=null)
            if(progressDialog.isShowing())
                progressDialog.dismiss();
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

        LatLng location = new LatLng(lat, longi);
        mMap.addMarker(new MarkerOptions().position(location).title(cityName+","+stateName+",India"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 800));
        progressDialog.dismiss();
    }

    private void getWeatherInfo(String stateName, String cityName) throws IOException {

        //String weather_url = Constants.CURRENT_WEATHER_URL+cityCode+"&appid="+Constants.API_KEY+"&units=metric";
        cityName = cityName.replace("*", " ").trim();
        stateName = stateName.replace("*", "").trim();
        stateName = stateName.replace(" ","+");
        String weather_url = Constants.CURRENT_WEATHER_URL+cityName+","+stateName+",India&appid="+Constants.API_KEY+"&units=metric";
        Log.d("muy", weather_url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, weather_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        weatherModel = gson.fromJson(response, WeatherModel.class);

                        Glide.with(MapsActivity.this).load(Constants.WEATHER_ICON+weatherModel.getWeather().get(0).getIcon()+".png").into(weatherIcon);
                        weatherStatus.append(weatherModel.getWeather().get(0).getDescription());
                        tempTv.append(String.valueOf(weatherModel.getMain().getTemp()));
                        pressureTv.append(String.valueOf(weatherModel.getMain().getPressure()));
                        humidityTv.append(String.valueOf(weatherModel.getMain().getHumidity()));
                        tmpMinTv.append(String.valueOf(weatherModel.getMain().getTemp_min()));
                        tmpMaxTv.append(String.valueOf(weatherModel.getMain().getTemp_max()));
                        windSpeedTv.append(String.valueOf(weatherModel.getWind().getSpeed()));
                        windDegTv.append(String.valueOf(weatherModel.getWind().getDeg()));
                        //Log.d("weather", String.valueOf(weatherModel.getMain().getTemp()));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(MapsActivity.this, "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(stringRequest);
    }
}
