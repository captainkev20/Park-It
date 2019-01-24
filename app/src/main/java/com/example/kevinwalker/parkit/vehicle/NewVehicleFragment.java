package com.example.kevinwalker.parkit.vehicle;

import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.kevinwalker.parkit.R;
import com.example.kevinwalker.parkit.profiles.ParentProfileFragment;
import com.example.kevinwalker.parkit.users.User;
import com.example.kevinwalker.parkit.utils.EditTextValidator;
import com.example.kevinwalker.parkit.utils.FirestoreHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;


public class NewVehicleFragment extends ParentProfileFragment implements View.OnClickListener,
        AdapterView.OnItemSelectedListener {

    private static final String TAG = NewVehicleFragment.class.getName();
    private static final String VEHICLE_SPINNER_JSON_URL = "https://gist.githubusercontent.com/" +
            "kwalker0456/5d184b08181f819eb1bcca8363a40735/raw/3edf3dd2cbcab4aef3cb83154e46371e5c4c5d3a/" +
            "vehicles.json";
    private static final String VOLLEY_REQUEST_TAG = "NewVehicleFragment Request";

    @BindView(R.id.et_license) EditText et_license;
    @BindView(R.id.et_vehicle_name) EditText et_vehicle_name;
    @BindView(R.id.btn_save_vehicle) Button btn_save_vehicle;
    @BindView(R.id.spinner_vehicle_make) Spinner spinner_vehicle_make;
    @BindView(R.id.txt_view_cancel_add_vehicle) TextView txt_view_cancel_add_vehicle;
    @BindView(R.id.txt_input_layout_vehicle_name) TextInputLayout txt_input_layout_vehicle_name;
    @BindView(R.id.txt_input_layout_license) TextInputLayout txt_input_layout_vehicle_license;

    private StringRequest stringRequest;
    private RequestQueue vehicleMakeRequestQueue;

    private NewVehicleCallback newVehicleCallback;
    private Vehicle userVehicle = new Vehicle();
    private ArrayList<Vehicle> vehicles = new ArrayList<>();
    private ArrayList<String> vehicleBrands = new ArrayList<>();
    private ArrayAdapter<String> vehicleBrandsAdapter;

    private EditTextValidator newVehicleEditTextValidator;

    private Context mContext;
    private NewVehicleCallback mListener;

    private View mView;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public NewVehicleFragment() { }

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

        vehicleMakeRequestQueue = Volley.newRequestQueue(getContext());

        vehicleBrandsAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, vehicleBrands);
        vehicleBrandsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        newVehicleEditTextValidator = new EditTextValidator(getContext());

        populateVehicleSpinner();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_new_vehicle, container, false);

        ButterKnife.bind(this, mView);

        btn_save_vehicle.setOnClickListener(this);
        txt_view_cancel_add_vehicle.setOnClickListener(this);
        spinner_vehicle_make.setOnItemSelectedListener(this);

        return mView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_save_vehicle:

                EditText vehicleNameEditText = txt_input_layout_vehicle_name.getEditText();
                EditText vehicleLicensePlateEditText = txt_input_layout_vehicle_license.getEditText();

                if (newVehicleEditTextValidator.validateEditText(vehicleNameEditText)
                        && newVehicleEditTextValidator.validateEditText(vehicleLicensePlateEditText)) {

                    // TODO: Review with Hollis - creating user/vehicle and adding
                    userVehicle.setVehicleName(vehicleNameEditText.getText().toString());
                    userVehicle.setVehicleLicensePlate(vehicleLicensePlateEditText.getText().toString());
                    userVehicle.setVehicleMake(spinner_vehicle_make.getSelectedItem().toString());
                    userVehicle.setVehicleUUID(FirestoreHelper.getInstance().getCurrentUser().getUserUUID());

                    vehicles.add(0, userVehicle);

                    FirestoreHelper.getInstance().mergeVehicleWithFirestore(vehicles.get(0));
                    newVehicleCallback.navigateToVehicleListings();
                } else if (!newVehicleEditTextValidator.validateEditText(vehicleNameEditText)) {
                    txt_input_layout_vehicle_name.setError(getResources().getString(R.string.text_input_layout_et_vehicle_name));
                } else if (!newVehicleEditTextValidator.validateEditText(vehicleLicensePlateEditText)) {
                    txt_input_layout_vehicle_license.setError(getResources().getString(R.string.text_input_layout_et_vehicle_license));
                }

                break;

            case R.id.txt_view_cancel_add_vehicle:
                newVehicleCallback.navigateToVehicleListings();

                break;
        }
    }

    private void populateVehicleSpinner() {

        // TODO: Review with Hollis. Best to create JSONObject inside of loop? Asked during interivew
        stringRequest = new StringRequest(Request.Method.GET, VEHICLE_SPINNER_JSON_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject vehicle = new JSONObject(response);
                    JSONArray vehicleArray = vehicle.getJSONArray(getResources()
                            .getString(R.string.vehicle_json_name));

                    for (int i = 0; i < vehicleArray.length(); i++) {
                        JSONObject vehicleJSONObject = vehicleArray.getJSONObject(i);

                        vehicleBrands.add(vehicleJSONObject.getString("make"));

                        spinner_vehicle_make.setAdapter(vehicleBrandsAdapter);

                    }
                } catch (JSONException e) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error on request");
            }
        });
        stringRequest.setTag(VOLLEY_REQUEST_TAG);
        vehicleMakeRequestQueue.add(stringRequest);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        getActivity().setTitle(getResources().getString(R.string.add_new_vehicle));
    }

    @Override
    public void onStop() {
        super.onStop();

        // If fragment is stopped, remove our network request
        if (vehicleMakeRequestQueue != null) {
            vehicleMakeRequestQueue.cancelAll(VOLLEY_REQUEST_TAG);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) { }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) { }

    public interface NewVehicleCallback {
        void navigateToVehicleListings();
    }
}
