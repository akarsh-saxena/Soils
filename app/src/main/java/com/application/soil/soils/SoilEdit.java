package com.application.soil.soils;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class SoilEdit extends AppCompatActivity {

    String soilName, cityName;
    ArrayList<String> soilComposition;
    EditText etPotash, etPhos, etAlkali, etNitro, etIron, etLime;
    String potash, phos, alkali, nitro, iron, lime;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soil_edit);

        soilName = getIntent().getExtras().getString("soilName");
        cityName = getIntent().getExtras().getString("cityName");
        soilComposition = getIntent().getStringArrayListExtra("soilComposition");

        etPotash = findViewById(R.id.etPotash);
        etPhos = findViewById(R.id.etPhos);
        etAlkali = findViewById(R.id.etAlkali);
        etNitro = findViewById(R.id.etNitro);
        etIron = findViewById(R.id.etIron);
        etLime = findViewById(R.id.etLime);
        btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatabaseHelper databaseHelper = new DatabaseHelper(SoilEdit.this, cityName);
                        databaseHelper.getWritableDatabase();
                        databaseHelper.createTable(cityName);

                        if(TextUtils.isEmpty(etPotash.getText().toString()))
                            potash = soilComposition.get(0);
                        else
                            potash = etPotash.getText().toString()+"%";

                        if(TextUtils.isEmpty(etPhos.getText().toString()))
                            phos = soilComposition.get(1);
                        else
                            phos = etPhos.getText().toString()+"%";

                        if(TextUtils.isEmpty(etAlkali.getText().toString()))
                            alkali = soilComposition.get(2);
                        else
                            alkali = etAlkali.getText().toString()+"%";

                        if(TextUtils.isEmpty(etNitro.getText().toString()))
                            nitro = soilComposition.get(3);
                        else
                            nitro = etNitro.getText().toString()+"%";

                        if(TextUtils.isEmpty(etIron.getText().toString()))
                            iron = soilComposition.get(4);
                        else
                            iron = etIron.getText().toString()+"%";

                        if(TextUtils.isEmpty(etLime.getText().toString()))
                            lime = soilComposition.get(5);
                        else
                            lime = etLime.getText().toString()+"%";

                        if(databaseHelper.insertData(soilName, potash, phos, alkali, nitro, iron, lime)) {
                            Toast.makeText(SoilEdit.this, "Values edited successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else
                            Toast.makeText(SoilEdit.this, "Try Again", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}
