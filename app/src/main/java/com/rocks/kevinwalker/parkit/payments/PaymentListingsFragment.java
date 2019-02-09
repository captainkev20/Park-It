package com.rocks.kevinwalker.parkit.payments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.rocks.kevinwalker.parkit.NavDrawer;
import com.rocks.kevinwalker.parkit.R;
import com.rocks.kevinwalker.parkit.utils.FirestoreHelper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PaymentListingsFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";

    private View mView;

    @BindView(R.id.payment_recycler_view) RecyclerView paymentRecyclerView;

    private int mColumnCount = 1;
    private PaymentListingsFragment.PaymentListingsInteraction mListener;
    private ArrayList<Payment> payment = new ArrayList<>();
    private PaymentListingsAdapter paymentListingsAdapter;


    public PaymentListingsFragment() {}

    @SuppressWarnings("unused")
    public static PaymentListingsFragment newInstance(int columnCount) {
        PaymentListingsFragment fragment = new PaymentListingsFragment();
        ArrayList<Payment> payment = new ArrayList<>();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PaymentListingsFragment.PaymentListingsInteraction) {
            mListener = (PaymentListingsFragment.PaymentListingsInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement PaymentListingsInteraction");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        if (payment.isEmpty()) {
            payment.add(FirestoreHelper.getInstance().getUserPayment());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_payment_list, container, false);

        ButterKnife.bind(this, mView);

        FirestoreHelper.getInstance().getAllUserPayments();

        // Set the adapter
        if (mView instanceof RecyclerView) {
            Context context = mView.getContext();
            paymentRecyclerView = (RecyclerView) mView;
            if (mColumnCount <= 1) {
                paymentRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                paymentRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
        }

        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle(getResources().getString(R.string.payments_nav_title));
    }

    @Override
    public void onResume(){
        super.onResume();

        // TODO: Review with Hollis and determine if best way to handle this
        NavDrawer navDrawer = new NavDrawer();
        if (navDrawer.getCurrentFragmentTag().equals("paymentListingFragmentTag")) {
            mListener.setPaymentFabVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        mListener.setPaymentFabVisibility(View.GONE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void resetRecyclerView() {
        paymentListingsAdapter = new PaymentListingsAdapter(FirestoreHelper.getInstance().getAllUserPayments(), mListener);
        paymentRecyclerView.setAdapter(paymentListingsAdapter);
    }

    public void resetRecyclerView(ArrayList<Payment> payments) {
        paymentListingsAdapter = new PaymentListingsAdapter(payments, mListener);
        paymentRecyclerView.setAdapter(paymentListingsAdapter);
    }

    public interface PaymentListingsInteraction {
        void onPaymentListingInteraction(Payment item);
        void setPaymentFabVisibility(int viewVisibilityConstant);
    }
}
