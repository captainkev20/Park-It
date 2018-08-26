package com.example.kevinwalker.parkit.notifications;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kevinwalker.parkit.R;

import java.io.File;

import static android.app.Activity.RESULT_OK;

public class TakePictureAlertDiaglogFragment extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    static final int REQUEST_IMAGE_CAPTURE = 1515;
    private static final String TAG = TakePictureAlertDiaglogFragment.class.getName();


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TakePictureInteractionListener mListener;

    public TakePictureAlertDiaglogFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static TakePictureAlertDiaglogFragment newInstance() {
        TakePictureAlertDiaglogFragment fragment = new TakePictureAlertDiaglogFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Confirmation")
                // Set Dialog Message
                .setMessage(R.string.change_profile_pic)

                // positive button
                .setPositiveButton(R.string.positive_change_profile_pic, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.startCameraIntent();
                    }
                })
                // negative button
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.cancelChangeProfilePicture();
                    }
                });
        return builder.create();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_leave_alert_dialog, container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LeaveAlertDialogFragment.LeaveSpotInteractionListener) {
            mListener = (TakePictureAlertDiaglogFragment.TakePictureInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement LeaveSpotInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mListener.changeProfilePicture(imageBitmap);
        } else {
            Log.e(TAG, "onActivityResult");
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface TakePictureInteractionListener {
        // TODO: Update argument type and name

        void changeProfilePicture(Bitmap imageBitmap);

        void cancelChangeProfilePicture();

        void startCameraIntent();



    }
}

