package com.rocks.kevinwalker.parkit.notifications;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rocks.kevinwalker.parkit.R;

public class LeaveAlertDialogFragment extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parame1ters
    private String mParam1;
    private String mParam2;

    private LeaveSpotInteractionListener mListener;

    public LeaveAlertDialogFragment() {}

    // TODO: Rename and change types and number of parameters
    public static LeaveAlertDialogFragment newInstance(String param1, String param2) {
        LeaveAlertDialogFragment fragment = new LeaveAlertDialogFragment();

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
        builder.setTitle(getResources().getString(R.string.LEAVE_DIALOG_TITLE))
                .setMessage(getResources().getString(R.string.LEAVE_DIALOG_MESSAGE))
                .setPositiveButton(getResources().getString(R.string.LEAVE_DIALOG_POSITIVE_BUTTON_TEXT), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.leaveSpace();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.LEAVE_DIALOG_NEGATIVE_BUTTON_TEXT), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.userStayInSpace();
                    }
                });
        return builder.create();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_leave_alert_dialog, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LeaveSpotInteractionListener) {
            mListener = (LeaveSpotInteractionListener) context;
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

    public interface LeaveSpotInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

        void userStayInSpace();

        void leaveSpace();
    }
}
