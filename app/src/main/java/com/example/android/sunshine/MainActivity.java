/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sunshine.data.SunshinePreferences;
import com.example.android.sunshine.utilities.NetworkUtils;
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements ForecastAdapter.ForecastAdapterOnClickHandler, LoaderManager.LoaderCallbacks<String[]>{
    private RecyclerView mRecyclerView;
    private ForecastAdapter mForecastAdapter;
//    private TextView mWeatherTextView;
    private TextView errorMessage;
    private ProgressBar loadingIcon;
    private String location;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        mRecyclerView=(RecyclerView) findViewById(R.id.recyclerview_forecast);
//        mWeatherTextView = (TextView) findViewById(R.id.tv_weather_data);
        errorMessage=(TextView) findViewById(R.id.error_msg);
        loadingIcon=(ProgressBar) findViewById(R.id.progressBar);


/** Setting up the main Recycler View*/
        LinearLayoutManager layoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mForecastAdapter=new ForecastAdapter(this);
        mRecyclerView.setAdapter(mForecastAdapter);

//        loadWeatherData();
        getSupportLoaderManager().restartLoader(0,null,this);

    }

    public void openMapLocation(){
        String addressString = "21 Roslin Avenue West, Waterloo";
        Uri geoLocation = Uri.parse("geo:0,0?q=" + addressString);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }else {
            Log.d(TAG, "Couldn't call " + geoLocation.toString()
                    + ", no receiving apps installed!");
        }

    }

    public void showWeatherTextView() {

        mRecyclerView.setVisibility(View.VISIBLE);
        errorMessage.setVisibility(View.INVISIBLE);


    }

    public void showErrorMessage() {

        errorMessage.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);

    }


    public void loadWeatherData() {

        this.location= SunshinePreferences.getPreferredWeatherLocation(this);
        showWeatherTextView();
//        new perform_networkTasks().execute(this.location);
    }

    @Override
    public void clickHandler(String weatherForDay) {
        Context context=this;
        Intent intent=new Intent(MainActivity.this,DetailsActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT,weatherForDay);
        startActivity(intent);

    }

    /**All the perform_networking Aysnch Tasks have been replaces by Async Task Loader.
    This will help me to maintain a Android Life cycle, as well as Cache Data*/

    /** Instead of loadWeatherData() , we just restart the AsyncLoader which takes care of it.*/

    @Override
    public Loader<String[]> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<String[]>(this) {

            /* This String array will hold and help cache our weather data */
            String[] mWeatherData = null;

            /**
             * Subclasses of AsyncTaskLoader must implement this to take care of loading their data.
             */
            @Override
            protected void onStartLoading() {
                if (mWeatherData != null) {
                    deliverResult(mWeatherData);
                } else {
                    loadingIcon.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            /**
             * This is the method of the AsyncTaskLoader that will load and parse the JSON data
             * from OpenWeatherMap in the background.
             *
             * @return Weather data from OpenWeatherMap as an array of Strings.
             * null if an error occurs
             */
            @Override
            public String[] loadInBackground() {

                String location = SunshinePreferences.getPreferredWeatherLocation(MainActivity.this);
                String weatherResults;
                String[] finalResults = null;
                URL weatherRequestUrl = NetworkUtils.buildUrl(location);

                //try clause getting information from the API class and storing in Json form
                try {

                    weatherResults = NetworkUtils.getResponseFromHttpUrl(weatherRequestUrl);
                    finalResults = OpenWeatherJsonUtils.getSimpleWeatherStringsFromJson(MainActivity.this, weatherResults);

                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }  catch (JSONException e) {
                    e.printStackTrace();
                }


                return finalResults;
            }

            /**
             * Sends the result of the load to the registered listener.
             *
             * @param data The result of the load
             */
            public void deliverResult(String[] data) {
                mWeatherData = data; //catching the data
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {

        if(data!=null){
            showWeatherTextView();
            loadingIcon.setVisibility(View.INVISIBLE);
            mForecastAdapter.setWeatherData(data);

        }else{
            showErrorMessage();
        }

    }

    @Override
    public void onLoaderReset(Loader<String[]> loader) {

    }

    private void showNullDataOnRefresh(){ mForecastAdapter.setWeatherData(null); }


    /** Performs Networking tasks asynchronously*/

//    public class perform_networkTasks extends AsyncTask<String, Void, String[]> {
//
//
//
//        @Override
//        public void onPreExecute() {
//
//            loadingIcon.setVisibility(View.VISIBLE);
//
//
//        }
//
//
//        @Override
//        protected String[] doInBackground(String... params) {
//
//            if (params.length == 0) {
//                System.out.println("Test");
//               return null;
//
//            }
//
//            String location = params[0];
//            String weatherResults;
//            String[] finalResults = null;
//            URL weatherRequestUrl = NetworkUtils.buildUrl(location);
//
//            //try clause getting information from the API class and storing in Json form
//            try {
//
//                weatherResults = NetworkUtils.getResponseFromHttpUrl(weatherRequestUrl);
//                finalResults = OpenWeatherJsonUtils.getSimpleWeatherStringsFromJson(MainActivity.this, weatherResults);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//                return null;
//            }  catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//
//            return finalResults;
//        }
//
//
//        @Override
//        protected void onPostExecute(String[] results) {
//
//
//            if (results != null) {
//               showWeatherTextView();
//
////                for (String weatherData : results) {
////
////                    mWeatherTextView.append((weatherData) + "\n\n\n");
////
////                }
//
//                mForecastAdapter.setWeatherData(results);
//                loadingIcon.setVisibility(View.INVISIBLE);
//
//            }else{
//                showErrorMessage();
//            }
//
//
//
//        }
//
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.forecast, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int menuItemSelected=item.getItemId();
        if(menuItemSelected==R.id.action_refresh){
            System.out.print("YO");
//            mWeatherTextView.setText("");
//            mForecastAdapter.setWeatherData(null);
            showNullDataOnRefresh();
//            loadWeatherData();
            getSupportLoaderManager().restartLoader(0,null,this);
            return true;
        }

        if(menuItemSelected==R.id.action_share){
          openMapLocation();
          return true;
        }

        return super.onOptionsItemSelected(item);

    }



}


