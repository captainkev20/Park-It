package com.example.kevinwalker.parkit.spot;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kevinwalker.parkit.R;

import java.util.List;

public class SpotListingsAdapter extends RecyclerView.Adapter<SpotListingsAdapter.ViewHolder> {

    private final List<Spot> mValues;
    private final SpotListingsFragment.SpotListingsInteraction mListener;

    public SpotListingsAdapter(List<Spot> items, SpotListingsFragment.SpotListingsInteraction listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_spot_list_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        //TODO: Modify to speed up
        holder.mItem = mValues.get(position);
        holder.txt_payment_type.setText(mValues.get(position).getPaymentType().toString());
        holder.txt_cost.setText(String.valueOf(mValues.get(position).getHourlyRate()));
        holder.txt_distance.setText(String.valueOf(mValues.get(position).getSpotDistance()));
        holder.txt_spot_name.setText(mValues.get(position).getName());
        holder.edit_image_logo.setImageResource(R.drawable.ic_test_profile_pic);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onSpotListingInteraction(holder.mItem);
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
        public final TextView txt_payment_type;
        public final TextView txt_distance;
        public final TextView txt_cost;
        public final TextView txt_spot_name;
        public final ImageView edit_image_logo;
        public Spot mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            txt_payment_type = view.findViewById(R.id.txt_payment_type);
            txt_distance = view.findViewById(R.id.txt_distance);
            txt_cost = view.findViewById(R.id.txt_cost);
            txt_spot_name = view.findViewById(R.id.txt_spot_name);
            edit_image_logo = view.findViewById(R.id.edit_image_logo);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
