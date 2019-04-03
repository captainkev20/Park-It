package com.rocks.kevinwalker.parkit.payments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.rocks.kevinwalker.parkit.R;
import java.util.List;

public class PaymentListingsAdapter extends RecyclerView.Adapter<PaymentListingsAdapter.ViewHolder> {

    private final List<Payment> mValues;
    private final PaymentListingsFragment.PaymentListingsInteraction mListener;

    public PaymentListingsAdapter(List<Payment> items, PaymentListingsFragment.PaymentListingsInteraction listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_payment_list_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        //TODO: Modify to speed up
        holder.mItem = mValues.get(position);
        holder.txt_card_brand_placeholder.setText(mValues.get(position).getCardBrand());
        holder.txt_ending_in_placeholder.setText(String.valueOf(mValues.get(position).getCardLastFourDigits()));
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onPaymentListingInteraction(holder.mItem);

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView txt_ending_in_label;
        public final TextView txt_ending_in_placeholder;
        public final TextView txt_card_brand_placeholder;
        public final TextView txt_card_brand_label;
        public Payment mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            txt_ending_in_label = view.findViewById(R.id.txt_ending_in_label);
            txt_ending_in_placeholder = view.findViewById(R.id.txt_ending_in_placeholder);
            txt_card_brand_placeholder = view.findViewById(R.id.txt_card_brand_placeholder);
            txt_card_brand_label = view.findViewById(R.id.txt_card_brand_label);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
