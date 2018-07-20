package com.example.kevinwalker.parkit.spot;

import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kevinwalker.parkit.R;
import com.example.kevinwalker.parkit.profiles.ParentProfileActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SpotFragment extends ParentProfileActivity {

    private View mView;
    @BindView(R.id.txt_distance) TextView txt_distance;
    @BindView(R.id.txt_cost) TextView txt_cost;
    @BindView(R.id.txt_last_updated) TextView txt_last_updated;
    @BindView(R.id.txt_payment_type) TextView txt_payment_type;
    @BindView(R.id.txt_surface) TextView txt_surface;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_spot2, container, false);

        ButterKnife.bind(this, mView);

        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        getActivity().setTitle("Spot Listing");
    }
}
