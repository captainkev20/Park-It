package com.example.kevinwalker.parkit.profiles;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.kevinwalker.parkit.R;
import com.google.android.gms.maps.model.LatLng;

public class ParentProfileActivity extends AppCompatActivity {


    private Bitmap primaryPhoto;
    private String toolbarTitle;
    private LatLng profileObjectLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_profile);
    }

    public Bitmap getPrimaryPhoto() {
        return primaryPhoto;
    }

    protected void setPrimaryPhoto(Bitmap primaryPhoto) {
        this.primaryPhoto = primaryPhoto;
    }

    public String getToolbarTitle() {
        return toolbarTitle;
    }

    protected void setToolbarTitle(String toolbarTitle) {
        this.toolbarTitle = toolbarTitle;
    }

    public LatLng getProfileObjectLatLng() {
        return profileObjectLatLng;
    }

    protected void setProfileObjectLatLng(LatLng profileObjectLatLng) {
        this.profileObjectLatLng = profileObjectLatLng;
    }
}
