package com.example.kevinwalker.parkit.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.kevinwalker.parkit.users.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

public class FirestoreHelper {

    private static final String TAG = FirestoreHelper.class.getName();

    private static User currentUser = new User();
    private static DocumentReference userDocument;
    private static FirestoreHelper instance;
    private static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public static FirestoreHelper getInstance() {
        if (instance != null) {
            return instance;
        } else {
            instance = new FirestoreHelper();
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
                } else {
                    mergeCurrentUserWithFirestore(currentUser);
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
}
