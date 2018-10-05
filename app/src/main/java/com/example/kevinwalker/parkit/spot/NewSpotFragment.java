package com.example.kevinwalker.parkit.spot;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.example.kevinwalker.parkit.R;
import com.example.kevinwalker.parkit.profiles.ParentProfileFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.util.UUID;


public class NewSpotFragment extends ParentProfileFragment implements View.OnClickListener {

    @BindView(R.id.et_spot_name) EditText et_spot_name;
    @BindView(R.id.et_hourly_rate) EditText et_hourly_rate;
    @BindView(R.id.txt_save_spot) TextView txt_save_spot;
    @BindView(R.id.layout_et_spot_name) TextInputLayout layout_et_spot_name;

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference spotCollectionReference;
    DocumentReference spotDocumentReference;

    private NewSpotCallback newSpotCallback;
    private Spot userSpot;

    private static final String TAG = NewSpotFragment.class.getName();

    private View mView;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";



    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //private OnFragmentInteractionListener mListener;

    public NewSpotFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static NewSpotFragment newInstance(String param1, String param2) {
        NewSpotFragment fragment = new NewSpotFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        //spotCollectionReference = firebaseFirestore.collection("spots/");

        spotDocumentReference = firebaseFirestore.collection("spots").document(String.valueOf(UUID.randomUUID()));

        spotDocumentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    userSpot = documentSnapshot.toObject(Spot.class);
                    if (userSpot.getSpotUUID().trim().isEmpty()) {
                        userSpot.setSpotUUID(String.valueOf(UUID.randomUUID()));
                        mergeSpotWithFirebase(userSpot);
                    } else {
                        mergeSpotWithFirebase(userSpot);
                    }
                    Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Failed to write", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_new_spot, container, false);

        // Inflate the layout for this fragment

        ButterKnife.bind(this, mView);

        txt_save_spot.setOnClickListener(this);

        return inflater.inflate(R.layout.fragment_new_spot, container, false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_save_spot:

                String spotNameString = et_spot_name.getText().toString();
                int spotHourlyRate = Integer.parseInt(et_hourly_rate.getText().toString());

                userSpot.setDailyRate(BigDecimal.valueOf(spotHourlyRate));
                userSpot.setName(spotNameString);

                mergeSpotWithFirebase(userSpot);

                break;
        }
    }

    private void mergeSpotWithFirebase(Spot userSpot) {
        spotDocumentReference.set(userSpot, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Successful write");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failed to write");
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {


        getActivity().setTitle(getResources().getString(R.string.add_new_spot));
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NewSpotFragment.NewSpotCallback) {
            newSpotCallback = (NewSpotFragment.NewSpotCallback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface NewSpotCallback {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
