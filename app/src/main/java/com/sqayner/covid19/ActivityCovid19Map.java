package com.sqayner.covid19;

import android.annotation.SuppressLint;
import android.app.UiModeManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class ActivityCovid19Map extends AppCompatActivity implements OnMapReadyCallback {

    private UiModeManager uiModeManager;
    private List<GetSetCovid19> getSetCovid19s = new ArrayList<>();
    private GoogleMap mMap;
    private BottomSheetBehavior bottomSheetBehavior;
    private TextView tv_country, tv_confirmed, tv_deaths, tv_recovered, tv_active;
    private Bitmap red_map_marker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_covid19_map);

        uiModeManager = (UiModeManager) getSystemService(Context.UI_MODE_SERVICE);

        assert uiModeManager != null;
        if (uiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        } else {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.WHITE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() & View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }

        View bottomSheet = findViewById(R.id.bottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        tv_country = findViewById(R.id.tv_country);
        tv_confirmed = findViewById(R.id.tv_confirmed);
        tv_deaths = findViewById(R.id.tv_deaths);
        tv_recovered = findViewById(R.id.tv_recovered);
        tv_active = findViewById(R.id.tv_active);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        new covidData().execute();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {
            boolean succs = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.mapstyle));
            if (!succs) {

            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(39.1667d, 35.6667d)));
        mMap.setMinZoomPreference(1f);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public boolean onMarkerClick(Marker marker) {
                int id = Integer.parseInt(marker.getId().replaceAll("m", ""));

                int conf = Integer.parseInt(getSetCovid19s.get(id).getConfirmed()), reco = Integer.parseInt(getSetCovid19s.get(id).getRecovered()), deat = Integer.parseInt(getSetCovid19s.get(id).getDeaths());
                int acti = conf - (reco + deat);

                if (!getSetCovid19s.get(id).getCountry_code().equals("null")) {
                    tv_country.setText(getSetCovid19s.get(id).getCountry() + " , " + getSetCovid19s.get(id).getCountry_code());
                    tv_confirmed.setText("Onaylanan toplam vakalar : " + getSetCovid19s.get(id).getConfirmed());
                    tv_active.setText("Etkin vakalar : " + acti);
                    tv_deaths.setText("Ölümcül vakalar : " + getSetCovid19s.get(id).getDeaths());
                    tv_recovered.setText("Tedavi edilen vakalar : " + getSetCovid19s.get(id).getRecovered());
                } else {
                    tv_country.setText(getSetCovid19s.get(id).getCountry());
                    tv_confirmed.setText("Onaylanan toplam vakalar : " + getSetCovid19s.get(id).getConfirmed());
                    tv_active.setText("Etkin vakalar : " + acti);
                    tv_deaths.setText("Ölümcül vakalar : " + getSetCovid19s.get(id).getDeaths());
                    tv_recovered.setText("Tedavi edilen vakalar : " + getSetCovid19s.get(id).getRecovered());
                }

                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                return false;
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    class covidData extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpsURLConnection connection;
            BufferedReader br;
            try {
                URL url = new URL("https://covid19.mathdro.id/api/confirmed");
                connection = (HttpsURLConnection) url.openConnection();
                connection.connect();
                InputStream is = connection.getInputStream();
                br = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuilder file = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    file.append(line);
                }
                return file.toString();
            } catch (Exception e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONArray jsonarray = new JSONArray(s);

                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject covid = jsonarray.getJSONObject(i);

                    try {
                        GetSetCovid19 getSetCovid19 = new GetSetCovid19(
                                covid.getDouble("lat") + ""
                                , covid.getDouble("long") + ""
                                , covid.getString("countryRegion")
                                , covid.getString("provinceState")
                                , covid.getInt("confirmed") + ""
                                , covid.getInt("deaths") + ""
                                , covid.getInt("recovered") + ""
                                , covid.getString("provinceState"));

                        getSetCovid19s.add(getSetCovid19);

                        int redmarkerwah = 0;
                        if (Integer.parseInt(getSetCovid19.getConfirmed()) < 100) {
                            break;
                        } else {
                            redmarkerwah = Integer.parseInt(getSetCovid19.getConfirmed()) / 600;
                        }
                        red_map_marker = Bitmap.createScaledBitmap(((BitmapDrawable) getResources().getDrawable(R.drawable.covidmapmarker)).getBitmap(), redmarkerwah, redmarkerwah, false);

                        LatLng sydney = new LatLng(covid.getDouble("lat"), covid.getDouble("long"));
                        MarkerOptions dnm = new MarkerOptions()
                                .position(sydney)
                                .anchor(0.5f, 0.5f)
                                .icon(BitmapDescriptorFactory.fromBitmap(red_map_marker))
                                .alpha(0.47f);
                        mMap.addMarker(dnm);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
