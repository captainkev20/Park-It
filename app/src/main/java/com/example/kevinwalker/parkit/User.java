package com.example.kevinwalker.parkit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.UUID;

public class User extends AppCompatActivity {

    private String firstName;
    private String lastName;
    private UUID userUUID;
    private Address[] address;
    private License license;
    private ImageView avatar;
    private Badge[] badges;
    private Long userScore;
    private Car userCar;
    private Vehicle[] vehicles;
    private PaymentType[] paymentTypes;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);


    }
}
