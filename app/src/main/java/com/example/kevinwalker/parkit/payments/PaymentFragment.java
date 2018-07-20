package com.example.kevinwalker.parkit.payments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kevinwalker.parkit.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PaymentFragment extends Fragment {

    private View mView;
    @BindView(R.id.image_card_logo) ImageView image_card_logo;
    @BindView(R.id.txt_card_ending_in) TextView txt_card_ending_in;
    @BindView(R.id.btn_add_payment) FloatingActionButton btn_add_payment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_payment, container, false);
        ButterKnife.bind(this, mView);

        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        getActivity().setTitle("Payments");
    }
}
