package com.application.soil.soils;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
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
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SoilDetails extends AppCompatActivity {

    RequestQueue requestQueue;

    String cityName, soilName;
    ArrayList<String> soilComposition = new ArrayList<>();
    TextView sName, sDescription, sComposition, edit;

    ImageView sImage;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soil_details);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.show();

        final String soilJSON = getResources().getString(R.string.soil_JSON);
        requestQueue = Volley.newRequestQueue(this);

        cityName = getIntent().getExtras().getString("cityName");
        soilName = getIntent().getExtras().getString("soilName");
        final String stateNameEnglish = getIntent().getExtras().getString("stateNameEnglish");
        final String cityNameEnglish = getIntent().getExtras().getString("cityNameEnglish");
        final String soilNameEnglish = getIntent().getExtras().getString("soilNameEnglish");

        sName = findViewById(R.id.sName);
        sDescription = findViewById(R.id.sDescription);
        sComposition = findViewById(R.id.sComposition);
        sImage = findViewById(R.id.sImage);
        edit = findViewById(R.id.edit);

        final DatabaseHelper databaseHelper = new DatabaseHelper(SoilDetails.this, cityName.trim());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, soilJSON, null,
            new Response.Listener<JSONObject>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(JSONObject response) {
                    List<SoilModel> soilModels;
                    try {
                        JSONArray soilsArray = response.getJSONArray("Soils");
                        for(int i=0; i<soilsArray.length(); ++i) {
                            JSONObject soilsObject = soilsArray.getJSONObject(i);
                            if(TextUtils.equals(soilsObject.get("soilName").toString(), soilName)) {
                                sName.setText(soilName);
                                Glide.with(SoilDetails.this).load(soilsObject.get("url")).into(sImage);
                                JSONArray charArray = soilsObject.getJSONArray("Characteristics");
                                for(int j=0; j<charArray.length(); ++j) {
                                    sDescription.append("-> "+charArray.getString(j)+"\n");
                                }
                                if(databaseHelper.isTableExist(cityNameEnglish)) {
                                    soilModels = databaseHelper.getSoilInformation(cityNameEnglish);
                                    for(int j=0; j<soilModels.size(); ++j)
                                        if(soilModels.get(j).getName().equals(soilNameEnglish)) {
                                            sComposition.setText(
                                                getResources().getString(R.string.alkali)+": "+soilModels.get(j).getAlkali()+"\n"+
                                                getResources().getString(R.string.potassium)+": "+soilModels.get(j).getPotash() +"\n"+
                                                getResources().getString(R.string.phosphorous)+": "+soilModels.get(j).getPhos()+"\n"+
                                                getResources().getString(R.string.nitrogen)+": "+soilModels.get(j).getNitro()+"\n"+
                                                getResources().getString(R.string.iron_oxide)+": "+soilModels.get(j).getIron()+"\n"+
                                                getResources().getString(R.string.lime)+": "+soilModels.get(j).getLime()+"\n");
                                            return;
                                        }
                                }
                                else {
                                    JSONObject contentsObject = soilsObject.getJSONObject("Contents");
                                    Iterator<String> keys = contentsObject.keys();
                                    while (keys.hasNext()) {
                                        String key = keys.next(); // First key in your json object
                                        String value = contentsObject.getString(key);
                                        sComposition.append(key + ": " + value + "\n");
                                        soilComposition.add(value);
                                    }
                                }
                            }
                        }
                        progressDialog.dismiss();
                    } catch (JSONException e) {
                        progressDialog.dismiss();
                        e.printStackTrace();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(SoilDetails.this, "Error!!", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                    progressDialog.dismiss();
                }
            });

        requestQueue.add(jsonObjectRequest);

        edit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(SoilDetails.this, SoilEdit.class);
                        intent.putExtra("cityName", cityNameEnglish);
                        intent.putExtra("soilName", soilNameEnglish);
                        if(databaseHelper.isTableExist(cityNameEnglish)) {
                            Toast.makeText(SoilDetails.this, "Adding from db", Toast.LENGTH_SHORT).show();
                            List<SoilModel> soilModels = databaseHelper.getSoilInformation(cityNameEnglish);
                            for(int i=0; i<soilModels.size(); ++i)
                                if(TextUtils.equals(soilModels.get(i).getName(), soilName)) {
                                    soilComposition.add(soilModels.get(i).getPotash());
                                    soilComposition.add(soilModels.get(i).getPhos());
                                    soilComposition.add(soilModels.get(i).getAlkali());
                                    soilComposition.add(soilModels.get(i).getNitro());
                                    soilComposition.add(soilModels.get(i).getIron());
                                    soilComposition.add(soilModels.get(i).getLime());
                                }
                        }
                        intent.putStringArrayListExtra("soilComposition", soilComposition);
                        startActivity(intent);
                    }
                }
        );
    }
}
