package com.example.kevinwalker.parkit.payments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;


import com.example.kevinwalker.parkit.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PaymentFragment extends Fragment {

    private View mView;
    @BindView(R.id.card_input_widget) CardInputWidget card_input_widget;
    private Card card;
    private Stripe stripe;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        card = new Card(
                "4242424242424242", //card number
                12, //expMonth
                2016,//expYear
                "123"//cvc
        );

        /*stripe.createToken(
                card, new TokenCallback() {
                    @Override
                    public void onError(Exception error) {
                        Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(Token token) {
                        Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                    }
                });*/

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_payment, container, false);
        ButterKnife.bind(this, mView);

        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        getActivity().setTitle(getResources().getString(R.string.payments_nav_title));
    }
}
