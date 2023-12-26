package com.dr34mt34m.v1ll4g3pl4nn3r.dataFetchers;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.dr34mt34m.v1ll4g3pl4nn3r.components.Place;
import com.dr34mt34m.v1ll4g3pl4nn3r.components.Reminder;
import com.dr34mt34m.v1ll4g3pl4nn3r.components.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class FirebaseHelper {
    private static final String TAG = FirebaseHelper.class.getName();
    // Create a storage reference from our app
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static FirebaseStorage storage = FirebaseStorage.getInstance();
    private static FirebaseAuth auth = FirebaseAuth.getInstance();

    public static Map<String, Place> places = new HashMap<>();

    public static void updateUserInfo(String firstName, String lastName, String email, Uri photoUri) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            Log.d(TAG, "Photo uri: " + photoUri);
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(firstName + " " + lastName)
                    .setPhotoUri(photoUri)
                    .build();

            user.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "User profile updated");
                }
            });
            //User currentUser = new User(firstName, lastName, email, photoUri);
            //createUser(currentUser);
        }
    }

    public static void updateProfilePicture(Uri photoUri) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            Log.d(TAG, "Photo uri: " + photoUri);
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(photoUri)
                    .build();

            user.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "User profile updated");
                }
            });
        }
    }


    public static Task<DocumentReference> createReminderAsync(Reminder reminder) {
        // interface with Firebase API to send this data to Firestore DB
        Map<String, Object> reminderMap = new HashMap<>();
        reminderMap.put("place", reminder.getPlace());
        reminderMap.put("message", reminder.getMessage()); //        reminderMap.put("year", reminder.getYear()); //        reminderMap.put("month", reminder.getMonth());
//        reminderMap.put("day", reminder.getDay());
//        reminderMap.put("hour", reminder.getHour());
//        reminderMap.put("minute", reminder.getMinute());
        reminderMap.put("timestamp", reminder.getTimestamp());
        String userId = auth.getCurrentUser().getUid();
        System.out.println("userID from FirebaseHelper (createReminder): " + userId);
        final String[] reminderId = new String[1];
        // get the current user
        // add this data to the overall reminders collection in firestore
        return db.collection("user/" + userId + "/reminder")
                .add(reminderMap);
    }


    public static String[] createReminder(Reminder reminder) {
        // interface with Firebase API to send this data to Firestore DB
        Map<String, Object> reminderMap = new HashMap<>();
        reminderMap.put("place", reminder.getPlace());
        reminderMap.put("message", reminder.getMessage()); //        reminderMap.put("year", reminder.getYear()); //        reminderMap.put("month", reminder.getMonth());
//        reminderMap.put("day", reminder.getDay());
//        reminderMap.put("hour", reminder.getHour());
//        reminderMap.put("minute", reminder.getMinute());
        reminderMap.put("timestamp", reminder.getTimestamp());
        String userId = auth.getCurrentUser().getUid();
        System.out.println("userID from FirebaseHelper (createReminder): " + userId);
        System.out.println("User email from FirebaseHelper (createReminder): " + auth.getCurrentUser().getEmail());
        final String[] reminderId = new String[1];
        // get the current user
        // add this data to the overall reminders collection in firestore
        db.collection("user/" + userId + "/reminder")
                .add(reminderMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        System.out.println("Added reminder with ID: " + documentReference.getId());
                        reminderId[0] = documentReference.getId();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Couldn't create this reminder");
                        Log.w(TAG, "Error adding document", e);
                    }
                });
        return reminderId;
    }

    public static void getReminders(Function<List<Reminder>, Void> f) {
        List<Reminder> ret = new ArrayList<Reminder>();
        String userId = auth.getCurrentUser().getUid();
        db.collection("user").document(userId).collection("reminder")
                .orderBy("timestamp")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                System.out.println(document.getId() + " => " + document.getData());
                                System.out.println(document.getData());

                                String remId = document.getId();
                                HashMap<String, Object> remdata = (HashMap<String, Object>) document.getData();
                                Reminder rem = new Reminder((String) remdata.get("place"),
                                        (String) remdata.get("message"),
                                        (Timestamp) remdata.get("timestamp"));
                                rem.setId(remId);
                                ret.add(rem);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        f.apply(ret);
                    }
                });
    }

    public static Task<QuerySnapshot> getRemindersAsync() {
        List<Reminder> ret = new ArrayList<Reminder>();
        String userId = auth.getCurrentUser().getUid();
        return db.collection("user").document(userId).collection("reminder")
                .orderBy("timestamp")
                .get();
    }

    public static void deleteReminder(String reminderId) {
        String userId = auth.getCurrentUser().getUid();
        db.collection("user").document(userId).collection("reminder").document(reminderId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

    public static Task<Void> deleteReminderAsync(String reminderId) {
        String userId = auth.getCurrentUser().getUid();
        System.out.println("User id from firebase helper: " + userId);
        return db.collection("user").document(userId).collection("reminder").document(reminderId)
                .delete();
    }

    public static Task<DocumentSnapshot> getReminderAsync(String[] reminderId) {
        String userId = auth.getCurrentUser().getUid();
        Reminder fetchedReminder = new Reminder();
        for (String element : reminderId) {
            System.out.println("reminderId from firebase helper (getReminder): " + element);
        }
        return db.collection("user").document(userId).collection("reminder").document(reminderId[0])
                .get();
    }

    public static void updateReminder(Reminder reminder) {
        Map<String, Object> reminderMap = new HashMap<>();
        reminderMap.put("place", reminder.getPlace());
        reminderMap.put("message", reminder.getMessage());
        reminderMap.put("timestamp", reminder.getTimestamp());
        String userId = auth.getCurrentUser().getUid();
        db.collection("user").document(userId).collection("reminder").document(reminder.getId())
                .update(reminderMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }

    public static Task<Void> updateReminderAsync(Reminder reminder) {
        Map<String, Object> reminderMap = new HashMap<>();
        reminderMap.put("place", reminder.getPlace());
        reminderMap.put("message", reminder.getMessage());
        reminderMap.put("timestamp", reminder.getTimestamp());
        System.out.println("ReminderId from Firebase helper: " + reminder.getId());
        String userId = auth.getCurrentUser().getUid();
        return db.collection("user").document(userId).collection("reminder").document(reminder.getId())
                .update(reminderMap);
    }


    public static void createUser(User currentUser) {
        // add the user's info into the cloud firestore database
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("first_name", currentUser.getFirstName());
        userMap.put("last_name", currentUser.getLastName());
        userMap.put("profile_picture", currentUser.getProfilePic());
        String userId = auth.getCurrentUser().getUid();
        db.collection("user").document(userId)
                .set(userMap)
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

    public static Task<Void> createUserAsync(User currentUser) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("first_name", currentUser.getFirstName());
        userMap.put("last_name", currentUser.getLastName());
        userMap.put("profile_picture", currentUser.getProfilePic());
        String userId = auth.getCurrentUser().getUid();
        return db.collection("user").document(userId).set(userMap);
    }

    public static Task<QuerySnapshot> getUsersAsync() {
        return db.collection("user").get();
    }

    public static Task<DocumentSnapshot> getUserAsync(String userId) {
        return db.collection("user").document(userId).get();
    }

    public static Task<Void> updateUserAsync(User user) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("first_name", user.getFirstName());
        userMap.put("last_name", user.getLastName());
        userMap.put("profile_picture", user.getProfilePic());

        return db.collection("user").document(user.getUserId()).update(userMap);
    }

    public static void updatePlace(Place place) {
        // add the place's info into the cloud firestore database
        Map<String, Object> placeMap = new HashMap<>();
        placeMap.put("cur_people", place.getCurPeople());
        placeMap.put("location", place.getLocation());
        placeMap.put("name", place.getName());
        placeMap.put("wait_time_multiplier", place.getWaitMultiplier());
        db.collection("place").document(place.getId())
                .update(placeMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }

    public static Task<Void> updatePlaceAsync(Place place) {
        // add the place's info into the cloud firestore database
        Map<String, Object> placeMap = new HashMap<>();
        placeMap.put("cur_people", place.getCurPeople());
        GeoPoint geoPoint = new GeoPoint(place.getLocation().latitude, place.getLocation().longitude);
        placeMap.put("location", geoPoint);
        placeMap.put("name", place.getName());
        placeMap.put("wait_time_multiplier", place.getWaitMultiplier());
        return db.collection("place").document(place.getId())
                .update(placeMap);
    }

    public static Map<String, Place> getPlaces() {
        return places;
    }

    public static void loadPlaces(Function<Map<String, Place>, Void> callback) {
        places = new HashMap<>();
        db.collection("place")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                System.out.println(document.getId() + " => " + document.getData());

                                String id = document.getId();
                                String name = document.getString("name");
                                GeoPoint locationGeoPoint = document.getGeoPoint("location");
                                LatLng location = new LatLng(
                                        locationGeoPoint.getLatitude(),
                                        locationGeoPoint.getLongitude()
                                );
                                // get the list of all people at a current location
                                List<String> curPeople = (List<String>) document.get("cur_people");
                                double waitMultiplier = document.getDouble("wait_time_multiplier");

                                Place p = new Place(id, name, location, curPeople, waitMultiplier);
                                places.put(name, p);
                            }

                            try {
                                callback.apply(places);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public static Task<QuerySnapshot> loadPlacesAsync() {
        places = new HashMap<>();
        return db.collection("place")
                .get();
    }

    public static void getPlace(Place place) {
        db.collection("place").document(place.getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            System.out.println(task.getResult().getId() + " => " + task.getResult().getData());
                        } else {
                            Log.d(TAG, "Error getting document: " + place.getId() + " " + place.getName(), task.getException());
                        }
                    }
                });
    }

    public static Task<DocumentSnapshot> getPlaceAsync(String placeId) {
        return db.collection("place").document(placeId)
                .get();
    }

    public static Task<Void> deletePlaceAsync(String placeId) {
        return db.collection("place").document(placeId)
                .delete();
    }

    public static void createPlace(Place place) {
        Map<String, Object> placeMap = new HashMap<>();
        placeMap.put("name", place.getName());
        placeMap.put("cur_people", place.getCurPeople());
        placeMap.put("wait_time_multiplier", place.getWaitMultiplier());

        LatLng locationLatLng = place.getLocation();
        GeoPoint location = new GeoPoint(locationLatLng.latitude, locationLatLng.longitude);
        placeMap.put("location", place.getLocation());


        db.collection("place")
                .add(placeMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public static Task<DocumentReference> createPlaceAsync(Place place) {
        Map<String, Object> placeMap = new HashMap<>();
        placeMap.put("name", place.getName());
        placeMap.put("cur_people", place.getCurPeople());
        placeMap.put("wait_time_multiplier", place.getWaitMultiplier());

        LatLng locationLatLng = place.getLocation();
        GeoPoint location = new GeoPoint(locationLatLng.latitude, locationLatLng.longitude);
        placeMap.put("location", location);


        return db.collection("place")
                .add(placeMap);
    }

    public static void uploadImage(Bitmap bitmap, Function<Uri, Void> callback) {
        String userId = auth.getCurrentUser().getUid();

        StorageReference imagesRef = storage.getReference().child("images/" + userId);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata("user", userId)
                .build();
        UploadTask uploadTask = imagesRef.putBytes(data, metadata);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.e(TAG, "Image upload failed");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                imagesRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri photoUri = task.getResult();
                            System.out.println("PHOTO URI: " + photoUri);
                            callback.apply(photoUri);
                        }
                    }
                });
            }
        });
    }
}
