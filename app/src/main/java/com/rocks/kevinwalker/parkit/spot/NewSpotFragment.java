package com.rocks.kevinwalker.parkit.spot;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rocks.kevinwalker.parkit.NavDrawer;
import com.rocks.kevinwalker.parkit.R;
import com.rocks.kevinwalker.parkit.maps.CustomLocation;
import com.rocks.kevinwalker.parkit.profiles.ParentProfileFragment;
import com.rocks.kevinwalker.parkit.utils.EditTextValidator;
import com.rocks.kevinwalker.parkit.utils.FirestoreHelper;
import com.rocks.kevinwalker.parkit.utils.LocationHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;


public class NewSpotFragment extends ParentProfileFragment implements View.OnClickListener,
        AdapterView.OnItemSelectedListener {

    private static final String TAG = NewSpotFragment.class.getName();
    private static final int REQUEST_IMAGE_CAPTURE = 3;

    private CustomLocation spotLocation = new CustomLocation();
    private LocationHelper locationHelper;

    private boolean isSpotLocationSet = false;

    @BindView(R.id.et_spot_name) EditText et_spot_name;
    @BindView(R.id.et_hourly_rate) EditText et_hourly_rate;
    @BindView(R.id.btn_save_spot) Button btn_save_spot;
    @BindView(R.id.btn_spot_location) Button btn_spot_location;
    @BindView(R.id.txt_view_cancel_add_spot) TextView txt_cancel_add_spot;
    @BindView(R.id.spinner_spot_size) Spinner spinner_spot_size;
    @BindView(R.id.txt_input_layout_hourly_rate) TextInputLayout txt_input_layout_hourly_rate;
    @BindView(R.id.txt_input_layout_spot_name) TextInputLayout txt_input_layout_spot_name;
    @BindView(R.id.spot_image) ImageView spot_image;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private DocumentReference spotDocumentReference;
    private StorageReference spotReference;
    private UUID spotUUID;

    private NewSpotCallback newSpotCallback;
    private Spot userSpot = new Spot();

    private EditTextValidator newSpotEditTextValidator;

    private Context mContext;

    private View mView;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public NewSpotFragment() { }

    public static NewSpotFragment newInstance(String param1, String param2) {
        NewSpotFragment fragment = new NewSpotFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        FirestoreHelper.getInstance().initializeFirestoreSpot();
        locationHelper = new LocationHelper(this.getContext());

        newSpotEditTextValidator = new EditTextValidator(getContext());

        spotUUID = UUID.randomUUID();

        spotDocumentReference = firebaseFirestore.collection("spots").document(String.valueOf(spotUUID));

        spotReference = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_new_spot, container, false);

        ButterKnife.bind(this, mView);

        // TODO: Review with Hollis
        ArrayAdapter<CharSequence> spotSizeAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.spinner_spot_size_items,
                android.R.layout.simple_spinner_item);
        spotSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_spot_size.setAdapter(spotSizeAdapter);
        spinner_spot_size.setOnItemSelectedListener(this);

        btn_save_spot.setOnClickListener(this);
        btn_spot_location.setOnClickListener(this);
        txt_cancel_add_spot.setOnClickListener(this);
        spot_image.setOnClickListener(this);


        return mView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_save_spot:

                EditText spotNameEditText = txt_input_layout_spot_name.getEditText();
                EditText spotHourlyRateEditText = txt_input_layout_hourly_rate.getEditText();

                if (newSpotEditTextValidator.validateEditText(spotNameEditText)
                        && newSpotEditTextValidator.validateEditText(spotHourlyRateEditText)) {

                    userSpot.setHourlyRate(Integer.parseInt(spotHourlyRateEditText.getText().toString()));
                    userSpot.setName(spotNameEditText.getText().toString());
                    userSpot.setLatitude(spotLocation.getLatitude());
                    userSpot.setLongitude(spotLocation.getLongitude());
                    userSpot.setSpotSize(spinner_spot_size.getSelectedItem().toString());
                    userSpot.setSpotPhotoURL(FirestoreHelper.getInstance().getUserSpot().getSpotPhotoURL());


                    if (isSpotLocationSet) {
                        FirestoreHelper.getInstance().mergeSpotWithFirestore(userSpot);
                        newSpotCallback.navigateToSpotListings();
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.spot_button_location_not_pressed),
                                Toast.LENGTH_SHORT).show();
                    }

                } else if (!newSpotEditTextValidator.validateEditText(spotHourlyRateEditText)) {
                    txt_input_layout_hourly_rate.setError(getResources().getString(R.string.text_input_layout_et_hourly_rate));
                } else if (!newSpotEditTextValidator.validateEditText(spotNameEditText)) {
                    txt_input_layout_spot_name.setError(getResources().getString(R.string.text_input_layout_et_spot_name));
                }

                break;

            case R.id.btn_spot_location:
                spotLocation = locationHelper.getCurrentLocation();
                isSpotLocationSet = true;

                break;

            case R.id.txt_view_cancel_add_spot:
                newSpotCallback.navigateToSpotListings();
                break;

            case R.id.spot_image:
                dispatchTakePictureIntent();
                break;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        getActivity().setTitle(getResources().getString(R.string.add_new_spot));
    }

    private File getProfilePictureFile(Bitmap bitmap) {
        String filename = getActivity().getFilesDir().getAbsolutePath() + "/" + FirestoreHelper.getInstance()
                .getSpotRef()
                + "_spot_picture.png";
        File dest = new File(filename);

        try {
            FileOutputStream out = new FileOutputStream(dest);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dest;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Verify a camera activity can handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == NavDrawer.RESULT_OK) {

            //vehicle_photo_progress_bar.setVisibility(View.VISIBLE);
            //edit_image_logo.setVisibility(View.INVISIBLE);

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Uri uri = Uri.fromFile(getProfilePictureFile(imageBitmap));

            StorageReference filePath = spotReference.child("UserSpotPhotos/").child(uri.getLastPathSegment());

            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //vehicle_photo_progress_bar.setVisibility(View.GONE);
                            //edit_image_logo.setVisibility(View.VISIBLE);

                            Toast.makeText(getActivity(), R.string.saved_profile_photo, Toast.LENGTH_SHORT).show();
                            if (FirestoreHelper.getInstance().getCurrentUser() != null) {
                                FirestoreHelper.getInstance().getUserSpot().setSpotPhotoURL(String.valueOf(uri));
                                FirestoreHelper.getInstance().mergeSpotWithFirestore(userSpot);
                            }
                            Picasso.get().load(uri).centerCrop().resize(128, 140).rotate(90).into(spot_image);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), "Failed to write!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onDetach() { super.onDetach(); }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) { }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) { }

    public interface NewSpotCallback {
        void onFragmentInteraction(Uri uri);
        void navigateToSpotListings();
    }
}
