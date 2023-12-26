package com.dr34mt34m.v1ll4g3pl4nn3r.components.RecycleView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.dr34mt34m.v1ll4g3pl4nn3r.R;
import com.dr34mt34m.v1ll4g3pl4nn3r.ViewRemindersFragment;
import com.dr34mt34m.v1ll4g3pl4nn3r.components.Reminder;
import com.dr34mt34m.v1ll4g3pl4nn3r.dataFetchers.FirebaseHelper;
import com.google.firebase.Timestamp;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Function;

public class ReminderRecycleViewAdapter extends RecyclerView.Adapter<ReminderViewHolder>{
    List<Reminder> list = Collections.emptyList();
    Context context;
    Function<View,Void> f;
    //ClickListener listener;

    public ReminderRecycleViewAdapter(List<Reminder> list, Context context, Function<View,Void> f)
    {
        this.list = list;
        this.context = context;
        //this.listener = listener;
        this.f = f;
    }

    @Override
    public ReminderViewHolder
    onCreateViewHolder(ViewGroup parent, int viewType) //what is viewtype
    {

        Context context
                = parent.getContext();
        LayoutInflater inflater
                = LayoutInflater.from(context);

        // Inflate the layout

        View remView
                = inflater
                .inflate(R.layout.reminder_layout,
                        parent, false);

        ReminderViewHolder viewHolder
                = new ReminderViewHolder(remView);
        return viewHolder;
    }

    @Override
    public void
    onBindViewHolder(final ReminderViewHolder viewHolder,
                     final int position)
    {
        //final index = viewHolder.getAdapterPosition();

        Reminder r = list.get(position);
        viewHolder.StoreView
                .setText(r.getPlace());
        viewHolder.MessageView
                .setText(r.getMessage());
        viewHolder.DeleteButton
                .setTag(r.getId());
        viewHolder.DeleteButton
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        f.apply(view);
                    }
                });

        Timestamp timestamp = r.getTimestamp();
        Date date = timestamp.toDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(cal.YEAR);
        int month = cal.get(cal.MONTH);
        //displayed month is month+1
        int day = cal.get(cal.DATE);
        int hour = cal.get(cal.HOUR_OF_DAY);
        int minute = cal.get(cal.MINUTE);

        String ampm="am";
        int displayHour=hour;
        if(displayHour==0){
            displayHour=12;
        }
        else if(displayHour>12){
            displayHour-=12;
            ampm="pm";
        }
        String timeString = String.format("%d/%d/%d\n%02d:%02d %s",month+1,day,year,
            displayHour,minute,ampm);
        viewHolder.TimeView
                .setText(timeString);

//        viewHolder.view.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view)
////            {
////                listiner.click(index);
////            }
//        });


    }

//    public interface ClickListener{
//        // here index is index of item clicked
//        void click(int index);
//    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(
            RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
    }

}
