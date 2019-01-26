package com.rocks.kevinwalker.parkit.spot;

import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rocks.kevinwalker.parkit.R;
import com.rocks.kevinwalker.parkit.profiles.ParentProfileFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SpotFragment extends ParentProfileFragment {

    private View mView;
    @BindView(R.id.txt_distance) TextView txt_distance;
    @BindView(R.id.txt_cost) TextView txt_cost;
    @BindView(R.id.txt_payment_type) TextView txt_payment_type;
    Spot spot = new Spot();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_spot_list_layout, container, false);
        ButterKnife.bind(this, mView);

        updateUI();

        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle(getResources().getString(R.string.listings_nav_title));
    }


    private void updateUI() {
        txt_cost.setText(String.valueOf(spot.getDailyRate()));
        txt_distance.setText(String.valueOf(spot.getSpotDistance()));
        txt_payment_type.setText(String.valueOf(spot.getPaymentType()));
    }
}
