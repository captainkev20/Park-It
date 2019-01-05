package com.example.kevinwalker.parkit.users;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.example.kevinwalker.parkit.NavDrawer;
import com.example.kevinwalker.parkit.R;
import com.example.kevinwalker.parkit.profiles.ParentProfileFragment;
import com.example.kevinwalker.parkit.utils.CustomTextView;
import com.example.kevinwalker.parkit.utils.FirestoreHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import java.io.File;
import java.io.FileOutputStream;

import static android.app.Activity.RESULT_OK;

public class UserProfileFragment extends ParentProfileFragment implements View.OnClickListener {

    @BindView(R.id.image_logo) CircleImageView image_logo;
    @BindView(R.id.ic_phone) CustomTextView ic_phone;
    @BindView(R.id.ic_email) CustomTextView ic_email;
    @BindView(R.id.txt_first_name) TextView txt_first_name;
    @BindView(R.id.txt_last_name) TextView txt_last_name;
    @BindView(R.id.txt_phone_number) TextView txt_phone_number;
    @BindView(R.id.txt_email) TextView txt_email;

    @BindView(R.id.edit_image_logo) CircleImageView edit_image_logo;
    @BindView(R.id.et_phone_number) EditText et_phone_number;
    @BindView(R.id.et_email) EditText et_email;
    @BindView(R.id.et_last_name2) EditText et_last_name2;
    @BindView(R.id.et_first_name2) EditText et_first_name2;
    @BindView(R.id.profile_view_switcher) ViewSwitcher profile_view_switcher;
    @BindView(R.id.btn_edit_profile) Button btn_edit_profile;
    @BindView(R.id.btn_cancel_edit_profile) Button btn_cancel_edit_profile;
    @BindView(R.id.btn_save_profile) Button btn_save_profile;
    @BindView(R.id.edit_profile_card_view) CardView edit_profile_card_view;
    @BindView(R.id.main_profile_card_view) CardView main_profile_card_view;
    @BindView(R.id.save_photo_progress_bar) ProgressBar save_user_progress_bar;

    private static final String TAG = UserProfileFragment.class.getName();
    private StorageReference userProfileReference;
    StorageReference filePath;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    protected DrawerLayout drawer;
    protected Toolbar toolbar;
    private View mView;
    private UserProfileCallback callback;
    private NavDrawer navDrawer;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UserProfileFragment.UserProfileCallback) {
            callback = (UserProfileFragment.UserProfileCallback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement UserProfileFragment.UserProfileCallback");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        navDrawer = (NavDrawer) getActivity();
        userProfileReference = FirebaseStorage.getInstance().getReference();
        filePath = userProfileReference.child("UserProfilePhotos/").child(getFirebaseUser().getUid() + "_profile_picture.png");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_user, container, false);
        ButterKnife.bind(this, mView);

        //updateUI();

        FirestoreHelper.getInstance().getUserProfilePhotoFromFirebase();

        btn_edit_profile.setOnClickListener(this);
        btn_save_profile.setOnClickListener(this);
        edit_image_logo.setOnClickListener(this);
        btn_cancel_edit_profile.setOnClickListener(this);

        save_user_progress_bar.setVisibility(View.GONE);


        return mView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_edit_profile:
                profile_view_switcher.showNext();
                updateUI();
                break;

            case R.id.btn_save_profile:
                String firstNameString = et_first_name2.getText().toString();
                String lastNameString = et_last_name2.getText().toString();
                String userEmail = et_email.getText().toString();
                String phoneNum = et_phone_number.getText().toString();

                // TODO: Data input validation


                FirestoreHelper.getInstance().getCurrentUser().setFirstName(firstNameString);
                FirestoreHelper.getInstance().getCurrentUser().setLastName(lastNameString);
                FirestoreHelper.getInstance().getCurrentUser().setUserEmail(userEmail);
                FirestoreHelper.getInstance().getCurrentUser().setUserPhone(phoneNum);

                FirestoreHelper.getInstance().mergeCurrentUserWithFirestore();
                FirestoreHelper.getInstance().getUserProfilePhotoFromFirebase();
                FirestoreHelper.getInstance().getUserNavProfileHeaderFromFirebase();
                updateUI();

                profile_view_switcher.showPrevious();
                break;

            case R.id.edit_image_logo:
                dispatchTakePictureIntent();
                break;

            case R.id.btn_cancel_edit_profile:
                profile_view_switcher.showPrevious();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        getActivity().setTitle(getResources().getString(R.string.profile_nav_title));

        updateUI();
    }

    private FirebaseUser getFirebaseUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    private void refreshProfilePicture(Bitmap bitmap) {
        edit_image_logo.setImageBitmap(bitmap);
    }

    private File getProfilePictureFile(Bitmap bitmap) {
        String filename = getActivity().getFilesDir().getAbsolutePath() + "/" + getFirebaseUser().getUid() + "_profile_picture.png";
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

    private void updateUI() {
        txt_first_name.setText(FirestoreHelper.getInstance().getCurrentUser().getFirstName());
        txt_email.setText(FirestoreHelper.getInstance().getCurrentUser().getUserEmail());
        txt_phone_number.setText(FirestoreHelper.getInstance().getCurrentUser().getUserPhone());
        txt_last_name.setText(FirestoreHelper.getInstance().getCurrentUser().getLastName());
        et_phone_number.setText(FirestoreHelper.getInstance().getCurrentUser().getUserPhone());
        et_email.setText(FirestoreHelper.getInstance().getCurrentUser().getUserEmail());
        et_last_name2.setText(FirestoreHelper.getInstance().getCurrentUser().getLastName());
        et_first_name2.setText(FirestoreHelper.getInstance().getCurrentUser().getFirstName());
    }

    public void updateUserProfilePicture(Uri filePath) {
        Picasso.get().load(filePath).centerCrop().resize(128, 140).rotate(90).into(edit_image_logo);
        Picasso.get().load(filePath).centerCrop().resize(128, 140).rotate(90).into(image_logo);
    }

    private boolean isEditTextEmpty(EditText editText) {
        return editText.getText().toString().isEmpty();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Verify a camera activity can handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }

    // TODO: Review with Hollis and determine if this is best way to handle
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            save_user_progress_bar.setVisibility(View.VISIBLE);
            edit_image_logo.setVisibility(View.INVISIBLE);

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Uri uri = Uri.fromFile(getProfilePictureFile(imageBitmap));

            StorageReference filePath = userProfileReference.child("UserProfilePhotos/").child(uri.getLastPathSegment());

            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            save_user_progress_bar.setVisibility(View.GONE);
                            edit_image_logo.setVisibility(View.VISIBLE);

                            Toast.makeText(getActivity(), R.string.saved_profile_photo, Toast.LENGTH_SHORT).show();
                            FirestoreHelper.getInstance().getCurrentUser().setUserProfilePhotoURL(String.valueOf(filePath));
                            FirestoreHelper.getInstance().mergeCurrentUserWithFirestore();
                            Picasso.get().load(uri).centerCrop().resize(128, 140).rotate(90).into(edit_image_logo);
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
    public void onStop() {
        super.onStop();
    }

    public interface UserProfileCallback {
        void userUpdated(User user);
    }

}
