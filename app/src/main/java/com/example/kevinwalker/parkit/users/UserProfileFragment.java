package com.example.kevinwalker.parkit.users;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.example.kevinwalker.parkit.R;
import com.example.kevinwalker.parkit.profiles.ParentProfileActivity;
import com.example.kevinwalker.parkit.utils.CustomTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileFragment extends ParentProfileActivity implements View.OnClickListener {

    @BindView(R.id.image_logo) CircleImageView image_logo;
    @BindView(R.id.ic_phone) CustomTextView ic_phone;
    @BindView(R.id.ic_email) CustomTextView ic_email;
    @BindView(R.id.txt_first_name) TextView txt_first_name;
    @BindView(R.id.txt_phone_number) TextView txt_phone_number;
    @BindView(R.id.txt_email) TextView txt_email;
    @BindView(R.id.rating_bar) RatingBar rating_bar;

    @BindView(R.id.edit_image_logo) CircleImageView edit_image_logo;
    @BindView(R.id.edit_first_name) TextView edit_first_name;
    @BindView(R.id.edit_last_name) TextView edit_last_name;
    @BindView(R.id.edit_phone_number) TextView edit_phone_number;
    @BindView(R.id.edit_email) TextView edit_email;
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
    protected DrawerLayout drawer;
    protected Toolbar toolbar;
    private View mView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userDatabaseReference = database.getReference("users");
    private FirebaseAuth mAuth;
    private User currentUser;
    public static final String FIRST_NAME_KEY = "firstName";
    public static final String LAST_NAME_KEY = "lastName";
    public static final String USER_EMAIL_KEY = "userEmail";
    public static final String PHONE_NUM_KEY = "userPhone";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userDatabaseReference = userDatabaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        //currentUser = userDatabaseReference
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_user, container, false);
        ButterKnife.bind(this, mView);

        userDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Log.i(TAG, dataSnapshot.getValue().toString());

                if (dataSnapshot.getValue(User.class) == null) {
                    userDatabaseReference.setValue(currentUser);
                } else {
                    currentUser = dataSnapshot.getValue(User.class);
                    updateUI();
                }
                //Log.i(TAG, currentUser.getUserUUID());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, "Failed to write");
            }
        });



        txt_edit_user_profile.setOnClickListener(this);
        txt_save_profile.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

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

                userDatabaseReference.child(FIRST_NAME_KEY).setValue(firstNameString);
                userDatabaseReference.child(LAST_NAME_KEY).setValue(lastNameString);
                userDatabaseReference.child(USER_EMAIL_KEY).setValue(userEmail);
                userDatabaseReference.child(PHONE_NUM_KEY).setValue(phoneNum);

                profile_view_switcher.showPrevious();
                break;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        ButterKnife.bind(this, view);

        /*userDatabaseReference = userDatabaseReference.child(mAuth.getUid());
        userDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);
                txt_email.setText(currentUser.getUserEmail());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

        getActivity().setTitle(getResources().getString(R.string.profile_nav_title));

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_test_profile_pic);

        setPrimaryPhoto(bm);
        image_logo.setImageBitmap(getPrimaryPhoto());
    }

    private void updateUI() {
        txt_first_name.setText(currentUser.getFirstName());
        txt_email.setText(currentUser.getUserEmail());
        txt_phone_number.setText(currentUser.getUserPhone());
    }


}
