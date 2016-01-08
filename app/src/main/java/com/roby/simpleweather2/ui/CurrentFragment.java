package com.roby.simpleweather2.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.roby.simpleweather2.R;
import com.roby.simpleweather2.model.Current;
import com.roby.simpleweather2.model.Forecast;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Roby on 1/5/2016.
 */
public class CurrentFragment extends Fragment implements UpdateableFragment {

    private Current mCurrent;

    @Bind(R.id.currentTime) TextView mTimeView;
    @Bind(R.id.currentTemp) TextView mTempView;
    @Bind(R.id.currentHumidity) TextView mHumidityView;
    @Bind(R.id.currentPrecipChance) TextView mPrecipView;
    @Bind(R.id.currentSummary) TextView mSummaryView;
    @Bind(R.id.currentTimezone) TextView mTimezoneView;


    public static CurrentFragment newInstance(Current current) {
        CurrentFragment currentFragment = new CurrentFragment();
        Bundle args = new Bundle();
        args.putParcelable("current", current);
        currentFragment.setArguments(args);
        return currentFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrent = getArguments().getParcelable("current");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_current_frag, container, false);
        ButterKnife.bind(this, view);
        updateUI();
        return view;
    }

    private void updateUI(){
        mTimeView.setText(mCurrent.getFormattedTime());
        mTempView.setText(mCurrent.getTemperature() + "");
        mHumidityView.setText(mCurrent.getHumidity() + "");
        mPrecipView.setText(mCurrent.getPrecipChance() + "");
        mSummaryView.setText(mCurrent.getSummary());
        mTimezoneView.setText(mCurrent.getTimezone());
    }

    @Override
    public void update(Forecast forecast) {
        mCurrent = forecast.getCurrent();
        updateUI();
    }
}
