package com.example.kevinwalker.parkit.profiles;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kevinwalker.parkit.NavDrawer;
import com.example.kevinwalker.parkit.R;
import com.google.android.gms.maps.model.LatLng;

public class ParentProfileActivity extends android.support.v4.app.Fragment {


    private Bitmap primaryPhoto;
    private String toolbarTitle;
    private LatLng profileObjectLatLng;
    private View mView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_parent_profile);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_parent_profile, container, false);
        return mView;
    }

    public Bitmap getPrimaryPhoto() {
        return primaryPhoto;
    }

    protected void setPrimaryPhoto(Bitmap primaryPhoto) {
        this.primaryPhoto = primaryPhoto;
    }

    public String getToolbarTitle() { return toolbarTitle; }

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
