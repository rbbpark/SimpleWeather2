package com.roby.simpleweather2.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.roby.simpleweather2.Constants;
import com.roby.simpleweather2.FetchAddressIntentService;
import com.roby.simpleweather2.R;
import com.roby.simpleweather2.model.Current;
import com.roby.simpleweather2.model.Day;
import com.roby.simpleweather2.model.Forecast;
import com.roby.simpleweather2.model.Hour;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient;
    private Forecast mForecast;

    private Location mLastLocation;
    private AddressResultReceiver mResultReceiver;

    private double latitude;
    private double longitude;
    private Current mCurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mResultReceiver = new AddressResultReceiver(new Handler());

    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private void getForecast(double latitude, double longitude) {
        String apiKey = "df60bd0939751969f194c8c9bf22606f";
        String forecastURL = "https://api.forecast.io/forecast/" + apiKey +
                "/" + latitude + "," + longitude;

        if (isNetworkAvailable()) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(forecastURL)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    //alertUserAboutError();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            mForecast = parseForecastDetails(jsonData);
                            mCurrent = mForecast.getCurrent();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateUI();
                                }
                            });
                        } else {
                            //alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "IO Exception caught ", e);
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON Exception caught ", e);
                    }
                }
            });
            if (mGoogleApiClient.isConnected() && mLastLocation != null) {
                //startIntentService();
                Log.e(TAG, "Started intent service");
            }
            Log.d(TAG, "Main UI code running here");

        } else {
            Toast.makeText(this, R.string.network_unavail_message, Toast.LENGTH_LONG).show();
        }
    }

    private void updateUI() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(CurrentFragment.newInstance(mCurrent));
        viewPager.setAdapter(adapter);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    private Forecast parseForecastDetails(String jsonData) throws JSONException {
        Forecast forecast = new Forecast();

        forecast.setCurrent(getCurrentDetails(jsonData));
        forecast.setHourlyForecast(getHourlyForecast(jsonData));
        forecast.setDailyForecast(getDailyForecast(jsonData));

        return forecast;
    }

    //how does this work?? idk

    private Day[] getDailyForecast(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        JSONObject daily = forecast.getJSONObject("daily");
        JSONArray data = daily.getJSONArray("data");

        Day[] dayArray = new Day[data.length()];
        for (int i = 0; i < dayArray.length; i++) {
            JSONObject day = data.getJSONObject(i);
            Day dayObj = new Day();
            dayObj.setTime(day.getLong("time"));
            dayObj.setIcon(day.getString("icon"));
            dayObj.setSummary(day.getString("summary"));
            dayObj.setTemperatureMax(day.getDouble("temperatureMax"));
            dayObj.setTimezone(forecast.getString("timezone"));
            dayArray[i] = dayObj;
        }

        return dayArray;
    }

    private Hour[] getHourlyForecast(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        JSONObject hourly = forecast.getJSONObject("hourly");
        JSONArray data = hourly.getJSONArray("data");

        Hour[] hourArray = new Hour[data.length()];
        for (int i = 0; i < hourArray.length; i++) {
            JSONObject hour = data.getJSONObject(i);
            Hour hourObj = new Hour();
            hourObj.setTime(hour.getLong("time"));
            hourObj.setTemperature(hour.getDouble("temperature"));
            hourObj.setIcon(hour.getString("icon"));
            hourObj.setSummary(hour.getString("summary"));
            hourObj.setTimezone(forecast.getString("timezone"));
            hourArray[i] = hourObj;
        }
        return hourArray;
    }

    private Current getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");

        Log.i(TAG, "From JSON: " + timezone);

        JSONObject currently = forecast.getJSONObject("currently");

        mCurrent = new Current();
        mCurrent.setHumidity(currently.getDouble("humidity"));
        mCurrent.setPrecipChance(currently.getDouble("precipProbability"));
        mCurrent.setIcon(currently.getString("icon"));
        mCurrent.setSummary(currently.getString("summary"));
        mCurrent.setTemperature(currently.getDouble("temperature"));
        mCurrent.setTime(currently.getLong("time"));
        mCurrent.setTimezone(timezone);

        Log.d(TAG, mCurrent.getFormattedTime());

        return mCurrent;
    }


    protected void startIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        startService(intent);
    }

    //Implemented methods
    @Override
    public void onConnected(Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }

        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
            //When it connects, update weather information
            //this runs when the app starts and GoogleApi builds/connects
            getForecast(latitude, longitude);

            if (!Geocoder.isPresent()) {
                Toast.makeText(this, "No GeoCoder available",
                        Toast.LENGTH_LONG).show();
                return;
            }
            startIntentService();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    //Fragment ViewPager Adapter inner class
    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    }

    //ResultReceiver inner class
    class AddressResultReceiver extends ResultReceiver {

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            // Display the address string
            // or an error message sent from the intent service.
            //mForecast.setLocation(resultData.getString(Constants.RESULT_DATA_KEY));
        }
    }
}
