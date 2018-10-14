package com.example.kevinwalker.parkit.users;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.example.kevinwalker.parkit.NavDrawer;
import com.example.kevinwalker.parkit.R;
import com.example.kevinwalker.parkit.profiles.ParentProfileFragment;
import com.example.kevinwalker.parkit.utils.CustomTextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import java.io.File;
import java.io.FileOutputStream;

public class UserProfileFragment extends ParentProfileFragment implements View.OnClickListener {

    @BindView(R.id.image_logo) CircleImageView image_logo;
    @BindView(R.id.ic_phone) CustomTextView ic_phone;
    @BindView(R.id.ic_email) CustomTextView ic_email;
    @BindView(R.id.txt_first_name) TextView txt_first_name;
    @BindView(R.id.txt_phone_number) TextView txt_phone_number;
    @BindView(R.id.txt_email) TextView txt_email;
    @BindView(R.id.rating_bar) RatingBar rating_bar;

    @BindView(R.id.edit_image_logo) CircleImageView edit_image_logo;
    @BindView(R.id.et_phone_number) EditText et_phone_number;
    @BindView(R.id.et_email) EditText et_email;
    @BindView(R.id.et_last_name2) EditText et_last_name2;
    @BindView(R.id.et_first_name2) EditText et_first_name2;
    @BindView(R.id.profile_view_switcher) ViewSwitcher profile_view_switcher;
    @BindView(R.id.txt_edit_user_profile) TextView txt_edit_user_profile;
    @BindView(R.id.txt_save_profile) TextView txt_save_profile;
    @BindView(R.id.edit_profile_card_view) CardView edit_profile_card_view;
    @BindView(R.id.main_profile_card_view) CardView main_profile_card_view;

    private static final String TAG = UserProfileFragment.class.getName();
    static final int REQUEST_IMAGE_CAPTURE = 1;
    protected DrawerLayout drawer;
    protected Toolbar toolbar;
    private View mView;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    private UserProfileCallback callback;
    private NavDrawer navDrawer;
    private User currentUser;
    private ListenerRegistration listenerRegistration;

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
        currentUser = navDrawer.getCurrentUser();
        documentReference = firebaseFirestore.document("users/" + navDrawer.getCurrentUser().getUserUUID());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_user, container, false);
        ButterKnife.bind(this, mView);

        listenerRegistration = documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    currentUser = snapshot.toObject(User.class);
                    updateUI();
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });

        txt_edit_user_profile.setOnClickListener(this);
        txt_save_profile.setOnClickListener(this);

        return mView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_edit_user_profile:
                profile_view_switcher.showNext();
                break;

            case R.id.txt_save_profile:
                String firstNameString = et_first_name2.getText().toString();
                String lastNameString = et_last_name2.getText().toString();
                String userEmail = et_email.getText().toString();
                String phoneNum = et_phone_number.getText().toString();

                // TODO: Data input validation
                currentUser.setFirstName(firstNameString);
                currentUser.setLastName(lastNameString);
                currentUser.setUserEmail(userEmail);
                currentUser.setUserPhone(phoneNum);

                callback.userUpdated(currentUser);

                profile_view_switcher.showPrevious();
                break;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        getActivity().setTitle(getResources().getString(R.string.profile_nav_title));

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_test_profile_pic);

        setPrimaryPhoto(bm);
        image_logo.setImageBitmap(getPrimaryPhoto());
    }

    private FirebaseUser getFirebaseUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    private void updateUserPhoto(File file, FirebaseUser firebaseUser) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(file.getName()))
                .build();

        firebaseUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                            Toast.makeText(getActivity(), "Saved new profile photo", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "Failed to updated FirebaseUser Profile Picture");
                            Toast.makeText(getActivity(), "Update failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
        if (currentUser != null) {
            txt_first_name.setText(navDrawer.getCurrentUser().getFirstName());
            txt_email.setText(navDrawer.getCurrentUser().getUserEmail());
            txt_phone_number.setText(navDrawer.getCurrentUser().getUserPhone());
            et_phone_number.setText(navDrawer.getCurrentUser().getUserPhone());
            et_email.setText(navDrawer.getCurrentUser().getUserEmail());
            et_last_name2.setText(navDrawer.getCurrentUser().getLastName());
            et_first_name2.setText(navDrawer.getCurrentUser().getFirstName());
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        listenerRegistration.remove();
    }
    
    public interface UserProfileCallback {
        void userUpdated(User user);
    }

}
