package com.example.kevinwalker.parkit.payments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.kevinwalker.parkit.utils.FirestoreHelper;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;


import com.example.kevinwalker.parkit.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PaymentFragment extends Fragment
        implements View.OnClickListener {

    private static final String TAG = PaymentFragment.class.getName();

    private View mView;

    @BindView(R.id.card_input_widget) CardInputWidget card_input_widget;
    @BindView(R.id.btn_save_payment) Button btn_save_payment;

    private Card card;
    private Stripe stripe;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirestoreHelper.getInstance().initializeFirestoreStripeCustomer();
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_payment, container, false);
        ButterKnife.bind(this, mView);

        btn_save_payment.setOnClickListener(this);

        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        getActivity().setTitle(getResources().getString(R.string.payments_nav_title));
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btn_save_payment:

                card = card_input_widget.getCard();
                if (card == null) {
                    return;
                } else {
                    stripe = new Stripe(getActivity().getApplicationContext(), "pk_test_o9GQIYLn6xNzdKk1TOxz5Iga");
                    stripe.createToken(
                            card, new TokenCallback() {
                                @Override
                                public void onError(Exception error) {
                                    Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onSuccess(Token token) {
                                    // TODO: Send to back-end
                                    Toast.makeText(getActivity(), "Card Successfully Saved!", Toast.LENGTH_SHORT).show();

                                    FirestoreHelper.getInstance().getStripeCustomer().setToken(token.getId());
                                    FirestoreHelper.getInstance().mergeStripeCustomerWithFirestore();
                                }
                            });
                }
        }
    }
}
