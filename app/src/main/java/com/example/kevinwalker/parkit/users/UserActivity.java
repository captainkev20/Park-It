package com.example.kevinwalker.parkit.users;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kevinwalker.parkit.R;
import com.example.kevinwalker.parkit.vehicle.Vehicle;
import com.example.kevinwalker.parkit.badges.Badge;
import com.example.kevinwalker.parkit.payments.PaymentType;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserActivity extends AppCompatActivity {

    @BindView(R.id.txt_name) TextView txt_name;
    @BindView(R.id.txt_rating) TextView txt_rating;
    @BindView(R.id.txt_badges) TextView txt_badges;
    @BindView(R.id.txt_score) TextView txt_score;
    @BindView(R.id.txt_score_placeholder) TextView txt_score_placholder;
    @BindView(R.id.txt_address) TextView txt_address;
    @BindView(R.id.txt_license) TextView txt_license;
    @BindView(R.id.txt_vehicle) TextView txt_vehicle;
    @BindView(R.id.image_logo) CircleImageView image_logo;
    @BindView(R.id.txt_rating_placeholder) TextView txt_rating_placeholder;
    @BindView(R.id.btn_user_cars) TextView btn_user_cars;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        ButterKnife.bind(this);


    }
}
