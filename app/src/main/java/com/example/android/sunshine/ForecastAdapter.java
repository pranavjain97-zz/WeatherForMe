package com.example.android.sunshine;

import android.content.Context;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by pranav on 23/04/17.
 */


public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

    private String[] mWeatherData;
    private final ForecastAdapterOnClickHandler mClickHandler;

    public ForecastAdapter(ForecastAdapterOnClickHandler clickHandler){
     mClickHandler=clickHandler;

    }


    /** Recieves on-click messages*/

    public interface ForecastAdapterOnClickHandler{

        void clickHandler(String weatherForDay);

    }

    
    

    public class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView mWeatherTextView;



        public ForecastAdapterViewHolder(View view) {
            super(view);
            mWeatherTextView=(TextView) view.findViewById((R.id.tv_weather_data));
            view.setOnClickListener(this);

        }



        @Override
        public void onClick(View v) {
            int adapterPosition=getAdapterPosition();
            String weatherForDay=mWeatherData[adapterPosition];
            mClickHandler.clickHandler(weatherForDay);
        }
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     */

    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.forecast_list_item, viewGroup, false);
        return new ForecastAdapterViewHolder(view);


    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the weather
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     */

    @Override
    public void onBindViewHolder(ForecastAdapterViewHolder viewHolder, int position) {

        String weatherForPosition = mWeatherData[position];
        viewHolder.mWeatherTextView.setText(weatherForPosition);

    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     */

    @Override
    public int getItemCount() {

        if (mWeatherData == null) { return 0; }
        return mWeatherData.length;
    }

    /**
     * This is handy when we get new data from the web but don't want to create a
     * new ForecastAdapter to display it.
     */

    public void setWeatherData(String[] weatherData) {
        mWeatherData = weatherData;
        notifyDataSetChanged();
    }


 

}









