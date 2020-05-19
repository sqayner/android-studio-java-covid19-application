package com.sqayner.covid19;

import android.annotation.SuppressLint;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

public class ActivityCovid19Menu extends AppCompatActivity {

    private UiModeManager uiModeManager;
    private TextView tv_confirmed, tv_deaths, tv_recovered, tv_active;
    private LinearLayout linearLayout_allıtem;
    private TextView tv_apiowner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_covid19_menu);

        tv_confirmed = findViewById(R.id.menu_tv_confirmed);
        tv_recovered = findViewById(R.id.menu_tv_recovered);
        tv_deaths = findViewById(R.id.menu_tv_deaths);
        tv_active = findViewById(R.id.menu_tv_active);
        tv_apiowner = findViewById(R.id.tv_apiowner);

        linearLayout_allıtem = findViewById(R.id.ll_allItem);

        uiModeManager = (UiModeManager) getSystemService(Context.UI_MODE_SERVICE);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0.0f);

        assert uiModeManager != null;
        if (uiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
            Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(Color.BLACK));
            getSupportActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>Covid-19</font>"));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        } else {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.WHITE);
            Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            getSupportActionBar().setTitle(Html.fromHtml("<font color='#000000'>Covid-19</font>"));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() & View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }

        tv_apiowner.setText("API OWNER : MATHDROID", TextView.BufferType.SPANNABLE);
        Spannable span = (Spannable) tv_apiowner.getText();
        span.setSpan(new ForegroundColorSpan(0xFF0075FF), 11, 21, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        tv_apiowner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/mathdroid/covid-19-api"));
                startActivity(myIntent);
            }
        });

        new getData().execute();
    }

    public void goMap(View view) {
        startActivity(new Intent(ActivityCovid19Menu.this, ActivityCovid19Map.class));
    }

    @SuppressLint("StaticFieldLeak")
    class getData extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            HttpsURLConnection connection;
            BufferedReader br;
            try {
                URL url = new URL("https://covid19.mathdro.id/api");
                connection = (HttpsURLConnection) url.openConnection();
                connection.connect();
                InputStream is = connection.getInputStream();
                br = new BufferedReader(new InputStreamReader(is));
                String line;
                String dosya = "";
                while ((line = br.readLine()) != null) {
                    dosya += line;
                }
                return dosya;
            } catch (Exception e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject covid = new JSONObject(s);
                JSONObject confirmed = covid.getJSONObject("confirmed");
                JSONObject recovered = covid.getJSONObject("recovered");
                JSONObject deaths = covid.getJSONObject("deaths");
                int iConfirmed = confirmed.getInt("value");
                int iRecovered = recovered.getInt("value");
                int iDeaths = deaths.getInt("value");
                int active = iConfirmed - (iRecovered + iDeaths);

                tv_confirmed.setText(String.valueOf(iConfirmed));
                tv_recovered.setText(String.valueOf(iRecovered));
                tv_deaths.setText(String.valueOf(iDeaths));
                tv_active.setText(String.valueOf(active));

                linearLayout_allıtem.setVisibility(View.VISIBLE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
