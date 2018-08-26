package com.example.kevinwalker.parkit.spot;

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

import com.example.kevinwalker.parkit.R;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link SpotListingsInteraction}
 * interface.
 */
public class SpotListings extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private SpotListingsInteraction mListener;
    private ArrayList<Spot> spots = new ArrayList<>();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SpotListings() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static SpotListings newInstance(int columnCount) {
        SpotListings fragment = new SpotListings();
        ArrayList<Spot> spots = new ArrayList<>();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        if (spots.isEmpty()) {
            addDummySpots();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spot_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MySpotRecyclerViewAdapter(spots, mListener));
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle(getResources().getString(R.string.listings_nav_title));
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void addDummySpots() {
        Spot spot0 = new Spot();
        Spot spot1 = new Spot();
        Spot spot2 = new Spot();
        Spot spot3 = new Spot();

        spot0.setName("Coliseum Parking");
        spot1.setName("Don't Park Here");
        spot2.setName("Outback Overflow");
        spot3.setName("RV Haven");

        spots.add(spot0);
        spots.add(spot1);
        spots.add(spot2);
        spots.add(spot3);
        spots.add(spot0);
        spots.add(spot0);
        spots.add(spot0);
        spots.add(spot0);
        spots.add(spot0);
        spots.add(spot0);
        spots.add(spot0);
        spots.add(spot0);
        spots.add(spot0);
        spots.add(spot0);
        spots.add(spot0);
        spots.add(spot0);
        spots.add(spot0);
        spots.add(spot0);
        spots.add(spot0);
        spots.add(spot0);

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface SpotListingsInteraction {
        void onSpotListingInteraction(Spot item);
    }
}
