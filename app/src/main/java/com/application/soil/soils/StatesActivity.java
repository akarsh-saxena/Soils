package com.application.soil.soils;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import java.util.List;

public class StatesActivity extends AppCompatActivity {

    ListView stateListView;
    RequestQueue requestQueue;
    List<String> stateList;
    ArrayAdapter<String> stateAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_states);

        final String JSONData = StatesActivity.this.getResources().getString(R.string.states_JSON);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading");
        progressDialog.show();

        requestQueue = Volley.newRequestQueue(this);
        stateListView = findViewById(R.id.stateListView);
        stateList = new ArrayList<>();

        stateAdapter = new ArrayAdapter<>(this, R.layout.listview_item, stateList);

        stateListView.setAdapter(stateAdapter);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, JSONData, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray statesArray = response.getJSONArray("states");
                            for (int i = 0; i < statesArray.length(); ++i) {
                                JSONObject statesObject = statesArray.getJSONObject(i);
                                stateList.add(statesObject.get("sname").toString());
                                //Collections.sort(stateList);
                                //Log.d("state",statesObject.get("sname").toString());
                                stateAdapter.notifyDataSetChanged();
                                progressDialog.dismiss();
                            }
                            stateAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(StatesActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });

        requestQueue.add(jsonObjectRequest);

        stateListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(final AdapterView<?> adapterView, View view, final int i, long l) {
                        progressDialog.show();
                        JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.GET, Values.STATES_ENG, null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            String state;
                                            JSONArray statesArray = response.getJSONArray("states");
                                            state = statesArray.getJSONObject(i).getString("sname");
                                            Intent intent = new Intent(StatesActivity.this, CityActivity.class);
                                            intent.putExtra("stateName", adapterView.getItemAtPosition(i).toString());
                                            intent.putExtra("stateNameEnglish", state);
                                            startActivity(intent);
                                            progressDialog.dismiss();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(StatesActivity.this, "Error!!", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                });
                        requestQueue.add(jsonObjectRequest1);
                    }
                }
        );

    }
}
