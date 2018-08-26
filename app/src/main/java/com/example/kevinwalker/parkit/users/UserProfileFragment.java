package com.example.kevinwalker.parkit.users;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.example.kevinwalker.parkit.R;
import com.example.kevinwalker.parkit.notifications.TakePictureAlertDiaglogFragment;
import com.example.kevinwalker.parkit.profiles.ParentProfileFragment;
import com.example.kevinwalker.parkit.utils.CustomTextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import static android.app.Activity.RESULT_OK;

public class UserProfileFragment extends ParentProfileFragment implements View.OnClickListener, TakePictureAlertDiaglogFragment.TakePictureInteractionListener {

    @BindView(R.id.image_logo) CircleImageView image_logo;
    @BindView(R.id.ic_phone) CustomTextView ic_phone;
    @BindView(R.id.ic_email) CustomTextView ic_email;
    @BindView(R.id.txt_first_name) TextView txt_first_name;
    @BindView(R.id.txt_phone_number) TextView txt_phone_number;
    @BindView(R.id.txt_email) TextView txt_email;
    @BindView(R.id.rating_bar) RatingBar rating_bar;

    @BindView(R.id.edit_image_logo) CircleImageView edit_image_logo;
    @BindView(R.id.et_phone_number) EditText et_phone_number;
    @BindView(R.id.et_email) EditText et_email;
    @BindView(R.id.et_last_name2) EditText et_last_name2;
    @BindView(R.id.et_first_name2) EditText et_first_name2;
    @BindView(R.id.profile_view_switcher) ViewSwitcher profile_view_switcher;
    @BindView(R.id.txt_edit_user_profile) TextView txt_edit_user_profile;
    @BindView(R.id.txt_save_profile) TextView txt_save_profile;
    @BindView(R.id.edit_profile_card_view) CardView edit_profile_card_view;
    @BindView(R.id.main_profile_card_view) CardView main_profile_card_view;

    private static final String TAG = UserProfileFragment.class.getName();
    static final int REQUEST_IMAGE_CAPTURE = 1;
    protected DrawerLayout drawer;
    protected Toolbar toolbar;
    private View mView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userDatabaseReference = database.getReference("users");
    private ValueEventListener valueEventListener;
    private FirebaseAuth mAuth;
    private User currentUser;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userDatabaseReference = userDatabaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid());


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_user, container, false);
        ButterKnife.bind(this, mView);

        txt_edit_user_profile.setOnClickListener(this);
        txt_save_profile.setOnClickListener(this);

        edit_image_logo.setImageURI(Uri.parse(getActivity().getFilesDir().getAbsolutePath() + "/" + getFirebaseUser().getPhotoUrl()));

        edit_image_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TakePictureAlertDiaglogFragment takePictureAlertDiaglogFragment = new TakePictureAlertDiaglogFragment();
                takePictureAlertDiaglogFragment.show(getActivity().getFragmentManager(), TAG);
            }
        });

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Log.i(TAG, dataSnapshot.getValue().toString());

                if (dataSnapshot.getValue(User.class) != null) {
                    currentUser = dataSnapshot.getValue(User.class);
                    updateUI();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, "Failed to write");
            }
        };

        mAuth = FirebaseAuth.getInstance();

        return mView;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_edit_user_profile:
                profile_view_switcher.showNext();
                break;

            case R.id.txt_save_profile:
                String firstNameString = et_first_name2.getText().toString();
                String lastNameString = et_last_name2.getText().toString();
                String userEmail = et_email.getText().toString();
                String phoneNum = et_phone_number.getText().toString();

                // TODO: Data input validation
                currentUser.setFirstName(firstNameString);
                currentUser.setLastName(lastNameString);
                currentUser.setUserEmail(userEmail);
                currentUser.setUserPhone(phoneNum);

                userDatabaseReference.setValue(currentUser);

                profile_view_switcher.showPrevious();
                break;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        ButterKnife.bind(this, view);

        getActivity().setTitle(getResources().getString(R.string.profile_nav_title));

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_test_profile_pic);

        setPrimaryPhoto(bm);
        image_logo.setImageBitmap(getPrimaryPhoto());
    }

    private FirebaseUser getFirebaseUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    private void updateUserPhoto(File file, FirebaseUser firebaseUser) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(file.getName()))
                .build();

        firebaseUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                            Toast.makeText(getActivity(), "Saved new profile photo", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "Failed to updated FirebaseUser Profile Picture");
                            Toast.makeText(getActivity(), "Update failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void refreshProfilePicture(Bitmap bitmap) {
        edit_image_logo.setImageBitmap(bitmap);
    }

    private File getProfilePictureFile(Bitmap bitmap) {
        String filename = getActivity().getFilesDir().getAbsolutePath() + "/" + getFirebaseUser().getUid() + "_profile_picture.png";
        File dest = new File(filename);

        try {
            FileOutputStream out = new FileOutputStream(dest);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dest;
    }



    @Override
    public void changeProfilePicture(Bitmap imageBitMap) {
        refreshProfilePicture(imageBitMap);
        File file = getProfilePictureFile(imageBitMap);
        updateUserPhoto(file, getFirebaseUser());
    }

    @Override
    public void cancelChangeProfilePicture() {
        // No implementation
    }

    @Override
    public void startCameraIntent() {
        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            refreshProfilePicture(imageBitmap);
            File file = getProfilePictureFile(imageBitmap);
            updateUserPhoto(file, getFirebaseUser());
        } else {
            Log.e(TAG, "onActivityResult");
        }
    }

    private class GetProfileAsyncTask extends AsyncTask<String, Integer, String> {

        ProgressBar progressBar;
        File profilePictureFile;
        Context context;
        URL url = null;
        int downloadSize = 0;
        int bytesDownloaded = 0;
        int readTimeout = 5000;
        int connectionTimeout = 5000;

        GetProfileAsyncTask(Context context) {
            this.context = context;
        }

        /**
         * Before starting background thread
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Declare the path for the File we're going to download and save
            String profilePictureFilePath = getActivity().getFilesDir().getAbsolutePath() + "/profilePicture.jpg";
            try {
                // Create the File we're going to write to
                profilePictureFile = new File(profilePictureFilePath);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... stringURL) {
            try {
                // Attempt to get the size of the download
                downloadSize = connectToURL(stringURL[0]);

                publishProgress(downloadSize);

                // Input stream to read file - with 8k buffer (the default size of a BufferedInputStream)
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream to write file
                FileOutputStream output = new FileOutputStream(profilePictureFile);

                // Create an array of bytes with size 2KB
                byte[] buffer = new byte[2048];

                // When numBytesRead = -1, we have reached the end of the InputStream
                int numBytesRead;

                // While there is data to be read, read that data into the buffer
                while ((numBytesRead = input.read(buffer, 0, buffer.length)) != -1) {
                    /*
                     Write the data contained in the byte[] "buffer"
                     Offset by nothing, start reading the "buffer" at index 0
                     Read no more than "numBytesRead", which will be equal to the number of bytes actually read in the while statement above
                      */
                    output.write(buffer, 0, numBytesRead);
                    updateProgressBar(numBytesRead);
                }

                // Flushing output - forces any remaining bytes in the FileOutputStream buffer to be written
                output.flush();

                // Closing streams - releases any resources associated with the streams
                output.close();
                input.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            } finally {

            }

            return null;
        }

        // Allows us to work on objects in the UIThread
        // Attempting to work on objects created by the UIThread without using this method will produce an error
        // Update the progress displayed in the ProgressBar here
        @Override
        protected void onProgressUpdate(Integer... values) {
            prepareIndeterminateProgressBar(downloadSize);
        }

        /**
         * After completing background task
         **/
        @Override
        protected void onPostExecute(String file_url) {
            // Remove the ProgressBar from the layout
            progressBar.setVisibility(View.GONE);

            // Display the downloaded picture to the user
            edit_image_logo.setImageURI(Uri.fromFile(profilePictureFile));
        }

        // Return true if ProgressBar is indeterminate
        private boolean prepareIndeterminateProgressBar(int downloadSize) {
            boolean isIndeterminate = true;
            if (downloadSize > 0) {
                isIndeterminate = false;
                progressBar.setProgress(0);
            }
            progressBar.setIndeterminate(isIndeterminate);
            progressBar.setVisibility(View.VISIBLE);
            return isIndeterminate;
        }

        private void updateProgressBar(int bytesRead) {
            bytesDownloaded += bytesRead;
            progressBar.setProgress((downloadSize / bytesDownloaded) * 100);
        }

        private URL getURLFromString(String stringURL) {
            try {
                url = new URL(stringURL);
            } catch (MalformedURLException e) {
                Log.e(TAG, e.getMessage());
            }
            return url;
        }

        private int connectToURL(String stringURL) {
            int contentLength = 0;
            // Create a URL from the first String parameter
            url = getURLFromString(stringURL);
            // Open a connection to that URL
            if (url != null) {
                try {
                    URLConnection connection = url.openConnection();
                    connection.setReadTimeout(readTimeout);
                    connection.setConnectTimeout(connectionTimeout);
                    connection.connect();
                    contentLength = connection.getContentLength();
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }

            // Return the contentLength or 0 if we couldn't get the length
            if (contentLength != -1 && contentLength < Integer.MAX_VALUE) {
                return contentLength;
            } else {
                return 0;
            }
        }
    }

    private void updateUI() {
        if (currentUser != null) {
            txt_first_name.setText(currentUser.getFirstName());
            txt_email.setText(currentUser.getUserEmail());
            txt_phone_number.setText(currentUser.getUserPhone());
            et_phone_number.setText(currentUser.getUserPhone());
            et_email.setText(currentUser.getUserEmail());
            et_last_name2.setText(currentUser.getLastName());
            et_first_name2.setText(currentUser.getFirstName());
        }
    }



    @Override
    public void onStop() {
        super.onStop();
        userDatabaseReference.removeEventListener(valueEventListener);
    }

}
