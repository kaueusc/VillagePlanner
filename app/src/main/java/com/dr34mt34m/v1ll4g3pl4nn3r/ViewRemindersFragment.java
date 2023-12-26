package com.dr34mt34m.v1ll4g3pl4nn3r;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dr34mt34m.v1ll4g3pl4nn3r.components.RecycleView.ReminderRecycleViewAdapter;
import com.dr34mt34m.v1ll4g3pl4nn3r.components.Reminder;
import com.dr34mt34m.v1ll4g3pl4nn3r.dataFetchers.FirebaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;


public class ViewRemindersFragment extends Fragment {
    private static final String TAG = ViewRemindersFragment.class.getName();

    //Code for Scrollable RecycleViews of Reminders
    ReminderRecycleViewAdapter adapter;
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_reminders, container, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode==0){
            FirebaseHelper.getReminders(this::createReminderList);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseHelper.getReminders(this::createReminderList);

        // Add button listeners
        FloatingActionButton createReminderButton = getView().findViewById(R.id.CreateReminderButton);
        createReminderButton.setOnClickListener(this::onAddClick);
    }

    public Void createReminderList(List<Reminder> list){
        if (getView() == null) {
            return null;
        }
        recyclerView = getView().findViewById(R.id.ReminderList);
//        listiner = new ClickListiner() {
//            @Override
//            public void click(int index){
//                Toast.makeTexT(this,"clicked item index is "+index,Toast.LENGTH_LONG).show();
//            }
//        };
        adapter = new ReminderRecycleViewAdapter(list, getActivity(), this::onDeleteClick);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return null;
    }

    public void onAddClick(View v) {
        Intent intent = new Intent(getActivity(), CreateReminderActivity.class);
        startActivityForResult(intent,0);
    }

    public Void onDeleteClick(View v){
        String id = (String)v.getTag();
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setMessage(getResources().getString(R.string.are_you_sure_that_you_want_to_delete_this_reminder));
        builder.setTitle(getResources().getString(R.string.deletion_confirmation));
        builder.setCancelable(false);
        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Go to firebase and delete reminder
                FirebaseHelper.deleteReminder(id);
                // redraw the reminders fragment
                FirebaseHelper.getReminders(ViewRemindersFragment.this::createReminderList);
                Toast.makeText(getContext(), R.string.deletion_success,
                        Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();


        return null;
    }
}