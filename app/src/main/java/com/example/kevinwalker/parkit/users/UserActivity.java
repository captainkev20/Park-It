package com.example.kevinwalker.parkit.users;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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

public class UserActivity extends ParentProfileActivity {

    @BindView(R.id.txt_name) TextView txt_name;
    @BindView(R.id.txt_rating) TextView txt_rating;
    @BindView(R.id.txt_badges) TextView txt_badges;
    @BindView(R.id.txt_score) TextView txt_score;
    @BindView(R.id.txt_score_placeholder) TextView txt_score_placholder;
    @BindView(R.id.image_logo) CircleImageView image_logo;
    @BindView(R.id.txt_rating_placeholder) TextView txt_rating_placeholder;
    @BindView(R.id.btn_user_cars) TextView btn_user_cars;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        ButterKnife.bind(this);

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_test_profile_pic);

        setPrimaryPhoto(bm);
        image_logo.setImageBitmap(getPrimaryPhoto());


        txt_name.setText("Kevin");

    }
}
