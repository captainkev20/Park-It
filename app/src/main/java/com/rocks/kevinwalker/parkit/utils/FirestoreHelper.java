package com.rocks.kevinwalker.parkit.utils;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.rocks.kevinwalker.parkit.payments.Payment;
import com.rocks.kevinwalker.parkit.spot.Spot;
import com.rocks.kevinwalker.parkit.users.User;
import com.rocks.kevinwalker.parkit.vehicle.Vehicle;
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
import java.util.UUID;

import javax.annotation.Nullable;

public class FirestoreHelper {

    private static final String TAG = FirestoreHelper.class.getName();
    private static final String testSpot = "0b38974e-f4b3-4988-83df-9b1b33cf6554";
    private static final String testVehicle = "86b2a124-e59c-4387-8dff-3a7200957cae";
    private static final String testStripe = "9HjucfCGRlm5CGz4pYdo";

    private static User currentUser = new User();
    private static Payment userPayment = new Payment();
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
    private ArrayList<Spot> mapSpots = new ArrayList<>();
    private ArrayList<Payment> allPayments = new ArrayList<>();

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
                    if (e != null) {
                        Log.d(TAG, "Listener failed");
                        return;
                    }

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
        userSpotDocument = firebaseFirestore.collection("spots").document(String.valueOf(UUID.randomUUID()));

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
        stripeCustomers = firebaseFirestore.collection("stripe_customers")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        stripeCustomers.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    userPayment = documentSnapshot.toObject(Payment.class);
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
        stripeCustomers.set(userPayment, SetOptions.merge())
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

    public void mergeVehicleWithFirestore(Vehicle userVehicle) {
        userVehicleDocument.set(userVehicle, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Successful write");
                Toast.makeText((Context) mListener, "Vehicle Saved!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failed to write");
                Toast.makeText((Context) mListener, "Vehicle Not Saved!", Toast.LENGTH_SHORT).show();
            }
        });
    }

//    // TODO: Review with Hollis on how to handle multiple vehicles
//    public void mergeVehicleWithFirestore(Map<String,Vehicle> userVehicle) {
//        userVehicleDocument.set(userVehicle).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Toast.makeText((Context) mListener, "Vehicle Saved!", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    public void mergeSpotWithFirestore(Spot userSpot) {
        userSpotDocument.set(userSpot, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Successful write");
                Toast.makeText((Context) mListener, "Spot Saved!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failed to write");
                Toast.makeText((Context) mListener, "Spot Not Saved!", Toast.LENGTH_SHORT).show();
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

                    // Clear array list - fixes RecyclerView issue of duplicating list items

                    allSpots.clear();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        allSpots.add(document.toObject(Spot.class));
                    }
                    mListener.onAllSpotsUpdated(allSpots);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"Failed to fetch spots");
            }
        });

        return allSpots;
    }

    public ArrayList<Payment> getAllUserPayments() {
        FirebaseFirestore.getInstance().collection("stripe_customers")
                .whereEqualTo("paymentUserUUID",FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    allPayments.clear();

                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        allPayments.add(documentSnapshot.toObject(Payment.class));
                    }
                    mListener.onAllPaymentsUpdated(allPayments);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failed to fetch payments");
            }
        });

        return allPayments;
    }

    public ArrayList<Spot> getSpotForMap() {
        FirebaseFirestore.getInstance().collection("spots")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        mapSpots.add(documentSnapshot.toObject(Spot.class));
                    }
                    mListener.onAllMapSpotsUpdated(mapSpots);
                }
            }
        });

        return mapSpots;
    }

    public ArrayList<Vehicle> getAllVehicles() {
        FirebaseFirestore.getInstance().collection("vehicles")
                .whereEqualTo("vehicleUUID",FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        allVehicles.clear();

                        if (queryDocumentSnapshots.isEmpty()) {
                            return;
                        } else {
                            allVehicles.addAll(queryDocumentSnapshots.toObjects(Vehicle.class));
                        }

                        mListener.onAllVehiclesUpdated(allVehicles);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failed to fetch");
            }
        });

        return allVehicles;
    }

    // TODO: Get working with Holis to get all vehicles out. Maybe use GSON to convert Map to POJO
//    public ArrayList<Vehicle> getAllVehicles() {
//        FirebaseFirestore.getInstance().collection("vehicles")
//                //.whereEqualTo("vehicleUUID",FirebaseAuth.getInstance().getCurrentUser().getUid())
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
//                                Map<String,Object> map = (Map<String,Object>) documentSnapshot.getData();
//                                for (Map.Entry<String,Object> entry : map.entrySet()) {
//                                    System.out.print(entry.getKey() + "/" + entry.getValue());
//
//                                }
//                            }
//                        }
//                    }
//                });
//
//        return allVehicles;
//
//    }

    public static void logOff() {
        currentUser = null;
        userDocument = null;
        userVehicle = null;
        userVehicleDocument = null;
        userSpot = null;
        stripeCustomers = null;
        filePath = null;
        userPayment = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        currentUser = user;
    }

    public Spot getUserSpot() { return userSpot; }

    public Payment getUserPayment() { return userPayment; }

    public Vehicle getUserVehicle() { return userVehicle; }

    public Payment getStripeCustomer() { return userPayment; }

    public void setStripeUser(Payment stripeUser) { userPayment = stripeUser; }

    public DocumentReference getSpotRef() { return userSpotDocument; }

    public DatabaseReference getRef() { return ref; }

    public void setRef(DatabaseReference ref) { this.ref = ref; }

    public interface OnDataUpdated {
        void onUserUpdated(User user);
        void onAllSpotsUpdated(ArrayList<Spot> spots);
        void onAllVehiclesUpdated(ArrayList<Vehicle> vehicles);
        void profilePictureUpdated(Uri filePath);
        void navHeaderProfilePictureUpdated(Uri filePath);
        void onAllMapSpotsUpdated(ArrayList<Spot> mapSpots);
        void onAllPaymentsUpdated(ArrayList<Payment> payments);
    }
}
