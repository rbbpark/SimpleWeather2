package com.roby.simpleweather2.ui;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.roby.simpleweather2.R;
import com.roby.simpleweather2.model.Forecast;
import com.roby.simpleweather2.model.Hour;

import java.util.Arrays;

/**
 * Created by Roby on 1/6/2016.
 */
public class HourFragment extends Fragment implements UpdateableFragment{

    private Hour[] mHours;
    private HourAdapter mAdapter;

    public static HourFragment newInstance(Hour[] hours){
        HourFragment hourFragment = new HourFragment();
        Bundle args = new Bundle();
        args.putParcelableArray("hours", hours);
        hourFragment.setArguments(args);
        return hourFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Parcelable[] parcelables = getArguments().getParcelableArray("hours");
        mHours =  Arrays.copyOf(parcelables, parcelables.length, Hour[].class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.activity_hour_frag, container, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        mAdapter = new HourAdapter(mHours);
        recyclerView.setAdapter(mAdapter);
        return recyclerView;
    }

    @Override
    public void update(Forecast forecast) {
        mHours = Arrays.copyOf(forecast.getHourlyForecast(), forecast.getHourlyForecast().length, Hour[].class);
    }

    //HourAdapter inner class
    private class HourAdapter extends RecyclerView.Adapter<HourAdapter.HourViewHolder> {

        private Hour[] mHours;

        public HourAdapter(Hour[] hours) {
            mHours = hours;
        }

        @Override
        public HourViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.hourly_list_item, parent, false);
            HourViewHolder holder = new HourViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(HourViewHolder holder, int position) {
            holder.bindHour(mHours[position]);
        }

        @Override
        public int getItemCount() {
            return mHours.length;
        }

        //HourViewHolder inner inner class
        public class HourViewHolder extends RecyclerView.ViewHolder {

            private TextView mTemperatureLabel;
            private TextView mTimeLabel;
            private TextView mSummaryLabel;

            public HourViewHolder(View itemView) {
                super(itemView);
                mTimeLabel = (TextView) itemView.findViewById(R.id.timeLabel);
                mSummaryLabel = (TextView) itemView.findViewById(R.id.summaryLabel);
                mTemperatureLabel = (TextView) itemView.findViewById(R.id.temperatureLabel);
            }

            public void bindHour(Hour hour) {
                mTimeLabel.setText(hour.getHourText());
                mSummaryLabel.setText(hour.getSummary());
                mTemperatureLabel.setText(hour.getTemperature() + "");
            }
        }
    }

}
