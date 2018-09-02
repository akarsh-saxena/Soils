package com.application.soil.soils;

import android.Manifest;
import android.app.ProgressDialog;
import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.Preference;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    LocationListener locationListener;
    LocationManager locationManager;
    Button btnFeed, btnAccess;
    //RadioGroup radioGroup;
    AlertDialog alertDialog;
    AlertDialog.Builder builder;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();

        sharedPref.registerOnSharedPreferenceChangeListener(this);

        String langPref = sharedPref.getString("langPref", null);

        if(langPref == null) {
            editor.putString("langPref", "hi_IN");
            editor.apply();
        }
        /*else {
            Locale locale;
            switch(langPref) {
                case "hi_IN":
                    Toast.makeText(this, "Hindi", Toast.LENGTH_SHORT).show();
                    locale = new Locale("hi", "IN");
                    Toast.makeText(this, "Lang: "+locale.getLanguage()+" "+locale.getDisplayLanguage(), Toast.LENGTH_LONG).show();
                    break;
                case "en_IN":
                    locale = new Locale("en", "IN");
                    Toast.makeText(this, "English", Toast.LENGTH_SHORT).show();
                    break;
                case "pa_IN":
                    locale = new Locale("pa", "IN");
                    Toast.makeText(this, "Lang: "+locale.getLanguage()+" "+locale.getDisplayLanguage(), Toast.LENGTH_LONG).show();
                    Toast.makeText(this, "Punjabi", Toast.LENGTH_SHORT).show();
                    break;
                case "bn_IN":
                    locale = new Locale("bn", "IN");
                    Toast.makeText(this, "Bangali", Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "Lang: "+locale.getLanguage()+" "+locale.getDisplayLanguage(), Toast.LENGTH_LONG).show();
                    break;
                default:
                    locale = new Locale("hi", "IN");
            }
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        }*/
        CharSequence[] languages = {"हिंदी", "English", "ਪੰਜਾਬੀ", "বাংলা"};

        builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.select_language));
        builder.setSingleChoiceItems(languages, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String pref;
                switch(i)
                {
                    case 0:
                        pref="hi_IN";
                        break;
                    case 1:
                        pref="en_IN";
                        break;
                    case 2:
                        pref="pa_IN";
                        break;
                    case 3:
                        pref="bn_IN";
                        break;
                    default:
                        pref="hi_IN";
                        break;
                }
                editor.putString("langPref", pref);
                editor.commit();
                alertDialog.dismiss();
            }
        });

        btnFeed = findViewById(R.id.btnFeed);
        btnAccess = findViewById(R.id.btnAccess);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        btnFeed.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        progressDialog.setMessage("Loading");
                        progressDialog.show();

                        locationListener = new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                Geocoder geocoder = new Geocoder(MainActivity.this);
                                try {
                                    List<Address> add = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                                    intent.putExtra("cityNameEnglish", add.get(0).getSubAdminArea());
                                    intent.putExtra("stateNameEnglish", add.get(0).getAdminArea());
                                    intent.putExtra("cityName", add.get(0).getSubAdminArea());
                                    intent.putExtra("stateName", add.get(0).getAdminArea());
                                    startActivity(intent);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onStatusChanged(String s, int i, Bundle bundle) {

                            }

                            @Override
                            public void onProviderEnabled(String s) {

                            }

                            @Override
                            public void onProviderDisabled(String s) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                            }
                        };
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            Toast.makeText(MainActivity.this, "Not granted", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 1000, locationListener);
                    }
                });

        btnAccess.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, StatesActivity.class);
                        startActivity(intent);
                    }
                }
        );
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_lang, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_language) {
            alertDialog = builder.create();
            alertDialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(progressDialog!=null)
            if(progressDialog.isShowing())
                progressDialog.dismiss();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals("langPref")) {
            String[] langPref = sharedPreferences.getString(s, null).split("_");
            Locale locale = new Locale(langPref[0], langPref[1]);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
            recreate();
        }
    }
}

