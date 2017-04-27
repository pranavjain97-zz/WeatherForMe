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
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class MainActivity extends AppCompatActivity implements ForecastAdapter.ForecastAdapterOnClickHandler {

    private RecyclerView mRecyclerView;
    private ForecastAdapter mForecastAdapter;
//    private TextView mWeatherTextView;
    private TextView errorMessage;
    private ProgressBar loadingIcon;

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

        loadWeatherData();

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

        String location= SunshinePreferences.getPreferredWeatherLocation(this);
        showWeatherTextView();
        new perform_networkTasks().execute(location);

    }

    @Override
    public void clickHandler(String weatherForDay) {
        Context context=this;
        Toast.makeText(context,weatherForDay,Toast.LENGTH_SHORT)
             .show();

    }

    /** Performs Networking tasks asynchronously*/

    public class perform_networkTasks extends AsyncTask<String, Void, String[]> {



        @Override
        public void onPreExecute() {

            loadingIcon.setVisibility(View.VISIBLE);


        }


        @Override
        protected String[] doInBackground(String... params) {

            if (params.length == 0) {
                System.out.println("Test");
               return null;

            }

            String location = params[0];
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


        @Override
        protected void onPostExecute(String[] results) {


            if (results != null) {
               showWeatherTextView();

//                for (String weatherData : results) {
//
//                    mWeatherTextView.append((weatherData) + "\n\n\n");
//
//                }

                mForecastAdapter.setWeatherData(results);
                loadingIcon.setVisibility(View.INVISIBLE);

            }else{
                showErrorMessage();
            }



        }

    }

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
            mForecastAdapter.setWeatherData(null);
            loadWeatherData();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }



}


