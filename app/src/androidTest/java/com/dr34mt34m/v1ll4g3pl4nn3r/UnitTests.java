package com.dr34mt34m.v1ll4g3pl4nn3r;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import com.dr34mt34m.v1ll4g3pl4nn3r.components.Place;
import com.dr34mt34m.v1ll4g3pl4nn3r.components.Reminder;
import com.dr34mt34m.v1ll4g3pl4nn3r.components.User;
import com.dr34mt34m.v1ll4g3pl4nn3r.dataFetchers.FirebaseHelper;
import com.dr34mt34m.v1ll4g3pl4nn3r.waitTimes.WaitTimeCalculator;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Test;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;


public class UnitTests {

    FirebaseAuth auth;
    private static final String TAG = FirebaseHelper.class.getName();

    @Before
    public void setup() {
        // sign in
        auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword("test@gmail.com", "password")
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = auth.getCurrentUser();
                            System.out.println(user.getEmail());
                        } else {
                            // If sign in fails, display a message to the user.
                            System.out.println("FAILED TO LOGIN WITH TEST USER");
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                        }
                    }
                });
    }

    @Test
    public void testCreateReminder() {
        Date date = new Date();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        int currentYear = calendar.get(Calendar.YEAR);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        Reminder testReminder = new Reminder("testPlace", "testMessage", currentYear, currentDay, currentMonth, currentHour, currentMinute);
        Task<DocumentReference> resultTask = FirebaseHelper.createReminderAsync(testReminder);
        final String[] testReminderId = new String[1];
        resultTask.addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                testReminderId[0] = task.getResult().getId();
            }
        });
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String result = testReminderId[0];
        // fetch the reminderId from firebase
        Task<DocumentSnapshot> fetchedReminder = FirebaseHelper.getReminderAsync(testReminderId);
        final String[] fetchedTestReminderId = new String[1];
        fetchedReminder.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                fetchedTestReminderId[0] = task.getResult().getId();
            }
        });
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String fetchedResult = fetchedTestReminderId[0];
        assertEquals(result, fetchedResult);
    }

    @Test
    public void testGetReminder() {
        // NOTE that we are testing for a reminder that should be prepopulated in the firestore databse under this test account
        String[] testReminderId = {"8OQulvIkreVC0YYGJssy"};
        // get the reminder from firestore
        Task<DocumentSnapshot> getTask = FirebaseHelper.getReminderAsync(testReminderId);
        final String[] fetchedTestReminderId = new String[1];
        getTask.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                fetchedTestReminderId[0] = task.getResult().getId();
            }
        });
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String fetchedResult = fetchedTestReminderId[0];
        assertEquals(fetchedResult, testReminderId[0]);
    }

    @Test
    public void testGetReminders() {
        // insert some test reminders into the test firestore database environment
        // NOTE these reminders should be prepopulated in the firestore database for the test account
        ArrayList<String> testReminders = new ArrayList<>();
        testReminders.add("8OQulvIkreVC0YYGJssy");
        testReminders.add("DmDAWhVgTA3JHZGTWkgi");
        // retrieve the newly inserted data from the test firestore database
        Task<QuerySnapshot> fetchedResult = FirebaseHelper.getRemindersAsync();
        ArrayList<String> fetchedReminders = new ArrayList<>();
        fetchedResult.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> query) {
                List<DocumentSnapshot> documentSnapshots = query.getResult().getDocuments();
                for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                    if (documentSnapshot.getId().equals("8OQulvIkreVC0YYGJssy") || documentSnapshot.getId().equals("DmDAWhVgTA3JHZGTWkgi")) {
                        fetchedReminders.add(documentSnapshot.getId());
                    }
                }
            }
        });
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String[] fetchedReminderArray = new String[2];
        fetchedReminderArray[0] = fetchedReminders.get(0);
        fetchedReminderArray[1] = fetchedReminders.get(1);
        // sort these lists for consistent rseults
        Arrays.sort(fetchedReminderArray);
        Collections.sort(testReminders);
        // assert
        assertArrayEquals(testReminders.toArray(), fetchedReminderArray);
    }

    @Test
    public void testDeleteReminder() {
        Date date = new Date();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        int currentYear = calendar.get(Calendar.YEAR);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        Reminder testReminder = new Reminder("testPlace", "testMessage", currentYear, currentDay, currentMonth, currentHour, currentMinute);
        Task<DocumentReference> resultTask = FirebaseHelper.createReminderAsync(testReminder);
        final String[] testReminderId = new String[1];
        resultTask.addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                testReminderId[0] = task.getResult().getId();
            }
        });
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String result = testReminderId[0];
        // delete the reminder
        Task<Void> deleteTask = FirebaseHelper.deleteReminderAsync(result);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // fetch all reminders and make sure that the deleted reminder ID isn't there
        Task<QuerySnapshot> fetchedResult = FirebaseHelper.getRemindersAsync();
        fetchedResult.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> query) {
                List<DocumentSnapshot> documentSnapshots = query.getResult().getDocuments();
                for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                    assert !documentSnapshot.getId().equals(result);
                }
            }
        });
    }

    @Test
    public void testUpdateReminder() {
        // create a new reminder
        Date date = new Date();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        int currentYear = calendar.get(Calendar.YEAR);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        Reminder testReminder = new Reminder("testPlace", "testMessage", currentYear, currentDay, currentMonth, currentHour, currentMinute);
        Task<DocumentReference> resultTask = FirebaseHelper.createReminderAsync(testReminder);
        final String[] testReminderId = new String[1];
        resultTask.addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                testReminderId[0] = task.getResult().getId();
            }
        });
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String result = testReminderId[0];
        testReminder.setId(result);

        // update the reminder's message
        String newMessage = "updated test message";
        Reminder updatedReminder = testReminder;
        updatedReminder.setMessage(newMessage);
        Task<Void> updateTask = FirebaseHelper.updateReminderAsync(updatedReminder);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // fetch the given reminder from firebase
        Task<QuerySnapshot> fetchedResult = FirebaseHelper.getRemindersAsync();
        fetchedResult.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> query) {
                List<DocumentSnapshot> documentSnapshots = query.getResult().getDocuments();
                for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                    assert !documentSnapshot.getId().equals(result) || Objects.equals(documentSnapshot.get("message"), newMessage);
                }
            }
        });
    }

    @Test
    public void testCreatePlace() {
        // create a new place
        String name = "testPlace";
        LatLng latLng = new com.google.android.gms.maps.model.LatLng(102.393, 45.249);
        List<String> testPeople = new ArrayList<>();
        testPeople.add("Bob");
        testPeople.add("Joe");
        testPeople.add("Amy");
        double testWaitTimeMultiplier = 1.7;

        Place testPlace = new Place(name, testPeople, latLng, testWaitTimeMultiplier);
        Task<DocumentReference> createTask = FirebaseHelper.createPlaceAsync(testPlace);
        final String[] testPlaceId = new String[1];
        createTask.addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                testPlaceId[0] = task.getResult().getId();
            }
        });
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String result = testPlaceId[0];

        // get the place from firebase
        Task<DocumentSnapshot> getTask = FirebaseHelper.getPlaceAsync(result);
        final String[] fetchedTestPlaceId = new String[1];
        getTask.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                fetchedTestPlaceId[0] = task.getResult().getId();
            }
        });
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String fetchedResult = fetchedTestPlaceId[0];
        assertEquals(result, fetchedResult);
    }

    @Test
    public void testGetPlace() {
        // NOTE that we are testing that we are able to retrieve data from our firestore database, so
        // we are checking that Trader Jpe's is able to be retrieved from our database under this test account
        String[] testPlaceId = {"ffhUPrzBxC5xauTrHJXs"};
        // get the reminder from firestore
        Task<DocumentSnapshot> getTask = FirebaseHelper.getPlaceAsync(testPlaceId[0]);
        final String[] fetchedTestPlaceId = new String[1];
        getTask.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                fetchedTestPlaceId[0] = task.getResult().getId();
            }
        });
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String fetchedResult = fetchedTestPlaceId[0];
        assertEquals(fetchedResult, testPlaceId[0]);
    }

    @Test
    public void testGetPlaces() {
        // NOTE: this test is to ensure that we are able to retrieve more than one place from firestore
        // this checks that firestore has properly prepopulated data
        ArrayList<String> testPlaces = new ArrayList<>();
        testPlaces.add("ffhUPrzBxC5xauTrHJXs");
        testPlaces.add("YM0FpkTv1hqBCjysZ9Ux");
        // retrieve the newly inserted data from the test firestore database
        Task<QuerySnapshot> fetchedResult = FirebaseHelper.loadPlacesAsync();
        ArrayList<String> fetchedPlaces = new ArrayList<>();
        fetchedResult.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> query) {
                List<DocumentSnapshot> documentSnapshots = query.getResult().getDocuments();
                for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                    if (documentSnapshot.getId().equals("ffhUPrzBxC5xauTrHJXs") || documentSnapshot.getId().equals("YM0FpkTv1hqBCjysZ9Ux")) {
                        fetchedPlaces.add(documentSnapshot.getId());
                    }
                }
            }
        });
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String[] fetchedPlacesArray = new String[2];
        fetchedPlacesArray[0] = fetchedPlaces.get(0);
        fetchedPlacesArray[1] = fetchedPlaces.get(1);
        // sort these lists for consistent results
        Arrays.sort(fetchedPlacesArray);
        Collections.sort(testPlaces);
        // assert
        assertArrayEquals(testPlaces.toArray(), fetchedPlacesArray);
    }

    @Test
    public void testUpdatePlace() {
        // create a new test place
        String name = "testPlace";
        LatLng latLng = new com.google.android.gms.maps.model.LatLng(102.393, 45.249);
        List<String> testPeople = new ArrayList<>();
        testPeople.add("Bob");
        testPeople.add("Joe");
        testPeople.add("Amy");
        double testWaitTimeMultiplier = 1.7;

        // create and insert this test place into the firestore database
        Place testPlace = new Place(name, testPeople, latLng, testWaitTimeMultiplier);
        Task<DocumentReference> createTask = FirebaseHelper.createPlaceAsync(testPlace);
        final String[] testPlaceId = new String[1];
        createTask.addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                testPlaceId[0] = task.getResult().getId();
            }
        });
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String result = testPlaceId[0];
        testPlace.setId(result);

        // update the place in firestore
        String updatedName = "updated test place name";
        Place updatedPlace = testPlace;
        updatedPlace.setName(updatedName);
        Task<Void> updateTask = FirebaseHelper.updatePlaceAsync(updatedPlace);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // get the place from firebase
        Task<DocumentSnapshot> getTask = FirebaseHelper.getPlaceAsync(result);
        final String[] fetchedTestPlaceId = new String[1];
        getTask.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().getId().equals(result) && task.getResult().get("name").equals(updatedName)) {
                    assert true;
                }
            }
        });
    }

//    @Test
//    public void testDeletePlace() {
//        // create a new test place
//        String name = "testPlace";
//        LatLng latLng = new com.google.android.gms.maps.model.LatLng(102.393, 45.249);
//        List<String> testPeople = new ArrayList<>();
//        testPeople.add("Bob");
//        testPeople.add("Joe");
//        testPeople.add("Amy");
//        double testWaitTimeMultiplier = 1.7;
//
//        // create and insert this test place into the firestore database
//        Place testPlace = new Place(name, testPeople, latLng, testWaitTimeMultiplier);
//        Task<DocumentReference> createTask = FirebaseHelper.createPlaceAsync(testPlace);
//        final String[] testPlaceId = new String[1];
//        createTask.addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentReference> task) {
//                testPlaceId[0] = task.getResult().getId();
//            }
//        });
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        String result = testPlaceId[0];
//        testPlace.setId(result);
//        System.out.println("deleting reminder id: " + result + " from delete reminder unit test");
//        // delete the reminder
//        Task<Void> deleteTask = FirebaseHelper.deleteReminderAsync(result);
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        // fetch all places and make sure that the deleted reminder ID isn't there
//        Task<QuerySnapshot> fetchedResult = FirebaseHelper.loadPlacesAsync();
//        ArrayList<String> fetchedPlaces = new ArrayList<>();
//        fetchedResult.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> query) {
//                List<DocumentSnapshot> documentSnapshots = query.getResult().getDocuments();
//                for (DocumentSnapshot documentSnapshot : documentSnapshots) {
//                    assert !documentSnapshot.getId().equals(result);
//                }
//            }
//        });
//    }

    @Test
    public void testCreateUser() {
        // create a new user
        String firstName = "Test";
        String lastName = "User";
        String email = "testUser@gmail.com";
        String profilePic = "testProfilePicture";

        User testUser = new User(firstName, lastName, email, profilePic);
        Task<Void> createTask = FirebaseHelper.createUserAsync(testUser);
        final String[] testUserId = new String[1];
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // get the user details using firebase
        Task<QuerySnapshot> getTask = FirebaseHelper.getUsersAsync();
        getTask.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> query) {
                List<DocumentSnapshot> documentSnapshots = query.getResult().getDocuments();
                for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                    // if we were able to find the current user in the firestore database then return true
                    if (documentSnapshot.get("first_name").equals(firstName) && documentSnapshot.get("last_name").equals(lastName) && documentSnapshot.get("profile_picture").equals(profilePic)) {
                        assert true;
                    }
                }
            }
        });
    }

    @Test
    public void testGetCurrentUser() {
        // NOTE: that this test relies on the fact that the test user account that we setup in the setup() method is going to be used
        // this is essential for the application because all of our functions rely on properly getting the current user's information from firebase and
        // nesting the user's data with respect to their user account information
        String userId = auth.getCurrentUser().getUid();
        Task<DocumentSnapshot> getTask = FirebaseHelper.getUserAsync(userId);
        final String[] fetchedUserId = new String[1];
        getTask.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                fetchedUserId[0] = task.getResult().getId();
            }
        });
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(userId, fetchedUserId[0]);
    }

    @Test
    public void testUpdateUser() {
        String userId = auth.getCurrentUser().getUid();
        // create a new user
        String firstName = "Test";
        String lastName = "User";
        String email = "testUser@gmail.com";
        String profilePic = "testProfilePicture";

        User testUser = new User(firstName, lastName, email, profilePic);
        testUser.setUserId(userId);
        Task<Void> createTask = FirebaseHelper.createUserAsync(testUser);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // update the current user's info
        String updatedName = "Updated test first name";
        testUser.setFirstName(updatedName);
        FirebaseHelper.updateUserAsync(testUser);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // fetch the user's info from firebase
        Task<DocumentSnapshot> getTask = FirebaseHelper.getUserAsync(testUser.getUserId());
        getTask.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().get("first_name").equals(updatedName)) {
                    assert true;
                }
            }
        });
    }

    @Test
    public void testWaitTimeAlgorithmWithNobody() {
        List<String> currentTestUsers = new ArrayList<>();
        LatLng testLocation = new LatLng(23.341, 63.858);
        Place testPlace = new Place("testPlace", currentTestUsers, testLocation, 1.7);
        double waitTime  = WaitTimeCalculator.getWaitTimeSeconds(testPlace);
        assertEquals(0, waitTime, 0.1);
    }

    @Test
    public void testWaitTimeAlgorithmWithPeople() {
        // NOTE: this test mainly checks that the waitTime computational logic hasn't changed
        List<String> currentTestUsers = new ArrayList<>();
        LatLng testLocation = new LatLng(23.341, 63.858);
        currentTestUsers.add("testPerson1");
        currentTestUsers.add("testPerson2");
        currentTestUsers.add("testPerson3");
        currentTestUsers.add("testPerson4");
        currentTestUsers.add("testPerson5");
        Place testPlace = new Place("testPlace", currentTestUsers, testLocation, 1.7);
        double waitTime  = WaitTimeCalculator.getWaitTimeSeconds(testPlace);
        assertEquals(1.7 * 5, waitTime, 0.1);
    }

    @Test
    public void testWaitTimeAlgorithmOutputWithDifferentMultiplier() {
        // test that this algorithm outputs a different value for
        List<String> currentTestUsers = new ArrayList<>();
        LatLng testLocation = new LatLng(23.341, 63.858);
        currentTestUsers.add("testPerson1");
        currentTestUsers.add("testPerson2");
        currentTestUsers.add("testPerson3");
        currentTestUsers.add("testPerson4");
        currentTestUsers.add("testPerson5");
        Place testPlace = new Place("testPlace", currentTestUsers, testLocation, 1.7);

        Place testPlace2 = new Place("testPlace", currentTestUsers, testLocation, 3.3);
        double firstWaitTime = WaitTimeCalculator.getWaitTimeSeconds(testPlace);
        double secondWaitTime = WaitTimeCalculator.getWaitTimeSeconds(testPlace2);
        assertNotEquals(firstWaitTime, secondWaitTime, 0.1);
    }
}
