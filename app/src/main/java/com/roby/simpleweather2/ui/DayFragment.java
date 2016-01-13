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
import com.roby.simpleweather2.model.Day;
import com.roby.simpleweather2.model.Forecast;

import java.util.Arrays;

import butterknife.ButterKnife;

/**
 * Created by Roby on 1/6/2016.
 */
public class DayFragment extends Fragment implements UpdateableFragment {

    private Day[] mDays;
    private DayAdapter mAdapter;

    public static DayFragment newInstance(Day[] days) {
        DayFragment dayFragment = new DayFragment();
        Bundle args = new Bundle();
        args.putParcelableArray("days", days);
        dayFragment.setArguments(args);
        return dayFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Parcelable[] parcelables = getArguments().getParcelableArray("days");
        mDays =  Arrays.copyOf(parcelables, parcelables.length, Day[].class);//seems legit??
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.activity_day_frag, container, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        mAdapter = new DayAdapter(mDays);
        recyclerView.setAdapter(mAdapter);
        return recyclerView;
    }

    public void update(Forecast forecast){
        mDays = Arrays.copyOf(forecast.getDailyForecast(), forecast.getDailyForecast().length, Day[].class);
        mAdapter.notifyDataSetChanged();
    }

    //DayAdapter inner class
    private class DayAdapter extends RecyclerView.Adapter<DayAdapter.DayViewHolder> {

        private Day[] mDays;

        public DayAdapter(Day[] days) {
            mDays = days;
        }

        @Override
        public DayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.daily_list_item, parent, false);
            DayViewHolder holder = new DayViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(DayViewHolder holder, int position) {
            holder.bindDay(mDays[position]);
        }

        @Override
        public int getItemCount() {
            return mDays.length;
        }

        //DayViewHolder inner inner class
        public class DayViewHolder extends RecyclerView.ViewHolder {

            private TextView mTemperatureLabel;
            private TextView mTimeLabel;
            private TextView mSummaryLabel;

            public DayViewHolder(View itemView) {
                super(itemView);
                mTimeLabel = (TextView) itemView.findViewById(R.id.timeLabel);
                mSummaryLabel = (TextView) itemView.findViewById(R.id.summaryLabel);
                mTemperatureLabel = (TextView) itemView.findViewById(R.id.temperatureLabel);
            }

            public void bindDay(Day day) {
                mTimeLabel.setText(day.getDayOfWeek());
                mSummaryLabel.setText(day.getSummary());
                mTemperatureLabel.setText(day.getTemperatureMax() + "");
            }
        }
    }
}
