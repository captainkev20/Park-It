package com.rocks.kevinwalker.parkit.payments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.rocks.kevinwalker.parkit.utils.FirestoreHelper;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Customer;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;
import com.rocks.kevinwalker.parkit.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class NewPaymentFragment extends Fragment
        implements View.OnClickListener {

    private static final String TAG = NewPaymentFragment.class.getName();

    private View mView;

    private NewPaymentCallback newPaymentCallback;

    @BindView(R.id.card_input_widget) CardInputWidget card_input_widget;
    @BindView(R.id.btn_save_payment) Button btn_save_payment;
    @BindView(R.id.txt_view_cancel_add_payment) TextView txt_view_cancel_add_payment;

    private Card card;
    private Stripe stripe;
    private Customer customer;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NewPaymentFragment.NewPaymentCallback) {
            newPaymentCallback = (NewPaymentFragment.NewPaymentCallback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement NewPaymentFragmentInteractionListener");
        }
    }

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
        txt_view_cancel_add_payment.setOnClickListener(this);

        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        getActivity().setTitle(getResources().getString(R.string.payments_add_new_title));
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btn_save_payment:

                //TODO: Save customer ID for future charges
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
                                    if (getView()!= null) {
                                        Snackbar.make(getView(), getString(R.string.card_saved),
                                                Snackbar.LENGTH_LONG).show();
                                    }
                                    FirestoreHelper.getInstance().getStripeCustomer().setCardLastFourDigits(card.getLast4());
                                    FirestoreHelper.getInstance().getStripeCustomer().setCardBrand(card.getBrand());
                                    FirestoreHelper.getInstance().getStripeCustomer().setPaymentUserUUID(FirebaseAuth.getInstance().getUid());
                                    FirestoreHelper.getInstance().mergeStripeCustomerWithFirestore();
                                    newPaymentCallback.navigateToPaymentListings();
                                }
                            });
                }
                break;

            case R.id.txt_view_cancel_add_payment:
                newPaymentCallback.navigateToPaymentListings();
                break;
        }
    }

    public interface NewPaymentCallback {
        void navigateToPaymentListings();
    }
}
