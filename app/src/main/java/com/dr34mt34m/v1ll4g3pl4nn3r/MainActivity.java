package com.dr34mt34m.v1ll4g3pl4nn3r;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;

import com.dr34mt34m.v1ll4g3pl4nn3r.components.Place;
import com.dr34mt34m.v1ll4g3pl4nn3r.components.Reminder;
import com.dr34mt34m.v1ll4g3pl4nn3r.dataFetchers.FirebaseHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {
    private BottomNavigationView bottomNavigationView;

    private final Fragment mapsFragment = new MapsFragment();
    private final Fragment remindersFragment = new ViewRemindersFragment();
    private final Fragment profileFragment = new ProfileFragment();

    Thread thread=null;
    Handler handler = new Handler();

    int delay = 1000*10;//every 10 seconds
    private Runnable periodicUpdate = new Runnable() {
        @Override
        public void run() {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                System.out.println("Checking reminders!");
                FirebaseHelper.getReminders(this::checkReminderTime);
                handler.postDelayed(periodicUpdate, delay);
            }
        }

        private Void checkReminderTime(List<Reminder> reminders) {
            for(Reminder r:reminders){
                //note: time is in milliseconds
                long reminderTime = r.getTimestamp().toDate().getTime();

                int hour = r.getTimestamp().toDate().getHours();
                int minute = r.getTimestamp().toDate().getMinutes();
                String ampm="am";
                int displayHour=hour;
                if(displayHour==0){
                    displayHour=12;
                }
                else if(displayHour>12){
                    displayHour-=12;
                    ampm="pm";
                }

                HashMap<String, Place> places = (HashMap)FirebaseHelper.getPlaces();
                Place p = places.get(r.getPlace());
                if(p==null){
                    return null;
                }
                double waitTimeMinutes = p.getWaitTime();
                long waitTimeTime = (long)(waitTimeMinutes*60*1000);

                long currentTime = Calendar.getInstance().getTime().getTime();
                long five_minutes = 5*60*1000;

                //reminderTime-currentTime shows how much time until reminderTime
                //However, we should factor in the waitTime and travel time.
                //We are giving buffer/leeway of 5 minutes for travel time.
                if(reminderTime-currentTime-waitTimeTime<five_minutes){
                    //send popup
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Time to leave for "+r.getPlace()+"!")
                            .setMessage(r.getMessage()+
                                    "\nReminder Time: "+displayHour+":"+minute+ampm+
                                    "\nWait Time: ~"+(int)waitTimeMinutes+" min")
                            .setPositiveButton("OK",null)
                            .show();

                    FirebaseHelper.deleteReminder(r.getId());
                    //maybe redraw?
                    FirebaseHelper.getReminders(((ViewRemindersFragment)remindersFragment)::createReminderList);
                }
            }

            return null;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.main_bottom_navigation_view);
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.tab_map);

        thread = new Thread(periodicUpdate);
        thread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(thread!=null){
            thread.interrupt();
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.tab_map) {
            switchTab(mapsFragment);
            return true;
        } else if (id == R.id.tab_reminders) {
            switchTab(remindersFragment);
            return true;
        } else if (id == R.id.tab_profile) {
            switchTab(profileFragment);
            return true;
        }

        return false;
    }

    private void switchTab(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).commit();
    }
}