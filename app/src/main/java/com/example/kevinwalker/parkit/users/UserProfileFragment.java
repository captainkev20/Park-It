package com.example.kevinwalker.parkit.users;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.example.kevinwalker.parkit.R;
import com.example.kevinwalker.parkit.profiles.ParentProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileFragment extends ParentProfileActivity {

    @BindView(R.id.txt_first_name) TextView txt_first_name;
    @BindView(R.id.txt_last_name) TextView txt_last_name;
    @BindView(R.id.txt_phone_number) TextView txt_phone_number;
    @BindView(R.id.txt_email) TextView txt_email;
    @BindView(R.id.et_enter_email) EditText et_enter_email;
    @BindView(R.id.txt_name) TextView txt_name;
    @BindView(R.id.et_enter_first_name) EditText et_enter_first_name;
    @BindView(R.id.txt_rating) TextView txt_rating;
    @BindView(R.id.image_logo) CircleImageView image_logo;
    protected DrawerLayout drawer;
    protected Toolbar toolbar;
    private View mView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userDatabaseReference = database.getReference("users");
    private FirebaseAuth mAuth;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_user, container, false);
        ButterKnife.bind(this, mView);

        mAuth = FirebaseAuth.getInstance();

        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        ButterKnife.bind(this, view);

        userDatabaseReference = userDatabaseReference.child(mAuth.getUid());
        userDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);
                et_enter_email.setText(currentUser.getUserEmail());
                //et_enter_email.setText(userEmail);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        getActivity().setTitle(getResources().getString(R.string.profile_nav_title));

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_test_profile_pic);

        setPrimaryPhoto(bm);
        image_logo.setImageBitmap(getPrimaryPhoto());
    }
}
