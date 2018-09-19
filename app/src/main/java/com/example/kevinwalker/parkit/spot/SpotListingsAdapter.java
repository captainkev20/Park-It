package com.example.kevinwalker.parkit.spot;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kevinwalker.parkit.R;

import java.util.List;

public class SpotListingsAdapter extends RecyclerView.Adapter<SpotListingsAdapter.ViewHolder> {

    private final List<Spot> mValues;
    private final SpotListings.SpotListingsInteraction mListener;

    public SpotListingsAdapter(List<Spot> items, SpotListings.SpotListingsInteraction listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_spot, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.txt_label.setText(mValues.get(position).getName());
        holder.txt_occupied.setText(String.valueOf(mValues.get(position).isOccupied()));

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
        public final TextView txt_label;
        public final TextView txt_occupied;
        public Spot mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            txt_label = (TextView) view.findViewById(R.id.txt_label);
            txt_occupied = (TextView) view.findViewById(R.id.txt_occupied);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + txt_occupied.getText() + "'";
        }
    }
}
