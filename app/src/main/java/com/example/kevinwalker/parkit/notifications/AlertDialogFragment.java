package com.example.kevinwalker.parkit.notifications;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kevinwalker.parkit.R;

public class AlertDialogFragment extends DialogFragment {

    private AlertDialogFragmentInteractionListener mListener;
    public static final String TYPE = "type";
    public static final String DIALOG_MESSAGE = "dialogMessage";
    public static final String ALERT_DIALOG_TYPE_EDIT_PROFILE_PICTURE = "editProfilePicture";
    public static final String ALERT_DIALOG_TYPE_LOGOFF = "logOff";
    public static final String ALERT_DIALOG_TYPE_PHONE_CALL = "phoneCall";
    public static final String TAG_ALERT_DIALOG_FRAGMENT = "ALERT_DIALOG";
    private Bundle bundle;

    public AlertDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AlertDialogFragment.
     */
    public static AlertDialogFragment newInstance() {
        AlertDialogFragment fragment = new AlertDialogFragment();
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("You up out dis bitch?")
                .setPositiveButton("Fo' sho", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onPositiveClick();
                    }
                })
                .setNegativeButton("Nah", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onNegativeClick();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alert_dialog, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AlertDialogFragmentInteractionListener) {
            mListener = (AlertDialogFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement AlertDialogFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface AlertDialogFragmentInteractionListener {
        void onPositiveClick();

        void onNegativeClick();
    }
}