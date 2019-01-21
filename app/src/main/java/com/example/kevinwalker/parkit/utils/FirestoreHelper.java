package com.example.kevinwalker.parkit.utils;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.kevinwalker.parkit.payments.StripeCustomer;
import com.example.kevinwalker.parkit.spot.Spot;
import com.example.kevinwalker.parkit.users.User;
import com.example.kevinwalker.parkit.vehicle.Vehicle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class FirestoreHelper {

    private static final String TAG = FirestoreHelper.class.getName();
    private static final String testSpot = "0b38974e-f4b3-4988-83df-9b1b33cf6554";
    private static final String testVehicle = "86b2a124-e59c-4387-8dff-3a7200957cae";
    private static final String testStripe = "9HjucfCGRlm5CGz4pYdo";

    private static User currentUser = new User();
    private static StripeCustomer stripeCustomer = new StripeCustomer();
    private static Spot userSpot = new Spot();
    private static Vehicle userVehicle = new Vehicle();

    private static DocumentReference userDocument;
    private static DocumentReference userSpotDocument;
    private static DocumentReference userVehicleDocument;
    private static DocumentReference stripeCustomers;

    private static FirestoreHelper instance;

    private static StorageReference filePath;

    private ArrayList<Spot> allSpots = new ArrayList<>();
    private ArrayList<Vehicle> allVehicles = new ArrayList<>();

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

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userDocument = firebaseFirestore.collection("users")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid());
            userDocument.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (documentSnapshot.exists()) {
                        currentUser = documentSnapshot.toObject(User.class);
                        mergeCurrentUserWithFirestore();
                        mListener.onUserUpdated(currentUser);
                    }
                }
            });

            userDocument.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        currentUser = documentSnapshot.toObject(User.class);
                        if (currentUser.getUserUUID().trim().isEmpty()) {
                            currentUser.setUserUUID(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            mergeCurrentUserWithFirestore();
                        }
                        if (currentUser.getUserEmail().trim().isEmpty()) {
                            currentUser.setUserEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                            mergeCurrentUserWithFirestore();
                        }
                        mListener.onUserUpdated(currentUser);

                    } else {
                        // TODO: Ask Hollis why would we merge if the snapshot does not exist?
                        mergeCurrentUserWithFirestore();
                    }
                }
            });
        }

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

    public void initializeFirestoreVehicle() {
        userVehicleDocument = firebaseFirestore.collection("vehicles")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        userVehicleDocument.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    userVehicle = documentSnapshot.toObject(Vehicle.class);
                }
            }
        });
    }

    public void initializeFirestoreStripeCustomer() {
        stripeCustomers = firebaseFirestore.collection("stripe_customers").document(testStripe);

        stripeCustomers.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    stripeCustomer = documentSnapshot.toObject(StripeCustomer.class);
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
        if (FirestoreHelper.getInstance().getCurrentUser() != null && userDocument != null) {
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

    }

    public void mergeStripeCustomerWithFirestore() {
        stripeCustomers.set(stripeCustomer, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Token successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Token failed to write", e);
            }
        });
    }


    // TODO: Review with Hollis and determine if this is best way to handle
    public StorageReference getUserProfilePhotoFromFirebase() {
        filePath = FirebaseStorage.getInstance().getReference()
                .child("UserProfilePhotos/")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid() + "_profile_picture.png");
        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d(TAG, "Image successfully retrieved!");

                mListener.profilePictureUpdated(uri);
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Image failed to retrieve!");
            }
        });

        return filePath;
    }

    public StorageReference getUserNavProfileHeaderFromFirebase() {
        filePath = FirebaseStorage.getInstance().getReference().child("UserProfilePhotos/")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid() + "_profile_picture.png");
        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d(TAG, "Image successfully retrieved!");

                mListener.navHeaderProfilePictureUpdated(uri);
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Image failed to retrieve!");
            }
        });

        return filePath;
    }

    public ArrayList<Spot> getAllSpots() {
        FirebaseFirestore.getInstance().collection("spots")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        allSpots.add(document.toObject(Spot.class));
                    }
                    mListener.onAllSpotsUpdated(allSpots);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"Failed to fetch");
            }
        });

        return allSpots;
    }

    public ArrayList<Vehicle> getAllVehicles() {
        FirebaseFirestore.getInstance().collection("vehicles")
                .whereEqualTo("vehicleName","My Acura")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                allVehicles.add(documentSnapshot.toObject(Vehicle.class));
                            }

                            mListener.onAllVehiclesUpdated(allVehicles);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"Failed to fetch");
            }
        });


        return allVehicles;

    }

    public static void logOff() {
        currentUser = null;
        userDocument = null;
        userVehicle = null;
        userVehicleDocument = null;
        userSpot = null;
        stripeCustomers = null;
        filePath = null;
        stripeCustomer = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        currentUser = user;
    }

    public Spot getUserSpot() { return userSpot; }

    public Vehicle getUserVehicle() { return userVehicle; }

    public StripeCustomer getStripeCustomer() { return stripeCustomer; }

    public void setStripeUser(StripeCustomer stripeUser) { stripeCustomer = stripeUser; }

    public DatabaseReference getRef() { return ref; }

    public void setRef(DatabaseReference ref) { this.ref = ref; }

    public interface OnDataUpdated {
        void onUserUpdated(User user);
        void onAllSpotsUpdated(ArrayList<Spot> spots);
        void onAllVehiclesUpdated(ArrayList<Vehicle> vehicles);
        void profilePictureUpdated(Uri filePath);
        void navHeaderProfilePictureUpdated(Uri filePath);
    }
}
