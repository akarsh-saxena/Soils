package com.application.soil.soils;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class CityActivity extends AppCompatActivity {

    ArrayList<String> cityList, soilTypes;
    ListView cityListView;
    ArrayAdapter cityAdapter;

    RequestQueue requestQueue;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);

        final String JSONData = getResources().getString(R.string.states_JSON);

        final String stateName = getIntent().getExtras().getString("stateName");
        final String stateNameEnglish = getIntent().getExtras().getString("stateNameEnglish");

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading");
        progressDialog.show();

        requestQueue = Volley.newRequestQueue(this);

        cityListView = findViewById(R.id.cityListView);
        cityList = new ArrayList<>();
        soilTypes = new ArrayList<>();
        cityAdapter = new ArrayAdapter<>(this, R.layout.listview_item, cityList);
        cityListView.setAdapter(cityAdapter);

        ActionBar myToolbar = getSupportActionBar();
        myToolbar.setTitle(getTitle()+" ("+stateName+")");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, JSONData, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray statesArray = response.getJSONArray("states");
                            for (int i = 0; i < statesArray.length(); ++i) {
                                JSONObject stateObject = statesArray.getJSONObject(i);
                                if (TextUtils.equals(stateObject.getString("sname"), stateName)) {
                                    JSONArray cityArray = stateObject.getJSONArray("cities");
                                    for (int j = 0; j < cityArray.length(); ++j) {
                                        cityList.add(cityArray.get(j).toString());
                                    }
                                    /*JSONArray soilArray = stateObject.getJSONArray("soiltype");
                                    for(int j=0; j<soilArray.length(); ++j)
                                        soilTypes.add(soilArray.get(j).toString());*/
                                    progressDialog.dismiss();
                                }
                            }
                        //    Collections.sort(cityList);
                            cityAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CityActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });

        requestQueue.add(jsonObjectRequest);

        cityListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(final AdapterView<?> adapterView, View view, final int i, long l) {
                        progressDialog.show();
                        JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.GET, Values.STATES_ENG, null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            String cityNameEnglish;
                                            JSONArray statesArray = response.getJSONArray("states");
                                            for (int j = 0; j < statesArray.length(); ++j) {
                                                JSONObject stateObject = statesArray.getJSONObject(j);
                                                if (TextUtils.equals(stateObject.getString("sname"), stateNameEnglish)) {
                                                    JSONArray cityArray = stateObject.getJSONArray("cities");
                                                    cityNameEnglish = cityArray.getString(i);
                                                    Intent intent = new Intent(CityActivity.this, MapsActivity.class);
                                                    intent.putExtra("stateName", stateName);
                                                    intent.putExtra("cityName", adapterView.getItemAtPosition(i).toString());
                                                    intent.putExtra("stateNameEnglish", stateNameEnglish);
                                                    intent.putExtra("cityNameEnglish", cityNameEnglish);
                                                    startActivity(intent);
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            progressDialog.dismiss();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(CityActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                });
                        requestQueue.add(jsonObjectRequest1);
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
}