package com.example.kevinwalker.parkit.users;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kevinwalker.parkit.R;
import com.example.kevinwalker.parkit.profiles.ParentProfileActivity;
import com.example.kevinwalker.parkit.vehicle.Vehicle;
import com.example.kevinwalker.parkit.badges.Badge;
import com.example.kevinwalker.parkit.payments.PaymentType;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileFragment extends ParentProfileActivity {

    @BindView(R.id.txt_name) TextView txt_name;
    @BindView(R.id.txt_rating) TextView txt_rating;
    @BindView(R.id.txt_badges) TextView txt_badges;
    @BindView(R.id.txt_score) TextView txt_score;
    @BindView(R.id.txt_score_placeholder) TextView txt_score_placholder;
    @BindView(R.id.image_logo) CircleImageView image_logo;
    @BindView(R.id.txt_rating_placeholder) TextView txt_rating_placeholder;
    @BindView(R.id.btn_user_cars) TextView btn_user_cars;
    protected DrawerLayout drawer;
    protected Toolbar toolbar;
    private View mView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_user, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        //ButterKnife.bind(UserProfileFragment.class);

        txt_name = getView().findViewById(R.id.txt_name);
        txt_rating = getView().findViewById(R.id.txt_rating);
        txt_badges = getView().findViewById(R.id.txt_badges);
        txt_score = getView().findViewById(R.id.txt_score);
        txt_score_placholder = getView().findViewById(R.id.txt_score_placeholder);
        image_logo = getView().findViewById(R.id.image_logo);
        txt_rating_placeholder = getView().findViewById(R.id.txt_rating_placeholder);
        btn_user_cars = getView().findViewById(R.id.btn_user_cars);
        txt_name.setText("Kevin");
        getActivity().setTitle("Profile");

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_test_profile_pic);

        setPrimaryPhoto(bm);
        image_logo.setImageBitmap(getPrimaryPhoto());
    }
}
