package com.blueangles.weatherapp.Activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.blueangles.weatherapp.App;
import com.blueangles.weatherapp.Helper.GoogleApiHelper;
import com.blueangles.weatherapp.R;
import com.blueangles.weatherapp.model.DailyWeatherReport;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    final static String URL_BASE = "http://api.openweathermap.org/data/2.5/forecast";
    final static String URL_LAT = "?lat=";
    final static String URL_LONG = "&lon=";
    final static String URL_UNITS = "&units=imperial";
    final static String URL_API_KEY = "&APPID=e5670ccc9a2c623a696144b76d8f8770";
    private static final int LOCATION_REQUEST_CODE = 1234;
    private ArrayList<DailyWeatherReport> weatherReports = new ArrayList<>();

    private ImageView weatherIconMini, weatherIcon;
    public TextView weatherDate, currentTemp, lowTemp, city, country, type;

    private WeatherAdapter weatherAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherIcon = (ImageView) findViewById(R.id.imageViewMain);
        weatherIconMini = (ImageView) findViewById(R.id.imageView);
        weatherDate = (TextView) findViewById(R.id.date);
        currentTemp = (TextView) findViewById(R.id.templarge);
        lowTemp = (TextView) findViewById(R.id.tempsmall);
        city = (TextView) findViewById(R.id.citycountry);
        type = (TextView) findViewById(R.id.type);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.content_weather_reports);
        weatherAdapter = new WeatherAdapter(weatherReports);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(weatherAdapter);

        App.getGoogleApiHelper().setConnectionListener(new GoogleApiHelper.ConnectionListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

            }

            @Override
            public void onConnectionSuspended(int i) {

            }

            @Override
            public void onConnected(Bundle bundle) {
                startLocationServices();
            }
        });
    }

    private void startLocationServices() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MainActivity.this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);
        } else {
            try {
                LocationRequest locationRequest = LocationRequest
                        .create()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setInterval(100);
                LocationServices.FusedLocationApi.requestLocationUpdates(App.getGoogleApiHelper().getGoogleApiClient(),
                        locationRequest, new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                Log.e("tag", "Latitude " + location.getLatitude()
                                        + " Longitude " + location.getLongitude());
                                downloadWeatherData(location.getLatitude(), location.getLongitude());
                            }
                        });
            } catch (SecurityException e) {
                //exception
                Log.e("tag", e.getMessage());
            }
        }
    }

    private void downloadWeatherData(final double lat, double lon) {
        final String url = URL_BASE + URL_LAT + lat + URL_LONG + lon + URL_UNITS + URL_API_KEY;
        Log.e("tag", "url " + url);

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("tag", response.toString());
                        DailyWeatherReport dailyWeatherReport = new DailyWeatherReport();
                        try {
                            JSONObject city = response.getJSONObject("city");

                            String cityName = city.getString("name");
                            String country = city.getString("country");

                            JSONArray list = response.getJSONArray("list");

                            for (int i = 0; i < 5; i++) {

                                dailyWeatherReport.setCityName(cityName);
                                dailyWeatherReport.setCountry(country);

                                JSONObject obj = list.getJSONObject(i);
                                JSONObject jsonObjectMain = obj.optJSONObject("main");

                                Double currentTemp = jsonObjectMain.getDouble("temp");
                                Double maxTemp = jsonObjectMain.getDouble("temp_max");
                                Double minTemp = jsonObjectMain.getDouble("temp_min");

                                dailyWeatherReport.setCurrentTemp(currentTemp.intValue());
                                dailyWeatherReport.setMaaxTemp(maxTemp.intValue());
                                dailyWeatherReport.setMinTemp(minTemp.intValue());

                                JSONArray jsonArrayWeather = obj.getJSONArray("weather");
                                JSONObject weather = jsonArrayWeather.getJSONObject(0);
                                String weatherType = weather.getString("main");

                                dailyWeatherReport.setWeather(weatherType);
                                Log.e("tag  here", weatherType);

                                String rawDate = obj.getString("dt_txt");

                                dailyWeatherReport.setFormattedDate(rawDate);

                                weatherReports.add(dailyWeatherReport);
                            }

                        } catch (JSONException error) {
                            Log.e("tag", error.getMessage());
                        }
                        updateUI();
                        weatherAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("tag", error.getMessage());
                    }
                });
        Volley.newRequestQueue(MainActivity.this).add(jsonObjectRequest);
    }

    private void updateUI() {
        if (weatherReports.size() > 0) {
            DailyWeatherReport report = weatherReports.get(0);

            switch (report.getWeather()) {
                case DailyWeatherReport.WEATHER_TYPE_CLOUDS:
                    weatherIcon.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.cloudy));
                    weatherIconMini.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.partially_cloudy_mini));
                    break;
                case DailyWeatherReport.WEATHER_TYPE_RAIN:
                    weatherIcon.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.rainy));
                    weatherIconMini.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.rainy_mini));
                    break;
                case DailyWeatherReport.WEATHER_TYPE_SNOW:
                    weatherIcon.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.snow));
                    weatherIconMini.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.snow_mini));
                    break;
                default:
                    weatherIcon.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.sunny));
                    weatherIconMini.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.sunny_mini));
                    break;
            }

            weatherDate.setText(report.getFormattedDate());

            Log.e("tag", "" + report.getCurrentTemp());
            Log.e("tag", "min " + report.getMinTemp());
            Log.e("tag", report.getCountry() + ", " + report.getCountry());
            Log.e("tag", report.getWeather());
            currentTemp.setText("" + report.getCurrentTemp());
            lowTemp.setText("" + report.getMinTemp());
            city.setText(report.getCountry() + ", " + report.getCountry());
            type.setText("" + report.getWeather());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationServices();
                } else {
                    //show dialog
                }
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        App.getGoogleApiHelper().disconnect();
    }

    private class WeatherAdapter extends RecyclerView.Adapter<WeatherReportViewHolder> {

        ArrayList<DailyWeatherReport> mDailyWeatherReports = new ArrayList<>();

        WeatherAdapter(ArrayList<DailyWeatherReport> mDailyWeatherReports) {
            this.mDailyWeatherReports = mDailyWeatherReports;
        }

        @Override
        public WeatherReportViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new WeatherReportViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_card, parent, false));
        }

        @Override
        public void onBindViewHolder(WeatherReportViewHolder holder, int position) {

            holder.updateUI(mDailyWeatherReports.get(position));

        }

        @Override
        public int getItemCount() {
            return mDailyWeatherReports.size();
        }
    }

    class WeatherReportViewHolder extends RecyclerView.ViewHolder {

        private ImageView weatherIcon;
        private TextView weatherDate, weatherDescrip, tempHigh, tempLow;

        WeatherReportViewHolder(View itemView) {
            super(itemView);
            weatherIcon = (ImageView) itemView.findViewById(R.id.weatherIcon);
            weatherDate = (TextView) itemView.findViewById(R.id.weather_day);
            weatherDescrip = (TextView) itemView.findViewById(R.id.weather_description);
            tempHigh = (TextView) itemView.findViewById(R.id.weather_temp_high);
            tempLow = (TextView) itemView.findViewById(R.id.weather_temp_low);
        }

        void updateUI(DailyWeatherReport dailyWeatherReport) {

            weatherDescrip.setText("" + dailyWeatherReport.getWeather());
            weatherDate.setText("" + dailyWeatherReport.getFormattedDate());
            tempHigh.setText("" + dailyWeatherReport.getCurrentTemp());
            tempLow.setText("" + dailyWeatherReport.getMinTemp());

            switch (dailyWeatherReport.getWeather()) {
                case DailyWeatherReport.WEATHER_TYPE_CLOUDS:
                    weatherIcon.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.cloudy));
                    // this.weatherIconMini.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.partially_cloudy_mini));
                    break;
                case DailyWeatherReport.WEATHER_TYPE_RAIN:
                    weatherIcon.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.rainy));
                    // weatherIconMini.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.rainy_mini));
                    break;
                case DailyWeatherReport.WEATHER_TYPE_SNOW:
                    weatherIcon.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.snow));
                    //  weatherIconMini.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.snow_mini));
                    break;
                default:
                    weatherIcon.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.sunny));
                    //weatherIconMini.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.sunny_mini));
                    break;
            }
        }
    }

}
