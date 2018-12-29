package com.example.kevinwalker.parkit.vehicle;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.kevinwalker.parkit.R;
import com.example.kevinwalker.parkit.profiles.ParentProfileFragment;
import com.example.kevinwalker.parkit.utils.FirestoreHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;


public class NewVehicleFragment extends ParentProfileFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = NewVehicleFragment.class.getName();

    @BindView(R.id.et_license) EditText et_license;
    @BindView(R.id.et_vehicle_name) EditText et_vehicle_name;
    @BindView(R.id.btn_save_vehicle) Button btn_save_vehicle;

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    DocumentReference vehicleDocumentReference;

    private NewVehicleCallback newVehicleCallback;
    private Vehicle userVehicle = new Vehicle();

    private Context mContext;
    private NewVehicleCallback mListener;

    private View mView;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public NewVehicleFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static NewVehicleFragment newInstance(String param1, String param2) {
        NewVehicleFragment fragment = new NewVehicleFragment();
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

        FirestoreHelper.getInstance().initializeFirestoreVehicle();

        vehicleDocumentReference = firebaseFirestore.collection("vehicles").document(String.valueOf(UUID.randomUUID()));

        vehicleDocumentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    userVehicle = documentSnapshot.toObject(Vehicle.class);
                    if (userVehicle.getVehicleUUID().trim().isEmpty()) {
                        userVehicle.setVehicleUUID(String.valueOf(UUID.randomUUID()));
                        mergeVehicleWithFirebase(userVehicle);
                    } else {
                        mergeVehicleWithFirebase(userVehicle);
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
        mView = inflater.inflate(R.layout.fragment_new_vehicle, container, false);

        ButterKnife.bind(this, mView);

        btn_save_vehicle.setOnClickListener(this);

        return mView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_save_vehicle:

                Log.d(TAG, "new vehicle button click");

                String vehicleName = et_vehicle_name.getText().toString();
                String vehicleLicensePlate = et_license.getText().toString();

                userVehicle.setVehicleName(vehicleName);
                userVehicle.setVehicleLicensePlate(vehicleLicensePlate);

                mergeVehicleWithFirebase(userVehicle);
                newVehicleCallback.navigateToVehicleListings();

                break;
        }
    }

    private void mergeVehicleWithFirebase(Vehicle userVehicle) {
        vehicleDocumentReference.set(userVehicle, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Successful write");
                Toast.makeText(getActivity(), "Vehicle Saved!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failed to write");
                Toast.makeText(getActivity(), "Vehicle Not Saved!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        getActivity().setTitle(getResources().getString(R.string.add_new_vehicle));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NewVehicleFragment.NewVehicleCallback) {
            newVehicleCallback = (NewVehicleFragment.NewVehicleCallback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public interface NewVehicleCallback {
        void navigateToVehicleListings();
    }
}
