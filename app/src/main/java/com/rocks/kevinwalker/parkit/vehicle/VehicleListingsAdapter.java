package com.rocks.kevinwalker.parkit.vehicle;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rocks.kevinwalker.parkit.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class VehicleListingsAdapter extends RecyclerView.Adapter<VehicleListingsAdapter.ViewHolder> {

    private final List<Vehicle> mValues;
    private final VehicleListingFragment.VehicleListingsInteraction mListener;

    public VehicleListingsAdapter(List<Vehicle> items, VehicleListingFragment.VehicleListingsInteraction listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_vehicle_list_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

         //TODO: Modify to speed up
        holder.mItem = mValues.get(position);
        holder.txt_vehicle_name.setText(mValues.get(position).getVehicleName());
        holder.txt_vehicle_make.setText(String.valueOf(mValues.get(position).getVehicleMake()));
        holder.txt_license_plate.setText(String.valueOf(mValues.get(position).getVehicleLicensePlate()));

        if (!mValues.get(position).getVehiclePhotoURL().isEmpty()) {
            Picasso.get().load(String.valueOf(mValues.get(position).getVehiclePhotoURL()))
                    .centerCrop()
                    .resize(128, 140)
                    .rotate(90)
                    .placeholder(R.drawable.ic_vehicle)
                    .into(holder.vehicle_image);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onVehicleListingInteraction(holder.mItem);
                    Toast.makeText((Context)mListener, mValues.get(position).getVehicleName(), Toast.LENGTH_SHORT).show();
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
        public final TextView txt_vehicle_name;
        public final TextView txt_vehicle_make;
        public final TextView txt_license_plate;
        public final ImageView vehicle_image;
        public Vehicle mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            txt_vehicle_name = view.findViewById(R.id.txt_vehicle_name);
            txt_vehicle_make = view.findViewById(R.id.txt_vehicle_make);
            txt_license_plate = view.findViewById(R.id.txt_license_plate);
            vehicle_image = view.findViewById(R.id.vehicle_image);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
