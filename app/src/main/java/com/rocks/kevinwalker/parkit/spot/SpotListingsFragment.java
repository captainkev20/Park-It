package com.rocks.kevinwalker.parkit.spot;

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

public class SpotListingsFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";

    @BindView(R.id.spot_recycler_view) RecyclerView spotRecyclerView;

    private int mColumnCount = 1;
    private SpotListingsInteraction mListener;
    private ArrayList<Spot> spots = new ArrayList<>();
    private SpotListingsAdapter spotListingsAdapter;


    public SpotListingsFragment() {}

    @SuppressWarnings("unused")
    public static SpotListingsFragment newInstance(int columnCount) {
        SpotListingsFragment fragment = new SpotListingsFragment();
        ArrayList<Spot> spots = new ArrayList<>();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SpotListingsInteraction) {
            mListener = (SpotListingsInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SpotListingsInteraction");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        if (spots.isEmpty()) {
            spots.add(FirestoreHelper.getInstance().getUserSpot());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spot_list, container, false);

        ButterKnife.bind(this, view);

        FirestoreHelper.getInstance().getAllSpots();

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            spotRecyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                spotRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                spotRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            //resetRecyclerView();
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle(getResources().getString(R.string.listings_nav_title));
    }

    @Override
    public void onResume(){
        super.onResume();

        mListener.setSpotFabVisibility(View.VISIBLE);

    }

    @Override
    public void onPause() {
        super.onPause();
        mListener.setSpotFabVisibility(View.GONE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void resetRecyclerView() {
        spotListingsAdapter = new SpotListingsAdapter(FirestoreHelper.getInstance().getAllSpots(), mListener);
        spotRecyclerView.setAdapter(spotListingsAdapter);
    }

    public void resetRecyclerView(ArrayList<Spot> spots) {
        spotListingsAdapter = new SpotListingsAdapter(spots, mListener);
        spotRecyclerView.setAdapter(spotListingsAdapter);
    }

    public interface SpotListingsInteraction {
        void onSpotListingInteraction(Spot item);
        void setSpotFabVisibility(int viewVisibilityConstant);
    }
}
