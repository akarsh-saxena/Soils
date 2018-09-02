package com.application.soil.soils;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class SoilInfoActivity extends AppCompatActivity {

    ListView soilListView;
    ArrayAdapter<String> soilArrayAdapter;
    ArrayList<String> soilTypes;
    String stateName, cityName;
    RequestQueue requestQueue;
    int statePosition, cityPosition;
    JSONArray soilJSONArray;
    String stateNameEnglish, cityNameEnglish;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soil_info);

        final String JSONData = getResources().getString(R.string.states_JSON);

        //stateName = getIntent().getExtras().getString("stateName");
        //cityName = getIntent().getExtras().getString("cityName");
        stateNameEnglish = getIntent().getExtras().getString("stateNameEnglish");
        cityNameEnglish = getIntent().getExtras().getString("cityNameEnglish");

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        soilListView = findViewById(R.id.soilListView);
        soilTypes = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);

        soilArrayAdapter = new ArrayAdapter<>(this, R.layout.listview_item, soilTypes);
        soilListView.setAdapter(soilArrayAdapter);

        stateNameEnglish = stateNameEnglish.trim();
        cityNameEnglish = cityNameEnglish.trim();
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, JSONData, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            progressDialog.dismiss();
                            JSONArray statesArray = response.getJSONArray("states");
                            JSONObject stateObject = statesArray.getJSONObject(statePosition);
                            stateName = stateObject.getString("sname");
                            cityName = stateObject.getJSONArray("cities").getString(cityPosition);
                            JSONArray soilArray = stateObject.getJSONArray("soiltype");
                            for(int j=0; j<soilArray.length(); ++j) {
                                soilTypes.add(soilArray.get(j).toString());
                                soilArrayAdapter.notifyDataSetChanged();
                            }
                            soilArrayAdapter.notifyDataSetChanged();
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(SoilInfoActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            public Priority getPriority() {
                return Priority.LOW;
            }
        };

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
                                        if(TextUtils.equals(cityArray.getString(j).trim(), cityNameEnglish)) {
                                            statePosition = i;
                                            cityPosition = j;
                                            soilJSONArray = stateObject.getJSONArray("soiltype");
                                            return;
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Toast.makeText(SoilInfoActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }){
            @Override
            public Priority getPriority() {
                return Priority.HIGH;
            }
        };

        requestQueue.add(jsonObjectRequest1);
        requestQueue.add(jsonObjectRequest);

        soilListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(final AdapterView<?> adapterView, View view, final int i, long l) {
          /*              JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.GET, Values.STATES_ENG, null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        JSONArray statesArray;
                                        try {
                                            statesArray = response.getJSONArray("states");
                                            for (int j = 0; j < statesArray.length(); ++j) {
                                                JSONObject stateObject = statesArray.getJSONObject(j);
                                                if (TextUtils.equals(stateObject.getString("sname"), stateNameEnglish)) {
                                                    JSONArray soilArray = stateObject.getJSONArray("soiltype");
                                                    String soilNameEnglish = soilArray.getString(i);
                                                    Intent intent = new Intent(SoilInfoActivity.this, SoilDetails.class);
                                                    intent.putExtra("soilName", adapterView.getItemAtPosition(i).toString());
                                                    intent.putExtra("soilNameEnglish", soilNameEnglish);
                                                    intent.putExtra("cityName", cityName);
                                                    intent.putExtra("stateNameEnglish", stateNameEnglish);
                                                    intent.putExtra("cityNameEnglish", cityNameEnglish);
                                                    startActivity(intent);
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                });
                        requestQueue.add(jsonObjectRequest1);*/

                        try {
                            Intent intent = new Intent(SoilInfoActivity.this, SoilDetails.class);
                            intent.putExtra("soilName", adapterView.getItemAtPosition(i).toString());
                            intent.putExtra("soilNameEnglish", soilJSONArray.getString(i));
                            intent.putExtra("cityName", cityName);
                            intent.putExtra("stateNameEnglish", stateNameEnglish);
                            intent.putExtra("cityNameEnglish", cityNameEnglish);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }
}
