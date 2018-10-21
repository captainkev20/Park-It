package com.example.kevinwalker.parkit.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.kevinwalker.parkit.spot.Spot;
import com.example.kevinwalker.parkit.users.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

public class FirestoreHelper {

    private static final String TAG = FirestoreHelper.class.getName();
    private static final String testSpot = "0b38974e-f4b3-4988-83df-9b1b33cf6554";

    private static User currentUser = new User();

    private static Spot userSpot = new Spot();
    private static DocumentReference userDocument;
    private static DocumentReference userSpotDocument;
    private static FirestoreHelper instance;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference("spots");
    private static FirestoreHelper.OnDataUpdated mListener;


    public static FirestoreHelper getInstance() {
        if (instance != null) {
            return instance;
        } else {
            instance = new FirestoreHelper();
            return instance;
        }
    }

    public static FirestoreHelper getInstance(Context context) {
        if (instance != null) {
            return instance;
        } else {
            instance = new FirestoreHelper();
            if (context instanceof FirestoreHelper.OnDataUpdated) {
                mListener = (FirestoreHelper.OnDataUpdated) context;
            }
            return instance;
        }
    }



    private FirestoreHelper() {}

    // Needs to be called upon app initialization for the class to work properly
    public void initializeFirestore() {
        // Initialize our User DocumentReference
        userDocument = firebaseFirestore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        userDocument.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    currentUser = documentSnapshot.toObject(User.class);
                    if (currentUser.getUserUUID().trim().isEmpty()) {
                        currentUser.setUserUUID(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        mergeCurrentUserWithFirestore(currentUser);
                    }
                    if (currentUser.getUserEmail().trim().isEmpty()) {
                        currentUser.setUserEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                        mergeCurrentUserWithFirestore(currentUser);
                    }
                    mListener.onUserUpdated(currentUser);

                } else {
                    //mergeCurrentUserWithFirestore(currentUser);
                }
            }
        });
    }

    public void initializeFirestoreSpot() {
        // Initialize our Spot DocumentReference
        userSpotDocument = firebaseFirestore.collection("spots").document(testSpot);

        userSpotDocument.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    userSpot = documentSnapshot.toObject(Spot.class);
                }
            }
        });
    }

    public void mergeCurrentUserWithFirestore(User user) {
        userDocument.set(user, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    public void mergeCurrentUserWithFirestore() {
        userDocument.set(currentUser, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        currentUser = user;
    }

    public static Spot getUserSpot() { return userSpot; }

    public DatabaseReference getRef() {
        return ref;
    }

    public void setRef(DatabaseReference ref) {
        this.ref = ref;
    }

    public interface OnDataUpdated {
        void onUserUpdated(User user);
    }
}
